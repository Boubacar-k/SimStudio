package examples.ddos2;

import exception.DEVS_Exception;
import java.util.Random;
import model.*;
import types.*;

public class Client extends AtomicModel {

    private static final String ACTIF_LEGITIME = "ACTIF.LÉGITIME";
    private static final String ACTIF_BOT = "ACTIF.BOT";
    private static final String INACTIF = "INACTIF";
    private static final String BLOQUE = "BLOQUÉ";

    private String typeClient;
    private int clientId;
    private String ipAddress;
    private String typeAttaque;
    private int taillePayload;
    private int compteurEnvois = 0;

    private FileGenerator fileGen = new FileGenerator();
    private Random random = new Random();

    // Types d'attaques DDoS
    public enum TypeAttaqueDDoS {
        VOLUMETRIC_UDP_FLOOD,
        VOLUMETRIC_ICMP_FLOOD,
        SYN_FLOOD,
        ACK_FLOOD,
        HTTP_FLOOD,
        SLOWLORIS,
        DNS_AMPLIFICATION,
        NTP_AMPLIFICATION,
        LEGITIME
    }

    public Client(String name, String desc, int id, boolean isBot, TypeAttaqueDDoS typeAttaqueDDoS) {
        super(name, desc);
        this.clientId = id;
        this.typeClient = isBot ? "BOT" : "LÉGITIME";
        this.ipAddress = genererIP(isBot);
        this.typeAttaque = typeAttaqueDDoS.name();
        this.taillePayload = calculerTaillePayload(typeAttaqueDDoS);

        String[] s = {ACTIF_LEGITIME, ACTIF_BOT, INACTIF, BLOQUE};
        DEVS_Enum etats = new DEVS_Enum(s);

        // Entrées
        String[] x = {"bloquer"};
        DEVS_Enum ordres = new DEVS_Enum(x);
        addInput(ordres, "ordre", "blocage du client");

        // Sorties
        addOutput(new DEVS_String("requête"), "requete", "requete client");
        addOutput(new DEVS_Integer(clientId), "client_id", "identifiant du client");
        addOutput(new DEVS_String(ipAddress), "ip_address", "adresse IP du client");
        addOutput(new DEVS_String(typeAttaque), "type_attaque", "type d'attaque");
        addOutput(new DEVS_Integer(taillePayload), "payload_size", "taille du payload");
        addOutput(new DEVS_Integer(compteurEnvois), "compteur", "Compteur d'envois");

        // Variables d'état
        addStateVariable(etats, "etat", "Etat du client");
        addStateVariable(new DEVS_String(typeClient), "type", "bot ou legitime");
        addStateVariable(new DEVS_String(ipAddress), "ip", "adresse IP");
        addStateVariable(new DEVS_String(typeAttaque), "attaque", "type d'attaque");

        // Initialisation
        if (isBot) {
            setVar("etat", ACTIF_BOT);
        } else {
            setVar("etat", ACTIF_LEGITIME);
        }
    }

    private String genererIP(boolean isBot) {
        if (isBot) {
            String[] plagesSuspectes = {
                "185.220.", "45.142.", "91.219.", "162.247.", "195.123."
            };
            String plage = plagesSuspectes[random.nextInt(plagesSuspectes.length)];
            return plage + random.nextInt(256) + "." + random.nextInt(256);
        } else {
            return "192.168." + random.nextInt(256) + "." + (1 + random.nextInt(254));
        }
    }

    private int calculerTaillePayload(TypeAttaqueDDoS type) {
        switch (type) {
            case VOLUMETRIC_UDP_FLOOD:
            case VOLUMETRIC_ICMP_FLOOD:
                return 1024 + random.nextInt(64000); // 1KB à 64KB
            case SYN_FLOOD:
            case ACK_FLOOD:
                return 60 + random.nextInt(40); // 60-100 bytes
            case HTTP_FLOOD:
                return 200 + random.nextInt(800); // 200B-1KB
            case SLOWLORIS:
                return 1 + random.nextInt(10); // Très petit
            case DNS_AMPLIFICATION:
            case NTP_AMPLIFICATION:
                return 512 + random.nextInt(4096); // Amplification
            case LEGITIME:
                return 100 + random.nextInt(1500); // Trafic normal
            default:
                return 512;
        }
    }

    @Override
    public void lambda() throws DEVS_Exception {
        String etatActuel = (String) getVar("etat").getValue();

        if (etatActuel.equals(ACTIF_LEGITIME) || etatActuel.equals(ACTIF_BOT)) {
            // Recalculer la taille pour varier
            this.taillePayload = calculerTaillePayload(TypeAttaqueDDoS.valueOf(this.typeAttaque));

            setOutput("requete", new DEVS_String("requête"));
            setOutput("client_id", new DEVS_Integer(clientId));
            setOutput("ip_address", new DEVS_String(ipAddress));
            setOutput("type_attaque", new DEVS_String(typeAttaque));
            setOutput("payload_size", new DEVS_Integer(taillePayload));
            setOutput("compteur", new DEVS_Integer(++compteurEnvois));

            // Log format réaliste
            long timestamp = System.currentTimeMillis();
            String logEntry = String.format("[%d] %s:%d -> CLIENT | Type: %s | Taille: %d bytes | Client: %s_%d",
                    timestamp, ipAddress,
                    typeAttaque.contains("HTTP") ? 80 : (typeAttaque.contains("DNS") ? 53 : 443),
                    typeAttaque, taillePayload, typeClient, clientId);

            System.out.println(logEntry);
            fileGen.appendLine(logEntry);
        }
    }

    @Override
    public void deltaInt() {
        String etatActuel = (String) getVar("etat").getValue();

        switch (etatActuel) {
            case ACTIF_LEGITIME:
            case ACTIF_BOT:
                break;

            case BLOQUE:
                setVar("etat", INACTIF);
                String logDeblocage = String.format("[%d] IP %s (Client_%d) débloqué -> INACTIF",
                        System.currentTimeMillis(), ipAddress, clientId);
                System.out.println(logDeblocage);
                fileGen.appendLine(logDeblocage);
                break;

            case INACTIF:
                setVar("etat", INACTIF);
                break;
        }
    }

    @Override
    public void deltaExt(int e) throws DEVS_Exception {
        String etatActuel = (String) getVar("etat").getValue();

        String ordreRecu = getInput("ordre").toString();
        if (ordreRecu != null) {
            String ordre = ordreRecu;

            if (ordre.equals("bloquer")) {
                if (etatActuel.equals(ACTIF_LEGITIME) || etatActuel.equals(ACTIF_BOT)) {
                    setVar("etat", BLOQUE);
                    String logBlocage = String.format("[%d] ALERTE SECURITE: IP %s (Client_%d) BLOQUE | Raison: Seuil dépassé",
                            System.currentTimeMillis(), ipAddress, clientId);
                    System.out.println(logBlocage);
                    fileGen.appendLine(logBlocage);
                }
            }
        }
    }

    @Override
    public int ta() {
        String etatActuel = (String) getVar("etat").getValue();

        switch (etatActuel) {
            case ACTIF_LEGITIME:
                return 10;

            case ACTIF_BOT:
                if (typeAttaque.contains("FLOOD")) {
                    return 2;
                } else if (typeAttaque.contains("SLOWLORIS")) {
                    return 30; // Envoie lentement
                }
                return 5;

            case BLOQUE:
                return 30;

            case INACTIF:
                return DEVS_Integer.POSITIVE_INTINITY;

            default:
                return DEVS_Integer.POSITIVE_INTINITY;
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getTypeAttaque() {
        return typeAttaque;
    }
}
