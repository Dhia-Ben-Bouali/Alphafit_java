package util;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SentimentAnalyzer {
    public static String analyzeAnswersSentiment(List<String> formAnswerStrings) {
        String result=null;
        try {
            String apiKey = "d88e7b03c1msh454bbc15759a31fp18861cjsn2ff2cf7b6afc"; // Your OpenAI API key
            String endpoint = "open-ai21.p.rapidapi.com"; // Adjust the endpoint

            // Construct the prompt with user answers
            JSONArray messagesArray = new JSONArray();
            for (String answer : formAnswerStrings) {
                JSONObject messageObject = new JSONObject();
                messageObject.put("role", "user");
                messageObject.put("content", answer);
                messagesArray.put(messageObject);
            }


            


            // Construct the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("messages", messagesArray);
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("prompt", "Calculate the percentage of satisfaction based on the sentiment of the responses.");

            // Send the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + endpoint + "/conversationgpt35"))
                    .header("content-type", "application/json")
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", endpoint)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Process the response
            if (response.statusCode() == 200) {
                processResponse(response.body());
                result = processResponse(response.body());
            } else {
                System.err.println("Error: HTTP " + response.statusCode() + " - " + response.body());
            }
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String processResponse(String responseBody) {
        // Add your processing logic here
        System.out.println("Response from GPT-3.5:\n" + responseBody);
        return responseBody;
    }
}

