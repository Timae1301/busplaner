package de.hsw.busplaner.dtos.fahrplanzuordnung;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class FahrplanzuordnungDTO {

    boolean richtung;

    Long startzeitpunkt;
}
