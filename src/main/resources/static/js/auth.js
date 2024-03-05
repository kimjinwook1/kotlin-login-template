function callApiWithToken(url) {
    var accessToken = localStorage.getItem('accessToken');
    var refreshToken = localStorage.getItem('refreshToken');

    // 엑세스 토큰이 있는 경우에만 url를 호출
    if (accessToken && refreshToken) {
        axios.get(url, {
            headers: {
                'Authorization': 'Bearer ' + accessToken
            }
        })
            .then(function (response) {
                console.log(response);
            })
            .catch(function (error) {
                console.error("error", error);
                // error.response.status 가 401일 경우에 토큰 갱신을 시도
                if (error.response && error.response.status === 401) {
                    axios.post('/auths/refresh', {
                        accessToken: accessToken,
                        refreshToken: refreshToken
                    })
                        .then(function (response) {
                            // 토큰 갱신이 성공한 경우, 새로운 토큰을 로컬 스토리지에 저장
                            localStorage.setItem('accessToken', response.data.accessToken);
                            localStorage.setItem('refreshToken', response.data.refreshToken);

                            // 토큰 갱신 후, 새로고침
                            window.location.reload();
                        })
                        .catch(function (error) {
                            console.error("Refresh token error", error);
                            // 토큰 갱신에 실패한 경우, /loginForm으로 리다이렉트
                            // window.location.href = '/loginForm';
                        });
                }
            });
    } else {
        // 엑세스 토큰이 없는 경우, /loginForm으로 리다이렉트
        window.location.href = '/loginForm';
    }
}
