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

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "T_FAHRPLAN")
public class Fahrplan {

    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "fahrplan")
    @JsonManagedReference(value = "FahrplanFahrplanzuordnung")
    List<Fahrplanzuordnung> fahrplanzuordnungen;

    public Fahrplan(String name) {
        this.name = name;
    }
}
