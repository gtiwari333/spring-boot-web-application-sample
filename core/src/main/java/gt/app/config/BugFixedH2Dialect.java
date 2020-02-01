package gt.app.config;

import org.hibernate.dialect.H2Dialect;

//https://stackoverflow.com/a/60012915/607637
public class BugFixedH2Dialect extends H2Dialect {

    @Override
    public boolean dropConstraints() {
        return true;
    }

    @Override
    public boolean supportsIfExistsAfterAlterTable() {
        return true;
    }

}
