package org.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto {
    private String addressLine1;
    private String addressLine2;
    private Long cityId;
    private Long countryId;
}
