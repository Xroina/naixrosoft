package jp.naixrosoft.xronia.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileController {
	private String file;

	public FileController(String f) {
		file = f;
	}

	public String Read() throws IOException  {
		// ファイルからスクリプトを読む
		StringBuffer buf = new StringBuffer();

		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
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

	public static String getPath(String file) {
		int idx = file.lastIndexOf(File.separatorChar);
		String path = file.substring(0, idx + 1);
		return path;
	}
}
