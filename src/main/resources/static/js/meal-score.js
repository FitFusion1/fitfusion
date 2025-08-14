// document.addEventListener('DOMContentLoaded', function() {
//     const btnCurrentDate = document.getElementById('btn-current-date');
//     const mealScoreModalEl = document.getElementById('mealScoreModal');
//     const mealScoreModal = new bootstrap.Modal(mealScoreModalEl);
//     const confirmBtn = mealScoreModalEl.querySelector('.confirm-btn');
//
//     btnCurrentDate.addEventListener('click', function() {
//         mealScoreModal.show();
//     });
//
//     confirmBtn.addEventListener('click', function() {
//         // 모달 닫고 영양소 페이지로 이동
//         mealScoreModal.hide();
//
//         // 날짜 쿼리스트링 넘길 수도 있음 (필요시)
//         const date = btnCurrentDate.getAttribute('data-date') || '';
//         window.location.href = `/meal/daily-nutrient?date=${encodeURIComponent(date)}`;
//     });
// });