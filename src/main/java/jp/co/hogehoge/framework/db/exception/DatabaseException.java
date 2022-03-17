package jp.co.hogehoge.framework.db.exception;

import java.sql.SQLException;
import java.util.Optional;

import jp.co.hogehoge.framework.db.Message;

public abstract class DatabaseException extends RuntimeException {

	/** シリアル・バージョンID */
	private static final long serialVersionUID = 1L;

	/** Message */
	private Message message = null;

	/** SQLException */
	private Optional<SQLException> exception = Optional.empty();

	/**
	 * コンストラクタ。
	 * 
	 * @param e       例外
	 * @param message メッセージ
	 * @param args    メッセージ・パラメータ
	 */
	public DatabaseException(Throwable e, Message message, Object... args) {
		super(message.format(args) + e.getMessage(), e);
		this.message = message;
		if (e instanceof SQLException) {
			this.exception = Optional.of((SQLException) e);
		}
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param e 例外
	 */
	public DatabaseException(Throwable e) {
		super(e);
		if (e instanceof SQLException) {
			this.exception = Optional.of((SQLException) e);
		}
	}

	/**
	 * メッセージIDを取得する。
	 * 
	 * @return メッセージID
	 */
	public String getMessageId() {
		return this.message.getId();
	}

	/**
	 * エラーコードを取得する。
	 * 
	 * @return エラーコード
	 */
	public Integer getErrorCode() {
		return this.exception.isPresent() ? this.exception.get().getErrorCode() : null;
	}

	/**
	 * SQLステートを取得する。
	 * 
	 * @return SQLステート
	 */
	public String getSqlState() {
		return this.exception.isPresent() ? this.exception.get().getSQLState() : "NONE";
	}

}
