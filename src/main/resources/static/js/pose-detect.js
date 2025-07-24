// FitFusion Pose Detection JavaScript
// MediaPipe PoseLandmarker with LIVE_STREAM and selective landmark rendering

import {
    FilesetResolver,
    PoseLandmarker,
    DrawingUtils
} from "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@latest";

let poseLandmarker;
let runningMode = "VIDEO";
let webcamRunning = false;

const enableWebcamButton = document.getElementById("webcamButton");
const video = document.getElementById("webcam");
const canvasElement = document.getElementById("output_canvas");
const videoContainer = document.querySelector('.video-container');
const videoHeight = videoContainer ? videoContainer.height : 480;
const videoWidth = videoContainer ? videoContainer.width : 640;

const feedbackDiv = document.getElementById("feedback");
const repCountDiv = document.getElementById("rep-count");

const canvasCtx = canvasElement.getContext("2d");
let drawingUtils;

// Optional: define landmark indices to keep (omit face/eyes/ears etc.)
const LANDMARK_INDICES = [
    11, 12, // shoulders
    13, 14, // elbows
    15, 16, // wrists
    23, 24, // hips
    25, 26, // knees
    27, 28  // ankles
];

// Color-coding based on landmark type (example)
const landmarkColors = {
    shoulder: '#00b374',
    elbow: '#ff8c00',
    wrist: '#ff4d4d',
    hip: '#005577',
    knee: '#aa00ff',
    ankle: '#008cff'
};

function getColorForIndex(index) {
    if ([11, 12].includes(index)) return landmarkColors.shoulder;
    if ([13, 14].includes(index)) return landmarkColors.elbow;
    if ([15, 16].includes(index)) return landmarkColors.wrist;
    if ([23, 24].includes(index)) return landmarkColors.hip;
    if ([25, 26].includes(index)) return landmarkColors.knee;
    if ([27, 28].includes(index)) return landmarkColors.ankle;
    return '#cccccc'; // fallback color
}

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
            runningMode: runningMode,
            numPoses: 1
        });

    drawingUtils = new DrawingUtils(canvasCtx);

}

createPoseLandmarker().catch(console.error);

let localStream;
const hasGetUserMedia = () => !!navigator.mediaDevices?.getUserMedia;
if (hasGetUserMedia()) {
    enableWebcamButton.addEventListener("click", enableCam);
} else {
    console.warn("getUserMedia() is not supported by your browser");
}

async function enableCam(event) {
    if (!poseLandmarker) {
        console.log("Wait! poseLandmaker not loaded yet.");
        return;
    }

    const constraints = {
        video: true,
        audio: false
    };

    try {
        localStream = await navigator.mediaDevices.getUserMedia(constraints);
        video.srcObject = localStream;
        video.addEventListener("loadeddata", predictWebcam);
        canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
    } catch (e) {
        console.error(e);
    }

    if (webcamRunning === true) {
        webcamRunning = false;
        if (localStream) {
            localStream.getTracks().forEach(track => track.stop());
            video.srcObject = null;
            localStream = null;
            canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
        }
        enableWebcamButton.innerText = "웹캠 시작하기";
    } else {
        webcamRunning = true;
        enableWebcamButton.innerText = "웹캠 종료하기";
    }
}

