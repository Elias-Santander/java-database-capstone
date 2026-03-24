/**
 * doctorCard.js
 * Generates dynamic doctor cards for the dashboard with role-based actions.
 */

// 1. Import dependencies
import { openBookingOverlay } from './loggedPatient.js';
import { deleteDoctor } from './doctorServices.js';
import { getPatientDetails } from './patientServices.js';

/**
 * 2. Function to create and return a DOM element for a single doctor card
 * @param {Object} doctor - The doctor data object from your API
 * @returns {HTMLElement} The complete doctor card element
 */
export const createDoctorCard = (doctor) => {
    // Create the main container for the doctor card
    const card = document.createElement('div');
    card.className = 'doctor-card';

    // Retrieve the current user role from localStorage
    const role = localStorage.getItem('userRole');

    // Create a div to hold doctor information
    const infoDiv = document.createElement('div');
    infoDiv.className = 'doctor-info';

    // Create and set the doctor’s name
    const name = document.createElement('h3');
    name.textContent = `Dr. ${doctor.name}`;

    // Create and set the doctor's specialization
    const specialization = document.createElement('p');
    specialization.className = 'doc-specialty';
    specialization.textContent = doctor.specialization;

    // Create and set the doctor's email
    const email = document.createElement('p');
    email.textContent = `Email: ${doctor.email}`;

    // Create and list available appointment times
    const times = document.createElement('p');
    // Assuming availableTimes is an array in your doctor object
    const availableStr = Array.isArray(doctor.availableTimes)
        ? doctor.availableTimes.join(', ')
        : doctor.availableTimes || 'Contact for availability';
    times.textContent = `Available: ${availableStr}`;

    // Append all info elements to the doctor info container
    infoDiv.append(name, specialization, email, times);

    // Create a container for card action buttons
    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'card-actions';

    // === ADMIN ROLE ACTIONS ===
    if (role === 'admin') {
        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'btn-delete';
        deleteBtn.textContent = 'Remove Doctor';

        // Add click handler for delete button
        deleteBtn.onclick = async () => {
            // Confirm before deleting (UX Best Practice)
            if (!confirm(`Are you sure you want to remove Dr. ${doctor.name}?`)) return;

            // Get the admin token from localStorage
            const token = localStorage.getItem('token');

            try {
                // Call API to delete the doctor
                const success = await deleteDoctor(doctor.id, token);

                // Show result and remove card if successful
                if (success) {
                    alert('Doctor removed successfully.');
                    card.remove(); // Instantly updates the UI
                } else {
                    alert('Failed to remove doctor. Please try again.');
                }
            } catch (error) {
                console.error("Error deleting doctor:", error);
                alert('An error occurred while connecting to the server.');
            }
        };
        // Add delete button to actions container
        actionsDiv.appendChild(deleteBtn);
    }

    // === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
    else if (role === 'patient' || !role) {
        const bookBtn = document.createElement('button');
        bookBtn.className = 'btn-book';
        bookBtn.textContent = 'Book Now';

        // Alert patient to log in before booking
        bookBtn.onclick = () => {
            alert('Please log in or sign up to book an appointment.');
            // Optional: Trigger your login modal here if you want
        };
        // Add button to actions container
        actionsDiv.appendChild(bookBtn);
    }

    // === LOGGED-IN PATIENT ROLE ACTIONS ===
    else if (role === 'loggedPatient') {
        const bookBtn = document.createElement('button');
        bookBtn.className = 'btn-book';
        bookBtn.textContent = 'Book Appointment';

        // Handle booking logic for logged-in patient
        bookBtn.onclick = async () => {
            const token = localStorage.getItem('token');

            // Redirect if token not available
            if (!token) {
                alert('Your session has expired. Please log in again.');
                window.location.href = '/';
                return;
            }

            try {
                // Fetch patient data with token
                // Assuming bookBtn is disabled or shows a loading state during fetch (optional enhancement)
                bookBtn.textContent = 'Loading...';
                bookBtn.disabled = true;

                const patientDetails = await getPatientDetails(token);

                // Show booking overlay UI with doctor and patient info
                openBookingOverlay(doctor, patientDetails);

            } catch (error) {
                console.error("Error fetching patient details:", error);
                alert("Could not initiate booking. Please try again later.");
            } finally {
                // Reset button state
                bookBtn.textContent = 'Book Appointment';
                bookBtn.disabled = false;
            }
        };
        // Add button to actions container
        actionsDiv.appendChild(bookBtn);
    }

    // Append doctor info and action buttons to the card
    card.append(infoDiv, actionsDiv);

    // Return the complete doctor card element
    return card;
};