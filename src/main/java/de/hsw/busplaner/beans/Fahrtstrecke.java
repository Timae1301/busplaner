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

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "T_FAHRTSTRECKE")
public class Fahrtstrecke {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @JoinColumn(name = "busnr")
    @ManyToOne
    @JsonBackReference(value = "BuslinieFahrtstrecke")
    private Buslinie busnr;

    @OneToMany(mappedBy = "fahrtstreckeid")
    @JsonManagedReference(value = "FahrtstreckeFahrplanzuordnung")
    List<Fahrplanzuordnung> fahrplanzuordnungen;

    @OneToMany(mappedBy = "fahrtstreckeid")
    @JsonManagedReference(value = "FahrtstreckeHaltestellenzuordnung")
    List<Haltestellenzuordnung> haltestellenzuordnungen;

}
