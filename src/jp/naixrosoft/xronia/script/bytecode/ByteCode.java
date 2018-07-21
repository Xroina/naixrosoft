package jp.naixrosoft.xronia.script.bytecode;

import java.util.ArrayList;
import java.util.List;

import jp.naixrosoft.xronia.script.exception.ByteCodeException;

/**
 * バイトコードクラス
 *
 * @author xronia
 *
 */
public class ByteCode {

	/**
	 * オペコード
	 *
	 * @author xronia
	 *
	 */
	public enum OpCode {
		PUSH_INT,
		PUSH_STRING,
		ADD,
		SUB,
		MUL,
		DIV,
		MINUS,
		EQ,
		NE,
		GT,
		GE,
		LT,
		LE,
		PUSH_VAR,
		ASSIGN,
		JUMP,
		JUMP_IF_ZERO,
		GOSUB,
		RETURN,
		PRINT,
		DEBUG,
		PUSH_SUB_START,
		PUSH_LAST_ADDRESS,
		PUSH_DOUBLE,
		OR,
		AND,
		NOT,
		LEFT_SHIFT,
		RIGHT_SHIFT,
		OR2,
		AND2,
		REMAIND,
		CLS,
		LOCATE,
		TIME,
		RANDOMIZE,
		RND,
		ABS,
		PI,
		RAD,
		DEG,
		SIN,
		COS,
		TAN,
		ASIN,
		ACOS,
		ATAN,
		SQR,
		LOG,
		SIG,
		INT,
		STICK_X,
		STICK_Y,
		CHARACTER,
		SCROLL_NEXT,	// 一行上へスクロール
		SCROLL_PREV,	// 一行下へスクロール
		SCROLL_LEFT,	// １列左へスクロール
		SCROLL_RIGHT,	// １列右へスクロール
		BUTTON,
	};

	private static OpCode[] enums = OpCode.values();	// オペコードの配列

	private class Code {
		private long   int_var;
		private double dbl_var;
		private String str_var;

		public Code(long var) {
			int_var = var;
			dbl_var = 0;
			str_var = "";
		}
		public Code(double var) {
			int_var = 0;
			dbl_var = var;
			str_var = "";
		}
		public Code(String var) {
			int_var = 0;
			dbl_var = 0;
			str_var = var;
		}
	}
	List<Code> code;

	/**
	 * コンストラクタ
	 */
	public ByteCode() {
		code = new ArrayList<>();
	}

	/**
	 * バイトコードに値追加
	 *
	 * @param var	値
	 */
	public void add(Code var) {
		code.add(var);
	}

	/**
	 * バイトコードに値(整数)追加
	 *
	 * @param var	値(整数)
	 */
	public void add(long var) {
		code.add(new Code(var));
	}

	/**
	 * バイトコードに値(実数)追加
	 *
	 * @param var	値(実数)
	 */
	public void add(double var) {
		code.add(new Code(var));
	}

	/**
	 * バイトコードに値(文字列)追加
	 *
	 * @param var	値(文字列)
	 */
	public void add(String var) {
		code.add(new Code(var));
	}

	/**
	 * バイトコードにコード追加
	 *
	 * @param c		コード
	 */
	public void add(OpCode c) {
		add((long)c.ordinal());
	}

	/**
	 * バイトコードから値取得
	 *
	 * @param pc	プログラムポインタ
	 * @return		値
	 * @throws ByteCodeException
	 */
	public Code get(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return code.get(pc);
	}

	/**
	 * バイトコードから値(整数)取得
	 *
	 * @param pc	プログラムポインタ
	 * @return		値(整数)
	 * @throws ByteCodeException
	 */
	public long getInt(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return code.get(pc).int_var;
	}

	/**
	 * バイトコードから値(実数)取得
	 *
	 * @param pc	プログラムポインタ
	 * @return		値(実数)
	 * @throws ByteCodeException
	 */
	public double getDouble(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return code.get(pc).dbl_var;
	}

	/**
	 * バイトコードから値(文字列)取得
	 *
	 * @param pc	プログラムポインタ
	 * @return		値(文字列)
	 * @throws ByteCodeException
	 */
	public String getString(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return code.get(pc).str_var;
	}

	/**
	 * バイトコードからコード取得
	 *
	 * @param pc	プログラムポインタ
	 * @return		コード
	 * @throws ByteCodeException
	 */
	public OpCode getOpCode(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return enums[(int)getInt(pc)];
	}

	/**
	 * プログラムポインタを指定して値(整数)を設定
	 *
	 * @param pc	プログラムポインタ
	 * @param var	値(整数)
	 * @throws ByteCodeException
	 */
	public void setInt(int pc, long var) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		Code c = code.get(pc);
		c.int_var = var;
		code.set(pc, c);
	}

	/**
	 * バイトコードサイズ取得
	 *
	 * @return	バイトコードサイズ
	 */
	public int size() {
		return code.size();
	}

	/**
	 * toStringメソッド
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());

		s.append(" ByteCode:\n");
		for(int i = 0; i < code.size(); i++) {
			try {
				OpCode opcode = getOpCode(i);
				s.append(String.valueOf(i) + ":" + opcode);
				switch(opcode) {
				case PUSH_STRING:
					i++;
					s.append(" " + getString(i));
					break;

				case PUSH_DOUBLE:
					i++;
					s.append(" " + String.valueOf(getDouble(i)));
					break;

				case PUSH_INT:
				case PUSH_VAR:
				case ASSIGN:
				case JUMP:
				case JUMP_IF_ZERO:
				case GOSUB:
					i++;
					s.append(" " + String.valueOf(getInt(i)));
					break;

				default:
					break;
				}
			}catch(ByteCodeException e) {
				s.append(e.toString());
			}
			s.append("\n");
		}
		s.append(":End");

		return s.toString();
	}

	/**
	 * デバックプリント
	 *
	 * @throws ByteCodeException
	 */
	public void debug() {
		System.out.println(this.toString());
	}
}
