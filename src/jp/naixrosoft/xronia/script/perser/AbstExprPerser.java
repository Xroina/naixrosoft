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
public abstract class AbstExprPerser extends AbstPrimaryPerser {
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
	protected AbstExprPerser(Tokens t, ByteCode c, Function f,
			Variable v, String bp, String sp) {
		super(t, c, f, v, bp, sp);
	}

	/**
	 * インクリメント/デクリメントコード生成本体
	 *
	 * @throws ParseException
	 */
	private void parse_incdec_expr(Token token, Kind kind, int idx)
			throws ParseException {

		if (idx > variable.size())
			throw new ParseException("identifier not found.", token);

		code.add(OpCode.PUSH_VAR);
		code.add(idx);
		code.add(OpCode.PUSH_INT);
		code.add(1);
		switch(kind) {
		case INC:
			code.add(OpCode.ADD);
			break;
		case DEC:
			code.add(OpCode.SUB);
			break;
		default:
			throw new ParseException("kind not found.", token);
		}
		code.add(OpCode.ASSIGN);
		code.add(idx);
	}

	/**
	 * 後ろ置きインクリメント/デクリメント
	 *
	 * @throws ParseException
	 */
	private void parse_incdec_expr(String ident, Token token)
			throws ParseException {
		int idx = variable.search(ident);

		parse_incdec_expr(token, token.getKind(), idx);
	}

	/**
	 * 前置きインクリメント/デクリメント
	 *
	 * @throws ParseException
	 */
	private void parse_incdec_expr(Kind kind) throws ParseException {

		Token token = getToken();
		if(!token.getKind().equals(Kind.IDENT))
			throw new ParseException("bad statement.", token);

		int idx = variable.search(token.getString());

		parse_incdec_expr(token, kind, idx);
	}

	/**
	 * マイナス式/論理否定をパース
	 *
	 * @throws ParseException
	 */
	private void parse_unary_expr() throws ParseException {
		Token token = getToken();
		switch(token.getKind()) {
		case SUB:
			parse_primary_expr();
			code.add(OpCode.MINUS);
			break;
		case NOT:
			parse_primary_expr();
			code.add(OpCode.NOT);
			break;
		case INC:
		case DEC:
			parse_incdec_expr(token.getKind());
			break;
		default:
			ungetToken(token);
			parse_primary_expr();
			break;
		}
	}

	/**
	 * 乗算除算をパース
	 *
	 * @throws ParseException
	 */
	private void parse_mult_expr() throws ParseException {
		parse_unary_expr();
		for(;;) {
			Token token = getToken();
			if(!token.getKind().equals(Kind.MUL)
			&& !token.getKind().equals(Kind.DIV)
			&& !token.getKind().equals(Kind.REMAIND)
			&& !token.getKind().equals(Kind.AND)) {
				ungetToken(token);
				break;
			}
			parse_unary_expr();
			switch(token.getKind()){
			case MUL:
				code.add(OpCode.MUL);
				break;
			case DIV:
				code.add(OpCode.DIV);
				break;
			case REMAIND:
				code.add(OpCode.REMAIND);
				break;
			case AND:
				code.add(OpCode.ADD);
				break;
			default:
				throw new ParseException("kind not found.", token);
			}
		}
	}

	/**
	 * 加算減算をパース
	 *
	 * @throws ParseException
	 */
	private void parse_add_expr() throws ParseException {
		parse_mult_expr();
		for(;;) {
			Token token = getToken();
			if(!token.getKind().equals(Kind.ADD)
			&& !token.getKind().equals(Kind.SUB)
			&& !token.getKind().equals(Kind.OR)) {
				ungetToken(token);
				break;
			}
			parse_mult_expr();
			switch(token.getKind()) {
			case ADD:
				code.add(OpCode.ADD);
				break;
			case SUB:
				code.add(OpCode.SUB);
				break;
			case OR:
				code.add(OpCode.OR);
				break;
			default:
				throw new ParseException("kind not found.", token);
			}
		}
	}

