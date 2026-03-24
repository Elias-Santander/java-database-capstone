/**
 * doctorServices.js
 * Service layer for handling all Doctor-related API interactions.
 */

// 1. Import the base API URL from the config file
import { BASE_URL } from './config.js';

// Define a constant DOCTOR_API to hold the full endpoint
const DOCTOR_API = `${BASE_URL}/api/doctors`;

/**
 * Function: getDoctors
 * Purpose: Fetch the list of all doctors from the API
 */
export const getDoctors = async () => {
    try {
        const response = await fetch(`${DOCTOR_API}/all`);
        const data = await response.json();
        // Return the 'doctors' array from the response
        return data.doctors || [];
    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
};

/**
 * Function: deleteDoctor
 * Purpose: Delete a specific doctor using their ID and an authentication token
 */
export const deleteDoctor = async (doctorId, token) => {
    try {
        // The URL includes the doctor ID and token as path parameters per instructions
        const response = await fetch(`${DOCTOR_API}/delete/${doctorId}/${token}`, {
            method: 'DELETE'
        });

        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Doctor deletion processed."
        };
    } catch (error) {
        console.error("Error deleting doctor:", error);
        return { success: false, message: "Network error occurred during deletion." };
    }
};

/**
 * Function: saveDoctor
 * Purpose: Save (create) a new doctor using a POST request
 */
export const saveDoctor = async (doctorObject, token) => {
    try {
        const response = await fetch(`${DOCTOR_API}/save/${token}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctorObject)
        });

        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Doctor saved successfully."
        };
    } catch (error) {
        console.error("Error saving doctor:", error);
        return { success: false, message: "Could not save doctor due to server error." };
    }
};

/**
 * Function: filterDoctors
 * Purpose: Fetch doctors based on filtering criteria
 */
export const filterDoctors = async (name, time, specialty) => {
    // Handling empty strings for path parameters to ensure valid URL structure
    const searchName = name || "all";
    const searchTime = time || "all";
    const searchSpecialty = specialty || "all";

    try {
        const response = await fetch(`${DOCTOR_API}/filter/${searchName}/${searchTime}/${searchSpecialty}`);

        if (response.ok) {
            const data = await response.json();
            return data; // Expected to contain { doctors: [...] }
        } else {
            console.error("Filter request failed with status:", response.status);
            return { doctors: [] };
        }
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("There was an issue filtering the doctor list.");
        return { doctors: [] };
    }
};