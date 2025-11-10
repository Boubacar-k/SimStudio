package examples.ddos2;

import exception.DEVS_Exception;
import java.util.HashMap;
import java.util.Map;
import model.AtomicModel;
import types.*;

public class Serveur extends AtomicModel {

    private static final String NORMAL = "NORMAL";
    private static final String SURCHARGE = "SURCHARGE";
    private static final String PANNE = "PANNE";
    private static final String REPORTING = "REPORTING";

    private int seuilSurcharge;
    private int seuilPanne;
    private int seuilBlocage;

    // Statistiques
    private Map<Integer, Integer> compteurParClient;
    private Map<Integer, String> ipParClient;
    private Map<String, Integer> compteurParIP;
    private int compteurRequetesTotal;
    private int compteurRequetesTraitees;
    private int compteurRequetesRejetees;

    // Analyse temporelle
    private long dernierReset;
    private long tempsEnPanne;
    private long tempsEnSurcharge;

    // Buffer pour les sorties
    private boolean doitBloquer;
    private int clientABloquer;
    private boolean doitAlerter;
    private String messageAlerte;
    private int compteurGlobalCourant;

    private FileGenerator fileGen = new FileGenerator();

    public Serveur(String name, String desc, int seuilSurcharge, int seuilPanne) {
        super(name, desc);

        this.seuilSurcharge = seuilSurcharge;
        this.seuilPanne = seuilPanne;
        this.seuilBlocage = 3;
        this.compteurParClient = new HashMap<>();
        this.ipParClient = new HashMap<>();
        this.compteurParIP = new HashMap<>();
        this.compteurRequetesTotal = 0;
        this.compteurRequetesTraitees = 0;
        this.compteurRequetesRejetees = 0;
        this.dernierReset = System.currentTimeMillis();
        this.tempsEnPanne = 0;
        this.tempsEnSurcharge = 0;
        this.doitBloquer = false;
        this.doitAlerter = false;
        this.compteurGlobalCourant = 0;

        String[] s = {NORMAL, SURCHARGE, PANNE, REPORTING};
        DEVS_Enum etats = new DEVS_Enum(s);

        // Entrées
        addInput(new DEVS_String(""), "requete", "requête validée");
        addInput(new DEVS_Integer(0), "client_id", "ID du client");

        // Sorties
        addOutput(new DEVS_String(""), "bloquer", "ordre de blocage");
        addOutput(new DEVS_Integer(0), "client_id_bloque", "ID client à bloquer");
        addOutput(new DEVS_String(""), "alerte_panne", "alerte de panne");

        // Variables d'état
        addStateVariable(etats, "etat", "état du serveur");
        addStateVariable(new DEVS_Integer(0), "compteur_global", "requêtes/seconde");
        addStateVariable(new DEVS_Integer(seuilSurcharge), "seuil_surcharge", "");
        addStateVariable(new DEVS_Integer(seuilPanne), "seuil_panne", "");
        addStateVariable(new DEVS_Integer(seuilBlocage), "seuil_blocage", "");

        ((DEVS_Enum) getVar("etat")).setValue(NORMAL);
        ((DEVS_Integer) getVar("compteur_global")).setValue(0);

        String initialisation = String.format(
                "-----------------------------------------------------\n"
                + " [SERVEUR] Initialisé \n"
                + "-----------------------------------------------------\n"
                + " Seuil surcharge:      %4d req/s                   \n"
                + " Seuil panne:          %4d req/s                   \n"
                + " Seuil blocage client: %4d req/s                   \n"
                + "-----------------------------------------------------\n",
                seuilSurcharge, seuilPanne, seuilBlocage
        );

        System.out.println(initialisation);
        fileGen.appendLine(initialisation);
    }

    @Override
    public void lambda() throws DEVS_Exception {
        // Récupère proprement l'état courant
        DEVS_Enum etatVar = (DEVS_Enum) getVar("etat");
        String etatActuel = (String) etatVar.getValue();
        long timestamp = System.currentTimeMillis();

        if (REPORTING.equals(etatActuel)) {
            // Émettre les blocages si nécessaire
            if (doitBloquer) {
                setOutput("bloquer", new DEVS_String("bloquer"));
                setOutput("client_id_bloque", new DEVS_Integer(clientABloquer));
            }

            // Émettre les alertes si nécessaire
            if (doitAlerter) {
                setOutput("alerte_panne", new DEVS_String(messageAlerte));
            }

            // Log de l'état actuel
            String etatReel = determinerEtatReel();
            String logStatus = String.format(
                    "[%d] [SERVEUR] Status: %s | Load: %d req/s | Active Clients: %d | Processed: %d | Rejected: %d",
                    timestamp, etatReel, compteurGlobalCourant, ipParClient.size(),
                    compteurRequetesTraitees, compteurRequetesRejetees
            );
            System.out.println(logStatus);
            fileGen.appendLine(logStatus);
        }
    }

    private String determinerEtatReel() {
        if (compteurGlobalCourant >= seuilPanne) {
            return "PANNE";
        } else if (compteurGlobalCourant >= seuilSurcharge) {
            return "SURCHARGE";
        } else {
            return "NORMAL";
        }
    }

