package jp.co.hogehoge.framework.db.exception;

import jp.co.hogehoge.framework.db.Message;

/**
 * データベース実行時例外。
 * トランザクション処理でエラーが発生した場合はこの例外が必ずスローされる。
 */
public class TransactionException extends DatabaseException {

	/** シリアル・バージョンID */
	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ。
	 * 
	 * @param e       エラー情報
	 * @param message メッセージ
	 * @param args    メッセージ置換パラメータ
	 */
	public TransactionException(Throwable e, Message message, Object... args) {
		super(e, message, args);
	}

}
