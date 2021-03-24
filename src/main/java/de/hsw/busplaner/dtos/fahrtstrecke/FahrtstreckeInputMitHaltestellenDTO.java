package de.hsw.busplaner.dtos.fahrtstrecke;

import java.util.List;

import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrtstreckeInputMitHaltestellenDTO extends FahrtstreckeDTO {

    List<HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO> haltestellen;

    Long buslinieId;
}