    @Override
    public void deltaInt() {
        DEVS_Enum etatVar = (DEVS_Enum) getVar("etat");
        String etatActuel = (String) etatVar.getValue();

        if (REPORTING.equals(etatActuel)) {
            // Transition based on current load
            DEVS_Integer compteurGlobalVar = (DEVS_Integer) getVar("compteur_global");
            int compteurGlobal = (int) compteurGlobalVar.getValue();

            String nouvelEtat = NORMAL;
            if (compteurGlobal >= seuilPanne) {
                nouvelEtat = PANNE;
            } else if (compteurGlobal >= seuilSurcharge) {
                nouvelEtat = SURCHARGE;
            }

            etatVar.setValue(nouvelEtat);

            // Reset counters
            compteurGlobalVar.setValue(0);
            compteurParClient.clear();
            compteurParIP.clear();
            dernierReset = System.currentTimeMillis();

            // Reset output flags
            doitBloquer = false;
            doitAlerter = false;
            compteurGlobalCourant = 0;
        }
    }

    @Override
    public void deltaExt(int e) throws DEVS_Exception {
        // Récupérer l'état proprement
        DEVS_Enum etatVar = (DEVS_Enum) getVar("etat");
        String etatActuel = (String) etatVar.getValue();
        long timestamp = System.currentTimeMillis();

        // Sécuriser l'accès aux entrées (vérifier null)
        Object inReqObj = getInput("requete");
        if (inReqObj == null) {
            // pas d'entrée valide, rien à faire
            return;
        }
        String requete = (String) ((DEVS_String) inReqObj).getValue();
        if (requete == null || requete.isEmpty()) {
            return;
        }

        Object inClientObj = getInput("client_id");
        int clientId = 0;
        if (inClientObj != null) {
            clientId = (int) ((DEVS_Integer) inClientObj).getValue();
        }

        // Si le serveur est en panne, rejeter
        if (PANNE.equals(etatActuel)) {
            String logRejet = String.format(
                    "[%d] [SERVEUR] Request DROPPED: Server is DOWN",
                    timestamp
            );
            System.out.println(logRejet);
            fileGen.appendLine(logRejet);

            compteurRequetesTotal++;
            compteurRequetesRejetees++;
            return;
        }

        // Incrémenter les compteurs
        compteurRequetesTotal++;

        DEVS_Integer compteurGlobalVar = (DEVS_Integer) getVar("compteur_global");
        int compteurGlobal = (int) compteurGlobalVar.getValue();
        compteurGlobal++;
        compteurGlobalVar.setValue(compteurGlobal);
        compteurGlobalCourant = compteurGlobal;

        // Compteur par client
        int compteurClient = compteurParClient.getOrDefault(clientId, 0);
        compteurClient++;
        compteurParClient.put(clientId, compteurClient);

        // Enregistrer l'IP du client
        if (!ipParClient.containsKey(clientId)) {
            ipParClient.put(clientId, "IP_" + clientId);
        }

        // Compteur par IP
        String ip = ipParClient.get(clientId);
        int compteurIP = compteurParIP.getOrDefault(ip, 0);
        compteurIP++;
        compteurParIP.put(ip, compteurIP);

        // Requête traitée
        compteurRequetesTraitees++;

        // Log de la requête
        String logRequete = String.format(
                "[%d] [SERVEUR] Request ACCEPTED: Client_%d | Current Load: %d req/s | Client Rate: %d req/s",
                timestamp, clientId, compteurGlobal, compteurClient
        );
        System.out.println(logRequete);
        fileGen.appendLine(logRequete);

        // Vérifier si un client doit être bloqué
        if (compteurClient > seuilBlocage) {
            doitBloquer = true;
            clientABloquer = clientId;

            String logBlocage = String.format(
                    "[%d] [SERVEUR] Preparing to BLOCK Client_%d (Rate: %d > %d req/s)",
                    timestamp, clientId, compteurClient, seuilBlocage
            );
            System.out.println(logBlocage);
            fileGen.appendLine(logBlocage);
        }

        // Vérifier si on doit alerter
        if (compteurGlobal >= seuilPanne) {
            doitAlerter = true;
            messageAlerte = "CRITICAL: SERVER DOWN";
        }

        // Passer en état REPORTING pour émettre les sorties — utilise l'objet etatVar
        etatVar.setValue(REPORTING);
    }

    @Override
    public int ta() {
        DEVS_Enum etatVar = (DEVS_Enum) getVar("etat");
        String etatActuel = (String) etatVar.getValue();

        switch (etatActuel) {
            case NORMAL:
            case SURCHARGE:
            case PANNE:
                return Integer.MAX_VALUE; // Wait for external events

            case REPORTING:
                return 0; // Immediate internal transition

            default:
                return Integer.MAX_VALUE;
        }
    }

    public void genererRapport() {
        long timestamp = System.currentTimeMillis();

        double tauxReussite = compteurRequetesTotal > 0
                ? (compteurRequetesTraitees * 100.0 / compteurRequetesTotal) : 100.0;

        String rapport = String.format(
                "\n-------------------------------------------------------------\n"
                + "|              RAPPORT FINAL DU SERVEUR                      |\n"
                + "-------------------------------------------------------------\n"
                + "| État final:           %-33s   |\n"
                + "| Requêtes totales:     %-33d   |\n"
                + "| Requêtes traitées:    %-33d   |\n"
                + "| Requêtes rejetées:    %-33d   |\n"
                + "| Taux de réussite:     %-32.1f%%  |\n"
                + "| Temps en surcharge:   %-30d ms   |\n"
                + "| Temps en panne:       %-30d ms   |\n"
                + "| Clients uniques:      %-33d   |\n"
                + "-------------------------------------------------------------\n",
                getVar("etat").getValue(),
                compteurRequetesTotal,
                compteurRequetesTraitees,
                compteurRequetesRejetees,
                tauxReussite,
                tempsEnSurcharge,
                tempsEnPanne,
                ipParClient.size()
        );

        System.out.println(rapport);
        fileGen.appendLine(rapport);
    }
}
