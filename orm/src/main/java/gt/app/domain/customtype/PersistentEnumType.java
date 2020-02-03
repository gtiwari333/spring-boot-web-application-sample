package gt.app.domain.customtype;

import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

@SuppressWarnings("unchecked")
public class PersistentEnumType implements UserType, DynamicParameterizedType, Serializable {

    private Class<? extends PersistentEnum> enumClass;
    private EnumValueMapper enumValueMapper;
    private int sqlType;

    @Override
    public void setParameterValues(Properties parameters) {

        final String enumClassName = (String) parameters.get(RETURNED_CLASS);

        try {
            enumClass = ReflectHelper.classForName(enumClassName, this.getClass()).asSubclass(PersistentEnum.class);
        } catch (ClassNotFoundException exception) {
            throw new HibernateException("Enum class not found: " + enumClassName, exception);
        } catch (ClassCastException exception) {
            throw new HibernateException("Enum class " + enumClassName + " must extend " + PersistentEnum.class, exception);
        }

        this.enumValueMapper = new PersistentEnumValueMapper();
        this.sqlType = enumValueMapper.getSqlType();
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{sqlType};
    }

    @Override
    public Class<? extends PersistentEnum> returnedClass() {
        return enumClass;
    }

    @Override
    public boolean equals(Object x, Object y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) {
        return x == null ? 0 : x.hashCode();
    }

    /**
     * db to enum
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        if (enumValueMapper == null) {
            throw new AssertionFailure("EnumType (" + enumClass.getName() + ") not properly, fully configured");
        }
        return enumValueMapper.getValue(rs, names);
    }

    /**
     * String to db
     */
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (enumValueMapper == null) {
            throw new AssertionFailure("EnumType (" + enumClass.getName() + ") not properly, fully configured");
        }
        enumValueMapper.setValue(st, (PersistentEnum) value, index);
    }

    @Override
    public Object deepCopy(Object value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return original;
    }

    private interface EnumValueMapper extends Serializable {
        int getSqlType();

        PersistentEnum getValue(ResultSet rs, String[] names) throws SQLException;

        void setValue(PreparedStatement st, PersistentEnum value, int index) throws SQLException;

    }

    private class PersistentEnumValueMapper implements EnumValueMapper, Serializable {

        @Override
        public int getSqlType() {
            return Types.VARCHAR; //map it as varchar
        }

        @Override
        public void setValue(PreparedStatement st, PersistentEnum value, int index) throws SQLException {
            final Object jdbcValue = value == null ? null : value.getPersistedValue();

            if (jdbcValue == null) {
                st.setNull(index, getSqlType());
                return;
            }

            st.setObject(index, jdbcValue, PersistentEnumType.this.sqlType);
        }

        @Override
        public PersistentEnum getValue(ResultSet rs, String[] names) throws SQLException {
            final String value = rs.getString(names[0]);

            if (rs.wasNull()) {
                return null;
            }

            return getByPersistedValue(value);
        }

        private PersistentEnum getByPersistedValue(String persistedValue) {
            if (persistedValue == null) {
                return null;
            }

            return Arrays.stream(returnedClass().getEnumConstants())
                .filter(en -> persistedValue.equalsIgnoreCase(en.getPersistedValue()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown value [%s] for enum class [%s]", persistedValue, enumClass.getName())));
        }

    }
}
