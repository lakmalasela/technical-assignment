package org.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerExcelDto {
    private String name;
    private LocalDate dateOfBirth;
    private String nic;
    private List<String> mobileNumbers;
    private List<AddressExcelDto> addresses;
    private String parentNic; // For family relationship
}



