/**
 * Library Management System – Client-side JavaScript
 *
 * - Auto-dismiss alerts after 5 seconds
 * - Confirm dialogs (handled inline via onclick)
 * - Smooth page load transitions
 */

document.addEventListener('DOMContentLoaded', function () {

    // ── Auto-dismiss Bootstrap alerts after 5 seconds ────────────────────
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const closeBtn = alert.querySelector('.btn-close');
            if (closeBtn) closeBtn.click();
        }, 5000);
    });

    // ── Highlight active nav link based on current URL path ──────────────
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('#main-navbar .nav-link');
    navLinks.forEach(function (link) {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            link.classList.add('active');
        }
    });

    // ── Add fade-in animation to main content ────────────────────────────
    const mainContent = document.querySelector('main');
    if (mainContent) {
        mainContent.style.opacity = '0';
        mainContent.style.transition = 'opacity 0.3s ease';
        setTimeout(function () {
            mainContent.style.opacity = '1';
        }, 50);
    }

    // ── Table row hover highlight (extra visual feedback) ────────────────
    const tableRows = document.querySelectorAll('.table-hover tbody tr');
    tableRows.forEach(function (row) {
        row.style.cursor = 'default';
    });

    console.log('📚 Library Management System loaded successfully.');
});
