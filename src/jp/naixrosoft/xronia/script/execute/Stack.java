package jp.naixrosoft.xronia.script.execute;

import java.util.ArrayList;
import java.util.List;

import jp.naixrosoft.xronia.script.exception.StackException;
import jp.naixrosoft.xronia.script.execute.Value.Type;

/**
 * スタッククラス
 *
 * @author xronia
 *
 */
public class Stack {

	private List<Value> value;		// 変数スタック
	private List<String> buf;		// 文字列バッファ

	/**
	 * コントラクタ
	 */
	public Stack() {
		value = new ArrayList<Value>();
		buf = new ArrayList<String>();
	}

	/**
	 * 指定した位置に値をセットする
	 *
	 * @param idx	インデックス
	 * @param val	値
	 * @throws StackException
	 */
	public void set(int idx, Value val) throws StackException {
		switch(val.getType()) {
		case INT:
			set(idx, val.getInteger());
			break;

		case DOUBLE:
			set(idx, val.getDouble());
			break;

		case STRING:
			set(idx, val.getString());
			break;

		case ADDRESS:
			setAddress(idx, val.getInteger());
		default:
			throw new StackException("Value Type not supported."
					+ this.toString());
		}
	}

	/**
	 * 指定した位置に値(整数)をセットする
	 *
	 * @param idx	インデックス
	 * @param val	値(整数)
	 * @throws StackException
	 */
	public void set(int idx, long val) throws StackException {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.setInteger(val);
		value.set(idx, v);
	}

	/**
	 * 指定した位置に値(boolean)をセットする
	 *
	 * @param idx	インデックス
	 * @param val	値(boolean)
	 * @throws StackException
	 */
	public void set(int idx, boolean val) throws StackException  {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.setInteger(val ? 1 : 0);
		value.set(idx, v);
	}

	/**
	 * 指定した位置に値(実数)をセットする
	 *
	 * @param idx	インデックス
	 * @param val	値(実数)
	 * @throws StackException
	 */
	public void set(int idx, double val) throws StackException {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.setDouble(val);
		value.set(idx, v);
	}

	/**
	 * 指定した位置に値(文字列)をセットする
	 *
	 * @param idx	インデックス
	 * @param val	値(文字列)
	 * @throws StackException
	 */
	public void set(int idx, String val) throws StackException {
		if(value.size() == idx) value.add(new Value());
		String str = new String(val);
		buf.add(str);

		Value v = get(idx);
		v.setString(str);
		value.set(idx, v);
	}

	/**
	 * 指定した位置にアドレスをセットする
	 *
	 * @param idx	インデックス
	 * @param val	アドレス
	 * @throws StackException
	 */
	public void setAddress(int idx, long val) throws StackException {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.setInteger(val);
		v.setType(Type.ADDRESS);
		value.set(idx, v);
	}

	/**
	 * 指定した位置にサブルーチンの開始をセットする
	 *
	 * @param idx	インデックス
	 * @throws StackException
	 */
	public void setSubStart(int idx) throws StackException {
		if(value.size() == idx) value.add(new Value());
		Value v = get(idx);
		v.setType(Type.SUB_START);
		value.set(idx, v);
	}

	/**
	 * 指定した位置から値を取得する
	 *
	 * @param idx	インデックス
	 * @return		値
	 * @throws StackException
	 */
	public Value get(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow." + this.toString());
		return value.get(idx);
	}

	/**
	 * 指定した位置から値(整数)を取得する
	 *
	 * @param idx	インデックス
	 * @return		値(整数)
	 * @throws StackException
	 */
	public long getInt(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow." + this.toString());
		if(!getType(idx).equals(Type.INT))
			throw new StackException("Value is not integer." + this.toString());
		return value.get(idx).getInteger();
	}

	/**
	 * 指定した位置から値(boolean)を取得する
	 *
	 * @param idx	インデックス
	 * @return		値(boolean)
	 * @throws StackException
	 */
	public boolean getBool(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow." + this.toString());
		if(getType(idx).equals(Type.INT)) {
			return value.get(idx).getInteger() != 0;
		} else if(getType(idx).equals(Type.STRING)) {
			return !value.get(idx).getString().equals("");
		}
		throw new StackException("Value is not integer." + this.toString());
	}

	/**
	 * 指定した位置から値(実数)を取得する
	 *
	 * @param idx	インデックス
	 * @return		値(実数)
	 * @throws StackException
	 */
	public double getDouble(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow." + this.toString());
		if(!getType(idx).equals(Type.DOUBLE))
			throw new StackException("Value is not integer." + this.toString());
		return value.get(idx).getDouble();
	}

	/**
	 * 指定した位置から値(文字列)を取得する
	 *
	 * @param idx	インデックス
	 * @return		値(文字列)
	 * @throws StackException
	 */
	public String getString(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow." + this.toString());
		if(!getType(idx).equals(Type.STRING))
			throw new StackException("Value is not string." + this.toString());
		return value.get(idx).getString();
	}

	/**
	 * 指定した位置からアドレスを取得する
	 *
	 * @param idx	インデックス
	 * @return		アドレス
	 * @throws StackException
	 */
	public long getAddress(int idx) throws StackException {
		if(idx >= value.size())
			throw new StackException("Stack OverFlow." + this.toString());
		if(idx < 0 || !getType(idx).equals(Type.ADDRESS)) {
			return -1;
		}
		return value.get(idx).getInteger();
	}

	/**
	 * 指定した位置から変数タイプを取得する
	 *
	 * @param idx	インデックス
	 * @return		スタックタイプ
	 * @throws StackException
	 */
	public Type getType(int idx) throws StackException {
		if(idx < 0 || idx >= value.size())
			throw new StackException("Stack OverFlow." + this.toString());
		return value.get(idx).getType();
	}

	/**
	 * スタックサイズを取得する
	 *
	 * @return		スタックサイズ
	 */
	public int size() {
		return value.size();
	}

	/**
	 * toStringメソッド
	 */
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer(super.toString());
		s.append(" Stack:\n");
		for(int i = 0; i < this.size(); i++) {
			s.append(String.valueOf(i)).append(":");
			try {
				s.append(this.get(i).toString()).append("\n");
			} catch (StackException e) {
				;	// 握りつぶす
			}
		}
		s.append(":End");

		return s.toString();
	}

}
