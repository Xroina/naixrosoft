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
public abstract class AbstPrimaryPerser extends AbstFunctionPerser {
	protected Variable variable;			// 変数

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
	protected AbstPrimaryPerser(Tokens t, ByteCode c, Function f,
			Variable v, String bp, String sp) {
		super(t, c, f, bp, sp);
		variable = v;
	}

	/**
	 * 基本式をパース
	 *
	 * @throws ParseException
	 */
	protected void parse_primary_expr() throws ParseException {
		int idx = 0;
		Token token = getToken();
		while(token.getKind() == Kind.CR) {
			token = getToken();
		}

		switch(token.getKind()) {
		case INT_VALUE:
			code.add(OpCode.PUSH_INT);
			code.add(token.getValue());
			break;

		case DOUBLE_VALUE:
			code.add(OpCode.PUSH_DOUBLE);
			code.add(token.getDouble());
			break;

		case STRING_LITERAL:
			code.add(OpCode.PUSH_STRING);
			code.add(token.getString());
			break;

		case LEFT_PAREN:
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			break;

		case COLON:
		case DOUBLE_COLON:
			parse_function_expr();
			break;

		case IDENT:
			idx = variable.search(token.getString());
			if (idx >= variable.size()) {
				idx = variable.newVar(token.getString());
				code.add(OpCode.PUSH_INT);
				code.add(0);
				code.add(OpCode.ASSIGN);
				code.add(idx);
			}
			code.add(OpCode.PUSH_VAR);
			code.add(idx);
			break;

		case TIME:
			code.add(OpCode.TIME);
			break;

		case RANDOMIZE:
			parse_expr();
			code.add(OpCode.RANDOMIZE);
			break;

		case RND:
			code.add(OpCode.RND);
			break;

		case ABS:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.ABS);
			break;

		case PI:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.PI);
			break;

		case RAD:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.RAD);
			break;

		case DEG:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.DEG);
			break;

		case SIN:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.SIN);
			break;

		case COS:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.COS);
			break;

		case TAN:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.TAN);
			break;

		case ASIN:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.ASIN);
			break;

		case ACOS:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.ACOS);
			break;

		case ATAN:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.ATAN);
			break;

		case SQR:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.SQR);
			break;

		case LOG:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.LOG);
			break;

		case SIG:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.SIG);
			break;

		case INT:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.INT);
			break;

		case ASSIGN:
			ungetToken(token);
			break;

		case STICK_X:
			code.add(OpCode.STICK_X);
			break;

		case STICK_Y:
			code.add(OpCode.STICK_Y);
			break;

		case BUTTON:
			code.add(OpCode.BUTTON);
			break;

		case CHARACTER:
			check_expected_token(Kind.LEFT_PAREN);
			parse_expr();
			check_expected_token(Kind.RIGHT_PAREN);
			code.add(OpCode.CHARACTER);
			break;

		case START:
		case END:
		case SEMICOLON:
			break;
		default:
			throw new ParseException("kind not found.", token);
		}
		while(token.getNext().getKind() == Kind.CR) {
			token = getToken();
		}
	}

}
