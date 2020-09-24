package com.hooldus.controllers;

import com.hooldus.models.ApplicationUser;
import com.hooldus.models.Maintenance;
import com.hooldus.repositories.MaintenanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;

@RestController
public class MaintenanceController {
    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceController(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @PostMapping("/maintenances")
    public void saveMaintenance (@RequestBody Maintenance maintenance) {
        maintenanceRepository.save(maintenance);
    }

    @GetMapping("/maintenances")
    public ResponseEntity<Map<String, Object>> getAllMaintenances (
            @RequestParam() String status,
            @RequestParam(required = false) ApplicationUser repairWorker,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int rpp
    ) {
        try {
            List<Maintenance> maintenances;
            Pageable paging = PageRequest.of(page, rpp);

            Page<Maintenance> paginatedMaintenances;

            if (repairWorker != null) {
                paginatedMaintenances = maintenanceRepository.findByStatusAndRepairWorker(status, repairWorker, paging);
            } else {
                paginatedMaintenances = maintenanceRepository.findByStatus(status, paging);
            }

            maintenances = paginatedMaintenances.getContent();

            if (maintenances.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", maintenances);
            response.put("page", paginatedMaintenances.getNumber());
            response.put("items", paginatedMaintenances.getTotalElements());
            response.put("pageQty", paginatedMaintenances.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/maintenances/{id}")
    public Maintenance getMaintenanceById (@PathVariable long id) {
        Optional<Maintenance> maintenance = maintenanceRepository.findById(id);

        if (maintenance.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Maintenance Not Found");

        return maintenance.get();
    }

    @PutMapping("/maintenances/{id}")
    public Maintenance updateMaintenance(@RequestBody Maintenance maintenance, @PathVariable long id) {
        Optional<Maintenance> maintenanceOptional = maintenanceRepository.findById(id);

        if (maintenanceOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Maintenance Not Found");

        maintenance.setId(id);

        return maintenanceRepository.save(maintenance);
    }
}