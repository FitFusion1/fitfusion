// Edit User Form Validation
document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('.edit-user-form');
    const currentEmail = document.getElementById('email').value; // Store current email for comparison

    // Initialize validation
    function initValidation() {
        addEventListeners();
    }

    // Add event listeners for real-time validation
    function addEventListeners() {
        // Email validation
        const emailInput = document.getElementById('email');
        emailInput.addEventListener('input', function () {
            validateEmail(this);
        });

        // Height validation
        const heightInput = document.getElementById('height');
        heightInput.addEventListener('input', function () {
            validateHeight(this);
        });

        // Weight validation
        const weightInput = document.getElementById('weight');
        weightInput.addEventListener('input', function () {
            validateWeight(this);
        });

        // Birth date validation
        const birthDateInput = document.getElementById('birthDate');
        birthDateInput.addEventListener('input', function () {
            validateBirthDate(this);
        });

        // Experience level validation
        const experienceLevelInput = document.getElementById('experienceLevel');
        experienceLevelInput.addEventListener('change', function () {
            validateExperienceLevel(this);
        });

        // Form submission validation
        form.addEventListener('submit', function (e) {
            if (!validateForm()) {
                e.preventDefault();
            }
        });
    }

    // Email validation
    function validateEmail(input) {
        const value = input.value.trim();
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        // Clear previous states
        clearFieldStates(input);

        if (value === '') {
            showError(input, 'empty');
            return false;
        }

        if (!emailRegex.test(value)) {
            showError(input, 'format');
            return false;
        }

        // Check for duplicate email (only if different from current)
        if (value !== currentEmail) {
            checkDuplicateEmail(input, value);
        }

        return true;
    }

    // Height validation
    function validateHeight(input) {
        const value = input.value.replace(/[^0-9]/g, '');
        input.value = value; // Update input with filtered value

        // Clear previous states
        clearFieldStates(input);

        if (value === '') {
            showError(input, 'empty');
            return false;
        }

        if (value < 80 || value > 222) {
            showError(input, 'format');
            return false;
        }

        // Show success state if validation passes
        showValidMessage(input, 'valid');
        return true;
    }

    // Weight validation
    function validateWeight(input) {
        const value = input.value.replace(/[^0-9]/g, '');
        input.value = value; // Update input with filtered value

        // Clear previous states
        clearFieldStates(input);

        if (value === '') {
            showError(input, 'empty');
            return false;
        }

        if (value < 20 || value > 500) {
            showError(input, 'format');
            return false;
        }

        // Show success state if validation passes
        showValidMessage(input, 'valid');
        return true;
    }

    // Birth date validation
    function validateBirthDate(input) {
        const value = input.value;

        // Clear previous states
        clearFieldStates(input);

        if (value === '') {
            showError(input, 'empty');
            return false;
        }

        // Show success state if validation passes
        showValidMessage(input, 'valid');
        return true;
    }

    // Experience level validation
    function validateExperienceLevel(input) {
        const value = input.value;

        // Clear previous states
        clearFieldStates(input);

        if (value === '') {
            showError(input, 'empty');
            return false;
        }

        // Show success state if validation passes
        showValidMessage(input, 'valid');
        return true;
    }

    // Check for duplicate email
    function checkDuplicateEmail(input, email) {
        $.getJSON("/api/registration/validation", `type=email&value=${email}`, function (result) {
            const duplicateExists = result.data;
            if (duplicateExists) {
                showError(input, 'duplicate');
            } else {
                showValidMessage(input, 'email');
            }
        });
    }

    // Validate entire form
    function validateForm() {
        let isValid = true;

        // Validate all required fields
        const emailInput = document.getElementById('email');
        const heightInput = document.getElementById('height');
        const weightInput = document.getElementById('weight');
        const birthDateInput = document.getElementById('birthDate');
        const experienceLevelInput = document.getElementById('experienceLevel');

        // Email validation
        if (!validateEmail(emailInput)) {
            isValid = false;
        }

        // Height validation
        if (!validateHeight(heightInput)) {
            isValid = false;
        }

        // Weight validation
        if (!validateWeight(weightInput)) {
            isValid = false;
        }

        // Birth date validation
        if (!validateBirthDate(birthDateInput)) {
            isValid = false;
        }

        // Experience level validation
        if (!validateExperienceLevel(experienceLevelInput)) {
            isValid = false;
        }

        return isValid;
    }

    // Initialize validation
    initValidation();

    // Initialize password modal functionality
    initPasswordModal();
});

