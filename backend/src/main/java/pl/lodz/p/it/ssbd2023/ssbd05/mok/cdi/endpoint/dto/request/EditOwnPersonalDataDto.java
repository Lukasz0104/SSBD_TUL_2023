package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AccessLevelDto;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditOwnPersonalDataDto {
    @NotNull
    private Long version;

    @NotNull
    private List<@Valid AccessLevelDto> accessLevels = new ArrayList<>();

    @NotBlank
    @Size(min = 1, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    private String lastName;
}
