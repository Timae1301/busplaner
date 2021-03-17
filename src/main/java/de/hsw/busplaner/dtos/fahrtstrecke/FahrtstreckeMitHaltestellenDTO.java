package de.hsw.busplaner.dtos.fahrtstrecke;

import java.util.ArrayList;

import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FahrtstreckeMitHaltestellenDTO {

    Long fahrtstreckeId;

    String fahrtstreckeName;

    ArrayList<HaltestellenzuordnungSortierDTO> haltestellen;

    public FahrtstreckeMitHaltestellenDTO(Fahrtstrecke fahrtstrecke,
            ArrayList<HaltestellenzuordnungSortierDTO> haltestellen) {
        this.fahrtstreckeId = fahrtstrecke.getId();
        this.fahrtstreckeName = fahrtstrecke.getName();
        this.haltestellen = haltestellen;
    }
}
