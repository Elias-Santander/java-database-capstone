/**
 * footer.js
 * Dynamically renders the site-wide footer with logo, copyright, and categorized links.
 */

const renderFooter = () => {
    // 1. Select the footer element from the DOM
    const footerDiv = document.getElementById("footer");

    // Safety check to prevent errors if the div isn't present on a specific page
    if (!footerDiv) return;

    // 2-10. Define the footer HTML structure
    const footerHTML = `
        <footer class="footer">
            <div class="footer-container">

                <div class="footer-logo">
                    <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo">
                    <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
                </div>

                <div class="footer-links">

                    <div class="footer-column">
                        <h4>Company</h4>
                        <a href="#">About</a>
                        <a href="#">Careers</a>
                        <a href="#">Press</a>
                    </div>

                    <div class="footer-column">
                        <h4>Support</h4>
                        <a href="#">Account</a>
                        <a href="#">Help Center</a>
                        <a href="#">Contact Us</a>
                    </div>

                    <div class="footer-column">
                        <h4>Legals</h4>
                        <a href="#">Terms & Conditions</a>
                        <a href="#">Privacy Policy</a>
                        <a href="#">Licensing</a>
                    </div>

                </div> </div> </footer>
    `;

    // 11. Render the Footer Content
    footerDiv.innerHTML = footerHTML;
};

// 12. Call the function when the script loads
// Using DOMContentLoaded to ensure the placeholder exists
document.addEventListener("DOMContentLoaded", renderFooter);