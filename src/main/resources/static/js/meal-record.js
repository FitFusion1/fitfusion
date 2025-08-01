document.addEventListener("DOMContentLoaded", function () {
    const mealSelect = document.getElementById("mealSelect");

    // ✅ 모달 요소
    const modalFoodName = document.getElementById("modalFoodName");
    const modalFoodId = document.getElementById("modalFoodId");
    const modalMealTime = document.getElementById("modalMealTime");
    const modalGramInput = document.getElementById("modalGramInput");

    // ✅ 영양소 표시 영역
    const modalKcal = document.getElementById("modalKcal");
    const modalCarb = document.getElementById("modalCarb");
    const modalSugars = document.getElementById("modalSugars");
    const modalSugarAlcohol = document.getElementById("modalSugarAlcohol");
    const modalFiber = document.getElementById("modalFiber");
    const modalProtein = document.getElementById("modalProtein");
    const modalFat = document.getElementById("modalFat");
    const modalSatFat = document.getElementById("modalSatFat");
    const modalTransFat = document.getElementById("modalTransFat");
    const modalCholesterol = document.getElementById("modalCholesterol");
    const modalUnsatFat = document.getElementById("modalUnsatFat");
    const modalSodium = document.getElementById("modalSodium");
    const modalCaffeine = document.getElementById("modalCaffeine");
    const modalCalcium = document.getElementById("modalCalcium");

    // ✅ 즐겨찾기 관리
    const favorites = new Set();

    // ✅ "선택" 버튼 클릭 시 모달 데이터 세팅
    document.querySelectorAll('[data-bs-target="#nutritionModal"]').forEach(btn => {
        btn.addEventListener("click", () => {
            const foodId = btn.dataset.foodId;
            const foodName = btn.dataset.foodName;

            modalFoodName.textContent = foodName;
            modalFoodId.value = foodId;
            modalMealTime.value = mealSelect.value;

            modalKcal.textContent = btn.dataset.calories || 0;
            modalCarb.textContent = btn.dataset.carb || 0;
            modalSugars.textContent = btn.dataset.sugars || 0;
            modalSugarAlcohol.textContent = btn.dataset.sugarAlcohol || 0;
            modalFiber.textContent = btn.dataset.fiber || 0;
            modalProtein.textContent = btn.dataset.protein || 0;
            modalFat.textContent = btn.dataset.fat || 0;
            modalSatFat.textContent = btn.dataset.satFat || 0;
            modalTransFat.textContent = btn.dataset.transFat || 0;
            modalCholesterol.textContent = btn.dataset.cholesterol || 0;
            modalUnsatFat.textContent = btn.dataset.unsatFat || 0;
            modalSodium.textContent = btn.dataset.sodium || 0;
            modalCaffeine.textContent = btn.dataset.caffeine || 0;
            modalCalcium.textContent = btn.dataset.calcium || 0;

            // ✅ ratio 계산용으로 원본 데이터 저장
            modalGramInput.dataset.baseCalories = btn.dataset.calories || 0;
            modalGramInput.dataset.baseCarb = btn.dataset.carb || 0;
            modalGramInput.dataset.baseProtein = btn.dataset.protein || 0;
            modalGramInput.dataset.baseFat = btn.dataset.fat || 0;
        });
    });

    // ✅ 즐겨찾기 버튼 토글
    const favoriteBtn = document.getElementById("modalFavoriteBtn");
    favoriteBtn.addEventListener("click", () => {
        const foodId = modalFoodId.value;
        if (!foodId) return;

        if (favorites.has(foodId)) {
            favorites.delete(foodId);
            favoriteBtn.classList.remove("btn-warning");
            favoriteBtn.classList.add("btn-outline-warning");
        } else {
            favorites.add(foodId);
            favoriteBtn.classList.add("btn-warning");
            favoriteBtn.classList.remove("btn-outline-warning");
        }
    });

    // ✅ 섭취량 변경 시 동적 계산 (비율 기반)
    modalGramInput.addEventListener("input", () => {
        const gram = parseFloat(modalGramInput.value);
        const base = 100;
        const ratio = gram / base;

        modalKcal.textContent = (modalGramInput.dataset.baseCalories * ratio).toFixed(1);
        modalCarb.textContent = (modalGramInput.dataset.baseCarb * ratio).toFixed(1);
        modalProtein.textContent = (modalGramInput.dataset.baseProtein * ratio).toFixed(1);
        modalFat.textContent = (modalGramInput.dataset.baseFat * ratio).toFixed(1);
    });
});
