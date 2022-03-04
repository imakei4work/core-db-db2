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

	/** データ・ソース */
	private static DataSource DATA_SOURCE = null;

	/** コネクション */
	private Connection conn = null;

	/** コネクション（スレッド毎に管理） */
	private static final ThreadLocal<DatabaseConnection> CONNECTION = ThreadLocal.withInitial(() -> {
		try {
			return new DatabaseConnection(DatabaseConnection.DATA_SOURCE.getConnection());
		} catch (Exception e) {
			throw new DatabaseConnectionException(e, Message.DBE00003);
		}
	});

	// データ・ソースの初期化
	// エラーが発生した場合はExceptionInInitializerErrorがthrowされる
	static {
		try {
			InitialContext context = new InitialContext();
			DATA_SOURCE = (DataSource) context.lookup(Config.DATA_SOURCE_PATH.get());
		} catch (NamingException e) {
			MissingResourceException me = new MissingResourceException(
					Message.DBE00004.format(Config.DATA_SOURCE_PATH.get()),
					Config.DATA_SOURCE_PATH.get(),
					Config.DATA_SOURCE_PATH.get());
			me.addSuppressed(e);
			throw me;
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
				conn = new DatabaseConnection(DATA_SOURCE.getConnection());
				CONNECTION.set(conn);
			} catch (Exception e) {
				throw new DatabaseConnectionException(e, Message.DBE00003);
			}
		}
		return conn;
	}

	/**
	 * プリペアード・ステートメントを取得する。 詳細は{@link Connection#prepareStatement(String)}を参照。
	 * 
	 * @param sql SQL
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	protected PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.conn.prepareStatement(sql);
	}

	/**
	 * コネクションがクローズされているかどうかを取得する。 詳細は{@link Connection#isClosed()}を参照。
	 * 
	 * @return 判定結果
	 * @throws SQLException
	 */
	protected boolean isClosed() throws SQLException {
		return this.conn.isClosed();
	}

	/**
	 * コミットを行う。 詳細は{@link Connection#commit()}を参照。
	 * 
	 * @return this
	 * @throws SQLException
	 */
	protected void commit() throws SQLException {
		this.conn.commit();
	}

	/**
	 * ロールバックを行う。 詳細は{@link Connection#rollback()}を参照。
	 * 
	 * @return this
	 * @throws SQLException
	 */
	protected void rollback() throws SQLException {
		this.conn.rollback();
	}

	/**
	 * コネクションをクローズする。 詳細は{@link Connection#close()}を参照。
	 * 
	 * @throws SQLException
	 */
	@Override
	public void close() throws SQLException {
		this.conn.close();
	}
}
