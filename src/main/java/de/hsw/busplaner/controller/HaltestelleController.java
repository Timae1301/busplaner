package de.hsw.busplaner.controller;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.haltestelle.HaltestelleOutputDTO;
import de.hsw.busplaner.services.HaltestelleService;
import de.hsw.busplaner.util.PatchUtil;
import lombok.extern.java.Log;

/**
 * Der Controller der Haltestelle stellt Endpunkte bereit unter dem Pfad
 * /api/haltestelle
 */
@RestController
@Log
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/haltestelle")
public class HaltestelleController {

    private final HaltestelleService service;

    @Autowired
    public HaltestelleController(final HaltestelleService service) {
        this.service = service;
    }

    /**
     * Gibt alle Haltestellen aus wahlweise zu einer übergebenen BuslinienID
     * 
     * @param buslinieId
     * @return ResponseEntity mit Liste aus HaltestelleOutputDTOs
     */
    @GetMapping(path = "")
    public ResponseEntity<List<HaltestelleOutputDTO>> getAlleHaltestellen(
            @RequestParam(required = false) Long buslinieId) {
        List<HaltestelleOutputDTO> haltestellen = new ArrayList<>();
        if (buslinieId == null) {
            haltestellen = service.getAllHaltestellen();
        } else {
            try {
                haltestellen = service.getAlleHaltestellenZuBuslinieId(buslinieId);
            } catch (InstanceNotFoundException e) {
                log.warning("Fehler beim Auslesen der Fahrten fuer die Haltestellen");
            } catch (IllegalArgumentException e) {
                log.warning(String.format("Keine Buslinie zu ID %s gefunden", buslinieId));
            }
        }
        if (haltestellen.isEmpty()) {
            log.warning("Es sind keine Haltestellen vorhanden");
        }
        return ResponseEntity.ok(haltestellen);
    }

    /**
     * Erstellt eine Haltestelle anhand des übergebenen Namens
     * 
     * @param haltestelleName
     * @return ResponseEntity mit der ID der neuen Haltestelle
     */
    @PostMapping(path = "/{haltestelleName}")
    public ResponseEntity<Long> postHaltestelle(@PathVariable String haltestelleName) {
        return ResponseEntity.ok(service.postHaltestelle(haltestelleName));
    }

    /**
     * Bearbeitet die Haltestelle zu der ID anhand des übergebenen Bodys in Form
     * eines JSON-Patches
     * 
     * @param haltestelleId
     * @param jsonPatch
     * @return ResponseEntity mit Boolean ob das Bearbeiten erfolgreich war
     */
    @PatchMapping(path = "")
    public ResponseEntity<Boolean> patchHaltestelle(@RequestParam Long haltestelleId,
            @RequestBody JsonPatch jsonPatch) {
        try {
            HaltestelleOutputDTO haltestelle = service.getHaltestelleDtoById(haltestelleId);
            haltestelle = PatchUtil.applyPatch(jsonPatch, haltestelle, HaltestelleOutputDTO.class);
            service.patchHaltestelle(haltestelle);
            return ResponseEntity.ok(true);
        } catch (JsonProcessingException | JsonPatchException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Löscht eine Haltestelle anhand der übergebenen ID
     * 
     * @param haltestelleId
     * @return ResponseEntity mit Boolean ob das Bearbeiten erfolgreich war
     */
    @DeleteMapping(path = "/{haltestelleId}")
    public ResponseEntity<Boolean> deleteHaltestelle(@PathVariable Long haltestelleId) {
        try {
            return ResponseEntity.ok(service.deleteHaltestelle(haltestelleId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(true);
        }
    }
}
