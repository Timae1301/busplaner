package de.hsw.busplaner.dtos.fahrtstrecke;

import java.util.List;

import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrtstreckeOutputDTO extends FahrtstreckeDTO {
    Long id;

    List<Fahrplanzuordnung> fahrplanzuordnungen;

    List<Haltestellenzuordnung> haltestellenzuordnungen;

    boolean loeschbar;

    public FahrtstreckeOutputDTO(Fahrtstrecke fahrtstrecke) {
        this.id = fahrtstrecke.getId();
        this.fahrplanzuordnungen = fahrtstrecke.getFahrplanzuordnungen();
        this.haltestellenzuordnungen = fahrtstrecke.getHaltestellenzuordnungen();
        super.name = fahrtstrecke.getName();
        super.buslinieId = fahrtstrecke.getBuslinieId().getBusnr();
    }
}
