package jp.co.hogehoge.framework.db;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ResultSetパーサー。
 * 
 * @param <R> SQL実行結果のデータ型
 */
abstract class ResultSetParser<R> {

	/** パラメータ R のクラス */
	protected Class<? extends Entity> clazz;

	/**
	 * コンストラクタ。
	 * 
	 * @param clazz パース対象のエンティティクラス
	 */
	public ResultSetParser(Class<? extends Entity> clazz) {
		this.clazz = clazz;
	}

	/**
	 * ResultSetを単一のエンティティへ変換するパーサーを提供する。
	 * 
	 * @param       <R> SQL実行結果のデータ型
	 * @param clazz パース対象のエンティティクラス
	 * @return ResultSetのパーサー
	 */
	public static <R extends Entity> ResultSetParser<Optional<R>> toEntity(Class<? extends Entity> clazz) {
		return new ResultSetParser<Optional<R>>(clazz) {
			@Override
			@SuppressWarnings("unchecked")
			public Optional<R> parse(ResultSet rs) throws SQLException {
				return Optional.ofNullable(rs.next() ? (R) parseEntity(rs) : null);
			}
		};
	}

	/**
	 * ResultSetをエンティティのリストへ変換するパーサーを提供する。
	 * 
	 * @param       <R> SQL実行結果のデータ型
	 * @param clazz パース対象のエンティティクラス
	 * @return ResultSetのパーサー
	 */
	public static <R extends Entity> ResultSetParser<List<R>> toEntityList(Class<? extends Entity> clazz) {
		return new ResultSetParser<List<R>>(clazz) {
			@Override
			@SuppressWarnings("unchecked")
			public List<R> parse(ResultSet rs) throws SQLException {
				List<R> list = new ArrayList<>();
				while (rs.next()) {
					list.add((R) parseEntity(rs));
				}
				return list;
			}
		};
	}

	/**
	 * ResultSetからEntityを構築する。
	 * 
	 * @param rs ResultSet
	 * @return Entity
	 * @throws SQLException
	 */
	protected Entity parseEntity(ResultSet rs) throws SQLException {
		try {
			Entity entity = this.clazz.newInstance();
			Class<? extends Entity> clazz = entity.getClass();
			ResultSetMetaData data = rs.getMetaData();

			for (int index = 1; index <= data.getColumnCount(); index++) {
				String label = data.getColumnLabel(index);

				// ラベル名はスネークケース（大文字）で取得されるためキャメルケースへ変換
				Field field = clazz.getDeclaredField(toCamelCase(label));
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				try {
					Object value = rs.getObject(label);
					if (value instanceof String) {
						value = ((String) value).trim(); // CLOB、CHAR、VARCHAR
					} else if (value instanceof Timestamp) {
						value = ((Timestamp) value).toLocalDateTime(); // TIMESTAMP
					} else if (value instanceof Date) {
						value = ((Date) value).toLocalDate(); // DATE
					} else if (value instanceof Clob) {
						value = ((Clob) value).getSubString(1L, (int) ((Clob) value).length()); // CLOB
					} else if (value instanceof Blob) {
						value = ((Blob) value).getBytes(1L, (int) ((Blob) value).length()); // BLOB
					}
					field.set(entity, value);
				} finally {
					field.setAccessible(accessible);
				}
			}
			return entity;
		} catch (Exception e) {
			throw new SQLException(Message.DBE00002.get(), e);
		}
	}

	/**
	 * スネークケースの文字列をキャメルケースへ変換する。
	 * 
	 * @param snake スネークケース文字列（全て大文字）
	 * @return キャメルケース文字列
	 */
	protected String toCamelCase(String snake) {
		final StringBuilder sb = new StringBuilder(snake.length());
		for (int i = 0; i < snake.length(); i++) {
			char c = snake.charAt(i);
			sb.append((c == '_' && ++i < snake.length()) ? snake.charAt(i) : Character.toLowerCase(c));
		}
		return sb.toString();
	}

	public abstract R parse(ResultSet rs) throws SQLException;

}
