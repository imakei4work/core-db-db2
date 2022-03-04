package jp.co.hogehoge.framework.db.exception;

import java.sql.SQLException;
import java.util.Optional;

import jp.co.hogehoge.framework.db.Message;

/**
 * データベース実行時例外。 トランザクション処理でエラーが発生した場合はこの例外が必ずスローされる。
 */
public class TransactionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** Message */
	private Message message = null;

	/** SQLException */
	private Optional<SQLException> exception = Optional.empty();

	/**
	 * コンストラクタ。
	 * 
	 * @param e       エラー情報
	 * @param message メッセージ
	 * @param arg     メッセージ置換パラメータ
	 */
	public TransactionException(Throwable e, Message message, Object... arg) {
		super(message.format(arg) + e.getMessage(), e);
		// メッセージの取得
		this.message = message;
		// SQLExceptionの取得
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
