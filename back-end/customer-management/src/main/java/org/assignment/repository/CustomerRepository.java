package org.assignment.repository;

import org.assignment.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    boolean existsByNic(String nic);
    Optional<CustomerEntity> findByNic(String nic);
}
