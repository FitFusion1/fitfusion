// Exercise Feedback JavaScript - Enhanced Feedback System
import {
    FilesetResolver,
    PoseLandmarker
} from "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@latest";

// Global variables
let poseLandmarker;
let runningMode = "VIDEO";
let webcamRunning = false;
let currentSet = 1;
let goodReps = 0;
let badReps = 0;
let currentRep = 0;
let currentGoodReps = 0;
let totalReps = 0;
let goodTime = 0;
let badTime = 0;
let currentExerciseTime = 0;
let globalExerciseTime = 0;
let isTimedExercise = false;
let exerciseConfig = null;
let exerciseState = 'ready';
let isInGoodPose = false;
let lastPoseTime = 0;
let badPoseStartTime = null;
const badPoseDurationThreshold = 2000; // 2 seconds in milliseconds
let feedbackEvents = [];
let workoutPaused = false;
let workoutDiscontinued = false;

// 덤벨 컬 변수
let elbowLockTime = null;
let initialLeftElbow = null;
let initialRightElbow = null;

// Rest timer variables
let restTime = 40; // Default rest time in seconds
let currentRestTime = 40;
let isRestActive = false;
let isRestPaused = false;
let totalRestTime = 0; // Total rest time used in workout
let restTimerInterval = null;

// DOM elements
const enableWebcamButton = document.getElementById("webcamButton");
const video = document.getElementById("webcam");
const canvasElement = document.getElementById("output_canvas");
const feedbackDiv = document.getElementById("feedback");
const currentSetSpan = document.getElementById("current-set");
const currentSetDisplay = document.getElementById("current-set-display");
const setDisplay = document.getElementById("set-display");
const currentRepDisplay = document.getElementById("current-rep-display");
const currentRepValue = document.getElementById("current-rep-value");
const goodRepsSpan = document.getElementById("good-reps");
const badRepsSpan = document.getElementById("bad-reps");
const totalRepsSpan = document.getElementById("total-reps");
const repsProgressSpan = document.getElementById("reps-progress");
const goodTimeSpan = document.getElementById("good-time");
const badTimeSpan = document.getElementById("bad-time");
const totalTimeSpan = document.getElementById("total-time");
const timeProgressSpan = document.getElementById("time-progress");
const accuracyPercentageSpan = document.getElementById("accuracy-percentage");
const repSection = document.getElementById("rep-section");
const timerSection = document.getElementById("timer-section");
const repsTarget = document.getElementById("reps-target");
const timerTarget = document.getElementById("timer-target");
const exerciseTipsSection = document.getElementById("exercise-tips");
const tipsContent = document.getElementById("tips-content");
const totalTimeElapsedSpan = document.getElementById("total-time-elapsed");

// Rest timer DOM elements
const restTimerSection = document.getElementById("rest-timer-section");
const restTimeDisplay = document.getElementById("rest-time-display");
const startRestButton = document.getElementById("start-rest");
const pauseRestButton = document.getElementById("pause-rest");
const skipRestButton = document.getElementById("skip-rest");
const editRestTimeButton = document.getElementById("edit-rest-time");
const restTimeEditModal = document.getElementById("rest-time-edit-modal");
const restTimeInput = document.getElementById("rest-time-input");
const saveRestTimeButton = document.getElementById("save-rest-time");
const cancelRestEditButton = document.getElementById("cancel-rest-edit");
const restTimerRingProgress = document.querySelector(".rest-timer-ring-progress");
const stopExerciseButton = document.getElementById("stop-exercise-btn");

const canvasCtx = canvasElement.getContext("2d");

// Exercise configurations
const exerciseConfigs = {
    'pushup': {
        name: '푸시업',
        isTimed: false,
        repTime: 3,
        landmarks: [11, 12, 13, 14, 15, 16, 23, 24, 25, 26, 27, 28],
        tips: [
            '어깨를 너무 넓게 벌리지 마세요',
            '팔꿈치를 몸통에 가깝게 유지하세요',
            '허리를 곧게 펴고 복근에 힘을 주세요',
            '가슴이 바닥에 닿을 때까지 내려가세요'
        ],
        checkPose: checkPushupPose
    },
    'squat': {
        name: '스쿼트',
        isTimed: false,
        repTime: 4,
        landmarks: [11, 12, 23, 24, 25, 26, 27, 28, 31, 32],
        tips: [
            '발을 어깨 너비만큼 벌리세요',
            '무릎이 발끝을 넘지 않도록 하세요',
            '허벅지가 바닥과 평행이 될 때까지 앉으세요',
            '가슴을 펴고 시선은 앞을 향하세요'
        ],
        checkPose: checkSquatPose
    },
    'plank': {
        name: '플랭크',
        isTimed: true,
        repTime: 30,
        landmarks: [11, 12, 13, 14, 15, 16, 23, 24, 25, 26, 27, 28],
        tips: [
            '어깨, 팔꿈치, 손목이 일직선이 되도록 하세요',
            '허리를 곧게 펴고 복근에 힘을 주세요',
            '엉덩이가 너무 높거나 낮지 않도록 하세요',
            '호흡을 자연스럽게 유지하세요'
        ],
        checkPose: checkPlankPose
    },
    'dumbbell-curl': {
        name: '덤벨 컬',
        isTimed: false,
        repTime: 3,
        landmarks: [11, 12, 13, 14, 15, 16, 23, 24],
        tips: [
            '어깨를 고정하고 팔꿈치를 몸통에 붙이세요',
            '팔꿈치를 움직이지 않고 팔뚝만 움직이세요',
            '덤벨을 천천히 올리고 내리세요',
            '어깨를 올리지 마세요'
        ],
        checkPose: checkDumbbellCurlPose
    }
};

