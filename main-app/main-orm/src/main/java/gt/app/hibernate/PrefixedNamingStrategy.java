package gt.app.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class PrefixedNamingStrategy extends PhysicalNamingStrategySnakeCaseImpl {

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        Identifier identifier = super.toPhysicalTableName(name, jdbcEnvironment);

        return Identifier.toIdentifier("G_" + identifier.getText().toUpperCase());

    }

}
