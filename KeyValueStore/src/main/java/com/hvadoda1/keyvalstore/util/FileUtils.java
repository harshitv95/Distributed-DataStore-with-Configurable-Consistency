package com.hvadoda1.keyvalstore.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
	 * <li>If param {@code deleteFolder} is {@code false} : Deletes only the
	 * contents of the folder represented by the parameter {@code file}, but does
	 * not delete the folder.</li>
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

	public static BufferedReader fileReader(File file) throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	}

	public static FileWriter fileAppender(File file) throws IOException {
		return fileAppender(file, true);
	}

	public static FileWriter fileAppender(File file, boolean truncateFileContents) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return new FileWriter(file, !truncateFileContents);
	}

	public static void deleteFile(File file) throws IOException {
		String message = null;
		if (!file.isFile())
			message = "Invalid file. [" + file.getAbsoluteFile() + "] is not a file";
		else if (!file.canWrite())
			message = "Cannot write to file [" + file.getAbsolutePath() + "], permission denied";
		else if (file.exists())
			file.delete();

		if (message != null)
			throw new IOException(message);
	}

	public static void renameFile(File file, String newName) throws IOException {
		if (file.getName().equals(newName))
			return;
		File newFile = new File(newName);
		if (newFile.exists() && newFile.isFile())
			throw new IOException("File [" + newName + "] already exists");
		if (!file.renameTo(newFile))
			throw new IOException("Failed to rename");
	}
}
