package jp.naixrosoft.xronia.script.execute;

public class Value {
	public enum Type {
		NONE,
		ADDRESS,
		SUB_START,
		INT,
		DOUBLE,
		STRING,
	};

	public Type type;
	public long int_var;
	public double dbl_var;
	public String str_var;

	public Value() {
		type = Type.NONE;
		int_var = 0;
		dbl_var = 0;
		str_var = "";
	}
};