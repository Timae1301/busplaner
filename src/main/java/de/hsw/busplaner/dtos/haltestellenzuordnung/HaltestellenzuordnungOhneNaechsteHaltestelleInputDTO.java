package de.hsw.busplaner.dtos.haltestellenzuordnung;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO extends HaltestellenzuordnungDTO {
    Long haltestelleId;
}
