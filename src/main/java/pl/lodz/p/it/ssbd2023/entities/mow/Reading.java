package pl.lodz.p.it.ssbd2023.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.entities.AbstractEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading")
@NoArgsConstructor
public class Reading extends AbstractEntity implements Serializable {

    @NotNull
    @Basic(optional = false)
    @Column(name = "date", nullable = false)
    @Getter
    @Setter
    private LocalDateTime date;

    @NotNull
    @Basic(optional = false)
    @Column(name = "value", nullable = false)
    @Getter
    @Setter
    private BigDecimal value;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "meter_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Meter meter;

    public Reading(LocalDateTime date, BigDecimal value, Meter meter) {
        this.date = date;
        this.value = value;
        this.meter = meter;
    }
}
