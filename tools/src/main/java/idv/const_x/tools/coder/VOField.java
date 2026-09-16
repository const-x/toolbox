package idv.const_x.tools.coder;

public class VOField {
	public String name = null,comment= null,type= null,defValue= null;
	public boolean isStatic =false,isFinal =false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean aStatic) {
		isStatic = aStatic;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean aFinal) {
		isFinal = aFinal;
	}

	@Override
	public String toString() {
		return "VOField{" +
				"name='" + name + '\'' +
				", comment='" + comment + '\'' +
				", type='" + type + '\'' +
				", defValue='" + defValue + '\'' +
				", isStatic=" + isStatic +
				", isFinal=" + isFinal +
				'}';
	}
}
