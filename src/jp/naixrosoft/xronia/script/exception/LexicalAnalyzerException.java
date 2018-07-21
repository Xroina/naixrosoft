package jp.naixrosoft.xronia.script.exception;

import jp.naixrosoft.xronia.script.token.Token;

public class LexicalAnalyzerException extends ScriptException {

	private static final long serialVersionUID = 1L;

	public LexicalAnalyzerException(int line, int col, String msg,
			Token token, char ch) {
		super(String.valueOf(line) + "," + String.valueOf(col) +
			":LexicalAnalyzer error " + msg + " \'" + ch + "\' " +
			"token \'" + token.getString() + "\'");
	}

}
