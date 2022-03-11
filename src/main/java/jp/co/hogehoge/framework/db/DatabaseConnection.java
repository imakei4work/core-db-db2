package jp.co.hogehoge.framework.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.MissingResourceException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import jp.co.hogehoge.framework.db.exception.DatabaseConnectionException;

/**
 * データベース・コネクション。
 */
public class DatabaseConnection implements AutoCloseable {

	/** コネクション */
	private Connection conn = null;

	/** コネクション（スレッド毎に管理） */
	private static final ThreadLocal<DatabaseConnection> CONNECTION = ThreadLocal.withInitial(() -> {
		try {
			return new DatabaseConnection(DatabaseDataSource.INSTANCE.getConnection());
		} catch (Exception e) {
			throw new DatabaseConnectionException(e, Message.DBE00003);
		}
	});

	/**
	 * データ・ソース格納クラス。
	 * 
	 * データ・ソースを通常のstatic変数として定義し、同様の処理にて初期化（※）した場合、
	 * データ・ソースがContextにbindされる前にlookupが実行されるため、データ・ソースが取得できない。
	 * （※staticイニシャライザを想定。それ以外の方法で初期化した場合、排他制御が必要になる。）
	 * 
	 * 内部クラスのstatic変数で定義した場合、親クラスがロードされたタイミングでは初期化されず、
	 * 最初にアクセスされたタイミングで初期化される遅延ロードとなるため、
	 * データ・ソースがContextのlookupにて取得される前にbindすることが出来る。
	 */
	private static class DatabaseDataSource {

		/** データ・ソース・インスタンス */
		private static DataSource INSTANCE = initialize();

		/**
		 * データ・ソース初期化処理。
		 * 
		 * @return データ・ソース
		 */
		private static DataSource initialize() {
			try {
				return (DataSource) InitialContext.doLookup(Config.DATA_SOURCE_PATH.get());
			} catch (NamingException e) {
				MissingResourceException me = new MissingResourceException(
						Message.DBE00004.format(Config.DATA_SOURCE_PATH.get()),
						Config.DATA_SOURCE_PATH.get(),
						Config.DATA_SOURCE_PATH.get());
				me.addSuppressed(e);
				throw me;
			}
		}
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param conn コネクション
	 */
	private DatabaseConnection(Connection conn) {
		this.conn = conn;
	}

	/**
	 * コネクションを取得する。
	 * 
	 * @return データベース・コネクション
	 * @throws DatabaseConnectionException 新規コネクションの取得に失敗した場合
	 */
	protected static DatabaseConnection getConnection() {
		DatabaseConnection conn = CONNECTION.get();
		boolean closed = true;
		try {
			closed = conn.isClosed();
		} catch (Exception e) {
			// エラーが発生した場合はコネクションを再取得
		}
		if (closed) {
			try {
				conn = new DatabaseConnection(DatabaseDataSource.INSTANCE.getConnection());
				CONNECTION.set(conn);
			} catch (Exception e) {
				throw new DatabaseConnectionException(e, Message.DBE00003);
			}
		}
		return conn;
	}

	/**
	 * プリペアード・ステートメントを取得する。
	 * 詳細は{@link Connection#prepareStatement(String)}を参照。
	 * 
	 * @param sql SQL
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	protected PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.conn.prepareStatement(sql);
	}

	/**
	 * コネクションがクローズされているかどうかを取得する。
	 * 詳細は{@link Connection#isClosed()}を参照。
	 * 
	 * @return 判定結果
	 * @throws SQLException
	 */
	protected boolean isClosed() throws SQLException {
		return this.conn.isClosed();
	}

	/**
	 * コミットを行う。
	 * 詳細は{@link Connection#commit()}を参照。
	 * 
	 * @return this
	 * @throws SQLException
	 */
	protected DatabaseConnection commit() throws SQLException {
		this.conn.commit();
		return this;
	}

	/**
	 * ロールバックを行う。
	 * 詳細は{@link Connection#rollback()}を参照。
	 * 
	 * @return this
	 * @throws SQLException
	 */
	protected DatabaseConnection rollback() throws SQLException {
		this.conn.rollback();
		return this;
	}

	/**
	 * コネクションをクローズする。
	 * 詳細は{@link Connection#close()}を参照。
	 * 
	 * @throws SQLException
	 */
	@Override
	public void close() throws SQLException {
		this.conn.close();
	}
}
