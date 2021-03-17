package de.hsw.busplaner.dtos.fahrplanzuordnung;

import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class FahrplanzuordnungDTO {

    boolean richtung;

    LocalTime startzeitpunkt;
}
