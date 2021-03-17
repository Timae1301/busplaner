package de.hsw.busplaner.dtos.fahrtstrecke;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class FahrtstreckeInputDTO extends FahrtstreckeDTO {

    Long buslinieId;
}
