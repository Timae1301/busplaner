package de.hsw.busplaner.dtos.haltestellenzuordnung;

import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class HaltestellenzuordnungSortierDTO extends HaltestellenzuordnungDTO {

    Long haltestelleId;

    String haltestellenName;

    public HaltestellenzuordnungSortierDTO(Haltestellenzuordnung zuordnung) {
        super.fahrtzeit = zuordnung.getFahrtzeit();
        this.haltestelleId = zuordnung.getHaltestelleid().getId();
        this.haltestellenName = zuordnung.getHaltestelleid().getName();
    }

    public HaltestellenzuordnungSortierDTO(Haltestelle haltestelle) {
        this.haltestelleId = haltestelle.getId();
        this.haltestellenName = haltestelle.getName();
    }
}
