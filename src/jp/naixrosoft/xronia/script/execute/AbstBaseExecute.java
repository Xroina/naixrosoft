package jp.naixrosoft.xronia.script.execute;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;

/**
 * 実行基本クラス
 *
 * @author xronia
 *
 */
public abstract class AbstBaseExecute {
	protected final ByteCode code;			// バイトコード
	protected Stack stack = new Stack();	// スタック
	protected int sp = 0;					// スタックポインタ
	protected int pc = 0;					// プログラムポインタ

	protected boolean running = false;		// 実施中フラグ

	/**
	 * コントラクタ
	 *
	 * @param c	バイトコード
	 */
	protected AbstBaseExecute(ByteCode c) {
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
	 * toStringメソッド
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		s.append(" pc:").append(String.valueOf(pc));
		s.append(" sp:").append(String.valueOf(sp)).append(" code:");
		try {
			s.append(code.getOpCode(pc));
		} catch (ByteCodeException e) {
			s.append(e.toString());
		}
		s.append("\n");
		s.append(stack.toString());

		return s.toString();
	}

	/**
	 * デバックプリント
	 */
	public void debug() {
		System.out.println(this.toString());
	}

}
