// src/main/resources/static/js/meal-daily-nutrient.js

document.addEventListener('DOMContentLoaded', function() {
    console.log("meal-daily-nutrient.js 스크립트 실행 시작!");

    // HTML에서 전달받은 전역 변수 window.nutrientSummaryData 사용
    // 데이터가 없는 경우를 대비하여 || {} 로 빈 객체로 초기화합니다.
    var nutrientData = window.nutrientSummaryData || {};
    console.log("HTML에서 전달받은 nutrientSummaryData:", nutrientData);

    // ⭐ 중요: 서버에서 넘어오는 실제 대문자 키 이름(예: CALORIES, CARBOHYDRATEG)을 사용합니다.
    var calories = nutrientData.CALORIES || 0;
    var carbohydrateG = nutrientData.CARBOHYDRATEG || 0;
    var proteinG = nutrientData.PROTEING || 0;
    var fatG = nutrientData.FATG || 0;
    var fiberG = nutrientData.FIBERG || 0;
    var sodiumMg = nutrientData.SODIUMMG || 0;
    var cholesterolMg = nutrientData.CHOLESTEROLMG || 0;

    console.log("추출된 영양소 데이터 (JS 내부):", {
        calories, carbohydrateG, proteinG, fatG, fiberG, sodiumMg, cholesterolMg
    });

    // Chart.js 캔버스 요소 가져오기
    var ctx = document.getElementById('macroChart') ? document.getElementById('macroChart').getContext('2d') : null;
    console.log("Canvas context (ctx):", ctx);

    // 캔버스 요소가 제대로 초기화되지 않았다면 Chart를 만들지 않고 종료합니다.
    if (!ctx) {
        console.error("ID 'macroChart'를 가진 Canvas 요소를 찾을 수 없거나 context를 가져올 수 없습니다. 차트 생성을 중단합니다.");
        // 여기서 DOM 요소를 통해 에러 메시지를 사용자에게 표시할 수도 있습니다.
        return;
    }

    // Chart.js 도넛 차트 생성
    let macroChart = new Chart(ctx, {
        type: 'pie',
        data: {
            // 차트 라벨은 탄수화물, 지방, 단백질로 표시 (총 칼로리 아님)
            labels: ['탄수화물', '지방', '단백질'],
            datasets: [{
                label: '오늘의 주요 영양소 섭취 (g)',
                data: [carbohydrateG, fatG, proteinG], // 각 영양소의 실제 그램 값
                backgroundColor: [
                    'rgba(255, 99, 132, 0.8)', // 탄수화물 색상 (빨강 계열)
                    'rgba(54, 162, 235, 0.8)', // 지방 색상 (파랑 계열)
                    'rgba(75, 192, 192, 0.8)'  // 단백질 색상 (초록 계열)
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(75, 192, 192, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false, // 캔버스 크기에 맞춰 차트가 유동적으로 조절됩니다.
            plugins: {
                title: {
                    display: true, // 제목 표시
                    text: '주요 영양소 섭취 비율', // 차트 제목
                    font: {
                        size: 16
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(tooltipItem) {
                            // 툴팁(마우스 오버 시 뜨는 정보)에 각 영양소의 그램과 비율을 표시합니다.
                            var total = tooltipItem.dataset.data.reduce((acc, val) => acc + val, 0); // 데이터셋의 총 합계
                            var currentValue = tooltipItem.raw; // 현재 항목의 값
                            var percentage = total === 0 ? 0 : parseFloat((currentValue / total * 100).toFixed(1)); // 비율 계산 (총합 0인 경우 방지)
                            return tooltipItem.label + ': ' + currentValue + 'g (' + percentage + '%)';
                        }
                    }
                },
                legend: {
                    position: 'bottom', // 범례 위치
                }
            }
        }
    });

    console.log("Chart.js 차트 생성 완료!");

    console.log("테이블 데이터 바인딩은 HTML에서 직접 nutrientSummary 대문자 키를 참조해야 합니다.");

}); // DOMContentLoaded 끝