let leftRepState = "ready";
let rightRepState = "ready";
let leftRepCount = 0;
let rightRepCount = 0;
function predictWebcam() {
    video.style.height = videoHeight + 'px';
    video.style.width = videoWidth + 'px';
    canvasElement.style.height = videoHeight + 'px';
    canvasElement.style.width = videoWidth + 'px';

    function frameLoop() {
        if (!webcamRunning) return;
        const now = performance.now();

        const result = poseLandmarker.detectForVideo(video, now);
        canvasCtx.save();
        canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);

        for (const landmark of result.landmarks) {
            LANDMARK_INDICES.forEach((i) => {
                const point = landmark[i];
                if (point.visibility < 0.5) return;
                const x = point.x * canvasElement.width;
                const y = point.y * canvasElement.height;
                const z = point.z;
                const color = getColorForIndex(i);
                canvasCtx.beginPath();
                canvasCtx.arc(x, y, 3, 0, 2 * Math.PI);
                canvasCtx.fillStyle = color;
                canvasCtx.fill();
            });

            // Manual fallback for pose connections (subset)
            const connections = [
                [11, 13], [13, 15], // left arm
                [12, 14], [14, 16], // right arm
                [11, 12], [11, 23], [12, 24], // shoulders to hips
                [23, 24], [23, 25], [25, 27], // left leg
                [24, 26], [26, 28]  // right leg
            ];
            connections.forEach(([startIdx, endIdx]) => {
                if (!LANDMARK_INDICES.includes(startIdx) || !LANDMARK_INDICES.includes(endIdx)) return;
                const start = landmark[startIdx];
                const end = landmark[endIdx];
                if (start.visibility < 0.5 || end.visibility < 0.5) return;
                canvasCtx.beginPath();
                canvasCtx.moveTo(start.x * canvasElement.width, start.y * canvasElement.height);
                canvasCtx.lineTo(end.x * canvasElement.width, end.y * canvasElement.height);
                canvasCtx.strokeStyle = '#888';
                canvasCtx.lineWidth = 2;
                canvasCtx.stroke();
            });
        }

        // --- BEGIN: Feedback logic ---
        if (result.landmarks.length > 0) {
            const landmark = result.landmarks[0];
            let feedbackMessages = [];

            function calculateAngle(a, b, c) {
                const ab = [a.x - b.x, a.y - b.y];
                const cb = [b.x - c.x, b.y - c.y];
                const dot = ab[0]*cb[0] + ab[1]*cb[1];
                const magAB = Math.hypot(ab[0], ab[1]);
                const magCB = Math.hypot(cb[0], cb[1]);
                const cosTheta = dot / (magAB * magCB);
                const clampedCos = Math.max(-1, Math.min(1, cosTheta));
                const angleRad = Math.acos(clampedCos);
                return angleRad * (180 / Math.PI);
            }

            const leftShoulder = landmark[11];
            const leftElbow = landmark[13];
            const leftWrist = landmark[15];
            const rightShoulder = landmark[12];
            const rightElbow = landmark[14];
            const rightWrist = landmark[16];

            const checkPoints = [leftShoulder, leftElbow, leftWrist, rightShoulder, rightElbow, rightWrist];

            const leftAngle = calculateAngle(leftShoulder, leftElbow, leftWrist);
            const rightAngle = calculateAngle(rightShoulder, rightElbow, rightWrist);

            if (165 > leftAngle && leftAngle > 100 && leftRepState === "up")
                feedbackMessages.push("왼팔을 더 들어 올리세요!");
            if (165 > rightAngle && rightAngle > 100 && rightRepState === "up")
                feedbackMessages.push("오른팔을 더 들어 올리세요!");

            // 팔꿈치 기준 위치가 어깨 - 골반 중간 지점
            const leftHip = landmark[23];
            const rightHip = landmark[24];

            const shoulderWidth = Math.abs(rightShoulder.x - leftShoulder.x);
            const leftRef = {
                x: leftShoulder.x + 0.3 * shoulderWidth,
                y: leftShoulder.y + 0.5 * (leftHip.y - leftShoulder.y)
            };
            const rightRef = {
                x: rightShoulder.x - 0.3 * shoulderWidth,
                y: rightShoulder.y + 0.5 * (rightHip.y - rightShoulder.y)
            };

            const deltaLeft = Math.hypot(leftElbow.x - leftRef.x, leftElbow.y - leftRef.y);
            const deltaRight = Math.hypot(rightElbow.x - rightRef.x, rightElbow.y - rightRef.y);

            if (deltaLeft > 0.04) feedbackMessages.push("왼쪽 팔꿈치를 고정하세요");
            if (deltaRight > 0.04) feedbackMessages.push("오른쪽 팔꿈치를 고정하세요");

            if (feedbackMessages.length > 0) {
                feedbackDiv.innerText = feedbackMessages.join("\n");
            } else {
                feedbackDiv.innerText = "";
            }

            // --- Draw reference points ---
            const refPoints = [leftRef, rightRef];
            refPoints.forEach((ref) => {
                const refX = ref.x * canvasElement.width;
                const refY = ref.y * canvasElement.height;
                canvasCtx.beginPath();
                canvasCtx.arc(refX, refY, 4, 0, 2 * Math.PI);
                canvasCtx.fillStyle = 'blue';
                canvasCtx.fill();
            });

            // --- Rep counting ---
            let visiblePoints = [];
            for (let point of checkPoints) {
                visiblePoints.push(checkVisibility(point));
            }
            const allVisible = visiblePoints.every(Boolean);
            if (allVisible) {
                if (leftRepState === "ready" && rightRepState === "ready") {
                    leftRepState = "up";
                    rightRepState = "up";
                }

                if (leftAngle < 10 && leftRepState === 'down') {
                    leftRepCount++;
                    leftRepState = 'up';
                } else if (leftAngle > 170 && leftRepState === 'up') {
                    leftRepState = 'down';
                }

                if (rightAngle < 10 && rightRepState === 'down') {
                    rightRepCount++;
                    rightRepState = 'up';
                } else if (rightAngle > 170 && rightRepState === 'up') {
                    rightRepState = 'down';
                }
            }

            repCountDiv.innerText = `상태: ${leftRepState}`
            repCountDiv.innerText += `\n왼팔 반복 횟수: ${leftRepCount}`;
            repCountDiv.innerText += `\n오른팔 반복 횟수: ${rightRepCount}`;
        }
        // --- END: Feedback logic ---

        canvasCtx.restore();

        requestAnimationFrame(frameLoop);
    }

    frameLoop();
}

function checkVisibility(landmark) {
    return landmark.visibility > 0.7;
}
