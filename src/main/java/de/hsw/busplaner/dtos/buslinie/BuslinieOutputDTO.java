package de.hsw.busplaner.dtos.buslinie;

import de.hsw.busplaner.beans.Buslinie;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BuslinieOutputDTO extends BuslinieDTO implements Comparable<BuslinieOutputDTO> {

    private Long id;

    private boolean loeschbar;

    public BuslinieOutputDTO(Buslinie buslinie) {
        this.id = buslinie.getId();
        super.busnr = buslinie.getBusnr();
    }

    @Override
    public int compareTo(BuslinieOutputDTO o) {
        return Long.compare(getBusnr(), o.getBusnr());
    }
}
