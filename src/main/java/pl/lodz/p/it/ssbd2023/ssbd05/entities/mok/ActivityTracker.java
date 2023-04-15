package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Embeddable
@Access(AccessType.FIELD)
public class ActivityTracker implements Serializable {

    @Column(name = "last_successful_login")
    private LocalDateTime lastSuccessfulLogin;

    @Column(name = "last_unsuccessful_login")
    private LocalDateTime lastUnsuccessfulLogin;

    @Column(name = "last_successful_login_ip")
    private String lastSuccessfulLoginIp;

    @Column(name = "last_unsuccessful_login_ip")
    private String lastUnsuccessfulLoginIp;
}
