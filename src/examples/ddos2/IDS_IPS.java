package examples.ddos2;

import exception.DEVS_Exception;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.AtomicModel;
import types.*;

public class IDS_IPS extends AtomicModel {

    private static final String NORMAL = "NORMAL";
    private static final String TRAITEMENT = "TRAITEMENT";

    private Map<String, List<Long>> historiqueTraficIP;
    private Map<String, Double> scoreAnomalie;

    private double seuilAnomalie;
    private int tailleHistorique;

    // Buffer pour la requête en cours
    private boolean requeteEnAttente;
    private String derniereRequete;
    private int dernierClientId;
    private String derniereIP;
    private String dernierTypeAttaque;
    private int dernierPayloadSize;
    private double dernierScore;
    private String derniereSignature;
    private boolean dernierBloquer;
    private boolean dernierSuspect;

    private FileGenerator fileGen = new FileGenerator();

    public IDS_IPS(String name, String desc, double seuilAnomalie) {
        super(name, desc);

        this.seuilAnomalie = seuilAnomalie;
        this.tailleHistorique = 10;
        this.historiqueTraficIP = new HashMap<>();
        this.scoreAnomalie = new HashMap<>();
        this.requeteEnAttente = false;

        String[] s = {NORMAL, TRAITEMENT};
        DEVS_Enum etats = new DEVS_Enum(s);

        // Entrées
        addInput(new DEVS_String(""), "requete", "requête du firewall");
        addInput(new DEVS_Integer(0), "client_id", "ID client");
        addInput(new DEVS_String(""), "ip_address", "IP source");
        addInput(new DEVS_String(""), "type_attaque", "type d'attaque");
        addInput(new DEVS_Integer(0), "payload_size", "taille payload");

        // Sorties
        addOutput(new DEVS_String(""), "requete_validee", "requête vers serveur");
        addOutput(new DEVS_Integer(0), "client_id_out", "ID client");
        addOutput(new DEVS_String(""), "alerte_ids", "alerte IDS");

        // Variables d'état
        addStateVariable(etats, "etat", "état IDS/IPS");
        addStateVariable(new DEVS_Integer(0), "menaces_detectees", "menaces détectées");
        addStateVariable(new DEVS_Integer(0), "menaces_prevenues", "menaces prévenues");
        addStateVariable(new DEVS_Integer(0), "trafic_legitime", "trafic légitime");

        setVar("etat", NORMAL);
        setVar("menaces_detectees", 0);
        setVar("menaces_prevenues", 0);
        setVar("trafic_legitime", 0);

        System.out.println("[IDS/IPS] Initialisé - Seuil anomalie: " + seuilAnomalie);
        fileGen.appendLine("[IDS/IPS] Initialisé - Seuil anomalie: " + seuilAnomalie);
    }

    private double calculerScoreAnomalie(String ipAddress, String typeAttaque, int payloadSize, long timestamp) {
        double score = 0.0;

        List<Long> historique = historiqueTraficIP.getOrDefault(ipAddress, new ArrayList<>());
        historique.add(timestamp);

        if (historique.size() > tailleHistorique) {
            historique.remove(0);
        }
        historiqueTraficIP.put(ipAddress, historique);

        if (historique.size() >= 2) {
            long tempsEcoule = timestamp - historique.get(0);
            double frequence = (historique.size() * 1000.0) / Math.max(tempsEcoule, 1);

            if (frequence > 5) {
                score += 30;
            } else if (frequence > 2) {
                score += 10;
            }
        }

        if (typeAttaque.contains("FLOOD")) {
            score += 40;
        } else if (typeAttaque.contains("AMPLIFICATION")) {
            score += 50;
        } else if (typeAttaque.contains("SLOWLORIS")) {
            score += 35;
        }

        if (payloadSize > 10000) {
            score += 20;
        } else if (payloadSize < 10 && typeAttaque.contains("SLOWLORIS")) {
            score += 25;
        }

        if (!ipAddress.startsWith("192.168.") && !ipAddress.startsWith("10.")) {
            score += 15;
        }

        return score;
    }

    private String determinerSignature(String typeAttaque, int payloadSize, double scoreAnomalie) {
        if (typeAttaque.contains("SYN_FLOOD")) {
            return "SIG-001: SYN Flood Attack Pattern";
        } else if (typeAttaque.contains("HTTP_FLOOD")) {
            return "SIG-002: HTTP Flood Attack Pattern";
        } else if (typeAttaque.contains("SLOWLORIS")) {
            return "SIG-003: Slowloris Attack Pattern";
        } else if (typeAttaque.contains("AMPLIFICATION")) {
            return "SIG-004: DNS/NTP Amplification Attack";
        } else if (typeAttaque.contains("UDP_FLOOD")) {
            return "SIG-005: UDP Flood Attack";
        } else if (scoreAnomalie > 70) {
            return "SIG-999: Unknown High-Severity Threat";
        } else if (scoreAnomalie > 50) {
            return "SIG-998: Suspicious Activity Detected";
        }
        return "No Signature Match";
    }

