package examples.ddos2;

import exception.DEVS_Exception;
import java.util.HashSet;
import java.util.Set;
import model.AtomicModel;
import types.*;

public class Firewall extends AtomicModel {

    private static final String ACTIF = "ACTIF";
    private static final String TRAITEMENT = "TRAITEMENT";

    private Set<String> ipBlanches;
    private Set<String> ipNoires;
    private Set<String> portsAutorises;

    // Buffer pour la requête en cours de traitement
    private boolean requeteEnAttente;
    private String derniereRequete;
    private int dernierClientId;
    private String derniereIP;
    private String dernierTypeAttaque;
    private int dernierPayloadSize;
    private boolean dernierBloquer;
    private String derniereRaison;

    private FileGenerator fileGen = new FileGenerator();

    public Firewall(String name, String desc) {
        super(name, desc);

        this.ipBlanches = new HashSet<>();
        this.ipNoires = new HashSet<>();
        this.portsAutorises = new HashSet<>();
        this.requeteEnAttente = false;

        initialiserReglesParDefaut();

        String[] s = {ACTIF, TRAITEMENT};
        DEVS_Enum etats = new DEVS_Enum(s);

        // Entrées
        addInput(new DEVS_String(""), "requete_filtree", "requête du WAF");
        addInput(new DEVS_Integer(0), "client_id_out", "ID client");
        addInput(new DEVS_String(""), "ip_out", "IP source");
        addInput(new DEVS_String(""), "type_out", "type d'attaque");
        addInput(new DEVS_Integer(0), "payload_out", "taille payload");

        // Sorties
        addOutput(new DEVS_String(""), "requete_autorisee", "requête vers IDS");
        addOutput(new DEVS_Integer(0), "client_id_out", "ID client");
        addOutput(new DEVS_String(""), "ip_out", "IP");
        addOutput(new DEVS_String(""), "type_out", "type");
        addOutput(new DEVS_Integer(0), "payload_out", "payload");
        addOutput(new DEVS_String(""), "alerte_firewall", "alerte firewall");

        // Variables d'état
        addStateVariable(etats, "etat", "état du firewall");
        addStateVariable(new DEVS_Integer(0), "paquets_bloques", "paquets bloqués");
        addStateVariable(new DEVS_Integer(0), "paquets_autorises", "paquets autorisés");

        setVar("etat", ACTIF);
        setVar("paquets_bloques", 0);
        setVar("paquets_autorises", 0);

        System.out.println("[FIREWALL] Initialisé avec règles par défaut");
        fileGen.appendLine("[FIREWALL] Initialisé avec règles par défaut");
    }

    private void initialiserReglesParDefaut() {
        ipBlanches.add("192.168.");
        ipBlanches.add("10.0.");

        portsAutorises.add("80");
        portsAutorises.add("443");
        portsAutorises.add("53");
        portsAutorises.add("22");
    }

    private boolean estIPSuspecte(String ip) {
        String[] plagesSuspectes = {
            "185.220.", "45.142.", "91.219.", "162.247.", "195.123.",
            "103.251.", "104.168.", "107.189."
        };

        for (String plage : plagesSuspectes) {
            if (ip.startsWith(plage)) {
                return true;
            }
        }
        return false;
    }

    private boolean verifierReglesProtocole(String typeAttaque) {
        return typeAttaque.contains("SYN_FLOOD")
                || typeAttaque.contains("ACK_FLOOD")
                || typeAttaque.contains("UDP_FLOOD")
                || typeAttaque.contains("ICMP_FLOOD");
    }

    @Override
    public void lambda() throws DEVS_Exception {
        if (!requeteEnAttente) {
            return;
        }

        long timestamp = System.currentTimeMillis();

        if (dernierBloquer) {
            // setOutput("alerte_firewall", new DEVS_String("DROP:" + derniereRaison));

            String logBlocage = String.format("[%d] [PARE-FEU] REFUSE: %s | Client_%d | Raison: %s",
                    timestamp, derniereIP, dernierClientId, derniereRaison);
            System.out.println(logBlocage);
            fileGen.appendLine(logBlocage);
        } else {
            // Transmettre au IDS/IPS
            setOutput("requete_autorisee", new DEVS_String(derniereRequete));
            setOutput("client_id_out", new DEVS_Integer(dernierClientId));
            setOutput("ip_out", new DEVS_String(derniereIP));
            setOutput("type_out", new DEVS_String(dernierTypeAttaque));
            setOutput("payload_out", new DEVS_Integer(dernierPayloadSize));

            String logAutorise = String.format("[%d] [PARE-FEU] ACCEPTE: %s | Client_%d | Type: %s",
                    timestamp, derniereIP, dernierClientId, dernierTypeAttaque);
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
        Object reqObj = getInput("requete_filtree");
        Object clientIdObj = getInput("client_id_out");
        Object ipObj = getInput("ip_out");
        Object typeObj = getInput("type_out");
        Object payloadObj = getInput("payload_out");

        if (reqObj == null || clientIdObj == null || ipObj == null
                || typeObj == null || payloadObj == null) {
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

        boolean bloquer = false;
        String raison = "";

        if (ipNoires.contains(ipAddress)) {
            bloquer = true;
            raison = "IP dans blacklist";
        }

        if (!bloquer && estIPSuspecte(ipAddress)) {
            bloquer = true;
            raison = "IP provenant d'une plage suspecte";
            ipNoires.add(ipAddress);
        }

        if (!bloquer && verifierReglesProtocole(typeAttaque)) {
            bloquer = true;
            raison = "Attaque protocole détectée: " + typeAttaque;
        }

        if (!bloquer && typeAttaque.contains("VOLUMETRIC") && payloadSize > 10000) {
            bloquer = true;
            raison = "Attaque volumétrique détectée";
        }

        // Stocker pour lambda()
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
            int bloques = (int) getVar("paquets_bloques").getValue();
            setVar("paquets_bloques", bloques + 1);
        } else {
            int autorises = (int) getVar("paquets_autorises").getValue();
            setVar("paquets_autorises", autorises + 1);
        }
    }

    @Override
    public int ta() {
        String etatActuel = (String) getVar("etat").getValue();
        if (etatActuel.equals(TRAITEMENT)) {
            return 0;
        }
        return DEVS_Integer.POSITIVE_INTINITY;
    }

    public void ajouterIPBlacklist(String ip) {
        ipNoires.add(ip);
    }

    public void ajouterIPWhitelist(String ip) {
        ipBlanches.add(ip);
    }
}
