package jp.naixrosoft.xronia.script.perser;

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
public abstract class AbstStmtPerser extends AbstLoopPerser {

	/**
	 * コンストラクタ
	 *
	 * @param t		トークン列
	 * @param c		バイトコード
	 * @param f		関数
	 * @param v		変数
	 * @param bp	ベースパス
	 * @param sp	ソースパス
	 */
	protected AbstStmtPerser(Tokens t, ByteCode c, Function f,
			Variable v, String bp, String sp) {
		super(t, c, f, v, bp, sp);
	}

	/**
	 * ifステートメント
	 *
	 * @throws ParseException
	 */
	protected void parse_if_stmt() throws ParseException {

		parse_expr();									// ifのあとには式があるはず

		int else_label = label.get();					// elseかifの終わりまでのラベル宣言
		code.add(OpCode.JUMP_IF_ZERO);
		code.add(else_label);

		parse_block();									// 式のあとにはブロックがあるはず

		Token token = getToken();
		if(token.getKind().equals(Kind.EF)) {			// efがある

			label.set(else_label);						// if文の終わり
			parse_if_stmt();

		} else if(token.getKind().equals(Kind.ELSE)) {	// elseがあるか？

			Token token2 = getToken();
			if(token2.getKind().equals(Kind.IF)) {		// elseのあとはifだったりする

				label.set(else_label);					// if文の終わり
				parse_if_stmt();

				return;
			}
			ungetToken(token2);

			int end_if_label = label.get();				// ifの終わりまでのラベルの宣言
			code.add(OpCode.JUMP);
			code.add(end_if_label);						// ifの終わりまで飛ぶ

			label.set(else_label);						// elseラベル設定

			parse_block();								// elseのあとにはブロックがあるはず

			label.set(end_if_label);					// ifの終わり設定
		} else {
			ungetToken(token);

			label.set(else_label);						// ifの終わり
		}
	}

	/**
	 * goto/gosubステートメント
	 *
	 * @throws ParseException
	 */
	protected void parse_goto_gosub_stmt(Kind kind) throws ParseException {
		check_expected_token(Kind.MUL);

		Token token = getToken();

		if(!token.getKind().equals(Kind.IDENT))
			throw new ParseException("label identifier expected.", token);

		int lbl = label.search_or_new(token.getString());

		switch(kind) {
		case GOTO:
			code.add(OpCode.JUMP);
			break;
		case GOSUB:
			code.add(OpCode.PUSH_SUB_START);
			code.add(OpCode.GOSUB);
			break;
		default:
			throw new ParseException("kind not found.", token);
		}
		code.add(lbl);
	}

	/**
	 * ラベルステートメント
	 *
	 * @throws ParseException
	 */
	protected void parse_label_stmt() throws ParseException {
		Token token = getToken();

		if(!token.getKind().equals(Kind.IDENT))
			throw new ParseException("label identifier expected.", token);

		int lbl = label.search_or_new(token.getString());
		label.set(lbl);
	}

	/**
	 * returnステートメント
	 *
	 * @throws ParseException
	 */
	protected void parse_return_stmt() throws ParseException {
		Token token = getToken();
		if(token.getKind() != Kind.CR && token.getKind() != Kind.SEMICOLON) {
			ungetToken(token);
			parse_expr();
		} else {
			code.add(OpCode.PUSH_INT);
			code.add(0);
		}
		code.add(OpCode.RETURN);
	}

}
