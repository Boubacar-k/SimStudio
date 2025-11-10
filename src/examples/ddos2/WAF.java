package examples.ddos2;

import exception.DEVS_Exception;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import model.AtomicModel;
import types.*;

public class WAF extends AtomicModel {

    private static final String ACTIF = "ACTIF";
    private static final String TRAITEMENT = "TRAITEMENT";

    private Map<String, Integer> compteurParIP;
    private Set<String> ipBlacklistees;

    private int seuilRequetesParSeconde;

    private boolean requeteEnAttente;
    private String derniereRequete;
    private int dernierClientId;
    private String derniereIP;
    private String dernierTypeAttaque;
    private int dernierPayloadSize;
    private boolean dernierBloquer;
    private String derniereRaison;

    private FileGenerator fileGen = new FileGenerator();

    public WAF(String name, String desc, int seuilRPS) {
        super(name, desc);

        this.seuilRequetesParSeconde = seuilRPS;
        this.compteurParIP = new HashMap<>();
        this.ipBlacklistees = new HashSet<>();
        this.requeteEnAttente = false;

        String[] s = {ACTIF, TRAITEMENT};
        DEVS_Enum etats = new DEVS_Enum(s);

        // Entrées
        addInput(new DEVS_String(""), "requete", "requête entrante");
        addInput(new DEVS_Integer(0), "client_id", "ID du client");
        addInput(new DEVS_String(""), "ip_address", "IP source");
        addInput(new DEVS_String(""), "type_attaque", "type d'attaque");
        addInput(new DEVS_Integer(0), "payload_size", "taille payload");
        addInput(new DEVS_Integer(0), "compteur", "compteur de requêtes par seconde");

        // Sorties
        addOutput(new DEVS_String(""), "requete_filtree", "requête validée");
        addOutput(new DEVS_Integer(0), "client_id_out", "ID client");
        addOutput(new DEVS_String(""), "ip_out", "IP validée");
        addOutput(new DEVS_String(""), "type_out", "type validé");
        addOutput(new DEVS_Integer(0), "payload_out", "payload validé");
        addOutput(new DEVS_String(""), "alerte_waf", "alerte WAF");

        // Variables d'état
        addStateVariable(etats, "etat", "état du WAF");
        addStateVariable(new DEVS_Integer(0), "requetes_bloquees", "nombre de requêtes bloquées");
        addStateVariable(new DEVS_Integer(0), "requetes_autorisees", "nombre de requêtes autorisées");

        setVar("etat", ACTIF);
        setVar("requetes_bloquees", 0);
        setVar("requetes_autorisees", 0);

        System.out.println("[WAF] Initialisé - Seuil: " + seuilRPS + " req/s");
        fileGen.appendLine("[WAF] Initialisé - Seuil: " + seuilRPS + " req/s");
    }

    @Override
    public void lambda() throws DEVS_Exception {
        if (!requeteEnAttente) {
            return;
        }

        long timestamp = System.currentTimeMillis();

        if (dernierBloquer) {
            String logBlocage = String.format("[%d] [WAF] BLOQUE: %s | Client_%d | Raison: %s | Type: %s",
                    timestamp, derniereIP, dernierClientId, derniereRaison, dernierTypeAttaque);
            System.out.println(logBlocage);
            fileGen.appendLine(logBlocage);
        } else {
            setOutput("requete_filtree", new DEVS_String(derniereRequete));
            setOutput("client_id_out", new DEVS_Integer(dernierClientId));
            setOutput("ip_out", new DEVS_String(derniereIP));
            setOutput("type_out", new DEVS_String(dernierTypeAttaque));
            setOutput("payload_out", new DEVS_Integer(dernierPayloadSize));

            String logAutorise = String.format("[%d] [WAF] AUTORISE: %s | Client_%d | Type: %s | Taille: %d",
                    timestamp, derniereIP, dernierClientId, dernierTypeAttaque, dernierPayloadSize);
            System.out.println(logAutorise);
            fileGen.appendLine(logAutorise);
        }
    }

    @Override
    public void deltaInt() {
        setVar("etat", ACTIF);
        requeteEnAttente = false;
    }

    @Override
    public void deltaExt(int e) throws DEVS_Exception {

        Object reqObj = getInput("requete");
        Object clientIdObj = getInput("client_id");
        Object ipObj = getInput("ip_address");
        Object typeObj = getInput("type_attaque");
        Object payloadObj = getInput("payload_size");
        Object compteurObj = getInput("compteur");

        // Sortir si une entrée manque
        if (reqObj == null || clientIdObj == null || ipObj == null
                || typeObj == null || payloadObj == null || compteurObj == null) {
            return;
        }

        String requete = (String) ((DEVS_String) reqObj).getValue();

        if (requete == null || requete.isEmpty()) {
            return;
        }

        int clientId = (int) ((DEVS_Integer) clientIdObj).getValue();
        String ipAddress = (String) ((DEVS_String) ipObj).getValue();
        String typeAttaque = (String) ((DEVS_String) typeObj).getValue();
        int payloadSize = (int) ((DEVS_Integer) payloadObj).getValue();
        int compteur = (int) ((DEVS_Integer) compteurObj).getValue();
        boolean bloquer = false;
        String raison = "";

        // Vérifier si IP déjà blacklistée
        if (ipBlacklistees.contains(ipAddress)) {
            bloquer = true;
            raison = "IP blacklistée";
        }

        // Vérifier le taux de requêtes par IP
        if (!bloquer) {
            compteurParIP.put(ipAddress, compteur);

            if (compteur > seuilRequetesParSeconde) {
                bloquer = true;
                raison = "Taux de requêtes excessif (" + compteur + " req/s)";
                ipBlacklistees.add(ipAddress);
            }
        }

        // Détection de patterns d'attaque
        if (!bloquer) {
            if (typeAttaque.contains("HTTP_FLOOD") && payloadSize > 500) {
                bloquer = true;
                raison = "Pattern HTTP Flood détecté";
            } else if (typeAttaque.contains("SLOWLORIS")) {
                bloquer = true;
                raison = "Pattern Slowloris détecté";
            } else if (payloadSize > 50000) {
                bloquer = true;
                raison = "Payload anormalement large (" + payloadSize + " bytes)";
            }
        }

        // Stocker la décision pour lambda()
        derniereRequete = requete;
        dernierClientId = clientId;
        derniereIP = ipAddress;
        dernierTypeAttaque = typeAttaque;
        dernierPayloadSize = payloadSize;
        dernierBloquer = bloquer;
        derniereRaison = raison;
        requeteEnAttente = true;

        setVar("etat", TRAITEMENT);

        if (bloquer) {
            int bloquees = (int) getVar("requetes_bloquees").getValue();
            setVar("requetes_bloquees", bloquees + 1);
        } else {
            int autorisees = (int) getVar("requetes_autorisees").getValue();
            setVar("requetes_autorisees", autorisees + 1);
        }
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
