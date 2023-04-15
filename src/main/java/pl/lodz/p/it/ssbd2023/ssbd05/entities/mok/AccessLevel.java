package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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

@Entity
@Table(name = "access_level", uniqueConstraints = @UniqueConstraint(columnNames = {"level", "account"}))
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "level")
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "AccessLevel.findAccessLevelById",
        query = "SELECT a FROM AccessLevel a WHERE a.id = :id"),
    @NamedQuery(
        name = "AccessLevel.findDataByAccessType",
        query = "SELECT a FROM AccessLevel a WHERE a.level = :level")
})
public abstract class AccessLevel extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Basic(optional = false)
    @Column(name = "level", updatable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private AccessTypes level;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "account", referencedColumnName = "id", updatable = false)
    @Getter
    @Setter
    private Account account;

    public AccessLevel(AccessTypes accessTypes, Account account) {
        this.level = accessTypes;
        this.account = account;
    }
}
