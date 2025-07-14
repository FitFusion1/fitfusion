// Login Modal Logic
(function () {
    const openBtn = document.querySelector('#login-modal-open-btn');
    const overlay = document.querySelector('.login-modal-overlay');
    const modal = document.querySelector('.login-modal');
    const closeBtn = document.querySelector('.login-modal-close');

    if (!openBtn || !overlay || !modal || !closeBtn) return;

    function openModal() {
        overlay.style.display = 'flex';
        document.body.style.overflow = 'hidden';
        setTimeout(() => {
            modal.querySelector('input[name="username"]').focus();
        }, 100);
    }

    function closeModal() {
        overlay.style.display = 'none';
        document.body.style.overflow = '';
    }

    openBtn.addEventListener('click', openModal);
    closeBtn.addEventListener('click', closeModal);
    let mouseDownOnOverlay = false;
    overlay.addEventListener('mousedown', function (e) {
        mouseDownOnOverlay = (e.target === overlay);
    });
    overlay.addEventListener('mouseup', function (e) {
        if (mouseDownOnOverlay && e.target === overlay) {
            closeModal();
        }
    });
    document.addEventListener('keydown', function (e) {
        if (overlay.style.display === 'flex' && e.key === 'Escape') closeModal();
    });

    const modalLoginForm = document.getElementById('modal-login-form');
    const modalUsernameField = document.getElementById('username-field')
    const modalPasswordField = document.getElementById('password-field')
    const modalUsernameWarning = document.getElementById('username-invalid-message')
    const modalPasswordWarning = document.getElementById('password-invalid-message')
    const modalLoginErrorMessage = document.getElementById('login-error-message')

    modalLoginForm.addEventListener('submit', async function (e) {
        e.preventDefault();
        modalUsernameWarning.textContent = '';
        modalPasswordWarning.textContent = '';
        modalLoginErrorMessage.textContent = '';

        const usernameInput = modalUsernameField.value.trim();
        const passwordInput = modalPasswordField.value.trim();

        let hasError = false;

        if (usernameInput.length < 3) {
            modalUsernameWarning.textContent = '아이디/이메일은 3자 이상이어야 합니다.';
            hasError = true;
        }

        if (passwordInput.length < 6) {
            modalPasswordWarning.textContent = '비밀번호를 6글자 이상 입력해주세요.';
            hasError = true;
        }

        if (hasError) return;

        const currentUrl = window.location.href;
        $.ajax({
            url: '/api/save-request-url',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                redirectUrl: currentUrl
            })
        });

        $.ajax({
            url: "/user/login",
            method: "POST",
            contentType: 'application/x-www-form-urlencoded',
            data: {
                username: usernameInput,
                password: passwordInput
            },
            success: function () {
                location.reload();
            },
            error: function (jqXHR) {
                if (jqXHR.status === 401) {
                    modalLoginErrorMessage.textContent = '아이디 혹은 비밀번호가 올바르지 않습니다.';
                }
            }
        });
    })
})();