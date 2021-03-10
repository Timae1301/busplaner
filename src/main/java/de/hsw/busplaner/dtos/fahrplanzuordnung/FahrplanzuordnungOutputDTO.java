package de.hsw.busplaner.dtos.fahrplanzuordnung;

import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.dtos.fahrplan.FahrplanOutputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrplanzuordnungOutputDTO extends FahrplanzuordnungDTO {

    Long id;

    FahrtstreckeOutputDTO fahrtstrecke;

    FahrplanOutputDTO fahrplan;

    public FahrplanzuordnungOutputDTO(Fahrplanzuordnung fahrplanzuordnung) {
        this.id = fahrplanzuordnung.getId();
        this.fahrtstrecke = new FahrtstreckeOutputDTO(fahrplanzuordnung.getFahrtstreckeid());
        this.fahrplan = new FahrplanOutputDTO(fahrplanzuordnung.getFahrplanid());
        super.richtung = fahrplanzuordnung.isRichtung();
        super.startzeitpunkt = fahrplanzuordnung.getStartzeitpunkt();
    }
}