// Initialize the page
document.addEventListener('DOMContentLoaded', function () {
    initializeExercise();
    setupEventListeners();
    createPoseLandmarker().catch(console.error);
});

function initializeExercise() {
    // Get exercise configuration
    const exerciseKey = getExerciseKey(currentExerciseTitle);
    exerciseConfig = exerciseConfigs[exerciseKey];

    if (!exerciseConfig) {
        console.error('Exercise configuration not found:', currentExerciseTitle);
        return;
    }

    // Update UI based on exercise type
    isTimedExercise = exerciseConfig.isTimed;

    if (isTimedExercise) {
        repsTarget.style.display = 'none';
        timerTarget.style.display = 'flex';
        repSection.style.display = 'none';
        timerSection.style.display = 'block';
    } else {
        repsTarget.style.display = 'flex';
        timerTarget.style.display = 'none';
        repSection.style.display = 'block';
        timerSection.style.display = 'none';
    }

    // Update exercise name
    const exerciseNameSpan = document.querySelector('#exercise-name span');
    if (exerciseNameSpan) {
        exerciseNameSpan.textContent = exerciseConfig.name;
    }

    // Show exercise tips
    showExerciseTips();

    // Initialize progress circle
    updateProgressCircle();

    // Initialize accuracy
    updateAccuracy();

    // Initialize current rep display
    updateCurrentRepDisplay();

    // Initialize total time elapsed display
    updateTotalTimeElapsed();
}

function getExerciseKey(exerciseName) {
    // Map exercise names to config keys
    const exerciseMap = {
        'pushup': 'pushup',
        'squat': 'squat',
        'plank': 'plank',
        'dumbbell-curl': 'dumbbell-curl'
    };
    return exerciseMap[exerciseName] || 'pushup';
}

function showExerciseTips() {
    if (exerciseConfig && exerciseConfig.tips) {
        tipsContent.innerHTML = '';
        exerciseConfig.tips.forEach((tip, index) => {
            const tipElement = document.createElement('div');
            tipElement.className = 'tip-item fade-in';
            tipElement.innerHTML = `
                <h5>팁 ${index + 1}</h5>
                <p>${tip}</p>
            `;
            tipsContent.appendChild(tipElement);
        });
        exerciseTipsSection.style.display = 'block';
    }
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
}

let localStream;
const hasGetUserMedia = () => !!navigator.mediaDevices?.getUserMedia;

async function enableCam(event) {
    if (!poseLandmarker) {
        console.log("Wait! poseLandmarker not loaded yet.");
        return;
    }

    const constraints = {
        video: true,
        audio: false
    };

    try {
        localStream = await navigator.mediaDevices.getUserMedia(constraints);
        video.srcObject = localStream;

        // Wait for the video to be ready before continuing
        video.onloadedmetadata = () => {
            video.play();

            // Now that video dimensions are available, set canvas size
            canvasElement.width = video.videoWidth;
            canvasElement.height = video.videoHeight;

            webcamRunning = true;
            enableWebcamButton.disabled = true;
            enableWebcamButton.innerHTML = '<i class="bi bi-camera-video-fill"></i><span>웹캠 실행 중...</span>';

            predictWebcam();

            updateFeedback("웹캠이 시작되었습니다. 운동을 시작하세요!", "info");
        };

    } catch (error) {
        console.error("Error accessing webcam:", error);
        updateFeedback("웹캠 접근에 실패했습니다. 권한을 확인해주세요.", "error");
    }
}

function predictWebcam() {
    if (!webcamRunning) return;

    function frameLoop() {
        if (!webcamRunning) return;

        const now = performance.now()
        poseLandmarker.detectForVideo(video, now, (result) => {
            canvasCtx.save();
            canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);

            if (result.landmarks.length > 0) {
                const landmarks = result.landmarks[0];

                // Create a structured object of named landmark points
                const landmarkPoints = {
                    leftShoulder: landmarks[11],
                    rightShoulder: landmarks[12],
                    leftElbow: landmarks[13],
                    rightElbow: landmarks[14],
                    leftWrist: landmarks[15],
                    rightWrist: landmarks[16],
                    leftHip: landmarks[23],
                    rightHip: landmarks[24],
                    leftKnee: landmarks[25],
                    rightKnee: landmarks[26],
                    leftAnkle: landmarks[27],
                    rightAnkle: landmarks[28],
                    leftToe: landmarks[31],
                    rightToe: landmarks[32]
                };

                // Draw landmarks
                drawLandmarks(landmarks);

                // Check pose based on exercise type
                if (exerciseConfig && exerciseConfig.checkPose) {
                    exerciseConfig.checkPose(landmarkPoints);
                }
            } else {
                updateFeedback("사람이 감지되지 않습니다. 웹캠 앞에 서주세요.", "warning");
                isInGoodPose = false;
            }

            canvasCtx.restore();
        });

        requestAnimationFrame(frameLoop);
    }

    frameLoop();
}

