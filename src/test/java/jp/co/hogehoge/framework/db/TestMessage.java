package jp.co.hogehoge.framework.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestMessage {

	/**
	 * 想定通りのメッセージが取得できること。
	 */
	@Test
	public void get_01() {
		// act
		String actual = Message.DBE00001.get();
		// assert
		assertThat("想定通りのメッセージが取得できること", actual, equalTo("トランザクション実行中にエラーが発生しました。"));
	}

	/**
	 * 想定通りのメッセージIDが取得できること。
	 */
	@Test
	public void getId_01() {
		// act
		String actual = Message.DBE00001.getId();
		// assert
		assertThat("取得したメッセージIDが想定通りであること", actual, equalTo("DBE00001"));
	}

	/**
	 * フォーマット後のメッセージが想定通りであること。
	 */
	@Test
	public void format_01() {
		// act
		String actual = Message.DBE00004.format("ABC");
		// assert
		assertThat("フォーマット後のメッセージが想定通りであること", actual, equalTo("データ・ソースの取得に失敗しました。[データ・ソース=ABC]"));
	}

}
