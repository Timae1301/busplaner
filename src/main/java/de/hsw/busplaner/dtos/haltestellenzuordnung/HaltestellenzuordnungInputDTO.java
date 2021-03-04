package de.hsw.busplaner.dtos.haltestellenzuordnung;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class HaltestellenzuordnungInputDTO extends HaltestellenzuordnungDTO {

    Long haltestelleId;

    Long naechsteHaltestelle;

    Long fahrtstreckeId;
}
