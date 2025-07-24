// FitFusion Pose Detection JavaScript - pushup
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
        canvasCtx.restore();
    } catch (e) {
        console.error(e);
    }

    if (webcamRunning === true) {
        webcamRunning = false;
        if (localStream) {
            localStream.getTracks().forEach(track => track.stop());
            video.srcObject = null;
            localStream = null;
        }
        enableWebcamButton.innerText = "웹캠 시작하기";
    } else {
        webcamRunning = true;
        enableWebcamButton.innerText = "웹캠 종료하기";
    }
}

let repState = "ready";
let repCount = 0;
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

            // 발목-어깨선과 지면의 각도 구하기
            function calculateAngleToGround(ankle, shoulder, wrist) {
                const v1x = shoulder.x - ankle.x;
                const v1y = shoulder.y - ankle.y;
                const v2x = wrist.x - ankle.x;
                const v2y = wrist.y - ankle.y;
                const dot = v1x * v2x + v1y * v2y;
                const magV1 = Math.hypot(v1x, v1y);
                const magV2 = Math.hypot(v2x, v2y);
                const cosTheta = dot / (magV1 * magV2);
                const clampedCos = Math.max(-1, Math.min(1, cosTheta)); // avoid floating point issues
                const angleRad = Math.acos(clampedCos);
                return angleRad * (180 / Math.PI);
            }

            // 세 점 각도 계싼
            function calculateBendAngle(a, b, c) {
                const v1x = a.x - b.x;
                const v1y = a.y - b.y;
                const v2x = c.x - b.x;
                const v2y = c.y - b.y;
                const dot = v1x * v2x + v1y * v2y;
                const magV1 = Math.hypot(v1x, v1y);
                const magV2 = Math.hypot(v2x, v2y);
                const cosTheta = dot / (magV1 * magV2);
                const clampedCos = Math.max(-1, Math.min(1, cosTheta));
                const angleRad = Math.acos(clampedCos);
                return angleRad * (180 / Math.PI);
            }

            // 허리 높이 계산
            function calculateHipPosition(shoulder, hip, ankle) {
                // Interpolate the expected y-position of the hip on a straight line between shoulder and ankle
                const t = ((hip.x - shoulder.x) / (ankle.x - shoulder.x));
                const expectedHipY = shoulder.y + t * (ankle.y - shoulder.y);

                const hipOffset = hip.y - expectedHipY; // Positive if hip is below the line
                console.log("hipoffset:", hipOffset);

                if (hipOffset > 0.03) {
                    if (pushupAngle < 20) {
                        if (hipOffset > 0.05) {
                            return "sagging";
                        } else {
                            return "straight";
                        }
                    }
                    return "sagging";
                } else if (hipOffset < -0.05) {
                    return "piking";
                } else {
                    return "straight";
                }
            }


            // 팔꿈치 위치 계산
            // 밖으로 굽혔을 때(잘못된 자세일 때) true 반환
            function checkElbowFlare(shoulder, elbow, threshold = 0.1) {
                const xDiff = Math.abs(elbow.x - shoulder.x);
                return xDiff < threshold;
            }

            const leftShoulder = landmark[11];
            const leftElbow = landmark[13];
            const leftWrist = landmark[15];
            const leftHip = landmark[23];
            const leftAnkle = landmark[27];
            const rightShoulder = landmark[12];
            const rightElbow = landmark[14];
            const rightWrist = landmark[16];
            const rightHip = landmark[24];
            const rightAnkle = landmark[28];

            const checkPoints = [leftShoulder, leftElbow, leftWrist, leftHip, leftAnkle,
                rightShoulder, rightElbow, rightWrist, rightHip, rightAnkle];

            let userDirection;
            let pushupAngle;
            let backStraightness;
            let hipState;
            let armBendAngle;
            let elbowFlare;
            if (leftShoulder.visibility > rightShoulder.visibility
                && leftAnkle.visibility > rightAnkle.visibility) {
                userDirection = "left";
            } else {
                userDirection = "right";
            }

            if (userDirection === "left") {
                pushupAngle = calculateAngleToGround(leftAnkle, leftShoulder, leftWrist);
                backStraightness = calculateBendAngle(leftShoulder, leftHip, leftAnkle);
                hipState = calculateHipPosition(leftShoulder, leftHip, leftAnkle);
                armBendAngle = calculateBendAngle(leftShoulder, leftElbow, leftWrist);
                elbowFlare = checkElbowFlare(leftShoulder, leftElbow);
            } else {
                pushupAngle = calculateAngleToGround(rightAnkle, rightShoulder, rightWrist);
                backStraightness = calculateBendAngle(rightShoulder, rightHip, rightAnkle);
                hipState = calculateHipPosition(rightHip, rightHip, rightAnkle);
                armBendAngle = calculateBendAngle(rightShoulder, rightElbow, rightWrist);
                elbowFlare = checkElbowFlare(rightShoulder, rightElbow);
            }

            // --- Rep counting ---
            let visiblePoints = [];
            for (let point of checkPoints) {
                visiblePoints.push(checkVisibility(point));
            }
            console.log("pushupAngle:", pushupAngle, "armBendAngle:", armBendAngle, "backStraightness:", backStraightness);
            if (visiblePoints.filter(Boolean).length > 4) {
                if (repState === "ready" && hipState === "straight") {
                    repState = "down";
                }
                if (hipState === "straight") {
                    if (repState === "up" && pushupAngle > 30 && armBendAngle > 155) {
                        repCount ++;
                        repState = "down";
                    } else if (repState === "down" && pushupAngle < 17 && armBendAngle < 110) {
                        repState = "up";
                    }
                } else {
                    if (hipState === "sagging") {
                        feedbackMessages.push("허리를 올리세요!")
                    }
                    if (hipState === "piking") {
                        feedbackMessages.push("허리를 내리세요!")
                    }
                }
                // if (elbowFlare && pushupAngle < 10) {
                //     feedbackMessages.push("팔꿈치를 몸 가까이 붙이세요!")
                // }
            }

            repCountDiv.innerText = `상태: ${repState}`
            repCountDiv.innerText += `\n반복 횟수: ${repCount}`;

            if (feedbackMessages.length > 0) {
                feedbackDiv.innerText = feedbackMessages.join("\n");
            } else {
                feedbackDiv.innerText = "";
            }
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
