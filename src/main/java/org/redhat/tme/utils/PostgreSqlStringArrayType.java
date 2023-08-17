package org.redhat.tme.utils;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

public class PostgreSqlStringArrayType implements UserType<String[]> {

    @Override
    public int getSqlType() {
        return 0;
    }

    @Override
    public Class returnedClass() {
        return String[].class;
    }

    @Override
    public boolean equals(String[] x, String[] y) {
        if (x instanceof String[] && y instanceof String[]) {
            return Arrays.deepEquals((String[])x, (String[])y);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(String[] strings) {
        return Arrays.hashCode(strings);
    }

    @Override
    public String[] nullSafeGet(ResultSet resultSet, int i, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {
        Array array = resultSet.getArray(i);
        return array != null ? (String[]) array.getArray() : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, String[] strings, int i, SharedSessionContractImplementor session) throws SQLException {
        if (strings != null && preparedStatement != null) {
            Array array = session.getJdbcConnectionAccess().obtainConnection().createArrayOf("text", strings);
            preparedStatement.setArray(i, array);
        } else {
            preparedStatement.setNull(i, getSqlType());
        }
    }

    @Override
    public String[] deepCopy(String[] strings) {
        String[] a = strings;
        if (a == null) return null;
        return Arrays.copyOf(a, a.length);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(String[] strings) {
        return null;
    }

    @Override
    public String[] assemble(Serializable serializable, Object o) {
        return new String[0];
    }
}
