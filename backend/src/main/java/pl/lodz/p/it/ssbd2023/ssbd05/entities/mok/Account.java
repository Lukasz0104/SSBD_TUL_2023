package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.EntityControlListenerMOK;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "account")
@SecondaryTable(name = "account_data")
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "Account.findByLogin",
        query = "SELECT a FROM Account a WHERE a.login = :login"),
    @NamedQuery(
        name = "Account.findByEmail",
        query = "SELECT a FROM Account a WHERE a.email = :email"),
    @NamedQuery(
        name = "Account.findAllAccountsByActiveAsc",
        query = """
            SELECT a FROM Account a
            WHERE a.active = :active
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )
            ORDER BY a.login ASC"""),
    @NamedQuery(
        name = "Account.findAllAccountsByActiveDesc",
        query = """
            SELECT a FROM Account a
            WHERE a.active = :active
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )
            ORDER BY a.login DESC"""),
    @NamedQuery(
        name = "Account.countAllAccountsByActive",
        query = """
            SELECT count(a.id) FROM Account a
            WHERE a.active = :active
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )"""),
    @NamedQuery(
        name = "Account.findAllAccountsByActiveAndAccessLevelAsc",
        query = """
            SELECT a FROM Account a
            JOIN AccessLevel al ON al.account = a
            WHERE a.active = :active
                AND al.level = :level
                AND al.active = TRUE
                AND al.verified = TRUE
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )
                ORDER BY a.login ASC"""),
    @NamedQuery(
        name = "Account.findAllAccountsByActiveAndAccessLevelDesc",
        query = """
            SELECT a FROM Account a
            JOIN AccessLevel al ON al.account = a
            WHERE a.active = :active
                AND al.level = :level
                AND al.active = TRUE
                AND al.verified = TRUE
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )
                ORDER BY a.login DESC"""),
    @NamedQuery(
        name = "Account.countAllAccountsByActiveAndAccessLevel",
        query = """
            SELECT count(a.id) FROM Account a
            JOIN AccessLevel al ON al.account = a
            WHERE a.active = :active
                AND al.level = :level
                AND al.active = TRUE
                AND al.verified = TRUE
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )"""),
    @NamedQuery(
        name = "Account.findAccountsThatNeedApprovalByAccessLevelAsc",
        query = """
            SELECT a FROM Account a
            JOIN AccessLevel al ON al.account = a
            WHERE a.active = TRUE AND a.verified = TRUE
                AND al.level = :level
                AND al.active = FALSE
                AND al.verified = FALSE
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )
                ORDER BY a.login ASC"""),
    @NamedQuery(
        name = "Account.findAccountsThatNeedApprovalByAccessLevelDesc",
        query = """
            SELECT a FROM Account a
            JOIN AccessLevel al on al.account = a
            WHERE a.active = TRUE AND a.verified = TRUE
                AND al.level = :level
                AND al.active = FALSE
                AND al.verified = FALSE
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )
                ORDER BY a.login DESC"""),
    @NamedQuery(
        name = "Account.countAccountsThatNeedApprovalByAccessLevel",
        query = """
            SELECT count(a.id) FROM Account a
            JOIN AccessLevel al on al.account = a
            WHERE a.active = TRUE AND a.verified = TRUE
                AND al.level = :level
                AND al.active = FALSE
                AND al.verified = FALSE
                AND (
                    LOWER(a.firstName) LIKE CONCAT('%',LOWER(:phrase), '%') OR
                    LOWER(a.lastName) LIKE CONCAT('%',LOWER(:phrase), '%')
                )
                AND (
                    LOWER(a.login) LIKE CONCAT('%',LOWER(:login), '%')
                )"""),
    @NamedQuery(name = "Account.findAccountsLoginsByLoginLike",
        query = "SELECT a.login FROM Account a WHERE LOWER(a.login) LIKE CONCAT('%' ,LOWER(:login), '%')"),
    @NamedQuery(
        name = "Account.findAccountsWithoutRecentActivity",
        query = """
            SELECT a FROM Account a
            WHERE a.active = true
                  AND a.verified = true
                  AND a.activityTracker.lastSuccessfulLogin < :lastSuccessfulLogin"""),
    @NamedQuery(
        name = "Account.findByOwnerId",
        query = """
            SELECT a FROM Account a
            JOIN OwnerData od ON od.account = a
            WHERE od.verified = true
            AND od.id = :ownerId
            """),
    @NamedQuery(
        name = "Account.findByOwnerIdAndActiveOwner",
        query = """
            SELECT a FROM Account a
            JOIN OwnerData od ON od.account = a
            WHERE od.verified = true
            AND od.active = true
            AND od.id = :ownerId
            """)
})
@Getter
@Setter
@EntityListeners({EntityControlListenerMOK.class})
public class Account extends AbstractEntity implements Serializable {

