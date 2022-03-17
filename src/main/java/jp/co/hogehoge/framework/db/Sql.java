package jp.co.hogehoge.framework.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.co.hogehoge.framework.db.exception.PessimisticLockingException;
import jp.co.hogehoge.framework.db.exception.SqlExecuteException;
import jp.co.hogehoge.framework.property.Property;
import jp.co.hogehoge.framework.property.PropertyType;

/**
 * SQL定義。
 *
 * @param <P> SQL実行パラメータのデータ型
 * @param <R> SQL実行結果のデータ型
 */
public abstract class Sql<P extends Entity, R> {

	/** logger */
	protected Logger logger = LogManager.getLogger(Sql.class);

	/**
	 * CREATE処理を定義する。
	 * 引数に指定されたプロパティファイル（クラスパス上）からSQLIDをキーとして実行するSQLを取得する。
	 * 正常に処理が終了した場合、戻り値は0が返却される。
	 * 
	 * @param          <P> SQL実行パラメータのデータ型
	 * @param filename プロパティファイル名
	 * @param sqlId    SQLID
	 * @return (1)
	 *         SQLデータ操作言語(DML)文の場合は行数、(2)何も返さないSQL文の場合は0
	 */
	public static <P extends Entity> Sql<P, Integer> defineCreateTable(String filename, String sqlId) {
		return define(filename, sqlId, Command.create(), null);
	}

	/**
	 * DROP処理を定義する。
	 * 引数に指定されたプロパティファイル（クラスパス上）からSQLIDをキーとして実行するSQLを取得する。
	 * 正常に処理が終了した場合、戻り値は0が返却される。
	 * 
	 * @param          <P> SQL実行パラメータのデータ型
	 * @param filename プロパティファイル名
	 * @param sqlId    SQLID
	 * @return (1)
	 *         SQLデータ操作言語(DML)文の場合は行数、(2)何も返さないSQL文の場合は0
	 */
	public static <P extends Entity> Sql<P, Integer> defineDropTable(String filename, String sqlId) {
		return define(filename, sqlId, Command.drop(), null);
	}

	/**
	 * DELETE処理を定義する。
	 * 引数に指定されたプロパティファイル（クラスパス上）からSQLIDをキーとして実行するSQLを取得する。
	 * 正常に処理が終了した場合、戻り値は削除レコード数が返却される。
	 * 
	 * @param          <P> SQL実行パラメータのデータ型
	 * @param filename プロパティファイル名
	 * @param sqlId    SQLID
	 * @return (1)
	 *         SQLデータ操作言語(DML)文の場合は行数、(2)何も返さないSQL文の場合は0
	 */
	public static <P extends Entity> Sql<P, Integer> defineDeleteRecords(String filename, String sqlId) {
		return define(filename, sqlId, Command.delete(), null);
	}

	/**
	 * INSERT処理を定義する。
	 * 引数に指定されたプロパティファイル（クラスパス上）からSQLIDをキーとして実行するSQLを取得する。
	 * 正常に処理が終了した場合、戻り値は登録レコード数が返却される。
	 * 
	 * @param          <P> SQL実行パラメータのデータ型
	 * @param filename プロパティファイル名
	 * @param sqlId    SQLID
	 * @return (1)
	 *         SQLデータ操作言語(DML)文の場合は行数、(2)何も返さないSQL文の場合は0
	 */
	public static <P extends Entity> Sql<P, Integer> defineInsertRecord(String filename, String sqlId) {
		return define(filename, sqlId, Command.insert(), null);
	}

	/**
	 * UPDATE処理を定義する。
	 * 引数に指定されたプロパティファイル（クラスパス上）からSQLIDをキーとして実行するSQLを取得する。
	 * 正常に処理が終了した場合、戻り値は更新レコード数が返却される。
	 * 
	 * @param          <P> SQL実行パラメータのデータ型
	 * @param filename プロパティファイル名
	 * @param sqlId    SQLID
	 * @return (1)SQLデータ操作言語(DML)文の場合は行数、(2)何も返さないSQL文の場合は0
	 */
	public static <P extends Entity> Sql<P, Integer> defineUpdateRecords(String filename, String sqlId) {
		return define(filename, sqlId, Command.update(), null);
	}

