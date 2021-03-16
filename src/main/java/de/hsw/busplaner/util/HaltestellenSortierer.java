package de.hsw.busplaner.util;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hsw.busplaner.beans.Haltestelle;
import de.hsw.busplaner.beans.Haltestellenzuordnung;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;
import de.hsw.busplaner.services.HaltestelleService;
import lombok.extern.java.Log;

@Log
@Component
public class HaltestellenSortierer {

    @Autowired
    HaltestelleService haltestelleService;

    public ArrayList<HaltestellenzuordnungSortierDTO> sortiereHaltestellen(
            ArrayList<Haltestellenzuordnung> haltestellenzuordnungen) throws InstanceNotFoundException {
        ArrayList<HaltestellenzuordnungSortierDTO> sortierteHaltestellen = new ArrayList<>();
        if (haltestellenzuordnungen.isEmpty()) {
            log.info("Es wurden keine Haltestellenzuordnungen uebergeben");
            return sortierteHaltestellen;
        }
        Haltestellenzuordnung naechsteHaltestelle = getErsteHaltestelle(haltestellenzuordnungen);
        if (naechsteHaltestelle == null) {
            log.warning("Keine erste Haltestelle gefunden");
            throw new InstanceNotFoundException("keine erste Haltestelle, Prüfung auf Rundkurs");
        }
        sortierteHaltestellen.add(new HaltestellenzuordnungSortierDTO(naechsteHaltestelle));
        haltestellenzuordnungen.remove(naechsteHaltestelle);

        while (haltestellenzuordnungen.size() > 0) {
            naechsteHaltestelle = getNaechsteHaltestelle(haltestellenzuordnungen,
                    naechsteHaltestelle.getNaechsteHaltestelle());
            if (naechsteHaltestelle == null) {
                log.warning("Keine naechste Haltestelle gefunden " + haltestellenzuordnungen.size());
                throw new InstanceNotFoundException("Haltestelle nicht gefunden, Daten ungenügend");
            }
            sortierteHaltestellen.add(new HaltestellenzuordnungSortierDTO(naechsteHaltestelle));
            haltestellenzuordnungen.remove(naechsteHaltestelle);
        }

        sortierteHaltestellen.add(new HaltestellenzuordnungSortierDTO(
                getLetzteHaltestelle(naechsteHaltestelle.getNaechsteHaltestelle())));
        return sortierteHaltestellen;
    }

    private Haltestelle getLetzteHaltestelle(Long haltestellenId) {
        return haltestelleService.getHaltestelleById(haltestellenId);
    }

    private Haltestellenzuordnung getErsteHaltestelle(ArrayList<Haltestellenzuordnung> haltestellenzuordnungen) {
        ArrayList<Long> naechstenHaltestellen = getNaechsteHaltestellen(haltestellenzuordnungen);
        for (Haltestellenzuordnung haltestellenzuordnung : haltestellenzuordnungen) {
            if (!naechstenHaltestellen.contains(haltestellenzuordnung.getHaltestelleid().getId())) {
                return haltestellenzuordnung;
            }
        }
        return null;
    }

    private ArrayList<Long> getNaechsteHaltestellen(ArrayList<Haltestellenzuordnung> haltestellenzuordnungen) {
        ArrayList<Long> naechstenHaltestellen = new ArrayList<>();
        for (Haltestellenzuordnung haltestellenzuordnung : haltestellenzuordnungen) {
            naechstenHaltestellen.add(haltestellenzuordnung.getNaechsteHaltestelle());
        }
        return naechstenHaltestellen;
    }

    private Haltestellenzuordnung getNaechsteHaltestelle(ArrayList<Haltestellenzuordnung> haltestellenzuordnungen,
            Long naechsteHaltestelle) {
        for (Haltestellenzuordnung haltestellenzuordnung : haltestellenzuordnungen) {
            if (haltestellenzuordnung.getHaltestelleid().getId().equals(naechsteHaltestelle)) {
                return haltestellenzuordnung;
            }
        }
        return null;
    }

    public ArrayList<HaltestellenzuordnungSortierDTO> sortiereHaltestellen(
            List<Haltestellenzuordnung> haltestellenzuordnungen) throws InstanceNotFoundException {
        ArrayList<Haltestellenzuordnung> arrayListHaltestellenzuordnungen = new ArrayList<>();
        arrayListHaltestellenzuordnungen.addAll(haltestellenzuordnungen);
        return sortiereHaltestellen(arrayListHaltestellenzuordnungen);
    }

}