function drawLandmarks(landmarks) {
    if (!exerciseConfig) return;

    // First, draw the landmark points (circles)
    exerciseConfig.landmarks.forEach(index => {
        const landmark = landmarks[index];
        if (landmark && checkVisibility(landmark)) {
            canvasCtx.beginPath();
            canvasCtx.arc(landmark.x * canvasElement.width, landmark.y * canvasElement.height, 5, 0, 2 * Math.PI);
            canvasCtx.fillStyle = '#00d084'; // Green color for points
            canvasCtx.fill();
        }
    });

    // Next, define and draw the connectors (lines)
    const connections = [
        // Arms
        [11, 13], [13, 15], // left arm (shoulder -> elbow -> wrist)
        [12, 14], [14, 16], // right arm (shoulder -> elbow -> wrist)
        // Torso
        [11, 12], // shoulder to shoulder
        [11, 23], // left shoulder to left hip
        [12, 24], // right shoulder to right hip
        [23, 24], // hip to hip
        // Legs
        [23, 25], [25, 27], // left leg (hip -> knee -> ankle)
        [24, 26], [26, 28]  // right leg (hip -> knee -> ankle)
    ];

    connections.forEach(([startIdx, endIdx]) => {
        // Only draw connections if both landmarks are relevant for the current exercise
        if (!exerciseConfig.landmarks.includes(startIdx) || !exerciseConfig.landmarks.includes(endIdx)) {
            return;
        }

        const start = landmarks[startIdx];
        const end = landmarks[endIdx];

        // Check visibility of both start and end points before drawing the line
        if (start && end && checkVisibility(start) && checkVisibility(end)) {
            canvasCtx.beginPath();
            canvasCtx.moveTo(start.x * canvasElement.width, start.y * canvasElement.height);
            canvasCtx.lineTo(end.x * canvasElement.width, end.y * canvasElement.height);
            canvasCtx.strokeStyle = '#ffffff'; // White color for lines
            canvasCtx.lineWidth = 2;
            canvasCtx.stroke();
        }
    });
}

function checkVisibility(landmark) {
    return landmark.visibility > 0.5;
}

// Exercise-specific pose checking functions
function checkPushupPose(points) {
    if (workoutPaused) return;
    // Destructure the landmark points from the input object
    const {
        leftShoulder, rightShoulder,
        leftElbow, rightElbow,
        leftWrist, rightWrist,
        leftHip, rightHip,
        leftAnkle, rightAnkle
    } = points;

    // 1. Determine the user's direction based on which side is more visible.
    // This is crucial for handling side-on views where one side is obscured.
    let shoulder, elbow, wrist, hip, ankle;

    // Check if the left side is more visible than the right.
    if (leftShoulder?.visibility > rightShoulder?.visibility && leftHip?.visibility > rightHip?.visibility) {
        shoulder = leftShoulder;
        elbow = leftElbow;
        wrist = leftWrist;
        hip = leftHip;
        ankle = leftAnkle;
    }
    // Check if the right side is more visible than the left.
    else if (rightShoulder?.visibility > leftShoulder?.visibility && rightHip?.visibility > leftHip?.visibility) {
        shoulder = rightShoulder;
        elbow = rightElbow;
        wrist = rightWrist;
        hip = rightHip;
        ankle = rightAnkle;
    }
    // If a clear direction cannot be determined, provide feedback and exit.
    else {
        updateFeedback("푸시업은 측면 자세에서 측정됩니다. 옆으로 서주세요.", "warning");
        isInGoodPose = false;
        return;
    }

    // 2. Validate that all necessary landmarks ON THE VISIBLE SIDE are clear.
    // This check happens *after* determining direction to avoid premature failure.
    if (!shoulder || !elbow || !wrist || !hip || !ankle ||
        !checkVisibility(shoulder) || !checkVisibility(elbow) || !checkVisibility(wrist) || !checkVisibility(hip) || !checkVisibility(ankle)) {
        updateFeedback("몸 전체가 선명하게 보이도록 웹캠 위치를 조정해주세요.", "warning");
        isInGoodPose = false;
        return;
    }

    // 3. Calculate key metrics for the pose analysis.
    const armAngle = calculateAngle(shoulder, elbow, wrist);
    const hipState = calculateHipPosition(shoulder, hip, ankle);
    const pushupBodyAngle = calculatePushupBodyAngle(shoulder, ankle);

    // 4. Implement the state machine for accurate rep counting.
    // State: 'ready' - Waiting for the user to assume the starting position.
    if (exerciseState === 'ready') {
        if (pushupBodyAngle < 27 && hipState === 'straight' && armAngle > 145) {
            exerciseState = 'down';
            updateFeedback("준비 완료! 내려가세요.", "good");
        } else {
            updateFeedback("팔과 허리를 곧게 펴고 시작 자세를 취하세요.", "warning");
        }
        return;
    }

    // Check if the user has stood up
    if (pushupBodyAngle > 45) {
        updateFeedback("푸시업 자세가 아닙니다. 다시 푸시업 자세를 취하세요.", "warning");
        isInGoodPose = false;
        // The user has stood up, reset the state
        exerciseState = 'ready';
        return;
    }

    // Always prioritize feedback on incorrect hip posture.
    if (hipState !== 'straight') {
        let code = '';
        let desc = '';
        if (hipState === 'sagging') {
            updateFeedback("허리가 너무 처졌습니다. 복근에 힘을 주고 허리를 드세요!", "warning");
            code = 'HIP_LOW';
            desc = '허리가 쳐졌습니다.';
        } else {
            updateFeedback("엉덩이가 너무 높습니다. 허리를 일직선으로 내려주세요!", "warning");
            code = 'HIP_HIGH';
            desc = '허리가 들렸습니다.';
        }
        handleBadPose(code, desc);
        return;
    }

    // State: 'down' - User is moving from the top to the bottom of the push-up.
    if (exerciseState === 'down') {
        if (armAngle < 110 && pushupBodyAngle < 18) { // User has reached the bottom.
            exerciseState = 'up'; // Transition to the 'up' state.
            updateFeedback("좋습니다! 이제 올라오세요.", "info");
        } else {
            updateFeedback("내려가는 중...", "good");
        }
        handleGoodPose(); // As long as hips are straight, the pose is good.
    }
    // State: 'up' - User is moving from the bottom back to the top.
    else if (exerciseState === 'up') {
        if (armAngle > 115 && pushupBodyAngle > 21) { // User has returned to the top, completing a rep.
            goodReps++;
            totalReps++;
            currentRep++;
            currentGoodReps++;
            exerciseState = 'down'; // Reset state for the next repetition.
            updateFeedback(`${goodReps}개 완료!`, "good");
            updateRepDisplay();
            checkSetCompletion();
        } else {
            updateFeedback("올라오는 중...", "good");
        }
        handleGoodPose();
    }
    updateAccuracy();
}

