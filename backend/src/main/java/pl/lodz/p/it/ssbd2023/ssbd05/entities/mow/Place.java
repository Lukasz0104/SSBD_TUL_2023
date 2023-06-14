package pl.lodz.p.it.ssbd2023.ssbd05.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.EntityControlListenerMOW;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "place",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "place_number_building_id",
            columnNames = {"place_number", "building_id"})
    })
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "Place.findAll",
        query = "SELECT p FROM Place p"),
    @NamedQuery(
        name = "Place.findByIdAndOwnerLogin",
        query = """
            SELECT p FROM Place AS p
                WHERE p.id = :id
                AND :login IN (SELECT o.account.login FROM p.owners o)
            """),
    @NamedQuery(
        name = "Place.findByPlaceNumber",
        query = "SELECT p FROM Place p WHERE p.placeNumber = :placeNumber"),
    @NamedQuery(
        name = "Place.findAllActive",
        query = "SELECT p FROM Place p WHERE p.active = true"),
    @NamedQuery(
        name = "Place.findAllInactive",
        query = "SELECT p FROM Place p WHERE p.active = false"),
    @NamedQuery(
        name = "Place.findByResidentsNumber",
        query = "SELECT p FROM Place p WHERE p.residentsNumber = :residentsNumber"),
    @NamedQuery(
        name = "Place.findByOwnerLogin",
        query = "SELECT p FROM Place p WHERE :login IN (SELECT ow.account.login FROM p.owners ow)"),
    @NamedQuery(
        name = "Place.findByResidentsNumberAndActive",
        query = "SELECT p FROM Place p WHERE p.residentsNumber = :residentsNumber AND p.active = true"),
    @NamedQuery(
        name = "Place.findByResidentsNumberAndInactive",
        query = "SELECT p FROM Place p WHERE p.residentsNumber = :residentsNumber AND p.active = false"),
    @NamedQuery(
        name = "Place.findBySquareFootage",
        query = "SELECT p FROM Place p WHERE p.squareFootage = :squareFootage"),
    @NamedQuery(
        name = "Place.findBySquareFootageAndActive",
        query = "SELECT p FROM Place p WHERE p.squareFootage = :squareFootage AND p.active = true"),
    @NamedQuery(
        name = "Place.findBySquareFootageAndInactive",
        query = "SELECT p FROM Place p WHERE p.squareFootage = :squareFootage AND p.active = false"),
    @NamedQuery(
        name = "Place.findByAddress",
        query = "SELECT p FROM Place p WHERE p.building.address = :address"),
    @NamedQuery(
        name = "Place.findByAddressAndActive",
        query = "SELECT p FROM Place p WHERE p.building.address = :address AND p.active = true"),
    @NamedQuery(
        name = "Place.findByAddressAndInactive",
        query = "SELECT p FROM Place p WHERE p.building.address = :address AND p.active = false"),
    @NamedQuery(
        name = "Place.findByLogin",
        query = """
            SELECT p FROM Place p
            JOIN p.owners od
            WHERE od.account.login = :login
            ORDER BY p.id
            """),
    @NamedQuery(
        name = "Place.findCurrentRateByPlaceId",
        query = """
            SELECT r FROM Rate r WHERE r.effectiveDate = (SELECT MAX(r2.effectiveDate) FROM Rate r2
            WHERE r2.effectiveDate <= CURRENT_DATE AND r.category = r2.category)
            AND EXISTS (SELECT p FROM Place p JOIN p.currentRates cr 
            WHERE p.id = :placeId AND cr.id = r.id) ORDER BY r.category.name ASC"""),
    @NamedQuery(
        name = "Place.findByBuildingId",
        query = """
            SELECT p FROM Place p
            WHERE p.building.id = :buildingId
            """),
    @NamedQuery(
        name = "Place.findCurrentRateByPlaceIdNotMatch",
        query = """
            SELECT r FROM Rate r WHERE r.effectiveDate = (SELECT MAX(r2.effectiveDate) FROM Rate r2
            WHERE r2.effectiveDate <= CURRENT_DATE AND r.category = r2.category)
            AND NOT EXISTS (SELECT p FROM Place p JOIN p.currentRates cr
            WHERE p.id = :placeId AND cr.id = r.id) ORDER BY r.category.name ASC"""),
    @NamedQuery(
        name = "Place.findCurrentRateByOwnPlaceId",
        query = """
            SELECT r FROM Rate r WHERE r.effectiveDate = (SELECT MAX(r2.effectiveDate) FROM Rate r2
            WHERE r2.effectiveDate < :now AND r.category = r2.category)
            AND EXISTS (SELECT p FROM Place p JOIN p.currentRates cr
            WHERE p.id = :placeId AND :login IN (SELECT o.account.login FROM p.owners o)
            AND cr.id = r.id) ORDER BY r.category.name ASC"""),
    @NamedQuery(
        name = "Place.findOwnerDataByNotOwnersOfPlaceId",
        query = """
            SELECT od FROM OwnerData od WHERE od.id NOT IN
            (SELECT DISTINCT b2.id FROM Place p JOIN p.owners b2 WHERE p.id = :placeId)
        """)
})
@EntityListeners({EntityControlListenerMOW.class})
public class Place extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Positive
    @NotNull
    @Basic(optional = false)
    @Column(name = "place_number", nullable = false)
    @Getter
    @Setter
    private Integer placeNumber;

    @Positive
    @NotNull
    @Basic(optional = false)
    @Column(name = "square_footage", nullable = false, scale = 3, precision = 38)
    @Getter
    @Setter
    private BigDecimal squareFootage;

    @NotNull
    @Basic(optional = false)
    @Column(name = "residents_number", nullable = false)
    @Getter
    @Setter
    private Integer residentsNumber;

    @NotNull
    @Basic(optional = false)
    @Column(name = "active", nullable = false)
    @Getter
    @Setter
    private boolean active;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "building_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Building building;

    @NotNull
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "place_owner",
        joinColumns = @JoinColumn(name = "place_id"),
        inverseJoinColumns = @JoinColumn(name = "owner_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"place_id", "owner_id"}))
    @Getter
    @Setter
    private Set<OwnerData> owners = new HashSet<>();

    @NotNull
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "place_rate",
        joinColumns = @JoinColumn(name = "place_id"),
        inverseJoinColumns = @JoinColumn(name = "rate_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"place_id", "rate_id"}))
    @Getter
    @Setter
    private Set<Rate> currentRates = new HashSet<>();

    @NotNull
    @OneToMany(mappedBy = "place", cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Meter> meters = new HashSet<>();

    public Place(Integer placeNumber, BigDecimal squareFootage, Integer residentsNumber, boolean active,
                 Building building) {
        this.placeNumber = placeNumber;
        this.squareFootage = squareFootage;
        this.residentsNumber = residentsNumber;
        this.active = active;
        this.building = building;
    }

    public Place(Long id, Long version, Integer placeNumber, BigDecimal squareFootage,
                 Integer residentsNumber, boolean active) {
        super(id, version);
        this.placeNumber = placeNumber;
        this.squareFootage = squareFootage;
        this.residentsNumber = residentsNumber;
        this.active = active;
    }
}
