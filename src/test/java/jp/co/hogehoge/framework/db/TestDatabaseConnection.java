package jp.co.hogehoge.framework.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.hogehoge.framework.db.exception.DatabaseConnectionException;
import jp.co.hogehoge.framework.test.db.TestDB;
import jp.co.hogehoge.framework.test.db.ds.TestDataSource;
import mockit.Mock;
import mockit.MockUp;
import untest.conf.TestDBConfig;

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
	 * コネクション取得エラーの場合にDatabaseConnectionExceptionがスローされること。
	 * 
	 * @throws Exception
	 */
	@Test(expected = DatabaseConnectionException.class)
	public void getConnection_01() throws Exception {
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
	 * コネクション取得エラーの場合にDatabaseConnectionExceptionがスローされること。
	 * 
	 * @throws SQLException
	 */
	@Test(expected = DatabaseConnectionException.class)
	public void getConnection_02() throws SQLException {
		// arrange
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			// 一度コネクションを取得してクローズする
			conn.commit();
		}
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
	public void getConnection_03() throws SQLException {
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
		} catch (Exception e) {
			fail("実行された場合はNG");
		}
	}

	/**
	 * コネクションが取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getConnection_04() throws SQLException {
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
	public void getConnection_05() throws SQLException {
		// arrange
		DatabaseConnection.getConnection();
		// act
		try (DatabaseConnection actual = DatabaseConnection.getConnection()) {
			// assert
			assertFalse("クローズされていないコネクションが再取得できること", actual.isClosed());
			actual.commit();
		}
	}

}