function checkSquatPose(points) {
    if (workoutPaused) return;
    const {
        leftShoulder, rightShoulder,
        leftElbow, rightElbow,
        leftWrist, rightWrist,
        leftHip, rightHip,
        leftKnee, rightKnee,
        leftAnkle, rightAnkle,
        leftToe, rightToe
    } = points;

    // 1. Determine the user's direction based on which side is more visible.
    // This is crucial for handling side-on views where one side is obscured.
    let shoulder, elbow, wrist, hip, knee, ankle, toe;

    // Check if the left side is more visible than the right.
    if (leftShoulder?.visibility > rightShoulder?.visibility && leftHip?.visibility > rightHip?.visibility) {
        shoulder = leftShoulder;
        elbow = leftElbow;
        wrist = leftWrist;
        hip = leftHip;
        knee = leftKnee;
        ankle = leftAnkle;
        toe = leftToe;
    }
    // Check if the right side is more visible than the left.
    else if (rightShoulder?.visibility > leftShoulder?.visibility && rightHip?.visibility > leftHip?.visibility) {
        shoulder = rightShoulder;
        elbow = rightElbow;
        wrist = rightWrist;
        hip = rightHip;
        knee = rightKnee;
        ankle = rightAnkle;
        toe = rightToe;
    }
    // If a clear direction cannot be determined, provide feedback and exit.
    else {
        updateFeedback("스쿼트는 측면 자세에서 측정됩니다. 옆으로 서주세요.", "warning");
        isInGoodPose = false;
        return;
    }

    // 2. Validate that all necessary landmarks ON THE VISIBLE SIDE are clear.
    // This check happens *after* determining direction to avoid premature failure.
    if (!shoulder || !elbow || !wrist || !hip || !ankle || !toe ||
        !checkVisibility(shoulder) || !checkVisibility(elbow) || !checkVisibility(wrist) || !checkVisibility(hip) || !checkVisibility(ankle)) {
        updateFeedback("몸 전체가 선명하게 보이도록 웹캠 위치를 조정해주세요.", "warning");
        isInGoodPose = false;
        return;
    }

    // Check knee angle for squat depth
    const kneeAngle = calculateAngle(hip, knee, ankle);
    const bodyAngle = calculateAngle(shoulder, hip, knee);
    const kneeAnkleDiff = Math.abs(knee.x - ankle.x);
    const kneeAnkleTolerance = 0.06;

    if (exerciseState === 'ready') {
        if (kneeAngle > 150 && bodyAngle > 150) {
            exerciseState = 'down';
            updateFeedback("좋은 자세입니다! 천천히 앉으면서 스쿼트를 시작하세요.", "good");
        } else {
            updateFeedback("서서 측면을 바라보며 시작 자세를 취하세요.", "warning");
        }
        return;
    }

    if (kneeAnkleDiff > kneeAnkleTolerance) {
        updateFeedback("무릎이 발끝을 넘어갔습니다! 엉덩이를 뒤로 빼세요.", "warning");
        handleBadPose("KNEE_FORWARD", "무릎이 발끝을 넘어갔습니다.");
        return;
    }

    if (exerciseState === 'down') {
        if (kneeAngle < 100 && bodyAngle < 100) {
            exerciseState = 'up';
            updateFeedback("좋습니다! 이제 올라오세요.", "info")
        } else {
            updateFeedback("내려가는 중...", "good");
        }
        handleGoodPose();
    }
    else if (exerciseState === 'up') {
        if (kneeAngle > 150 && bodyAngle > 150) {
            goodReps++;
            totalReps++;
            currentRep++;
            currentGoodReps++;
            exerciseState = 'down';
            updateFeedback(`${goodReps}개 완료!`, "good");
            updateRepDisplay();
            checkSetCompletion();
        } else {
            updateFeedback("올라오는 중...", "good");
        }
        handleGoodPose();
    }
    updateAccuracy();
}

