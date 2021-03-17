package de.hsw.busplaner.util;

import de.hsw.busplaner.dtos.fahrtstrecke.FahrtstreckeMitHaltestellenDTO;
import de.hsw.busplaner.dtos.haltestellenzuordnung.HaltestellenzuordnungSortierDTO;

public class FahrtzeitenErmitteln {

    public static int ermittleFahrtzeitInMinuten(FahrtstreckeMitHaltestellenDTO fahrtstrecke, Long haltestelleId) {
        int fahrtzeit = 0;
        for (HaltestellenzuordnungSortierDTO haltestelle : fahrtstrecke.getHaltestellen()) {
            if (haltestelle.getHaltestelleId().equals(haltestelleId)) {
                break;
            }
            fahrtzeit += haltestelle.getFahrtzeit();
        }
        return fahrtzeit;
    }

    public static int ermittleFahrtzeitInMinutenInvertiert(FahrtstreckeMitHaltestellenDTO fahrtstrecke,
            Long haltestelleId) {
        int fahrtzeit = 0;
        for (int i = fahrtstrecke.getHaltestellen().size() - 1; i > 0; i--) {
            if (fahrtstrecke.getHaltestellen().get(i).getHaltestelleId().equals(haltestelleId)) {
                break;
            }
            fahrtzeit += fahrtstrecke.getHaltestellen().get(i - 1).getFahrtzeit();
        }
        return fahrtzeit;
    }

}
