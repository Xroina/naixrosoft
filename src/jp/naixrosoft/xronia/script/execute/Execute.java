package jp.naixrosoft.xronia.script.execute;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;
import jp.naixrosoft.xronia.script.exception.ExecuteException;
import jp.naixrosoft.xronia.script.exception.StackException;
import jp.naixrosoft.xronia.script.execute.Value.Type;

public abstract class Execute {
	private ByteCode code;
	private Stack stack = new Stack();
	private Map<Integer, Integer> variable_to_stack = new HashMap<>();
	private Map<Integer, Integer> stack_to_variable = new HashMap<>();
	private int pc = 0;
	private int sp = 0;
	private Random rnd = new Random();
	private static final LocalDateTime baseTime =
			LocalDateTime.of(2000, 1, 1, 0, 0, 0);
	private volatile boolean running = true;

	protected Execute(ByteCode c) {
		code = c;
	}

	public synchronized void stop() {
		running = false;
	}

	protected void execute() throws ExecuteException, ByteCodeException {

		for(pc = 0; pc < code.size() && running; pc++) {

//			debug();

			switch(code.getOpCode(pc)) {
			case PUSH_INT:
				stack.set(sp, code.getInt(pc + 1));
				sp++;
				pc++;
				break;

			case PUSH_DOUBLE:
				stack.set(sp, code.getDouble(pc + 1));
				sp++;
				pc++;
				break;

			case PUSH_STRING:
				stack.set(sp, code.getString(pc + 1));
				sp++;
				pc++;
				break;

			case PUSH_SUB_START:
				stack.setSubStart(sp);
				sp++;
				break;

			case PUSH_LAST_ADDRESS:
				stack.setAddress(sp, Integer.MAX_VALUE);
				sp++;
				break;

			case ADD:
				add_exec();
				sp--;
				break;

			case SUB:
				sub_exec();
				sp--;
				break;

			case MUL:
				mul_exec();
				sp--;
				break;

			case DIV:
				div_exec();
				sp--;
				break;

			case REMAIND:
				stack.set(sp - 2, stack.getInt(sp - 2) %
								  stack.getInt(sp - 1));
				sp--;
				break;

			case OR:
				stack.set(sp - 2, stack.getInt(sp - 2) |
								  stack.getInt(sp - 1));
				sp--;
				break;

			case AND:
				stack.set(sp - 2, stack.getInt(sp - 2) &
								  stack.getInt(sp - 1));
				sp--;
				break;

			case OR2:
				stack.set(sp - 2, stack.getBool(sp - 2) ||
								  stack.getBool(sp - 1));
				sp--;
				break;

			case AND2:
				stack.set(sp - 2, stack.getBool(sp - 2) &&
								  stack.getBool(sp - 1));
				sp--;
				break;

			case LEFT_SHIFT:
				stack.set(sp - 2, stack.getInt(sp - 2) >>
								  stack.getInt(sp - 1));
				sp--;
				break;

			case RIGHT_SHIFT:
				stack.set(sp - 2, stack.getInt(sp - 2) <<
								  stack.getInt(sp - 1));
				sp--;
				break;

			case MINUS:
				if(stack.getType(sp - 1).equals(Type.INT)) {
					stack.set(sp - 1, -stack.getInt(sp - 1));
				} else
				if(stack.getType(sp - 1).equals(Type.DOUBLE)) {
					stack.set(sp - 1, -stack.getDouble(sp - 1));
				} else {
					throw new ExecuteException("not support minus operator.");
				}
				break;

			case NOT:
				stack.set(sp - 1, !stack.getBool(sp - 1));
				break;

			case EQ:
				eq_exec();
				sp--;
				break;

			case NE:
				ne_exec();
				sp--;
				break;

			case GT:
				gt_exec();
				sp--;
				break;

			case GE:
				ge_exec();
				sp--;
				break;

			case LT:
				lt_exec();
				sp--;
				break;

			case LE:
				le_exec();
				sp--;
				break;

			case PUSH_VAR:
				push_variable();
				break;

			case ASSIGN:
				assign_variable(sp - 1);
				pc++;
				break;

			case JUMP:
				pc = (int)code.getInt(pc + 1) - 1;
				break;

			case JUMP_IF_ZERO:
				if(stack.getBool(sp - 1)) {
					pc = (int)code.getInt(pc + 1) - 1;
				} else {
					pc++;
				}
				sp--;
				break;

			case GOSUB:
				stack.setAddress(sp, pc + 2);
				sp++;
				pc = (int)code.getInt(pc + 1) - 1;
				break;

			case RETURN:
				sp--;
				Value val = stack.get(sp);
				variable_free(Type.ADDRESS);
				pc = (int)stack.getAddress(sp) - 1;
				variable_free(Type.SUB_START);
				stack.set(sp, val);
				sp++;
				break;

			case TIME:
				LocalDateTime tm = LocalDateTime.now();
				stack.set(sp, ChronoUnit.MILLIS.between(baseTime, tm));
				sp++;
				break;

			case RANDOMIZE:
				rnd = new Random(stack.getInt(sp - 1));
				sp--;
				break;

			case RND:
				stack.set(sp, rnd.nextLong());
				sp++;
				break;

			case ABS:
				if(stack.getType(sp - 1).equals(Type.INT)) {
					stack.set(sp - 1, Math.abs(stack.getInt(sp - 1)));
				} else
				if(stack.getType(sp - 1).equals(Type.DOUBLE)) {
					stack.set(sp - 1, Math.abs(stack.getDouble(sp - 1)));
				} else {
					throw new ExecuteException("Not Support Value.");
				}
				break;

			case PI:
				stack.set(sp - 1, Math.PI * stack.getDouble(sp - 1));
				break;

			case RAD:
				stack.set(sp - 1, Math.toRadians(stack.getDouble(sp - 1)));
				break;

			case DEG:
				stack.set(sp - 1, Math.toDegrees(stack.getDouble(sp - 1)));
				break;

			case SIN:
				stack.set(sp - 1, Math.sin(stack.getDouble(sp - 1)));
				break;

			case COS:
				stack.set(sp - 1, Math.cos(stack.getDouble(sp - 1)));
				break;

			case TAN:
				stack.set(sp - 1, Math.tan(stack.getDouble(sp - 1)));
				break;

			case ASIN:
				stack.set(sp - 1, Math.asin(stack.getDouble(sp - 1)));
				break;

			case ACOS:
				stack.set(sp - 1, Math.acos(stack.getDouble(sp - 1)));
				break;

			case ATAN:
				stack.set(sp - 1, Math.atan(stack.getDouble(sp - 1)));
				break;

			case SQR:
				stack.set(sp - 1, Math.sqrt(stack.getDouble(sp - 1)));
				break;

			case LOG:
				stack.set(sp - 1, Math.log(stack.getDouble(sp - 1)));
				break;

			case SIG:
				if(stack.getType(sp - 1).equals(Type.INT)) {
					stack.set(sp - 1, (int)Math.signum((double)stack.getInt(sp - 1)));
				} else
				if(stack.getType(sp - 1).equals(Type.DOUBLE)) {
					stack.set(sp - 1, Math.signum(stack.getDouble(sp - 1)));
				} else {
					throw new ExecuteException("Not Support Value.");
				}
				break;

			case INT:
				stack.set(sp - 1, Math.floor(stack.getDouble(sp - 1)));
				break;

			case STICK_X:
				stack.set(sp, stickX());
				sp++;
				break;

			case STICK_Y:
				stack.set(sp, stickY());
				sp++;
				break;

			case BUTTON:
				stack.set(sp, button());
				sp++;
				break;

			case PRINT:
				switch(stack.getType(sp - 1)) {
				case INT:
					print(String.valueOf(stack.getInt(sp - 1)));
					break;
				case DOUBLE:
					print(String.valueOf(stack.getDouble(sp - 1)));
					break;
				case STRING:
					print(stack.getString(sp - 1));
					break;
				default:
					throw new ExecuteException("Not Support Value.");
				}
				sp--;
				break;

			case CLS:
				cls();
				break;

			case LOCATE:
				locate((int)stack.getInt(sp - 2), (int)stack.getInt(sp - 1));
				sp -= 2;
				break;

			case CHARACTER:
				stack.set(sp - 2,
						getCharacter((int)stack.getInt(sp - 2),
									 (int)stack.getInt(sp - 1)));
				sp--;
				break;

			default:
				throw new ExecuteException("Not Support Operator.");
			}
		}
		return;
	}