function checkPlankPose(points) {
    if (workoutPaused) return;
    const {
        leftShoulder, rightShoulder,
        leftElbow, rightElbow,
        leftWrist, rightWrist,
        leftHip, rightHip,
        leftAnkle, rightAnkle
    } = points;

    // 1. Determine the user's direction based on which side is more visible.
    // This is crucial for handling side-on views where one side is obscured.
    let shoulder, elbow, wrist, hip, ankle;

    // Check if the left side is more visible than the right.
    if (leftShoulder?.visibility > rightShoulder?.visibility && leftHip?.visibility > rightHip?.visibility) {
        shoulder = leftShoulder;
        elbow = leftElbow;
        wrist = leftWrist;
        hip = leftHip;
        ankle = leftAnkle;
    }
    // Check if the right side is more visible than the left.
    else if (rightShoulder?.visibility > leftShoulder?.visibility && rightHip?.visibility > leftHip?.visibility) {
        shoulder = rightShoulder;
        elbow = rightElbow;
        wrist = rightWrist;
        hip = rightHip;
        ankle = rightAnkle;
    }
    // If a clear direction cannot be determined, provide feedback and exit.
    else {
        updateFeedback("플랭크는 측면 자세에서 측정됩니다. 옆으로 서주세요.", "warning");
        isInGoodPose = false;
        return;
    }

    // 2. Validate that all necessary landmarks ON THE VISIBLE SIDE are clear.
    // This check happens *after* determining direction to avoid premature failure.
    if (!shoulder || !elbow || !wrist || !hip || !ankle ||
        !checkVisibility(shoulder) || !checkVisibility(elbow) || !checkVisibility(wrist) || !checkVisibility(hip) || !checkVisibility(ankle)) {
        updateFeedback("몸 전체가 선명하게 보이도록 웹캠 위치를 조정해주세요.", "warning");
        isInGoodPose = false;
        return;
    }

    // 3. Calculate key metrics for the pose analysis.
    const armAngle = calculateAngle(shoulder, elbow, wrist);
    const hipState = calculateHipPosition(shoulder, hip, ankle);
    const groundBodyAngle = calculatePushupBodyAngle(shoulder, ankle);

    if (exerciseState === 'ready') {
        if (groundBodyAngle < 40 && hipState === 'straight' && armAngle < 110) {
            exerciseState = 'holding';
            updateFeedback("완벽한 플랭크 자세입니다! 이제 시작합니다.", "good");
        } else {
            updateFeedback("플랭크 자세를 취하세요.", "warning");
        }
        return;
    }

    // Check if the user has stood up
    if (groundBodyAngle > 50) {
        updateFeedback("플랭크 자세가 아닙니다. 다시 플랭크 자세를 취하세요.", "warning");
        isInGoodPose = false;
        // The user has stood up, reset the state
        exerciseState = 'ready';
        return;
    }

    if (hipState !== 'straight') {
        let code = '';
        let desc = '';
        if (hipState === 'sagging') {
            updateFeedback("허리가 너무 처졌습니다. 복근에 힘을 주고 허리를 드세요!", "warning");
            code = 'HIP_LOW';
            desc = '허리가 쳐졌습니다.';
        } else {
            updateFeedback("엉덩이가 너무 높습니다. 허리를 일직선으로 내려주세요!", "warning");
            code = 'HIP_HIGH';
            desc = '허리가 들렸습니다.';
        }
        handleBadPose(code, desc);
        return;
    }
    handleGoodPose();
}

function checkDumbbellCurlPose(points) {
    if (workoutPaused) return;
    const {
        leftShoulder, rightShoulder,
        leftElbow, rightElbow,
        leftWrist, rightWrist,
        leftHip, rightHip,
    } = points;

    if (!leftShoulder || !rightShoulder || !leftElbow || !rightElbow || !leftWrist || !rightWrist || !leftHip || !rightHip ||
        !checkVisibility(leftShoulder) || !checkVisibility(rightShoulder) || !checkVisibility(leftElbow) || !checkVisibility(rightElbow) ||
        !checkVisibility(leftWrist) || !checkVisibility(rightWrist) || !checkVisibility(leftHip) || !checkVisibility(rightHip)) {
        updateFeedback("자세를 인식할 수 없습니다. 덤벨을 들고 웹캠 앞에 서주세요.", "warning");
        isInGoodPose = false;
        return;
    }

    // --- Calculate angles and heights ---
    const leftArmAngle = calculateAngle(leftShoulder, leftElbow, leftWrist);
    const rightArmAngle = calculateAngle(rightShoulder, rightElbow, rightWrist);
    const avgArmAngle = (leftArmAngle + rightArmAngle) / 2;

    const avgWristY = (leftWrist.y + rightWrist.y) / 2;
    const avgShoulderY = (leftShoulder.y + rightShoulder.y) / 2;

    // --- Rep position thresholds ---
    const curlUpAngleThreshold = 70;     // Fully bent
    const curlDownAngleThreshold = 140;  // Fully extended
    const wristHighThreshold = avgShoulderY + 0.03;  // Wrist near/above shoulder
    const wristLowThreshold = avgShoulderY + 0.12;   // Wrist well below shoulder

// Elbow drift check using body proportions
    if (initialLeftElbow && initialRightElbow && leftShoulder && rightShoulder && leftHip && rightHip) {
        // Reference distances for proportional scaling
        const shoulderWidth = Math.abs(rightShoulder.x - leftShoulder.x);
        const torsoHeight = Math.abs((leftShoulder.y + rightShoulder.y) / 2 - (leftHip.y + rightHip.y) / 2);

        // Drift relative to initial positions
        const leftDriftX = Math.abs(leftElbow.x - initialLeftElbow.x);
        const rightDriftX = Math.abs(rightElbow.x - initialRightElbow.x);
        const leftDriftY = Math.abs(leftElbow.y - initialLeftElbow.y);
        const rightDriftY = Math.abs(rightElbow.y - initialRightElbow.y);

        // Allowable drift (proportion of body size)
        const maxXDrift = shoulderWidth * 0.16;  // 15% of shoulder width
        const maxYDrift = torsoHeight * 0.13;    // 15% of torso height

        // Horizontal drift check
        if (leftDriftX > maxXDrift || rightDriftX > maxXDrift) {
            updateFeedback("팔꿈치가 옆으로 벌어졌습니다. 몸에 붙이세요.", "warning");
            handleBadPose("ELBOW_X_DRIFT", "팔꿈치가 옆으로 벌어졌습니다.");
            return;
        }

        // Vertical drift check
        if (leftDriftY > maxYDrift || rightDriftY > maxYDrift) {
            updateFeedback("팔꿈치가 너무 올라갔거나 내려갔습니다. 고정하세요.", "warning");
            handleBadPose("ELBOW_Y_DRIFT", "팔꿈치가 위로 들렸습니다.");
            return;
        }
    }


    // --- Initial ready position ---
    if (exerciseState === 'ready') {
        if (avgArmAngle > curlDownAngleThreshold) {
            // User is in starting pose. Begin stability timer
            if (!elbowLockTime) {
                elbowLockTime = Date.now();
                updateFeedback("잠시만 기다려주세요... 자세 확인 중", "info");
            } else if (Date.now() - elbowLockTime > 5000) {  // Wait 1 second
                initialLeftElbow = {x: leftElbow.x, y: leftElbow.y};
                initialRightElbow = {x: rightElbow.x, y: rightElbow.y};
                exerciseState = 'up';
                updateFeedback("준비 완료. 덤벨을 들어올리세요.", "good");
            }
        } else {
            elbowLockTime = null; // Reset timer if pose breaks
            updateFeedback("팔을 자연스럽게 내리고 시작하세요.", "warning");
        }
    }

    // --- Rep State Machine ---
    if (exerciseState === 'up') {
        if (avgArmAngle < curlUpAngleThreshold && avgWristY < wristHighThreshold) {
            exerciseState = 'down';
            updateFeedback("좋아요! 천천히 내려주세요.", "info");
            handleGoodPose();
        } else {
            updateFeedback("들어올리는 중...", "good");
        }
        handleGoodPose();
    } else if (exerciseState === 'down') {
        if (avgArmAngle > curlDownAngleThreshold && avgWristY > wristLowThreshold) {
            goodReps++;
            totalReps++;
            currentRep++;
            currentGoodReps++;
            exerciseState = 'up';
            updateRepDisplay();
            checkSetCompletion();
            updateFeedback(`${goodReps}개 완료!`, "good");
        } else {
            updateFeedback("내리는 중...", "good");
        }
        handleGoodPose();
    }

    updateAccuracy();
}

