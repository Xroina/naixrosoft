package jp.naixrosoft.xronia.script.perser;

import java.util.ArrayList;
import java.util.List;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;

/**
 * シンボルクラス
 *
 * @author xronia
 *
 */
public class Symbol {

	/**
	 * ラベルクラス
	 *
	 * @author xronia
	 *
	 */
	private class Label {
		private String ident;
		private int addr;

		/**
		 * コントラクタ
		 */
		public Label() {
			ident = "";
			addr = 0;
		}

		/**
		 * コントラクタ(ラベルシンボル指定)
		 *
		 * @param i		ラベルシンボル
		 */
		public Label(String i) {
			ident = i;
			addr = 0;
		}
	};

	private List<Label> label;		// ラベルリスト
	private ByteCode code;			// バイトコード

	/**
	 * コントラクタ
	 *
	 * @param c		バイトコード
	 */
	public Symbol(ByteCode c) {
		label = new ArrayList<Label>();
		code = c;
	}

	/**
	 * 無名のラベル取得
	 *
	 * @return		ラベルインデックス
	 */
	public int get() {
		Label l = new Label();
		label.add(l);
		return label.size() - 1;
	}

	/**
	 * ラベル取得
	 *
	 * @param lbl	ラベルシンボル
	 * @return		ラベルインデックス
	 */
	public int get(String lbl) {
		Label l = new Label(lbl);
		label.add(l);
		return label.size() - 1;
	}

	/**
	 * ラベル設定
	 *
	 * @param idx	ラベルインデックス
	 */
	public void set(int idx) {
		label.get(idx).addr = code.size();
	}

	/**
	 * ラベル検索<br>
	 * 検索にヒットしなかった場合は-1を返す
	 *
	 * @param lbl	ラベルシンボル
	 * @return		ラベルインデックス
	 */
	public int search(String lbl) {
		for(int i = 0; i < label.size(); i++) {
			if(label.get(i).ident.length() !=0
					&& lbl.equals(label.get(i).ident))
				return i;
		}
		return -1;
	}

	/**
	 * ラベル検索or新しいラベル取得<br>
	 * ラベルを検索して存在すればそのインデックスを返す<br>
	 * 存在しなければ新しいラベルを取得してそのインデックスを返す
	 *
	 * @param lbl	ラベルシンボル
	 * @return		ラベルインデックス
	 */
	public int search_or_new(String lbl) {
		int i = search(lbl);
		if(i < 0) i = get(lbl);
		return i;
	}

	/**
	 * バイトコード内のラベルインデックスをバイトコード内のアドレスに変換する
	 *
	 * @throws ByteCodeException
	 */
	public void fix() throws ByteCodeException {
		for(int i = 0; i < code.size(); i++) {
			switch(code.getOpCode(i)) {
			case PUSH_INT:
			case PUSH_DOUBLE:
			case PUSH_STRING:
			case PUSH_VAR:
			case ASSIGN:
				i++;
				break;

			case JUMP:
			case JUMP_IF_ZERO:
			case GOSUB:
				i++;
				if(code.getInt(i) >= 0) {
					code.setInt(i, label.get((int)code.getInt(i)).addr);
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * toStringメソッド
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		s.append(" Label:");
		for(Label i: label) {
			s.append(String.valueOf(i.addr));
			if(i.ident != null) s.append(":").append(i.ident);
			s.append(" ");
		}
		s.append(":End");

		return s.toString();
	}

	/**
	 * デバックプリント
	 */
	public void debug() {
		System.out.print(this.toString());
	}

}
