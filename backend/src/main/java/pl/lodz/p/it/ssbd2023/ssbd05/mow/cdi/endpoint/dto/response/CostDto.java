package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CostDto {

    @NotNull
    private Long id;

    @NotNull
    private Long version;

    @NotNull
    private Integer year;

    @NotNull
    private Month month;

    @PositiveOrZero
    private BigDecimal totalConsumption;

    @NotNull
    @PositiveOrZero
    private BigDecimal realRate;

    @NotNull
    @Valid
    private String category;

    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private LocalDateTime createdTime;

    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private String createdBy;

    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private LocalDateTime updatedTime;

    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private String updatedBy;

    public CostDto(Long id, Long version, Integer year,
                   Month month, BigDecimal totalConsumption, BigDecimal realRate, String category) {
        this.id = id;
        this.version = version;
        this.year = year;
        this.month = month;
        this.totalConsumption = totalConsumption;
        this.realRate = realRate;
        this.category = category;
    }
}
