package de.hsw.busplaner.dtos.fahrplanzuordnung;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrplanzuordnungInputDTO extends FahrplanzuordnungDTO {

    Long fahrtstreckeId;

    Long fahrplanId;

}
