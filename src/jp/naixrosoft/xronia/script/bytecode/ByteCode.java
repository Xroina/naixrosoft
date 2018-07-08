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
		BUTTON,
	};

	private static OpCode[] enums = OpCode.values();	// オペコードの配列

	private class Code {
		public long   int_var;
		public double dbl_var;
		public String str_var;

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
	public void add(Code var) {
		code.add(var);
	}

	public void add(long var) {
		code.add(new Code(var));
	}

	public void add(double var) {
		code.add(new Code(var));
	}

	public void add(String var) {
		code.add(new Code(var));
	}

	public void add(OpCode c) {
		add((long)c.ordinal());
	}

	public Code get(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return code.get(pc);
	}

	public long getInt(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return code.get(pc).int_var;
	}

	public double getDouble(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return code.get(pc).dbl_var;
	}

	public String getString(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return code.get(pc).str_var;
	}

	public OpCode getOpCode(int pc) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		return enums[(int)getInt(pc)];
	}

	public void setInt(int pc, long var) throws ByteCodeException {
		if(pc < 0 || pc >= code.size())
			throw new ByteCodeException("Code Buffer OverFlow.");
		Code c = code.get(pc);
		c.int_var = var;
		code.set(pc, c);
	}

	public int size() {
		return code.size();
	}

	public void debug() throws ByteCodeException {
		System.out.println("ByteCode:");
		for(int i = 0; i < code.size(); i++) {
			OpCode opcode = getOpCode(i);
			System.out.print(String.valueOf(i) + ":" + opcode);
			switch(opcode) {
			case PUSH_STRING:
				i++;
				System.out.print(" " + getString(i));
				break;

			case PUSH_DOUBLE:
				i++;
				System.out.print(" " + String.valueOf(getDouble(i)));
				break;

			case PUSH_INT:
			case PUSH_VAR:
			case ASSIGN:
			case JUMP:
			case JUMP_IF_ZERO:
			case GOSUB:
				i++;
				System.out.print(" " + String.valueOf(getInt(i)));
				break;

			default:
				break;
			}
			System.out.println("");
		}
		System.out.println(":End ByteCode");
	}
}