    @Override
    public void lambda() throws DEVS_Exception {
        if (!requeteEnAttente) {
            return;
        }

        long timestamp = System.currentTimeMillis();

        if (dernierBloquer) {
            // MENACE BLOQUÉE
            // setOutput("alerte_ids", new DEVS_String("THREAT:" + derniereSignature));

            String logMenace = String.format(
                    "[%d] [IDS/IPS] MENACE DETECTE & BLOQUE: %s | Client_%d | Score: %.1f | Signature: %s | Type: %s | Payload: %d bytes",
                    timestamp, derniereIP, dernierClientId, dernierScore, derniereSignature, dernierTypeAttaque, dernierPayloadSize);
            System.out.println(logMenace);
            fileGen.appendLine(logMenace);

        } else if (dernierSuspect) {
            // ACTIVITÉ SUSPECTE mais pas bloquée
            // setOutput("alerte_ids", new DEVS_String("SUSPICIOUS:" + derniereSignature));
            setOutput("requete_validee", new DEVS_String(derniereRequete));
            setOutput("client_id_out", new DEVS_Integer(dernierClientId));

            String logSuspect = String.format(
                    "[%d] [IDS] ACTIVITE SUSPECTE : %s | Client_%d | Score: %.1f | Signature: %s | Type: %s",
                    timestamp, derniereIP, dernierClientId, dernierScore, derniereSignature, dernierTypeAttaque);
            System.out.println(logSuspect);
            fileGen.appendLine(logSuspect);

        } else {
            // TRAFIC LÉGITIME
            setOutput("requete_validee", new DEVS_String(derniereRequete));
            setOutput("client_id_out", new DEVS_Integer(dernierClientId));

            String logLegit = String.format(
                    "[%d] [IDS] TRAFFIC LEGITIME: %s | Client_%d | Score: %.1f | Type: %s",
                    timestamp, derniereIP, dernierClientId, dernierScore, dernierTypeAttaque);
            System.out.println(logLegit);
            fileGen.appendLine(logLegit);
        }
    }

    @Override
    public void deltaInt() {
        setVar("etat", NORMAL);
        requeteEnAttente = false;
    }

    @Override
    public void deltaExt(int e) throws DEVS_Exception {
        String requete = (String) ((DEVS_String) getInput("requete")).getValue();

        if (requete == null || requete.isEmpty()) {
            return;
        }

        int clientId = (int) ((DEVS_Integer) getInput("client_id")).getValue();
        String ipAddress = (String) ((DEVS_String) getInput("ip_address")).getValue();
        String typeAttaque = (String) ((DEVS_String) getInput("type_attaque")).getValue();
        int payloadSize = (int) ((DEVS_Integer) getInput("payload_size")).getValue();

        long timestamp = System.currentTimeMillis();

        double score = calculerScoreAnomalie(ipAddress, typeAttaque, payloadSize, timestamp);
        scoreAnomalie.put(ipAddress, score);

        String signature = determinerSignature(typeAttaque, payloadSize, score);

        boolean bloquer = false;
        boolean suspect = false;

        if (score >= seuilAnomalie) {
            bloquer = true;
            int menacesDetectees = (int) getVar("menaces_detectees").getValue();
            int menacesPrevenues = (int) getVar("menaces_prevenues").getValue();
            setVar("menaces_detectees", menacesDetectees + 1);
            setVar("menaces_prevenues", menacesPrevenues + 1);

        } else if (score >= seuilAnomalie * 0.6) {
            suspect = true;
            int menacesDetectees = (int) getVar("menaces_detectees").getValue();
            setVar("menaces_detectees", menacesDetectees + 1);

        } else {
            int legitime = (int) getVar("trafic_legitime").getValue();
            setVar("trafic_legitime", legitime + 1);
        }

        // Stocker pour lambda()
        derniereRequete = requete;
        dernierClientId = clientId;
        derniereIP = ipAddress;
        dernierTypeAttaque = typeAttaque;
        dernierPayloadSize = payloadSize;
        dernierScore = score;
        derniereSignature = signature;
        dernierBloquer = bloquer;
        dernierSuspect = suspect;
        requeteEnAttente = true;

        setVar("etat", TRAITEMENT);
    }

    @Override
    public int ta() {
        String etatActuel = (String) getVar("etat").getValue();
        if (etatActuel.equals(TRAITEMENT)) {
            return 1;
        }
        return DEVS_Integer.POSITIVE_INTINITY;
    }
}
