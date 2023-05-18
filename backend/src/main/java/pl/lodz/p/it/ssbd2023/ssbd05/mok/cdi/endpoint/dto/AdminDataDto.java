package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminDataDto extends AccessLevelDto {

    public AdminDataDto(@NotNull Long id,
                        @NotNull Long version,
                        @NotNull boolean verified,
                        @NotNull boolean active, LocalDateTime createdTime,
                        String createdBy, LocalDateTime updatedTime, String updatedBy) {
        super(id, version, verified, active, createdTime, createdBy, updatedTime, updatedBy);
    }

    public AdminDataDto(Long id, Long version, boolean verified, boolean active) {
        super(id, version, verified, active);
    }
}
