package gt.app.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

public class PrefixedNamingStrategy extends SpringPhysicalNamingStrategy {
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        Identifier identifier = super.toPhysicalTableName(name, jdbcEnvironment);

        return Identifier.toIdentifier("G_" + identifier.getText().toUpperCase());

    }

}
