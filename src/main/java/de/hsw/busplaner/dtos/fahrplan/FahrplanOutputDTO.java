package de.hsw.busplaner.dtos.fahrplan;

import java.util.ArrayList;
import java.util.List;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.dtos.fahrplanzuordnung.FahrplanzuordnungOutputFahrplanDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrplanOutputDTO extends FahrplanDTO {

    private Long id;

    private List<FahrplanzuordnungOutputFahrplanDTO> fahrplanzuordnungen = new ArrayList<>();

    // private List<FahrtstreckeOutputDTO> fahrtstrecken = new ArrayList<>();
    // TODO: infos zu Fahrtstrecken

    public FahrplanOutputDTO(Fahrplan fahrplan) {
        this.id = fahrplan.getId();
        super.setName(fahrplan.getName());
        for (Fahrplanzuordnung fahrplanzuordnung : fahrplan.getFahrplanzuordnungen()) {
            getFahrplanzuordnungen().add(new FahrplanzuordnungOutputFahrplanDTO(fahrplanzuordnung));
        }

    }
}
