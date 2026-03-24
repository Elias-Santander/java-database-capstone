/**
 * doctorDashboard.js
 * Logic for the Doctor's view: managing appointments, searching patients,
 * and filtering by date.
 */

// 1. Imports
import { getAllAppointments } from './appointmentServices.js';
import { createPatientRow } from './render.js';

// 2. Global State & DOM Elements
const patientTableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const datePicker = document.getElementById("datePicker");
const todayButton = document.getElementById("todayButton");

let selectedDate = new Date().toISOString().split('T')[0]; // 'YYYY-MM-DD'
let patientName = "null"; // Default as expected by backend
const token = localStorage.getItem("token");

/**
 * 3. Event Listeners
 */

// Search Bar: Filter by patient name on every keystroke
if (searchBar) {
    searchBar.addEventListener("input", (e) => {
        const value = e.target.value.trim();
        patientName = value !== "" ? value : "null";
        loadAppointments();
    });
}

// Today Button: Reset filters to current date
if (todayButton) {
    todayButton.addEventListener("click", () => {
        selectedDate = new Date().toISOString().split('T')[0];
        if (datePicker) datePicker.value = selectedDate;
        loadAppointments();
    });
}

// Date Picker: Filter by a specific selected date
if (datePicker) {
    datePicker.value = selectedDate; // Sync UI on load
    datePicker.addEventListener("change", (e) => {
        selectedDate = e.target.value;
        loadAppointments();
    });
}

/**
 * 4. Function: loadAppointments
 * Purpose: Fetch and display filtered data
 */
async function loadAppointments() {
    if (!token) {
        console.error("No authentication token found.");
        return;
    }

    try {
        // Step 1: Fetch data from service
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        // Step 2: Clear table body
        patientTableBody.innerHTML = "";

        // Step 3 & 4: Handle results
        if (!appointments || appointments.length === 0) {
            patientTableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="noPatientRecord">No Appointments found for this selection.</td>
                </tr>`;
        } else {
            appointments.forEach(app => {
                // Construct normalized patient object from appointment data
                const patient = {
                    id: app.patientId,
                    name: app.patientName,
                    phone: app.patientPhone || "N/A",
                    email: app.patientEmail || "N/A",
                    prescription: app.prescriptionStatus || "None"
                };

                // Generate row and append
                const row = createPatientRow(patient);
                patientTableBody.appendChild(row);
            });
        }
    } catch (error) {
        // Step 5: Error handling
        console.error("Error loading appointments:", error);
        patientTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="noPatientRecord" style="color: #A62B1F;">
                    Error loading appointments. Try again later.
                </td>
            </tr>`;
    }
}

/**
 * 5. Initialization
 */
document.addEventListener("DOMContentLoaded", () => {
    // Assuming renderContent() is a global function from render.js
    // that handles header/footer injection
    if (typeof renderContent === "function") {
        renderContent();
    }

    // Initial data load
    loadAppointments();
});