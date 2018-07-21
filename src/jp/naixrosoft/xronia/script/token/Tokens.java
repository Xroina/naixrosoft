package jp.naixrosoft.xronia.script.token;

/**
 * トークンリストクラス
 *
 * @author xronia
 *
 */
public class Tokens {
	private Token begin;		// 先頭のトークン
	private Token end;			// 終端のトークン

	/**
	 * コントラクタ
	 */
	public Tokens() {
		Token begin = new Token();
		Token end   = new Token();
		begin.setKind(Token.Kind.START);
		end.setKind(Token.Kind.END);
		begin.setNext(end);
		end.setPrev(begin);

		setBegin(begin);
		setEnd(end);
	}

	/**
	 * トークンを追加
	 *
	 * @param token		追加するトークン
	 */
	public void add(Token token) {
		token.setNext(getEnd());
		token.setPrev(getEnd().getPrev());

		synchronized(this) {
			this.getEnd().getPrev().setNext(token);
			this.getEnd().setPrev(token);
		}
	}

	/**
	 * 先頭トークン取得
	 *
	 * @return		先頭トークン
	 */
	public Token getBegin() {
		return begin;
	}

	/**
	 * 先頭トークン設定
	 *
	 * @param begin	先頭トークン
	 */
	public void setBegin(Token begin) {
		this.begin = begin;
	}

	/**
	 * 終端トークン取得
	 *
	 * @return		終端トークン
	 */
	public Token getEnd() {
		return end;
	}

	/**
	 * 終端トークン設定
	 *
	 * @param end	終端トークン
	 */
	public void setEnd(Token end) {
		this.end = end;
	}

	/**
	 * toStringメソッド
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		s.append(" Token:");
		for(Token i = getBegin(); i != null; i = i.getNext()) {
			s.append(i.toString()).append(" ");
		}
		s.append(":End");
		return s.toString();
	}

	/**
	 * デバックプリント
	 */
	public void debug() {
		System.out.println(this.toString());
	}
}
