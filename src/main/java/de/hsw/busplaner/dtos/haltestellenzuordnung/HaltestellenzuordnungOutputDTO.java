package de.hsw.busplaner.dtos.haltestellenzuordnung;

import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.dtos.haltestelle.HaltestelleOutputDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class HaltestellenzuordnungOutputDTO extends HaltestellenzuordnungDTO {

    Long id;

    HaltestelleOutputDTO naechsteHaltestelle;

    HaltestelleOutputDTO haltestelle;

    FahrtstreckeOutputDTO fahrtstrecke;

    public HaltestellenzuordnungOutputDTO(Haltestellenzuordnung zuordnung, Haltestelle naechsteHaltestelle) {
        this.id = zuordnung.getId();
        this.haltestelle = new HaltestelleOutputDTO(zuordnung.getHaltestelle());
        this.fahrtstrecke = new FahrtstreckeOutputDTO(zuordnung.getFahrtstrecke());
        this.naechsteHaltestelle = new HaltestelleOutputDTO(naechsteHaltestelle);
        super.fahrtzeit = zuordnung.getFahrtzeit();
    }
}
