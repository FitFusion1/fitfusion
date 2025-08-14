/* ======================================================== */
/*       modal-food-search.html 모달 부분                    */
/* ======================================================== */
document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('modal-food-search');
    if (!modal) return;

    // 체크박스 변경 시, 같은 인덱스의 섭취량 input 활성/비활성 토글
    modal.addEventListener('change', e => {
        const cb = e.target;
        if (!(cb instanceof HTMLInputElement) || cb.type !== 'checkbox') return;

        const match = cb.name.match(/\[(\d+)\]/);
        if (!match) return;
        const idx = match[1];

        // 체크박스가 아닌 관련 input들 (hidden 포함)
        const relatedInputs = modal.querySelectorAll(`input[name^="mealRecords[${idx}]"]`);

        relatedInputs.forEach(input => {
            if (input.type !== 'checkbox') {
                input.disabled = !cb.checked;
            }
        });

        // 체크 시, 섭취량 값이 비어있으면 기본 제공량 세팅
        if (cb.checked) {
            const li = cb.closest('li');
            const servingSize = li?.dataset?.servingSize;
            const intakeInput = modal.querySelector(`input[name="mealRecords[${idx}].userIntake"]`);
            if (intakeInput && !intakeInput.value && servingSize) {
                intakeInput.value = servingSize;
            }
        }
    });

    // 모달 열릴 때, 버튼 data 속성으로 끼니 정보 세팅 및 타이틀 업데이트
    modal.addEventListener('show.bs.modal', event => {
        const button = event.relatedTarget;
        if (!button) return;

        const meal = button.getAttribute('data-meal');
        const mealDisplay = button.getAttribute('data-meal-display');

        const mealInput = modal.querySelector('input[name="mealTime"]');
        if (mealInput) mealInput.value = meal || '';

        const title = modal.querySelector('.modal-title');
        if (title) title.textContent = (mealDisplay || meal || '') + ' 추가';
    });
});

// 폼 제출 시 체크된 항목만 활성화하고 섭취량 기본값 보정
function enableCheckedOnly() {
    const form = document.querySelector('#modal-food-search form[method="post"]');
    if (!form) return true;

    const checkedBoxes = form.querySelectorAll('input[type="checkbox"]:checked');
    if (checkedBoxes.length === 0) {
        alert('선택된 음식이 없습니다.');
        return false;
    }

    // 모든 input 비활성화(checkbox 제외)
    form.querySelectorAll('input[name^="mealRecords["]').forEach(input => {
        if (input.type !== 'checkbox') input.disabled = true;
    });

    // 체크된 항목만 활성화 + 기본 섭취량 세팅
    checkedBoxes.forEach(cb => {
        const match = cb.name.match(/\[(\d+)\]/);
        if (!match) return;
        const idx = match[1];

        form.querySelectorAll(`input[name^="mealRecords[${idx}]"]`).forEach(input => {
            input.disabled = false;
        });

        const li = cb.closest('li');
        const servingSize = li?.dataset?.servingSize;
        const intakeInput = form.querySelector(`input[name="mealRecords[${idx}].userIntake"]`);
        if (intakeInput && !intakeInput.value && servingSize) {
            intakeInput.value = servingSize;
        }
    });

    return true;
}

/* ======================================================== */
/*       modal-detail-nutrient.html 모달 부분                */
// /* ===================================================== */
document.addEventListener("DOMContentLoaded", () => {
    const modal = document.querySelector(".modal-dialog");
    const btnClose = modal.querySelector(".btn-close");
    const cancelBtn = modal.querySelector("#detail-nutrient__cancel-btn");

    // // 모달 열기 함수 (예시, foodName 등 동적 처리 추가 가능)
    // function openModal() {
    //     modal.classList.add("show");
    //     document.body.style.overflow = "hidden"; // 배경 스크롤 막기
    // }
    //
    // // 모달 닫기 함수
    // function closeModal() {
    //     modal.classList.remove("show");
    //     document.body.style.overflow = ""; // 배경 스크롤 풀기
    // }
    //
    // // 닫기 버튼 클릭 이벤트
    // btnClose.addEventListener("click", closeModal);
    // cancelBtn.addEventListener("click", closeModal);

    // 백드롭 클릭시 모달 닫기 (모달 바깥 클릭)
    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            closeModal();
        }
    });

    // ESC 키 누르면 모달 닫기
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && modal.classList.contains("show")) {
            closeModal();
        }
    });

    // 필요시 외부에서 openModal 호출하도록 전역에 노출 가능
    // window.openDetailNutrientModal = openModal;
});

