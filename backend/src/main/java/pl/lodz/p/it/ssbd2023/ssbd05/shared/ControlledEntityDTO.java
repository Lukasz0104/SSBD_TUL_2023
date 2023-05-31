package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlledEntityDTO {
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
}
