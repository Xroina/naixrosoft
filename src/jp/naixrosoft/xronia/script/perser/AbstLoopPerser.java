package jp.naixrosoft.xronia.script.perser;

import java.util.ArrayList;
import java.util.List;

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
public abstract class AbstLoopPerser extends AbstExprPerser {
	protected Symbol label;				// ラベル

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
	private List<LoopLabel> loop_label_stack = new ArrayList<LoopLabel>();

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
	protected AbstLoopPerser(Tokens t, ByteCode c, Function f,
			Variable v, String bp, String sp) {
		super(t, c, f, v, bp, sp);
		label = new Symbol(c);
	}

	/**
	 * forステートメント
	 *
	 * @throws ParseException
	 */
	protected void parse_for_stmt() throws ParseException {

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
	protected void parse_while_stmt() throws ParseException {

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
	protected void parse_break_continue_stmt(Kind kind) throws ParseException {

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
	 * ブロックパーサー
	 *
	 * @throws ParseException
	 */
	protected abstract void parse_block() throws ParseException;
}
