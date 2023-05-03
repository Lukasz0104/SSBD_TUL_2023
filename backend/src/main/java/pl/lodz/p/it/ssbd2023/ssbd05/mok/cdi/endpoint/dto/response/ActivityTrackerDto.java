package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivityTrackerDto {
    private LocalDateTime lastSuccessfulLogin;
    private LocalDateTime lastUnsuccessfulLogin;
    private String lastSuccessfulLoginIp;
    private String lastUnsuccessfulLoginIp;
    private Integer unsuccessfulLoginChainCounter;
}
