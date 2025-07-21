let map = null; // 카카오 맵 객체
let userLat, userLon; // 현재 사용자의 위도, 경도
let openOverlay = null; // 열려 있는 오버레이 객체
let currentPositionMarker = null; // 현재 위치 마커
let gymMarkers = []; // 헬스장 마커 저장 배열
let selectedGyms = []; // ✅ 전역에서 선택한 헬스장들 저장
const loginUserId = document.getElementById("loginUserId").value;
console.log("loginUserId : " + loginUserId);


window.onload = function () {
    const mapContainer = document.getElementById('map');

    function initMap(lat, lon) {
        const position = new kakao.maps.LatLng(lat, lon);

        if (!map) {
            map = new kakao.maps.Map(mapContainer, {
                center: position,
                level: 4
            });
        } else {
            map.setCenter(position);
        }

        if (currentPositionMarker) currentPositionMarker.setMap(null);

        currentPositionMarker = new kakao.maps.Marker({
            map: map,
            position: position,
            image: new kakao.maps.MarkerImage(
                "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png",
                new kakao.maps.Size(40, 42),
                { offset: new kakao.maps.Point(13, 42) }
            )
        });

        userLat = lat;
        userLon = lon;

        searchGyms("헬스장");
    }

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            pos => initMap(pos.coords.latitude, pos.coords.longitude),
            err => {
                alert("위치 정보를 가져오지 못했습니다. 직접 위치를 입력해주세요.");
                console.error("위치 오류:", err);
            }
        );
    } else {
        alert("브라우저가 위치 정보를 지원하지 않습니다.");
    }

    document.getElementById("set-location-btn").addEventListener("click", function () {
        const keyword = document.getElementById("location-input").value.trim();
        if (!keyword) {
            alert("위치를 입력해주세요.");
            return;
        }

        const ps = new kakao.maps.services.Places();
        ps.keywordSearch(keyword, function (data, status) {
            if (status === kakao.maps.services.Status.OK && data.length > 0) {
                initMap(parseFloat(data[0].y), parseFloat(data[0].x));
            } else {
                alert("입력한 위치를 찾을 수 없습니다.");
            }
        });
    });

    function searchGyms(query) {
        gymMarkers.forEach(m => m.setMap(null));
        gymMarkers = [];

        const url = "/api/kakao/gyms?lat=" + encodeURIComponent(userLat) + "&lon=" + encodeURIComponent(userLon);

        fetch(url)
            .then(res => res.json())
            .then(data => {
                if (!Array.isArray(data)) {
                    alert("헬스장 정보를 불러오지 못했습니다.");
                    return;
                }

                data.forEach(place => {
                    const name = place.place_name;
                    const x = place.x;
                    const y = place.y;
                    const kakaoPlaceId = name + "_" + x + "_" + y;

                    // ✅ 커스텀 div 마커 생성
                    const marker = document.createElement('div');
                    marker.className = 'gym-label';
                    marker.innerText = '헬스장';

                    // ✅ 클릭 이벤트 등록
                    marker.addEventListener('click', function () {
                        const gymData = {
                            name: name,
                            address: place.road_address_name || place.address_name || '',
                            phone: place.phone || '',
                            latitude: parseFloat(y),
                            longitude: parseFloat(x),
                            kakaoPlaceId: kakaoPlaceId
                        };

                        fetch('/api/gyms', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(gymData)
                        })
                            .then(res => res.json())
                            .then(savedGym => {
                                if (!savedGym.gymId) return;
                                savedGym.averageRating = savedGym.averageRating ?? 0.0;

                                if (openOverlay) openOverlay.setMap(null);

                                const content = document.createElement('div');
                                content.style.padding = '10px';
                                content.style.fontSize = '14px';
                                content.style.background = 'white';
                                content.style.border = '1px solid #ccc';
                                content.style.borderRadius = '10px';
                                content.style.position = 'relative';

                                const closeBtn = document.createElement('div');
                                closeBtn.innerText = '✖';
                                closeBtn.style.position = 'absolute';
                                closeBtn.style.top = '5px';
                                closeBtn.style.right = '8px';
                                closeBtn.style.cursor = 'pointer';
                                closeBtn.onclick = () => {
                                    if (openOverlay) openOverlay.setMap(null);
                                };
                                content.appendChild(closeBtn);

                                const ratingEl = document.createElement('div');
                                ratingEl.innerHTML = `⭐ <strong>${savedGym.averageRating.toFixed(1)}</strong> / 5.0`;
                                ratingEl.style.margin = '5px 0';
                                ratingEl.style.color = '#f5a623';
                                ratingEl.style.fontWeight = 'bold';
                                content.appendChild(ratingEl);

                                const nameEl = document.createElement('strong');
                                nameEl.innerText = savedGym.name;
                                content.appendChild(nameEl);
                                content.appendChild(document.createElement('br'));

                                const link = document.createElement('a');
                                link.href = '/gyms/detail/' + savedGym.gymId;
                                link.innerText = '상세정보 보기';
                                link.style.color = 'blue';
                                content.appendChild(link);


                                if (loginUserId != null && loginUserId !== "") {
                                    const compareBtn = document.createElement('a');
                                    compareBtn.innerText = '비교하기';
                                    compareBtn.style.marginTop = '5px';
                                    compareBtn.style.padding = '4px 8px';
                                    compareBtn.style.border = '1px solid #333';
                                    compareBtn.style.borderRadius = '4px';
                                    compareBtn.style.background = '#f0f0f0';
                                    compareBtn.style.cursor = 'pointer';

                                    compareBtn.addEventListener('click', function (e) {
                                        e.preventDefault();

                                        if (!selectedGyms.includes(savedGym.gymId)) {
                                            selectedGyms.push(savedGym.gymId);
                                            alert(`'${savedGym.name}' 헬스장이 비교 목록에 추가되었습니다.`);
                                        } else {
                                            alert("이미 비교 목록에 있는 헬스장입니다.");
                                        }
                                    });

                                    content.appendChild(document.createElement('br'));
                                    content.appendChild(compareBtn);
                                }

                                const overlay = new kakao.maps.CustomOverlay({
                                    map: map,
                                    position: new kakao.maps.LatLng(savedGym.latitude, savedGym.longitude),
                                    content: content,
                                    yAnchor: 1
                                });

                                overlay.setMap(map);
                                openOverlay = overlay;
                            });
                    });

                    // ✅ 커스텀 오버레이로 마커로 표시
                    const markerOverlay = new kakao.maps.CustomOverlay({
                        position: new kakao.maps.LatLng(y, x),
                        content: marker,
                        yAnchor: 1
                    });

                    markerOverlay.setMap(map);
                    gymMarkers.push(markerOverlay);
                });
            });
    }
};

// ✅ compare-final-btn 이벤트 리스너는 딱 한 번만 여기서 등록!
document.addEventListener('DOMContentLoaded', function () {
    const compareBtn = document.getElementById('compare-final-btn');
    if (compareBtn) {
        compareBtn.addEventListener('click', function () {
            if (selectedGyms.length === 0) {
                alert("비교할 헬스장을 먼저 선택해주세요.");
                return;
            }

            const query = selectedGyms.map(id => `gymId=${id}`).join('&');
            window.location.href = `/gyms/compare?${query}`;
        });
    }
});
