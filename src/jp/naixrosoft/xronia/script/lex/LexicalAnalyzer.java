package jp.naixrosoft.xronia.script.lex;

import jp.naixrosoft.xronia.script.exception.LexicalAnalyzerException;
import jp.naixrosoft.xronia.script.token.Token;
import jp.naixrosoft.xronia.script.token.Token.Kind;
import jp.naixrosoft.xronia.script.token.Tokens;

public class LexicalAnalyzer {

	private String src;
	private Tokens tokens;

	private int line;
	private int col;

	enum State {
		NONE,
		VALUE,
		IDENT,
		STRING,
		OPERATOR,
		COMMENT
	};

	public LexicalAnalyzer(String s, Tokens t) {
		src = s;
		tokens = t;
		line = 1;
		col = 0;
	}

	public void analyze() throws LexicalAnalyzerException {
		State state = State.NONE;
		Token token = new Token();

		for(int i = 0; i < src.length(); i++) {
			char ch = src.charAt(i);
			char ch2 = i + 1 < src.length() ? src.charAt(i + 1) : 0;
			char ch3 = i + 2 < src.length() ? src.charAt(i + 2) : 0;
			col++;

			switch(state) {
			case NONE:
				if((ch == '-' || ch == '+') &&
						(Character.isDigit(ch2) || ch2 == '.')) {
					token.str += ch;
					token.str += ch2;
					state = State.VALUE;
					i++;
					col++;
					break;
				}
				if(Character.isDigit(ch) || ch == '.') {	// 数字？
					token.str += ch;
					state = State.VALUE;
				} else if(Character.isAlphabetic(ch) || ch == '_'
						|| ch == '$' || ch == '@') {		// 英文字？
					token.str += ch;
					state = State.IDENT;
				} else if(ch == '\"') {						// 文字列？
					state = State.STRING;
				} else if(Operator.is(token, ch)) {			// 演算子？
					token.str += ch;
					state = State.OPERATOR;
				} else if(ch == '\n' || ch == '\r'
						|| (ch == '\r' && ch2 == '\n')) {	// 改行？
					if(ch2 == '\n') i++;
					token.kind = Kind.CR;
					token.line = line;
					token.col  = col;
					tokens.add(token);
					line++;
					col = 0;
					state = State.NONE;
					token = new Token();
				} else if(Character.isSpaceChar(ch) || ch == '\t') {	// 空白？
					;
				} else if(ch == '#') {					// コメント？
					state = State.COMMENT;
				} else {								// エラー
					throw new LexicalAnalyzerException(
							line, col, "bad character", token, ch);
				}
				break;

			case VALUE:
				if(Character.isDigit(ch) || ch == '.') {
					token.str += ch;
					if(i != src.length() -1) break;
				} else if(ch == 'e' || ch == 'E') {
					if(Character.isDigit(ch2)) {
						token.str += ch;
						token.str += ch2;
						i++;
						col++;
						if(i != src.length() -1) break;
					} else if((ch2 == '+' || ch2 == '-')
							&& Character.isDigit(ch3)) {
						token.str += ch;
						token.str += ch2;
						token.str += ch3;
						i += 2;
						col += 2;
						if(i != src.length() -1) break;
					}
				}
				try {
					token.value = Long.parseLong(token.str);
					token.kind = Kind.INT_VALUE;
				} catch(NumberFormatException e1) {
					try {
						token.dbl = Double.parseDouble(token.str);
						token.kind = Kind.DOUBLE_VALUE;
					} catch(NumberFormatException e2) {
						throw new LexicalAnalyzerException(
								line, col, "not number format", token, ch);
					}
				}
				token.line = line;
				token.col  = col;
				tokens.add(token);
				i--;
				col--;
				state = State.NONE;
				token = new Token();
				continue;

			case IDENT:
				if(Character.isAlphabetic(ch)
						|| Character.isDigit(ch) || ch == '_') {
					token.str += ch;
					if(i != src.length() -1) break;
				}
				if(!Keyword.is(token)) token.kind = Kind.IDENT;
				token.line = line;
				token.col  = col;
				tokens.add(token);
				i--;
				col--;
				state = State.NONE;
				token = new Token();
				continue;

			case STRING:
				if(ch != '\"') {
					if(ch == '\n' || ch == '\r'
						|| (ch == '\r' && ch2 == '\n')) {	// 改行？
						if(ch2 == '\n') i++;
						line++;
						col = 0;
					}
					if(ch == '\\' && i + 1 < src.length()) {
						if(ch2 == 'n') {
							ch = '\n';
							i++;
							col++;
						} else if(ch2 == '"') {
							ch = '"';
							i++;
							col++;
						}
					}
					token.str += ch;
					if(i != src.length() -1) break;
				}
				token.kind = Kind.STRING_LITERAL;
				token.line = line;
				token.col  = col;
				tokens.add(token);
				state = State.NONE;
				token = new Token();
				continue;

			case OPERATOR:
				if(Operator.is(token, ch)) {
					token.str += ch;
					if(i != src.length() -1) break;
				}
				token.kind = Operator.select(token);
				token.line = line;
				token.col  = col;
				tokens.add(token);
				i--;
				col--;
				state = State.NONE;
				token = new Token();
				continue;

			case COMMENT:
				if(ch != '\n' && ch != '\r'
					&& (ch != '\r' || ch2 != '\n')) {	// 改行？
					if(i != src.length() -1) break;
				}
				if(ch2 == '\n') i++;
				token.kind = Kind.CR;
				token.line = line;
				token.col  = col;
				tokens.add(token);
				line++;
				col = 0;
				state = State.NONE;
				token = new Token();
				continue;
			default:
				throw new LexicalAnalyzerException(
						line, col, "bad state", token, ch);
			}
		}

		return;
	}
}