	/**
	 * SELECT処理により単一のレコードを取得するSQLを定義する。
	 * 引数に指定されたプロパティファイル（クラスパス上）からSQLIDをキーとして実行するSQLを取得する。
	 * 
	 * @param          <P> SQL実行パラメータのデータ型
	 * @param          <R> SQL実行結果のデータ型
	 * @param filename プロパティファイル名
	 * @param sqlId    SQLID
	 * @param clazz    SQL実行結果を格納するクラス
	 * @return SQL実行結果
	 */
	public static <P extends Entity, R extends Entity> Sql<P, Optional<R>> defineSelectSingleRecord(String filename,
			String sqlId, Class<R> clazz) {
		return define(filename, sqlId, Command.select(), ResultSetParser.toEntity(clazz));
	}

	/**
	 * SELECT処理により複数のレコードを取得するSQLを定義する。
	 * 引数に指定されたプロパティファイル（クラスパス上）からSQLIDをキーとして実行するSQLを取得する。
	 * 
	 * @param          <P> SQL実行パラメータのデータ型
	 * @param          <R> SQL実行結果のデータ型
	 * @param filename プロパティファイル名
	 * @param sqlId    SQLID
	 * @param clazz    SQL実行結果を格納するクラス
	 * @return SQL実行結果
	 */
	public static <P extends Entity, R extends Entity> Sql<P, List<R>> defineSelectMultipleRecords(String filename,
			String sqlId, Class<R> clazz) {
		return define(filename, sqlId, Command.select(), ResultSetParser.toEntityList(clazz));
	}

	/**
	 * SQLを定義する。
	 * 
	 * @param          <P> SQL実行パラメータのデータ型
	 * @param          <R> SQL実行結果のデータ型
	 * @param sqlId    SQLID
	 * @param operator オペレータ
	 * @param parser   戻り値のデータ型Rへ変換するパーサー
	 * @return
	 */
	protected static <P extends Entity, R> Sql<P, R> define(String fileName, String sqlId, Command<R> command,
			ResultSetParser<R> parser) {
		return new Sql<P, R>() {

			// SQLプロパティ定義
			private final Property<String> sql = Property.define(fileName, sqlId, "", PropertyType.isString());

			@Override
			public R execute(P param) {
				// SQL実行パラメータのマップ化とリトライ回数の取得
				Map<String, Object> args = Objects.nonNull(param) ? param.toMap() : null;
				Integer retryCount = Config.RETRY_COUNT.get();

				// SQL実行処理
				while (true) {
					try {
						String sql = this.sql.get();
						if (logger.isInfoEnabled()) {
							logger.info("SQLID={" + sqlId + "}, SQL={" + sql + "}, PARAM=" + args);
						}
						return command.execute(DatabaseConnection.getConnection(), sql, args, parser);
					} catch (SQLException e) {
						// リトライ対象のエラーコードに該当した場合はリトライ処理を実施
						if (Config.RETRY_ERROR_CODE.get().contains(e.getErrorCode())) {
							if (retryCount-- > 0) {
								try {
									logger.info("リトライ処理実行（カウント=" + retryCount + "）");
									Thread.sleep(Config.RETRY_WAIT.get()); // 一定時間待機
								} catch (InterruptedException ie) {
									// リトライ処理を継続する必要があるためエラーは無視
								}
								continue;
							} else {
								// リトライ処理が指定回数を超えた場合はエラー
								throw new PessimisticLockingException(e, Message.DBE00009, sqlId, param);
							}
						}
						// リトライ対象外のエラーは無条件でスロー
						throw new SqlExecuteException(e);
					}
				}
			}

			@Override
			public R execute() {
				return this.execute(null);
			}

			@Override
			public String getSql() {
				return this.sql.get();
			}

			@Override
			public String getSqlId() {
				return sqlId;
			}
		};
	};

	/**
	 * SQLを実行する。
	 * 
	 * @param       <P> SQL実行パラメータのデータ型
	 * @param       <R> SQL実行結果のデータ型
	 * @param param SQL実行パラメータ
	 * @return SQL実行結果
	 * @throws SqlExecuteException         SQL実行時エラー
	 * @throws PessimisticLockingException ロック取得エラー
	 */
	public abstract R execute(P param);

	/**
	 * SQLを実行する。
	 * 
	 * @param <R> SQL実行結果のデータ型
	 * @return SQL実行結果
	 * @throws SqlExecuteException         SQL実行時エラー
	 * @throws PessimisticLockingException ロック取得エラー
	 */
	public abstract R execute();

	/**
	 * SQLを取得する。
	 * 
	 * @return SQL
	 */
	public abstract String getSql();

	/**
	 * SQLIDを取得する。
	 * 
	 * @return SQLID
	 */
	public abstract String getSqlId();
}