function resetExerciseState(exercise) {
    if (exercise === 'dumbbell-curl') {
        initialLeftElbow = null;
        initialRightElbow = null;
        elbowLockTime = null;
    }
}

function logFeedback(code, desc) {
    let repNo = currentRep + 1;
    feedbackEvents.push({
        issueCode: code,
        description: desc,
        setNo: currentSet,
        repNo: repNo,
        timestamp: globalExerciseTime
    });
}

// 올바른 자세 인식/기록
function handleGoodPose() {
    if (badPoseStartTime) {
        badPoseStartTime = null;
    }

    if (!isInGoodPose) {
        isInGoodPose = true;
        lastPoseTime = Date.now();
    }

    if (isTimedExercise) {
        // For timed exercises, accumulate good time
        if (Date.now() - lastPoseTime >= 100) { // Update every 100ms
            goodTime++;
            currentExerciseTime++;
            globalExerciseTime++;
            lastPoseTime = Date.now();
            updateTimerDisplay();
            checkSetCompletion();
        }
    } else {
        const now = Date.now();
        const elapsed = now - lastPoseTime;

        if (elapsed >= 100) {
            globalExerciseTime += Math.floor(elapsed / 100); // keep consistent with timed
            lastPoseTime = now;
        }
    }
}

function handleBadPose(code, desc) {
    if (isInGoodPose) {
        isInGoodPose = false;
        badPoseStartTime = Date.now();
    }

    const now = Date.now();
    const elapsed = now - lastPoseTime;

    if (badPoseStartTime && (now - badPoseStartTime >= badPoseDurationThreshold)) {
        if (isTimedExercise) {
            if (elapsed >= 100) {
                badTime++;
                currentExerciseTime++;
                globalExerciseTime++;
                lastPoseTime = now;
                updateTimerDisplay();
                logFeedback(code, desc);
            }
        } else {
            badReps++;
            totalReps++;
            currentRep++;
            updateRepDisplay();
            checkSetCompletion();
            badPoseStartTime = null;
            logFeedback(code, desc);

            if (elapsed >= 100) {
                globalExerciseTime += Math.floor(elapsed / 100);
                lastPoseTime = now;
            }
        }
    }
}

// Utility functions
function calculateAngle(a, b, c) {
    const radians = Math.atan2(c.y - b.y, c.x - b.x) - Math.atan2(a.y - b.y, a.x - b.x);
    let angle = Math.abs(radians * 180.0 / Math.PI);
    if (angle > 180.0) angle = 360 - angle;
    return angle;
}

function calculateLevelAngle(a, b) {
    const radians = Math.atan2(b.y - a.y, b.x - a.x);
    return radians * 180.0 / Math.PI;
}

function calculatePushupBodyAngle(shoulder, ankle) {
    // This function calculates the angle of the body (shoulder to ankle line) with the horizontal
    if (!shoulder || !ankle || !checkVisibility(shoulder) || !checkVisibility(ankle)) {
        return 90; // Default to a high angle if landmarks are not visible
    }

    // Calculate the angle using the arctan of the slope (deltaY / deltaX)
    const deltaY = ankle.y - shoulder.y;
    const deltaX = ankle.x - shoulder.x;
    const angleRad = Math.atan2(Math.abs(deltaY), Math.abs(deltaX));
    return angleRad * (180 / Math.PI);
}

function calculateHipPosition(shoulder, hip, ankle) {
    // Return a default value if any landmark is missing
    if (!shoulder || !hip || !ankle || !checkVisibility(shoulder) || !checkVisibility(hip) || !checkVisibility(ankle)) {
        return "straight";
    }

    // Calculate the expected 'y' position of the hip if the body were a straight line
    const t = (hip.x - shoulder.x) / (ankle.x - shoulder.x);
    const expectedHipY = shoulder.y + t * (ankle.y - shoulder.y);

    // Get the difference between the actual and expected hip position
    const hipOffset = hip.y - expectedHipY;

    if (hipOffset < -0.05) {
        return "piking";
    }
    if (hipOffset > 0.07) {
        return "sagging";
    }
    if (hipOffset > 0.03) {
        return (calculatePushupBodyAngle(shoulder, ankle) < 20) ? "straight" : "sagging";
    }

    return "straight";
}

