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
                    "https://cdn-icons-png.flaticon.com/512/2776/2776067.png",
                    new kakao.maps.Size(40, 42),
                    { offset: new kakao.maps.Point(13, 42) }
                )
            });
        }, error => {
            console.error("현재 위치를 가져올 수 없습니다.");
        });
    }

    const toggleRoadviewBtn = document.getElementById("roadview-btn");
    const roadviewContainer = document.getElementById('roadview');
    const wrapper = document.getElementById('roadview-wrapper');
    const roadview = new kakao.maps.Roadview(roadviewContainer);
    const roadviewClient = new kakao.maps.RoadviewClient();

    toggleRoadviewBtn.addEventListener("click", function () {
        if (wrapper.style.display === 'block') {
            // 이미 열려있다면 -> 닫기
            wrapper.style.display = 'none';
        } else {
            // 안 보이는 상태면 -> 보이게 하고 로드뷰 띄우기
            wrapper.style.display = 'block';

            setTimeout(() => {
                roadviewClient.getNearestPanoId(gymPosition, 100, function(panoId) {
                    if (panoId) {
                        console.log("panoId:", panoId);
                        roadview.setPanoId(panoId, gymPosition);
                    } else {
                        roadviewContainer.innerHTML = "로드뷰를 지원하지 않는 위치입니다.";
                    }
                });
            }, 100); // 렌더링 타이밍 확보
        }
    });

    const currentLocationBtn = document.getElementById("current-location-btn");
    currentLocationBtn.addEventListener('click', function () {

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(pos => {
                const myLat = pos.coords.latitude;
                const mylon = pos.coords.longitude;
                const myPosition = new kakao.maps.LatLng(myLat, mylon);

                map.setCenter(myPosition);
            });
        }
    })

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

const likeBtns = document.getElementById("like-btns");

if (likeBtns) {
    likeBtns.addEventListener('click', function () {
        let gymId = document.getElementById("gymId").value;

        if (likeBtns.classList.contains('liked')) {
            fetch(`/gym/unlike/${gymId}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': token
                }
            })
                .then(res => {
                    if (!res.ok) {
                        alert("삭제 실패");
                    } else {
                        likeBtns.innerText = "🤍 찜하기";
                        likeBtns.classList.remove('liked');
                        likeBtns.style.backgroundColor = "";
                        likeBtns.style.color = "";
                        alert("찜 취소 완료");
                    }
                })
                .catch(error => {
                    alert("오류 발생: " + error.message);
                });
        }
        else {
            fetch(`/gym/like/${gymId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': token
                },
            })
                .then(res => {
                    if (res.status === 409) {
                        // 이미 찜한 경우
                        return res.text().then(msg => {
                            alert(msg); // "이미 찜한 헬스장입니다."
                        });
                    } else if (!res.ok) {
                        throw new Error('서버 오류 발생');
                    } else {
                        // 찜 성공
                        likeBtns.innerText = "❤️ 찜 완료";
                        likeBtns.style.backgroundColor = "#ffebee";
                        likeBtns.style.color = "#e91e63";
                        alert("찜 완료");
                        return res.json();
                    }
                })
                .catch(error => {
                    alert("오류 발생: " + error.message);

                });
        }
    });
}

let page = 1;
const gymId = document.getElementById("gymId").value;
const loadMoreBtn = document.getElementById("load-more-btn");
const commentContainer = document.querySelector(".review-list");
// HTML의 meta 태그에서 로그인 사용자 ID를 가져옴
const loginUserId = document.querySelector('meta[name="login-user-id"]')?.getAttribute('content');
console.log("로그인 유저 ID:", loginUserId);

