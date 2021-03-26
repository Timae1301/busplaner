package de.hsw.busplaner.dtos.fahrtstrecke;

import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FahrtstreckeMitUhrzeitDTO {

    Long fahrtId;

    String fahrtName;

    LocalTime uhrzeit;

    public FahrtstreckeMitUhrzeitDTO(FahrtstreckeMitHaltestellenDTO fahrtstreckeMitHaltestellenDTO, LocalTime uhrzeit) {
        this.fahrtId = fahrtstreckeMitHaltestellenDTO.getFahrtstreckeId();
        this.fahrtName = fahrtstreckeMitHaltestellenDTO.getFahrtstreckeName();
        this.uhrzeit = uhrzeit;
    }
}
