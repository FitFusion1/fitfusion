import {
    PoseLandmarker,
    FilesetResolver,
    DrawingUtils
} from "https://cdn.skypack.dev/@mediapipe/tasks-vision@0.10.0";

let poseLandmarker;
let runningMode = "IMAGE";

const createPoseLandmarker = async () => {
    const vision = await FilesetResolver.forVisionTasks(
        "https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.0/wasm"
    );
    poseLandmarker = await PoseLandmarker.createFromOptions(vision, {
        baseOptions: {
            modelAssetPath: "D:\\Documents\\IdeaProjects\\demo1\\app\\shared\\models\\pose_landmarker_heavy.task",
            delegate: "GPU"
        },
        runningMode: runningMode
    });
};

createPoseLandmarker();

async function handleClick(event) {

}
