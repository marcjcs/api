package com.hooldus.repositories;

import com.hooldus.models.ApplicationUser;
import com.hooldus.models.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    Page<Maintenance> findByStatus(String status, Pageable page);
    Page<Maintenance> findByStatusAndRepairWorker(String status, ApplicationUser repairWorker, Pageable page);
}