function checkSetCompletion() {
    if (isTimedExercise) {
        // For timed exercises, check if good time reaches target
        if (goodTime >= targetTime) {
            completeSet();
        }
    } else {
        // For rep-based exercises, check if good reps reach target
        if (currentGoodReps >= targetReps) {
            completeSet();
        }
    }
}

function completeSet() {
    workoutPaused = true;
    resetExerciseState(currentExerciseTitle);
    if (currentSet < targetSets) {
        exerciseState = 'ready';
        // Show rest timer before next set
        showRestTimer();
        updateFeedback("세트 완료! 휴식 시간을 가져보세요. 😌", "good");
    } else {
        // Final set completed
        setDisplay.innerHTML = "완료!";
        setDisplay.classList.add("pulse");

        // Include rest time in total duration
        const totalDuration = Math.floor(globalExerciseTime / 10) + totalRestTime;

        // Save session data with rest time included
        saveSessionDataWithRestTime(totalDuration);
    }
}

// UI Update functions
function updateFeedback(message, status = "info") {
    feedbackDiv.textContent = message;
    feedbackDiv.className = `feedback-content status-${status}`;
}

function updateSetDisplay() {
    currentSetSpan.textContent = currentSet;
    currentSetDisplay.textContent = currentSet;
    setDisplay.textContent = currentSet;
}

function updateRepDisplay() {
    goodRepsSpan.textContent = goodReps;
    badRepsSpan.textContent = badReps;
    totalRepsSpan.textContent = totalReps;
    repsProgressSpan.textContent = goodReps;
    updateCurrentRepDisplay();
}

function updateCurrentRepDisplay() {
    currentRepDisplay.textContent = currentGoodReps;
    currentRepValue.textContent = currentGoodReps;
}

function updateTotalTimeElapsed() {
    const minutes = Math.floor(globalExerciseTime / 10 / 60);
    const seconds = Math.floor(globalExerciseTime / 10) % 60;
    const timeString = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    totalTimeElapsedSpan.textContent = timeString;
}

function updateTimerDisplay() {
    goodTimeSpan.textContent = Math.floor(goodTime / 10); // Convert to seconds
    badTimeSpan.textContent = Math.floor(badTime / 10); // Convert to seconds
    totalTimeSpan.textContent = Math.floor(globalExerciseTime / 10); // Convert to seconds
    timeProgressSpan.textContent = Math.floor(goodTime / 10); // Convert to seconds
    updateTotalTimeElapsed();
}

function updateAccuracy() {
    let accuracy = 0;

    if (isTimedExercise) {
        accuracy = currentExerciseTime > 0 ? (goodTime / currentExerciseTime) * 100 : 0;
    } else {
        accuracy = totalReps > 0 ? (goodReps / totalReps) * 100 : 0;
    }

    accuracyPercentageSpan.textContent = `${Math.round(accuracy)}%`;
}

function updateProgressCircle() {
    const progress = (currentSet - 1) / targetSets;
    const circle = document.querySelector('.progress-ring-circle');
    const circumference = 2 * Math.PI * 54;
    const offset = circumference - (progress * circumference);
    circle.style.strokeDashoffset = offset;
}

function calculateAccuracy() {
    if (isTimedExercise) {
        return currentExerciseTime > 0 ? (goodTime / currentExerciseTime) * 100 : 0;
    } else {
        return totalReps > 0 ? (goodReps / totalReps) * 100 : 0;
    }
}

// Save session data to database
function saveSessionDataWithRestTime(totalDuration) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    let performedSets = currentSet;
    if (workoutDiscontinued) {
        performedSets = currentSet - 1;
    }
    const sessionData = {
        exerciseId: currentExerciseId,
        targetSets: targetSets,
        targetReps: targetReps,
        performedSets: performedSets,
        performedReps: totalReps,
        goodReps: goodReps,
        targetTime: targetTime,
        performedTime: Math.floor(currentExerciseTime / 10), // Convert to seconds
        goodTime: Math.floor(goodTime / 10), // Convert to seconds
        accuracyPercent: calculateAccuracy(),
        duration: totalDuration, // Total duration including rest time
        performedDate: Date.now()
    };

    fetch('/api/live-coaching/save-session', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(sessionData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                const logId = data.data;
                console.log('Session saved successfully:', data.message);
                updateFeedback("세션이 성공적으로 저장되었습니다! 📊", "good");

                fetch('/api/live-coaching/save-feedback', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify({
                        coachingLogId: logId,
                        feedbackList: feedbackEvents
                    })
                })
                    .then(res => res.json())
                    .then(result => {
                        if (result.success) {
                            console.log("성공적으로 피드백 저장됨");
                        } else {
                            console.error("피드백 저장 실패", result.message);
                        }
                    })
                    .catch(error => {
                        console.error("피드백 저장 오류:", error);
                    })
            } else {
                console.error('Failed to save session:', data.message);
                updateFeedback("세션 저장에 실패했습니다.", "error");
            }
        })
        .catch(error => {
            console.error('Error saving session:', error);
            updateFeedback("세션 저장 중 오류가 발생했습니다.", "error");
        });
}

// 휴식 타이머 함수
function showRestTimer() {
    restTimerSection.style.display = 'block';
    restTimerSection.classList.add('active');
    currentRestTime = restTime;
    updateRestTimerDisplay();
    updateRestTimerRing();
}

function hideRestTimer() {
    restTimerSection.style.display = 'none';
    restTimerSection.classList.remove('active');
    stopRestTimer();
}

