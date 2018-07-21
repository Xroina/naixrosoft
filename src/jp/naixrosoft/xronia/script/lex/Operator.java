package jp.naixrosoft.xronia.script.lex;

import java.util.HashMap;
import java.util.Map;

import jp.naixrosoft.xronia.script.token.Token;
import jp.naixrosoft.xronia.script.token.Token.Kind;

/**
 * 演算子クラス
 *
 * @author xronia
 *
 */
public class Operator {
	/**
	 * 識別する演算子
	 */
	private static Map<String, Kind> oper = new HashMap<String, Kind>();
	static {
		oper.put("==", Kind.EQ);
		oper.put("!=", Kind.NE);
		oper.put(">=", Kind.GE);
		oper.put("<=", Kind.LE);
		oper.put("+", Kind.ADD);
		oper.put("-", Kind.SUB);
		oper.put("|", Kind.OR);
		oper.put("++", Kind.INC);
		oper.put("--", Kind.DEC);
		oper.put("*", Kind.MUL);
		oper.put("/", Kind.DIV);
		oper.put("%", Kind.REMAIND);
		oper.put("!", Kind.NOT);
		oper.put(">>", Kind.LEFT_SHIFT);
		oper.put("<<", Kind.RIGHT_SHIFT);
		oper.put("&", Kind.AND);
		oper.put("||", Kind.OR2);
		oper.put("&&", Kind.AND2);
		oper.put("=", Kind.ASSIGN);
		oper.put(">", Kind.GT);
		oper.put("<", Kind.LT);
		oper.put("(", Kind.LEFT_PAREN);
		oper.put(")", Kind.RIGHT_PAREN);
		oper.put("{", Kind.LEFT_BRACE);
		oper.put("}", Kind.RIGHT_BRACE);
		oper.put(",", Kind.COMMA);
		oper.put(";", Kind.SEMICOLON);
		oper.put(":", Kind.COLON);
		oper.put("::", Kind.DOUBLE_COLON);
	}

	/**
	 * コンストラクタ<br>
	 * 実体化不可
	 */
	private Operator() {}

	/**
	 * トークンとそれに続く文字が演算子かを調査し演算子かどうかを返す
	 *
	 * @param token		調査するトークン
	 * @param letter	トークンに続く文字
	 * @return		true:演算子		false:演算子でない
	 */
	public static boolean is(Token token, char letter) {
		return oper.containsKey(token.str + letter);
	}

	/**
	 * トークンが演算子かを調査し演算子なら種別を返す
	 *
	 * @param token	調査するトークン
	 * @return		演算子の種別
	 */
	public static Kind select(Token token){
		Kind kind = oper.get(token.str);
		if(kind == null) return Kind.NONE;
		return kind;
	}

}
