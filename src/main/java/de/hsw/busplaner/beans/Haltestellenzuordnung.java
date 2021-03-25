package de.hsw.busplaner.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungInputDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "T_HALTESTELLENZUORDNUNG")
public class Haltestellenzuordnung {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FAHRTZEIT")
    private Long fahrtzeit;

    @Column(name = "NAECHSTE_HALTESTELLE")
    private Long naechsteHaltestelle;

    @JoinColumn(name = "haltestelle")
    @ManyToOne
    @JsonBackReference(value = "HaltestelleHaltestellenzuordnung")
    private Haltestelle haltestelle;

    @JoinColumn(name = "fahrtstrecke")
    @ManyToOne
    @JsonBackReference(value = "FahrtstreckeHaltestellenzuordnung")
    private Fahrtstrecke fahrtstrecke;

    public Haltestellenzuordnung(HaltestellenzuordnungInputDTO haltestellenzuordnungInputDTO, Fahrtstrecke fahrtstrecke,
            Haltestelle haltestelle) {
        this.fahrtzeit = haltestellenzuordnungInputDTO.getFahrtzeit();
        this.fahrtstrecke = fahrtstrecke;
        this.haltestelle = haltestelle;
        this.naechsteHaltestelle = haltestellenzuordnungInputDTO.getNaechsteHaltestelle();
    }
}
