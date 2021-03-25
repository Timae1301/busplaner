package de.hsw.busplaner.dtos.haltestellenzuordnung;

import java.time.LocalTime;

import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class HaltestellenzuordnungSortierDTO extends HaltestellenzuordnungDTO
        implements Comparable<HaltestellenzuordnungSortierDTO> {

    Long haltestelleId;

    String haltestellenName;

    LocalTime uhrzeit;

    public HaltestellenzuordnungSortierDTO(Haltestellenzuordnung zuordnung) {
        super.fahrtzeit = zuordnung.getFahrtzeit();
        this.haltestelleId = zuordnung.getHaltestelle().getId();
        this.haltestellenName = zuordnung.getHaltestelle().getName();
    }

    public HaltestellenzuordnungSortierDTO(Haltestelle haltestelle) {
        this.haltestelleId = haltestelle.getId();
        this.haltestellenName = haltestelle.getName();
    }

    @Override
    public int compareTo(HaltestellenzuordnungSortierDTO o) {
        if (getUhrzeit() == null)
            return -1;
        if (o.getUhrzeit() == null)
            return 1;
        return getUhrzeit().compareTo(o.getUhrzeit());
    }
}
