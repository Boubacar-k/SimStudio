package examples.ddos;

import model.AtomicModel;
import types.*;
import java.util.HashMap;
import java.util.Map;

import exception.DEVS_Exception;


public class Serveur extends AtomicModel {
    
    private static final String NORMAL = "NORMAL";
    private static final String SURCHARGE = "SURCHARGE";
    private static final String PANNE = "PANNE";
    

    private int seuilSurcharge;
    private int seuilPanne;
    private int seuilBlocage;
    
  
    private Map<Integer, Integer> compteurParClient;
    private FileGenerator fileGen = new FileGenerator();
    
    
    public Serveur(String name,String desc, int seuilSurcharge, int seuilPanne) {
        super(name,desc);
        
        this.seuilSurcharge = seuilSurcharge;
        this.seuilPanne = seuilPanne;
        this.seuilBlocage = 3;
        this.compteurParClient = new HashMap<>();
        
        String[] s = {NORMAL,SURCHARGE,PANNE};
        
        DEVS_Enum etats = new DEVS_Enum(s);
        
        // X
        addInput(new DEVS_String(""),"requete","requete client");
        addInput(new DEVS_Integer(0),"client_id","L'id du client");
        
        // Y
        addOutput(new DEVS_String(""),"bloquer","Blocage");
        addOutput(new DEVS_Integer(0),"client_id_bloque","id du client a bloquer");
        addOutput(new DEVS_String(""),"alerte_panne","alert de panne");
        
        // Variables d'etat
        addStateVariable(etats,"etat","");
        addStateVariable(new DEVS_Integer(0),"compteur_global","");
        addStateVariable(new DEVS_Integer(seuilSurcharge),"seuil_surcharge","");
        addStateVariable(new DEVS_Integer(seuilPanne),"seuil_panne", "");
        addStateVariable(new DEVS_Integer(seuilBlocage),"seuil_blocage","");
        
        // init
        setVar("etat", NORMAL);
        setVar("compteur_global", 0);
        
        System.out.println("Serveur initialisé:");
        System.out.println("  - Seuil surcharge: " + seuilSurcharge + " req/s");
        System.out.println("  - Seuil panne: " + seuilPanne + " req/s");
        System.out.println("  - Seuil blocage client: " + seuilBlocage + " req/s");
        
        String[] allValue = {"Serveur initialisé:","  - Seuil surcharge: " + seuilSurcharge + " req/s",
        		"  - Seuil panne: " + seuilPanne + " req/s","  - Seuil blocage client: " + seuilBlocage + " req/s"};  
        
        for (String value : allValue) {
        	fileGen.appendLine(value);
        }
    }
    

    @Override
    public void lambda() throws DEVS_Exception {
        String etatActuel = (String) getVar("etat").getValue();
        int compteurGlobal = (int) getVar("compteur_global").getValue();
  
        
        for (Map.Entry<Integer, Integer> entry : compteurParClient.entrySet()) {
            int clientId = entry.getKey();
            int nbRequetes = entry.getValue();
            
            if (nbRequetes > seuilBlocage) {
            	setOutput("bloquer", new DEVS_String("bloquer"));
				setOutput("client_id_bloque", new DEVS_Integer(clientId));
                System.out.println("[SERVEUR] Blocage du Client_" + clientId 
                    + " (trop de requêtes: " + nbRequetes + "/s)");
                fileGen.appendLine("[SERVEUR] Blocage du Client_" + clientId 
                        + " (trop de requêtes: " + nbRequetes + "/s)");
            }
        }
  
        if (etatActuel.equals(PANNE)) {
            setOutput("alerte_panne", new DEVS_String("ALERTE: SERVEUR EN PANNE!"));
            System.out.println("[SERVEUR] ALERTE PANNE - Compteur global: " + compteurGlobal);
            fileGen.appendLine("[SERVEUR] ALERTE PANNE - Compteur global: " + compteurGlobal);
        }
        
        System.out.println("[SERVEUR] État: " + etatActuel 
            + " | Requêtes/s: " + compteurGlobal);
        fileGen.appendLine("[SERVEUR] État: " + etatActuel 
                + " | Requêtes/s: " + compteurGlobal);
    }
    

    @Override
    public void deltaInt() {
        String etatActuel = (String) getVar("etat").getValue();
        int compteurGlobal = (int) getVar("compteur_global").getValue();
        
        if (etatActuel.equals(NORMAL)) {
            if (compteurGlobal >= seuilSurcharge) {
                setVar("etat", SURCHARGE);
                System.out.println("[SERVEUR] Passage en SURCHARGE!");
                fileGen.appendLine("[SERVEUR] Passage en SURCHARGE!");
            }
        } else if (etatActuel.equals(SURCHARGE)) {
            if (compteurGlobal >= seuilPanne) {
                setVar("etat", PANNE);
                System.out.println("[SERVEUR] PASSAGE EN PANNE");
                fileGen.appendLine("[SERVEUR] PASSAGE EN PANNE");
            } else if (compteurGlobal < seuilSurcharge) {
                setVar("etat", NORMAL);
                System.out.println("[SERVEUR] Retour à NORMAL");
                fileGen.appendLine("[SERVEUR] Retour à NORMAL");
            }
        }
        
        setVar("compteur_global", 0);
        compteurParClient.clear();
    }
    
    @Override
    public void deltaExt(int e) {
    	String etatActuel = (String) ((DEVS_Enum) getVar("etat")).getValue();
        

        if (etatActuel.equals(PANNE)) {
            System.out.println("[SERVEUR] Requête ignorée (serveur en panne)");
            fileGen.appendLine("[SERVEUR] Requête ignorée (serveur en panne)");
            return;
        }
        
        String requete = (String) ((DEVS_String) getInput("requete")).getValue();
        int clientId = (int) ((DEVS_Integer) getInput("client_id")).getValue();
        
        if (requete != null) {

            int compteurGlobal = (int) getVar("compteur_global").getValue();
            compteurGlobal++;
            setVar("compteur_global", compteurGlobal);
            
            int compteurClient = compteurParClient.getOrDefault(clientId, 0);
            compteurClient++;
            compteurParClient.put(clientId, compteurClient);
            
            System.out.println("[SERVEUR] Requête reçue de Client_" + clientId 
                + " | Total: " + compteurGlobal + " req/s");
            
            fileGen.appendLine("[SERVEUR] Requête reçue de Client_" + clientId 
                    + " | Total: " + compteurGlobal + " req/s");
        }
    }
    
    @Override
    public int ta() {
        return 1;
    }
}
