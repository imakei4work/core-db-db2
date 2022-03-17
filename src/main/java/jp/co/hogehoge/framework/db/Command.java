package jp.co.hogehoge.framework.db;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.ibm.db2.jcc.DB2ParameterMetaData;
import com.ibm.db2.jcc.DB2PreparedStatement;

/**
 * SQL命令。
 *
 * @param <R> SQL実行結果のデータ型
 */
abstract class Command<R> {

	/**
	 * SELECT処理を実行するExecutorを提供する。
	 * 
	 * @param <R> SQL実行結果のデータ型
	 * @return Executor
	 */
	public static <R> Command<R> select() {
		return new Command<R>() {
			@Override
			public R execute(DatabaseConnection conn, String sql, Map<String, Object> param, ResultSetParser<R> parser)
					throws SQLException {
				R result = null;
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					setPreparedStatement(ps, param);
					try (ResultSet rs = ps.executeQuery()) {
						result = parser.parse(rs);
					}
				}
				return result;
			}
		};
	}

	/**
	 * UPDATE処理を実行するExecutorを提供する。
	 * 
	 * @return Executor
	 */
	public static Command<Integer> update() {
		return new Command<Integer>() {
			@Override
			public Integer execute(DatabaseConnection conn, String sql, Map<String, Object> param,
					ResultSetParser<Integer> parser) throws SQLException {
				Integer result = null;
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					setPreparedStatement(ps, param);
					result = ps.executeUpdate();
				}
				return result;
			}
		};
	}

	/**
	 * SQLのINSERT処理を実行するExecutorを提供する。
	 * 
	 * @return Executor
	 */
	public static Command<Integer> insert() {
		return update();
	}

	/**
	 * SQLのDELETE処理を実行するExecutorを提供する。
	 * 
	 * @return Executor
	 */
	public static Command<Integer> delete() {
		return update();
	}

	/**
	 * SQLのCREATE処理を実行するExecutorを提供する。
	 * 
	 * @return Executor
	 */
	public static Command<Integer> create() {
		return update();
	}

	/**
	 * SQLのDROP処理を実行するExecutorを提供する。
	 * 
	 * @return Executor
	 */
	public static Command<Integer> drop() {
		return update();
	}

	/**
	 * プリペアード・ステートメントを設定する。
	 * パラメータのフィールド変数名とSQLのマーカーが一致する箇所にパラメータを設定する。
	 * 
	 * @param pstmt プリペアード・ステートメント
	 * @param param パラメータ（非null）
	 * @return パラメータ・マーカー情報
	 * @throws SQLException
	 */
	protected void setPreparedStatement(PreparedStatement pstmt, Map<String, Object> param) throws SQLException {
		if (Objects.nonNull(param) && !param.isEmpty()) {

			// パラメータ・マーカーリストを取得
			DB2PreparedStatement db2pstmt = (DB2PreparedStatement) pstmt;
			List<String> markers = Arrays
					.asList(((DB2ParameterMetaData) db2pstmt.getParameterMetaData()).getParameterMarkerNames());

			// SQLのパラメータ・マーカーに存在するフィールドのみパラメータを設定
			for (Entry<String, Object> entry : param.entrySet()) {
				String mark = entry.getKey();
				if (markers.contains(mark)) {
					Object value = entry.getValue();
					if (Objects.isNull(value)) {
						db2pstmt.setJccObjectAtName(mark, null);
					} else if (value instanceof String) {
						db2pstmt.setJccStringAtName(mark, (String) value); // CLOB、CHAR、VARCHAR
					} else if (value instanceof LocalDateTime) {
						db2pstmt.setJccTimestampAtName(mark, Timestamp.valueOf((LocalDateTime) value)); // TIMESTAMP
					} else if (value instanceof LocalDate) {
						db2pstmt.setJccDateAtName(mark, Date.valueOf((LocalDate) value)); // DATE
					} else if (value instanceof Integer) {
						db2pstmt.setJccIntAtName(mark, ((Integer) value).intValue()); // INTEGER
					} else if (value instanceof Double) {
						db2pstmt.setJccDoubleAtName(mark, ((Double) value).doubleValue()); // DOUBLE
					} else if (value instanceof byte[]) {
						db2pstmt.setJccBytesAtName(mark, (byte[]) value); // BLOB
					} else if (value instanceof BigDecimal) {
						db2pstmt.setJccBigDecimalAtName(mark, (BigDecimal) value); // DECIMAL
					} else if (value instanceof Long) {
						db2pstmt.setJccLongAtName(mark, (Long) value); // BIGINT
					} else {
						throw new SQLException(Message.DBE00006.format(mark, value.getClass().getCanonicalName()));
					}
				}
			}
		}
	}

	/**
	 * SQLを実行する。
	 * 
	 * @param        <P> SQL実行パラメータのデータ型
	 * @param        <R> SQL実行結果のデータ型
	 * @param conn   データベース・コネクション
	 * @param sql    SQL
	 * @param param  SQL実行パラメータ
	 * @param parser ResultSetパーサー
	 * @return SQL実行結果
	 * @throws SQLException
	 */
	public abstract R execute(DatabaseConnection conn, String sql, Map<String, Object> param, ResultSetParser<R> parser)
			throws SQLException;
}
