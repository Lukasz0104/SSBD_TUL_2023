package pl.lodz.p.it.ssbd2023.ssbd05.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Year;

@Entity
@Table(name = "report", uniqueConstraints = @UniqueConstraint(columnNames = {"year", "place_id", "category_id"}))
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "Report.findAll",
        query = "SELECT r FROM Report r"),
    @NamedQuery(
        name = "Report.findById",
        query = "SELECT r FROM Report r WHERE r.id = :id"),
    @NamedQuery(
        name = "Report.findByYear",
        query = "SELECT r FROM Report r WHERE r.year = :year"),
    @NamedQuery(
        name = "Report.findByTotalCost",
        query = "SELECT r FROM Report r WHERE r.totalCost = :totalCost"),
    @NamedQuery(
        name = "Report.findByTotalConsumption",
        query = "SELECT r FROM Report r WHERE r.totalConsumption = :totalConsumption"),
    @NamedQuery(
        name = "Report.findByPlaceId",
        query = "SELECT r FROM Report r WHERE r.place.id = :placeId"),
    @NamedQuery(
        name = "Report.findByPlaceNumberAndBuildingId",
        query = """
            SELECT r FROM Report r
            WHERE r.place.placeNumber = :placeNumber
                  AND r.place.building.id = :buildingId"""),
    @NamedQuery(
        name = "Report.findByCategoryId",
        query = "SELECT r FROM Report r WHERE r.category.id = :categoryId"),
    @NamedQuery(
        name = "Report.findByCategoryName",
        query = "SELECT r FROM Report r WHERE r.category.name = :categoryName"),
    @NamedQuery(
        name = "Report.findByPlaceIdAndCategoryId",
        query = """
            SELECT r FROM Report r
            WHERE r.place.id = :placeId
                  AND r.category.id = :categoryId
            """),
    @NamedQuery(
        name = "Report.findByPlaceIdAndCategoryName",
        query = """
            SELECT r FROM Report r
            WHERE r.place.id = :placeId
                  AND r.category.name = :name
            """),
    @NamedQuery(
        name = "Report.findByPlaceNumberAndBuildingIdAndCategoryId",
        query = """
            SELECT r FROM Report r
            WHERE r.place.placeNumber = :placeNumber
                  AND r.place.building.id = :buildingId
                  AND r.category.id = :categoryId
            """),
    @NamedQuery(
        name = "Report.findByPlaceNumberAndCategoryName",
        query = """
            SELECT r FROM Report r
            WHERE r.place.placeNumber = :placeNumber
                  AND r.place.building.id = :buildingId
                  AND r.category.name = :name
            """),
    @NamedQuery(
        name = "Report.findByPlaceIdAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.place.id = :placeId
                  AND r.year = :year
            """),
    @NamedQuery(
        name = "Report.findByPlaceNumberAndBuildingIdAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.place.placeNumber = :placeNumber
                  AND r.place.building.id = :buildingId
                  AND r.year = :year"""),
    @NamedQuery(
        name = "Report.findByCategoryIdAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.category.id = :categoryId
                  AND r.year = :year"""),
    @NamedQuery(
        name = "Report.findByCategoryNameAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.category.name = :categoryName
                  AND r.year = :year"""),
    @NamedQuery(
        name = "Report.findByPlaceIdAndCategoryIdAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.place.id = :placeId
                  AND r.category.id = :categoryId
                  AND r.year = :year"""),
    @NamedQuery(
        name = "Report.findByPlaceIdAndCategoryNameAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.place.id = :placeId
                  AND r.category.name = :categoryName
                  AND r.year = :year"""),
    @NamedQuery(
        name = "Report.findByPlaceNumberAndBuildingIdAndCategoryIdAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.place.placeNumber = :placeNumber
                  AND r.place.building.id = :buildingId
                  AND r.category.id = :categoryId
                  AND r.year = :year"""),
    @NamedQuery(
        name = "Report.findByPlaceNumberAndBuildingIdAndCategoryNameAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.place.placeNumber = :placeNumber
                  AND r.place.building.id = :buildingId
                  AND r.category.name = :categoryName
                  AND r.year = :year""")
})
public class Report extends AbstractEntity implements Serializable {

    @NotNull
    @Basic(optional = false)
    @Column(name = "year", nullable = false)
    @Getter
    @Setter
    private Year year;

    @NotNull
    @Basic(optional = false)
    @Column(name = "total_cost", nullable = false)
    @Getter
    @Setter
    private BigDecimal totalCost;


    @NotNull
    @Basic(optional = false)
    @Column(name = "total_consumption", nullable = false)
    @Getter
    @Setter
    private BigDecimal totalConsumption;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "place_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Place place;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Category category;

    public Report(Year year, BigDecimal totalCost, BigDecimal totalConsumption, Place place, Category category) {
        this.year = year;
        this.totalCost = totalCost;
        this.totalConsumption = totalConsumption;
        this.place = place;
        this.category = category;
    }
}
