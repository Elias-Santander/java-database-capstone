package com.project.back_end.mvc;

import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 1. Set Up the MVC Controller Class:
 * Este controlador gestiona el acceso a las vistas protegidas del Dashboard.
 */
@Controller
public class DashboardController {

    /**
     * 2. Autowire the Shared Service:
     * Inyectamos la lógica de negocio para validación de sesiones.
     */
    @Autowired
    private Service sharedService;

    /**
     * 3. Define the `adminDashboard` Method:
     * Endpoint: GET /adminDashboard/{token}
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {

        // Validamos el token para el rol "admin"
        // Asumimos que validateToken devuelve null o un String vacío si es exitoso
        String validationError = sharedService.validateToken(token, "admin");

        if (validationError == null || validationError.isEmpty()) {
            // Si es válido, retornamos la ruta de la plantilla (Thymeleaf)
            return "admin/adminDashboard";
        } else {
            // Si es inválido, redirigimos al home/login
            return "redirect:/";
        }
    }

    /**
     * 4. Define the `doctorDashboard` Method:
     * Endpoint: GET /doctorDashboard/{token}
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {

        // Validamos el token para el rol "doctor"
        String validationError = sharedService.validateToken(token, "doctor");

        if (validationError == null || validationError.isEmpty()) {
            // Forwards to the doctor view
            return "doctor/doctorDashboard";
        } else {
            // Invalid token redirect
            return "redirect:/";
        }
    }
}