function renderComments(newComments) {
    newComments.forEach(review => {
        const li = document.createElement("li");

        function formatDate(dateStr) {
            const date = new Date(dateStr);
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, "0");
            const day = String(date.getDate()).padStart(2, "0");
            return `${year}-${month}-${day}`;
        }



        let buttonHtml = "";
        if (loginUserId === String(review.userId)) {
            buttonHtml = `
              <button class="edit-button" data-comment-id="${review.reviewId}">수정</button>
              <button class="delete-button" data-comment-id="${review.reviewId}">삭제</button>
            `;
        }

        li.innerHTML = `
  <div class="review-card" id="${review.reviewId}">
    <div class="review-header">
      <div>
        <p class="username">${review.username}</p>
        <p class="stars">${'⭐'.repeat(review.rating)}</p>
      </div>
      <span class="review-date">${formatDate(review.createdDate)}</span>
      ${
            loginUserId === String(review.userId)
                ? `<div class="review-actions">
                <button class="edit-button" data-comment-id="${review.reviewId}">수정</button>
                <button class="delete-button" data-comment-id="${review.reviewId}">삭제</button>
             </div>`
                : ""
        }
    </div>
    <div class="review-content">
      <p>${review.content}</p>
    </div>

    <!-- 수정 폼 (기본 숨김) -->
    <div class="edit-form" style="display: none;">
      <div class="mb-2">
        <label>별점</label>
        <select class="form-select rating-select">
          <option value="1" ${review.rating === 1 ? "selected" : ""}>⭐</option>
          <option value="2" ${review.rating === 2 ? "selected" : ""}>⭐⭐</option>
          <option value="3" ${review.rating === 3 ? "selected" : ""}>⭐⭐⭐</option>
          <option value="4" ${review.rating === 4 ? "selected" : ""}>⭐⭐⭐⭐</option>
          <option value="5" ${review.rating === 5 ? "selected" : ""}>⭐⭐⭐⭐⭐</option>
        </select>
      </div>

      <div class="mb-2">
        <label>리뷰 내용</label>
        <textarea class="form-control content-textarea" rows="3">${review.content}</textarea>
      </div>

      <button class="edit-submit-button btn btn-sm btn-success mt-2">수정 완료</button>
    </div>
  </div>
`;
        commentContainer.appendChild(li);


        const editBtn = li.querySelector(".edit-button");
        if (editBtn) {
            editBtn.addEventListener("click", function () {
                const commentId = editBtn.getAttribute("data-comment-id");
                const card = editBtn.closest(".review-card");
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
        }

        const deleteBtn = li.querySelector(".delete-button");
        if (deleteBtn) {
            deleteBtn.addEventListener("click", function () {
                const commentId = deleteBtn.getAttribute("data-comment-id");
                const card = deleteBtn.closest(".review-card");

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
        }
    });
}


function loadComments() {
    fetch(`/gyms/${gymId}/reviews?page=${page}&size=5`)
        .then(res => {
            return res.json();
        })
        .then(data => {
            if(data.length === 0) {
                loadMoreBtn.style.display = 'none';
            }
            else {
                renderComments(data);
            }
        }).catch(error => {
            console.error('불러오기 실패', error);
    });
}

loadMoreBtn.addEventListener('click', function() {
    page++
    if (loginUserId == null) {
        alert("로그인 후 사용")
    }
    loadComments();
})


const copyBtn = document.getElementById("copy-address-btn");

copyBtn.addEventListener("click", function () {
    const addressText = document.getElementById("gym-address").innerText;
    console.log("복사 대상:", addressText);
    console.log("타입:", typeof addressText);

    navigator.clipboard.writeText(addressText)
        .then(res => {
            alert('복사완료');
        })
        .catch(error => {
            alert('복사 실패' + error.message);
        })
});

const naviBtn = document.getElementById("nav-btn");
const latitude = document.getElementById("latitude").value;
const longitude = document.getElementById("longitude").value;
console.log("latitude22:", latitude);
console.log("longitude22:", longitude);

naviBtn.addEventListener("click", function () {
    const name = document.getElementById("gym-name").value;
    const encodedName = encodeURIComponent(name);
    const url = `https://map.kakao.com/link/map/${encodedName},${latitude},${longitude}`;
    console.log("name : " + name);
    window.open(url, "_blank");
})