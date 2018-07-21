package jp.naixrosoft.xronia.script.lex;

import jp.naixrosoft.xronia.script.exception.LexicalAnalyzerException;
import jp.naixrosoft.xronia.script.token.Token;
import jp.naixrosoft.xronia.script.token.Token.Kind;
import jp.naixrosoft.xronia.script.token.Tokens;

/**
 * レキシュアルアナライザクラス
 *
 * @author xronia
 *
 */
public class LexicalAnalyzer {

	private String src;		// ソースコード
	private Tokens tokens;	// トークン列

	private int line;		// 行数
	private int col;		// カラム数
	private int index;		// 文字位置

	/**
	 * 解析ステータス
	 *
	 * @author xronia
	 *
	 */
	enum State {
		NONE,		// なし
		VALUE,		// 数値解析中
		HEX,		// 16進数解析中
		IDENT,		// 文字式解析中
		STRING,		// 文字列解析中
		OPERATOR,	// 演算子解析中
		COMMENT		// コメント解析中
	};

	/**
	 * コンストラクタ
	 *
	 * @param s	ソースコード
	 * @param t	トークン列
	 */
	public LexicalAnalyzer(String s, Tokens t) {
		src = s;		// ソースコード
		tokens = t;		// トークン列
		line = 1;		// 行数
		col = 0;		// カラム数
		index = 0;
	}

