package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeterDto {
    @NotNull
    private Long id;

    @NotNull
    private String category;

    @NotNull
    private boolean hasReadingInLast30Days;

    @NotNull
    private boolean active;

    @NotNull
    private LocalDateTime dateOfNextReading;
}
