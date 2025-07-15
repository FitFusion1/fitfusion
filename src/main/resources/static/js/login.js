document.addEventListener('DOMContentLoaded', function () {
    const usernameField = document.getElementById('username-input');
    const passwordField = document.getElementById('password-input');
    const usernameWarningMsg = document.getElementById('username-invalid-message');
    const passwordWarningMsg = document.getElementById('password-invalid-message');

    document.addEventListener('submit', function (e) {
        e.preventDefault();
        const usernameInput = usernameField.value.trim();
        const passwordInput = passwordField.value.trim();

        let hasError = false;

        if (usernameInput.length < 3) {
            usernameWarningMsg.textContent = '아이디/이메일은 3자 이상이어야 합니다.';
            hasError = true;
        }

        if (passwordInput.length < 6) {
            passwordWarningMsg.textContent = '비밀번호를 6글자 이상 입력해주세요.';
            hasError = true;
        }

        if (hasError) return;

        e.target.submit();
    });
});
