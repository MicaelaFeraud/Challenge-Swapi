package com.swapi.challenge.vehicle.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDetailDto {
    private String id;
    private String name;
    private String model;
    private String manufacturer;
    private String vehicleClass;
}
