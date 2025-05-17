package org.assignment.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assignment.dto.*;
import org.assignment.entity.CustomerEntity;
import org.assignment.exception.DuplicateException;
import org.assignment.exception.NotFoundException;
import org.assignment.mapper.CustomerMapper;
import org.assignment.repository.CityRepository;
import org.assignment.repository.CountryRepository;
import org.assignment.repository.CustomerRepository;
import org.assignment.service.CustomerService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public ResponseMessage<CustomerResponseDto> createCustomer(CustomerRequestDto request) {
        try {
            if (customerRepository.existsByNic(request.getNic())) {
                throw new DuplicateException("Customer with NIC " + request.getNic() + " already exists");
            }

            // Save basic customer first (no children, no parent)
            CustomerEntity customer = customerMapper.toBasicEntity(request);
            customerRepository.save(customer);

            // Assign children and parent references
            customerMapper.assignChildren(customer, request);

            // Save again to persist children
            customerRepository.save(customer);

            return new ResponseMessage<>("Customer created successfully", customerMapper.toResponse(customer));
        } catch (DuplicateException ex) {
            return new ResponseMessage<>(ex.getMessage(), null);
        } catch (Exception ex) {
            return new ResponseMessage<>("An unexpected error occurred: " + ex.getMessage(), null);
        }
    }



    @Override
    @Transactional
    public ResponseMessage<CustomerResponseDto> updateCustomer(Long id, CustomerRequestDto request) {
        try {
            CustomerEntity customer = customerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));

            // Validate NIC is unique if changed
            if (!customer.getNic().equals(request.getNic())) {
                if (customerRepository.existsByNic(request.getNic())) {
                    throw new DuplicateException("Customer with NIC " + request.getNic() + " already exists");
                }
            }

            customerMapper.updateEntity(customer, request);
            customerRepository.save(customer);

            return new ResponseMessage<>("Customer updated successfully", customerMapper.toResponse(customer));
        } catch (NotFoundException | DuplicateException ex) {
            return new ResponseMessage<>(ex.getMessage(), null);
        } catch (Exception ex) {
            return new ResponseMessage<>("An unexpected error occurred: " + ex.getMessage(), null);
        }
    }


    @Override
    public ResponseMessage<CustomerResponseDto> getCustomer(Long id) {
        try {
            CustomerEntity customer = customerRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));

            return new ResponseMessage<>("Customer retrieved successfully", customerMapper.toResponse(customer));
        } catch (NotFoundException ex) {

            throw ex;
        } catch (Exception ex) {
            throw new ServiceException("Error while retrieving customer with id: " + id, ex);
        }
    }

    @Override
    public ResponseMessage<PaginatedCustomerResponseDto> getAllCustomers(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            Page<CustomerEntity> customersPage = customerRepository.findAll(pageable);

            List<CustomerResponseDto> dtoList = customersPage.getContent().stream()
                    .map(customerMapper::toResponse)
                    .collect(Collectors.toList());

            PaginatedCustomerResponseDto response = new PaginatedCustomerResponseDto();
            response.setContent(dtoList);
            response.setPageNumber(customersPage.getNumber());
            response.setPageSize(customersPage.getSize());
            response.setTotalElements(customersPage.getTotalElements());
            response.setTotalPages(customersPage.getTotalPages());
            response.setLastPage(customersPage.isLast());

            return new ResponseMessage<>("Customers retrieved successfully", response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("Failed to retrieve customers", e);
        }
    }



    @Override
    @Transactional
    public ResponseMessage<String> bulkCreateCustomers(MultipartFile file) {
        try {
            List<CustomerExcelDto> customers = parseExcelFile(file);
            List<CustomerEntity> batch = new ArrayList<>();
            int batchSize = 1000;
            int totalCreated = 0;

            for (CustomerExcelDto dto : customers) {
                if (!customerRepository.existsByNic(dto.getNic())) {
                    CustomerEntity entity = customerMapper.excelToEntity(dto);
                    batch.add(entity);
                }

                if (batch.size() >= batchSize) {
                    customerRepository.saveAll(batch);
                    totalCreated += batch.size();
                    batch.clear(); // release memory
                }
            }

            // Save remaining
            if (!batch.isEmpty()) {
                customerRepository.saveAll(batch);
                totalCreated += batch.size();
            }

            return new ResponseMessage<>("Bulk create completed", "Created " + totalCreated + " customers");

        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file", e);
        }
    }


    @Override
    @Transactional
    public ResponseMessage<String> bulkUpdateCustomers(MultipartFile file) {
        try {
            List<CustomerExcelDto> customers = parseExcelFile(file);
            AtomicInteger updatedCount = new AtomicInteger(0);

            for (CustomerExcelDto dto : customers) {
                customerRepository.findByNic(dto.getNic())
                        .ifPresent(entity -> {
                            customerMapper.updateEntityFromExcel(entity, dto);
                            customerRepository.save(entity);
                            updatedCount.incrementAndGet();
                        });
            }

            return new ResponseMessage<>("Bulk update completed", "Updated " + updatedCount.get() + " customers");
        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file", e);
        }
    }


    private List<CustomerExcelDto> parseExcelFile(MultipartFile file) throws IOException {
        List<CustomerExcelDto> customers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row row = rows.next();
                CustomerExcelDto dto = new CustomerExcelDto();

                dto.setName(getStringValue(row.getCell(0)));
                dto.setDateOfBirth(getDateValue(row.getCell(1)));
                dto.setNic(getStringValue(row.getCell(2)));

                // Parse mobile numbers (comma separated)
                String mobiles = getStringValue(row.getCell(3));
                if (mobiles != null) {
                    dto.setMobileNumbers(Arrays.asList(mobiles.split(",")));
                }


                // Parse parent NIC
                dto.setParentNic(getStringValue(row.getCell(4)));

                // Parse addresses (assuming one address per row)
                AddressExcelDto address = new AddressExcelDto();
                address.setAddressLine1(getStringValue(row.getCell(5)));
                address.setAddressLine2(getStringValue(row.getCell(6)));
                address.setCityName(getStringValue(row.getCell(7)));
                address.setCountryName(getStringValue(row.getCell(8)));

                dto.setAddresses(Collections.singletonList(address));
                customers.add(dto);
            }
        }

        return customers;
    }


    private String getStringValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {

                    return cell.getDateCellValue().toString();
                } else {
                    // Remove decimal point for integer-like values
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    private LocalDate getDateValue(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        // attempt to parse string dates
        if (cell.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(cell.getStringCellValue());
            } catch (Exception e) {
                // ignore parse error
            }
        }

        return null;
    }

}
