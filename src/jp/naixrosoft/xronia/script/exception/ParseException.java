package jp.naixrosoft.xronia.script.exception;

import jp.naixrosoft.xronia.script.token.Token;

public class ParseException extends ScriptException {

	private static final long serialVersionUID = 1L;

	public ParseException(String msg) {
		super(createMainMessage(msg));
	}

	public ParseException(String msg, Token token) {
		super(createLineColums(token) + createMainMessage(msg) +
				" Token:" + token.getString() +
				" Kind:"+ String.valueOf(token.getKind()));
	}

	public ParseException(String msg, Token fast, Token last) {
		super(createMessage(msg, fast, last));
	}

	static private String createMessage(String msg, Token fast, Token last) {
		String str = createLineColums(fast) + createMainMessage(msg) +
				" Token:";
		for(Token t = fast; t != last; t = t.getNext()) {
			str += t.getString();
		}
		return str;
	}

	static private String createLineColums(Token token) {
		return String.valueOf(token.getLine()) + "," +
				String.valueOf(token.getColumn()) + " ";
	}

	static private String createMainMessage(String msg) {
		return "Parse error:" + msg;
	}
}
