package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "account")
@SecondaryTable(name = "account_data")
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "Account.findAllAccounts",
        query = "SELECT a FROM Account a"),
    @NamedQuery(
        name = "Account.findByLogin",
        query = "SELECT a FROM Account a WHERE a.login = :login"),
    @NamedQuery(
        name = "Account.findById",
        query = "SELECT a FROM Account a WHERE a.id = :id"),
    @NamedQuery(
        name = "Account.findByFirstName",
        query = "SELECT a FROM Account a WHERE a.firstName = :firstname"),
    @NamedQuery(
        name = "Account.findByLastName",
        query = "SELECT a FROM Account a WHERE a.lastName = :lastname"),
    @NamedQuery(
        name = "Account.findByEmail",
        query = "SELECT a FROM Account a WHERE a.email = :email"),
    @NamedQuery(
        name = "Account.findByLanguage",
        query = "SELECT a FROM Account a WHERE a.language = :language"),
    @NamedQuery(
        name = "Account.findAllVerifiedAccounts",
        query = "SELECT a FROM Account a WHERE a.verified = TRUE"),
    @NamedQuery(
        name = "Account.findAllNotVerifiedAccounts",
        query = "SELECT a FROM Account a WHERE a.verified = FALSE"),
    @NamedQuery(
        name = "Account.findAllActiveAccounts",
        query = "SELECT a FROM Account a WHERE a.active = TRUE"),
    @NamedQuery(
        name = "Account.findAllNotActiveAccounts",
        query = "SELECT a FROM Account a WHERE a.active = FALSE")
})
public class Account extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Getter
    private final Set<AccessLevel> accessLevels = new HashSet<>();

    @NotNull
    @Basic(optional = false)
    @Email
    @Size(min = 3, max = 320)
    @Column(name = "email", nullable = false, unique = true)
    @Getter
    @Setter
    private String email;

    @NotNull
    @Basic(optional = false)
    @Size(min = 3, max = 100)
    @Column(name = "login", nullable = false, unique = true)
    @Getter
    @Setter
    private String login;

    @NotNull
    @Basic(optional = false)
    @Column(name = "password", nullable = false)
    @Getter
    @Setter
    private String password;

    @NotNull
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "first_name", table = "account_data", nullable = false, length = 100)
    @Getter
    @Setter
    private String firstName;

    @NotNull
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "last_name", table = "account_data", nullable = false, length = 100)
    @Getter
    @Setter
    private String lastName;

    @NotNull
    @Basic(optional = false)
    @Column(name = "verified", nullable = false)
    @Getter
    @Setter
    private boolean verified = false;

    @NotNull
    @Basic(optional = false)
    @Column(name = "active", nullable = false)
    @Getter
    @Setter
    private boolean active = true;

    @NotNull
    @Column(name = "language", nullable = false, length = 2)
    @Size(min = 2, max = 2)
    @Basic(optional = false)
    @Getter
    @Setter
    private String language = "PL";


    @Embedded
    @Getter
    @Setter
    private ActivityTracker activityTracker = new ActivityTracker();

    public Account(String email, String password, String firstName, String lastName, String login) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
    }
}
