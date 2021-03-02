package de.hsw.busplaner.dtos;

import de.hsw.busplaner.beans.Buslinie;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuslinieDTO {
    
    private long busnr;

    public BuslinieDTO (Buslinie buslinie){
        this.busnr = buslinie.getBusnr();
    }
}
