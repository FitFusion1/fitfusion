// Error Page JavaScript
document.addEventListener('DOMContentLoaded', function () {
    // Add any error page specific functionality here

    // Example: Track error page views for analytics
    const errorCode = document.querySelector('.error-code')?.textContent;
    if (errorCode) {
        console.log('Error page viewed:', errorCode);
        // You can add analytics tracking here
        // gtag('event', 'error_page_view', { error_code: errorCode });
    }

    // Add smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });
});
