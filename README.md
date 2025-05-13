# AI Medical Agent

AI Medical Agent is an Android application designed to provide potential medical diagnoses and medication suggestions based on user-inputted symptoms. It utilizes Google's Gemini AI for generating this information. The app also features a simulated subscription model for "doctor-verified" diagnoses and an option to search for suggested medications on Amazon.

**Disclaimer:** This application is for informational and demonstrative purposes only. It is **NOT** a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition. Never disregard professional medical advice or delay in seeking it because of something you have read on this app.

## Features

*   **Symptom Input:** Users can enter their medical symptoms into a text field.
*   **AI-Powered Diagnosis:**
    *   Utilizes Google's Gemini API to process symptoms and provide a potential diagnosis and medication suggestion.
    *   Displays "AI Diagnosis" and "Suggested medication" for free users.
*   **Subscription Model (Simulated):**
    *   Users can "upgrade" via a dedicated screen.
    *   Subscribed users see "Doctor's Diagnosis" and "Prescription".
    *   Subscribed users can access a "Verify by Doctor" feature (simulated).
*   **Buy Medication:**
    *   A "Buy" button allows users to search for the suggested medication on Amazon.com via an in-app WebView.
*   **Verify by Doctor (Simulated):**
    *   For subscribed users, a "Verify" button simulates a doctor's verification of the AI's diagnosis.

## Core Components

1.  **`MainActivity.java`**:
    *   The main screen of the application.
    *   Handles user input for symptoms.
    *   Communicates with `GeminiApiClient` to get diagnosis and medication.
    *   Displays the results.
    *   Manages UI elements for "Enter", "Buy", "Upgrade", and "Verify" actions.
    *   Checks subscription status from SharedPreferences.

2.  **`GeminiApiClient.java`**:
    *   Responsible for making API calls to the Google Gemini API.
    *   Constructs the request body with the user's symptoms and a specific prompt.
    *   Parses the JSON response to extract diagnosis and medication.
    *   Handles API errors and communicates results back via a callback.
    *   **IMPORTANT:** Contains a hardcoded API key. This is **NOT secure** for production applications.

3.  **`UpgradeActivity.java`**:
    *   A simple screen with a "Subscribe" button.
    *   Clicking "Subscribe" updates the `isSubscribed` flag in SharedPreferences to `true`.

4.  **`WebViewActivity.java`**:
    *   Hosts a WebView to display web content.
    *   Used to load Amazon search results for the suggested medication.

## How It Works

1.  The user launches the app and is presented with `MainActivity`.
2.  The user types their symptoms into the `EditText` field.
3.  The user taps the "Enter" button.
4.  `MainActivity` retrieves the symptoms and calls `geminiApiClient.getDiagnosis()`.
5.  `GeminiApiClient` constructs a JSON request with the symptoms and a predefined prompt ("You are a medical AI assistant...").
6.  The request is sent to the Google Gemini API (`generativelanguage.googleapis.com`).
7.  The API processes the request and returns a JSON response containing the AI-generated text.
8.  `GeminiApiClient` parses this response to extract the diagnosis and medication.
9.  The results are passed back to `MainActivity` via the `DiagnosisCallback`.
10. `MainActivity` updates the `TextViews` to display the diagnosis and medication.
    *   If the user is subscribed, the text indicates "Doctor's Diagnosis" and "Prescription".
    *   Otherwise, it shows "AI Diagnosis" and "Suggested medication".
11. The "Buy" button, when clicked, takes the medication name, forms an Amazon search URL, and opens `WebViewActivity` to display the search results.
12. The "Upgrade" button navigates to `UpgradeActivity`, where the user can tap "Subscribe" to simulate a subscription.
13. The "Verify" button (enabled for subscribed users) shows a Toast message simulating doctor verification.

## Important Considerations

*   **API Key Security:** The hardcoded API key is a significant security risk. Always use secure methods like `local.properties` and `BuildConfig` to manage API keys.
*   **Medical Disclaimer:** The disclaimer at the top of this README and within the app (if you choose to add it) is crucial. This app should not be mistaken for a real medical diagnostic tool.
*   **"Verify by Doctor" Simulation:** The current "Verify" feature is purely a simulation (shows a Toast). A real implementation would require backend integration with certified medical professionals.
*   **Gemini API Usage & Costs:** Be mindful of the usage limits and potential costs associated with the Google Gemini API.
*   **Error Handling:** The current error handling is basic. More robust error handling and user feedback mechanisms could be implemented.
*   **UI/UX:** The UI is functional but could be enhanced for a better user experience.

## Future Improvements

*   Implement secure API key storage.
*   Enhance UI/UX design.
*   Add more detailed error messages and user guidance.
*   Integrate with a proper user authentication system if user-specific data needs to be stored.
*   Explore more sophisticated parsing of the Gemini API response for better accuracy.
*   Consider localization for different languages.
*   For a real product: Integrate with actual medical professionals for verification.

## Contributing

This project is currently a conceptual example. If it were an open-source project, contributions would be welcome. Please fork the repository and submit a pull request.

---

com.catsmoker.aimedicalagent

![qrcode_201284726_526e4947dca1ddabd6cfc5aee1d36d6f(2)](https://github.com/user-attachments/assets/08ee5de5-214b-41af-bc53-93e76a5c0228)
