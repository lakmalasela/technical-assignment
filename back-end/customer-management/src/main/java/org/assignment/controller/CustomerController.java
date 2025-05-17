package org.assignment.controller;

import org.assignment.dto.CustomerRequestDto;
import org.assignment.dto.CustomerResponseDto;
import org.assignment.dto.PaginatedCustomerResponseDto;
import org.assignment.dto.ResponseMessage;
import org.assignment.exception.NotFoundException;
import org.assignment.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ResponseMessage<CustomerResponseDto>> createCustomer(@RequestBody CustomerRequestDto request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage<CustomerResponseDto>> updateCustomer(
            @PathVariable Long id, @RequestBody CustomerRequestDto request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<CustomerResponseDto>> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @GetMapping
    public ResponseEntity<ResponseMessage<PaginatedCustomerResponseDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ResponseMessage<PaginatedCustomerResponseDto> response = customerService.getAllCustomers(page, size);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage<>(e.getMessage(), null));
        }
    }


    @PostMapping("/bulk-create")
    public ResponseEntity<ResponseMessage<String>> bulkCreateCustomers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(customerService.bulkCreateCustomers(file));
    }

    @PostMapping("/bulk-update")
    public ResponseEntity<ResponseMessage<String>> bulkUpdateCustomers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(customerService.bulkUpdateCustomers(file));
    }

}
