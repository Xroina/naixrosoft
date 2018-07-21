package jp.naixrosoft.xronia;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;
import jp.naixrosoft.xronia.script.exception.ExecuteException;

/**
 * 外部実行本体クラス
 *
 * @author xronia
 *
 */
public class Execute extends jp.naixrosoft.xronia.script.execute.Execute {
	/**
	 * コントラクタ
	 *
	 * @param code	バイトコード
	 */
	public Execute(ByteCode code) {
		super(code);
	}

	/**
	 * 実行メソッド
	 *
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	public void execute() throws ExecuteException, ByteCodeException {
		super.execute();
	}

	/**
	 * print 本体
	 */
	@Override
	public void doPrint(String str) {
		System.out.print(str);
	}

	/**
	 * クリアスクリーン<br>
	 * コマンドプロンプトでは実行できないためスタブ
	 */
	@Override
	public void doCls() {
		;		// 何もしない
	}

	/**
	 * 座標指定<br>
	 * コマンドプロンプトでは指定できないためスタブ
	 *
	 * @param x	座標
	 * @param y 座標
	 */
	@Override
	public void setLocate(int x, int y) {
		;		// 何もしない
	}

	/**
	 * スティックX座標取得<br>
	 * コマンドプロンプトでは取得不能のためスタブ
	 *
	 * @return	X座標
	 *
	 */
	@Override
	public double getStickX() {
		return 0.0;
	}

	/**
	 * スティックY座標取得<br>
	 * コマンドプロンプトでは取得不能のためスタブ
	 *
	 * @return	Y座標
	 *
	 */
	@Override
	public double getStickY() {
		return 0.0;
	}

	/**
	 * スティックボタン取得<br>
	 * コマンドプロンプトでは取得不能のためスタブ
	 *
	 * @return	ボタンビットマップ
	 */
	@Override
	public long getButton() {
		return 0;
	}

	/**
	 * キャラクタ取得<br>
	 * コマンドプロンプトでは取得不能のためスタブ
	 *
	 * @param x	座標
	 * @param y 座標
	 * @return	キャラクタ文字
	 */
	@Override
	public String getCharacter(int x, int y) {
		return "";
	}

	/**
	 * 上スクロール<br>
	 * コマンドプロンプトでは実施不能のためスタブ
	 *
	 * @param y1	スクロール開始位置
	 * @param y2	スクロール終了位置
	 */
	@Override
	public void scrollNext(int y1, int y2) {
		;		// 何もしない
	}

	/**
	 * 下スクロール<br>
	 * コマンドプロンプトでは実施不能のためスタブ
	 *
	 * @param y1	スクロール開始位置
	 * @param y2	スクロール終了位置
	 */
	@Override
	public void scrollPrev(int y1, int y2) {
		;		// 何もしない
	}

	/**
	 * 左スクロール<br>
	 * コマンドプロンプトでは実施不能のためスタブ
	 *
	 * @param x1	スクロール開始位置
	 * @param x2	スクロール終了位置
	 */
	@Override
	public void scrollLeft(int x1, int x2) {
		;		// 何もしない
	}

	/**
	 * 右スクロール<br>
	 * コマンドプロンプトでは実施不能のためスタブ
	 *
	 * @param x1	スクロール開始位置
	 * @param x2	スクロール終了位置
	 */
	@Override
	public void scrollRight(int x1, int x2) {
		;		// 何もしない
	}
}
