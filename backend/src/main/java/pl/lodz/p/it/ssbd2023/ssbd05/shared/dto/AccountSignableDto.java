package pl.lodz.p.it.ssbd2023.ssbd05.shared.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AccessLevelDto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AccountSignableDto implements SignableDto {

    @NotBlank
    private String login;

    @NotNull
    private Long version;

    @NotNull
    private Set<@Valid AccessLevelDto> accessLevels = new HashSet<>();

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
