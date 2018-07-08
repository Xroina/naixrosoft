package jp.naixrosoft.xronia.script.perser;

import java.util.ArrayList;
import java.util.List;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;

public class Symbol {

	private class Label {
		public String ident;
		public int addr;
		public Label() {
			ident = "";
			addr = 0;
		}
		public Label(String i) {
			ident = i;
			addr = 0;
		}
	};

	private List<Label> label;

	private ByteCode code;

	public Symbol(ByteCode c) {
		label = new ArrayList<Label>();
		code = c;
	}

	public int get() {
		Label l = new Label();
		label.add(l);
		return label.size() - 1;
	}

	public int get(String lbl) {
		Label l = new Label(lbl);
		label.add(l);
		return label.size() - 1;
	}

	public void set(int idx) {
		label.get(idx).addr = code.size();
	}

	public int search(String lbl) {
		for(int i = 0; i < label.size(); i++) {
			if(label.get(i).ident.length() !=0
					&& lbl.equals(label.get(i).ident))
				return i;
		}
		return -1;
	}

	public int search_or_new(String lbl) {
		int i = search(lbl);
		if(i < 0) i = get(lbl);
		return i;
	}

	public void fix() throws ByteCodeException {
		for(int i = 0; i < code.size(); i++) {
			switch(code.getOpCode(i)) {
			case PUSH_INT:
			case PUSH_DOUBLE:
			case PUSH_STRING:
			case PUSH_VAR:
			case ASSIGN:
				i++;
				break;

			case JUMP:
			case JUMP_IF_ZERO:
			case GOSUB:
				i++;
				if(code.getInt(i) >= 0) {
					code.setInt(i, label.get((int)code.getInt(i)).addr);
				}
				break;

			default:
				break;
			}
		}
	}

	public void debug() {
		System.out.print("Label:");
		for(Label i: label) {
			System.out.print(String.valueOf(i.addr));
			if(i.ident != null) {
				System.out.print(":" + i.ident);
			}
			System.out.print(" ");
		}
		System.out.println(":End Label");
	}

}
