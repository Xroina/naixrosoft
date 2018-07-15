package jp.naixrosoft.xronia.script.execute;

import java.util.ArrayList;
import java.util.List;

import jp.naixrosoft.xronia.script.exception.StackException;
import jp.naixrosoft.xronia.script.execute.Value.Type;

public class Stack {

	private List<Value> value;
	private List<String> buf;


	public Stack() {
		value = new ArrayList<Value>();
		buf = new ArrayList<String>();
	}

	public void set(int idx, Value val) throws StackException {
		switch(val.type) {
		case INT:
			set(idx, val.int_var);
			break;

		case DOUBLE:
			set(idx, val.dbl_var);
			break;

		case STRING:
			set(idx, val.str_var);
			break;

		case ADDRESS:
			setAddress(idx, val.int_var);
		default:
			throw new StackException("Value Type not supported.");
		}
	}

	public void set(int idx, long val) throws StackException {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.type = Type.INT;
		v.int_var = val;
		value.set(idx, v);
	}

	public void set(int idx, boolean val) throws StackException  {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.type = Type.INT;
		v.int_var = val ? 1 : 0;
		value.set(idx, v);
	}

	public void setAddress(int idx, long val) throws StackException {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.type = Type.ADDRESS;
		v.int_var = val;
		value.set(idx, v);
	}

	public void set(int idx, double val) throws StackException {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.type = Type.DOUBLE;
		v.dbl_var = val;
		value.set(idx, v);
	}

	public void setSubStart(int idx) throws StackException {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.type = Type.SUB_START;
		value.set(idx, v);
	}

	public void set(int idx, String val) throws StackException {
		if(value.size() == idx) value.add(new Value());
		String str = new String(val);
		buf.add(str);

		Value v = get(idx);
		v.type = Type.STRING;
		v.str_var = str;
		value.set(idx, v);
	}

	public Value get(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow.");
		return value.get(idx);
	}

	public long getInt(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow.");
		if(!getType(idx).equals(Type.INT))
			throw new StackException("Value is not integer.");
		return value.get(idx).int_var;
	}

	public long getAddress(int idx) throws StackException {
		if(idx >= value.size())
			throw new StackException("Stack OverFlow.");
		if(idx < 0 || !getType(idx).equals(Type.ADDRESS)) {
			return -1;
		}
		return value.get(idx).int_var;
	}

	public boolean getBool(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow.");
		if(getType(idx).equals(Type.INT)) {
			return value.get(idx).int_var != 0;
		} else if(getType(idx).equals(Type.STRING)) {
			return !value.get(idx).str_var.equals("");
		}
		throw new StackException("Value is not integer.");
	}

	public double getDouble(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow.");
		if(!getType(idx).equals(Type.DOUBLE))
			throw new StackException("Value is not integer.");
		return value.get(idx).dbl_var;
	}

	public String getString(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow.");
		if(!getType(idx).equals(Type.STRING))
			throw new StackException("Value is not string.");
		return value.get(idx).str_var;
	}

	public Type getType(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow.");
		return value.get(idx).type;
	}

	public int size() {
		return value.size();
	}
}
