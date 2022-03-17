package jp.co.hogehoge.framework.db.exception;

/**
 * SQL実行時例外。
 */
public class SqlExecuteException extends DatabaseException {

	/** シリアル・バージョンID */
	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ。
	 * 
	 * @param e 例外
	 */
	public SqlExecuteException(Throwable e) {
		super(e);
	}

}
