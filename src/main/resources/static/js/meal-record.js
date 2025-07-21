document.addEventListener('DOMContentLoaded', () => {
    const allFoods = [
        {
            id: 1,
            name: "찐고구마",
            serving: "1개 (170g)",
            kcal: 235,
            carb: 53,
            sugars: 10,
            sugarAlcohol: 0,
            fiber: 3.1,
            protein: 2.3,
            fat: 0.4,
            satFat: 0.1,
            transFat: 0,
            cholesterol: 0,
            unsatFat: 0.3,
            sodium: 1,
            caffeine: 0,
            alcohol: 0,
            calcium: 35,
            baseGram: 170
        },
        {
            id: 2,
            name: "군고구마",
            serving: "1개 (170g)",
            kcal: 319,
            carb: 73,
            sugars: 20,
            sugarAlcohol: 0,
            fiber: 3.1,
            protein: 2.3,
            fat: 0.5,
            satFat: 0.1,
            transFat: 0,
            cholesterol: 0,
            unsatFat: 0.4,
            sodium: 1,
            caffeine: 0,
            alcohol: 0,
            calcium: 35,
            baseGram: 170
        }
    ];

    const foodData = {
        all: allFoods,
        frequent: [allFoods[0]],
        favorite: [],
        manual: []
    };

    const favorites = new Set(foodData.favorite.map(f => f.id));

    const resultContainers = {
        all: document.getElementById("pane-all"),
        frequent: document.getElementById("pane-frequent"),
        favorite: document.getElementById("pane-favorite"),
        manual: document.getElementById("pane-manual")
    };

    const searchInput = document.getElementById("searchInput");
    const clearBtn = document.getElementById("clearBtn");
    const mealSelect = document.getElementById("mealSelect");
    const saveBtn = document.getElementById("saveBtn");

    let selectedFoods = new Map();
    let currentTab = 'all';

    document.getElementById("tabMenu").addEventListener("click", e => {
        if (e.target.tagName === "BUTTON") {
            currentTab = e.target.id.replace("tab-", "");
            renderResults();
            updateSaveBtn();
        }
    });

    searchInput.addEventListener("input", renderResults);
    clearBtn.addEventListener("click", () => {
        searchInput.value = "";
        renderResults();
        searchInput.focus();
    });

    mealSelect.addEventListener("change", updateSaveBtn);

    saveBtn.addEventListener("click", () => {
        if (saveBtn.disabled) return;
        const meal = mealSelect.value;
        const items = Array.from(selectedFoods.values())
            .map(({ food, amount }) =>
                `${food.name} (${amount}${food.unit || 'g'})`
            ).join(', ');
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
                const starClass = isFavorite
                    ? 'bi-star-fill text-warning'
                    : 'bi-star text-light';

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

    function toggleFavorite(foodId) {
        if (favorites.has(foodId)) {
            favorites.delete(foodId);
        } else {
            favorites.add(foodId);
        }

        foodData.favorite = [];
        favorites.forEach(id => {
            const f = findFoodById(id);
            if (f) {
                foodData.favorite.push(f);
            }
        });

        if (currentTab === 'favorite') {
            renderResults();
        }
    }

    function findFoodById(id) {
        for (const tab in foodData) {
            const f = foodData[tab].find(x => x.id === id);
            if (f) return f;
        }
        return null;
    }

    function showNutritionModal(foodId) {
        let stored;
        let food;

        const original = findFoodById(foodId);
        food = JSON.parse(JSON.stringify(original));
        stored = { food, amount: food.baseGram || 100 };

        const modalEl = document.getElementById('nutritionModal');
        const modal = new bootstrap.Modal(modalEl);

        document.getElementById('modalFoodName').textContent = food.name;

        const gramInput = document.getElementById('modalGramInput');
        gramInput.value = stored.amount;

        updateModalNutrition(food, stored.amount);

        modalEl.addEventListener('shown.bs.modal', () => {
            gramInput.focus();
            gramInput.select();
        }, { once: true });

        gramInput.oninput = () => {
            const gram = parseInt(gramInput.value, 10) || (food.baseGram || 100);
            stored.amount = gram;
            updateModalNutrition(food, gram);
        };

        gramInput.addEventListener('keydown', e => {
            if (e.key === 'Enter') {
                e.preventDefault();
                document.getElementById('modalConfirmBtn').click();
            }
        });

        document.getElementById('modalConfirmBtn').onclick = () => {
            const gram = parseInt(gramInput.value, 10) || (food.baseGram || 100);
            stored.amount = gram;

            selectedFoods.set(foodId, stored);

            modal.hide();
            renderResults();
            updateSaveBtn();
        };

        modal.show();
    }

    function updateModalNutrition(food, gram) {
        const ratio = gram / (food.baseGram || 100);

        document.getElementById('modalKcal').textContent = (food.kcal * ratio).toFixed(1);
        document.getElementById('modalCarb').textContent = ((food.carb || 0) * ratio).toFixed(1);
        document.getElementById('modalSugars').textContent = ((food.sugars || 0) * ratio).toFixed(1);
        document.getElementById('modalSugarAlcohol').textContent = ((food.sugarAlcohol || 0) * ratio).toFixed(1);
        document.getElementById('modalFiber').textContent = ((food.fiber || 0) * ratio).toFixed(1);
        document.getElementById('modalProtein').textContent = ((food.protein || 0) * ratio).toFixed(1);
        document.getElementById('modalFat').textContent = ((food.fat || 0) * ratio).toFixed(1);
        document.getElementById('modalSatFat').textContent = ((food.satFat || 0) * ratio).toFixed(1);
        document.getElementById('modalTransFat').textContent = ((food.transFat || 0) * ratio).toFixed(1);
        document.getElementById('modalCholesterol').textContent = ((food.cholesterol || 0) * ratio).toFixed(1);
        document.getElementById('modalUnsatFat').textContent = ((food.unsatFat || 0) * ratio).toFixed(1);
        document.getElementById('modalSodium').textContent = ((food.sodium || 0) * ratio).toFixed(1);
        document.getElementById('modalCaffeine').textContent = ((food.caffeine || 0) * ratio).toFixed(1);
        document.getElementById('modalAlcohol').textContent = ((food.alcohol || 0) * ratio).toFixed(1);
        document.getElementById('modalCalcium').textContent = ((food.calcium || 0) * ratio).toFixed(1);
    }

    function addFoodDirectly(foodId) {
        const original = findFoodById(foodId);
        const clonedFood = JSON.parse(JSON.stringify(original));
        const defaultGram = clonedFood.baseGram || 100;

        selectedFoods.set(foodId, {
            food: clonedFood,
            amount: defaultGram
        });
        renderResults();
        updateSaveBtn();
    }

    function updateSaveBtn() {
        const count = selectedFoods.size;
        const meal = mealSelect.value;
        saveBtn.disabled = (count === 0 || !meal);
        saveBtn.textContent = count === 0 ? "기록하기" : `${count}개 기록하기`;
    }

    document.getElementById('openManualModalBtn').addEventListener('click', () => {
        const modalEl = document.getElementById('manualFoodModal');
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    });

    document.getElementById('saveManualFoodBtn').addEventListener('click', () => {
        const name = document.getElementById('manualName').value.trim();
        const amount = parseInt(document.getElementById('manualAmount').value);
        const unit = document.getElementById('manualUnit').value;
        const kcal = parseInt(document.getElementById('manualKcal').value);
        const carbs = parseFloat(document.getElementById('manualCarbs').value) || 0;
        const sugars = parseFloat(document.getElementById('manualSugars').value) || 0;
        const sugarAlcohol = parseFloat(document.getElementById('manualSugarAlcohol').value) || 0;
        const fiber = parseFloat(document.getElementById('manualFiber').value) || 0;
        const protein = parseFloat(document.getElementById('manualProtein').value) || 0;
        const fat = parseFloat(document.getElementById('manualFat').value) || 0;
        const satFat = parseFloat(document.getElementById('manualSatFat').value) || 0;
        const transFat = parseFloat(document.getElementById('manualTransFat').value) || 0;
        const cholesterol = parseFloat(document.getElementById('manualCholesterol').value) || 0;
        const unsatFat = parseFloat(document.getElementById('manualUnsatFat').value) || 0;
        const sodium = parseFloat(document.getElementById('manualSodium').value) || 0;
        const caffeine = parseFloat(document.getElementById('manualCaffeine').value) || 0;
        const alcohol = parseFloat(document.getElementById('manualAlcohol').value) || 0;
        const calcium = parseFloat(document.getElementById('manualCalcium').value) || 0;

        if (!name || !amount || !kcal) {
            alert('필수 항목을 모두 입력하세요.');
            return;
        }

        const newFood = {
            id: Date.now(),
            name,
            serving: `${amount}${unit}`,
            kcal,
            unit,
            baseGram: amount,
            carb: carbs,
            sugars,
            sugarAlcohol,
            fiber,
            protein,
            fat,
            satFat,
            transFat,
            cholesterol,
            unsatFat,
            sodium,
            caffeine,
            alcohol,
            calcium
        };

        const clonedFood = JSON.parse(JSON.stringify(newFood));

        foodData.manual.unshift(newFood);

        selectedFoods.set(newFood.id, {
            food: clonedFood,
            amount: amount
        });

        renderResults();
        updateSaveBtn();

        [
            'manualBrand', 'manualName', 'manualAmount', 'manualUnit', 'manualKcal',
            'manualCarbs', 'manualSugars', 'manualSugarAlcohol', 'manualFiber',
            'manualProtein', 'manualFat', 'manualSatFat', 'manualTransFat',
            'manualCholesterol', 'manualUnsatFat', 'manualSodium', 'manualCaffeine',
            'manualAlcohol', 'manualCalcium'
        ].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.value = (id === 'manualUnit') ? 'g' : '';
        });

        const modalEl = document.getElementById('manualFoodModal');
        const modal = bootstrap.Modal.getInstance(modalEl);
        modal.hide();
    });

    renderResults();
    updateSaveBtn();
});
