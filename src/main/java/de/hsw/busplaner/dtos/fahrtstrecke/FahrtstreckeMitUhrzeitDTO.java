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
}
