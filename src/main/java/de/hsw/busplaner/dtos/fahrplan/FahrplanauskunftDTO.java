package de.hsw.busplaner.dtos.fahrplan;

import java.util.ArrayList;

import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrplanauskunftDTO extends FahrplanDTO {

    ArrayList<FahrtstreckeMitHaltestellenDTO> fahrten;

    public FahrplanauskunftDTO(String fahrplanName, ArrayList<FahrtstreckeMitHaltestellenDTO> fahrten) {
        super.name = fahrplanName;
        this.fahrten = fahrten;
    }

}
