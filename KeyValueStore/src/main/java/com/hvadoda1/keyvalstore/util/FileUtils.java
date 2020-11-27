package com.hvadoda1.keyvalstore.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class FileUtils {
	public static String readFile(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(file));) {
			while ((line = br.readLine()) != null)
				sb.append(line).append(System.lineSeparator());
		}
		if (sb.length() >= System.lineSeparator().length())
			sb.delete(sb.length() - System.lineSeparator().length(), sb.length());
		return sb.toString();
	}

	public static void writeFile(File file, String content) throws IOException {
		Objects.requireNonNull(file, "Cannot write to File as File was null");
		if (!file.exists())
			file.createNewFile();
		byte buf[] = new byte[4096];
		int nBytes;
		try (OutputStream fw = new FileOutputStream(file);
				ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());) {
			while ((nBytes = bis.read(buf)) > 0) {
				fw.write(buf, 0, nBytes);
				fw.flush();
			}
		}
	}

	/**
	 * <li>If param {@code deleteFolder} is {@code true} : Deletes only the contents
	 * of the folder represented by the parameter {@code file}, but does not delete
	 * the folder.</li>
	 * <li>Else: Deletes the whole folder represented by the parameter
	 * {@code file}</li>
	 * 
	 * @param folder
	 */
	public static void deleteDirectory(File folder, boolean deleteFolder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory())
					deleteDirectory(f, true);
				else
					f.delete();
			}
		}
		if (deleteFolder)
			folder.delete();
	}
}
