package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.ControlledEntityDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingDto extends ControlledEntityDTO {

    @NotNull
    private Long id;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private BigDecimal value;

    @NotNull
    private boolean reliable;

    public ReadingDto(LocalDateTime createdTime, String createdBy, LocalDateTime updatedTime, String updatedBy, Long id,
                      LocalDateTime date, BigDecimal value, boolean reliable) {
        super(createdTime, createdBy, updatedTime, updatedBy);
        this.id = id;
        this.date = date;
        this.value = value;
        this.reliable = reliable;
    }
}
