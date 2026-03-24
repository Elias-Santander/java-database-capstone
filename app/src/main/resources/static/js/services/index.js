/**
 * auth.js / index.js (Logic for Login Handlers)
 * Handles Admin and Doctor authentication via fetch API.
 */

// 1. Imports
import { openModal } from './util.js';
import { BASE_URL } from './config.js';

// 2. Define API Endpoints
const ADMIN_API = `${BASE_URL}/api/admin/login`;
const DOCTOR_API = `${BASE_URL}/api/doctor/login`;

// 3. Initialize Event Listeners on Page Load
window.onload = () => {
    const adminLoginBtn = document.getElementById("adminLogin");
    const doctorLoginBtn = document.getElementById("doctorLogin");

    if (adminLoginBtn) {
        adminLoginBtn.addEventListener("click", () => openModal('adminLogin'));
    }

    if (doctorLoginBtn) {
        doctorLoginBtn.addEventListener("click", () => openModal('doctorLogin'));
    }
};

/**
 * ADMIN LOGIN HANDLER
 * Exposed to global window for HTML inline event access
 */
window.adminLoginHandler = async () => {
    // Step 1: Get credentials
    const username = document.getElementById("adminUsername")?.value;
    const password = document.getElementById("adminPassword")?.value;

    if (!username || !password) {
        alert("Please enter both username and password.");
        return;
    }

    // Step 2: Create object
    const adminCredentials = { username, password };

    try {
        // Step 3: Fetch POST request
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(adminCredentials)
        });

        // Step 4: Handle success
        if (response.ok) {
            const data = await response.json();
            const token = data.token;

            localStorage.setItem("token", token);
            localStorage.setItem("userRole", "admin"); // State persistence

            // Proceed with admin behavior
            if (typeof selectRole === "function") {
                selectRole('admin');
            } else {
                window.location.href = "/pages/adminDashboard.html";
            }
        } else {
            // Step 5: Handle invalid credentials
            alert("Invalid admin credentials. Please try again.");
        }
    } catch (error) {
        // Step 6: Graceful error handling
        console.error("Admin Login Error:", error);
        alert("Server connection error. Please check if the backend is running.");
    }
};

/**
 * DOCTOR LOGIN HANDLER
 * Exposed to global window
 */
window.doctorLoginHandler = async () => {
    // Step 1: Get credentials (using email for doctors)
    const email = document.getElementById("doctorEmail")?.value;
    const password = document.getElementById("doctorPassword")?.value;

    if (!email || !password) {
        alert("Please enter both email and password.");
        return;
    }

    // Step 2: Create object
    const doctorCredentials = { email, password };

    try {
        // Step 3: Fetch POST request
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctorCredentials)
        });

        // Step 4: Handle success
        if (response.ok) {
            const data = await response.json();

            localStorage.setItem("token", data.token);
            localStorage.setItem("userRole", "doctor");

            if (typeof selectRole === "function") {
                selectRole('doctor');
            } else {
                window.location.href = "/pages/doctorDashboard.html";
            }
        } else {
            // Step 5: Handle failure
            alert("Login failed: Invalid doctor email or password.");
        }
    } catch (error) {
        // Step 6: Catch network/server errors
        console.error("Doctor Login Error:", error);
        alert("An unexpected error occurred. Please try again later.");
    }
};