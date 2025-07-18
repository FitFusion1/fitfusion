let map = null; // 카카오 맵 객체
let userLat, userLon; // 현재 사용자의 위도, 경도
let openOverlay = null; // 열려 있는 오버레이(말풍선) 객체, 하나만 열기 위해 사용
let currentPositionMarker = null; // 현재 위치를 표시하는 마커 객체
let gymMarkers = []; // 지도 위의 헬스장 마커들을 저장하는 배열(초기화 시 사용)


window.onload = function () {
    const mapContainer = document.getElementById('map');

    function initMap(lat, lon) {
        const position = new kakao.maps.LatLng(lat, lon);
        console.log('position :' + position);
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
                        { offset: new kakao.maps.Point(13, 42)
                        }
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

            const ps = new kakao.maps.services.Places(); //  생성자 함수 장소 검색을 지원받는 api을 받는다
            // 생성자 함수로 만들어진 ps로 ketwordSerarch 메소드를 실행할 수 있다
            // 이 기능은 입력한 키워드로 장소를 검색
            ps.keywordSearch(keyword, function (data, status) {
                console.log('data2:', data , status);
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

            // encodeURIComponent() : 특수 문자(공백, 한글 등)를 URL에서 안전하게 인코딩하기 위해서
            // ex) /api/kakao/gyms?keyword=%EB%A7%8C%EC%88%98%EC%97%AD&lat=37.45729&lon=126.72929 이런식으로 전달되어서
            const url = "/api/kakao/gyms?lat=" + encodeURIComponent(userLat) + "&lon=" + encodeURIComponent(userLon);

            fetch(url)
                .then(res => res.json()) // ① 응답을 JSON으로 파싱
                .then(data => {                      // ② 파싱된 데이터를 처리 시작
                    if (!Array.isArray(data)) {     // ③ 데이터가 배열인지 확인
                        alert("헬스장 정보를 불러오지 못했습니다.");
                        return;
                    }

                    // ④ 여기에 헬스장 마커 찍는 로직이 들어감
                    data.forEach(place => {
                        console.log('data :', data);
                        const name = place.place_name;
                        const x = place.x;
                        const y = place.y;
                        const kakaoPlaceId = name + "_" + x + "_" + y;

                        const marker = new kakao.maps.Marker({
                            map: map,
                            position: new kakao.maps.LatLng(y, x),
                            image: new kakao.maps.MarkerImage(
                                "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png",
                                new kakao.maps.Size(40, 42),
                                { offset: new kakao.maps.Point(13, 42) }
                            )
                        });

                        gymMarkers.push(marker);

                        kakao.maps.event.addListener(marker, 'click', function () {
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
                                    savedGym.averageRating = savedGym.averageRating ?? 0.0;
                                    console.log('savedGym :', savedGym);
                                    if (!savedGym.gymId) return;

                                    if (openOverlay) openOverlay.setMap(null);

                                    console.log('gymData :', gymData);
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

                                    const rating = savedGym.averageRating || 0.0;
                                    const ratingEl = document.createElement('div');
                                    ratingEl.innerHTML = `⭐ <strong>${rating.toFixed(1)}</strong> / 5.0`;
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

                                    const compareBtn = document.createElement('button');
                                    compareBtn.innerText = '비교하기';
                                    compareBtn.style.marginTop = '5px';
                                    compareBtn.style.padding = '4px 8px';
                                    compareBtn.style.border = '1px solid #333';
                                    compareBtn.style.borderRadius = '4px';
                                    compareBtn.style.background = '#f0f0f0';
                                    compareBtn.style.cursor = 'pointer';

                                    compareBtn.onclick = () => {

                                    };

                                    content.appendChild(document.createElement('br')); // 줄바꿈
                                    content.appendChild(compareBtn);

                                    const overlay = new kakao.maps.CustomOverlay({
                                        map: map,
                                        position: new kakao.maps.LatLng(savedGym.latitude, savedGym.longitude),
                                        content: content,
                                        yAnchor: 1
                                    });
                                    console.log("savedGym.lat/lon:", savedGym.latitude, savedGym.longitude);
                                    overlay.setMap(map);
                                    console.log('오버레이 출력됨');
                                    openOverlay = overlay;

                                });
                        });
                    });
                });
        }
    };
