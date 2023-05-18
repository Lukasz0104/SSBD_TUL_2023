package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@JsonbTypeInfo(key = "level", value = {
    @JsonbSubtype(alias = "OWNER", type = OwnerDataDto.class),
    @JsonbSubtype(alias = "MANAGER", type = ManagerDataDto.class),
    @JsonbSubtype(alias = "ADMIN", type = AdminDataDto.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "level")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OwnerDataDto.class, name = "OWNER"),
    @JsonSubTypes.Type(value = ManagerDataDto.class, name = "MANAGER"),
    @JsonSubTypes.Type(value = AdminDataDto.class, name = "ADMIN")
})
public abstract class AccessLevelDto {
    @NotNull
    private Long id;

    @NotNull
    private Long version;

    @NotNull
    private boolean verified;

    @NotNull
    private boolean active;

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

    public AccessLevelDto(Long id, Long version, boolean verified, boolean active) {
        this.id = id;
        this.version = version;
        this.verified = verified;
        this.active = active;
    }
}
