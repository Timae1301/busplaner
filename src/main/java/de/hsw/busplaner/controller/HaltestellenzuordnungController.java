package de.hsw.busplaner.controller;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungInputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.services.HaltestellenzuordnungService;
import lombok.extern.java.Log;

/**
 * Der Controller der Haltestellenzuordnung stellt Endpunkte bereit unter dem
 * Pfad /api/haltestellenzuordnung
 */
@Log
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/haltestellenzuordnung")
public class HaltestellenzuordnungController {

    private final HaltestellenzuordnungService service;

    @Autowired
    public HaltestellenzuordnungController(final HaltestellenzuordnungService service) {
        this.service = service;
    }

    /**
     * Erstellt eine meherere Haltestellenzuordnungen anhand einer Liste an
     * HaltestellenzuordnungInputDTOs
     * 
     * @param haltestellenzuordnungInputDTOs
     * @return ResponseEntity mit Liste an IDs der neuen Haltestellenzuordnungen
     */
    @PostMapping(path = "")
    public ResponseEntity<List<Long>> postZuordnungen(
            @RequestBody List<HaltestellenzuordnungInputDTO> haltestellenzuordnungInputDTOs) {
        List<Long> ids = new ArrayList<>();
        for (HaltestellenzuordnungInputDTO haltestellenzuordnungInputDTO : haltestellenzuordnungInputDTOs) {
            try {
                ids.add(service.postHaltestellenzuordnug(haltestellenzuordnungInputDTO));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.notFound().build();
            }
        }
        if (ids.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ids);
    }

    /**
     * Gibt alle Haltestellenzuordnungen zurück
     * 
     * @return ResponseEntity mit Liste aus HaltestellenzuordnungOutputDTOs
     */
    @GetMapping(path = "")
    public ResponseEntity<List<HaltestellenzuordnungOutputDTO>> getAlleZuordnungen() {
        List<HaltestellenzuordnungOutputDTO> zuordnungen = service.getAlleZuordnungen();
        if (zuordnungen.isEmpty()) {
            log.warning("Es sind keine Haltestellenzuordnungen vorhanden");
        }
        return ResponseEntity.ok(zuordnungen);
    }

    /**
     * Gibt alle Haltestellenzuordnungen zu einer Fahrtstrecke sortiert mit Uhrzeit
     * aus
     * 
     * @param fahrtstreckeId
     * @return ResponseEntity mit Liste aus HaltestellenzuordnungSortierDTOs
     */
    @GetMapping(path = "/{fahrtstreckeId}/sort")
    public ResponseEntity<List<HaltestellenzuordnungSortierDTO>> getAlleZuordnungenSorted(
            @PathVariable Long fahrtstreckeId) {
        List<HaltestellenzuordnungSortierDTO> zuordnungen = new ArrayList<>();
        try {
            zuordnungen = service.getAlleZuordnungenSorted(fahrtstreckeId);
        } catch (InstanceNotFoundException e) {
            log.warning("Problem beim sortieren");
        }
        if (zuordnungen.isEmpty()) {
            log.warning("Es sind keine Haltestellenzuordnungen vorhanden");
        }
        return ResponseEntity.ok(zuordnungen);
    }
}
