package untest.setup;

import javax.naming.InitialContext;

import org.apache.logging.log4j.ThreadContext;

import jp.co.hogehoge.framework.db.Config;
import untest.setup.mock.ds.MockDataSource;

public class UnitTest {

	// テスト実行時は以下のシステムプロパティを設定すること。
	//
	// java.naming.factory.initial =
	// setup.db.datasource.MockContextFactory
	//
	public static void setup() {
		// ログへ出力されるユーザー名の設定
		ThreadContext.put("userId", "TEST_USER");
		
		// データ・ソースの設定
		try {
			InitialContext context = new InitialContext();
			context.bind(Config.DATA_SOURCE_PATH.get(), new MockDataSource());
		} catch (Exception e) {
			throw new RuntimeException("データ・ソースの設定に失敗しました。", e);
		}
	}

}
