package dev.ranieri.assesors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RevAssess implements AfterTestExecutionCallback {

	private static Gson gson = new Gson();

	@Override
	public void afterTestExecution(ExtensionContext context) {
		Map<String, JUnitTest> testsMap = getAllTests();
		
		if (context.getExecutionException().isPresent()) {
			JUnitTest test = new JUnitTest(context.getDisplayName(), 0, false,
					context.getExecutionException().get().toString());
			test.errorMessage = test.errorMessage.replace('<', '^');
			test.errorMessage = test.errorMessage.replace('>', '^');

			testsMap.put(test.testName, test);
			writeToJsonFile(testsMap);
		} else {

			JUnitTest test = new JUnitTest(context.getDisplayName(),
					context.getTestMethod().get().getDeclaredAnnotation(RevaTest.class).points(), true, "SUCCESS");

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
		try (FileWriter file = new FileWriter("testresults.json")) {

			file.write(gson.toJson(testsList));
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static List<JUnitTest> readTestResultsJson() {
		
		File f = new File("testresults.json");
		
		if(!f.exists()) {
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

		public JUnitTest(String testName, int points, boolean isSuccessful, String errorMessage) {
			super();
			this.testName = testName;
			this.points = points;
			this.isSuccessful = isSuccessful;
			this.errorMessage = errorMessage;
		}

		@Override
		public String toString() {
			return "JUnitTest [testName=" + testName + ", points=" + points + ", isSuccessful=" + isSuccessful
					+ ", errorMessage=" + errorMessage + "]";
		}

	}

}
