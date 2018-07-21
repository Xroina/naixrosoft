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
		HEX,
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

		for(int i = 0; i <= src.length(); i++) {
			char ch  = i     < src.length() ? src.charAt(i)     : 0;
			char ch2 = i + 1 < src.length() ? src.charAt(i + 1) : 0;
			col++;

			switch(state) {
			case NONE:
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
					if(ch == '\r' && ch2 == '\n') i++;
					token.kind = Kind.CR;
					token.line = line;
					token.col  = col;
					tokens.add(token);
					line++;
					col = 0;
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

			case VALUE:
				if(Character.isDigit(ch) || ch == '.') {
					token.str += ch;
					break;
				} else if(token.str.equals("0") && ch == 'x') {
					token.str = "";
					state = State.HEX;
					break;
				} else if(ch == 'e' || ch == 'E') {
					if(Character.isDigit(ch2) || ch2 == '+' || ch2 == '-') {
						token.str += ch;
						token.str += ch2;
						i++;
						col++;
						break;
					} else {
						throw new LexicalAnalyzerException(
								line, col, "not number format", token, ch);
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

			case HEX:
				if(Character.isDigit(ch) ||
						ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {
					token.str += ch;
					break;
				}
				try {
					token.value = Long.parseLong(token.str, 16);
					token.kind = Kind.INT_VALUE;
				} catch(NumberFormatException e1) {
					throw new LexicalAnalyzerException(
							line, col, "not number format", token, ch);
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
					break;
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
						if(ch == '\r' && ch2 == '\n') i++;
						line++;
						col = 0;
						ch = '\n';
					}
					if(ch == '\\') {
						if(ch2 == 'n') {
							ch = '\n';
							i++;
							col++;
						} else if(ch2 == '"') {
							ch = '"';
							i++;
							col++;
						} else if(ch2 == 'r') {
							ch = '\u001c';
							i++;
							col++;
						} else if(ch2 == 'l') {
							ch = '\u001d';
							i++;
							col++;
						} else if(ch2 == 'u') {
							ch = '\u001e';
							i++;
							col++;
						} else if(ch2 == 'd') {
							ch = '\u001f';
							i++;
							col++;
						}
					}
					token.str += ch;
					break;
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
					break;
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
					break;
				}
				if(ch == '\r' && ch2 == '\n') i++;
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
