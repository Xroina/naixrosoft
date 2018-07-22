package jp.naixrosoft.xronia.script.perser;

import java.io.File;
import java.io.IOException;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.bytecode.ByteCode.OpCode;
import jp.naixrosoft.xronia.script.exception.ParseException;
import jp.naixrosoft.xronia.script.token.Token;
import jp.naixrosoft.xronia.script.token.Token.Kind;
import jp.naixrosoft.xronia.script.token.Tokens;

/**
 * パーサークラス
 *
 * @author xronia
 */
public abstract class AbstFunctionPerser extends AbstBasePerser {
	protected ByteCode code;			// バイトコード
	private Function function;			// 関数
	private String base_path;			// スクリプト起動元パス
	private String src_path;			// スクリプトのパス

	/**
	 * コンストラクタ
	 *
	 * @param t		トークン列
	 * @param c		バイトコード
	 * @param f		関数
	 * @param bp	ベースパス
	 * @param sp	ソースパス
	 */
	protected AbstFunctionPerser(Tokens t, ByteCode c, Function f,
			String bp, String sp) {
		super(t);
		code = c;
		function = f;
		base_path = bp;
		src_path = sp;
	}

	/**
	 * 関数のパース
	 *
	 * @throws ParseException
	 */
	protected void parse_function_expr() throws ParseException {
		Token this_token = token_itr;
		// 関数トークンの切り出し
		Token token;
		do {
			token = getToken();
			if(token.getKind() != Kind.IDENT)
				throw new ParseException("bad statement", token);

			token = getToken();
		} while(token.getKind() == Kind.COLON);

		ungetToken(token);

		// 関数ファイルの生成
		StringBuffer sb = new StringBuffer();
		switch(this_token.getKind()) {
		case COLON:
			sb.append(src_path);
			break;
		case DOUBLE_COLON:
			sb.append(base_path);
			break;
		default:
			throw new ParseException("kind not found.", this_token);
		}
		sb.append(File.separator);

		for(Token t = this_token.getNext(); t != token; t = t.getNext()) {
			switch(t.getKind()) {
			case COLON:
				sb.append(File.separator);
				break;
			case IDENT:
				sb.append(t.getString());
				break;
			default:
				throw new ParseException("kind not found.", t);
			}
		}
		sb.append(".x");

		// 関数ファイルの存在確認
		String file = sb.toString();
		File f = new File(file);
		if(!f.exists()) throw new ParseException(
				"\"" + file + "\" File Not Found", this_token, token);

		// 完全なソースパスの生成
		try {
			file = f.getCanonicalPath();
		} catch (IOException e) {
			throw new ParseException(
					"\"" + file + "\" File Canonical Path Exception",
					this_token, token);
		}

		code.add(OpCode.PUSH_SUB_START);

		// 引数の取得
		check_expected_token(Kind.LEFT_PAREN);
		parse_expr();
		check_expected_token(Kind.RIGHT_PAREN);

		// ラベルの取得
		int lbl = 0;
		if(function.hasFunction(file)) {
			lbl = function.getLabel(file);
		} else {
			lbl = function.createNewFunction(file);
		}

		code.add(OpCode.GOSUB);
		code.add(lbl);
	}

	/**
	 * 式をパース
	 *
	 * @throws ParseException
	 */
	protected abstract void parse_expr() throws ParseException;

}
