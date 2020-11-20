package dev.ranieri.assesors;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;



public class RevAssess implements AfterTestExecutionCallback, AfterAllCallback {

	private static Gson gson = new Gson();
	private static RevAssessConfig config = null;
	
	
	private static void readRevAssessConfig() {
		try (FileReader reader = new FileReader("RevAssessConfig.json")) {
			
			StringBuilder json = new StringBuilder("");
			int i;
			while ((i = reader.read()) != -1) {
				json.append((char) i);
			}
			config = gson.fromJson(json.toString(), RevAssessConfig.class);
			System.out.println(config);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		
		readRevAssessConfig();

		File directoryToZip = new File(System.getProperty("user.dir"));
		List<File> fileList = new ArrayList<File>();
		getAllFiles(directoryToZip, fileList);
		writeZipFile(directoryToZip, fileList);
		
		String [] path = System.getProperty("user.dir").toString().split("\\\\");
		String zipFile = path[path.length-1] + ".zip";
		zipFile = System.getProperty("user.dir").toString() +"\\" +zipFile;
		
		HttpClient client = HttpClientBuilder.create().build();
		File file = new File(zipFile);
		FileBody fb = new FileBody(file,ContentType.DEFAULT_BINARY);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addPart("file", fb);
		HttpEntity entity = builder.build();
		HttpPost post = new HttpPost(config.serverLocation);
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		file.delete();

	}

	public static void getAllFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				// System.out.println("directory:" + file.getCanonicalPath());
				getAllFiles(file, fileList);
			} else {
				// System.out.println(" file:" + file.getCanonicalPath());
			}
		}
	}

	public static void writeZipFile(File directoryToZip, List<File> fileList) {

		try {
			FileOutputStream fos = new FileOutputStream(directoryToZip.getName() + ".zip");
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

	public static void addToZip(File directoryToZip, File file, ZipOutputStream zos)
			throws FileNotFoundException, IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		// System.out.println("Writing '" + zipFilePath + "' to zip file");
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

	@Override
	public void afterTestExecution(ExtensionContext context) {
		Map<String, JUnitTest> testsMap = getAllTests();

		if (context.getExecutionException().isPresent()) {
			JUnitTest test = new JUnitTest(context.getDisplayName(), 0, false,
					context.getExecutionException().get().toString(),
					context.getTestMethod().get().getDeclaredAnnotation(RevaTest.class).tier());
			test.errorMessage = test.errorMessage.replace('<', '^');
			test.errorMessage = test.errorMessage.replace('>', '^');

			testsMap.put(test.testName, test);
			writeToJsonFile(testsMap);
		} else {

			JUnitTest test = new JUnitTest(context.getDisplayName(),
					context.getTestMethod().get().getDeclaredAnnotation(RevaTest.class).points(), true, "SUCCESS",
					context.getTestMethod().get().getDeclaredAnnotation(RevaTest.class).tier());

			testsMap.put(test.testName, test);
			writeToJsonFile(testsMap);
		}

	}

	public static Map<String, JUnitTest> getAllTests() {
		List<JUnitTest> allTests = readTestResultsJson();
		Map<String, JUnitTest> testsMap = new HashMap<String, JUnitTest>();
		for (JUnitTest test : allTests) {
			testsMap.put(test.testName, test);
		}
		return testsMap;
	}

	public static void writeToJsonFile(Map<String, JUnitTest> tests) {

		List<JUnitTest> testsList = new ArrayList<JUnitTest>(tests.values());
		int totalPoints = 0;
		for(JUnitTest test : testsList) {
			totalPoints += test.points;
		}
		try (FileWriter file = new FileWriter("testresults.json")) {

			file.write(gson.toJson(testsList));
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static List<JUnitTest> readTestResultsJson() {

		File f = new File("testresults.json");

		if (!f.exists()) {
			try (FileWriter file = new FileWriter("testresults.json")) {
				file.write("[]");
				file.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		Type junitListType = new TypeToken<List<JUnitTest>>() {
		}.getType();
		try (FileReader reader = new FileReader("testresults.json")) {

			StringBuilder json = new StringBuilder("");
			int i;
			while ((i = reader.read()) != -1) {
				json.append((char) i);
			}

			List<JUnitTest> allTests = gson.fromJson(json.toString(), junitListType);
			return allTests;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	class JUnitTest {
		String testName;
		int points;
		boolean isSuccessful;
		String errorMessage;
		int tier;

		public JUnitTest(String testName, int points, boolean isSuccessful, String errorMessage, int tier) {
			super();
			this.testName = testName;
			this.points = points;
			this.isSuccessful = isSuccessful;
			this.errorMessage = errorMessage;
			this.tier = tier;
		}

		@Override
		public String toString() {
			return "JUnitTest [testName=" + testName + ", points=" + points + ", isSuccessful=" + isSuccessful
					+ ", errorMessage=" + errorMessage + "]";
		}

	}
	
	class RevAssessConfig{
		String associateEmail;
		String associateName;
		int assessmentId;
		String serverLocation;
		List<Tier> tiers;
		public RevAssessConfig(String associateEmail, String associateName, int assessmentId, String serverLocaiton,
				List<Tier> tiers) {
			super();
			this.associateEmail = associateEmail;
			this.associateName = associateName;
			this.assessmentId = assessmentId;
			this.serverLocation = serverLocaiton;
			this.tiers = tiers;
		}
		public RevAssessConfig() {
			super();
		}
		@Override
		public String toString() {
			return "RevAssessConfig [associateEmail=" + associateEmail + ", associateName=" + associateName
					+ ", assessmentId=" + assessmentId + ", serverLocation=" + serverLocation + ", tiers=" + tiers
					+ "]";
		}
		
		
	}
	
	class Tier{
		int level;
		int threshold;
	}

}
