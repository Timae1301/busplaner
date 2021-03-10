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

import de.hsw.busplaner.dtos.fahrplanzuordnung.FahrplanzuordnungInputDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "T_FAHRPLANZUORDNUNG")
public class Fahrplanzuordnung {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "RICHTUNG")
    private boolean richtung;

    @Column(name = "STARTZEITPUNKT")
    private Long startzeitpunkt;

    @JoinColumn(name = "fahrtstreckeid")
    @ManyToOne
    @JsonBackReference(value = "FahrtstreckeFahrplanzuordnung")
    private Fahrtstrecke fahrtstreckeid;

    @JoinColumn(name = "fahrplanid")
    @ManyToOne
    @JsonBackReference(value = "FahrplanFahrplanzuordnung")
    private Fahrplan fahrplanid;

    public Fahrplanzuordnung(FahrplanzuordnungInputDTO fahrplanzuordnungInputDTO, Fahrtstrecke fahrtstrecke,
            Fahrplan fahrplan) {
        this.richtung = fahrplanzuordnungInputDTO.isRichtung();
        this.startzeitpunkt = fahrplanzuordnungInputDTO.getStartzeitpunkt();
        this.fahrtstreckeid = fahrtstrecke;
        this.fahrplanid = fahrplan;
    }
}
