package de.hsw.busplaner.dtos.fahrtstrecke;

import java.time.LocalTime;
import java.util.List;

import de.hsw.busplaner.beans.Fahrtstrecke;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FahrtstreckeMitHaltestellenDTO implements Comparable<FahrtstreckeMitHaltestellenDTO> {

    Long fahrtstreckeId;

    String fahrtstreckeName;

    LocalTime startzeitpunkt;

    String zielhaltestelle;

    List<HaltestellenzuordnungSortierDTO> haltestellen;

    public FahrtstreckeMitHaltestellenDTO(Fahrtstrecke fahrtstrecke,
            List<HaltestellenzuordnungSortierDTO> haltestellen) {
        this.fahrtstreckeId = fahrtstrecke.getId();
        this.fahrtstreckeName = fahrtstrecke.getName();
        this.haltestellen = haltestellen;
        if (!haltestellen.isEmpty()) {
            this.startzeitpunkt = haltestellen.get(0).getUhrzeit();
            this.zielhaltestelle = haltestellen.get(haltestellen.size() - 1).getHaltestellenName();
        }
    }

    @Override
    public int compareTo(FahrtstreckeMitHaltestellenDTO o) {
        if (getStartzeitpunkt() == null)
            return -1;
        if (o.getStartzeitpunkt() == null)
            return 1;
        return getStartzeitpunkt().compareTo(o.getStartzeitpunkt());
    }
}
