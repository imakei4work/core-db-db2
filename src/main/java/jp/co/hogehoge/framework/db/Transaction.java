package jp.co.hogehoge.framework.db;

import java.sql.SQLException;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.co.hogehoge.framework.db.exception.DatabaseConnectionException;
import jp.co.hogehoge.framework.db.exception.TransactionException;

/**
 * トランザクション。
 */
public class Transaction {

	/** logger */
	protected static Logger logger = LogManager.getLogger(Transaction.class);

	/** トランザクション・スタック */
	private static final ThreadLocal<Stack<Object>> TRANSACTION = ThreadLocal.withInitial(() -> new Stack<>());

	/**
	 * コンストラクタ
	 */
	private Transaction() {
		// NOP
	}

	/**
	 * トランザクションを実行する。
	 * 
	 * @param supplier トランザクション処理
	 * @return SQL実行結果
	 * @throws DatabaseConnectionException コネクションの取得に失敗した場合
	 * @throws TransactionException        コネクション取得後、トランザクション内で何らかのエラーが発生した場合
	 */
	public static <R> R execute(TransactionSupplier<R> supplier) {
		Stack<Object> stack = TRANSACTION.get();
		DatabaseConnection conn = null;
		R result = null;
		try {
			// コネクションを取得
			logger.debug("データベース・コネクション取得 開始");
			conn = DatabaseConnection.getConnection();
			logger.debug("データベース・コネクション取得 終了");

			// 生成したトランザクションをトランザクション・スタックへ追加
			stack.push(supplier);

			// トランザクションを実行
			logger.debug("データベース・トランザクション 開始");
			result = supplier.execute();
			logger.debug("データベース・トランザクション 終了");

			// トランザクション・スタックからポップ
			stack.pop();
			// トランザクションが先頭（トランザクション・スタックが空）の場合にコミット
			if (stack.isEmpty()) {
				logger.debug("データベース・コミット 開始");
				conn.commit();
				logger.debug("データベース・コミット 終了");
			}
		} catch (DatabaseConnectionException e) {
			// コネクションの取得に失敗した場合、クローズやロールバック等、コネクションに対する処理が実行不可であるため、
			// トランザクション・スタックのクリアのみ実行して例外をスローする
			stack.clear();
			throw new TransactionException(e, Message.DBE00001);
		} catch (TransactionException e) {
			// ネストしたトランザクションにてエラーが発生した場合はそのままスロー
			throw e;
		} catch (Exception e) {
			// トランザクション内でエラーが発生した場合トランザクション・スタックをクリアしてロールバックを行う
			stack.clear();
			try {
				logger.debug("データベース・ロールバック 開始");
				conn.rollback();
				logger.debug("データベース・ロールバック 終了");
				throw new TransactionException(e, Message.DBE00007);
			} catch (SQLException se) {
				se.addSuppressed(e);
				throw new TransactionException(se, Message.DBE00008);
			}
		} finally {
			try {
				logger.debug("データベース・クローズ 開始");
				conn.close();
				logger.debug("データベース・クローズ 終了");
			} catch (Exception e) {
				// NOP
			}
		}
		return result;
	}

	/**
	 * トランザクションを実行する。
	 * 
	 * @param consumer トランザクション処理
	 * @throws DatabaseConnectionException コネクションの取得に失敗した場合
	 * @throws TransactionException        コネクション取得後、トランザクション内で何らかのエラーが発生した場合
	 */
	public static void execute(TransactionConsumer consumer) {
		Stack<Object> stack = TRANSACTION.get();
		DatabaseConnection conn = null;
		try {
			// コネクションを取得
			logger.debug("データベース・コネクション取得 開始");
			conn = DatabaseConnection.getConnection();
			logger.debug("データベース・コネクション取得 終了");

			// 生成したトランザクションをトランザクション・スタックへ追加
			stack.push(consumer);

			// トランザクションを実行
			logger.debug("データベース・トランザクション 開始");
			consumer.execute();
			logger.debug("データベース・トランザクション 終了");

			// トランザクション・スタックからポップ
			stack.pop();
			// トランザクションが先頭（トランザクション・スタックが空）の場合にコミット
			if (stack.isEmpty()) {
				logger.debug("データベース・コミット 開始");
				conn.commit();
				logger.debug("データベース・コミット 終了");
			}
		} catch (DatabaseConnectionException e) {
			// コネクションの取得に失敗した場合、クローズやロールバック等、コネクションに対する処理が実行不可であるため、
			// トランザクション・スタックのクリアのみ実行して例外をスローする
			stack.clear();
			throw new TransactionException(e, Message.DBE00001);
		} catch (TransactionException e) {
			// ネストしたトランザクションにてエラーが発生した場合はそのままスロー
			throw e;
		} catch (Exception e) {
			// トランザクション内でエラーが発生した場合トランザクション・スタックをクリアしてロールバックを行う
			stack.clear();
			try {
				logger.debug("データベース・ロールバック 開始");
				conn.rollback();
				logger.debug("データベース・ロールバック 終了");
				throw new TransactionException(e, Message.DBE00007);
			} catch (SQLException se) {
				se.addSuppressed(e);
				throw new TransactionException(se, Message.DBE00008);
			}
		} finally {
			try {
				logger.debug("データベース・クローズ 開始");
				conn.close();
				logger.debug("データベース・クローズ 終了");
			} catch (Exception e) {
				// NOP
			}
		}
	}

	/**
	 * トランザクション実行インタフェース。
	 *
	 * @param <R> SQL実行結果のデータ型
	 */
	public static interface TransactionSupplier<R> {
		public R execute() throws SQLException;
	}

	/**
	 * トランザクション実行インタフェース。
	 */
	public static interface TransactionConsumer {
		public void execute() throws SQLException;
	}

}
