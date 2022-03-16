package jp.co.hogehoge.framework.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.hogehoge.framework.test.db.TestDB;
import untest.clazz.AllTypeEntity;
import untest.clazz.NotSupportTypeEntity;
import untest.clazz.UnitTestSql;
import untest.conf.DBConfig;

public class TestCommand {

	// 初期登録データ
	private final AllTypeEntity initData1 = (new AllTypeEntity()).setVarcharColumn("12345")
			.setCharColumn("12345")
			.setIntegerColumn(1)
			.setDateColumn(LocalDate.now())
			.setDoubleColumn(12.5)
			.setClobColumn("あいうえお")
			.setBlobColumn("あいうえお".getBytes())
			.setBigDecimalColumn(BigDecimal.valueOf(22.30))
			.setLongColumn((long) 12345)
			.setTimestampColumn(LocalDateTime.now());

	// 初期登録データ2
	private final AllTypeEntity initData2 = (new AllTypeEntity()).setVarcharColumn("54321")
			.setCharColumn("12345")
			.setIntegerColumn(1)
			.setDateColumn(LocalDate.now())
			.setDoubleColumn(12.5)
			.setClobColumn("あいうえお")
			.setBlobColumn("あいうえお".getBytes())
			.setBigDecimalColumn(BigDecimal.valueOf(22.30))
			.setLongColumn((long) 12345)
			.setTimestampColumn(LocalDateTime.now());

	// データ・ソースの設定
	@BeforeClass
	public static void beforeClass() {
		TestDB.setup(Config.DATA_SOURCE_PATH.get(), // データ・ソース
				DBConfig.HOST_NAME.get(), // ホスト名
				DBConfig.PORT_NUMBER.get(), // ポート番号
				DBConfig.DATABASE_NAME.get(), // データベース名
				DBConfig.USER_NAME.get(), // ユーザー名
				DBConfig.PASSWORD.get(), // パスワード
				DBConfig.CONNECT_OPTION.get()); // 接続オプション
	}

	// テスト用テーブルの構築と初期データの登録
	@Before
	public void before() {
		Transaction.execute(() -> {
			try {
				UnitTestSql.DLOP_001.execute();
			} catch (Exception e) {
			} finally {
				UnitTestSql.CREATE_001.execute();
				UnitTestSql.INSERT_001.execute(initData1);
				UnitTestSql.INSERT_001.execute(initData2);
			}
		});
	}

