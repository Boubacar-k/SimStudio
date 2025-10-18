package examples.ddos;

// import java.io.BufferedWriter;
// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;


class FileGenerator {
	private static File file = new File("data.csv");

    public FileGenerator() {}

    public void appendLine(String line) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l’écriture dans le fichier : " + e.getMessage());
        }
    }

    public File analyzeWithOllama() {
    File resultFile = new File("result_analysis.txt");

    try {
        if (!file.exists()) {
            System.err.println("Le fichier data.csv n'existe pas.");
            return null;
        }

        String csvContent = new String(Files.readAllBytes(file.toPath()));

        String safePrompt = escapeJson(csvContent);
        String prompt = "Analyse le contenu suivant du fichier CSV et donne un résumé :\n\n" + safePrompt;

        String jsonBody = String.format(
            "{\"model\": \"phi3\", \"prompt\": \"%s\", \"stream\": false}", 
            escapeJson(prompt)
        );

        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes("UTF-8"));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            System.err.println("Erreur HTTP : " + responseCode);
            return null;
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        String resultText = extractResponse(response.toString());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))) {
            writer.write(resultText);
        }

        System.out.println("Analyse terminée. Résultat dans " + resultFile.getAbsolutePath());
        return resultFile;

    } catch (IOException e) {
        System.err.println("Erreur lors de l'analyse : " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
private static String escapeJson(String str) {
    return str.replace("\\", "\\\\")
              .replace("\"", "\\\"")
              .replace("\n", "\\n")
              .replace("\r", "\\r")
              .replace("\t", "\\t");
}


    private static String extractResponse(String json) {
        int start = json.indexOf("\"response\":\"");
        if (start == -1) return json;
        start += 12;
        int end = json.indexOf("\"", start);
        if (end == -1) end = json.length();
        return json.substring(start, end).replace("\\n", "\n");
    }
}
