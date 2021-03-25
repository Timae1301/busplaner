package de.hsw.busplaner.controller;

import java.time.LocalTime;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.fahrplan.FahrplanOutputDTO;
import de.hsw.busplaner.dtos.fahrplan.FahrplanauskunftDTO;
import de.hsw.busplaner.services.FahrplanService;
import lombok.extern.java.Log;

/**
 * Der Controller des Fahrplans stellt Endpunkte bereit unter dem Pfad
 * /api/fahrplan
 */
@Log
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/fahrplan")
public class FahrplanController {

    private final FahrplanService service;

    @Autowired
    public FahrplanController(final FahrplanService service) {
        this.service = service;
    }

    /**
     * Erstellt neuen Fahrplan zu übergebenem Namen
     * 
     * @param name
     * @return ResponseEntity mit ID des neuen Fahrplans
     */
    @PostMapping(path = "/{name}")
    public ResponseEntity<Long> postFahrplan(@PathVariable String name) {
        return ResponseEntity.ok(service.postFahrplan(name));
    }

    /**
     * Gibt alle Fahrpläne zurück
     * 
     * @return ResponseEntity mit Liste aus FahrplanOutputDTOs
     */
    @GetMapping(path = "")
    public ResponseEntity<List<FahrplanOutputDTO>> getAlleFahrplaene() {
        List<FahrplanOutputDTO> fahrplaene = service.getAlleFahrplaene();
        if (fahrplaene.isEmpty()) {
            log.warning("Es sind keine Fahrplaene vorhanden");
        }
        return ResponseEntity.ok(fahrplaene);
    }

    /**
     * Löscht Fahrplan zu übergebener ID
     * 
     * @param fahrplanId
     * @return ResponseEntity mit Boolean ob die Löschung erfolgreich war
     */
    @DeleteMapping(path = "/{fahrplanId}")
    public ResponseEntity<Boolean> deleteFahrplan(@PathVariable Long fahrplanId) {
        service.deleteFahrplan(fahrplanId);
        return ResponseEntity.ok(true);
    }

    /**
     * Gibt die Fahrplanauskunft der FahrplanID innerhalb der eingestellten
     * Zeitspanne aus
     * 
     * @param fahrplanId
     * @param zeitpunktVorher
     * @param zeitpunktNachher
     * @return ResponseEntity mit FahrplanauskunftDTO
     */
    @GetMapping(path = "/auskunft/{fahrplanId}")
    public ResponseEntity<FahrplanauskunftDTO> getFahrplanauskunft(@PathVariable Long fahrplanId,
            @RequestParam LocalTime zeitpunktVorher, @RequestParam LocalTime zeitpunktNachher) {
        try {
            FahrplanauskunftDTO fahrplanauskunft = service.getFahrplanauskunft(fahrplanId, zeitpunktVorher,
                    zeitpunktNachher);
            return ResponseEntity.ok(fahrplanauskunft);
        } catch (InstanceNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
