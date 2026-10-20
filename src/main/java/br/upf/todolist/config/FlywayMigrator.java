package br.upf.todolist.config;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

/**
 * Executa as migrations do banco (src/main/resources/db/migration) assim que a
 * aplicação sobe. Se alguma falhar, a exceção derruba o deploy, deixando o erro visível.
 */
@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class FlywayMigrator {

    private static final Logger LOG = Logger.getLogger(FlywayMigrator.class.getName());

    /**
     * O DataSource é procurado em tempo de execução (e não com @Resource): ao
     * reiniciar o GlassFish, a validação de @Resource roda antes de o recurso
     * java:app existir e a aplicação não sobe.
     */
    static final String DATASOURCE = "java:app/jdbc/todolist";

    @PostConstruct
    public void migrar() {
        DataSource dataSource;
        try {
            dataSource = InitialContext.doLookup(DATASOURCE);
        } catch (NamingException e) {
            throw new IllegalStateException("DataSource " + DATASOURCE + " não encontrado. Confira o <data-source> do web.xml.", e);
        }

        MigrateResult resultado = Flyway.configure(getClass().getClassLoader())
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .migrate();
        LOG.info(() -> "Flyway: " + resultado.migrationsExecuted
                + " migration(s) executada(s); versão atual do schema: " + resultado.targetSchemaVersion);
    }
}
