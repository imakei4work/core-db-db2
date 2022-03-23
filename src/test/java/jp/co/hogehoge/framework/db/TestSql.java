package jp.co.hogehoge.framework.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.hogehoge.framework.db.exception.PessimisticLockingException;
import jp.co.hogehoge.framework.db.exception.SqlExecuteException;
import jp.co.hogehoge.framework.test.db.TestDB;
import untest.conf.TestDBConfig;
import untest.entity.AllTypeEntity;
import untest.sql.UnitTestSql;

public class TestSql {

	// SQLプロパティ名
	private static final String FILE_NAME = "test_sql.properties";

	// 初期登録データ
	private final AllTypeEntity initData = (new AllTypeEntity()).setVarcharColumn("12345")
			.setCharColumn("12345")
			.setIntegerColumn(1)
			.setDateColumn(LocalDate.now())
			.setDoubleColumn(12.5)
			.setClobColumn("あいうえお")
			.setBlobColumn("あいうえお".getBytes())
			.setBigDecimalColumn(BigDecimal.valueOf(22.30))
			.setLongColumn((long) 12345)
			.setTimestampColumn(LocalDateTime.now());

	// レコード・ロック確認用カスタム・スレッドクラス。
	private static class CustomThread extends Thread {
		private Consumer<Object> runner = null;

		public CustomThread(Consumer<Object> runner) {
			this.runner = runner;
		}

		@Override
		public void run() {
			runner.accept(null);
		}
	}

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

	// テスト用テーブルの構築と初期データの登録
	@Before
	public void before() {
		Transaction.execute(() -> {
			try {
				UnitTestSql.SELECT_001.execute(initData); // テーブルが存在していない場合エラー
				UnitTestSql.DELETE_001.execute();
			} catch (Exception e) { // テーブルが存在していない場合
				UnitTestSql.CREATE_001.execute();
			}
			try {
				UnitTestSql.SELECT_004.execute(initData); // テーブルが存在していない場合エラー
				UnitTestSql.DELETE_003.execute();
			} catch (Exception e) { // テーブルが存在していない場合
				UnitTestSql.CREATE_002.execute();
			}
			UnitTestSql.INSERT_001.execute(initData);
		});
	}

	/**
	 * 正常にテーブルが構築できることを確認する。
	 */
	@Test
	public void defineCreateTable_01() {
		// arrange
		Transaction.execute(() -> UnitTestSql.DLOP_002.execute()); // テーブルを削除しておく
		// act
		Integer actual = Transaction.execute(() -> UnitTestSql.CREATE_002.execute());
		// assert
		assertThat("テーブルが正常に作成されていること", actual, equalTo(0));
	}

	/**
	 * 正常にテーブルが削除されること。
	 */
	@Test
	public void defineDropTable_01() {
		// act
		Integer actual = Transaction.execute(() -> UnitTestSql.DLOP_002.execute());
		// assert
		assertThat("テーブルが正常に削除されていること", actual, equalTo(0));
	}

	/**
	 * 正常にレコードが削除されること。
	 */
	@Test
	public void defineDeleteRecords_01() {
		// act
		Integer actual = Transaction.execute(() -> UnitTestSql.DELETE_001.execute());
		// assert
		assertThat("正常にレコードが削除されること", actual, equalTo(1));
	}

	/**
	 * 正常にレコードが登録できること。
	 */
	@Test
	public void defineInsertRecord_01() {
		// arrange
		Transaction.execute(() -> UnitTestSql.DELETE_001.execute());
		// act
		Integer actual = Transaction.execute(() -> UnitTestSql.INSERT_001.execute(initData));
		// assert
		assertThat("正常にレコードが登録されること", actual, equalTo(1));
	}

	/**
	 * 正常にレコードが更新されること。
	 */
	@Test
	public void defineUpdateRecords_01() {
		// arrange
		AllTypeEntity data = initData.clone().setCharColumn("555");
		// act
		Integer actual = Transaction.execute(() -> UnitTestSql.UPDATE_001.execute(data));
		// assert
		assertThat("正常にレコードが更新されること", actual, equalTo(1));
	}

	/**
	 * 正常にレコードが取得できること。
	 */
	@Test
	public void defineSelectSingleRecord_01() {
		// act
		Optional<AllTypeEntity> actual = Transaction.execute(() -> UnitTestSql.SELECT_001.execute(initData));
		// assert
		assertTrue("正常にレコードが取得できること", actual.get().equals(initData));
	}

	/**
	 * 正常にレコードが取得できること。
	 */
	@Test
	public void defineSelectMultipleRecords_01() {
		// act
		List<AllTypeEntity> actual = Transaction.execute(() -> UnitTestSql.SELECT_002.execute(initData));
		// assert
		assertThat("レコードの取得件数が正しいこと", actual.size(), equalTo(1));
		assertThat("正常にレコードが取得できること", actual.get(0), equalTo(initData));
	}

	/**
	 * 定義したSQLが正常に実行できること。
	 */
	@Test
	public void define_01() {
		// act
		Sql<AllTypeEntity, Optional<AllTypeEntity>> actual = Sql.define(FILE_NAME,
				"sql.test.select001",
				Command.select(),
				ResultSetParser.toEntity(AllTypeEntity.class));
		// assert
		Optional<AllTypeEntity> data = Transaction.execute(() -> actual.execute(initData));
		assertThat("正常にレコードが取得できること", data.get(), equalTo(initData));
	}

