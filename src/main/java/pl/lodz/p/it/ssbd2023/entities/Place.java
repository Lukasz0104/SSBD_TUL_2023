package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "place")
@NoArgsConstructor
public class Place extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Basic(optional = false)
    @Column(name = "place_number", nullable = false, unique = true)
    @Getter
    @Setter
    private Integer placeNumber;

    @NotNull
    @Basic(optional = false)
    @Column(name = "square_footage", nullable = false)
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
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "building_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Building building;

    @NotNull
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "place_owner",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id"))
    @Getter
    @Setter
    private Set<OwnerData> owners = new HashSet<>();

    @NotNull
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "place_rate",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "rate_id"))
    @Getter
    @Setter
    private Set<Rate> currentRates = new HashSet<>();

    public Place(Integer placeNumber, BigDecimal squareFootage, Integer residentsNumber, boolean active,
                 Building building) {
        this.placeNumber = placeNumber;
        this.squareFootage = squareFootage;
        this.residentsNumber = residentsNumber;
        this.active = active;
        this.building = building;
    }
}
