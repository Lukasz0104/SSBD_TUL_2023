package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.*;
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
    @Column(name = "email", nullable = false)
    @Getter @Setter
    private String email;

    @NotNull
    @Basic(optional = false)
    @Column(name = "password", nullable = false)
    @Getter @Setter
    private String password;

    @NotNull
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "name", table = "account_data", nullable = false)
    @Getter @Setter
    private String name;

    @NotNull
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "surname", table = "account_data", nullable = false)
    private String surname;

    @NotNull
    @Basic(optional = false)
    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @NotNull
    @Basic(optional = false)
    @Column(name = "blocked", nullable = false)
    private boolean blocked = false;

    public Account(String email, String password, String name, String surname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }
}
