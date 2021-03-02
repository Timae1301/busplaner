package de.hsw.busplaner.dtos;

import de.hsw.busplaner.beans.Haltestelle;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HaltestelleDTO {

    private Long id;

    private String bezeichnung;

    public HaltestelleDTO(Haltestelle haltestelle) {
        this.id = haltestelle.getId();
        this.bezeichnung = haltestelle.getBezeichnung();
    }
}
