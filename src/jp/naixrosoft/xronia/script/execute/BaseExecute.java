package jp.naixrosoft.xronia.script.execute;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;
import jp.naixrosoft.xronia.script.exception.StackException;

/**
 * 実行クラス
 *
 * @author xronia
 *
 */
public abstract class BaseExecute {
	protected final ByteCode code;
	protected Stack stack = new Stack();
	protected int sp = 0;
	protected int pc = 0;

	protected boolean running = true;

	/**
	 * コントラクタ
	 *
	 * @param c	バイトコード
	 */
	protected BaseExecute(ByteCode c) {
		code = c;
	}

	/**
	 * 停止メソッド<br>
	 *
	 * 他スレッドから停止させるときに使用
	 */
	public synchronized void stop() {
		running = false;
	}

	/**
	 * 実行中かを調べる
	 *
	 * @return	true:実行中/false:非実行
	 */
	public synchronized boolean runnable() {
		return running;
	}
	/**
	 * デバックプリント
	 *
	 * @throws ByteCodeException
	 * @throws StackException
	 */
	public void debug() throws ByteCodeException, StackException {
		System.out.println("pc:" + String.valueOf(pc) +
				" sp:" + String.valueOf(sp) +
				" code:" + code.getOpCode(pc));
		System.out.println("Stack:");
		for(int i = 0; i < stack.size(); i++) {
			System.out.print(String.valueOf(i) + ":");
			switch(stack.getType(i)) {
			case INT:
				System.out.print(String.valueOf(stack.getInt(i)));
				break;
			case DOUBLE:
				System.out.print(String.valueOf(stack.getDouble(i)));
				break;
			case STRING:
				System.out.print(stack.getString(i));
				break;
			case ADDRESS:
				System.out.print(stack.getAddress(i));
				break;
			default:
				break;
			}
			System.out.println("(" + stack.getType(i) + ")");
		}
		System.out.println(": End Stack");
	}

}
