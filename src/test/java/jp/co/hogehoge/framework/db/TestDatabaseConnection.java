package jp.co.hogehoge.framework.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import jp.co.hogehoge.framework.db.exception.DatabaseConnectionException;
import jp.co.hogehoge.framework.test.db.TestDB;
import jp.co.hogehoge.framework.test.db.ds.TestDataSource;
import mockit.Mock;
import mockit.MockUp;
import untest.conf.TestDBConfig;
import untest.sql.UnitTestSql;

public class TestDatabaseConnection {

	// データ・ソースの設定
	@BeforeClass
	public static void beforeClass() {
		TestDB.setup(DatabaseConfig.DATA_SOURCE_NAME.get(), // データ・ソース
				TestDBConfig.HOST.get(), // ホスト名
				TestDBConfig.PORT.get(), // ポート番号
				TestDBConfig.DATABASE.get(), // データベース名
				TestDBConfig.USER.get(), // ユーザー名
				TestDBConfig.PASSWORD.get(), // パスワード
				TestDBConfig.OPTION.get()); // 接続オプション
	}

	/**
	 * ThreadLocalが空の状態でコネクションが取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getConnection_01() throws SQLException {
		// arrange
		Whitebox.setInternalState(DatabaseConnection.class, "CONNECTION", new ThreadLocal<DatabaseConnection>());
		// act
		try (DatabaseConnection actual = DatabaseConnection.getConnection()) {
			// assert
			assertFalse("コネクションが取得できること", actual.isClosed());
			actual.commit();
		}
	}

	/**
	 * ThreadLocalが空出ない状態でコネクションが取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getConnection_02() throws SQLException {
		// arrange
		Whitebox.setInternalState(DatabaseConnection.class, "CONNECTION", new ThreadLocal<DatabaseConnection>());
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			conn.commit();
		}
		// act
		try (DatabaseConnection actual = DatabaseConnection.getConnection()) {
			// assert
			assertFalse("コネクションが取得できること", actual.isClosed());
			actual.commit();
		}
	}

	/**
	 * クローズされていないコネクションが再取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getConnection_03() throws SQLException {
		// arrange
		DatabaseConnection.getConnection();
		// act
		try (DatabaseConnection actual = DatabaseConnection.getConnection()) {
			// assert
			assertFalse("クローズされていないコネクションが再取得できること", actual.isClosed());
			actual.commit();
		}
	}

	/**
	 * コネクション取得エラーの場合にDatabaseConnectionExceptionがスローされること。
	 * 
	 * @throws Exception
	 */
	@Test(expected = DatabaseConnectionException.class)
	public void getConnection_04() throws Exception {
		// arrange
		new MockUp<TestDataSource>() {
			@Mock
			public Connection getConnection() throws SQLException {
				{
					throw new RuntimeException("");
				}
			};
		};
		// act
		DatabaseConnection.getConnection();
		fail("実行された場合はNG");
	}

	/**
	 * クローズ確認にてエラーが発生した場合でも正常にコネクションが取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getConnection_05() throws SQLException {
		// arrange
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			// 一度コネクションを取得してクローズする
			conn.commit();
		}
		new MockUp<DatabaseConnection>() {
			@Mock
			public boolean isClosed() throws SQLException {
				{
					throw new SQLException("");
				}
			};
		};
		// act
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			// assert
			assertTrue("クローズ確認にてエラーが発生した場合でも正常にコネクションが取得できること", Objects.nonNull(conn));
			conn.commit();
		}
	}

	/**
	 * PrepareStatementが取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void prepareStatement_01() throws SQLException {
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			// act
			try (PreparedStatement actual = conn.prepareStatement(UnitTestSql.SELECT_001.getSql())) {
				// assert
				assertTrue("PrepareStatementが取得できること", Objects.nonNull(actual));
			}
			conn.commit();
		}
	}

	/**
	 * 想定する接続状態が取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void isClosed_01() throws SQLException {
		// arrange
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			// act
			boolean actual = conn.isClosed();
			// assert
			assertFalse("接続中であること", actual);
			conn.commit();
		}
	}

	/**
	 * 想定する接続状態が取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void isClosed_02() throws SQLException {
		// arrange
		DatabaseConnection conn = DatabaseConnection.getConnection();
		conn.commit().close();
		// act
		boolean actual = conn.isClosed();
		// assert
		assertTrue("コネクションが切断されていること", actual);
	}

	/**
	 * コミット処理が正常実行できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void commit_01() throws SQLException {
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			// act
			DatabaseConnection actual = conn.commit();
			// assert
			assertTrue("コミット処理が正常実行できること", Objects.nonNull(actual));
		}
	}

	/**
	 * ロールバック処理が正常実行できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void rollback_01() throws SQLException {
		// arrange
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			// act
			DatabaseConnection actual = conn.rollback();
			// assert
			assertTrue("ロールバック処理が正常実行できること", Objects.nonNull(actual));
		}
	}

	/**
	 * コネクションが切断されること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void close_01() throws SQLException {
		// arrange
		DatabaseConnection conn = DatabaseConnection.getConnection();
		// act
		conn.commit().close();
		// assert
		assertTrue("コネクションが切断されていること", conn.isClosed());
	}

}
