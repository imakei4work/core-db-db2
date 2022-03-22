package jp.co.hogehoge.framework.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import mockit.Mock;
import mockit.MockUp;
import untest.entity.AllTypeEntity;

public class TestEntity {

	/**
	 * 想定通りの変換結果が得られること。
	 */
	@Test
	public void toMap_01() {
		// arrange
		AllTypeEntity entity = (new AllTypeEntity()).setVarcharColumn("12345")
				.setCharColumn("12345")
				.setIntegerColumn(1)
				.setDateColumn(LocalDate.now())
				.setDoubleColumn(12.5)
				.setClobColumn("あいうえお")
				.setBlobColumn("あいうえお".getBytes())
				.setBigDecimalColumn(BigDecimal.valueOf(22.30))
				.setLongColumn((long) 12345)
				.setTimestampColumn(LocalDateTime.now());
		// act
		Map<String, Object> actual = entity.toMap();
		// assert
		Map<String, Object> expect = new HashMap<>();
		expect.put("VARCHAR_COLUMN", entity.getVarcharColumn());
		expect.put("CHAR_COLUMN", entity.getCharColumn());
		expect.put("INTEGER_COLUMN", entity.getIntegerColumn());
		expect.put("DATE_COLUMN", entity.getDateColumn());
		expect.put("DOUBLE_COLUMN", entity.getDoubleColumn());
		expect.put("CLOB_COLUMN", entity.getClobColumn());
		expect.put("BLOB_COLUMN", entity.getBlobColumn());
		expect.put("BIG_DECIMAL_COLUMN", entity.getBigDecimalColumn());
		expect.put("LONG_COLUMN", entity.getLongColumn());
		expect.put("TIMESTAMP_COLUMN", entity.getTimestampColumn());
		assertThat("想定通りの変換結果が得られること", actual, equalTo(expect));
	}

	/**
	 * Exceptionがスローされず変換結果が得られること。
	 */
	@Test
	public void toMap_02() {
		// arrange
		AllTypeEntity entity = new AllTypeEntity();
		entity.setCharColumn("12345").setVarcharColumn("54321");
		new MockUp<Field>() {
			@Mock
			public String getName() {
				throw new IllegalArgumentException("mock error.");
			}
		};
		// act
		Map<String, Object> actual = entity.toMap();
		// assert
		assertTrue("Exceptionがスローされず変換結果が得られること", actual.isEmpty());
	}

	/**
	 * 想定されるスネークケースへ変換されること。
	 */
	@Test
	public void toSnakeUpperCase_01() {
		// arrange
		AllTypeEntity entity = new AllTypeEntity();
		// act
		String actual = entity.toSnakeUpperCase("varChar");
		// assert
		assertThat("想定されるスネークケースへ変換されること", actual, equalTo("VAR_CHAR"));
	}

	/**
	 * 想定されるスネークケースへ変換されること。
	 */
	@Test
	public void toSnakeUpperCase_02() {
		// arrange
		AllTypeEntity entity = new AllTypeEntity();
		// act
		String actual = entity.toSnakeUpperCase("vARCHAR");
		// assert
		assertThat("想定されるスネークケースへ変換されること", actual, equalTo("V_A_R_C_H_A_R"));
	}

	/**
	 * 想定されるスネークケースへ変換されること。
	 */
	@Test
	public void toSnakeUpperCase_03() {
		// arrange
		AllTypeEntity entity = new AllTypeEntity();
		// act
		String actual = entity.toSnakeUpperCase("varchar");
		// assert
		assertThat("想定されるスネークケースへ変換されること", actual, equalTo("VARCHAR"));
	}

}
