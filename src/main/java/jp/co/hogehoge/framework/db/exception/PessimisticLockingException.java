package jp.co.hogehoge.framework.db.exception;

import jp.co.hogehoge.framework.db.Message;

/**
 * 悲観ロック例外。
 * ロックを取得、或いはレコードを更新する際、すでに対象レコードがロックされており、
 * 一定時間待機してもロックが解放されず処理に失敗した場合にスローされる。
 */
public class PessimisticLockingException extends DatabaseException {

	/** シリアル・バージョンID */
	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ。
	 * 
	 * @param e       エラー情報
	 * @param message メッセージ
	 * @param args    メッセージ置換パラメータ
	 */
	public PessimisticLockingException(Throwable e, Message message, Object... args) {
		super(e, message, args);
	}

}
