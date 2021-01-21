package com.revature.assessors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class ZipUtil {
	
	public static void main(String [] args) {
		artifactBase64();
	}
	
	public static String artifactBase64() {
		zipCurrentDirectory();
		String data = convertZipToBase64();
		return data;
	}
	
	private static void zipCurrentDirectory() {
		File directoryToZip = new File(System.getProperty("user.dir"));
		List<File> fileList = new ArrayList<File>();	
		getAllFiles(directoryToZip, fileList);
		writeZipFile(directoryToZip, fileList);		
	}
	
	@SuppressWarnings("restriction")
	private static String convertZipToBase64() {
		String zipFilePath = System.getProperty("user.dir") +"\\project.zip";
		File zipFile = new File(zipFilePath);
		try {
			byte [] data = Files.readAllBytes(zipFile.toPath());
			@SuppressWarnings("deprecation")
			String encoded = Base64.encode(data);
			return encoded;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally {
			zipFile.delete();
		}
		
	}

	private static void getAllFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		
		for (File file : files) {
			// prevents very large files from being added
			if(file.length()>15000) {
				continue;
			}
			fileList.add(file);
			if (file.isDirectory()) {
				getAllFiles(file, fileList);
			} else {
			}
		}
	}

	private static void writeZipFile(File directoryToZip, List<File> fileList) {

		try {
			FileOutputStream fos = new FileOutputStream("project.zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
			IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}


}
