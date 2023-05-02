package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminDataDto extends AccessLevelDto {
    public AdminDataDto(Long id, Long version) {
        super(id, version);
    }

}
