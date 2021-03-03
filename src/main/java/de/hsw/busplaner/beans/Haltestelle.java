package de.hsw.busplaner.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "T_HALTESTELLE")
public class Haltestelle {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "bezeichnung")
    private String bezeichnung;

    @OneToMany(mappedBy = "haltestelleid")
    @JsonManagedReference(value = "HaltestelleHaltestellenzuordnung")
    List<Haltestellenzuordnung> haltestellenzuordnungen;

    public Haltestelle(String bezeichnung) {
        this.bezeichnung = bezeichnung;
        this.haltestellenzuordnungen = new ArrayList<>();
    }
}
