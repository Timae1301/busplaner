package de.hsw.busplaner.dtos.fahrplan;

import java.util.List;

import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrplanauskunftDTO extends FahrplanDTO {

    List<FahrtstreckeMitHaltestellenDTO> fahrten;

    public FahrplanauskunftDTO(String fahrplanName, List<FahrtstreckeMitHaltestellenDTO> fahrten) {
        super.name = fahrplanName;
        this.fahrten = fahrten;
    }

}
