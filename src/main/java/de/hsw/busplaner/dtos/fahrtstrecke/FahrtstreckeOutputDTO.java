package de.hsw.busplaner.dtos.fahrtstrecke;

import de.hsw.busplaner.beans.Fahrtstrecke;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrtstreckeOutputDTO extends FahrtstreckeDTO {
    Long id;

    public FahrtstreckeOutputDTO(Fahrtstrecke fahrtstrecke) {
        this.id = fahrtstrecke.getId();
        super.name = fahrtstrecke.getName();
        super.buslinieId = fahrtstrecke.getBuslinieId().getBusnr();
    }
}
