package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.SharedService; // Asegúrate de que el nombre coincida con tu clase
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 1. **Set Up the Controller Class**
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    // 2. **Autowire Service Dependency (Constructor Injection)**
    private final SharedService service;

    public AdminController(SharedService service) {
        this.service = service;
    }

    /**
     * 3. **Define the adminLogin Method**
     * Endpoint: POST /api/v1/admin/login (dependiendo de tu api.path)
     * Maneja la autenticación del administrador y devuelve un token JWT si es exitoso.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        // Delegamos la validación al método validateAdmin del SharedService
        return service.validateAdmin(admin);
    }
}
