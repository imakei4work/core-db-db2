package untest.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import jp.co.hogehoge.framework.db.Entity;

public class AllTypeEntity extends Entity {

	/** VARCHAR */
	private String varcharColumn = null;

	/** CHAR */
	private String charColumn = null;

	/** INTEGER */
	private Integer integerColumn = null;

	/** DOUBLE */
	private Double doubleColumn = null;

	/** DATE */
	private LocalDate dateColumn = null;

	/** CLOB */
	private String clobColumn = null;

	/** BLOB */
	private byte[] blobColumn = null;

	/** DECIMAL */
	private BigDecimal bigDecimalColumn = null;

	/** BIGINT */
	private Long longColumn = null;

	/** TIMESTAMP */
	private LocalDateTime timestampColumn = null;

	public String getVarcharColumn() {
		return varcharColumn;
	}

	public AllTypeEntity setVarcharColumn(String varcharColumn) {
		this.varcharColumn = varcharColumn;
		return this;
	}

	public String getCharColumn() {
		return charColumn;
	}

	public AllTypeEntity setCharColumn(String charColumn) {
		this.charColumn = charColumn;
		return this;
	}

	public Integer getIntegerColumn() {
		return integerColumn;
	}

	public AllTypeEntity setIntegerColumn(Integer integerColumn) {
		this.integerColumn = integerColumn;
		return this;
	}

	public LocalDate getDateColumn() {
		return dateColumn;
	}

	public AllTypeEntity setDateColumn(LocalDate dateColumn) {
		this.dateColumn = dateColumn;
		return this;
	}

	public Double getDoubleColumn() {
		return doubleColumn;
	}

	public AllTypeEntity setDoubleColumn(Double doubleColumn) {
		this.doubleColumn = doubleColumn;
		return this;
	}

	public String getClobColumn() {
		return clobColumn;
	}

	public AllTypeEntity setClobColumn(String clobColumn) {
		this.clobColumn = clobColumn;
		return this;
	}

	public byte[] getBlobColumn() {
		return blobColumn;
	}

	public AllTypeEntity setBlobColumn(byte[] blobColumn) {
		this.blobColumn = blobColumn;
		return this;
	}

	public BigDecimal getBigDecimalColumn() {
		return bigDecimalColumn;
	}

	public AllTypeEntity setBigDecimalColumn(BigDecimal bigDecimalColumn) {
		this.bigDecimalColumn = bigDecimalColumn;
		return this;
	}

	public Long getLongColumn() {
		return longColumn;
	}

	public AllTypeEntity setLongColumn(Long longColumn) {
		this.longColumn = longColumn;
		return this;
	}

	public LocalDateTime getTimestampColumn() {
		return timestampColumn;
	}

	public AllTypeEntity setTimestampColumn(LocalDateTime timestampColumn) {
		this.timestampColumn = timestampColumn;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AllTypeEntity) {
			AllTypeEntity trg = (AllTypeEntity) obj;
			return Objects.equals(trg.getVarcharColumn(), this.varcharColumn)
					&& Objects.equals(trg.getCharColumn(), this.charColumn)
					&& Objects.equals(trg.getIntegerColumn(), this.integerColumn)
					&& Objects.equals(trg.getDateColumn(), this.dateColumn)
					&& Objects.equals(trg.getDoubleColumn(), this.doubleColumn)
					&& Objects.equals(trg.getClobColumn(), this.clobColumn)
					&& Arrays.equals(trg.getBlobColumn(), this.blobColumn) && Objects.nonNull(trg.getBigDecimalColumn())
					&& trg.getBigDecimalColumn().compareTo(this.bigDecimalColumn) == 0
					&& Objects.equals(trg.getLongColumn(), this.longColumn);
		}
		return super.equals(obj);

	}

	public AllTypeEntity clone() {
		return (new AllTypeEntity()).setVarcharColumn(this.varcharColumn)
				.setCharColumn(this.charColumn)
				.setIntegerColumn(this.integerColumn)
				.setDateColumn(this.dateColumn)
				.setDoubleColumn(this.doubleColumn)
				.setClobColumn(this.clobColumn)
				.setBlobColumn(this.blobColumn)
				.setBigDecimalColumn(this.bigDecimalColumn)
				.setLongColumn(this.longColumn)
				.setTimestampColumn(this.timestampColumn);

	}

}
