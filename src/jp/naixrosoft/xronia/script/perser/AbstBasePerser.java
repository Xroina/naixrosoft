package jp.naixrosoft.xronia.script.perser;

import jp.naixrosoft.xronia.script.exception.ParseException;
import jp.naixrosoft.xronia.script.token.Token;
import jp.naixrosoft.xronia.script.token.Token.Kind;
import jp.naixrosoft.xronia.script.token.Tokens;

/**
 * パーサークラス
 *
 * @author xronia
 */
public abstract class AbstBasePerser {
	protected Token token_itr;			// 今のトークン
	protected Token look_ahead = null;	// 前のトークン

	/**
	 * コンストラクタ
	 *
	 * @param t		トークン列
	 */
	protected AbstBasePerser(Tokens t) {
		token_itr = t.getBegin();
	}

	/**
	 * トークン取得
	 */
	protected Token getToken() {
		if(look_ahead != null) {
			token_itr = look_ahead;
			look_ahead = null;
		} else {
			token_itr = token_itr.getNext();
		}
		return token_itr;
	}

	/**
	 * トークンを押し戻す
	 *
	 * @param tokan	押し戻すトークン
	 */
	protected void ungetToken(Token tokan){
		look_ahead = tokan;
	}

	/**
	 * トークンの存在確認
	 *
	 * @param  expected	確認したいトークン
	 * @throws ParseException
	 */
	protected void check_expected_token(Kind expected) throws ParseException {
		Token token = getToken();
		while(token.getKind() == Kind.CR) {
			token = getToken();
		}
		if(token.getKind() != expected) {
			throw new ParseException(
					"parse error:Expected " + String.valueOf(expected), token);
		}
	}
}