// 영양소 모달 값 계산
document.addEventListener('DOMContentLoaded', () => {
    // 모달마다 처리
    document.querySelectorAll('[id^="modal-nutrient-"]').forEach(modal => {
        const modalId = modal.id; // e.g. modal-nutrient-123
        const recordId = modalId.replace('modal-nutrient-', '');

        const intakeInput = document.getElementById(`user-intake-input-${recordId}`);
        if (!intakeInput) return;

        // 기준 섭취량 (servingSizeValue)
        const baseServing = parseFloat(intakeInput.value) || 100;

        // 영양소 span 요소 셀렉터 (기준 텍스트에서 값 읽어와 계산)
        const nutrients = {
            calories: modal.querySelector('p span[data-nutrient="calories"]'),
            carbohydrateG: modal.querySelector('p span[data-nutrient="carbohydrateG"]'),
            sugarG: modal.querySelector('p span[data-nutrient="sugarG"]'),
            sugarAlcoholG: modal.querySelector('p span[data-nutrient="sugarAlcoholG"]'),
            fiberG: modal.querySelector('p span[data-nutrient="fiberG"]'),
            proteinG: modal.querySelector('p span[data-nutrient="proteinG"]'),
            fatG: modal.querySelector('p span[data-nutrient="fatG"]'),
            saturatedFatG: modal.querySelector('p span[data-nutrient="saturatedFatG"]'),
            transFatG: modal.querySelector('p span[data-nutrient="transFatG"]'),
            cholesterolMg: modal.querySelector('p span[data-nutrient="cholesterolMg"]'),
            unsaturatedFatG: modal.querySelector('p span[data-nutrient="unsaturatedFatG"]'),
            sodiumMg: modal.querySelector('p span[data-nutrient="sodiumMg"]'),
            caffeineMg: modal.querySelector('p span[data-nutrient="caffeineMg"]'),
            calciumMg: modal.querySelector('p span[data-nutrient="calciumMg"]'),
        };

        // 원래 값(기준 1회 제공량 기준 값)을 data 속성으로 저장해두자 (js로 계산하기 위해)
        Object.values(nutrients).forEach(span => {
            if (!span) return;
            if (!span.dataset.original) {
                span.dataset.original = span.textContent.trim() === '(정보없음)' ? '0' : span.textContent.trim();
            }
        });

        // 계산 함수
        const updateNutrients = () => {
            const intake = parseFloat(intakeInput.value);
            if (isNaN(intake) || intake <= 0) return;

            Object.entries(nutrients).forEach(([key, span]) => {
                if (!span) return;
                const originalValue = parseFloat(span.dataset.original) || 0;
                const newValue = (originalValue * intake) / baseServing;
                span.textContent = Number(newValue.toFixed(2)).toLocaleString(undefined, {
                    minimumFractionDigits: 0,
                    maximumFractionDigits: 2,
                });
            });
        };

        intakeInput.addEventListener('input', updateNutrients);

// 초기값 계산
        updateNutrients();
    });
});


/* ======================================================== */
/*       modal-meal-score.html 모달 부분                */
// /* ===================================================== */
// document.addEventListener('DOMContentLoaded', () => {
//     const btnCurrent = document.getElementById('btn-current-date');
//     const mealScoreModal = new bootstrap.Modal(document.getElementById('modal-meal-score'));
//
//     btnCurrent.addEventListener('click', () => {
//         mealScoreModal.show();
//     });
// });

