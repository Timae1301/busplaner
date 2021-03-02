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

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "T_HALTESTELLENZUORDNUNG")
public class Haltestellenzuordnung {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "FAHRTZEIT")
    private Long fahrtzeit;

    @Column(name = "NAECHSTE_HALTESTELLE")
    private Long naechsteHaltestelle;

    @JoinColumn(name = "haltestelleid")
    @ManyToOne
    @JsonBackReference(value = "HaltestelleHaltestellenzuordnung")
    private Haltestelle haltestelleid;

    @JoinColumn(name = "fahrtstreckeid")
    @ManyToOne
    @JsonBackReference(value = "FahrtstreckeHaltestellenzuordnung")
    private Fahrtstrecke fahrtstreckeid;
}
