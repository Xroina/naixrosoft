package jp.naixrosoft.xronia.script.perser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 関数クラス
 *
 * @author xronia
 *
 */
public class Function {
	private Map<String, Integer> function_label_map;
	private Map<Integer, Integer> label_address_map;

	/**
	 * コントラクタ
	 */
	public Function() {
		function_label_map= new HashMap<>();
		label_address_map = new HashMap<>();
	}

	/**
	 * 関数の存在有無
	 *
	 * @param function	関数名(ファイル名)
	 * @return			true:あり		false:なし
	 */
	public boolean hasFunction(String function) {
		return function_label_map.containsKey(function);
	}

	/**
	 * 関数名からラベルの取得
	 *
	 * @param function	関数名(ファイル名)
	 * @return			ラベル(Index)	常にマイナスの値を返す
	 */
	public int getLabel(String function) {
		return function_label_map.get(function);
	}

	/**
	 * 関数の新規作成
	 *
	 * @param function	関数名(ファイル名)
	 * @return			ラベル(Index) 常にマイナスの値を返す
	 */
	public int createNewFunction(String function) {
		int lbl = -function_label_map.size() - 1;
		function_label_map.put(function, lbl);
		label_address_map.put(lbl, -1);
		return lbl;
	}

	/**
	 * 登録済み関数の一覧を返す
	 *
	 * @return			Entryのセット
	 */
	public Set<Entry<String, Integer>> getFunctionSet() {
		return function_label_map.entrySet();
	}

	/**
	 * 関数ラベルがアドレスにマッピングされているかどうかを返す
	 *
	 * @return			true:アドレスあり	false:アドレスなし
	 */
	public boolean isAddressMapping() {
		for(int address: label_address_map.values()) {
			if(address < 0) return false;
		}
		return true;
	}

	/**
	 * 関数ラベルからアドレスを得る
	 *
	 * @param label		ラベル(index)
	 * @return			アドレス
	 */
	public int getAddress(int label) {
		return label_address_map.get(label);
	}

	/**
	 * 関数アドレスにアドレスを設定する
	 *
	 * @param label		ラベル(index)
	 * @param address	アドレス
	 */
	public void setAddress(int label, int address) {
		label_address_map.put(label, address);
	}
}
