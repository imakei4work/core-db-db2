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
	public static <R> R start(TransactionSupplier<R> supplier) {
		Stack<Object> stack = TRANSACTION.get();
		DatabaseConnection conn = null;
		R result = null;
		try {
			// コネクションを取得
			conn = DatabaseConnection.getConnection();
			// 生成したトランザクションをトランザクション・スタックへ追加
			stack.push(supplier);
			// トランザクションを実行
			result = supplier.execute();
			// トランザクション・スタックからポップ
			stack.pop();
			// トランザクションが先頭（トランザクション・スタックが空）の場合にコミット
			if (stack.isEmpty()) {
				conn.commit();
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
				conn.rollback();
				throw new TransactionException(e, Message.DBE00007);
			} catch (SQLException se) {
				se.addSuppressed(e);
				throw new TransactionException(se, Message.DBE00008);
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
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
	public static void start(TransactionConsumer consumer) {
		Stack<Object> stack = TRANSACTION.get();
		DatabaseConnection conn = null;
		try {
			// コネクションを取得
			conn = DatabaseConnection.getConnection();
			// 生成したトランザクションをトランザクション・スタックへ追加
			stack.push(consumer);
			// トランザクションを実行
			consumer.execute();
			// トランザクション・スタックからポップ
			stack.pop();
			// トランザクションが先頭（トランザクション・スタックが空）の場合にコミット
			if (stack.isEmpty()) {
				conn.commit();
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
				conn.rollback();
				throw new TransactionException(e, Message.DBE00007);
			} catch (SQLException se) {
				se.addSuppressed(e);
				throw new TransactionException(se, Message.DBE00008);
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
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
