package fj.control.db;

import fj.Unit;
import fj.data.Option;
import fj.function.Try1;
import org.apache.commons.dbutils.DbUtils;
import org.junit.Test;

import java.sql.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestDbState {
    @Test
    public void testWriter() throws SQLException {
        final int TEN = 10;
        DbState writer = DbState.writer(DbState.driverManager("jdbc:h2:mem:"));

        DB<Unit> setup = DB.db((Try1<Connection, Unit, SQLException>) c -> {
            Statement s = null;
            try {
                s = c.createStatement();
                assertThat(s.executeUpdate("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255))"), is(0));
                assertThat(s.executeUpdate("INSERT INTO TEST (ID, NAME) VALUES (" + TEN + ", 'FOO')"), is(1));
            } finally {
                DbUtils.closeQuietly(s);
            }
            return Unit.unit();
        });

        DB<Option<Integer>> query = new DB<Option<Integer>>() {
            @Override
            public Option<Integer> run(Connection c) throws SQLException {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = c.prepareStatement("SELECT ID FROM TEST WHERE NAME = ?");
                    ps.setString(1, "FOO");
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        return Option.some(rs.getInt("ID"));
                    } else {
                        return Option.none();
                    }
                } finally {
                    DbUtils.closeQuietly(rs);
                    DbUtils.closeQuietly(ps);
                }
            }
        };
        assertThat(writer.run(setup.bind(v -> query)).some(), is(TEN));
    }
}
