package jp.naixrosoft.xronia.script.token;

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
		START,			// トークンの開始
		END,			// トークンの終了
	};
	public Kind kind;
	public long value;
	public double dbl;
	public String str;
	public long line;
	public int col;

	public Token next;
	public Token prev;

	public Token() {
		kind = Kind.NONE;
		value = 0;
		str = "";
		line = 0;
		next = null;
		prev = null;
	}
}
