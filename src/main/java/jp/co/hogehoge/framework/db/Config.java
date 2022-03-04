package jp.co.hogehoge.framework.db;

import java.util.Arrays;
import java.util.List;

import jp.co.hogehoge.framework.property.Property;
import jp.co.hogehoge.framework.property.PropertyType;

/**
 * データベース設定。
 */
public abstract class Config {

	/**
	 * コンストラクタ。
	 */
	private Config() {
		// NOP
	}

	/** プロパティファイル名 */
	private static final String FILENAME = "database.properties";

	/** データ・ソース名 */
	public static final Property<String> DATA_SOURCE_PATH = Property
			.define(FILENAME, "db.datasource.path", "java:comp/env/jdbc", PropertyType.isString());

	/** リトライ対象エラーコードリスト */
	public static final Property<List<Integer>> RETRY_ERROR_CODE = Property.define(FILENAME,
			"db.retry.errorcode",
			Arrays.asList(-911, -913),
			PropertyType.isIntegerList().delimitWith(";"));

	/** リトライ回数 */
	public static final Property<Integer> RETRY_COUNT = Property
			.define(FILENAME, "db.retry.count", 5, PropertyType.isInteger());

	/** リトライ時の待機時間（ms） */
	public static final Property<Integer> RETRY_WAIT = Property
			.define(FILENAME, "db.retry.wait", 1000, PropertyType.isInteger());

}
