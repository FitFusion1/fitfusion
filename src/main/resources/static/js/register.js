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
            if (!input.value.trim()) {
                isValid = false;
                input.style.borderColor = '#ff4444';
                input.focus();
                input.parentNode.querySelector('span[data-username-error="length"]').classList.add('invalid');
                // input.addEventListener('input', function () {
                //     this.style.borderColor = '#e0e0e0';
                // }, {once: true});
            }
        });

        if (currentStep === 1) {
            let usernameField = currentCard.querySelector('input[name="username"]');
            if (usernameField.value.trim().length < 3) {
                isValid = false;
            }
        }

        return isValid;
    }

    // Handle form submission
    function handleSubmit(e) {
        e.preventDefault();
        if (validateCurrentStep()) {
            // Collect all form data
            const formData = new FormData(form);
            const data = Object.fromEntries(formData);

            // Here you would typically send the data to your server
            console.log('Registration data:', data);

            // Show success message or redirect
            alert('Registration successful!');
        }
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

        // Form submission
        form.addEventListener('submit', handleSubmit);

        // Enter key navigation
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && currentStep < totalSteps) {
                e.preventDefault();
                nextStep();
            }
        });

        // username 필드 영어 대소문자, '-', '_' 이외의 문자는
        // 입력 감지 시에 ''로 대체
        const usernameInput = document.querySelector('input[name="username"]');
        const invalidCharMessage = document.querySelector('[data-username-error="invalid-chars"]');
        const invalidLengthMessage = document.querySelector('[data-username-error="length"]');

        usernameInput.addEventListener('input', () => {
            const value = usernameInput.value;
            const validOnly = value.replace(/[^0-9a-zA-Z-_]/g, '');

            if (value !== validOnly) {
                usernameInput.value = validOnly;
                invalidCharMessage.classList.add('invalid');
            } else {
                invalidCharMessage.classList.remove('invalid');
            }

            if (value.length < 3) {
                invalidLengthMessage.classList.add('invalid');
            } else {
                invalidLengthMessage.classList.remove('invalid');
            }
        });

    }

    // Initialize the form
    initForm();
});