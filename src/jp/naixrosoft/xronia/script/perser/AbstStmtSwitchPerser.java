package jp.naixrosoft.xronia.script.perser;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.bytecode.ByteCode.OpCode;
import jp.naixrosoft.xronia.script.exception.ParseException;
import jp.naixrosoft.xronia.script.token.Token;
import jp.naixrosoft.xronia.script.token.Tokens;

/**
 * パーサークラス
 *
 * @author xronia
 */
public abstract class AbstStmtSwitchPerser extends AbstStmtPerser {

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
	protected AbstStmtSwitchPerser(Tokens t, ByteCode c, Function f,
			Variable v, String bp, String sp) {
		super(t, c, f, v, bp, sp);
	}

	/**
	 * ステートメントパーサー
	 *
	 * @throws ParseException
	 */
	protected void parse_stmt() throws ParseException {
		Token token = getToken();

		switch(token.getKind()) {
		case IF:
			parse_if_stmt();
			break;
		case FOR:
			parse_for_stmt();
			break;
		case WHILE:
			parse_while_stmt();
			break;
		case PRINT:
			parse_expr();
			code.add(OpCode.PRINT);
			break;
		case DEBUG:
			parse_expr();
			code.add(OpCode.DEBUG);
			break;
		case GOTO:
		case GOSUB:
			parse_goto_gosub_stmt(token.getKind());
			break;
		case RETURN:
			parse_return_stmt();
			break;
		case BREAK:
		case CONTINUE:
			parse_break_continue_stmt(token.getKind());
			break;
		case MUL:
			parse_label_stmt();
			break;
		case CLS:
			code.add(OpCode.CLS);
			break;
		case LOCATE:
			parse_expr();
			code.add(OpCode.LOCATE);
			break;
		case SCROLL_NEXT:
			parse_expr();
			code.add(OpCode.SCROLL_NEXT);
			break;
		case SCROLL_PREV:
			parse_expr();
			code.add(OpCode.SCROLL_PREV);
			break;
		case SCROLL_LEFT:
			parse_expr();
			code.add(OpCode.SCROLL_LEFT);
			break;
		case SCROLL_RIGHT:
			parse_expr();
			code.add(OpCode.SCROLL_RIGHT);
			break;
		case COLOR:
			parse_expr();
			code.add(OpCode.COLOR);
			break;
		case FGCOLOR:
			parse_expr();
			code.add(OpCode.FGCOLOR);
			break;
		case BGCOLOR:
			parse_expr();
			code.add(OpCode.BGCOLOR);
			break;
		case START:
		case END:
		case SEMICOLON:
		case CR:
			break;
		default:
			ungetToken(token);
			parse_expr();
			break;
		}
	}
}
