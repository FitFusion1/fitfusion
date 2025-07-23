// 전역 변수
const foodData = {
    all: [],
    frequent: [],
    favorite: [],
    manual: []
};

const favorites = new Set();
let selectedFoods = new Map();
let currentTab = 'all';

// 영양정보 모달 표시 함수
function showNutritionModal(foodId) {
    const food =
        foodData.all.find(f => f.id === foodId) ||
        foodData.frequent.find(f => f.id === foodId) ||
        foodData.favorite.find(f => f.id === foodId) ||
        foodData.manual.find(f => f.id === foodId);

    if (!food) return;

    document.getElementById('modalKcal').textContent = food.calories ?? 0;
    document.getElementById('modalCarb').textContent = food.carbohydrateG ?? 0;
    document.getElementById('modalSugars').textContent = food.sugarG ?? 0;
    document.getElementById('modalSugarAlcohol').textContent = food.sugarAlcoholG ?? 0;
    document.getElementById('modalFiber').textContent = food.fiberG ?? 0;
    document.getElementById('modalProtein').textContent = food.proteinG ?? 0;
    document.getElementById('modalFat').textContent = food.fatG ?? 0;
    document.getElementById('modalSatFat').textContent = food.saturatedFatG ?? 0;
    document.getElementById('modalTransFat').textContent = food.transFatG ?? 0;
    document.getElementById('modalCholesterol').textContent = food.cholesterolMg ?? 0;
    document.getElementById('modalUnsatFat').textContent = food.unsaturatedFatG ?? 0;
    document.getElementById('modalSodium').textContent = food.sodiumMg ?? 0;
    document.getElementById('modalCaffeine').textContent = food.caffeineMg ?? 0;
    document.getElementById('modalCalcium').textContent = food.calciumMg ?? 0;

    const modalEl = document.getElementById('nutritionModal');
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
}