	/**
	 * 解析本体
	 *
	 * @throws LexicalAnalyzerException
	 */
	public void analyze() throws LexicalAnalyzerException {
		State state = State.NONE;	// 初期ステータスはなし
		Token token = new Token();

		// ソースコード文字数+1ループ
		for(index = 0; index <= src.length(); index++) {
			// ソースコード文字取得
			char ch  = index     < src.length() ? src.charAt(index)     : 0;
			char ch2 = index + 1 < src.length() ? src.charAt(index + 1) : 0;
			col++;	// カラムのインクリメント

			switch(state) {
			case NONE:				// ステータスなし
				if(Character.isDigit(ch) || ch == '.') {	// 数字？
					token.setString(token.getString() + ch);
					state = State.VALUE;
				} else if(Character.isAlphabetic(ch) || ch == '_'
						|| ch == '$' || ch == '@') {		// 英文字？
					token.setString(token.getString() + ch);
					state = State.IDENT;
				} else if(ch == '\"') {						// 文字列？
					state = State.STRING;
				} else if(Operator.is(token, ch)) {			// 演算子？
					token.setString(token.getString() + ch);
					state = State.OPERATOR;
				} else if(isCr(ch, ch2)) {					// 改行？
					addCr(token);
					state = State.NONE;
					token = new Token();
				} else if(Character.isSpaceChar(ch) || ch == '\t' || ch == 0) {
					;										// 空白？
				} else if(ch == '#') {						// コメント？
					state = State.COMMENT;
				} else {									// エラー
					throw new LexicalAnalyzerException(
							line, col, "bad character", token, ch);
				}
				break;

			case VALUE:				// 数値の解析
				if(Character.isDigit(ch) || ch == '.') {
					// 数または小数点ならトークンに追加して次の文字解析
					token.setString(token.getString() + ch);
					break;
				} else if(token.getString().equals("0") && ch == 'x') {
					// 0xで始まる場合は16進数解析へ遷移
					token.setString("");
					state = State.HEX;
					break;
				} else if(ch == 'e' || ch == 'E') {
					// 指数表記
					if(Character.isDigit(ch2) || ch2 == '+' || ch2 == '-') {
						// 指数の場合は次の文字は数字か±
						token.setString(token.getString() + ch);
						token.setString(token.getString() + ch2);
						index++;
						col++;
						break;
					} else {
						// それ以外はエラー
						throw new LexicalAnalyzerException(
								line, col, "not number format", token, ch);
					}
				}
				try {		// Longにパース成功すればトークンは整数型
					token.setValue(Long.parseLong(token.getString()));
					token.setKind(Kind.INT_VALUE);
				} catch(NumberFormatException e1) {
					// Longにパース失敗
					try {	// Doubleにパース成功すればトークンは実数型
						token.setDouble(Double.parseDouble(token.getString()));
						token.setKind(Kind.DOUBLE_VALUE);
					} catch(NumberFormatException e2) {
						// LongにもDoubleにもパース失敗した場合はエラー
						throw new LexicalAnalyzerException(
								line, col, "not number format", token, ch);
					}
				}
				addTokenAndUnprChar(token);
				state = State.NONE;
				token = new Token();
				continue;

			case HEX:				// 16進数の解析
				if(Character.isDigit(ch) ||
						ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {
					token.setString(token.getString() + ch);
					break;
				}
				try {
					token.setValue(Long.parseLong(token.getString(), 16));
					token.setKind(Kind.INT_VALUE);
				} catch(NumberFormatException e1) {
					throw new LexicalAnalyzerException(
							line, col, "not number format", token, ch);
				}
				addTokenAndUnprChar(token);
				state = State.NONE;
				token = new Token();
				continue;

			case IDENT:				// 文字式の解析
				if(Character.isAlphabetic(ch)
						|| Character.isDigit(ch) || ch == '_') {
					token.setString(token.getString() + ch);
					break;
				}
				if(!Keyword.is(token)) token.setKind(Kind.IDENT);
				addTokenAndUnprChar(token);
				state = State.NONE;
				token = new Token();
				continue;

			case STRING:			// 文字列の解析
				if(ch != '\"') {
					if(isCr(ch, ch2)) {		// 改行？
						line++;
						col = 0;
						ch = '\n';
					}
					token.setString(token.getString() + escSqens(ch, ch2));
					break;
				}
				token.setKind(Kind.STRING_LITERAL);
				token.setLine(line);
				token.setColumn(col);
				tokens.add(token);
				state = State.NONE;
				token = new Token();
				continue;

			case OPERATOR:			// 演算子の解析
				if(Operator.is(token, ch)) {
					token.setString(token.getString() + ch);
					break;
				}
				token.setKind(Operator.select(token));
				addTokenAndUnprChar(token);
				state = State.NONE;
				token = new Token();
				continue;

			case COMMENT:			// コメントの解析
				if(!isCr(ch, ch2)) {	// 改行ではない？
					break;
				}
				addCr(token);
				state = State.NONE;
				token = new Token();
				continue;

			default:				// その他のステータスはエラー
				throw new LexicalAnalyzerException(
						line, col, "bad state", token, ch);
			}
		}

		return;
	}

	/**
	 * トークンを登録して、文字を一文字戻す
	 *
	 * @param token	登録するトークン
	 */
	private void addTokenAndUnprChar(Token token) {
		token.setLine(line);
		token.setColumn(col);
		tokens.add(token);
		index--;
		col--;
	}

	/**
	 * 改行かを判断<br>
	 * CRLFなら一文字進める
	 *
	 * @param c1	1文字目
	 * @param c2	2文字目
	 * @return		true:改行	false:改行でない
	 */
	private boolean isCr(char c1, char c2) {
		if(c1 == '\n' || c1 == '\r' || (c1 == '\r' && c2 == '\n')) {
			if(c1 == '\r' && c2 == '\n') index++;
			return true;
		}
		return false;
	}

	/**
	 * CRトークンを登録する
	 *
	 * @param token
	 */
	private void addCr(Token token) {
		token.setKind(Kind.CR);
		token.setLine(line);
		token.setColumn(col);
		tokens.add(token);
		line++;
		col = 0;
	}

	/**
	 * エスケープシーケンスの処理
	 *
	 * @param c1	1文字目
	 * @param c2	2文字目
	 * @return		解析後の文字コード
	 */
	private char escSqens(char c1, char c2) {
		if(c1 != '\\') return c1;

		switch(c2) {
		case 'n':
			c1 = '\n';
			break;
		case '"':
			c1 = '"';
			break;
		case 'r':
			c1 = '\u001c';
			break;
		case 'l':
			c1 = '\u001d';
			break;
		case 'u':
			c1 = '\u001e';
			break;
		case 'd':
			c1 = '\u001f';
			break;
		default:
			return c1;
		}
		index++;
		col++;
		return c1;
	}
}
