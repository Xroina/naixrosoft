package jp.naixrosoft.xronia.script.token;

public class Tokens {
	public Token begin;
	public Token end;

	public Tokens() {
		begin = new Token();
		end = new Token();
		begin.kind = Token.Kind.START;
		end.kind = Token.Kind.END;
		begin.next = end;
		end.prev = begin;
	}

	public void add(Token token) {
		token.next = end;
		token.prev = end.prev;

		synchronized(this) {
			this.end.prev.next = token;
			this.end.prev = token;
		}
	}

	public void debug() {
		System.out.println("Token:");
		for(Token i = begin; i != null; i = i.next) {
			System.out.println(String.valueOf(i.line) + "," + String.valueOf(i.col) +
					":" + String.valueOf(i.kind) + ":" + i.str + " ");
		}
		System.out.println(":End Token");
	}
}
