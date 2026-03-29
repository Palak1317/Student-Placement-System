package service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAIService {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");

    public static String generateResume(String name, String skills,
                                        String education, String experience,
                                        String jobTitle, String company,
                                        String jobSkills) {

        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String prompt = "Create a professional resume for:\n" +
                    "Name: " + name + "\n" +
                    "Skills: " + skills + "\n" +
                    "Education: " + education + "\n" +
                    "Experience: " + experience + "\n\n" +
                    "Target Job: " + jobTitle + " at " + company + "\n" +
                    "Required Skills: " + jobSkills + "\n\n" +
                    "Make it ATS-friendly and well formatted.";

            String safePrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            String jsonInput = "{\n" +
                    "\"model\": \"gpt-3.5-turbo\",\n" +
                    "\"messages\": [\n" +
                    "  {\"role\": \"user\", \"content\": \"" + safePrompt + "\"}\n" +
                    "]\n" +
                    "}";

            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes());
            os.flush();

            BufferedReader br;

            if (conn.getResponseCode() >= 400) {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            String json = response.toString();

            // 🔥 DEBUG (VERY IMPORTANT)
            System.out.println("OPENAI RESPONSE: " + json);

            // ✅ SAFER EXTRACTION
            if (json.contains("content")) {
                int start = json.indexOf("\"content\":\"") + 11;
                int end = json.indexOf("\"", start);
                return json.substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
            }

            return "Error generating resume";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating resume";
        }
    }

    // 🔥 Extract only AI text from JSON
    private static String extractText(String json) {

        try {
            int start = json.indexOf("\"content\":\"") + 11;
            int end = json.indexOf("\"", start);

            return json.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");

        } catch (Exception e) {
            return json;
        }
    }
}