// FitFusion Pose Detection JavaScript
// MediaPipe PoseLandmarker implementation

import {
    FilesetResolver,
    PoseLandmarker,
    DrawingUtils
} from "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@latest";

let poseLandmarker;
let runningMode = "IMAGE";
let webcamRunning = false;

// Initialize the pose landmarker
async function createPoseLandmarker() {
    const vision = await FilesetResolver.forVisionTasks(
        "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@latest/wasm"
    );

    poseLandmarker = await PoseLandmarker.createFromOptions(
        vision,
        {
            baseOptions: {
                modelAssetPath: "/models/pose_landmarker_lite.task",
                delegate: "GPU"
            },
            runningMode: runningMode
        });
}
createPoseLandmarker().catch(console.error);

// 이미지 클릭 이벤트핸들러 등록
document.addEventListener('DOMContentLoaded', function () {
    const images = document.querySelectorAll('.detectOnClick img');
    images.forEach(img => {
        img.addEventListener('click', (e) => {
            detectPoseInImage(e);
        });
    });
});

// 이미지 포즈 감지
async function detectPoseInImage(event) {
    if (!poseLandmarker) {
        console.log("poselandmarker not loaded yet.")
        return;
    }

    if (runningMode === "VIDEO") {
        runningMode = "IMAGE";
        await poseLandmarker.setOptions({runningMode: "IMAGE"});
    }

    // 이전에 그린 랜드마크 제거
    const allCanvas = event.target.parentNode.querySelectorAll('canvas');
    allCanvas.forEach(c => {
        c.remove();
    });

    // 감지
    poseLandmarker.detect(event.target, (result) => {
       // 결과를 그릴 캔버스 생성
        const canvas = document.createElement("canvas");
        canvas.setAttribute("class", "canvas");
        const rect = event.target.getBoundingClientRect();
        canvas.width = rect.width;
        canvas.height = rect.height;
        canvas.style.left = "0px";
        canvas.style.top = "0px";
        canvas.style.width = rect.width + "px";
        canvas.style.height = rect.height + "px";

        event.target.parentNode.appendChild(canvas);
        // canvas 컨텍스트 생성
        const canvasCtx = canvas.getContext("2d");
        const drawingUtils = new DrawingUtils(canvasCtx);
        for (const landmark of result.landmarks) {
            drawingUtils.drawLandmarks(landmark, {
                radius: (data) => DrawingUtils.lerp(data.from.z, -0.15, 0.1, 5, 1)
        });
            drawingUtils.drawConnectors(landmark, PoseLandmarker.POSE_CONNECTIONS);
        }
    });
}

// 비디오 감지
const enableWebcamButton = document.getElementById("webcamButton");
const video = document.getElementById("webcam");
const canvasElement = document.getElementById("output_canvas");
const videoHeight = document.querySelector('.video-container').height;
const videoWidth = document.querySelector('.video-container').width;

const canvasCtx = canvasElement.getContext("2d");
const drawingUtils = new DrawingUtils(canvasCtx);

// Check if webcam access is supported.
const hasGetUserMedia = () => !!navigator.mediaDevices?.getUserMedia;

// If webcam supported, add event listener to button for when user
// wants to activate it.
if (hasGetUserMedia()) {
    enableWebcamButton.addEventListener("click", enableCam);
} else {
    console.warn("getUserMedia() is not supported by your browser");
}

// Enable the live webcam view and start detection.
function enableCam(event) {
    if (!poseLandmarker) {
        console.log("Wait! poseLandmaker not loaded yet.");
        return;
    }

    if (webcamRunning === true) {
        webcamRunning = false;
        enableWebcamButton.innerText = "웹캠 시작하기";
    } else {
        webcamRunning = true;
        enableWebcamButton.innerText = "웹캠 종료하기";
    }

    // getUsermedia parameters.
    const constraints = {
        video: true
    };

    // Activate the webcam stream.
    navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
        video.srcObject = stream;
        video.addEventListener("loadeddata", predictWebcam);
    });
}

let lastVideoTime = -1;
async function predictWebcam() {
    canvasElement.style.height = videoHeight;
    video.style.height = videoHeight;
    canvasElement.style.width = videoWidth;
    video.style.width = videoWidth;
    // Now let's start detecting the stream.
    if (runningMode === "IMAGE") {
        runningMode = "VIDEO";
        await poseLandmarker.setOptions({ runningMode: "VIDEO" });
    }
    let startTimeMs = performance.now();
    if (lastVideoTime !== video.currentTime) {
        lastVideoTime = video.currentTime;
        poseLandmarker.detectForVideo(video, startTimeMs, (result) => {
            canvasCtx.save();
            canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
            for (const landmark of result.landmarks) {
                drawingUtils.drawLandmarks(landmark, {
                    radius: (data) => DrawingUtils.lerp(data.from.z, -0.15, 0.1, 5, 1)
            });
                drawingUtils.drawConnectors(landmark, PoseLandmarker.POSE_CONNECTIONS);
            }
            canvasCtx.restore();
        });
    }

    // Call this function again to keep predicting when the browser is ready.
    if (webcamRunning === true) {
        window.requestAnimationFrame(predictWebcam);
    }
}

// 에러 메세지 표시
function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.innerHTML = `
        <div class="error-content">
            <i class="bi bi-exclamation-triangle"></i>
            <span>${message}</span>
        </div>
    `;

    document.body.appendChild(errorDiv);

    setTimeout(() => {
        if (errorDiv.parentNode) {
            document.body.removeChild(errorDiv);
        }
    }, 5000);
}

// Add CSS for result modal and error messages
const style = document.createElement('style');
style.textContent = `
    #resultCanvas {
        max-width: 100%;
        height: auto;
    }
    
    .error-message {
        position: fixed;
        top: 20px;
        right: 20px;
        background: #dc3545;
        color: #fff;
        padding: 15px 20px;
        border-radius: 8px;
        z-index: 1001;
        animation: slideIn 0.3s ease;
    }
    
    .error-content {
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
`;
document.head.appendChild(style); 