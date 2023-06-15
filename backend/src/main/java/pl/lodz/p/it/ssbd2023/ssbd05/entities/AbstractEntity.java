package pl.lodz.p.it.ssbd2023.ssbd05.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;

import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractEntity {

    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "version", updatable = false)
    @Version
    @NotNull
    @Getter
    private long version;

    @Getter
    @Setter
    @Basic(optional = false)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "created_by")
    private Account createdBy;

    @Getter
    @Setter
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Getter
    @Setter
    @OneToOne()
    @JoinColumn(name = "updated_by")
    private Account updatedBy;

    public AbstractEntity(long version) {
        this.version = version;
    }

    public AbstractEntity(Long id, long version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AbstractEntity other)) {
            return false;
        }
        return (this.getId() != null || other.getId() == null)
            && (this.getId() == null || this.getId().equals(other.getId()));
    }
}
