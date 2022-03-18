package untest.entity;

import jp.co.hogehoge.framework.db.Entity;

public class NotSupportTypeEntity extends Entity {

	private AllTypeEntity varcharColumn = null;

	public AllTypeEntity getVarcharColumn() {
		return varcharColumn;
	}

	public NotSupportTypeEntity setVarcharColumn(AllTypeEntity varcharColumn) {
		this.varcharColumn = varcharColumn;
		return this;
	}

}
