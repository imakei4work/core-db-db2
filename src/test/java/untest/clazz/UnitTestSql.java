package untest.clazz;

import java.util.List;
import java.util.Optional;

import jp.co.hogehoge.framework.db.Sql;

public class UnitTestSql {

	private static final String FILE_NAME = "test_sql.properties";

	public static final Sql<AllTypeEntity, Integer> DLOP_001 = Sql.defineDropTable(FILE_NAME, "sql.test.drop001");

	public static final Sql<AllTypeEntity, Integer> CREATE_001 = Sql.defineCreateTable(FILE_NAME, "sql.test.create001");

	public static final Sql<AllTypeEntity, Integer> DELETE_001 = Sql.defineDeleteRecords(FILE_NAME,
			"sql.test.delete001");

	public static final Sql<AllTypeEntity, Integer> INSERT_001 = Sql.defineInsertRecord(FILE_NAME,
			"sql.test.insert001");

	public static final Sql<AllTypeEntity, Integer> UPDATE_001 = Sql.defineUpdateRecords(FILE_NAME,
			"sql.test.update001");

	public static final Sql<AllTypeEntity, Optional<AllTypeEntity>> SELECT_001 = Sql
			.defineSelectSingleRecord(FILE_NAME, "sql.test.select001", AllTypeEntity.class);

	public static final Sql<AllTypeEntity, Integer> CREATE_002 = Sql.defineCreateTable(FILE_NAME, "sql.test.create002");

	public static final Sql<AllTypeEntity, Integer> DLOP_002 = Sql.defineDropTable(FILE_NAME, "sql.test.drop002");

	public static final Sql<AllTypeEntity, List<AllTypeEntity>> SELECT_002 = Sql
			.defineSelectMultipleRecords(FILE_NAME, "sql.test.select002", AllTypeEntity.class);

	public static final Sql<AllTypeEntity, Integer> DELETE_002 = Sql.defineDeleteRecords(FILE_NAME,
			"sql.test.delete002");

	public static final Sql<AllTypeEntity, Integer> INSERT_002 = Sql.defineInsertRecord(FILE_NAME,
			"sql.test.insert002");

	public static final Sql<AllTypeEntity, Optional<AllTypeEntity>> SELECT_003 = Sql
			.defineSelectSingleRecord(FILE_NAME, "sql.test.select003", AllTypeEntity.class);

	public static final Sql<AllTypeEntity, Integer> DELETE_003 = Sql.defineDeleteRecords(FILE_NAME,
			"sql.test.delete003");

	public static final Sql<AllTypeEntity, Optional<AllTypeEntity>> SELECT_004 = Sql
			.defineSelectSingleRecord(FILE_NAME, "sql.test.select004", AllTypeEntity.class);

}
