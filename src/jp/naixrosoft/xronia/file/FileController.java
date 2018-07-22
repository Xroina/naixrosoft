package jp.naixrosoft.xronia.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * ファイルコントローラクラス
 *
 * @author xronia
 *
 */
public class FileController {
	private String file;

	/**
	 * コントラクタ
	 *
	 * @param f		ファイル名
	 */
	public FileController(String f) {
		file = f;
	}

	/**
	 * ファイルからテキストを読む
	 *
	 * @return		読み込んだテキスト
	 * @throws IOException
	 */
	public String Read() throws IOException {
		StringBuffer buf = new StringBuffer();

		String encode = getEncode();	// エンコーディングの取得
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, encode);
		BufferedReader br = new BufferedReader(isr);

		int c;
		while((c = br.read()) != -1) {
			buf.append((char)c);
		}

		br.close();
		isr.close();
		fis.close();

		String text = buf.toString();
		// BOMを取り除く
		if(encode.indexOf("UTF") == 0 && text.charAt(0) == 65279) {
			text = text.substring(1);
		}
		return text;
	}

	/**
	 * 文字コードを判定するメソッド
	 *
	 * @return	文字コード
	 */
	public String getEncode() throws IOException {
		byte[] buf = new byte[4096];

		FileInputStream fis = new FileInputStream(file);

		// 文字コード判定ライブラリの実装
		UniversalDetector detector = new UniversalDetector(null);

		// 判定開始
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}

		// 判定終了
		detector.dataEnd();
		fis.close();

		// 文字コード判定
		String encoding = detector.getDetectedCharset();
		if (encoding == null) encoding = "MS932";

		// 判定の初期化
		detector.reset();

		return encoding;
	}

	/**
	 * フルパスからパス部だけを取得する
	 *
	 * @param file	ファイル名(フルパス)
	 * @return		パス部
	 */
	public static String getPath(String file) {
		int idx = file.lastIndexOf(File.separatorChar);
		String path = file.substring(0, idx + 1);
		return path;
	}
}
