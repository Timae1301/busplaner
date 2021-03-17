package de.hsw.busplaner.dtos.fahrtstrecke;

import java.util.ArrayList;

import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrtstreckeInputMitHaltestellenDTO extends FahrtstreckeDTO {

    ArrayList<HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO> haltestellen;

    Long buslinieId;
}
