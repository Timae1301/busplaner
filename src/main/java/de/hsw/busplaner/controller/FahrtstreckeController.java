package de.hsw.busplaner.controller;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeInputMitHaltestellenDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitUhrzeitDTO;
import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeOutputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungInputDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO;
import de.hsw.busplaner.services.FahrtstreckeService;
import lombok.extern.java.Log;

/**
 * Der Controller der Fahrtstrecke stellt Endpunkte bereit unter dem Pfad
 * /api/fahrtstrecke
 */
@Log
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/fahrtstrecke")
public class FahrtstreckeController {

    private final FahrtstreckeService service;

    private final HaltestellenzuordnungController haltestellenzuordnungController;

    @Autowired
    public FahrtstreckeController(final FahrtstreckeService service,
            final HaltestellenzuordnungController haltestellenzuordnungController) {
        this.service = service;
        this.haltestellenzuordnungController = haltestellenzuordnungController;
    }

    /**
     * Erstellt neue Fahrtstrecke ohne Haltestellen anhand des übergebenen
     * FahrtstreckeInputDTOs
     * 
     * @param fahrtstreckeInputDTO
     * @return ResponseEntity mit ID der neuen Fahrtstrecke
     */
    @PostMapping(path = "")
    public ResponseEntity<Long> postFahrtstrecke(@RequestBody FahrtstreckeInputDTO fahrtstreckeInputDTO) {
        try {
            return ResponseEntity.ok(service.postFahrtstrecke(fahrtstreckeInputDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Erstellt neue Fahrtstrecke mit übergebenen Haltestellen aus dem RequestBody
     * 
     * @param fahrtstreckeInputMitHaltestellenDTO
     * @return ResponseEntity mit ID der neuen Fahrtstrecke
     */
    @PostMapping(path = "/haltestellen")
    public ResponseEntity<Long> postFahrtstreckeMitHaltestellen(
            @RequestBody FahrtstreckeInputMitHaltestellenDTO fahrtstreckeInputMitHaltestellenDTO) {
        try {
            Long fahrtstreckeId = service
                    .postFahrtstrecke(genNewFahrtstreckeInputDTO(fahrtstreckeInputMitHaltestellenDTO));
            List<HaltestellenzuordnungInputDTO> haltestellenzuordnungInputDTOs = genHaltestellenzuordnungInputDTOs(
                    fahrtstreckeInputMitHaltestellenDTO, fahrtstreckeId);
            haltestellenzuordnungController.postZuordnungen(haltestellenzuordnungInputDTOs);
            return ResponseEntity.ok(fahrtstreckeId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Erstellt HaltestellenzuordnungInputDTO Liste aus übergebenem
     * FahrtstreckeInputMitHaltestellenDTO und FahrtstreckenID
     * 
     * @param fahrtstreckeInputMitHaltestellenDTO
     * @param fahrtstreckeId
     * @return Liste aus HaltestellenzuordnungInputDTOs
     */
    private List<HaltestellenzuordnungInputDTO> genHaltestellenzuordnungInputDTOs(
            FahrtstreckeInputMitHaltestellenDTO fahrtstreckeInputMitHaltestellenDTO, Long fahrtstreckeId) {
        List<HaltestellenzuordnungInputDTO> zuordnungDtos = new ArrayList<>();

        // durchläuft die angegebenen Haltestellen und baut daraus das
        // HaltestellenzuordnungInputDTO
        for (int i = 0; i < fahrtstreckeInputMitHaltestellenDTO.getHaltestellen().size() - 1; i++) {
            HaltestellenzuordnungOhneNaechsteHaltestelleInputDTO haltestelle = fahrtstreckeInputMitHaltestellenDTO
                    .getHaltestellen().get(i);
            HaltestellenzuordnungInputDTO zuordnung = new HaltestellenzuordnungInputDTO();
            zuordnung.setFahrtstreckeId(fahrtstreckeId);
            zuordnung.setHaltestelleId(haltestelle.getHaltestelleId());
            zuordnung.setFahrtzeit(haltestelle.getFahrtzeit());
            zuordnung.setNaechsteHaltestelle(
                    fahrtstreckeInputMitHaltestellenDTO.getHaltestellen().get(i + 1).getHaltestelleId());
            zuordnungDtos.add(zuordnung);
        }
        return zuordnungDtos;
    }

    /**
     * Generiert aus einem FahrtstreckeInputMitHaltestellenDTO ein
     * FahrtstreckeInputDTO für den Fahrtstrecke Service
     * 
     * @param fahrtstreckeInputMitHaltestellenDTO
     * @return FahrtstreckeInputDTO
     */
    private FahrtstreckeInputDTO genNewFahrtstreckeInputDTO(
            FahrtstreckeInputMitHaltestellenDTO fahrtstreckeInputMitHaltestellenDTO) {
        FahrtstreckeInputDTO fahrtstreckeInputDTO = new FahrtstreckeInputDTO();
        fahrtstreckeInputDTO.setBuslinieId(fahrtstreckeInputMitHaltestellenDTO.getBuslinieId());
        fahrtstreckeInputDTO.setFahrtstreckeName(fahrtstreckeInputMitHaltestellenDTO.getFahrtstreckeName());
        return fahrtstreckeInputDTO;
    }

    /**
     * Gibt alle Fahrten zu der übergebenen Buslinie zurück
     * 
     * @param buslinieId
     * @return ResponseEntity mit Liste aus FahrtstreckeMitHaltestellenDTOs
     */
    @GetMapping(path = "/buslinie/{buslinieId}")
    public ResponseEntity<List<FahrtstreckeMitHaltestellenDTO>> getAlleFahrtenZuBuslinie(
            @PathVariable Long buslinieId) {
        List<FahrtstreckeMitHaltestellenDTO> alleFahrten = new ArrayList<>();
        try {
            alleFahrten.addAll(service.getAlleFahrtstreckenZuBuslinieId(buslinieId));
        } catch (InstanceNotFoundException e) {
            log.warning("Fehler beim Sortieren der Haltestellen");
        }
        return ResponseEntity.ok(alleFahrten);
    }

    /**
     * Gibt alle Fahrten mit Haltestllen zurück
     * 
     * @return ResponseEntity mit Liste aus FahrtstreckeMitHaltestellenDTOs
     */
    @GetMapping(path = "/haltestelle")
    public ResponseEntity<List<FahrtstreckeMitHaltestellenDTO>> getAlleFahrtenMitHaltestellen() {
        List<FahrtstreckeMitHaltestellenDTO> alleFahrten = new ArrayList<>();
        try {
            alleFahrten.addAll(service.getAlleFahrtstreckenMitHaltestellen());
        } catch (InstanceNotFoundException e) {
            log.warning("Fehler beim Sortieren der Haltestellen");
        }
        return ResponseEntity.ok(alleFahrten);
    }

    /**
     * Gibt alle Fahrten zu einer Buslinie aus in der die Haltestelle enthalten ist
     * mit Uhrzeit zu der sie laut Fahrplan abfahren
     * 
     * @param buslinieId
     * @param haltestelleId
     * @return ResponseEntity mit Liste aus FahrtstreckeMitUhrzeitDTOs
     */
    @GetMapping(path = "/uhrzeit")
    public ResponseEntity<List<FahrtstreckeMitUhrzeitDTO>> getAlleFahrtenZuBuslinieUndHaltestelleMitUhrzeit(
            @RequestParam Long buslinieId, @RequestParam Long haltestelleId) {
        List<FahrtstreckeMitUhrzeitDTO> fahrtstreckenMitUhrzeit = new ArrayList<>();
        try {
            fahrtstreckenMitUhrzeit.addAll(service.ermittleFahrtstreckenMitUhrzeit(buslinieId, haltestelleId));
        } catch (InstanceNotFoundException e) {
            log.warning("Eine Instanz wurde nicht gefunden");
        } catch (IllegalArgumentException e) {
            log.warning("Zu einer ID konnte nichts gefunden werden");
        }
        if (fahrtstreckenMitUhrzeit.isEmpty()) {
            log.warning("Es wurden keine Fahrtstrecken gefunden");
        }
        return ResponseEntity.ok(fahrtstreckenMitUhrzeit);
    }

    /**
     * Gibt alle Fahrtstrecken aus
     * 
     * @return ResponseEntity mit Liste aus FahrtstreckeOutputDTOs
     */
    @GetMapping(path = "")
    public ResponseEntity<List<FahrtstreckeOutputDTO>> getAlleFahrtstrecken() {
        List<FahrtstreckeOutputDTO> fahrtstrecken = service.getAlleFahrtstrecken();
        if (fahrtstrecken.isEmpty()) {
            log.warning("Es sind keine Fahrtstrecken vorhanden");
        }
        return ResponseEntity.ok(fahrtstrecken);
    }

    /**
     * Löscht eine Fahrtstrecke anhand der ID
     * 
     * @param fahrtstreckeId
     * @return ResponseEntity mit Boolean ob das Löschen erfolgreich war
     */
    @DeleteMapping(path = "/{fahrtstreckeId}")
    public ResponseEntity<Boolean> deleteFahrtstreckeById(@PathVariable Long fahrtstreckeId) {
        try {
            return ResponseEntity.ok(service.deleteFahrtstrecke(fahrtstreckeId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(true);
        }
    }
}
