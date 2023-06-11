package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.SignableDto;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceSignableDto implements SignableDto {


    @NotNull
    private Long id;

    @NotNull
    private Long version;

    @Override
    public String getSignableFields() {
        Map<String, String> fields = new HashMap<>();
        fields.put("id", String.valueOf(id));
        fields.put("version", String.valueOf(version));
        return fields.toString();
    }
}
