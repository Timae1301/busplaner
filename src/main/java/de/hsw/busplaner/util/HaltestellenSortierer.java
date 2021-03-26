package de.hsw.busplaner.util;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hsw.busplaner.beans.Haltestellenzuordnung;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.services.HaltestelleService;
import lombok.extern.java.Log;

/**
 * Haltestellensortierer, der die für die Haltestellenzuordnungen die
 * Haltestellen in der richtigen Reihenfolge ordnet
 */
@Log
@Component
public class HaltestellenSortierer {

    @Autowired
    HaltestelleService haltestelleService;

    /**
     * Erstellt aus den Haltestellenzuordnungen eine sortierte Liste mit allen
     * Haltestellen in der korrekten Reihenfolge
     * 
     * @param haltestellenzuordnungen
     * @return sortierte Liste mit HaltestellenzuordnungSortierDTOs
     * @throws InstanceNotFoundException wenn beim Sortieren der Haltestellen die
     *                                   Informationen zu der Haltestelle nicht
     *                                   ermittelt werden konnten
     * @throws IllegalArgumentException  wenn zu der ID keine Haltestelle gefunden
     *                                   wurde
     */
    public List<HaltestellenzuordnungSortierDTO> sortiereHaltestellen(
            /*
             * Absichtlich eine ArrayList, da eine List Haltestellenzuordnungen mit gleicher
             * Fahrtstrecke und Haltestelle nicht speichert und so Haltestellenzuordnungen
             * verloren gehen
             */ArrayList<Haltestellenzuordnung> haltestellenzuordnungen)
            throws InstanceNotFoundException, IllegalArgumentException {
        List<HaltestellenzuordnungSortierDTO> sortierteHaltestellen = new ArrayList<>();
        if (haltestellenzuordnungen.isEmpty()) {
            log.info("Es wurden keine Haltestellenzuordnungen uebergeben");
            return sortierteHaltestellen;
        }
        // Die erste Haltestelle wird gesucht (Rundkurse nicht möglich)
        Haltestellenzuordnung naechsteHaltestelle = getErsteHaltestelle(haltestellenzuordnungen);
        if (naechsteHaltestelle == null) {
            log.warning("Keine erste Haltestelle gefunden");
            throw new InstanceNotFoundException("keine erste Haltestelle gefunden");
        }
        sortierteHaltestellen.add(new HaltestellenzuordnungSortierDTO(naechsteHaltestelle));
        haltestellenzuordnungen.remove(naechsteHaltestelle);

        // Die nächste Haltestelle wird anhand des Eintrags der vorherigen Haltestelle
        // gesucht und dann aus der Liste entfernt bis die Haltestellenzuordungen leer
        // sind
        while (!haltestellenzuordnungen.isEmpty()) {
            naechsteHaltestelle = getNaechsteHaltestelle(haltestellenzuordnungen,
                    naechsteHaltestelle.getNaechsteHaltestelle());
            if (naechsteHaltestelle == null) {
                log.warning("Keine naechste Haltestelle gefunden " + haltestellenzuordnungen.size());
                throw new InstanceNotFoundException("Haltestelle nicht gefunden, Daten ungenügend");
            }
            sortierteHaltestellen.add(new HaltestellenzuordnungSortierDTO(naechsteHaltestelle));
            haltestellenzuordnungen.remove(naechsteHaltestelle);
        }

        // für die letzte Haltestelle ist kein vollständiger Eintrag in der List weshalb
        // diese extra ermittelt wird, um der Liste hinzugefügt zu werden
        sortierteHaltestellen.add(new HaltestellenzuordnungSortierDTO(
                haltestelleService.getHaltestelleById(naechsteHaltestelle.getNaechsteHaltestelle())));
        return sortierteHaltestellen;
    }

    /**
     * Die erste Haltestelle wird ermittelt Da keine Rundkurse möglich sind ist die
     * ID der erste Haltstelle in der Liste nicht als nächste Haltestelle
     * eingetragen
     * 
     * @param haltestellenzuordnungen
     * @return Haltestellenzuordnung für die erste Haltestelle
     */
    private Haltestellenzuordnung getErsteHaltestelle(List<Haltestellenzuordnung> haltestellenzuordnungen) {
        List<Long> naechstenHaltestellen = getNaechsteHaltestellen(haltestellenzuordnungen);
        for (Haltestellenzuordnung haltestellenzuordnung : haltestellenzuordnungen) {
            if (!naechstenHaltestellen.contains(haltestellenzuordnung.getHaltestelle().getId())) {
                return haltestellenzuordnung;
            }
        }
        return null;
    }

    /**
     * Erstellt eine Liste mit den IDs der nächsten Haltestellen
     * 
     * @param haltestellenzuordnungen
     * @return Liste mit den IDs aller nächsten Haltestellen der
     *         Haltestellenzuordnungen
     */
    private List<Long> getNaechsteHaltestellen(List<Haltestellenzuordnung> haltestellenzuordnungen) {
        List<Long> naechstenHaltestellen = new ArrayList<>();
        for (Haltestellenzuordnung haltestellenzuordnung : haltestellenzuordnungen) {
            naechstenHaltestellen.add(haltestellenzuordnung.getNaechsteHaltestelle());
        }
        return naechstenHaltestellen;
    }

    /**
     * Ermittelt nächste Haltestelle anhand der übergebenen ID aus der Liste der
     * Haltestellenzuordnungen
     * 
     * @param haltestellenzuordnungen
     * @param naechsteHaltestelleId
     * @return Haltestellenzuordnungen mit der nächsten Haltestelle als ID
     */
    private Haltestellenzuordnung getNaechsteHaltestelle(List<Haltestellenzuordnung> haltestellenzuordnungen,
            Long naechsteHaltestelleId) {
        for (Haltestellenzuordnung haltestellenzuordnung : haltestellenzuordnungen) {
            if (haltestellenzuordnung.getHaltestelle().getId().equals(naechsteHaltestelleId)) {
                return haltestellenzuordnung;
            }
        }
        return null;
    }
}