    @Setter(lombok.AccessLevel.NONE)
    @Getter(lombok.AccessLevel.NONE)
    private static final long serialVersionUID = 1L;

    @NotNull
    @OneToMany(
        mappedBy = "account",
        cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE},
        fetch = FetchType.EAGER)
    @Setter(lombok.AccessLevel.NONE)
    private Set<AccessLevel> accessLevels = new HashSet<>();

    @NotNull
    @Basic(optional = false)
    @Email
    @Size(min = 3, max = 320)
    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @NotNull
    @Basic(optional = false)
    @Size(min = 3, max = 100)
    @Column(name = "login", updatable = false, nullable = false, unique = true, length = 100)
    private String login;

    @NotNull
    @Basic(optional = false)
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "old_password")
    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "past_passwords", joinColumns = @JoinColumn(name = "account_id"))
    private Set<String> pastPasswords = new HashSet<>();

    @NotNull
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "first_name", table = "account_data", nullable = false, length = 100)
    private String firstName;

    @NotNull
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "last_name", table = "account_data", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @Basic(optional = false)
    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @NotNull
    @Basic(optional = false)
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @NotNull
    @Basic(optional = false)
    @Column(name = "reminded", nullable = false)
    private boolean reminded = false;

    @NotNull
    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Language language = Language.PL;

    @NotNull
    @Column(name = "two_factor_auth", nullable = false)
    @Basic(optional = false)
    private boolean twoFactorAuth = false;

    @NotNull
    @Column(name = "light_theme_preferred", nullable = false)
    @Basic(optional = false)
    private boolean lightThemePreferred = true;

    @Embedded
    private ActivityTracker activityTracker = new ActivityTracker();

    public Account(String email, String password, String firstName, String lastName, String login, Language language) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.language = language;
    }

    public Account(Long id, @NotNull Long version, Set<AccessLevel> accessLevels, String firstName, String lastName) {
        super(id, version);
        this.accessLevels = accessLevels;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Account(@NotNull Long version, Set<AccessLevel> accessLevels, String firstName, String lastName) {
        super(version);
        this.accessLevels = accessLevels;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Account(String login, Long version, String email, String firstName, String lastName,
                   Language language, Set<AccessLevel> accessLevelSet) {
        super(version);
        this.login = login;
        this.email = email;
        this.language = language;
        this.accessLevels = accessLevelSet;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void registerUnsuccessfulLogin(String ip) {
        this.activityTracker.setLastUnsuccessfulLogin(LocalDateTime.now());
        this.activityTracker.setLastUnsuccessfulLoginIp(ip);
        this.activityTracker.incrementUnsuccessfulLoginChainCounter();
    }

    public void registerSuccessfulLogin(String ip) {
        this.activityTracker.setLastSuccessfulLogin(LocalDateTime.now());
        this.activityTracker.setLastSuccessfulLoginIp(ip);
        this.activityTracker.setUnsuccessfulLoginChainCounter(0);
    }

    public boolean hasAccessLevel(AccessType accessType) {
        return accessLevels.stream()
            .filter(AccessLevel::isActive)
            .filter(AccessLevel::isVerified)
            .anyMatch(x -> x.getLevel() == accessType);
    }

    public boolean isAbleToAuthenticate() {
        boolean hasActiveAccessLevels = getAccessLevels().stream()
            .anyMatch(AccessLevel::isActive);

        return (hasActiveAccessLevels && active && verified);
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
