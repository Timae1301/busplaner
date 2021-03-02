package de.hsw.busplaner.beans;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "T_BUSLINIE")
public class Buslinie {

    @Column(name = "BUSNR")
    @Id
    private Long busnr;

    @OneToMany(mappedBy = "busnr")
    @JsonManagedReference(value = "BuslinieFahrtstrecke")
    List<Fahrtstrecke> fahrtstrecken;

    public Buslinie(Long busnr) {
        this.busnr = busnr;
    }
}
