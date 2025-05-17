package org.assignment.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "mobile_numbers")
public class MobileNumberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}
