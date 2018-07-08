package jp.naixrosoft.xronia.script.lex;

import java.util.HashMap;
import java.util.Map;

import jp.naixrosoft.xronia.script.token.Token;
import jp.naixrosoft.xronia.script.token.Token.Kind;

public class Keyword {
	private static final Map<String, Kind> keywd = new HashMap<>();
	static {
		keywd.put("if", Kind.IF);
		keywd.put("else", Kind.ELSE);
		keywd.put("ef", Kind.EF);
		keywd.put("for", Kind.FOR);
		keywd.put("while", Kind.WHILE);
		keywd.put("goto", Kind.GOTO);
		keywd.put("gosub", Kind.GOSUB);
		keywd.put("return", Kind.RETURN);
		keywd.put("print", Kind.PRINT);
		keywd.put("continue", Kind.CONTINUE);
		keywd.put("break", Kind.BREAK);
		keywd.put("cls", Kind.CLS);
		keywd.put("locate", Kind.LOCATE);
		keywd.put("time", Kind.TIME);
		keywd.put("randomize", Kind.RANDOMIZE);
		keywd.put("rnd", Kind.RND);
		keywd.put("abs", Kind.ABS);
		keywd.put("pi", Kind.PI);
		keywd.put("rad", Kind.RAD);
		keywd.put("deg", Kind.DEG);
		keywd.put("sin", Kind.SIN);
		keywd.put("cos", Kind.COS);
		keywd.put("tan", Kind.TAN);
		keywd.put("asin", Kind.ASIN);
		keywd.put("acos", Kind.ACOS);
		keywd.put("atan", Kind.ATAN);
		keywd.put("sqr", Kind.SQR);
		keywd.put("log", Kind.LOG);
		keywd.put("sig", Kind.SIG);
		keywd.put("int", Kind.INT);
		keywd.put("stickX", Kind.STICK_X);
		keywd.put("stickY", Kind.STICK_Y);
		keywd.put("button", Kind.BUTTON);
		keywd.put("charcter", Kind.CHARACTER);
	}

	private Keyword() {}

	public static boolean is(Token token) {
		Kind kind = keywd.get(token.str);
		if(kind == null) return false;;
		token.kind = kind;
		return true;
	}

}