function startRestTimer() {
    if (isRestActive && !isRestPaused) return;

    if (isRestPaused) {
        // Resume paused timer
        isRestPaused = false;
        pauseRestButton.style.display = 'none';
        startRestButton.style.display = 'inline-flex';
    } else {
        // Start new timer
        isRestActive = true;
        isRestPaused = false;
        startRestButton.style.display = 'none';
        pauseRestButton.style.display = 'inline-flex';
    }

    restTimerInterval = setInterval(() => {
        if (currentRestTime > 0) {
            currentRestTime--;
            totalRestTime++;
            updateRestTimerDisplay();
            updateRestTimerRing();
        } else {
            completeRestTimer();
        }
    }, 1000);
}

function pauseRestTimer() {
    if (!isRestActive || isRestPaused) return;

    isRestPaused = true;
    clearInterval(restTimerInterval);
    pauseRestButton.style.display = 'none';
    startRestButton.style.display = 'inline-flex';
}

function stopRestTimer() {
    isRestActive = false;
    isRestPaused = false;
    clearInterval(restTimerInterval);
    currentRestTime = restTime;
    startRestButton.style.display = 'inline-flex';
    pauseRestButton.style.display = 'none';
    updateRestTimerDisplay();
    updateRestTimerRing();
}

function skipRestTimer() {
    stopRestTimer();
    hideRestTimer();
    startNextSet();
}

function completeRestTimer() {
    stopRestTimer();
    hideRestTimer();
    startNextSet();
}

function updateRestTimerDisplay() {
    restTimeDisplay.textContent = currentRestTime;
}

function updateRestTimerRing() {
    const progress = (restTime - currentRestTime) / restTime;
    const circumference = 2 * Math.PI * 45; // r=45 for rest timer
    const offset = circumference - (progress * circumference);
    restTimerRingProgress.style.strokeDashoffset = offset;
}

function startNextSet() {
    if (currentSet < targetSets) {
        currentSet++;
        resetSetCounters();
        updateSetDisplay();
        updateProgressCircle();
        updateFeedback("다음 세트를 시작하세요! 💪", "good");
        workoutPaused = false;
    } else {
        updateFeedback("운동이 완료되었습니다! 🎉", "good");
        setTimeout(() => {
            completeSet();
        }, 2000);
    }
}

function resetSetCounters() {
    currentGoodReps = 0;
    currentRep = 0;
    goodTime = 0;
    badTime = 0;
    currentExerciseTime = 0;
    resetExerciseState(currentExerciseTitle);
    updateRepDisplay();
    updateTimerDisplay();
    updateAccuracy();
    updateCurrentRepDisplay();
}

// Rest Timer Modal Functions
function showRestTimeEditModal() {
    restTimeEditModal.classList.add('show');
    restTimeInput.value = restTime;
}

function hideRestTimeEditModal() {
    restTimeEditModal.classList.remove('show');
}

function saveRestTime() {
    const newTime = parseInt(restTimeInput.value);
    if (newTime >= 10 && newTime <= 120) {
        restTime = newTime;
        currentRestTime = restTime;
        updateRestTimerDisplay();
        updateRestTimerRing();
        hideRestTimeEditModal();
    } else {
        alert('휴식 시간은 10초에서 120초 사이여야 합니다.');
    }
}

// Stop exercise function
function stopExercise() {
    // Stop webcam if running
    if (webcamRunning) {
        webcamRunning = false;
        if (video.srcObject) {
            const tracks = video.srcObject.getTracks();
            tracks.forEach(track => track.stop());
        }
    }

    // Stop rest timer if active
    if (isRestActive) {
        stopRestTimer();
    }

    workoutPaused = true;
    workoutDiscontinued = true;
    exerciseState = 'ready';
    // Calculate total duration including rest time
    const totalDuration = Math.floor(globalExerciseTime / 10) + totalRestTime;

    // Save session data
    saveSessionDataWithRestTime(totalDuration);

    // Update UI to show exercise stopped
    updateFeedback("운동이 중단되었습니다.", "warning");

    // Disable the stop button
    stopExerciseButton.disabled = true;
    stopExerciseButton.innerHTML = '<i class="bi bi-stop-circle"></i><span>운동 중단됨</span>';
}

// Enhanced setupEventListeners function
function setupEventListeners() {
    if (hasGetUserMedia()) {
        enableWebcamButton.addEventListener("click", enableCam);
    } else {
        console.warn("getUserMedia() is not supported by your browser");
        enableWebcamButton.disabled = true;
        enableWebcamButton.textContent = "웹캠을 지원하지 않습니다";
    }

    // Rest timer event listeners
    if (startRestButton) {
        startRestButton.addEventListener("click", startRestTimer);
    }

    if (pauseRestButton) {
        pauseRestButton.addEventListener("click", pauseRestTimer);
    }

    if (skipRestButton) {
        skipRestButton.addEventListener("click", skipRestTimer);
    }

    if (editRestTimeButton) {
        editRestTimeButton.addEventListener("click", showRestTimeEditModal);
    }

    if (saveRestTimeButton) {
        saveRestTimeButton.addEventListener("click", saveRestTime);
    }

    if (cancelRestEditButton) {
        cancelRestEditButton.addEventListener("click", hideRestTimeEditModal);
    }

    // Stop exercise button event listener
    if (stopExerciseButton) {
        stopExerciseButton.addEventListener("click", stopExercise);
    }

    // Close modal when clicking outside
    if (restTimeEditModal) {
        restTimeEditModal.addEventListener("click", (e) => {
            if (e.target === restTimeEditModal) {
                hideRestTimeEditModal();
            }
        });
    }

    // Close modal with Escape key
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && restTimeEditModal.classList.contains("show")) {
            hideRestTimeEditModal();
        }
    });
}