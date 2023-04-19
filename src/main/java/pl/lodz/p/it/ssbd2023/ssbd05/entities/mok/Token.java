package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "token", uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id", "token_type"})})
@NamedQueries({
    @NamedQuery(
        name = "Token.findAll",
        query = "SELECT t FROM Token t"),
    @NamedQuery(
        name = "Token.findById",
        query = "SELECT t FROM Token t WHERE t.id = :id"),
    @NamedQuery(
        name = "Token.findByToken",
        query = "SELECT t FROM Token t WHERE t.token = :token"),
    @NamedQuery(
        name = "Token.findTokenByAccountId",
        query = "SELECT t FROM Token t WHERE t.account.id = :accountId"),
    @NamedQuery(
        name = "Token.findByTokenType",
        query = "SELECT t FROM Token t WHERE t.tokenType = :tokenType"),
    @NamedQuery(
        name = "Token.findByExpiresAt",
        query = "SELECT t FROM Token t WHERE t.expiresAt = :expiresAt"),
    @NamedQuery(
        name = "Token.findByExpiresAtAfter",
        query = "SELECT t FROM Token t WHERE t.expiresAt >= :expiresAt"),
    @NamedQuery(
        name = "Token.findByExpiresAtBefore",
        query = "SELECT t FROM Token t WHERE t.expiresAt < :expiresAt"),
    @NamedQuery(
        name = "Token.findByAccountIdAndTokenType",
        query = "SELECT t FROM Token t WHERE t.account.id = :accountId AND t.tokenType = :tokenType"),
    @NamedQuery(
        name = "Token.findByTokenTypeAndExpiresAtAfter",
        query = "SELECT t FROM Token t WHERE t.tokenType = :tokenType AND t.expiresAt >= :expiresAt"),
    @NamedQuery(
        name = "Token.findByTokenTypeAndExpiresAtBefore",
        query = "SELECT t FROM Token t WHERE t.tokenType = :tokenType AND t.expiresAt < :expiresAt"),
    @NamedQuery(
        name = "Token.findByAccountIdAndExpiresAtAfter",
        query = "SELECT t FROM Token t WHERE t.account.id = :accountId AND t.expiresAt >= :expiresAt"),
    @NamedQuery(
        name = "Token.findByAccountIdAndExpiresAtBefore",
        query = "SELECT t FROM Token t WHERE t.account.id = :accountId AND t.expiresAt < :expiresAt")
})
public class Token extends AbstractEntity {

    @NotNull
    @Basic(optional = false)
    @Column(name = "token", unique = true, updatable = false, nullable = false)
    private UUID token;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", referencedColumnName = "id", updatable = false, nullable = false)
    private Account account;

    @NotNull
    @Basic(optional = false)
    @Column(name = "expires_at", updatable = false, nullable = false)
    private LocalDateTime expiresAt;

    @NotNull
    @Basic(optional = false)
    @Column(name = "token_type", updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    public Token(UUID token, Account account, TokenType tokenType) {
        this.token = token;
        this.account = account;
        this.tokenType = tokenType;
        this.expiresAt = switch (tokenType) {
            case REFRESH_TOKEN -> LocalDateTime.now().plusHours(24);
            case PASSWORD_RESET_TOKEN -> LocalDateTime.now().plusMinutes(15);
            default -> LocalDateTime.now().plusHours(2);
        };
    }
}

