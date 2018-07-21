package jp.naixrosoft.xronia.script.execute;

/**
 * 変数クラス
 *
 * @author xronia
 *
 */
public class Value {
	/**
	 * 変数タイプ列挙型
	 *
	 * @author xronia
	 *
	 */
	public enum Type {
		NONE,
		ADDRESS,
		SUB_START,
		INT,
		DOUBLE,
		STRING,
	};

	private Type type;			// 変数タイプ
	private long int_var;		// 値(整数)
	private double dbl_var;		// 値(実数)
	private String str_var;		// 値(文字列)

	/**
	 * コントラクタ
	 */
	public Value() {
		setType(Type.NONE);
		setInteger(0);
		setDouble(0);
		setString("");
	}

	/**
	 * 変数タイプ取得
	 *
	 * @return		変数タイプ
	 */
	public Type getType() {
		return type;
	}

	/**
	 * 変数タイプ設定
	 *
	 * @param type	変数タイプ
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 値(整数)取得
	 *
	 * @return	値(整数)
	 */
	public long getInteger() {
		return int_var;
	}

	/**
	 * 値(整数)設定
	 *
	 * @param str_var	値(整数)
	 */
	public void setInteger(long int_var) {
		this.int_var = int_var;
		this.setType(Type.INT);
	}

	/**
	 * 値(実数)取得
	 *
	 * @return	値(実数)
	 */
	public double getDouble() {
		return dbl_var;
	}

	/**
	 * 値(実数)設定
	 *
	 * @param str_var	値(実数)
	 */
	public void setDouble(double dbl_var) {
		this.dbl_var = dbl_var;
		this.setType(Type.DOUBLE);
	}

	/**
	 * 値(文字列)取得
	 *
	 * @return	値(文字列)
	 */
	public String getString() {
		return str_var;
	}

	/**
	 * 値(文字列)設定
	 *
	 * @param str_var	値(文字列)
	 */
	public void setString(String str_var) {
		this.str_var = str_var;
		this.setType(Type.STRING);
	}

	/**
	 * toStringメソッド
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		s.append(" ");
		switch(this.getType()) {
		case INT:
		case ADDRESS:
			s.append(String.valueOf(this.getInteger()));
			break;
		case DOUBLE:
			s.append(String.valueOf(this.getDouble()));
			break;
		case STRING:
			s.append(this.getString());
			break;
		default:
			break;
		}
		s.append("(").append(this.getType()).append(")");
		return s.toString();
	}
}