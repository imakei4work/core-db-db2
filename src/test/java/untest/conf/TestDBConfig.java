package untest.conf;

import jp.co.hogehoge.framework.property.Property;
import jp.co.hogehoge.framework.property.PropertyType;

public class TestDBConfig {

	/** プロパティファイル名 */
	private static final String FILENAME = "test_database.properties";

	/** JDBC接続時のホスト名 */
	public static final Property<String> HOST = Property
			.define(FILENAME, "db.host", "jdbc:db2://localhost", PropertyType.isString());

	/** JDBC接続時のポート番号 */
	public static final Property<String> PORT = Property
			.define(FILENAME, "db.port", "50000", PropertyType.isString());

	/** JDBC接続時のデータベース名 */
	public static final Property<String> DATABASE = Property
			.define(FILENAME, "db.name", "toybox", PropertyType.isString());

	/** JDBC接続時のユーザー名 */
	public static final Property<String> USER = Property
			.define(FILENAME, "db.username", "mrima", PropertyType.isString());

	/** JDBC接続時のパスワード */
	public static final Property<String> PASSWORD = Property
			.define(FILENAME, "db.password", "@zaq12wsx@", PropertyType.isString());

	/** JDBC接続時のオプション */
	public static final Property<String> OPTION = Property
			.define(FILENAME, "db.option", "", PropertyType.isString());

}
