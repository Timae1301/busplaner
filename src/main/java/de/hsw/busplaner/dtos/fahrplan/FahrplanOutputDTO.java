package de.hsw.busplaner.dtos.fahrplan;

import de.hsw.busplaner.beans.Fahrplan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrplanOutputDTO extends FahrplanDTO {

    private Long id;

    public FahrplanOutputDTO(Fahrplan fahrplan) {
        this.id = fahrplan.getId();
        super.setName(fahrplan.getName());
    }
}
