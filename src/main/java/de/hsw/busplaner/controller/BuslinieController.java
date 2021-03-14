package de.hsw.busplaner.controller;

import java.util.ArrayList;
import java.util.Comparator;

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

    @GetMapping(path = "")
    public ResponseEntity<ArrayList<BuslinieOutputDTO>> getAllBuslinie() {
        ArrayList<BuslinieOutputDTO> buslinien = service.getAllBuslinien();
        if (buslinien.isEmpty()) {
            log.warning("Es sind keine Buslinien vorhanden");
        }
        buslinien.sort(new Comparator<BuslinieOutputDTO>() {

            @Override
            public int compare(BuslinieOutputDTO o1, BuslinieOutputDTO o2) {
                return Long.compare(o1.getBusnr(), o2.getBusnr());
            }

        });
        return ResponseEntity.ok(buslinien);
    }

    @PostMapping(path = "/{busNr}")
    public ResponseEntity<Long> postBuslinie(@PathVariable Long busNr) {
        return ResponseEntity.ok(service.postBuslinie(busNr));
    }

    @PatchMapping(path = "")
    public ResponseEntity<Boolean> patchBuslinie(@RequestParam Long buslinieId, @RequestBody JsonPatch jsonPatch) {
        try {
            BuslinieOutputDTO buslinie = service.getBuslinie(buslinieId);
            buslinie = PatchUtil.applyPatch(jsonPatch, buslinie, BuslinieOutputDTO.class);
            service.patchBuslinie(buslinie);
            return ResponseEntity.ok(true);
        } catch (JsonProcessingException | JsonPatchException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{buslinieId}")
    public ResponseEntity<Boolean> deleteBuslinie(@PathVariable Long buslinieId) {
        try {
            return ResponseEntity.ok(service.deleteBuslinie(buslinieId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(true);
        }
    }
}
