document.addEventListener('DOMContentLoaded', function () {
    const exerciseForm = document.getElementById('exerciseForm');
    const exerciseSelect = document.getElementById('exerciseId');
    const setsInput = document.getElementById('targetSets');
    const repsInput = document.getElementById('targetReps');
    const timerInput = document.getElementById('targetTime');
    const timerInputBlock = document.getElementById('timer-input-field');
    const exerciseDescription = document.getElementById('exerciseDescription');
    const quickStartCards = document.querySelectorAll('.quick-start-card');
    let isTimedExercise = false;
    // 과한 이미지 로딩 방지 목적; 선택된 운동을 담는 변수
    let currentExercise = null;

    // 운동 선택 시 운동 상세 설명 업데이트
    function updateExerciseDescription(exercise) {
        const exerciseInfo = exerciseData[exercise];
        if (exerciseInfo) {
            document.getElementById('exerciseTitle').textContent = exerciseInfo.name;
            document.getElementById('exerciseDesc').textContent = exerciseInfo.description;

            if (exercise === "plank") {
                timerInputBlock.style.display = 'flex';
                isTimedExercise = true;
            } else {
                timerInputBlock.style.display = 'none';
                isTimedExercise = false;
            }
            const tipsList = document.getElementById('exerciseTips');
            tipsList.innerHTML = '';
            exerciseInfo.tips.forEach(tip => {
                const li = document.createElement('li');
                li.textContent = tip;
                tipsList.appendChild(li);
            });

            exerciseDescription.style.display = 'block';
        } else {
            exerciseDescription.style.display = 'none';
        }
    }

    // 미리보기 정보 업데이트
    function updatePreview(exercise) {
        const sets = setsInput.value;
        const reps = repsInput.value;
        const time = timerInput.value;
        const repTime = exerciseData[exercise].repTime;

        document.getElementById('previewSets').textContent = sets || '-';
        document.getElementById('previewReps').textContent = reps || '-';

        // 예상 운동시간 계산
        if (sets > 0 && reps > 0) {
            const totalReps = parseInt(sets) * parseInt(reps);
            const exerciseTime = isTimedExercise ?  totalReps * parseInt(time) : totalReps * parseInt(repTime);
            const restTime = (parseInt(sets) - 1) * 40; // 휴식 40초
            const totalTime = exerciseTime + restTime;
            const minutes = Math.floor(totalTime / 60);
            const seconds = totalTime % 60;
            document.getElementById('previewTime').textContent =
                `${minutes}분 ${seconds}초`;
        } else {
            document.getElementById('previewTime').textContent = '-';
        }

        // 운동 이미지 업데이트
        if (currentExercise !== exercise) {
            const previewImage = document.getElementById('previewImage');
            previewImage.innerHTML = `
                <img src="${exerciseData[exercise].previewImagePath}" alt="${exerciseData[exercise].title}" 
                     style="max-width: 100%; height: auto; border-radius: 8px;">
            `;
            currentExercise = exercise;
        }
    }

    // 선택 운동 변경 시 설명/미리보기 업데이트
    exerciseSelect.addEventListener('change', function () {
        updateExerciseDescription(getCurrentExerciseTitle());
        updatePreview(getCurrentExerciseTitle());
    });

    // 세트, 횟수, 시간 변경 감지
    setsInput.addEventListener('input', () => updatePreview(getCurrentExerciseTitle()));
    repsInput.addEventListener('input', () => updatePreview(getCurrentExerciseTitle()));
    timerInput.addEventListener('input', () => updatePreview(getCurrentExerciseTitle()));

    // 빠른 시작 카드 클릭 감지/처리하는 이벤트리스너 등록
    quickStartCards.forEach(card => {
        card.addEventListener('click', function () {
            const exerciseId = this.dataset.exerciseId;
            const sets = this.dataset.sets;
            const reps = this.dataset.reps;
            const timer = this.dataset.timer ? this.dataset.timer : 0;

            // 폼 입력값 업데이트
            exerciseSelect.value = exerciseId;
            setsInput.value = sets;
            repsInput.value = reps;
            timerInput.value = timer;

            // UI 업데이트
            updateExerciseDescription(getCurrentExerciseTitle());
            updatePreview(getCurrentExerciseTitle());

            // 모든 카드에서 'selected' 클래스 제거
            quickStartCards.forEach(c => c.classList.remove('selected'));
            // 클릭된 카드에 'selected' 클래스 추가
            this.classList.add('selected');
        });
    });

    // 폼 제출 로직
    exerciseForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const exerciseId = exerciseSelect.value;
        const sets = setsInput.value;
        const reps = repsInput.value;
        const timer = timerInput.value;

        if (!exerciseId) {
            alert('운동을 선택해주세요.');
            return;
        }

        if (!sets || !reps) {
            alert('세트 수와 반복 횟수를 입력해주세요.');
            return;
        }

        if (isTimedExercise && !timer) {
            alert('회당 시간을 입력해주세요.');
            return;
        }

        if (!isTimedExercise) {
            timerInputBlock.style.display = 'flex';
            timerInput.value = 0;
        }

        exerciseForm.submit();
    });

    function adjustInputValues(inputElement, min, max) {
        const inputValue = inputElement.value;
        if (inputValue < min) {
            inputElement.value = min;
        }
        if (inputValue > max) {
            inputElement.value = max;
        }
    }

    setsInput.addEventListener('input', function () {
        adjustInputValues(this, 1, 10);
    });
    repsInput.addEventListener('input', function () {
        adjustInputValues(this, 1, 100);
    });
    timerInput.addEventListener('input', function () {
        adjustInputValues(this, 15, 180);
    });

    function getCurrentExerciseTitle() {
        return exerciseSelect.options[exerciseSelect.selectedIndex]?.dataset.title;
    }
});