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

		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, detector());
		BufferedReader br = new BufferedReader(isr);

		int c;
		while((c = br.read()) != -1) {
			buf.append((char)c);
		}

		br.close();
		isr.close();
		fis.close();

		return buf.toString();
	}

  /**
   * 文字コードを判定するメソッド
   *
   * @param ファイルパス
   * @return 文字コード
   */
	public String detector() throws IOException {
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
		if (encoding != null) {
			System.out.println(file + ":文字コード = " + encoding);
		} else {
			System.out.println(file + ":文字コードを判定できませんでした");
			encoding = "UTF-8";
		}

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
