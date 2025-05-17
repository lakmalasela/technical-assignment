package org.assignment.mapper;

import lombok.RequiredArgsConstructor;
import org.assignment.dto.*;
import org.assignment.entity.*;
import org.assignment.repository.CityRepository;
import org.assignment.repository.CountryRepository;
import org.assignment.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerMapper {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final CustomerRepository customerRepository;


public CustomerEntity toBasicEntity(CustomerRequestDto request) {
    CustomerEntity customer = new CustomerEntity();
    customer.setName(request.getName());
    customer.setDateOfBirth(request.getDateOfBirth());
    customer.setNic(request.getNic());
    return customer;
}

    public void assignChildren(CustomerEntity customer, CustomerRequestDto request) {
        if (request.getMobileNumbers() != null) {
            List<MobileNumberEntity> mobileNumbers = request.getMobileNumbers().stream()
                    .map(num -> {
                        MobileNumberEntity mobile = new MobileNumberEntity();
                        mobile.setNumber(num);
                        mobile.setCustomer(customer);
                        return mobile;
                    }).collect(Collectors.toList());
            customer.setMobileNumbers(mobileNumbers);
        }

        if (request.getAddresses() != null) {
            List<AddressEntity> addressEntities = request.getAddresses().stream()
                    .map(addr -> {
                        AddressEntity address = new AddressEntity();
                        address.setAddressLine1(addr.getAddressLine1());
                        address.setAddressLine2(addr.getAddressLine2());


                        CityEntity city = cityRepository.findByName(addr.getCityName())
                                .orElse(null);
                        address.setCity(city);

                        CountryEntity country = countryRepository.findByName(addr.getCountryName())
                                .orElse(null);
                        address.setCountry(country);

                        address.setCustomer(customer);
                        return address;
                    }).collect(Collectors.toList());
            customer.setAddresses(addressEntities);
        }

        // Parent assignment (if using self-reference)
        if (request.getParentNic() != null) {
            CustomerEntity parent = customerRepository.findByNic(request.getParentNic())
                    .orElse(null); // or throw new NotFoundException
            customer.setParent(parent);
        }
    }



    public CustomerResponseDto toResponse(CustomerEntity customer) {
        CustomerResponseDto response = new CustomerResponseDto();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setNic(customer.getNic());

        if (customer.getMobileNumbers() != null) {
            response.setMobileNumbers(customer.getMobileNumbers()
                    .stream().map(MobileNumberEntity::getNumber).collect(Collectors.toList()));
        }

        if (customer.getAddresses() != null) {
            List<AddressResponseDto> addressResponses = customer.getAddresses().stream()
                    .map(addr -> {
                        AddressResponseDto res = new AddressResponseDto();
                        res.setAddressLine1(addr.getAddressLine1());
                        res.setAddressLine2(addr.getAddressLine2());
                        res.setCityId(addr.getCity() != null ? addr.getCity().getId() : null);
                        res.setCountryId(addr.getCountry() != null ? addr.getCountry().getId() : null);

                        return res;
                    }).collect(Collectors.toList());
            response.setAddresses(addressResponses);
        }

        if (customer.getParent() != null) {
            response.setFamilyMemberIds(Collections.singletonList(customer.getParent().getId()));
        } else {
            response.setFamilyMemberIds(new ArrayList<>());
        }

        return response;
    }

public CustomerEntity excelToEntity(CustomerExcelDto dto) {
    CustomerEntity customer = new CustomerEntity();
    customer.setName(dto.getName());
    customer.setDateOfBirth(dto.getDateOfBirth());
    customer.setNic(dto.getNic());

    // Mobile Numbers
    if (dto.getMobileNumbers() != null) {
        List<MobileNumberEntity> mobiles = dto.getMobileNumbers().stream()
                .map(number -> {
                    MobileNumberEntity entity = new MobileNumberEntity();
                    entity.setNumber(number);
                    entity.setCustomer(customer);
                    return entity;
                }).collect(Collectors.toList());
        customer.setMobileNumbers(mobiles);
    }

    // Addresses
    if (dto.getAddresses() != null) {
        List<AddressEntity> addresses = new ArrayList<>();
        for (AddressExcelDto addrDto : dto.getAddresses()) {
            AddressEntity address = new AddressEntity();
            address.setAddressLine1(addrDto.getAddressLine1());
            address.setAddressLine2(addrDto.getAddressLine2());

            // Resolve city and country entities
            CityEntity city = cityRepository.findByName(addrDto.getCityName())
                    .orElse(null); // You may throw error if null
            CountryEntity country = countryRepository.findByName(addrDto.getCountryName())
                    .orElse(null);

            address.setCity(city);
            address.setCountry(country);
            address.setCustomer(customer);
            addresses.add(address);
        }
        customer.setAddresses(addresses);
    }

    // Parent linkage (optional)
    if (dto.getParentNic() != null) {
        customerRepository.findByNic(dto.getParentNic()).ifPresent(customer::setParent);
    }

    return customer;
}

    public void updateEntity(CustomerEntity entity, CustomerRequestDto request) {
        entity.setName(request.getName());
        entity.setDateOfBirth(request.getDateOfBirth());
        entity.setNic(request.getNic());

        // Update mobile numbers
        if (request.getMobileNumbers() != null) {
            entity.getMobileNumbers().clear();
            request.getMobileNumbers().forEach(num -> {
                MobileNumberEntity mobile = new MobileNumberEntity();
                mobile.setNumber(num);
                mobile.setCustomer(entity);
                entity.getMobileNumbers().add(mobile);
            });
        }

        // Update addresses
        if (request.getAddresses() != null) {
            entity.getAddresses().clear();
            request.getAddresses().forEach(addr -> {
                AddressEntity address = new AddressEntity();
                address.setAddressLine1(addr.getAddressLine1());
                address.setAddressLine2(addr.getAddressLine2());
                address.setCity(cityRepository.findById(addr.getCityId()).orElse(null));
                address.setCountry(countryRepository.findById(addr.getCountryId()).orElse(null));
                address.setCustomer(entity);
                entity.getAddresses().add(address);
            });
        }

        // Update parent
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            customerRepository.findById(request.getParentId().get(0))
                    .ifPresent(entity::setParent);
        } else {
            entity.setParent(null);
        }
    }

    public void updateEntityFromExcel(CustomerEntity entity, CustomerExcelDto dto) {
        entity.setName(dto.getName());
        entity.setDateOfBirth(dto.getDateOfBirth());

        // Update mobile numbers
        if (dto.getMobileNumbers() != null) {
            entity.getMobileNumbers().clear();
            dto.getMobileNumbers().forEach(num -> {
                MobileNumberEntity mobile = new MobileNumberEntity();
                mobile.setNumber(num);
                mobile.setCustomer(entity);
                entity.getMobileNumbers().add(mobile);
            });
        }

        // Update addresses
        if (dto.getAddresses() != null) {
            entity.getAddresses().clear();
            dto.getAddresses().forEach(addr -> {
                AddressEntity address = new AddressEntity();
                address.setAddressLine1(addr.getAddressLine1());
                address.setAddressLine2(addr.getAddressLine2());

                CityEntity city = cityRepository.findByName(addr.getCityName())
                        .orElseGet(() -> {
                            CityEntity newCity = new CityEntity();
                            newCity.setName(addr.getCityName());
                            return cityRepository.save(newCity);
                        });

                CountryEntity country = countryRepository.findByName(addr.getCountryName())
                        .orElseGet(() -> {
                            CountryEntity newCountry = new CountryEntity();
                            newCountry.setName(addr.getCountryName());
                            return countryRepository.save(newCountry);
                        });

                address.setCity(city);
                address.setCountry(country);
                address.setCustomer(entity);
                entity.getAddresses().add(address);
            });
        }

        // Update parent
        if (dto.getParentNic() != null) {
            customerRepository.findByNic(dto.getParentNic())
                    .ifPresent(entity::setParent);
        } else {
            entity.setParent(null);
        }
    }
}
