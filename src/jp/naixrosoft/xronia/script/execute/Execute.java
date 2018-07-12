package jp.naixrosoft.xronia.script.execute;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ByteCodeException;
import jp.naixrosoft.xronia.script.exception.ExecuteException;
import jp.naixrosoft.xronia.script.execute.Value.Type;

/**
 * 実行クラス
 *
 * @author xronia
 *
 */
public abstract class Execute extends VariableExecute implements ImplExecute {

	protected Random rnd = new Random();
	protected static final LocalDateTime baseTime =
			LocalDateTime.of(2000, 1, 1, 0, 0, 0);

	/**
	 * コントラクタ
	 *
	 * @param c	バイトコード
	 */
	protected Execute(ByteCode c) {
		super(c);
	}

	/**
	 * 実行メソッド
	 *
	 * @throws ExecuteException
	 * @throws ByteCodeException
	 */
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
				stack.set(sp, getStickX());
				sp++;
				break;

			case STICK_Y:
				stack.set(sp, getStickY());
				sp++;
				break;

			case BUTTON:
				stack.set(sp, getButton());
				sp++;
				break;

			case PRINT:
				switch(stack.getType(sp - 1)) {
				case INT:
					doPrint(String.valueOf(stack.getInt(sp - 1)));
					break;
				case DOUBLE:
					doPrint(String.valueOf(stack.getDouble(sp - 1)));
					break;
				case STRING:
					doPrint(stack.getString(sp - 1));
					break;
				default:
					throw new ExecuteException("Not Support Value.");
				}
				sp--;
				break;

			case CLS:
				doCls();
				break;

			case LOCATE:
				setLocate((int)stack.getInt(sp - 2), (int)stack.getInt(sp - 1));
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
}
