package com.revature.assessors;

import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RevAssess implements AfterAllCallback, AfterTestExecutionCallback {

    private static List<Spec> specs = new ArrayList<Spec>();
    private static Gson gson = new Gson();

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        RevaConfig config = context.getTestClass().get().getDeclaredAnnotation(RevaConfig.class);

        AssessmentPayload payload = new AssessmentPayload();
        payload.setEmail(config.email());
        payload.setExerciseId(config.exerciseId());
        payload.setTests(specs);
        payload.setSourceCode(getSourceCodeBase64(config.path()));
        sendPayload(payload, config.server());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Spec spec = new Spec();
        spec.setTestName(context.getDisplayName());
        spec.setSuccessful(context.getExecutionException().isEmpty());
        spec.setPoints(context.getTestMethod().get().getDeclaredAnnotation(RevaTest.class).points());
        spec.setErrorMessage(context.getExecutionException().isEmpty()? "SUCCESS": context.getExecutionException().get().toString().replace('<', '^'));
        specs.add(spec);
    }

    private void sendPayload(AssessmentPayload payload, String url){
        String json = gson.toJson(payload);
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type","application/json");
        try {
            request.setEntity(new StringEntity(json));
            client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createZip(String path){
        ZipUtils appZip = new ZipUtils();
        ZipUtils.SOURCE_FOLDER = path;
        appZip.generateFileList(new File(ZipUtils.SOURCE_FOLDER));
        appZip.zipIt(ZipUtils.OUTPUT_ZIP_FILE);
    }

    private void destroyZip(){
        ZipUtils appZip = new ZipUtils();
        appZip.deleteZip();
    }

    private String getSourceCodeBase64(String path){
        createZip(path);
        String encoded = ZipUtils.base64SourceCode();
        destroyZip();
        return encoded;
    }
}
