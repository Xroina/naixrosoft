package jp.naixrosoft.xronia.script.execute;

import java.util.HashMap;
import java.util.Map;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;
import jp.naixrosoft.xronia.script.exception.ExecuteException;
import jp.naixrosoft.xronia.script.exception.StackException;
import jp.naixrosoft.xronia.script.execute.Value.Type;

/**
 * 実行クラス
 *
 * @author xronia
 *
 */
public abstract class VariableExecute extends CalcExecute {

	protected Map<Integer, Integer> variable_to_stack = new HashMap<>();
	protected Map<Integer, Integer> stack_to_variable = new HashMap<>();

	/**
	 * コントラクタ
	 *
	 * @param c	バイトコード
	 */
	protected VariableExecute(ByteCode c) {
		super(c);
	}

	/**
	 * 変数をスタックにプッシュ
	 *
	 * @throws ByteCodeException
	 * @throws ExecuteException
	 */
	protected void push_variable()
			throws ByteCodeException, ExecuteException {
		int cd = (int)code.getInt(pc + 1);
		if(!variable_to_stack.containsKey(cd)) {
			// 引数の場合
			int local_sp = sp - 1;
			for(; !stack.getType(local_sp).equals(Type.SUB_START);
					local_sp--) {
				if(local_sp < 0)
					throw new ExecuteException("not arguments area.");
			}
			for(int i = 0; i > cd; i--) {
				local_sp++;
				if(!stack.getType(local_sp).equals(Type.ADDRESS))
					continue;
				stack.set(sp, 0);
				assign_variable(sp);
				sp++;
				push_variable_main();
				return;
			}
			assign_variable(local_sp);
		}
		push_variable_main();
		return;
	}

	/**
	 * 変数をスタックにプッシュ(本体)
	 *
	 * @throws ByteCodeException
	 * @throws ExecuteException
	 */
	private void push_variable_main()
			throws StackException, ByteCodeException {
		stack.set(sp, stack.get(
				variable_to_stack.get(
						(int)code.getInt(pc + 1))));
		sp++;
		pc++;
	}

	/**
	 * 変数をスタックに割り当てる
	 *
	 * @param sp	スタックポインタ
	 * @throws ByteCodeException
	 * @throws StackException
	 */
	protected void assign_variable(int sp)
			throws ByteCodeException, StackException {
		int cd = (int)code.getInt(pc + 1);
		if(!variable_to_stack.containsKey(cd)) {
			variable_to_stack.put(cd, sp);
			stack_to_variable.put(sp, cd);
		} else {
			stack.set(variable_to_stack.get(cd), stack.get(sp));
			sp--;
		}
	}

	/**
	 * 変数を解放する。
	 *
	 * @param type	ココまで戻る
	 * @throws StackException
	 */
	protected void variable_free(Type type) throws StackException {
		do {
			if(stack_to_variable.containsKey(sp)) {
				variable_to_stack.remove(stack_to_variable.get(sp));
				stack_to_variable.remove(sp);
			}
			sp--;
		} while(!stack.getType(sp).equals(type));
	}
}
