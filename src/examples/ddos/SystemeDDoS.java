package examples.ddos;

import model.CoupledModel;
import model.Model;
import types.DEVS_String;

import java.util.List;
import java.util.ArrayList;

public class SystemeDDoS extends CoupledModel {
    
    private Serveur serveur;
    private List<Client> clients;
    
    private FileGenerator fileGen = new FileGenerator();
  
    public SystemeDDoS(String name,String desc, int nbClientsLegitimes, int nbBots, 
                       int seuilSurcharge, int seuilPanne) {
        super(name,desc);
        
        this.clients = new ArrayList<>();
        
        serveur = new Serveur("Serveur","Serveur", seuilSurcharge, seuilPanne);
        addSubModel(serveur);
        
        int clientId = 1;

        for (int i = 0; i < nbClientsLegitimes; i++) {
            Client client = new Client("Client_Legitime_" + clientId,"Client legitime Numero"+clientId, clientId, false);
            clients.add(client);
            addSubModel(client);
            clientId++;
        }
        
 
        for (int i = 0; i < nbBots; i++) {
            Client bot = new Client("Bot_" + clientId,"Bot numero"+clientId, clientId, true);
            clients.add(bot);
            addSubModel(bot);
            clientId++;
        }
        
        
        addOutput(new DEVS_String(),"alerte_panne", "Alerte en cas de panne du serveur");
        
        creerLiaisons();
        
        System.out.println("\n=== Système DDoS initialisé ===");
        System.out.println("Clients légitimes: " + nbClientsLegitimes);
        System.out.println("Bots: " + nbBots);
        System.out.println("Total clients: " + clients.size());
        System.out.println("================================\n");
        String[] allValue = {"\n=== Système DDoS initialisé ===",
        		"Clients légitimes: " + nbClientsLegitimes,"Bots: " + nbBots,"Total clients: " + clients.size(),
        		"================================\n"};
        for (String value : allValue) {
        	fileGen.appendLine(value);
        }
    }
    
    
    private void creerLiaisons() {
        for (Client client : clients) {
            addIC(client.output("requete"), serveur.input("requete"));
            addIC(client.output("client_id"), serveur.input("client_id"));

            addIC(serveur.output("bloquer"), client.input("ordre"));
        }

        addEOC(serveur.output("alerte_panne"), output("alerte_panne"));
    }
    

    @Override
    public Model select(ArrayList<Model> modeles) {
        for (Model m : modeles) {
            if (m instanceof Serveur) {
                return m;
            }
        }
        return modeles.get(0);
    }

    public Serveur getServeur() {
        return serveur;
    }
    
    public List<Client> getClients() {
        return clients;
    }
}
