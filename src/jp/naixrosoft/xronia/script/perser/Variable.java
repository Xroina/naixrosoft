package jp.naixrosoft.xronia.script.perser;

import java.util.ArrayList;
import java.util.List;

/**
 * 変数クラス
 *
 * @author xronia
 *
 */
public class Variable {
	private List<String> variable;		// 変数のリスト

	/**
	 * コントラクタ
	 */
	public Variable() {
		variable = new ArrayList<String>();
	}

	/**
	 * 変数が引数だったらそのインデックスを返す
	 * インデックスは常にマイナスの値を返す
	 * 引数でなかった場合は0を返す
	 *
	 * @param ident			変数名
	 * @return				引数のインデックス
	 */
	private int argument(String ident) {
		if(ident.charAt(0) == '@') {
			try {
				return -Integer.parseInt(ident.substring(1)) - 1;
			} catch(NumberFormatException e) {
				;
			}
		}
		return 0;
	}

	/**
	 * 変数を検索してそのインデックスを返す
	 * 引数変数の場合はマイナス値を、通常変数の場合は0以上を返す
	 * 検索できなかった場合は変数リストのサイズを返す
	 *
	 * @param ident			変数名
	 * @return				変数のインデックス
	 */
	public int search(String ident) {
		int ret = argument(ident);
		if(ret < 0) return ret;
		for(; ret < variable.size(); ret++) {
			if (variable.get(ret).equals(ident)) break;
		}
		return ret;
	}

	/**
	 * 変数の新規割り当てを行い変数インデックスを返す
	 *
	 * @param ident			変数名
	 * @return				変数のインデックス
	 */
	public int newVar(String ident) {
		int ret = argument(ident);
		if(ret < 0) return ret;
		ret = variable.size();
		variable.add(ident);
		return ret;
	}

	/**
	 * 変数を検索し、無ければ新規割り当てを行う
	 * 戻り値として変数リストのインデックスを返す
	 *
	 * @param ident			変数名
	 * @return				変数のインデックス
	 */
	public int search_or_new(String ident) {
		int ret = search(ident);
		if(ret >= variable.size()) ret = newVar(ident);
		return ret;
	}

	/**
	 * 変数リストのサイズを返す
	 *
	 * @return				変数のサイズ
	 */
	public int size() {
		return variable.size();
	}

	/**
	 * デバックプリント
	 */
	public void debug() {
		System.out.print("Variable:");
		for(String i : variable) System.out.print(i + " ");
		System.out.println(":End Variable");
	}
}