// Show error message
function showError(input, errorType) {
    const errorMessage = input.parentNode.querySelector(`[data-error-cat="${errorType}"]`);
    if (errorMessage) {
        errorMessage.classList.add('invalid');
    }
    input.classList.add('field-error');
    input.classList.remove('field-valid');
}

// Show valid message
function showValidMessage(input, validType) {
    const validMessage = input.parentNode.querySelector(`[data-valid-cat="${validType}"]`);
    if (validMessage) {
        validMessage.classList.add('active');
    }
    input.classList.add('field-valid');
    input.classList.remove('field-error');
}

// Clear field states
function clearFieldStates(input) {
    // Clear error messages
    const errorMessages = input.parentNode.querySelectorAll('.error-message');
    errorMessages.forEach(msg => msg.classList.remove('invalid'));

    // Clear valid messages
    const validMessages = input.parentNode.querySelectorAll('.valid-message');
    validMessages.forEach(msg => msg.classList.remove('active'));

    // Clear visual states
    input.classList.remove('field-error', 'field-valid');
}

// Password Modal Functions
function initPasswordModal() {
    const passwordForm = document.getElementById('passwordChangeForm');

    // Add event listeners for password form validation
    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmNewPasswordInput = document.getElementById('confirmNewPassword');

    // Real-time validation for password fields
    currentPasswordInput.addEventListener('input', function () {
        validatePasswordField(this, 'currentPassword');
    });

    newPasswordInput.addEventListener('input', function () {
        validatePasswordField(this, 'newPassword');
        validatePasswordConfirmation();
    });

    confirmNewPasswordInput.addEventListener('input', function () {
        validatePasswordConfirmation();
    });

    // Form submission
    passwordForm.addEventListener('submit', function (e) {
        e.preventDefault();
        if (validatePasswordForm()) {
            submitPasswordChange();
        }
    });
}

// Open password modal
function openPasswordModal() {
    const modal = document.getElementById('passwordModal');
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';

    // Focus on first input
    setTimeout(() => {
        document.getElementById('currentPassword').focus();
    }, 100);
}

// Close password modal
function closePasswordModal() {
    const modal = document.getElementById('passwordModal');
    modal.classList.remove('show');
    document.body.style.overflow = 'auto';

    // Clear form and validation states
    clearPasswordForm();
}

// Validate individual password field
function validatePasswordField(input, fieldType) {
    const value = input.value.trim();

    // Clear previous states
    clearFieldStates(input);

    if (value === '') {
        showError(input, 'empty');
        return false;
    }

    if (fieldType === 'newPassword' && value.length < 6) {
        showError(input, 'length');
        return false;
    }

    if (fieldType === 'confirmNewPassword' && value.length < 6) {
        showError(input, 'length');
        return false;
    }

    // Show success state if validation passes
    showValidMessage(input, 'valid');
    return true;
}

// Validate password confirmation
function validatePasswordConfirmation() {
    const newPasswordInput = document.getElementById('newPassword');
    const confirmNewPasswordInput = document.getElementById('confirmNewPassword');
    const newPassword = newPasswordInput.value.trim();
    const confirmPassword = confirmNewPasswordInput.value.trim();

    // Clear previous states
    clearFieldStates(confirmNewPasswordInput);

    if (confirmPassword === '') {
        showError(confirmNewPasswordInput, 'empty');
        return false;
    }

    if (newPassword !== confirmPassword) {
        showError(confirmNewPasswordInput, 'mismatch');
        return false;
    }

    // Show success state if validation passes
    showValidMessage(confirmNewPasswordInput, 'valid');

    // Visual feedback
    confirmNewPasswordInput.classList.add('password-match');
    confirmNewPasswordInput.classList.remove('password-mismatch');
    return true;
}

