package jp.naixrosoft.xronia.script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import jp.naixrosoft.xronia.file.FileController;
import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.bytecode.ByteCode.OpCode;
import jp.naixrosoft.xronia.script.exception.ScriptException;
import jp.naixrosoft.xronia.script.lex.LexicalAnalyzer;
import jp.naixrosoft.xronia.script.perser.Function;
import jp.naixrosoft.xronia.script.perser.Perser;
import jp.naixrosoft.xronia.script.perser.Variable;
import jp.naixrosoft.xronia.script.token.Tokens;

/**
 * スクリプトクラス
 *
 * @author xronia
 *
 */
public class Script {

	ByteCode code;		// バイトコード
	Function function;	// 関数
	int var_size;		// 変数のサイズ

	/**
	 * コントラクタ
	 *
	 * @param c			バイトコード
	 */
	public Script(ByteCode c) {
		code = c;					// バイトコード
		function = new Function();	// 関数
		var_size = 0;				// 変数のサイズ
	}

	/**
	 * バイトコードコンパイラ
	 *
	 * @param file				ソースファイル名
	 * @param arguments			パラメータリスト
	 * @throws IOException
	 * @throws ScriptException
	 */
	public void compile(String file, List<String> arguments)
			throws IOException, ScriptException {

		List<String> functions = new ArrayList<>();

		arguments(arguments);				// 引数をバイトコードに展開

		// ソースの存在するパスを取得する。
		File f = new File(file);
		if(!f.exists()) {
			throw new ScriptException("\"" + file + "\"File Not Found");
		}

		String canonicalPath = null;		// 完全なパス名を取得する
		try {
			canonicalPath = f.getCanonicalPath();
		} catch(IOException e) {
			throw new ScriptException(
					"\"" + file + "\" File Canonical Path Exception");
		}
		String base = FileController.getPath(canonicalPath);	// PATH部だけ取得

		// 最初の関数の定義
		function.createNewFunction(canonicalPath);

		// 関数読み込み本体
		while(!function.isAddressMapping()) {
			for(Entry<String, Integer> entry: function.getFunctionSet()) {

				if(Collections.binarySearch(functions, entry.getKey()) >= 0)
					continue;					// コンパイル済みなら処理しない

				main(entry.getKey(), entry.getValue(), base);	// コンパイル本体

				functions.add(entry.getKey());	// コンパイル済み関数リストへ登録
				Collections.sort(functions);	// ソートしておく
			}
		}

		// 関数へのアドレスの調整
		for(int i = 0; i < code.size(); i++) {
			switch(code.getOpCode(i)) {
			case PUSH_INT:		// プッシュと割り当ては何もしないで次のオペコードへ
			case PUSH_DOUBLE:
			case PUSH_STRING:
			case PUSH_VAR:
			case ASSIGN:
				i++;
				break;

			case JUMP:			// アドレスは関数へのアドレスがあるかを見て調整
			case JUMP_IF_ZERO:
			case GOSUB:
				i++;
				if(code.getInt(i) < 0) {
					code.setInt(i, function.getAddress((int)code.getInt(i)));
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * バイトコードコンパイラ(メイン)
	 *
	 * @param file			ソースファイル名
	 * @param label			関数ラベル(index)
	 * @param base			呼び出しソースファイルのベースパス
	 * @throws IOException
	 * @throws ScriptException
	 */
	private void main(String file, int label, String base)
			throws IOException, ScriptException {

		// ファイルからスクリプトを読む
		FileController fc = new FileController(file);
		String src = fc.Read();
		String path = FileController.getPath(file);

		// トークン作成して、レキシュアルアナライザを起動する
		Tokens tokens = new Tokens();
		LexicalAnalyzer lex = new LexicalAnalyzer(src, tokens);
		lex.analyze();

		// バイトコードと変数を作成
		ByteCode code = new ByteCode();
		Variable variable = new Variable();

		// パーサーを通す
		Perser perser = new Perser(
				tokens, code, function, variable, base, path);
		perser.parse();

		// 作ったコードをずらしてマージする
		int code_size = this.code.size();	// バイトコードのサイズ
		// ラベルリストの更新
		function.setAddress(label, code_size);
		for(int i = 0; i < code.size(); i++) {
			this.code.add(code.get(i));	// マージ本体

			switch(code.getOpCode(i)) {
			case PUSH_INT:		// 値のプッシュは何もしないでコードをマージ
			case PUSH_DOUBLE:
			case PUSH_STRING:
				i++;
				this.code.add(code.get(i));
				break;

			case PUSH_VAR:		// ローカル変数のプッシュと割り当ては変数サイズだけずらす
			case ASSIGN:
				i++;
				if(code.getInt(i) >= 0) {
					this.code.add(code.getInt(i) + var_size);
				} else {
					this.code.add(code.getInt(i));	// 引数となる変数は放置
				}
				break;

			case JUMP:			// アドレスはコードサイズ分ずらす。
			case JUMP_IF_ZERO:
			case GOSUB:
				i++;
				if(code.getInt(i) >= 0) {
					this.code.add(code.getInt(i) + code_size);
				} else {
					this.code.add(code.getInt(i));	// 関数へのアドレスは後回し
				}
				break;

			default:			// その他のオペコードは何もしない
				break;
			}
		}

		// 変数サイズの調整
		var_size += variable.size();
	}

	/**
	 * 引数をバイトコードに展開
	 *
	 * @param arguments			引数リスト
	 */
	private void arguments(List<String> arguments) {

		code.add(OpCode.PUSH_SUB_START);

		for(String arg: arguments) {
			try {
				long var = Long.parseLong(arg);
				code.add(OpCode.PUSH_INT);
				code.add(var);
			} catch(NumberFormatException e1) {
				try {
					double var = Double.parseDouble(arg);
					code.add(OpCode.PUSH_DOUBLE);
					code.add(var);
				} catch(NumberFormatException e2) {
					code.add(OpCode.PUSH_STRING);
					code.add(arg);

				}
			}
		}

		code.add(OpCode.PUSH_LAST_ADDRESS);
	}
}
