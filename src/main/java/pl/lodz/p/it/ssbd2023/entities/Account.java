package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "account")
@SecondaryTable(name = "account_data")
@NoArgsConstructor
public class Account extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
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
    @Column(name = "first_name", table = "account_data", nullable = false)
    @Getter
    @Setter
    private String firstName;

    @NotNull
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "last_name", table = "account_data", nullable = false)
    private String lastName;

    @NotNull
    @Basic(optional = false)
    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @NotNull
    @Basic(optional = false)
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "language")
    @Size(min = 2, max = 2)
    @NotNull
    @Basic(optional = false)
    private String language;


    @Embedded
    @Getter
    @Setter
    private ActivityTracker activityTracker = new ActivityTracker();

    public Account(String email, String password, String name, String surname) {
        this.email = email;
        this.password = password;
        this.firstName = name;
        this.lastName = surname;
    }
}
