package org.assignment.service;

import org.assignment.dto.CustomerRequestDto;
import org.assignment.dto.CustomerResponseDto;
import org.assignment.dto.PaginatedCustomerResponseDto;
import org.assignment.dto.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    ResponseMessage<CustomerResponseDto> createCustomer(CustomerRequestDto request);
    ResponseMessage<CustomerResponseDto> updateCustomer(Long id, CustomerRequestDto request);
    ResponseMessage<CustomerResponseDto> getCustomer(Long id);
    ResponseMessage<PaginatedCustomerResponseDto> getAllCustomers(int page, int size);    ResponseMessage<String> bulkCreateCustomers(MultipartFile file);
    ResponseMessage<String> bulkUpdateCustomers(MultipartFile file);
}