	/**
	 * 加算処理
	 *
	 * @throws StackException
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
	private void add_exec()
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
	private void sub_exec()
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
	private void mul_exec()
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
	private void div_exec()
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
	private void eq_exec()
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
	private void ne_exec()
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
	private void gt_exec()
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
	private void ge_exec()
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
	private void lt_exec()
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
	private void le_exec()
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

	/**
	 * 変数をスタックにプッシュ
	 *
	 * @throws ByteCodeException
	 * @throws ExecuteException
	 */
	private void push_variable()
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
	private void assign_variable(int sp)
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
	private void variable_free(Type type) throws StackException {
		do {
			if(stack_to_variable.containsKey(sp)) {
				variable_to_stack.remove(stack_to_variable.get(sp));
				stack_to_variable.remove(sp);
			}
			sp--;
		} while(!stack.getType(sp).equals(type));
	}

	/**
	 * デバックプリント
	 *
	 * @throws ByteCodeException
	 * @throws StackException
	 */
	public void debug() throws ByteCodeException, StackException {
		System.out.println("pc:" + String.valueOf(pc) +
				" sp:" + String.valueOf(sp) +
				" code:" + code.getOpCode(pc));
		System.out.println("Stack:");
		for(int i = 0; i < stack.size(); i++) {
			System.out.print(String.valueOf(i) + ":");
			switch(stack.getType(i)) {
			case INT:
				System.out.print(String.valueOf(stack.getInt(i)));
				break;
			case DOUBLE:
				System.out.print(String.valueOf(stack.getDouble(i)));
				break;
			case STRING:
				System.out.print(stack.getString(i));
				break;
			case ADDRESS:
				System.out.print(stack.getAddress(i));
				break;
			default:
				break;
			}
			System.out.println("(" + stack.getType(i) + ")");
		}
		System.out.println(": End Stack");
	}

	/**
	 * Print
	 *
	 * @param str 出力文字列
	 */
	protected abstract void print(String str);

	/**
	 * クリアスクリーン
	 */
	protected abstract void cls();

	/**
	 * 座標指定
	 *
	 * @param x	座標
	 * @param y 座標
	 */
	protected abstract void locate(int x, int y);

	protected abstract double stickX();

	protected abstract double stickY();

	protected abstract long button();

	/**
	 * キャラクタ取得
	 *
	 * @param x	座標
	 * @param y 座標
	 * @return	キャラクタ文字
	 */
	protected abstract String getCharacter(int x, int y);
}
