package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto;

import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonbTypeInfo(key = "@level", value = {
    @JsonbSubtype(alias = "OWNER", type = OwnerDataDto.class),
    @JsonbSubtype(alias = "MANAGER", type = ManagerDataDto.class),
    @JsonbSubtype(alias = "ADMIN", type = AdminDataDto.class)
})
public abstract class AccessLevelDto {
    @NotNull
    private Long id;

    @NotNull
    private Long version;

    @NotNull
    private AccessType level;
}
