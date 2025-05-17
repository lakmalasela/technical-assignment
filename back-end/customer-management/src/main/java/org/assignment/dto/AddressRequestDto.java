package org.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDto {
    private String addressLine1;
    private String addressLine2;
    private Long cityId;
    private Long countryId;

    private String cityName;
    private String countryName;
}
