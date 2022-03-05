package untest.setup.mock.ds;

import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

import javax.sql.DataSource;

import jp.co.hogehoge.framework.db.Message;
import jp.co.hogehoge.framework.db.exception.DatabaseConnectionException;

public class MockDataSource implements DataSource, Closeable {

	private Connection conn = null;

	/** コネクション（スレッド毎に管理） */
	private static final ThreadLocal<Connection> CONNECTION = ThreadLocal.withInitial(() -> {
		try {
			return MockDataSource.newConnection();
		} catch (SQLException e) {
			throw new DatabaseConnectionException(e, Message.DBE00003);
		}
	});

	private static Connection newConnection() throws SQLException {
		String connectUrl = MockConfig.HOST_NAME.get() + ":" + MockConfig.PORT_NUMBER.get() + "/"
				+ MockConfig.DATABASE_NAME.get() + ":" + MockConfig.CONNECT_OPTION.get();
		return DriverManager.getConnection(connectUrl, MockConfig.USER_NAME.get(), MockConfig.PASSWORD.get());
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection conn = CONNECTION.get();
		if (Objects.isNull(this.conn) || this.conn.isClosed()) {
			conn = MockDataSource.newConnection();
			CONNECTION.set(conn);
		}
		return conn;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection();
	}

	@Override
	public void close() {
		try {
			if (Objects.nonNull(this.conn)) {
				this.conn.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {

		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

}
