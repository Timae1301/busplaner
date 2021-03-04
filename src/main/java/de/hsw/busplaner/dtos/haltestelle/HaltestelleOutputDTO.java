package de.hsw.busplaner.dtos.haltestelle;

import de.hsw.busplaner.beans.Haltestelle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class HaltestelleOutputDTO extends HaltestelleDTO {

    Long id;

    public HaltestelleOutputDTO(Haltestelle haltestelle) {
        this.id = haltestelle.getId();
        super.setName(haltestelle.getName());
    }

}
