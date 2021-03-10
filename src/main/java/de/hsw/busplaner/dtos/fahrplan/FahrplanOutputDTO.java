package de.hsw.busplaner.dtos.fahrplan;

import java.util.ArrayList;
import java.util.List;

import de.hsw.busplaner.beans.Fahrplan;
import de.hsw.busplaner.beans.Fahrplanzuordnung;
import de.hsw.busplaner.dtos.fahrplanzuordnung.FahrplanzuordnungOutputDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrplanOutputDTO extends FahrplanDTO {

    private Long id;

    private List<Fahrplanzuordnung> fahrplanzuordnungen = new ArrayList<>();

    public FahrplanOutputDTO(Fahrplan fahrplan) {
        this.id = fahrplan.getId();
        super.setName(fahrplan.getName());
        getFahrplanzuordnungen().addAll(fahrplan.getFahrplanzuordnungen());
    }
}
