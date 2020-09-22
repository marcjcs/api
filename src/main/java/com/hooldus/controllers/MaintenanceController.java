package com.hooldus.controllers;

import com.hooldus.models.Maintenance;
import com.hooldus.repositories.MaintenanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

@RestController
public class MaintenanceController {
    private MaintenanceRepository maintenanceRepository;

    public MaintenanceController(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @PostMapping("/maintenance")
    public void getMaintenances(@RequestBody Maintenance maintenance) {
        maintenanceRepository.save(maintenance);
    }

    @GetMapping("/maintenance")
    public List<Maintenance> getMaintenances () {
        return maintenanceRepository.findAll();
    }

    @GetMapping("/maintenance/{id}")
    public Maintenance getMaintenanceById (@PathVariable long id) {
        Optional<Maintenance> maintenance = maintenanceRepository.findById(id);

        if (!maintenance.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Maintenance Not Found");

        return maintenance.get();
    }

    @PutMapping("/maintenance/{id}")
    public ResponseEntity<Object> updateMaintenance(@RequestBody Maintenance maintenance, @PathVariable long id) {

        Optional<Maintenance> maintenanceOptional = maintenanceRepository.findById(id);

        if (!maintenanceOptional.isPresent())
            return ResponseEntity.notFound().build();

        maintenance.setId(id);
        
        maintenanceRepository.save(maintenance);

        return ResponseEntity.noContent().build();
    }
}