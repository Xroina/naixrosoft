package jp.naixrosoft.xronia;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;
import jp.naixrosoft.xronia.script.exception.ExecuteException;

public class Execute extends jp.naixrosoft.xronia.script.execute.Execute {
	public Execute(ByteCode code) {
		super(code);
	}

	public void execute() throws ExecuteException, ByteCodeException {
		super.execute();
	}

	/**
	 * print 本体
	 */
	@Override
	protected void print(String str) {
		System.out.print(str);
	}

	/**
	 * クリアスクリーン
	 */
	@Override
	protected void cls() {
		;
	}

	/**
	 * Locate
	 *
	 * @param x	座標
	 * @param y 座標
	 */
	@Override
	protected void locate(int x, int y) {
		;
	}

	@Override
	protected double stickX() {
		return 0.0;
	}

	@Override
	protected double stickY() {
		return 0.0;
	}

	@Override
	protected long button() {
		return 0;
	}

	@Override
	protected String getCharacter(int x, int y) {
		return "";
	}
}
