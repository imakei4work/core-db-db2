package jp.co.hogehoge.framework.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

import jp.co.hogehoge.framework.test.db.TestDB;
import untest.conf.TestDBConfig;
import untest.entity.AllTypeEntity;
import untest.sql.UnitTestSql;

public class TestResultSetParser {

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

	// カスタム・コマンド
	private final Command<PreparedStatement> customCommand = new Command<PreparedStatement>() {
		@Override
		public PreparedStatement execute(DatabaseConnection conn, String sql, Map<String, Object> param,
				ResultSetParser<PreparedStatement> parser) throws SQLException {
			PreparedStatement ps = conn.prepareStatement(sql);
			setPreparedStatement(ps, param);
			return ps;
		}
	};

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
	 * 検索結果が存在する場合、想定通りのパース結果が得られること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void toEntity_01() throws SQLException {
		// act
		ResultSetParser<Optional<AllTypeEntity>> parser = ResultSetParser.toEntity(AllTypeEntity.class);
		// assert
		Command<Optional<AllTypeEntity>> command = Command.select();
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			Optional<AllTypeEntity> result = command
					.execute(conn, UnitTestSql.SELECT_001.getSql(), initData1.toMap(), parser);
			conn.commit();
			assertThat("パース結果が想定通りであること", result.get(), equalTo(initData1));
		}
	}

	/**
	 * 検索結果が存在しない場合、想定通りの結果が得られること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void toEntity_02() throws SQLException {
		// arrange
		AllTypeEntity data = initData1.clone().setVarcharColumn("00000");
		// act
		ResultSetParser<Optional<AllTypeEntity>> parser = ResultSetParser.toEntity(AllTypeEntity.class);
		// assert
		Command<Optional<AllTypeEntity>> command = Command.select();
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			Optional<AllTypeEntity> result = command
					.execute(conn, UnitTestSql.SELECT_001.getSql(), data.toMap(), parser);
			conn.commit();
			assertFalse("検索結果が存在しない場合は想定通りの結果が得られること", result.isPresent());
		}
	}

	/**
	 * 検索結果が存在する場合、想定通りのパース結果が得られること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void toEntityList_01() throws SQLException {
		// act
		ResultSetParser<List<AllTypeEntity>> parser = ResultSetParser.toEntityList(AllTypeEntity.class);
		// assert
		Command<List<AllTypeEntity>> command = Command.select();
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			List<AllTypeEntity> result = command
					.execute(conn, UnitTestSql.SELECT_002.getSql(), initData1.toMap(), parser);
			conn.commit();
			assertThat("パース結果が想定通りであること", result, equalTo(Arrays.asList(initData1, initData2)));
		}
	}

	/**
	 * パース処理の結果、想定通りの値が取得できること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void parseEntity_01() throws SQLException {
		// arrange
		ResultSetParser<Optional<AllTypeEntity>> parser = ResultSetParser.toEntity(AllTypeEntity.class);
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			try (PreparedStatement ps = customCommand
					.execute(conn, UnitTestSql.SELECT_001.getSql(), initData1.toMap(), null)) {
				try (ResultSet rs = ps.executeQuery()) {
					rs.next();
					// act
					AllTypeEntity result = (AllTypeEntity) parser.parseEntity(rs);
					// assert
					conn.commit();
					assertThat("パース結果が想定通りであること", result, equalTo(initData1));
				}
			}
		}
	}

	/**
	 * パース処理にてエラーが発生した場合はSQLExceptionがスローされること。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void parseEntity_02() throws SQLException {
		// arrange
		ResultSetParser<Optional<AllTypeEntity>> parser = ResultSetParser.toEntity(AllTypeEntity.class);
		try (DatabaseConnection conn = DatabaseConnection.getConnection()) {
			try (PreparedStatement ps = customCommand
					.execute(conn, UnitTestSql.SELECT_001.getSql(), initData1.toMap(), null)) {
				try (ResultSet rs = ps.executeQuery()) {
					try {
						// act
						parser.parseEntity(rs);
						fail("例外がスローされない場合はNG");
					} catch (SQLException e) {
						// assert
						assertThat("SQLExceptionがスローされること", e.getMessage(), equalTo(Message.DBE00002.get()));
					}
				}
			} finally {
				conn.commit();
			}
		}
	}

	/**
	 * 正常にキャメルケースへ変換されることを確認する。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void toCamelCase_01() throws SQLException {
		// arrange
		ResultSetParser<String> parser = new ResultSetParser<String>(null) {
			@Override
			public String parse(ResultSet rs) throws SQLException {
				return this.toCamelCase("VAR_CHAR");
			}
		};
		// act
		String actual = parser.parse(null);
		// assert
		assertThat("想定されるキャメルケースへ変換されること", actual, equalTo("varChar"));
	}

	/**
	 * キャメルケースへ変換された場合に、最短文字列となる場合でも正しく変換されることを確認する。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void toCamelCase_02() throws SQLException {
		// arrange
		ResultSetParser<String> parser = new ResultSetParser<String>(null) {
			@Override
			public String parse(ResultSet rs) throws SQLException {
				return this.toCamelCase("V_A_R_C_H_A_R");
			}
		};
		// act
		String actual = parser.parse(null);
		// assert
		assertThat("想定されるキャメルケースへ変換されること", actual, equalTo("vARCHAR"));
	}

	/**
	 * キャメルケースへ変換された場合に、文字列長が変換しない場合でも正しく変換されることを確認する。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void toCamelCase_03() throws SQLException {
		// arrange
		ResultSetParser<String> parser = new ResultSetParser<String>(null) {
			@Override
			public String parse(ResultSet rs) throws SQLException {
				return this.toCamelCase("VARCHAR");
			}
		};
		// act
		String actual = parser.parse(null);
		// assert
		assertThat("想定されるキャメルケースへ変換されること", actual, equalTo("varchar"));
	}

	/**
	 * キャメルケースへ変換された場合に、最後がアンダースコアの場合でも正常変換されることを確認する。
	 * 
	 * @throws SQLException
	 */
	@Test
	public void toCamelCase_04() throws SQLException {
		// arrange
		ResultSetParser<String> parser = new ResultSetParser<String>(null) {
			@Override
			public String parse(ResultSet rs) throws SQLException {
				return this.toCamelCase("VARCHAR_");
			}
		};
		// act
		String actual = parser.parse(null);
		// assert
		assertThat("想定されるキャメルケースへ変換されること", actual, equalTo("varchar_"));
	}

}
