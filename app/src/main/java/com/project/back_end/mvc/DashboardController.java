package com.project.back_end.mvc;

import com.project.back_end.services.SharedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private SharedService sharedService;

    /**
     * 3. Define the `adminDashboard` Method:
     * Corregido para manejar ResponseEntity
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {

        // Llamamos al servicio y recibimos el ResponseEntity
        ResponseEntity<Map<String, String>> response = sharedService.validateToken(token, "admin");

        // Verificamos si el status es 200 OK
        if (response.getStatusCode() == HttpStatus.OK) {
            // Si es válido, retornamos la ruta de la plantilla (Thymeleaf)
            return "admin/adminDashboard";
        } else {
            // Si es 401 Unauthorized o cualquier otro error, redirigimos
            return "redirect:/";
        }
    }

    /**
     * 4. Define the `doctorDashboard` Method:
     * Corregido para manejar ResponseEntity
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {

        // Llamamos al servicio y recibimos el ResponseEntity
        ResponseEntity<Map<String, String>> response = sharedService.validateToken(token, "doctor");

        if (response.getStatusCode() == HttpStatus.OK) {
            // Forwards to the doctor view
            return "doctor/doctorDashboard";
        } else {
            // Invalid token redirect
            return "redirect:/";
        }
    }
}