package de.hsw.busplaner.dtos.fahrtstrecke;

import java.util.List;

import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrtstreckeOutputFahrplanDTO extends FahrtstreckeDTO {

    Long id;

    List<Haltestellenzuordnung> haltestellenzuordnungen;

    boolean loeschbar;

    Long buslinie;

    public FahrtstreckeOutputFahrplanDTO(Fahrtstrecke fahrtstrecke) {
        this.id = fahrtstrecke.getId();
        this.haltestellenzuordnungen = fahrtstrecke.getHaltestellenzuordnungen();
        super.name = fahrtstrecke.getName();
        this.buslinie = fahrtstrecke.getBuslinie().getBusnr();
    }
}
