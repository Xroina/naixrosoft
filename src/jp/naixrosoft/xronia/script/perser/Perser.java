package jp.naixrosoft.xronia.script.perser;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;
import jp.naixrosoft.xronia.script.exception.ParseException;
import jp.naixrosoft.xronia.script.token.Token;
import jp.naixrosoft.xronia.script.token.Token.Kind;
import jp.naixrosoft.xronia.script.token.Tokens;

/**
 * パーサークラス
 *
 * @author xronia
 */
public class Perser extends AbstStmtSwitchPerser {

	/**
	 * コンストラクタ
	 *
	 * @param t		トークン列
	 * @param c		バイトコード
	 * @param v		変数
	 * @param f		関数
	 * @param bp	ベースパス
	 * @param sp	ソースパス
	 */
	public Perser(Tokens t, ByteCode c, Function f,
			Variable v, String bp, String sp) {
		super(t, c, f, v, bp, sp);
	}

	/**
	 * ブロックパーサー
	 *
	 * @throws ParseException
	 */
	protected void parse_block() throws ParseException {
		check_expected_token(Kind.LEFT_BRACE);

		for(;;) {
			Token token = getToken();

			if(token.getKind().equals(Kind.RIGHT_BRACE)) break;

			ungetToken(token);

			parse_stmt();
		}
	}

	/**
	 * パーサーの開始
	 *
	 * @throws ParseException
	 * @throws ByteCodeException
	 */
	public void parse() throws ParseException, ByteCodeException {
		for (;;) {
			Token token = getToken();

			if(token.getKind().equals(Kind.END)) break;

			ungetToken(token);

			parse_stmt();
		}
		label.fix();
//		label.debug();
	}
}
