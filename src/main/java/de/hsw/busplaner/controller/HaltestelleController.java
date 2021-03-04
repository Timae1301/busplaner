package de.hsw.busplaner.controller;

import java.util.ArrayList;

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

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<HaltestelleOutputDTO>> getAlleHaltestellen() {
        ArrayList<HaltestelleOutputDTO> haltestellen = service.getAllHaltestellen();
        if (haltestellen.isEmpty()) {
            log.warning("Es sind keine Haltestellen vorhanden");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(haltestellen);

    }

    @PostMapping(path = "/{haltestelleName}")
    public ResponseEntity<Long> postHaltestelle(@PathVariable String haltestelleName) {
        return ResponseEntity.ok(service.postHaltestelle(haltestelleName));
    }

    @PatchMapping(path = "")
    public ResponseEntity<Boolean> patchHaltestelle(@RequestParam Long haltestelleId,
            @RequestBody JsonPatch jsonPatch) {
        try {
            HaltestelleOutputDTO haltestelle = service.getHaltestelle(haltestelleId);
            haltestelle = PatchUtil.applyPatch(jsonPatch, haltestelle, HaltestelleOutputDTO.class);
            service.patchHaltestelle(haltestelle);
            return ResponseEntity.ok(true);
        } catch (JsonProcessingException | JsonPatchException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{haltestelleId}")
    public ResponseEntity<Boolean> deleteHaltestelle(@PathVariable Long haltestelleId) {
        try {
            return ResponseEntity.ok(service.deleteHaltestelle(haltestelleId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(true);
        }
    }
}