	/**
	 * 検索結果が想定通りであること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void select_01() throws SQLException {
		// act
		Command<Optional<AllTypeEntity>> actual = Command.select();
		// assert
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			Optional<AllTypeEntity> result = actual.execute(conn,
					UnitTestSql.SELECT_001.getSql(),
					initData1.toMap(),
					ResultSetParser.toEntity(AllTypeEntity.class));
			conn.commit();
			assertThat("検索結果が想定通りであること", result.get(), equalTo(initData1));
		}
	}

	/**
	 * 更新結果が想定通りであること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void update_01() throws SQLException {
		// arrange
		AllTypeEntity data = initData1.clone().setCharColumn("00000");
		// act
		Command<Integer> actual = Command.update();
		// assert
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			Integer result = actual.execute(conn, UnitTestSql.UPDATE_001.getSql(), data.toMap(), null);
			conn.commit();
			assertThat("更新結果が想定通りであること", result, equalTo(1));
		}
	}

	/**
	 * 登録結果が想定通りであること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void insert_01() throws SQLException {
		// arrange
		AllTypeEntity data = initData1.clone().setVarcharColumn("00000");
		// act
		Command<Integer> actual = Command.insert();
		// assert
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			Integer result = actual.execute(conn, UnitTestSql.INSERT_001.getSql(), data.toMap(), null);
			conn.commit();
			assertThat("登録結果が想定通りであること", result, equalTo(1));
		}
	}

	/**
	 * 削除結果が想定通りであること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void delete_01() throws SQLException {
		// act
		Command<Integer> actual = Command.delete();
		// assert
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			Integer result = actual.execute(conn, UnitTestSql.DELETE_002.getSql(), initData1.toMap(), null);
			conn.commit();
			assertThat("削除結果が想定通りであること", result, equalTo(1));
		}
	}

	/**
	 * 作成結果が想定通りであること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void create_01() throws SQLException {
		// arrange
		Transaction.execute(() -> UnitTestSql.DLOP_001.execute());
		// act
		Command<Integer> actual = Command.create();
		// assert
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			Integer result = actual.execute(conn, UnitTestSql.CREATE_001.getSql(), null, null);
			conn.commit();
			assertThat("作成結果が想定通りであること", result, equalTo(0));
		}
	}

	/**
	 * 消去結果が想定通りであること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void drop_01() throws SQLException {
		// act
		Command<Integer> actual = Command.drop();
		// assert
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			Integer result = actual.execute(conn, UnitTestSql.DLOP_001.getSql(), null, null);
			conn.commit();
			assertThat("消去結果が想定通りであること", result, equalTo(0));
		}
	}

	/**
	 * パラメータがnullの場合に正常に処理が終了すること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void setPreparedStatement_01() throws SQLException {
		// arrange
		Command<Integer> command = Command.create();
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			try (PreparedStatement pstmt = conn.prepareStatement(UnitTestSql.CREATE_001.getSql())) {
				// act
				command.setPreparedStatement(pstmt, null);
				// assert
				conn.commit();
			}
		}
	}

	/**
	 * パラメータがEmptyの場合でも正常に処理が終了すること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void setPreparedStatement_02() throws SQLException {
		// arrange
		Command<Integer> command = Command.create();
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			try (PreparedStatement pstmt = conn.prepareStatement(UnitTestSql.CREATE_001.getSql())) {
				// act
				command.setPreparedStatement(pstmt, new HashMap<>());
				// assert
				conn.commit();
			}
		}
	}

	/**
	 * 全てのデータ型に対して正常に設定が出来ること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void setPreparedStatement_03() throws SQLException {
		// arrange
		Command<Integer> command = Command.create();
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			try (PreparedStatement pstmt = conn.prepareStatement(UnitTestSql.INSERT_001.getSql())) {
				// act
				command.setPreparedStatement(pstmt, initData1.toMap());
				// assert
				conn.commit();
			}
		}
	}

	/**
	 * 全てのデータ型がnull（主キーのみ非null）の場合に正常に設定が出来ること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void setPreparedStatement_04() throws SQLException {
		// arrange
		AllTypeEntity data = (new AllTypeEntity()).setVarcharColumn("55555")
				.setCharColumn(null)
				.setIntegerColumn(null)
				.setDateColumn(null)
				.setDoubleColumn(null)
				.setClobColumn(null)
				.setBlobColumn(null)
				.setBigDecimalColumn(null)
				.setLongColumn(null)
				.setTimestampColumn(null);

		Command<Integer> command = Command.create();
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			try (PreparedStatement pstmt = conn.prepareStatement(UnitTestSql.INSERT_001.getSql())) {
				// act
				command.setPreparedStatement(pstmt, data.toMap());
				// assert
				conn.commit();
			}
		}
	}

	/**
	 * サポートしていないデータ型がEntityに含まれていた場合はSQLExceptionがスローされること。
	 * 
	 * @throws SQLException
	 */
	@Test(expected = SQLException.class)
	public void setPreparedStatement_05() throws SQLException {
		NotSupportTypeEntity entity = (new NotSupportTypeEntity()).setVarcharColumn(initData1);
		Command<Integer> command = Command.create();
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			try (PreparedStatement pstmt = conn.prepareStatement(UnitTestSql.SELECT_001.getSql())) {
				try { // act
					command.setPreparedStatement(pstmt, entity.toMap());
					fail("実行された場合はNG");
				} finally {
					conn.commit();
				}
			}
		}
	}

}
