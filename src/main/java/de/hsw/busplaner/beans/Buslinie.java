package de.hsw.busplaner.beans;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.hsw.busplaner.dtos.buslinie.BuslinieOutputDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "T_BUSLINIE")
public class Buslinie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BUSNR")
    private Long busnr;

    @OneToMany(mappedBy = "buslinie")
    @JsonManagedReference(value = "BuslinieFahrtstrecke")
    List<Fahrtstrecke> fahrtstrecken;

    public Buslinie(Long busnr) {
        this.busnr = busnr;
    }

    public Buslinie(BuslinieOutputDTO buslinieDTO) {
        this.id = buslinieDTO.getId();
        this.busnr = buslinieDTO.getBusnr();
    }
}
