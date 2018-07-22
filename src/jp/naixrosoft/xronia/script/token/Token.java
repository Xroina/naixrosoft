package jp.naixrosoft.xronia.script.token;

/**
 * トークンクラス
 *
 * @author xronia
 *
 */
public class Token {
	public enum Kind {
		NONE,			//
		INT_VALUE,		//
		DOUBLE_VALUE,	//
		IDENT,			//
		STRING_LITERAL, //
		EQ,				// ==
		NE,				// !=
		GE,				// >=
		LE,				// <=
		ADD,			// +
		SUB,			// -
		INC,			// ++
		DEC,			// --
		OR,				// |
		MUL,			// *
		DIV,			// /
		REMAIND,		// %
		AND,			// &
		NOT,			// !
		LEFT_SHIFT,		// >>
		RIGHT_SHIFT,	// <<
		OR2,			// ||
		AND2,			// &&
		ASSIGN,			// =
		GT,				// >
		LT,				// <
		LEFT_PAREN,		// (
		RIGHT_PAREN,	// )
		LEFT_BRACE,		// {
		RIGHT_BRACE,	// }
		COMMA,			// ,
		SEMICOLON,		// ;
		CR,				// 改行
		COLON,			// :
		DOUBLE_COLON,	// ::
		IF,				// if
		ELSE,			// else
		EF,				// ef
		FOR,			// for
		WHILE,			// while
		GOTO,			// goto
		GOSUB,			// gosub
		RETURN,			// return
		PRINT,			// print
		DEBUG,			// debug print
		CONTINUE,		// continue
		BREAK,			// break
		CLS,			// cls
		LOCATE,			// locate
		TIME,			// time
		RANDOMIZE,		// randomize
		RND,			// rnd
		ABS,			// abs
		PI,				// pi
		DEG,			// deg ラジアンから度に変換
		RAD,			// rad
		SIN,			// sin
		COS,			// cos
		TAN,			// tan
		ASIN,			// asin
		ACOS,			// acos
		ATAN,			// atan
		SQR,			// sqr
		LOG,			// log
		SIG,			// signum
		INT,			// int
		STICK_X,		// stickX
		STICK_Y,		// stickY
		BUTTON,			// button
		CHARACTER,		// character
		SCROLL_NEXT,	// 一行上へスクロール
		SCROLL_PREV,	// 一行下へスクロール
		SCROLL_LEFT,	// １列左へスクロール
		SCROLL_RIGHT,	// １列右へスクロール
		COLOR,			// カラー
		FGCOLOR,		// フォアグラウンドカラー
		BGCOLOR,		// バックグラウンドカラー
		START,			// トークンの開始
		END,			// トークンの終了
	};

	private Kind kind;		// 種別
	private long value;		// 値(整数)
	private double dbl;		// 値(実数)
	private String str;		// 値(文字列)
	private long line;		// 行数
	private int col;		// カラム

	private Token next;		// 次のトークン
	private Token prev;		// 前のトークン

	public Token() {
		setKind(Kind.NONE);
		setValue(0);
		setDouble(0.0);
		setString("");
		setLine(0);
		setNext(null);
		setPrev(null);
	}

	/**
	 * 種別取得
	 *
	 * @return		種別
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * 種別設定
	 *
	 * @param kind	種別
	 */
	public void setKind(Kind kind) {
		this.kind = kind;
	}

	/**
	 * 値(整数)取得
	 *
	 * @return		値(整数)
	 */
	public long getValue() {
		return value;
	}

	/**
	 * 値(整数)設定
	 *
	 * @param value	値(整数)
	 */
	public void setValue(long value) {
		this.value = value;
	}

	/**
	 * 値(実数)取得
	 *
	 * @return		値(実数)
	 */
	public double getDouble() {
		return dbl;
	}

	/**
	 * 値(実数)設定
	 *
	 * @param value	値(実数)
	 */
	public void setDouble(double value) {
		this.dbl = value;
	}

	/**
	 * 値(文字列)取得
	 *
	 * @return		値(文字列)
	 */
	public String getString() {
		return str;
	}

	/**
	 * 値(文字列)設定
	 *
	 * @param value	値(文字列)
	 */
	public void setString(String value) {
		this.str = value;
	}

	/**
	 * 行数取得
	 *
	 * @return		行数
	 */
	public long getLine() {
		return line;
	}

	/**
	 * 行数設定
	 *
	 * @param line	行数
	 */
	public void setLine(long line) {
		this.line = line;
	}

	/**
	 * カラム取得
	 *
	 * @return		カラム
	 */
	public int getColumn() {
		return col;
	}

	/**
	 * カラム設定
	 *
	 * @param col	カラム
	 */
	public void setColumn(int col) {
		this.col = col;
	}

	/**
	 * 次のトークン取得
	 *
	 * @return		次のトークン
	 */
	public Token getNext() {
		return next;
	}

	/**
	 * 次のトークン設定
	 *
	 * @param next	次のトークン
	 */
	public void setNext(Token next) {
		this.next = next;
	}

	/**
	 * 前のトークン取得
	 *
	 * @return		前のトークン
	 */
	public Token getPrev() {
		return prev;
	}

	/**
	 * 前のトークン設定
	 *
	 * @param prev	前のトークン
	 */
	public void setPrev(Token prev) {
		this.prev = prev;
	}

	/**
	 * toStringメソッド
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		s.append(" ").append(String.valueOf(this.getLine()));
		s.append(",").append(String.valueOf(this.getColumn()));
		s.append(":").append(String.valueOf(this.getKind()));
		s.append(":").append(this.getString());
		return s.toString();
	}
}
