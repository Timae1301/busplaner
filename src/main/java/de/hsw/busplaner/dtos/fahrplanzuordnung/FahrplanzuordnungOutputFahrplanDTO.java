package de.hsw.busplaner.dtos.fahrplanzuordnung;

import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputFahrplanDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrplanzuordnungOutputFahrplanDTO extends FahrplanzuordnungDTO {

    Long id;

    FahrtstreckeOutputFahrplanDTO fahrtstrecke;

    public FahrplanzuordnungOutputFahrplanDTO(Fahrplanzuordnung fahrplanzuordnung) {
        this.id = fahrplanzuordnung.getId();
        this.fahrtstrecke = new FahrtstreckeOutputFahrplanDTO(fahrplanzuordnung.getFahrtstrecke());
        super.richtung = fahrplanzuordnung.isRichtung();
        super.startzeitpunkt = fahrplanzuordnung.getStartzeitpunkt();
    }
}
