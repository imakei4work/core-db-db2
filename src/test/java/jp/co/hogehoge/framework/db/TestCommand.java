package jp.co.hogehoge.framework.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.hogehoge.framework.test.db.TestDB;
import untest.clazz.AllTypeEntity;
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
		// arrange
		Command<Optional<AllTypeEntity>> actual = Command.select();
		// act
		DatabaseConnection conn = DatabaseConnection.getConnection();
		Optional<AllTypeEntity> result = actual.execute(conn,
				UnitTestSql.SELECT_001.getSql(),
				initData1.toMap(),
				ResultSetParser.toEntity(AllTypeEntity.class));
		conn.commit().close();
		// assert
		assertThat("検索結果が想定通りであること", result.get(), equalTo(initData1));
	}

}
