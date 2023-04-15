package pl.lodz.p.it.ssbd2023.ssbd05.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Connection;

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd05admin",
    className = "org.postgresql.ds.PGSimpleDataSource",
    user = "ssbd05admin",
    password = "admin",
    serverName = "db",
    portNumber = 5432,
    databaseName = "ebok",
    initialPoolSize = 1,
    minPoolSize = 0,
    maxPoolSize = 1,
    maxIdleTime = 10,
    transactional = true,
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd05mow",
    className = "org.postgresql.ds.PGSimpleDataSource",
    user = "ssbd05mow",
    password = "mow",
    serverName = "db",
    portNumber = 5432,
    databaseName = "ebok",
    transactional = true,
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd05mok",
    className = "org.postgresql.ds.PGSimpleDataSource",
    user = "ssbd05mok",
    password = "mok",
    serverName = "db",
    portNumber = 5432,
    databaseName = "ebok",
    transactional = true,
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd05auth",
    className = "org.postgresql.ds.PGSimpleDataSource",
    user = "ssbd05auth",
    password = "auth",
    serverName = "db",
    portNumber = 5432,
    databaseName = "ebok",
    transactional = true,
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@Stateless
public class JDBCConfig {
    @PersistenceContext(unitName = "ssbd05adminPU")
    private EntityManager em;
}
