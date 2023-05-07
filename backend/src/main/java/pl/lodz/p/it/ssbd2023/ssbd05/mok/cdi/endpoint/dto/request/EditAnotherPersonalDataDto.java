package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Language;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.SignableDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditAnotherPersonalDataDto implements SignableDto {
    @NotNull
    private Long id;

    @NotNull
    private Long version;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 1, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    private String lastName;

    @NotNull
    private Language language;

    @NotNull
    private List<@Valid AccessLevelDto> accessLevels;

    @Override
    public String getSignableFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("id", String.valueOf(id));
        fields.put("version", String.valueOf(version));
        getAccessLevels().forEach(al -> fields.put(
            String.valueOf(al.getId()),
            String.valueOf(al.getVersion())
        ));
        return fields.toString();
    }
}
