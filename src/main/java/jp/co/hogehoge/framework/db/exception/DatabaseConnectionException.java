package jp.co.hogehoge.framework.db.exception;

import jp.co.hogehoge.framework.db.Message;

public class DatabaseConnectionException extends DatabaseException {

	/** シリアル・バージョンID */
	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ。
	 * 
	 * @param e       エラー情報
	 * @param message メッセージ
	 * @param args    メッセージ置換パラメータ
	 */
	public DatabaseConnectionException(Throwable e, Message message, Object... args) {
		super(e, message, args);
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param message メッセージ
	 */
	public DatabaseConnectionException(Message message) {
		this(null, message);
	}

}
