package jp.co.hogehoge.framework.db.exception;

import java.sql.SQLException;

import jp.co.hogehoge.framework.db.Message;

public class DatabaseConnectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** エラーコード */
	private Integer errorCode = null;

	/** メッセージID */
	private String messageId = "none";

	/** SQLステート */
	private String sqlState = "none";

	/**
	 * コンストラクタ。
	 * 
	 * @param e       エラー情報
	 * @param message メッセージ
	 * @param arg     メッセージ置換パラメータ
	 */
	public DatabaseConnectionException(Throwable e, Message message, Object... arg) {
		super(message.format(arg), e);
		this.messageId = message.getId();
		// SQLExceptionの場合のみ、エラーコードとSQLステートを取得
		if (e instanceof SQLException) {
			SQLException se = (SQLException) e;
			this.errorCode = se.getErrorCode();
			this.sqlState = se.getSQLState();
		}
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param message メッセージ
	 */
	public DatabaseConnectionException(Message message) {
		this(null, message);
	}

	/**
	 * メッセージIDを取得する。
	 * 
	 * @return メッセージID
	 */
	public String getMessageId() {
		return this.messageId;
	}

	/**
	 * エラーコードを取得する。
	 * 
	 * @return エラーコード
	 */
	public Integer getErrorCode() {
		return this.errorCode;
	}

	/**
	 * SQLステートを取得する。
	 * 
	 * @return SQLステート
	 */
	public String getSqlState() {
		return this.sqlState;
	}

}
