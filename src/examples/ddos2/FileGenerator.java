package examples.ddos2;

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

        String context = """
            CONTEXTE DE LA SIMULATION :
            L'objectif principal de cette étude est d'analyser le comportement d'un système d'information face à une attaque par déni de service distribué (DDoS), 
            et de proposer un mécanisme de réaction adapté pour limiter ses effets.Pour cette simulation j'ai mis en place un modèle DEVS représentant un serveur recevant des requêtes de clients légitimes et de bots malveillants.
            Voici les modèle:
            Modèle atomique DEVS pour le client:
            Client=<X, S, Y, int, ext, , ta>
            X = "bloquer",
            Y={"requête"},
            S= {"ACTIF.LÉGITIME", "ACTIF.BOT", "INACTIF", "BLOQUÉ"},
            ta=  si  "INACTIF";
            ta=  10s si "BLOQUER";
            ta= 5s si "ACTIF.LÉGITIME";
            ta=[0,1]s  si "ACTIF.BOT"

            = "requête" si ACTIF

            int("ACTIF.LÉGITIME")= "requête",
            int("ACTIF.BOT")= "requête",
            int("BLOQUER")= "INACTIF",
            int("INACTIF")= "ACTIF.[TYPE]"

            ext("ACTIF.[TYPE]",bloquer)= "BLOQUÉ",

            Modèle atomique DEVS pour le serveur:
            Serveur=<X, S, Y, int, ext, , ta>
            X = "requête",
            Y={"bloquer(client_id)","alerte_panne" si "PANNE"},
            S= {"NORMAL", "SURCHARGE", "PANNE","var"},
            var={'compteur_global','compteur_par_client','seuil_surcharge','seuil_panne'}
            ta= 1s

            ext("compteur_global"et"compteur_par_client[client_id]","requête(client_id)")
            = Incrémenter "compteur_global"et "compteur_par_client[client_id]",
            ext("compteur_par_client[client_id] "> 3)= "bloquer(client_id)",
            ext("compteur_global "> seuil_surcharge)= "SURCHARGE",

            int("NORMAL",)= "SURCHARGE" si 'compteur_global >= seuil_surcharge',
            int("SURCHARGE")= "PANNE" si compteur_global >= seuil_panne
            int("compteur_global" et "compteur_par_client" )= 0 après 1s,

            = "bloquer(client_id)" si 'compteur_par_client[client_id] > 3',

            = "alerte_panne" si "PANNE"


            Modèle couplé DEVS:
            M=<X,Y, {Md}, EIC, EOC, IC, select>
            X =   pas d'entrée globale 
            Y={alerte_panne}
            D={Serveur, Client1​,​Client2,…, Clientn}
            Md={Serveur, Client1​,​Client2,…, Clientn}
            EIC=∅
            EOC={(Serveur, alerte_panne)→(Système, alerte_panne)}
            IC = { (Clienti​, requête)→(Serveur, requête(client_id))| ∀i ∈ {1, ..., n}
            (Serveur, bloquer(client_id))→(Clienti, bloquer) | i = client_id }
            select = {serveur}

            Voici un description du modèle
            Description du modèle
            Le système étudié est basé sur une architecture simple de type client-serveur.
            Les clients sont de deux types :
            Les clients légitimes, qui envoient un nombre limité de requêtes (au maximum une toutes les cinq secondes).
            Les bots, qui génèrent un trafic anormalement élevé (plusieurs requêtes par seconde).
            Le serveur reçoit l’ensemble des requêtes et peut se trouver dans trois états :
            En fonction : il traite normalement les requêtes reçues.
            En surcharge : il reçoit un grand nombre de requêtes en un court laps de temps.
            En panne : il devient indisponible lorsque le seuil de surcharge est dépassé.
            Chaque client possède également trois états possibles :
            Actif : envoi régulier de requêtes.
            Inactif : ne génère pas de trafic pendant une période donnée.
            Bloqué : temporairement suspendu par le serveur pour comportement suspect.

            Stratégie de défense
            Pour prévenir les surcharges, le serveur met en place une règle de blocage temporaire :
            Lorsqu’un client envoie plus de trois requêtes par seconde, il est bloqué pendant dix secondes.
            Après ce délai, il redevient inactif puis actif s’il recommence à envoyer des requêtes.
            Cette stratégie permet de limiter l’impact des bots tout en préservant l’accès des utilisateurs légitimes.

            Les étapes comprennent :
            - La modélisation du fonctionnement d'un serveur recevant des requêtes de clients légitimes et de bots.
            - L'observation de l'évolution des états du serveur (fonctionnel, en surcharge, en panne) en fonction de la fréquence des requêtes reçues.
            - Le test d'une stratégie de blocage temporaire des clients suspects dépassant un seuil de requêtes par seconde.
            - L'évaluation de l'efficacité du mécanisme en termes de disponibilité et de performance du système.
            """;

        String prompt = context +
                "\n\n---\nVoici les données de simulation (CSV) :\n\n" + csvContent +
                "\n\nTâche demandée :\n" +
                "1. Résumer les cas rencontrés pendant la simulation (états du serveur, comportements observés, anomalies).\n" +
                "2. Décrire la stratégie de gestion ou de réaction face à l'attaque.\n" +
                "3. Donner une conclusion sur l'efficacité du mécanisme proposé.\n";

        String escapedPrompt = escapeJson(prompt);
        
        String jsonBody = String.format(
            "{\"model\": \"phi3\", \"prompt\": \"%s\", \"stream\": false}",
            escapedPrompt
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
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder errorMsg = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorMsg.append(line);
                }
                System.err.println("Erreur HTTP " + responseCode + ": " + errorMsg);
            }
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

        System.out.println("Analyse terminée. Résultat enregistré dans : " + resultFile.getAbsolutePath());
        return resultFile;

    } catch (IOException e) {
        System.err.println("Erreur lors de l'analyse : " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

private static String extractResponse(String json) {
    int start = json.indexOf("\"response\":\"");
    if (start == -1) return json;
    start += 12;
    int end = json.indexOf("\"", start);
    if (end == -1) end = json.length();
    return json.substring(start, end)
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\\"", "\"");
}

private static String escapeJson(String text) {
    return text.replace("\\", "\\\\")  // IMPORTANT : d'abord les backslashes
               .replace("\"", "\\\"")
               .replace("\n", "\\n")
               .replace("\r", "\\r")
               .replace("\t", "\\t");
}
}
