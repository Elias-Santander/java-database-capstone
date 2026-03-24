/**
 * adminDashboard.js
 * Logic for managing the Doctor grid, filtering, and adding new records.
 */

import { getDoctors, filterDoctors, saveDoctor } from './doctorServices.js';
import { createDoctorCard } from './doctorCard.js';
import { openModal, closeModal } from './util.js';

// Elements
const contentArea = document.getElementById("content");
const searchBar = document.getElementById("searchBar");
const filterTime = document.getElementById("filter-time");
const filterSpecialty = document.getElementById("filter-specialty");
const addDocBtn = document.getElementById("addDocBtn");

/**
 * 1. Initialize Event Listeners
 */
document.addEventListener("DOMContentLoaded", () => {
    // Load initial data
    loadDoctorCards();

    // Attach listener for Add Doctor button
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => openModal('addDoctor'));
    }

    // Attach listeners for Search and Filters
    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

/**
 * 2. Function: loadDoctorCards
 * Purpose: Initial fetch of all doctors
 */
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Failed to load doctor cards:", error);
        contentArea.innerHTML = `<p class="error">Error loading doctors. Please try again later.</p>`;
    }
}

/**
 * 3. Function: filterDoctorsOnChange
 * Purpose: Reactive filtering based on UI inputs
 */
async function filterDoctorsOnChange() {
    const name = searchBar.value.trim() || null;
    const time = filterTime.value || null;
    const specialty = filterSpecialty.value || null;

    try {
        const response = await filterDoctors(name, time, specialty);
        const doctors = response.doctors || [];

        if (doctors.length > 0) {
            renderDoctorCards(doctors);
        } else {
            contentArea.innerHTML = `<p class="no-results">No doctors found with the given filters.</p>`;
        }
    } catch (error) {
        console.error("Filtering error:", error);
        alert("An error occurred while filtering. Please try again.");
    }
}

/**
 * 4. Function: renderDoctorCards
 * Helper to clear and repopulate the grid
 */
function renderDoctorCards(doctors) {
    contentArea.innerHTML = ""; // Clear current content
    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentArea.appendChild(card);
    });
}

/**
 * 5. Function: adminAddDoctor
 * Global function for the "Save" button in the Add Doctor Modal
 */
window.adminAddDoctor = async () => {
    // Collect form data
    const name = document.getElementById("docName").value;
    const email = document.getElementById("docEmail").value;
    const phone = document.getElementById("docPhone").value;
    const password = document.getElementById("docPassword").value;
    const specialty = document.getElementById("docSpecialty").value;
    const availableTimes = document.getElementById("docTimes").value; // e.g., "AM, PM"

    // Auth Check
    const token = localStorage.getItem("token");
    if (!token) {
        alert("Authentication error. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Validation
    if (!name || !email || !specialty) {
        alert("Please fill in the required fields (Name, Email, Specialty).");
        return;
    }

    const newDoctor = {
        name,
        email,
        phone,
        password,
        specialization: specialty,
        availableTimes: availableTimes.split(",").map(t => t.trim()) // Convert string to array
    };

    try {
        const result = await saveDoctor(newDoctor, token);

        if (result.success) {
            alert("Doctor added successfully!");
            closeModal();
            location.reload(); // Reload to show the new card
        } else {
            alert(`Error: ${result.message}`);
        }
    } catch (error) {
        console.error("Save Doctor Error:", error);
        alert("Failed to save doctor. Check console for details.");
    }
};