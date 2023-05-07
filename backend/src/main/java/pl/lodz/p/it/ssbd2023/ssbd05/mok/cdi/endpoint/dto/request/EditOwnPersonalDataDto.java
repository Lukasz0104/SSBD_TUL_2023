package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.SignableDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditOwnPersonalDataDto implements SignableDto {


    @NotNull
    private Long version;

    @NotNull
    private List<@Valid AccessLevelDto> accessLevels = new ArrayList<>();

    @NotNull
    @Size(min = 3, max = 100)
    private String login;

    @NotBlank
    @Size(min = 1, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    private String lastName;

    @Override
    public String getSignableFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("login", login);
        fields.put("version", String.valueOf(version));
        getAccessLevels().forEach(al -> fields.put(
            String.valueOf(al.getId()),
            String.valueOf(al.getVersion())
        ));
        return fields.toString();
    }
}
