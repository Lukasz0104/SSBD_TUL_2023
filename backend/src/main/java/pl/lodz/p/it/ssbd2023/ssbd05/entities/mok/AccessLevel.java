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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;

import java.io.Serializable;

@Entity
@Table(
    name = "access_level",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"level", "account_id"},
        name = "access_level_account_id_level_unique"))
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "level")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NamedQueries({
    @NamedQuery(
        name = "AccessLevel.findAccessLevelById",
        query = "SELECT a FROM AccessLevel a WHERE a.id = :id"),
    @NamedQuery(
        name = "AccessLevel.findDataByAccessType",
        query = "SELECT a FROM AccessLevel a WHERE a.level = :level"),
    @NamedQuery(
        name = "AccessLevel.findByAccountId",
        query = """
            SELECT al FROM AccessLevel al
            WHERE al.account.id = :accountId"""),
    @NamedQuery(
        name = "AccessLevel.findActiveByAccountId",
        query = """
            SELECT al FROM AccessLevel al
            WHERE al.active = TRUE AND al.account.id = :accountId"""),
})
public abstract class AccessLevel extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Basic(optional = false)
    @Column(name = "level", updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private AccessType level;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Account account;

    @NotNull
    @Getter
    @Setter
    @Basic(optional = false)
    @Column(name = "active", nullable = false)
    private boolean active = false;

    @NotNull
    @Getter
    @Setter
    @Basic(optional = false)
    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    public AccessLevel(AccessType accessTypes, Account account) {
        this.level = accessTypes;
        this.account = account;
    }

    public AccessLevel(AccessType accessTypes, Account account, boolean active) {
        this(accessTypes, account);
        this.active = active;
    }

    public AccessLevel(Long id, @NotNull Long version, AccessType level) {
        super(id, version);
        this.level = level;
    }

    public AccessLevel(Long id, Long version, AccessType accessTypes, boolean active) {
        super(id, version);
        this.level = accessTypes;
        this.active = active;
    }

    public AccessLevel(AccessType level) {
        this.level = level;
    }

    public boolean isValidAccessLevel(AccessType type) {
        return active && verified && this.level == type;
    }

}
