package de.hsw.busplaner.controller;

import java.util.ArrayList;
import java.util.Collections;
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

import de.hsw.busplaner.dtos.buslinie.BuslinieOutputDTO;
import de.hsw.busplaner.services.BuslinieService;
import de.hsw.busplaner.util.PatchUtil;
import lombok.extern.java.Log;

/**
 * Der Controller der Buslinie stellt Endpunkte bereit unter dem Pfad
 * /api/buslinie
 */
@RestController
@Log
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/buslinie")
public class BuslinieController {

    private final BuslinieService service;

    @Autowired
    public BuslinieController(final BuslinieService service) {
        this.service = service;
    }

    /**
     * Gibt alle Buslinien für eine HaltestellenId zurück
     * 
     * @param haltestelleId
     * @return ResponseEntity mit Liste aus BuslinienOutputDTOs
     */
    @GetMapping(path = "/haltestelle/{haltestelleId}")
    public ResponseEntity<List<BuslinieOutputDTO>> getAlleBuslinienFuerHaltestelle(@PathVariable Long haltestelleId) {
        List<BuslinieOutputDTO> buslinien = new ArrayList<>();
        try {
            buslinien.addAll(service.getAlleBuslinienFuerHaltestelle(haltestelleId));
        } catch (InstanceNotFoundException e) {
            log.warning("Fehler beim Finden der Haltestellen");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(buslinien);
    }

    /**
     * Gibt alle Buslinien zurück
     * 
     * @return ResponseEntity mit Liste aus BuslinienDTOs
     */
    @GetMapping(path = "")
    public ResponseEntity<List<BuslinieOutputDTO>> getAllBuslinie() {
        List<BuslinieOutputDTO> buslinien = service.getAllBuslinien();
        if (buslinien.isEmpty()) {
            log.warning("Es sind keine Buslinien vorhanden");
        }
        Collections.sort(buslinien);
        return ResponseEntity.ok(buslinien);
    }

    /**
     * Erstellt neue Buslinie zu übergebener BusNr
     * 
     * @param busNr
     * @return ResponseEntity mit ID der neuen Buslinie
     */
    @PostMapping(path = "/{busNr}")
    public ResponseEntity<Long> postBuslinie(@PathVariable Long busNr) {
        return ResponseEntity.ok(service.postBuslinie(busNr));
    }

    /**
     * Bearbeitet Buslinie zu der übergebenen ID mit dem RequestBody
     * 
     * @param buslinieId
     * @param jsonPatch
     * @return ResponseEntity mit Boolean ob der Patch erfolgreich war
     */
    @PatchMapping(path = "")
    public ResponseEntity<Boolean> patchBuslinie(@RequestParam Long buslinieId, @RequestBody JsonPatch jsonPatch) {
        try {
            BuslinieOutputDTO buslinie = service.getBuslinieDtoById(buslinieId);
            buslinie = PatchUtil.applyPatch(jsonPatch, buslinie, BuslinieOutputDTO.class);
            service.patchBuslinie(buslinie);
            return ResponseEntity.ok(true);
        } catch (JsonProcessingException | JsonPatchException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Löscht Buslinie
     * 
     * @param buslinieId
     * @return ResponseEntity mit Boolean ob die Löschung erfolgreich war
     */
    @DeleteMapping(path = "/{buslinieId}")
    public ResponseEntity<Boolean> deleteBuslinie(@PathVariable Long buslinieId) {
        try {
            return ResponseEntity.ok(service.deleteBuslinie(buslinieId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(true);
        }
    }
}
