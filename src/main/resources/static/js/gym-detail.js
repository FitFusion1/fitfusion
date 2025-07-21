let map;
let currentPositionMarker;

window.onload = function () {
    const mapContainer = document.getElementById("map");
    const gymId = document.getElementById("gymId").value;
    const lat = parseFloat(document.getElementById("latitude").value);
    const lon = parseFloat(document.getElementById("longitude").value);

    const gymPosition = new kakao.maps.LatLng(lat, lon);

    map = new kakao.maps.Map(mapContainer, {
        center: gymPosition,
        level: 4
    });

    currentPositionMarker = new kakao.maps.Marker({
        map: map,
        position: gymPosition,
        image: new kakao.maps.MarkerImage(
            "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png",
            new kakao.maps.Size(40, 42),
            { offset: new kakao.maps.Point(13, 42) }
        )
    });

    const overlayContent = `<div class="custom-marker">헬스장</div>`;
    const customOverlay = new kakao.maps.CustomOverlay({
        content: overlayContent,
        position: gymPosition,
        yAnchor: 1.5
    });
    customOverlay.setMap(map);

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(pos => {
            const myLat = pos.coords.latitude;
            const myLon = pos.coords.longitude;
            const myPosition = new kakao.maps.LatLng(myLat, myLon);

            new kakao.maps.Marker({
                map: map,
                position: myPosition,
                image: new kakao.maps.MarkerImage(
                    "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png",
                    new kakao.maps.Size(40, 42),
                    { offset: new kakao.maps.Point(13, 42) }
                )
            });
        }, error => {
            console.error("현재 위치를 가져올 수 없습니다.");
        });
    }
};

document.addEventListener("DOMContentLoaded", function () {
    const reviewBtn = document.getElementById("review-btn");
    const submitBtn = document.getElementById("review-submit-btn");

    const token = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    if (reviewBtn) {
        reviewBtn.addEventListener("click", function () {
            const form = document.getElementById("review-form");
            form.style.display = (form.style.display === "none") ? "block" : "none";
        });
    }

    if (submitBtn) {
        submitBtn.addEventListener("click", function () {
            const gymId = document.getElementById("gymId").value;
            const rating = document.getElementById("rating").value;
            const content = document.getElementById("content").value;

            const data = { gymId, rating, content };

            fetch('/gym/review', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify(data)
            })
                .then(res => {
                    if (!res.ok) throw new Error("리뷰 등록 실패");
                    return res.json();
                })
                .then(data => {
                    alert("등록 완료");
                    location.reload();
                })
                .catch(error => {
                    alert("오류 발생: " + error.message);
                });
        });
    }

    document.querySelectorAll(".edit-button").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const commentId = btn.getAttribute("data-comment-id");
            const card = btn.closest(".review-card");
            const editForm = card.querySelector(".edit-form");

            editForm.style.display = (editForm.style.display === "none" || editForm.style.display === "") ? "block" : "none";

            const submitBtn = editForm.querySelector(".edit-submit-button");
            const ratingSelect = editForm.querySelector(".rating-select");
            const contentTextarea = editForm.querySelector(".content-textarea");

            submitBtn.onclick = () => {
                const rating = ratingSelect.value;
                const content = contentTextarea.value;
                const data = { rating, content };

                fetch(`/gym/review/${commentId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        [header]: token
                    },
                    body: JSON.stringify(data)
                })
                    .then(res => {
                        if (!res.ok) throw new Error('수정 실패');
                        alert('수정 완료');
                        location.reload();
                    })
                    .catch(error => {
                        alert('오류 발생: ' + error.message);
                    });
            };
        });
    });

    document.querySelectorAll(".delete-button").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const commentId = btn.getAttribute("data-comment-id");
            const card = btn.closest(".review-card");

            fetch(`/gym/review/${commentId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': token
                }
            })
                .then(del => {
                    alert('삭제 완료');
                    card.remove();
                    location.reload();
                })
                .catch(error => {
                    alert('오류 발생: ' + error.message);
                });
        });
    });
});
