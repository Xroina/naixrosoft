package jp.naixrosoft.xronia.script.perser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.bytecode.ByteCode.OpCode;
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
public class Perser {
	private ByteCode code;			// バイトコード
	private Variable variable;		// 変数
	private Function function;		// 関数
	private Symbol label;			// ラベル
	private Token token_itr;		// 今のトークン
	private Token look_ahead;		// 前のトークン
	private String base_path;		// スクリプト起動元パス
	private String src_path;		// スクリプトのパス

	/**
	 * ループの開始と終了クラス
	 *
	 * @author xronia
	 */
	private class LoopLabel {
		public int start;
		public int end;
		public LoopLabel(int s, int e) {
			start = s;
			end = e;
		}
	};

	/**
	 * ループ開始終了ラベルのスタック
	 */
	private List<LoopLabel> loop_label_stack;

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
		token_itr = t.getBegin();
		code = c;
		variable = v;
		function = f;
		base_path = bp;
		src_path = sp;
		label = new Symbol(c);
		look_ahead = null;
		loop_label_stack = new ArrayList<LoopLabel>();
	}

	/**
	 * トークン取得
	 */
	private Token getToken() {
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
	private void ungetToken(Token tokan){
		look_ahead = tokan;
	}

	/**
	 * トークンの存在確認
	 *
	 * @param  expected	確認したいトークン
	 * @throws ParseException
	 */
	private void check_expected_token(Kind expected) throws ParseException {
		Token token = getToken();
		while(token.getKind() == Kind.CR) {
			token = getToken();
		}
		if(token.getKind() != expected) {
			throw new ParseException(
					"parse error:Expected " + String.valueOf(expected), token);
		}
	}

	/**
	 * 関数のパース
	 *
	 * @throws ParseException
	 */
	private void parse_function_expr() throws ParseException {
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
	 * 基本式をパース
	 *
	 * @throws ParseException
	 */
	private void parse_primary_expr() throws ParseException {
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
	private void parse_expr() throws ParseException {
		parse_comma_expr();
	}

	/**
	 * ifステートメント
	 *
	 * @throws ParseException
	 */
	private void parse_if_stmt() throws ParseException {

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
	 * forステートメント
	 *
	 * @throws ParseException
	 */
	private void parse_for_stmt() throws ParseException {

		int start_label = label.get();		// for開始ラベル
		int loop_label = label.get();		// ループの開始ラベル
		int for_label = label.get();		// for本体ラベル
		int end_loop_label = label.get();	// ループの終了ラベル

		Token token = getToken();			// カッコの処理
		boolean paren = false;
		if(token.getKind().equals(Kind.LEFT_PAREN)) {
			paren = true;
		} else {
			ungetToken(token);
		}

		parse_expr();						// forのあとはステートメントがあるはず

		// セミコロンを読み飛ばす
		token = getToken();
		if(!token.getKind().equals(Kind.SEMICOLON))
			ungetToken(token);

		label.set(start_label);				// for開始ラベル設定

		parse_expr();						// ステートメントのあとは式があるはず

		// セミコロンを読み飛ばす
		token = getToken();
		if(!token.getKind().equals(Kind.SEMICOLON))
			ungetToken(token);

		LoopLabel lpl = new LoopLabel(loop_label, end_loop_label);
		loop_label_stack.add(lpl);			// ループラベルをストックする

		code.add(OpCode.JUMP_IF_ZERO);
		code.add(end_loop_label);			// 式が成り立てばループ終了
		code.add(OpCode.JUMP);
		code.add(for_label);				// for本体まで飛ぶ

		label.set(loop_label);				// ループ開始ラベル設定

		parse_expr();						// ステートメント→式のあとはステートメント

		if(paren) {							// カッコ閉じる
			check_expected_token(Kind.RIGHT_PAREN);
		}

		code.add(OpCode.JUMP);
		code.add(start_label);				// スタートまで飛ぶ

		label.set(for_label);				// for本体ラベル

		parse_block();						// ステートメント→式→ステートメントのあとにはブロックがあるはず

		code.add(OpCode.JUMP);
		code.add(loop_label);				// ループ開始まで飛ぶ

		label.set(end_loop_label);			// ループ終了ラベル設定

		loop_label_stack.remove(loop_label_stack.size() - 1);
											// ストックしたラベルを開放
	}

	/**
	 * whileステートメント
	 *
	 * @throws ParseException
	 */
	private void parse_while_stmt() throws ParseException {

		int loop_label = label.get();		// ループの開始ラベル
		int end_loop_label = label.get();	// ループの終了ラベル

		LoopLabel lpl = new LoopLabel(loop_label, end_loop_label);
		loop_label_stack.add(lpl);	  		// ループラベルをストックする

		label.set(loop_label);				// ループ開始ラベル設定

		parse_expr();						// whileのあとは式があるはず

		code.add(OpCode.JUMP_IF_ZERO);
		code.add(end_loop_label);			// 式が成り立てばループ終了

		parse_block();						// 式のあとにはブロックがあるはず

		code.add(OpCode.JUMP);
		code.add(loop_label);				// ループ開始まで飛ぶ

		label.set(end_loop_label);			// ループ終了ラベル設定

		loop_label_stack.remove(loop_label_stack.size() - 1);
											// ストックしたラベルを開放
	}

	/**
	 * break/continueステートメント
	 *
	 * @throws ParseException
	 */
	private void parse_break_continue_stmt(Kind kind)
			throws ParseException {

		Token token = getToken();
		int level = 1;
		// break/continue の次は数か何もないはず
		if(token.getKind().equals(Kind.INT_VALUE)) {
			level = (int) token.getValue();
		} else {
			ungetToken(token);
		}

		// ループラベルのストックから指定数前のラベルを取得する
		int idx = loop_label_stack.size() - level;
		if(idx < 0 || idx >= loop_label_stack.size())
			throw new ParseException("Loop Stack OverFlow.", token);

		int lbl = 0;
		switch(kind) {					// continue/breakラベルの取得
		case CONTINUE:
			lbl = loop_label_stack.get(idx).start;
			break;
		case BREAK:
			lbl = loop_label_stack.get(idx).end;
			break;
		default:
			throw new ParseException("kind not found.", token);
		}

		code.add(OpCode.JUMP);			// そこに飛ぶ
		code.add(lbl);
	}

	/**
	 * goto/gosubステートメント
	 *
	 * @throws ParseException
	 */
	private void parse_goto_gosub_stmt(Kind kind) throws ParseException {
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
	private void parse_label_stmt() throws ParseException {
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
	private void parse_return_stmt() throws ParseException {
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

	/**
	 * ステートメントパーサー
	 *
	 * @throws ParseException
	 */
	private void parse_stmt() throws ParseException {
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

	/**
	 * ブロックパーサー
	 *
	 * @throws ParseException
	 */
	private void parse_block() throws ParseException {
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
