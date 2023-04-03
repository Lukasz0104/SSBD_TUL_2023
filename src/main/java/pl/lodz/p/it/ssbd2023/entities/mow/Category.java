package pl.lodz.p.it.ssbd2023.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.entities.AbstractEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@NamedQueries({
    @NamedQuery(
        name = "Category.findAll",
        query = "SELECT c FROM Category c"),
    @NamedQuery(
        name = "Category.findById",
        query = "SELECT c FROM Category c WHERE c.id = :id"),
    @NamedQuery(
        name = "Category.findByName",
        query = "SELECT c FROM Category c WHERE c.name = :name")
})
@NoArgsConstructor
public class Category extends AbstractEntity implements Serializable {

    @NotNull
    @Basic(optional = false)
    @Column(name = "name", nullable = false, unique = true)
    @Getter
    @Setter
    private String name;

    @NotNull
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Rate> rates = new HashSet<>();

    @NotNull
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Cost> costs = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }
}
