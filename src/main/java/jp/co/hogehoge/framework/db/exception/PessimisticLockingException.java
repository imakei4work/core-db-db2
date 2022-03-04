package jp.co.hogehoge.framework.db.exception;

import java.sql.SQLException;
import java.util.Optional;

import jp.co.hogehoge.framework.db.Message;

/**
 * 悲観ロック例外。 ロックを取得、或いはレコードを更新する際、すでに対象レコードがロックされており、
 * 一定時間待機してもロックが解放されず処理に失敗した場合にスローされる。
 */
public class PessimisticLockingException extends RuntimeException {

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
	public PessimisticLockingException(Throwable e, Message message, Object... arg) {
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
