package com.catsmoker.aimedicalagent;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiApiClient {

    private static final String API_KEY = "AIzaSyD5oVNy3kvVjo9oGIkn9xlAf8SFs2FcmlE";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface DiagnosisCallback {
        void onDiagnosisReceived(String diagnosis, String medication);
        void onError(String error);
    }

    public void getDiagnosis(String symptoms, DiagnosisCallback callback) {
        executor.execute(() -> {
            try {
                RequestBody body = RequestBody.create(createRequestBody(symptoms), JSON);
                Request request = new Request.Builder().url(API_URL).post(body).build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        postError(callback, "API request failed: " + response.code());
                        return;
                    }

                    String responseBody = response.body() != null ? response.body().string() : null;
                    if (responseBody == null) {
                        postError(callback, "Empty response");
                        return;
                    }

                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String generatedText = jsonResponse
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

                    String diagnosis = "Unknown";
                    String medication = "Consult a doctor";

                    if (generatedText.contains("Diagnosis:") && generatedText.contains("Medication:")) {
                        diagnosis = generatedText.split("Diagnosis:")[1].split(",")[0].trim();
                        medication = generatedText.split("Medication:")[1].trim();
                    }

                    String finalDiagnosis = diagnosis;
                    String finalMedication = medication;

                    mainHandler.post(() -> callback.onDiagnosisReceived(finalDiagnosis, finalMedication));
                }
            } catch (Exception e) {
                Log.e("GeminiApiClient", "Error: " + e.getMessage());
                postError(callback, e.getMessage());
            }
        });
    }

    private String createRequestBody(String symptoms) throws Exception {
        JSONObject requestBodyJson = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject contentObject = new JSONObject();
        JSONArray partsArray = new JSONArray();
        JSONObject partObject = new JSONObject();

        partObject.put("text", "You are a medical AI assistant. Based on the following symptoms, provide a possible diagnosis and a suggested medication in the format: Diagnosis: [condition], Medication: [medication]. Symptoms: " + symptoms);
        partsArray.put(partObject);
        contentObject.put("parts", partsArray);
        contentsArray.put(contentObject);
        requestBodyJson.put("contents", contentsArray);

        return requestBodyJson.toString();
    }

    private void postError(DiagnosisCallback callback, String error) {
        mainHandler.post(() -> callback.onError(error));
    }
}
