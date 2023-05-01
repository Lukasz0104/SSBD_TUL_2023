package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.edit;

import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonbTypeInfo(key = "level", value = {
    @JsonbSubtype(alias = "OWNER", type = EditOwnerDto.class),
    @JsonbSubtype(alias = "MANAGER", type = EditManagerDto.class),
    @JsonbSubtype(alias = "ADMIN", type = EditAdminDto.class)
})
public abstract class EditAccessLevelDto {
    @NotNull
    private AccessType accessType;
}