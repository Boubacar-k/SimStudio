package examples.ddos2;

import java.util.ArrayList;
import java.util.List;
import model.AtomicModel;
import model.CoupledModel;
import model.Model;
import types.DEVS_String;

public class SystemeDDoS extends CoupledModel {

    private WAF waf;
    private Firewall firewall;
    private IDS_IPS idsIps;
    private Serveur serveur;
    private List<Client> clients;

    private FileGenerator fileGen = new FileGenerator();

    public SystemeDDoS(String name, String desc,
            int nbClientsLegitimes,
            int nbBots,
            Client.TypeAttaqueDDoS typeAttaque,
            int seuilSurcharge,
            int seuilPanne) {
        super(name, desc);

        this.clients = new ArrayList<>();

        // Créer les composants de sécurité (ordre: WAF -> Firewall -> IDS/IPS -> Serveur)
        waf = new WAF("WAF", "Web Application Firewall", 5);
        addSubModel(waf);

        firewall = new Firewall("Firewall", "Network Firewall");
        addSubModel(firewall);

        idsIps = new IDS_IPS("IDS_IPS", "Intrusion Detection/Prevention System", 50.0);
        addSubModel(idsIps);

        serveur = new Serveur("Serveur", "Application Server", seuilSurcharge, seuilPanne);
        addSubModel(serveur);

        int clientId = 1;

        // Créer clients légitimes
        for (int i = 0; i < nbClientsLegitimes; i++) {
            Client client = new Client(
                    "Client_Legitime_" + clientId,
                    "Client legitime Numero " + clientId,
                    clientId,
                    false,
                    Client.TypeAttaqueDDoS.LEGITIME
            );
            clients.add(client);
            addSubModel(client);
            clientId++;
        }

        // Créer bots avec le type d'attaque spécifié
        for (int i = 0; i < nbBots; i++) {
            Client bot = new Client(
                    "Bot_" + clientId,
                    "Bot numero " + clientId,
                    clientId,
                    true,
                    typeAttaque
            );
            clients.add(bot);
            addSubModel(bot);
            clientId++;
        }

        addOutput(new DEVS_String(), "alerte_systeme", "Alertes système globales");

        creerLiaisons();

        String resume = String.format(
                "\n-----------------------------------------------------\n"
                + "        SYSTÈME DDoS INITIALISÉ                    \n"
                + "-----------------------------------------------------\n"
                + " Clients légitimes:    %-3d                       \n"
                + " Bots malveillants:    %-3d                       \n"
                + " Type d'attaque:       %-24s\n"
                + " Total clients:        %-3d                       \n"
                + "-----------------------------------------------------\n"
                + " Architecture de sécurité:                         \n"
                + "  1. WAF (Web Application Firewall)                \n"
                + "  2. Firewall réseau                               \n"
                + "  3. IDS/IPS                                       \n"
                + "  4. Serveur applicatif                            \n"
                + "----------------------------------------------------\n",
                nbClientsLegitimes, nbBots, typeAttaque.name(), clients.size()
        );

        System.out.println(resume);
        fileGen.appendLine(resume);
    }

    private void creerLiaisons() {
        // Flux: Client -> WAF -> Firewall -> IDS/IPS -> Serveur

        for (Client client : clients) {
            // Client -> WAF (tous les clients envoient au même WAF)
            addIC(client.output("requete"), waf.input("requete"));
            addIC(client.output("client_id"), waf.input("client_id"));
            addIC(client.output("ip_address"), waf.input("ip_address"));
            addIC(client.output("type_attaque"), waf.input("type_attaque"));
            addIC(client.output("payload_size"), waf.input("payload_size"));
            addIC(client.output("compteur"), waf.input("compteur"));

            // Retour pour blocage : Serveur -> Client
            addIC(serveur.output("bloquer"), client.input("ordre"));
        }

        // WAF -> Firewall
        addIC(waf.output("requete_filtree"), firewall.input("requete_filtree"));
        addIC(waf.output("client_id_out"), firewall.input("client_id_out"));
        addIC(waf.output("ip_out"), firewall.input("ip_out"));
        addIC(waf.output("type_out"), firewall.input("type_out"));
        addIC(waf.output("payload_out"), firewall.input("payload_out"));

        // Firewall -> IDS/IPS
        addIC(firewall.output("requete_autorisee"), idsIps.input("requete"));
        addIC(firewall.output("client_id_out"), idsIps.input("client_id"));
        addIC(firewall.output("ip_out"), idsIps.input("ip_address"));
        addIC(firewall.output("type_out"), idsIps.input("type_attaque"));
        addIC(firewall.output("payload_out"), idsIps.input("payload_size"));

        // IDS/IPS -> Serveur
        addIC(idsIps.output("requete_validee"), serveur.input("requete"));
        addIC(idsIps.output("client_id_out"), serveur.input("client_id"));

        // Alertes système (vers l'extérieur du système couplé)
        addEOC(waf.output("alerte_waf"), output("alerte_systeme"));
        addEOC(firewall.output("alerte_firewall"), output("alerte_systeme"));
        addEOC(idsIps.output("alerte_ids"), output("alerte_systeme"));
        addEOC(serveur.output("alerte_panne"), output("alerte_systeme"));
    }

    @Override
    public Model select(ArrayList<Model> modeles) {
        // Filter models that are actually ready to process
        ArrayList<Model> readyModels = new ArrayList<>();
        for (Model m : modeles) {
            if (m instanceof AtomicModel) {
                AtomicModel am = (AtomicModel) m;
                if (am.ta() < Integer.MAX_VALUE) {
                    readyModels.add(m);
                }
            }
        }

        if (readyModels.isEmpty()) {
            return null;
        }

        // Priority-based selection
        for (Model m : readyModels) {
            if (m instanceof Client) {
                return m;
            }
        }
        for (Model m : readyModels) {
            if (m instanceof WAF) {
                return m;
            }
        }
        for (Model m : readyModels) {
            if (m instanceof Firewall) {
                return m;
            }
        }
        for (Model m : readyModels) {
            if (m instanceof IDS_IPS) {
                return m;
            }
        }
        for (Model m : readyModels) {
            if (m instanceof Serveur) {
                return m;
            }
        }

        return readyModels.get(0);
    }

    public Serveur getServeur() {
        return serveur;
    }

    public List<Client> getClients() {
        return clients;
    }

    public WAF getWaf() {
        return waf;
    }

    public Firewall getFirewall() {
        return firewall;
    }

    public IDS_IPS getIdsIps() {
        return idsIps;
    }
}
