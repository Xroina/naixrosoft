package jp.naixrosoft.xronia.script.execute;

public interface ImplExecute {

	/**
	 * Print
	 *
	 * @param str 出力文字列
	 */
	abstract void doPrint(String str);

	/**
	 * クリアスクリーン
	 */
	abstract void doCls();

	/**
	 * 座標指定
	 *
	 * @param x	座標
	 * @param y 座標
	 */
	abstract void setLocate(int x, int y);

	/**
	 * スティックX座標取得
	 *
	 * @return	X座標
	 */
	abstract double getStickX();

	/**
	 * スティックY座標取得
	 *
	 * @return	Y座標
	 */
	abstract double getStickY();

	/**
	 * スティックボタン取得
	 *
	 * @return	ボタンビットマップ
	 */
	abstract long getButton();

	/**
	 * キャラクタ取得
	 *
	 * @param x	座標
	 * @param y 座標
	 * @return	キャラクタ文字
	 */
	abstract String getCharacter(int x, int y);

}
