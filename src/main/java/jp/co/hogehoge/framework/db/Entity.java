package jp.co.hogehoge.framework.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * エンティティ。 SQL実行パラメータ・クラス、SQL実行結果クラスは必ず継承すること。
 */
public abstract class Entity {

	/**
	 * エンティティをマップへ変換する。 マップのキーはフィールド名称をスネークケース（大文字）へ変換した値とする。
	 * 
	 * @return マップ
	 */
	protected Map<String, Object> toMap() {
		Map<String, Object> param = new HashMap<>();
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				if (!field.getName().contains("$")) {
					param.put(toSnakeUpperCase(field.getName()), field.get(this));
				}
				field.setAccessible(accessible);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// NOP
		}
		return param;
	}

	/**
	 * キャメルケースの文字列をスネークケース（大文字）へ変換する。
	 * 
	 * @param camel キャメルケース文字列
	 * @return スネークケース文字列
	 */
	protected String toSnakeUpperCase(String camel) {
		final StringBuilder sb = new StringBuilder(camel.length() * 2);
		for (int i = 0; i < camel.length(); i++) {
			final char c = camel.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append('_').append(c);
			} else {
				sb.append(Character.toUpperCase(c));
			}
		}
		return sb.toString();
	}

}
