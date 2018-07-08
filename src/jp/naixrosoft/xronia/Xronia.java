package jp.naixrosoft.xronia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.naixrosoft.xronia.script.Script;
import jp.naixrosoft.xronia.script.bytecode.ByteCode;
import jp.naixrosoft.xronia.script.exception.ScriptException;

public class Xronia {
	public static void main (String[] args)
			throws IOException, ScriptException {

		// 引数チェック
		if(args.length == 0) {
			System.err.println("Usage:Xronia filename");
			System.exit(0);
		}

		// 第一引数はファイル名とする
		String file = args[0];
		if(file.lastIndexOf(".x") != file.length() - 2)
			file += ".x";

		// 第二引数以降をスクリプトの引数とする
		List<String> arguments = new ArrayList<>();
		for(int i = 1; i < args.length; i++)
			arguments.add(args[i]);

		// バイトコード生成
		ByteCode code = new ByteCode();

		// スクリプトのコンパイル
		Script script = new Script(code);
		script.compile(file, arguments);

		code.debug();

		// バイトコードを実行する。
		Execute exe = new Execute(code);
		exe.execute();

		System.exit(0);
	}
}
