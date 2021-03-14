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

import de.hsw.busplaner.dtos.haltestelle.HaltestelleOutputDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "T_HALTESTELLE")
public class Haltestelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "haltestelleid")
    @JsonManagedReference(value = "HaltestelleHaltestellenzuordnung")
    List<Haltestellenzuordnung> haltestellenzuordnungen;

    public Haltestelle(String name) {
        this.name = name;
    }

    public Haltestelle(HaltestelleOutputDTO haltestelleOutputDTO) {
        this.id = haltestelleOutputDTO.getId();
        this.name = haltestelleOutputDTO.getName();
    }
}
