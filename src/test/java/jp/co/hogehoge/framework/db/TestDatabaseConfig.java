package jp.co.hogehoge.framework.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestDatabaseConfig {

	/**
	 * データ・ソース名が取得できること。
	 */
	@Test
	public void DATA_SOURCE_NAME_01() {
		// act
		String actual = DatabaseConfig.DATA_SOURCE_NAME.get();
		// assert
		assertThat("データ・ソース・パスが取得できること", actual, equalTo("java:comp/env/jdbc"));
	}

	/**
	 * リトライ対象エラーコードリストが取得できること。
	 */
	@Test
	public void RETRY_ERROR_CODE_01() {
		// act
		List<Integer> actual = DatabaseConfig.RETRY_ERROR_CODE.get();
		// assert
		assertThat("リトライ対象エラーコードリストが取得できること", actual, equalTo(Arrays.asList(-911, -913)));
	}

	/**
	 * リトライ回数が取得できること。
	 */
	@Test
	public void RETRY_COUNT_01() {
		// act
		Integer actual = DatabaseConfig.RETRY_COUNT.get();
		// assert
		assertThat("リトライ回数が取得できること", actual, equalTo(5));
	}

	/**
	 * リトライ時の待機時間が取得できること。
	 */
	@Test
	public void RETRY_WAIT_01() {
		// act
		Integer actual = DatabaseConfig.RETRY_WAIT.get();
		// assert
		assertThat("リトライ時の待機時間が取得できること", actual, equalTo(1000));
	}

}
