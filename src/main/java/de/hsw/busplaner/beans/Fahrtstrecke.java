package de.hsw.busplaner.beans;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "T_FAHRTSTRECKE")
public class Fahrtstrecke {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @JoinColumn(name = "buslinie")
    @ManyToOne
    @JsonBackReference(value = "BuslinieFahrtstrecke")
    private Buslinie buslinie;

    @OneToMany(mappedBy = "fahrtstrecke")
    @JsonManagedReference(value = "FahrtstreckeFahrplanzuordnung")
    List<Fahrplanzuordnung> fahrplanzuordnungen;

    @OneToMany(mappedBy = "fahrtstrecke")
    @JsonManagedReference(value = "FahrtstreckeHaltestellenzuordnung")
    List<Haltestellenzuordnung> haltestellenzuordnungen;

    public Fahrtstrecke(FahrtstreckeInputDTO fahrtstreckeInputDTO, Buslinie buslinie) {
        this.name = fahrtstreckeInputDTO.getName();
        this.buslinie = buslinie;
    }
}
