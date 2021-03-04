package de.hsw.busplaner.dtos.buslinie;

import de.hsw.busplaner.beans.Buslinie;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BuslinieOutputDTO extends BuslinieDTO {

    private Long id;

    public BuslinieOutputDTO(Buslinie buslinie) {
        this.id = buslinie.getId();
        setBusnr(buslinie.getBusnr());
    }
}
