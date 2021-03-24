package de.hsw.busplaner.util;

import java.time.LocalTime;
import java.util.List;

import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;

public class FahrtzeitenErmitteln {

    private FahrtzeitenErmitteln() {
    }

    public static int ermittleFahrtzeitInMinuten(List<HaltestellenzuordnungSortierDTO> haltestellen,
            Long haltestelleId) {
        int fahrtzeit = 0;
        for (HaltestellenzuordnungSortierDTO haltestelle : haltestellen) {
            if (haltestelle.getHaltestelleId().equals(haltestelleId)) {
                break;
            }
            fahrtzeit += haltestelle.getFahrtzeit();
        }
        return fahrtzeit;
    }

    public static int ermittleFahrtzeitInMinutenInvertiert(List<HaltestellenzuordnungSortierDTO> haltestellen,
            Long haltestelleId) {
        int fahrtzeit = 0;
        for (int i = haltestellen.size() - 1; i > 0; i--) {
            if (haltestellen.get(i).getHaltestelleId().equals(haltestelleId)) {
                break;
            }
            fahrtzeit += haltestellen.get(i - 1).getFahrtzeit();
        }
        return fahrtzeit;
    }

    public static List<HaltestellenzuordnungSortierDTO> setzeUhrzeiten(
            List<HaltestellenzuordnungSortierDTO> haltestellen, LocalTime startzeit) {
        if (haltestellen.isEmpty()) {
            return haltestellen;
        }
        for (int i = 0; i < haltestellen.size(); i++) {
            LocalTime uhrzeit = startzeit
                    .plusMinutes(ermittleFahrtzeitInMinuten(haltestellen, haltestellen.get(i).getHaltestelleId()));
            haltestellen.get(i).setUhrzeit(uhrzeit);
        }
        return haltestellen;
    }

    public static List<HaltestellenzuordnungSortierDTO> setzeUhrzeitenInvertiert(
            List<HaltestellenzuordnungSortierDTO> haltestellen, LocalTime startzeit) {
        if (haltestellen.isEmpty()) {
            return haltestellen;
        }
        for (int i = haltestellen.size() - 1; i >= 0; i--) {
            LocalTime uhrzeit = startzeit.plusMinutes(
                    ermittleFahrtzeitInMinutenInvertiert(haltestellen, haltestellen.get(i).getHaltestelleId()));
            haltestellen.get(i).setUhrzeit(uhrzeit);
        }
        return haltestellen;
    }
}
