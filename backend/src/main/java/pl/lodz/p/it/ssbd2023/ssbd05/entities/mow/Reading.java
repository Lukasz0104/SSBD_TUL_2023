package pl.lodz.p.it.ssbd2023.ssbd05.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.EntityControlListenerMOW;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading")
@NamedQueries({
    @NamedQuery(
        name = "Reading.findByMeterId",
        query = """
            SELECT r FROM Reading r
            WHERE r.meter.id = :meterId
            ORDER BY r.date DESC
            """),
    @NamedQuery(
        name = "Reading.countByMeterId",
        query = """
            SELECT count(r) FROM Reading r
            WHERE r.meter.id = :meterId
            """),
    @NamedQuery(
        name = "Reading.findReliableReadingsFromLastDayOfYear",
        query = """
            SELECT r FROM Reading r
            WHERE
                r.reliable = true
                AND r.meter.place.id = :placeId
                AND r.meter.category.id = :categoryId
                AND EXTRACT(MONTH FROM r.date) = 12
                AND EXTRACT(DAY FROM r.date) = 31
                AND EXTRACT(YEAR FROM r.date) <= :year
            ORDER BY r.date DESC""")
})
@NoArgsConstructor
@EntityListeners({EntityControlListenerMOW.class})
@ToString
public class Reading extends AbstractEntity implements Serializable {

    @NotNull
    @Basic(optional = false)
    @Column(name = "date", nullable = false)
    @Getter
    @Setter
    private LocalDateTime date;

    @PositiveOrZero
    @NotNull
    @Basic(optional = false)
    @Column(name = "value", nullable = false, scale = 3, precision = 38)
    @Getter
    @Setter
    private BigDecimal value;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "meter_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Meter meter;

    @NotNull
    @Basic(optional = false)
    @Column(name = "reliable", nullable = false)
    @Getter
    @Setter
    private boolean reliable = true;

    public Reading(LocalDateTime date, BigDecimal value, Meter meter) {
        this.date = date;
        this.value = value;
        this.meter = meter;
    }

    public Reading(LocalDateTime date, BigDecimal value) {
        this.date = date;
        this.value = value;
    }
}