document.addEventListener('DOMContentLoaded', () => {
    const resultContainers = {
        all: document.getElementById("pane-all"),
        frequent: document.getElementById("pane-frequent"),
        favorite: document.getElementById("pane-favorite"),
        manual: document.getElementById("pane-manual")
    };

    const searchInput = document.getElementById("searchInput");
    const searchBtn = document.getElementById("searchBtn");
    const mealSelect = document.getElementById("mealSelect");
    const saveBtn = document.getElementById("saveBtn");

    document.getElementById("tabMenu").addEventListener("click", e => {
        if (e.target.tagName === "BUTTON") {
            currentTab = e.target.id.replace("tab-", "");
            renderResults();
            updateSaveBtn();
        }
    });

    async function fetchAndRender(keyword) {
        if (keyword === "") {
            foodData.all = [];
            renderResults();
            return;
        }
        try {
            const response = await fetch(`/api/foods/search?keyword=${encodeURIComponent(keyword)}`);
            if (!response.ok) throw new Error("검색 실패");

            const result = await response.json();
            foodData.all = result.map(dto => ({
                id: dto.foodId,
                name: dto.foodName || dto.FOOD_NM_KR,
                serving: dto.foodServingSizeRaw || '100g',
                kcal: dto.calories || 0,
                carb: dto.carbohydrateG || 0,
                sugars: dto.sugarG || 0,
                sugarAlcohol: dto.sugarAlcoholG || 0,
                fiber: dto.fiberG || 0,
                protein: dto.proteinG || 0,
                fat: dto.fatG || 0,
                satFat: dto.saturatedFatG || 0,
                transFat: dto.transFatG || 0,
                cholesterol: dto.cholesterolMg || 0,
                unsatFat: dto.unsaturatedFatG || 0,
                sodium: dto.sodiumMg || 0,
                caffeine: dto.caffeineMg || 0,
                alcohol: 0,
                calcium: dto.calciumMg || 0,
                baseGram: dto.foodWeightValue || 100,
                unit: 'g'
            }));

            favorites.clear();
            result.forEach(dto => {
                if (dto.isFavorite) favorites.add(dto.foodId);
            });

            renderResults();
        } catch (error) {
            console.error(" 검색 오류:", error);
        }
    }

    searchInput.addEventListener("input", () => {
        fetchAndRender(searchInput.value.trim());
    });

    searchBtn.addEventListener("click", () => {
        fetchAndRender(searchInput.value.trim());
    });

    document.getElementById('openManualModalBtn')?.addEventListener('click', () => {
        const modalEl = document.getElementById('manualFoodModal');
        if (modalEl) {
            const modal = new bootstrap.Modal(modalEl);
            modal.show();
        } else {
            console.warn(' manualFoodModal ID를 가진 요소가 없습니다.');
        }
    });

    mealSelect.addEventListener("change", updateSaveBtn);

    saveBtn.addEventListener("click", () => {
        if (saveBtn.disabled) return;
        const meal = mealSelect.value;
        const items = Array.from(selectedFoods.values())
            .map(({ food, amount }) => `${food.name} (${amount}${food.unit || 'g'})`)
            .join(', ');
        alert(`${meal}에 ${selectedFoods.size}개 음식 기록:\n${items}`);
        selectedFoods.clear();
        renderResults();
        updateSaveBtn();
    });

    function renderResults() {
        Object.keys(resultContainers).forEach(tab => {
            const pane = resultContainers[tab];
            const ulId = `resultList-${tab}`;
            const ul = document.getElementById(ulId);
            if (!ul) return;

            ul.innerHTML = '';
            ul.style.display = 'none';

            if (tab !== currentTab) {
                pane.style.display = 'none';
                return;
            }

            const keyword = searchInput.value.trim();
            const filtered = foodData[tab].filter(f => f.name.includes(keyword));

            if (filtered.length === 0) {
                pane.style.display = '';
                return;
            }

            pane.style.display = '';
            ul.style.display = '';

            filtered.forEach(originalFood => {
                let food, amount;
                if (selectedFoods.has(originalFood.id)) {
                    const stored = selectedFoods.get(originalFood.id);
                    food = stored.food;
                    amount = stored.amount;
                } else {
                    food = originalFood;
                    amount = null;
                }

                const li = document.createElement('li');
                li.className = "meal-record-list-item d-flex justify-content-between align-items-center meal-record-clickable-area";
                li.setAttribute("data-food-id", food.id);

                const isFavorite = favorites.has(food.id);
                const starClass = isFavorite ? 'bi-star-fill text-warning' : 'bi-star text-light';

                let innerHTML = `
                    <div class="flex-grow-1">
                        <div class="d-flex align-items-center gap-2">
                            <i class="bi ${starClass} meal-record-favorite-icon" style="cursor:pointer;" data-food-id="${food.id}"></i>
                            <strong>${food.name}</strong>
                        </div>
                        <small class="text-secondary">${food.serving} / ${food.kcal}kcal</small>
                    </div>
                `;

                if (amount !== null) {
                    innerHTML += `
                        <div class="d-flex align-items-center gap-2">
                            <span>${amount}${food.unit || 'g'}</span>
                            <button class="btn btn-sm btn-danger meal-record-remove-btn" type="button">✕</button>
                        </div>
                    `;
                } else {
                    innerHTML += `
                        <div class="d-flex align-items-center gap-2">
                            <button class="btn btn-sm btn-outline-primary meal-record-add-btn" type="button">+</button>
                        </div>
                    `;
                }

                li.innerHTML = innerHTML;

                li.querySelector('.meal-record-favorite-icon').addEventListener('click', (ev) => {
                    ev.stopPropagation();
                    toggleFavorite(food.id);
                    renderResults();
                });

                const addBtn = li.querySelector('.meal-record-add-btn');
                if (addBtn) {
                    addBtn.addEventListener('click', (ev) => {
                        ev.stopPropagation();
                        addFoodDirectly(food.id);
                    });
                }

                const removeBtn = li.querySelector('.meal-record-remove-btn');
                if (removeBtn) {
                    removeBtn.addEventListener('click', (ev) => {
                        ev.stopPropagation();
                        selectedFoods.delete(food.id);
                        renderResults();
                        updateSaveBtn();
                    });
                }

                li.addEventListener('click', () => {
                    showNutritionModal(food.id);
                });

                ul.appendChild(li);
            });
        });
    }

    function updateSaveBtn() {
        const count = selectedFoods.size;
        const meal = mealSelect.value;
        saveBtn.disabled = (count === 0 || !meal);
        saveBtn.textContent = count === 0 ? "기록하기" : `${count}개 기록하기`;
    }

    renderResults();
    updateSaveBtn();
});
