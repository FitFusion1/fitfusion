// Registration Form Step Navigation
document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('.registration-form');
    const steps = document.querySelectorAll('.step');
    let currentStep = 1;
    const totalSteps = steps.length;

    // Initialize the form
    function initForm() {
        showStep(1);
        addEventListeners();
    }

    // Show specific step
    function showStep(stepNumber) {
        steps.forEach((step, index) => {
            step.classList.remove('active', 'prev', 'next');
            if (index + 1 === stepNumber) {
                step.classList.add('active');
            } else if (index + 1 < stepNumber) {
                step.classList.add('prev');
            } else {
                step.classList.add('next');
            }
        });
        currentStep = stepNumber;
    }

    // Go to next step
    function nextStep() {
        if (validateCurrentStep() && currentStep < totalSteps) {
            const currentCard = document.querySelector(`.step-${currentStep}`);
            const nextCard = document.querySelector(`.step-${currentStep + 1}`);

            // Position next card off-screen to the right
            nextCard.style.transform = 'translateX(100%)';
            nextCard.style.display = 'flex';
            nextCard.style.opacity = '1';
            nextCard.style.visibility = 'visible';

            // Force reflow
            nextCard.offsetHeight;

            // Trigger slide animation
            currentCard.classList.add('slide-left');
            nextCard.style.transform = 'translateX(0)';

            // After animation completes
            setTimeout(() => {
                showStep(currentStep + 1);
                currentCard.classList.remove('slide-left');
                currentCard.style.display = 'none';
                currentCard.style.opacity = '';
                currentCard.style.visibility = '';
                currentCard.style.transform = '';

                nextCard.style.transform = '';
            }, 300);
        }
    }

    // Go to previous step
    function prevStep() {
        if (currentStep > 1) {
            const currentCard = document.querySelector(`.step-${currentStep}`);
            const prevCard = document.querySelector(`.step-${currentStep - 1}`);

            // Prepare previous card for entry from left
            prevCard.style.transform = 'translateX(-100%)';
            prevCard.style.display = 'flex';
            prevCard.style.opacity = '1';
            prevCard.style.visibility = 'visible';

            // Force reflow to ensure the transition happens
            prevCard.offsetHeight;

            // Animate
            currentCard.classList.add('slide-right');
            prevCard.style.transform = 'translateX(0)';

            // After animation completes
            setTimeout(() => {
                // Reset the styles
                currentCard.classList.remove('slide-right');
                currentCard.style.display = 'none';
                currentCard.style.opacity = '';
                currentCard.style.visibility = '';
                currentCard.style.transform = '';

                prevCard.style.transform = '';
                showStep(currentStep - 1);
            }, 300);
        }
    }


    // Validate current step
    function validateCurrentStep() {
        const currentCard = document.querySelector(`.step-${currentStep}`);
        const inputs = currentCard.querySelectorAll('input, select');
        let isValid = true;

        inputs.forEach(input => {
            let invalidEmptyMessage = input.parentNode.querySelector('[data-error-cat="empty"]');
            invalidEmptyMessage.classList.remove('invalid');
            if (!input.value.trim()) {
                isValid = false;
                input.style.borderColor = '#ff4444';
                input.parentNode.querySelector('[data-error-cat="empty"]').classList.add('invalid');
                input.addEventListener('input', function () {
                    this.style.removeProperty('border-color');
                }, {once: true});
            } else {
                input.dispatchEvent(new Event('input'));
                const errorMessages = input.parentNode.querySelectorAll('.error-message.invalid');
                if (errorMessages.length !== 0) {
                    isValid = false;
                    input.style.borderColor = '#ff4444';
                }
            }
        });

        return isValid;
    }

    function checkDuplicateValue(type, inputField) {
        $.getJSON("/api/registration/validation", `type=${type}`, function (result) {
            const duplicateExists = result.data;
            if (duplicateExists) {
                inputField.parentNode.querySelector('[data-error-cat="duplicate"]')
                    .classList.add('invalid');
            } else {
                inputField.parentNode.querySelector('.valid-message')
                    .classList.add('active');
            }
        });
    }

    // Add event listeners
    function addEventListeners() {
        // Next button listeners
        document.querySelectorAll('.next-btn').forEach(btn => {
            btn.addEventListener('click', nextStep);
        });

        // Back button listeners
        document.querySelectorAll('.back-btn').forEach(btn => {
            btn.addEventListener('click', prevStep);
        });

        // Enter key navigation
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && currentStep < totalSteps) {
                e.preventDefault();
                nextStep();
            }
        });

        document.addEventListener('submit', function (e) {
           if (!validateCurrentStep()) {
               e.preventDefault();
           }
        });

        document.addEventListener('input', (e) => {
            const inputElement = e.target;
            const invalidEmptyMessage = inputElement.parentNode.querySelector('[data-error-cat="empty"]');
            invalidEmptyMessage.classList.remove('invalid');
            inputElement.parentNode.querySelector('.valid-message')
                ?.classList.remove('active');
            const inputValue = inputElement.value;

            if (inputElement.getAttribute('name') === 'username') {
                const filteredInput = inputValue.replace(/[^0-9a-zA-Z-_]/g, '');
                const invalidCharMessage = inputElement.parentNode.querySelector('[data-error-cat="format"]');
                const invalidLengthMessage = inputElement.parentNode.querySelector('[data-error-cat="length"]');

                if (inputValue !== filteredInput) {
                    inputElement.value = filteredInput;
                    invalidCharMessage.classList.add('invalid');
                } else {
                    invalidCharMessage.classList.remove('invalid');
                }

                if (inputValue.length < 3) {
                    invalidLengthMessage.classList.add('invalid');
                } else {
                    invalidLengthMessage.classList.remove('invalid');
                }

                const invalidMessages = inputElement.parentNode.querySelectorAll('.error-message.invalid');
                if (invalidMessages.length === 0) {
                    checkDuplicateValue('username', inputElement);
                }
            }

            if (inputElement.getAttribute('name') === 'email') {
                const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
                const invalidFormatMessage = inputElement.parentNode.querySelector('[data-error-cat="format"]');
                if (!emailRegex.test(inputValue)) {
                    invalidFormatMessage.classList.add('invalid');
                } else {
                    invalidFormatMessage.classList.remove('invalid');
                    checkDuplicateValue('email', inputElement);
                }
            }

            if (inputElement.getAttribute('name') === 'password') {
                const filteredInput = inputValue.replace(/\s/g, '');
                const invalidLengthMessage = inputElement.parentNode.querySelector('[data-error-cat="length"]');
                if (inputValue !== filteredInput) {
                    inputElement.value = filteredInput;
                }
                if (inputValue.length < 6) {
                    invalidLengthMessage.classList.add('invalid');
                } else {
                    invalidLengthMessage.classList.remove('invalid');
                }
            }

            if (inputElement.getAttribute('name') === 'name') {
                const validInput = /^[가-힣]{2,}$/;
                const invalidFormatMessage = inputElement.parentNode.querySelector('[data-error-cat="format"]');
                if (!validInput.test(inputValue)) {
                    invalidFormatMessage.classList.add('invalid');
                } else {
                    invalidFormatMessage.classList.remove('invalid');
                }
            }

            if (inputElement.getAttribute('name') === 'height') {
                inputElement.value = inputValue.replace(/[^0-9]/g, '');
                const invalidFormatMessage = inputElement.parentNode.querySelector('[data-error-cat="format"]');
                if (inputValue < 80 || inputValue > 222) {
                    invalidFormatMessage.classList.add('invalid');
                } else {
                    invalidFormatMessage.classList.remove('invalid');
                }
            }

            if (inputElement.getAttribute('name') === 'weight') {
                inputElement.value = inputValue.replace(/[^0-9]/g, '');
                const invalidFormatMessage = inputElement.parentNode.querySelector('[data-error-cat="format"]');
                if (inputValue < 20 || inputValue > 500) {
                    invalidFormatMessage.classList.add('invalid');
                } else {
                    invalidFormatMessage.classList.remove('invalid');
                }
            }

            // 부적절한 입력값이 있는지 확인하고 있으면 붉은색 테두리 추가하기
            const invalidMessages = inputElement.parentNode.querySelectorAll('.error-message.invalid');
            if (invalidMessages.length !== 0) {
                inputElement.style.borderColor = '#ff4444';
            } else {
                inputElement.style.removeProperty('border-color');
            }
        });
    }

    // Initialize the form
    initForm();
});