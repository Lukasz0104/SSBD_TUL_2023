package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "access_level")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "level")
@NoArgsConstructor
public abstract class AccessLevel extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Basic(optional = false)
    @Column(name = "level", updatable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private AccessTypes level;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "account", referencedColumnName = "id", updatable = false)
    @Getter
    @Setter
    private Account account;

    public AccessLevel(AccessTypes accessTypes, Account account) {
        this.level = accessTypes;
        this.account = account;
    }
}