// Validate entire password form
function validatePasswordForm() {
    let isValid = true;

    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmNewPasswordInput = document.getElementById('confirmNewPassword');

    if (!validatePasswordField(currentPasswordInput, 'currentPassword')) {
        isValid = false;
    }

    if (!validatePasswordField(newPasswordInput, 'newPassword')) {
        isValid = false;
    }

    if (!validatePasswordField(confirmNewPasswordInput, 'confirmNewPassword')) {
        isValid = false;
    }

    if (!validatePasswordConfirmation()) {
        isValid = false;
    }

    return isValid;
}

// Submit password change via AJAX
function submitPasswordChange() {
    const form = document.getElementById('passwordChangeForm');
    const formData = new FormData(form);
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmNewPasswordInput = document.getElementById('confirmNewPassword');

    // Show loading state
    const submitBtn = document.querySelector('.change-password-btn');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 처리중...';
    submitBtn.disabled = true;

    fetch('/api/password', {
        method: 'POST',
        headers: {
            [csrfHeader]: csrfToken
        },
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showPasswordSuccessMessage();
                setTimeout(() => {
                    closePasswordModal();
                }, 2000);
            } else {
                if (data.status === 551) {
                    showError(currentPasswordInput, 'mismatch')
                } else if (data.status === 552) {
                    showError(confirmNewPasswordInput, 'mismatch')
                } else if (data.status === 553) {
                    showError(newPasswordInput, 'length')
                }
                showPasswordErrorMessage(data.message || '비밀번호 변경에 실패했습니다.');
            }
        })
        .catch(error => {
            showPasswordErrorMessage('서버 오류가 발생했습니다. ' + error.message);
        })
        .finally(() => {
            // Restore button state
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
}

// Show password success message
function showPasswordSuccessMessage() {
    const modal = document.getElementById('passwordModal');
    const form = document.getElementById('passwordChangeForm');

    // Hide form and show success message
    form.style.display = 'none';

    const successDiv = document.createElement('div');
    successDiv.className = 'password-success';
    successDiv.innerHTML = `
        <div class="success-content">
            <i class="bi bi-check-circle-fill"></i>
            <h3>비밀번호 변경 완료!</h3>
            <p>비밀번호가 성공적으로 변경되었습니다.</p>
        </div>
    `;

    modal.querySelector('.password-modal-content').appendChild(successDiv);
}

// Show password error message
function showPasswordErrorMessage(message) {
    // Create and show error toast
    const toast = document.createElement('div');
    toast.className = 'error-toast';
    toast.innerHTML = `
        <i class="bi bi-exclamation-circle"></i>
        <span>${message}</span>
    `;

    document.body.appendChild(toast);

    // Show toast
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);

    // Hide and remove toast
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(toast);
        }, 300);
    }, 4000);
}

// Clear password form
function clearPasswordForm() {
    const form = document.getElementById('passwordChangeForm');
    form.reset();

    // Clear validation states
    const inputs = form.querySelectorAll('input');
    inputs.forEach(input => {
        clearFieldStates(input);
        input.classList.remove('password-match', 'password-mismatch');
    });

    // Remove success message if exists
    const modal = document.getElementById('passwordModal');
    const successDiv = modal.querySelector('.password-success');
    if (successDiv) {
        successDiv.remove();
    }

    // Show form again
    form.style.display = 'block';
}

// Close modal when clicking outside
document.addEventListener('click', function (e) {
    const modal = document.getElementById('passwordModal');
    if (e.target === modal) {
        closePasswordModal();
    }
});

// Close modal with Escape key
document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        const modal = document.getElementById('passwordModal');
        if (modal.classList.contains('show')) {
            closePasswordModal();
        }
    }
});