	/**
	 * シフト演算子をパース
	 *
	 * @throws ParseException
	 */
	private void parse_shift_expr() throws ParseException {
		parse_add_expr();
		for(;;) {
			Token token = getToken();
			if(token.getKind() != Kind.LEFT_SHIFT
			&& token.getKind() != Kind.RIGHT_SHIFT) {
				ungetToken(token);
				break;
			}
			parse_add_expr();
			switch(token.getKind()) {
			case LEFT_SHIFT:
				code.add(OpCode.LEFT_SHIFT);
				break;
			case RIGHT_SHIFT:
				code.add(OpCode.RIGHT_SHIFT);
				break;
			default:
				throw new ParseException("kind not found.", token);
			}
		}
	}

	/**
	 * 比較演算子をパース
	 *
	 * @throws ParseException
	 */
	private void parse_compare_expr() throws ParseException {
		parse_shift_expr();
		for(;;) {
			Token token = getToken();
			if(!token.getKind().equals(Kind.EQ)
			&& !token.getKind().equals(Kind.NE)
			&& !token.getKind().equals(Kind.GT)
			&& !token.getKind().equals(Kind.GE)
			&& !token.getKind().equals(Kind.LT)
			&& !token.getKind().equals(Kind.LE)) {
				ungetToken(token);
				break;
			}
			parse_shift_expr();
			switch(token.getKind()) {
			case EQ:
				code.add(OpCode.EQ);
				break;
			case NE:
				code.add(OpCode.NE);
				break;
			case GT:
				code.add(OpCode.GT);
				break;
			case GE:
				code.add(OpCode.GE);
				break;
			case LT:
				code.add(OpCode.LT);
				break;
			case LE:
				code.add(OpCode.LE);
				break;
			default:
				throw new ParseException("kind not found.", token);
			}
		}
	}

	/**
	 * 論理ANDをパース
	 *
	 * @throws ParseException
	 */
	private void parse_and_expr() throws ParseException {
		parse_compare_expr();
		for(;;) {
			Token token = getToken();
			if(!token.getKind().equals(Kind.AND2)) {
				ungetToken(token);
				break;
			}
			parse_compare_expr();
			code.add(OpCode.AND2);
		}
	}

	/**
	 * 論理ORをパース
	 *
	 * @throws ParseException
	 */
	private void parse_or_expr() throws ParseException {
		parse_and_expr();
		for(;;) {
			Token token = getToken();
			if(token.getKind() != Kind.OR2) {
				ungetToken(token);
				break;
			}
			parse_and_expr();
			code.add(OpCode.OR2);
		}
	}

	/**
	 * アサイン式をパース
	 *
	 * @throws ParseException
	 */
	private void parse_assign_expr() throws ParseException {
		parse_or_expr();
		for(;;) {
			Token token = getToken();
			if(!token.getKind().equals(Kind.ASSIGN) &&
			   !token.getKind().equals(Kind.INC) &&
			   !token.getKind().equals(Kind.DEC)) {
				ungetToken(token);
				break;
			}
			int idx;
			switch(token.getKind()) {
			case ASSIGN:
				parse_or_expr();

				token = token.getPrev();
				if(!token.getKind().equals(Kind.IDENT))
					throw new ParseException("bad statement", token);

				idx = variable.search_or_new(token.getString());

				code.add(OpCode.ASSIGN);
				code.add(idx);
				break;

			case INC:
			case DEC:
				if(!token.getPrev().getKind().equals(Kind.IDENT))
					throw new ParseException("bad statement", token);

				parse_incdec_expr(token.getPrev().getString(), token);
				break;

			default:
				throw new ParseException("kind not found.", token);
			}
		}
	}

	/**
	 * カンマをパース
	 *
	 * @throws ParseException
	 */
	private void parse_comma_expr() throws ParseException {
		parse_assign_expr();
		for(;;) {
			Token token = getToken();
			if(token.getKind() != Kind.COMMA) {
				ungetToken(token);
				break;
			}
			parse_expr();
		}
	}

	/**
	 * 式をパース
	 *
	 * @throws ParseException
	 */
	protected void parse_expr() throws ParseException {
		parse_comma_expr();
	}
}
