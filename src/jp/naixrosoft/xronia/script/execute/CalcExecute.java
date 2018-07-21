package jp.naixrosoft.xronia.script.execute;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;
import jp.naixrosoft.xronia.script.exception.ExecuteException;
import jp.naixrosoft.xronia.script.exception.StackException;
import jp.naixrosoft.xronia.script.execute.Value.Type;

/**
 * 実行計算クラス
 *
 * @author xronia
 *
 */
public abstract class CalcExecute extends BaseExecute {

	/**
	 * コントラクタ
	 *
	 * @param c	バイトコード
	 */
	protected CalcExecute(ByteCode c) {
		super(c);
	}

	/**
	 * 加算処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void add_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.STRING) && type1.equals(Type.STRING)) {
			stack.set(sp - 2, stack.getString(sp - 2) +
							  stack.getString(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) +
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) +
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getInt(sp - 2) +
						 (int)stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) +
					  (double)stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.STRING) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getString(sp - 2) +
							  String.valueOf(stack.getInt(sp - 1)));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.STRING)) {
			long var = 0;
			String s =stack.getString(sp - 1);
			try {
				var = Long.parseLong(s);
			} catch(NumberFormatException e) {
				;
			}
			stack.set(sp - 2, stack.getInt(sp - 2) + var);
		} else
		if(type2.equals(Type.STRING) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getString(sp - 2) +
							  String.valueOf(stack.getDouble(sp - 1)));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.STRING)) {
			double var = 0;
			String s =stack.getString(sp - 1);
			try {
				var = Double.parseDouble(s);
			} catch(NumberFormatException e) {
				;
			}
			stack.set(sp - 2, stack.getDouble(sp - 2) + var);
		} else {
			throw new ExecuteException("not support add operator.");
		}
	}

	/**
	 * 減算処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void sub_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) -
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) -
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) -
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) -
					  (double)stack.getInt(sp - 1));
		} else {
			throw new ExecuteException("not support sub operator.");
		}
	}

	/**
	 * 乗算処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void mul_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) *
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) *
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) *
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) *
					  (double)stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.STRING) && type1.equals(Type.INT)) {
			String str = "";
			for(int i = 0; i < stack.getInt(sp - 1); i++)
				str += stack.getString(sp - 2);
			stack.set(sp - 2, str);
		} else {
			throw new ExecuteException("not support mul operator.");
		}
	}

	/**
	 * 除算処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void div_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) /
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) /
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) /
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) /
					  (double)stack.getInt(sp - 1));
		} else {
			throw new ExecuteException("not support div operator.");
		}
	}

	/**
	 * イコール処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void eq_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) ==
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) ==
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) ==
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) ==
					  (double)stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.STRING) && type1.equals(Type.STRING)) {
			stack.set(sp - 2, stack.getString(sp - 2).equals(
							  stack.getString(sp - 1)));
		} else {
			throw new ExecuteException("not support eq operator.");
		}
	}

	/**
	 * ノットイコール処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void ne_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) !=
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) !=
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) !=
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) !=
					  (double)stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.STRING) && type1.equals(Type.STRING)) {
			stack.set(sp - 2, !stack.getString(sp - 2).equals(
							   stack.getString(sp - 1)));
		} else {
			throw new ExecuteException("not support ne operator.");
		}
	}

	/**
	 * >処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void gt_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) >
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) >
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) >
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) >
					  (double)stack.getInt(sp - 1));
		} else {
			throw new ExecuteException("not support gt operator.");
		}
	}

	/**
	 * >=処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void ge_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) >=
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) >=
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) >=
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) >=
					  (double)stack.getInt(sp - 1));
		} else {
			throw new ExecuteException("not support ge operator.");
		}
	}

	/**
	 * <処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void lt_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) <
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) <
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) <
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) <
					  (double)stack.getInt(sp - 1));
		} else {
			throw new ExecuteException("not support lt operator.");
		}
	}

	/**
	 * <=処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	protected void le_exec()
			throws StackException, ExecuteException, ByteCodeException {
		Type type1 = stack.getType(sp - 1);
		Type type2 = stack.getType(sp - 2);
		if(type2.equals(Type.INT)	 && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getInt(sp - 2) <=
							  stack.getInt(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) <=
							  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.INT)	 && type1.equals(Type.DOUBLE)) {
			stack.set(sp - 2, (double)stack.getInt(sp - 2) <=
						 			  stack.getDouble(sp - 1));
		} else
		if(type2.equals(Type.DOUBLE) && type1.equals(Type.INT)) {
			stack.set(sp - 2, stack.getDouble(sp - 2) <=
					  (double)stack.getInt(sp - 1));
		} else {
			throw new ExecuteException("not support le operator.");
		}
	}

}
