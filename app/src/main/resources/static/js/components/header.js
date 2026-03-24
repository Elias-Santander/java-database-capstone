/**
 * header.js
 * Dynamically renders the navigation bar based on User Role and Session Status.
 */

const renderHeader = () => {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return; // Safety check

  const path = window.location.pathname;
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // 3. Root Page Logic: Clear session and show basic header
  if (path === "/" || path.endsWith("index.html")) {
    localStorage.removeItem("userRole");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  // 6. Session Validation
  const protectedRoles = ["loggedPatient", "admin", "doctor"];
  if (protectedRoles.includes(role) && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // 5. Initialize Base Header Content
  let navContent = "";

  // 7. Role-Specific Logic
  if (role === "admin") {
    navContent = `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <button class="adminBtn" onclick="logout()">Logout</button>`;
  }
  else if (role === "doctor") {
    navContent = `
      <button class="adminBtn" onclick="window.location.href='/pages/doctorDashboard.html'">Home</button>
      <button class="adminBtn" onclick="logout()">Logout</button>`;
  }
  else if (role === "patient") {
    navContent = `
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  }
  else if (role === "loggedPatient") {
    navContent = `
      <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <button class="adminBtn" onclick="logoutPatient()">Logout</button>`;
  }

  // 10. Assemble and Render
  headerDiv.innerHTML = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav class="nav-actions">
        ${navContent}
      </nav>
    </header>`;

  // 11. Attach dynamic listeners
  attachHeaderButtonListeners();
};

/**
 * Helper Functions
 */

const attachHeaderButtonListeners = () => {
  const loginBtn = document.getElementById("patientLogin");
  const signupBtn = document.getElementById("patientSignup");

  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal('login'));
  }
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal('signup'));
  }
};

window.logout = () => {
  localStorage.clear();
  window.location.href = "/";
};

window.logoutPatient = () => {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient"); // Demote to guest patient
  window.location.href = "/pages/doctorDashboard.html"; // Or relevant landing
};

// 16. Initialize on load
document.addEventListener("DOMContentLoaded", renderHeader);