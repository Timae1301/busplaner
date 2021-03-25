package de.hsw.busplaner.beans;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RICHTUNG")
    private boolean richtung;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Column(name = "STARTZEITPUNKT")
    private LocalTime startzeitpunkt;

    @JoinColumn(name = "fahrtstrecke")
    @ManyToOne
    @JsonBackReference(value = "FahrtstreckeFahrplanzuordnung")
    private Fahrtstrecke fahrtstrecke;

    @JoinColumn(name = "fahrplan")
    @ManyToOne
    @JsonBackReference(value = "FahrplanFahrplanzuordnung")
    private Fahrplan fahrplan;

    public Fahrplanzuordnung(FahrplanzuordnungInputDTO fahrplanzuordnungInputDTO, Fahrtstrecke fahrtstrecke,
            Fahrplan fahrplan) {
        this.richtung = fahrplanzuordnungInputDTO.isRichtung();
        this.startzeitpunkt = fahrplanzuordnungInputDTO.getStartzeitpunkt();
        this.fahrtstrecke = fahrtstrecke;
        this.fahrplan = fahrplan;
    }
}
