package de.hsw.busplaner.dtos.fahrtstrecke;

import java.time.LocalTime;
import java.util.ArrayList;

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

    String starthaltestelle;

    ArrayList<HaltestellenzuordnungSortierDTO> haltestellen;

    public FahrtstreckeMitHaltestellenDTO(Fahrtstrecke fahrtstrecke,
            ArrayList<HaltestellenzuordnungSortierDTO> haltestellen) {
        this.fahrtstreckeId = fahrtstrecke.getId();
        this.fahrtstreckeName = fahrtstrecke.getName();
        this.haltestellen = haltestellen;
        if (!haltestellen.isEmpty()) {
            this.startzeitpunkt = haltestellen.get(0).getUhrzeit();
            this.starthaltestelle = haltestellen.get(0).getHaltestellenName();
        }

    }

    @Override
    public int compareTo(FahrtstreckeMitHaltestellenDTO o) {
        if (getStartzeitpunkt() == null || o.getStartzeitpunkt() == null)
            return 0;
        return getStartzeitpunkt().compareTo(o.getStartzeitpunkt());
    }
}
