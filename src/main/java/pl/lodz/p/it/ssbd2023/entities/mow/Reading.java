package pl.lodz.p.it.ssbd2023.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
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
@NamedQueries({
    @NamedQuery(
        name = "Reading.findAll",
        query = "SELECT r FROM Reading r"),
    @NamedQuery(
        name = "Reading.findById",
        query = "SELECT r FROM Reading r WHERE r.id = :id"),
    @NamedQuery(
        name = "Reading.findByValue",
        query = "SELECT r FROM Reading r WHERE r.value = :value"),
    @NamedQuery(
        name = "Reading.findByDate",
        query = "SELECT r FROM Reading r WHERE r.date = :date"),
    @NamedQuery(
        name = "Reading.findByDateAfter",
        query = """
            SELECT r FROM Reading r
            WHERE r.date >= :date"""),
    @NamedQuery(
        name = "Reading.findByDateBefore",
        query = """
            SELECT r FROM Reading r
            WHERE r.date < :date"""),
    @NamedQuery(
        name = "Reading.findByDateBetween",
        query = """
               SELECT r FROM Reading r
               WHERE r.date BETWEEN :beginDate AND :endDate"""),
    @NamedQuery(
        name = "Reading.findByMeterId",
        query = """
            SELECT r FROM Reading r
            WHERE r.meter.id = :meterId"""),
    @NamedQuery(
        name = "Reading.findByMeterIdAndDate",
        query = """
            SELECT r FROM Reading r
            WHERE r.meter.id = :meterId
                  AND r.date = :date"""),
    @NamedQuery(
        name = "Reading.findByMeterIdAndDateAfter",
        query = """
            SELECT r FROM Reading r
            WHERE r.meter.id = :meterId
                  AND r.date >= :date"""),
    @NamedQuery(
        name = "Reading.findByMeterIdAndDateBefore",
        query = """
            SELECT r FROM Reading r
            WHERE r.meter.id = :meterId
                  AND r.date < :date"""),
    @NamedQuery(
        name = "Reading.findByMeterIdAndDateBetween",
        query = """
            SELECT r FROM Reading r
            WHERE r.meter.id = :meterId
                  AND r.date BETWEEN :beginDate AND :endDate"""),
    @NamedQuery(
        name = "Reading.findByPlaceId",
        query = """
            SELECT r FROM Reading r
            INNER JOIN Meter m
                ON m.id = r.meter.id
            INNER JOIN Place p
                ON m.place.id = p.id"""),
    @NamedQuery(
        name = "Reading.findByPlaceIdAndDate",
        query = """
            SELECT r FROM Reading r
            INNER JOIN Meter m
                ON m.id = r.meter.id
            INNER JOIN Place p
                ON m.place.id = p.id
            WHERE r.date = :date"""),
    @NamedQuery(
        name = "Reading.findByPlaceIdAndDateBetween",
        query = """
            SELECT r FROM Reading r
            INNER JOIN Meter m
                ON m.id = r.meter.id
            INNER JOIN Place p
                ON m.place.id = p.id
            WHERE r.date BETWEEN :beginDate AND :endDate"""),
    @NamedQuery(
        name = "Reading.findByPlaceIdAndDateAfter",
        query = """
            SELECT r FROM Reading r
            INNER JOIN Meter m
                ON m.id = r.meter.id
            INNER JOIN Place p
                ON m.place.id = p.id
            WHERE r.date >= :date"""),
    @NamedQuery(
        name = "Reading.findByPlaceIdAndDateBefore",
        query = """
            SELECT r FROM Reading r
            INNER JOIN Meter m
                ON m.id = r.meter.id
            INNER JOIN Place p
                ON m.place.id = p.id
            WHERE r.date < :date""")
})
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