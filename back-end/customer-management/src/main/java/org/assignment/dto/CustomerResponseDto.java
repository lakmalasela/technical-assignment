package org.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDto {
    private Long id;
    private String name;
    private String nic;
    private LocalDate dateOfBirth;
    private List<String> mobileNumbers;
    private List<AddressResponseDto> addresses;
    private List<Long> familyMemberIds;
}