	/**
	 * SQL定義が正常に実行出来ること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void execute_01() throws SQLException {
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			// act
			Integer actual = UnitTestSql.INSERT_002.execute(initData);
			// assert
			conn.commit();
			assertThat("正常にレコードが登録できること", actual, equalTo(1));
		}
	}

	/**
	 * リトライ対象外のエラーが発生した場合にSqlExecuteExceptionがスローされること。
	 * 
	 * @throws SQLException
	 */
	@Test(expected = SqlExecuteException.class)
	public void execute_02() throws SQLException {
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			UnitTestSql.INSERT_002.execute(initData.clone().setCharColumn("00000000"));
		}

	}

	/**
	 * レコード・ロックが発生するが、リトライ内でロックが解除された場合にSQLExceptionが発生しないこと。
	 * 
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	@Test
	public void execute_03() throws InterruptedException, SQLException {
		// arrange
		AllTypeEntity data = initData.clone().setCharColumn("55555");
		CustomThread child = new CustomThread(arg -> {
			// act
			try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
				UnitTestSql.SELECT_003.execute(initData);
				UnitTestSql.UPDATE_001.execute(data);
				conn.commit();
			} catch (SQLException e) {
				// NOP
			}
		});
		CustomThread main = new CustomThread(arg -> {
			Transaction.execute(() -> {
				try {
					UnitTestSql.SELECT_003.execute(initData);
					child.start();
					Thread.sleep(DatabaseConfig.RETRY_WAIT.get() * 2);
					DatabaseConnection.getConnection().commit(); // レコード・ロック解除
					child.join();
				} catch (InterruptedException e) {
					// NOP
				}
				return null;
			});
		});
		main.start();
		main.join();
		// assert
		Optional<AllTypeEntity> result = Transaction.execute(() -> UnitTestSql.SELECT_001.execute(initData));
		assertThat("更新されたデータが取得できること", result.get(), equalTo(data));
	}

	/**
	 * 待機中（Thread.sleep）で例外が発生しても正常にSQLが実行出来ること。
	 * 
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	@Test
	public void execute_04() throws InterruptedException, SQLException {
		// arrange
		AllTypeEntity data = initData.clone().setCharColumn("55555");

		CustomThread child = new CustomThread(arg -> {
			// act
			try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
				UnitTestSql.SELECT_003.execute(initData); // select for update
				UnitTestSql.UPDATE_001.execute(data);
				conn.commit();
			} catch (SQLException e) {
				// NOP
			}
		});
		CustomThread timer = new CustomThread(arg -> {
			try {
				Thread.sleep(DatabaseConfig.RETRY_WAIT.get());
				child.interrupt();
			} catch (InterruptedException e) {
				// NOP
			}
		});
		CustomThread main = new CustomThread(arg -> {
			Transaction.execute(() -> {
				try {
					UnitTestSql.SELECT_003.execute(initData); // select for update
					child.start();
					timer.start();
					Thread.sleep(DatabaseConfig.RETRY_WAIT.get() * 2);
					DatabaseConnection.getConnection().commit(); // レコード・ロック解除
					child.join();
					timer.join();
				} catch (InterruptedException e) {
					// NOP
				}
				return null;
			});
		});
		main.start();
		main.join();
		// assert
		Optional<AllTypeEntity> result = Transaction.execute(() -> UnitTestSql.SELECT_001.execute(initData));
		assertThat("更新されたデータが取得できること", result.get(), equalTo(data));

	}

	/**
	 * レコード・ロックが発生するが、リトライ内でロックが解除されない場合にPessimisticLockingExceptionが発生すること。
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void execute_05() throws InterruptedException {
		// arrange
		CustomThread child = new CustomThread(arg -> {
			// act
			try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
				UnitTestSql.SELECT_003.execute(initData);
				fail("実行された場合はNG");
			} catch (PessimisticLockingException e) {
				// assert
				assertTrue("リトライ対象のエラーが発生した場合にInterruptedExceptionがスローされること", true);
			} catch (SQLException e) {
				fail("実行された場合はNG");
			}
		});
		CustomThread main = new CustomThread(arg -> {
			Transaction.execute(() -> {
				try {
					UnitTestSql.SELECT_003.execute(initData); // select for update
					child.start();
					Thread.sleep(DatabaseConfig.RETRY_WAIT.get() * DatabaseConfig.RETRY_COUNT.get());
					Thread.sleep(DatabaseConfig.RETRY_WAIT.get());
					DatabaseConnection.getConnection().commit(); // レコード・ロック解除
					child.join();
				} catch (InterruptedException e) {
					// NOP
				}
				return null;
			});
		});
		main.start();
		main.join();
	}

	/**
	 * 想定通りのSQLが取得できること。
	 */
	@Test
	public void getSql_01() {
		// arrange
		Sql<AllTypeEntity, Integer> drop = Sql.defineDropTable(FILE_NAME, "sql.test.drop001");
		// act
		String actual = drop.getSql();
		// assert
		assertThat("想定通りのSQLが取得できること", actual, equalTo("DROP TABLE UNIT_TEST_TABLE"));
	}

	/**
	 * 想定通りのSQLIDが取得できること。
	 */
	@Test
	public void getSqlId_01() {
		// arrange
		Sql<AllTypeEntity, Integer> drop = Sql.defineDropTable(FILE_NAME, "sql.test.drop001");
		// act
		String actual = drop.getSqlId();
		// assert
		assertThat("想定通りのSQLIDが取得できること", actual, equalTo("sql.test.drop001"));
	}

}
