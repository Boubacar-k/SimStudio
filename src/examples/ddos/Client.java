package examples.ddos;

import model.*;
import types.*;
import exception.DEVS_Exception;

public class Client extends AtomicModel {
    
   
    private static final String ACTIF_LEGITIME = "ACTIF.LÉGITIME";
    private static final String ACTIF_BOT = "ACTIF.BOT";
    private static final String INACTIF = "INACTIF";
    private static final String BLOQUE = "BLOQUÉ";
    
    private String typeClient;
    private int clientId;
    
    private FileGenerator fileGen = new FileGenerator();
    
    public Client(String name,String desc, int id, boolean isBot) {
        super(name, desc);
        this.clientId = id;
        this.typeClient = isBot ? "BOT" : "LÉGITIME";
        
        String[] s = {ACTIF_LEGITIME,ACTIF_BOT,INACTIF,BLOQUE};

        DEVS_Enum etats = new DEVS_Enum(s);
        
        String[] x = {"bloquer"};
        DEVS_Enum ordres = new DEVS_Enum(x);
        addInput(ordres,"ordre","blocage du client");
    
        DEVS_String requete = new DEVS_String("requête");
        addOutput(requete,"requete","requete client");
 
        addOutput(new DEVS_Integer(clientId),"client_id","identifiant du client");

        
        addStateVariable(etats,"etat","Etat du client");
        addStateVariable(new DEVS_String(typeClient),"type","bot ou legitime");
        
        //init
        
        if (isBot) {
            setVar("etat", ACTIF_BOT);
        } else {
            setVar("etat", ACTIF_LEGITIME);
        }
    }

    @Override
    public void lambda() throws DEVS_Exception {
        String etatActuel = (String) getVar("etat").getValue();
        
     
        if (etatActuel.equals(ACTIF_LEGITIME) || etatActuel.equals(ACTIF_BOT)) {
            setOutput("requete", new DEVS_String("requête"));
            setOutput("client_id", new DEVS_Integer(clientId));
            
            System.out.println("Client_" + clientId 
                + " (" + typeClient + ") envoie une requête");
            fileGen.appendLine("Client_" + clientId 
                + " (" + typeClient + ") envoie une requête");
            
        }
    }
    

    @Override
    public void deltaInt() {
        String etatActuel = (String) getVar("etat").getValue();
        
        switch (etatActuel) {
            case ACTIF_LEGITIME:
                setVar("etat", ACTIF_LEGITIME);
                break;
                
            case ACTIF_BOT:
               
                setVar("etat", ACTIF_BOT);
                break;
                
            case BLOQUE:
                
                setVar("etat", INACTIF);
                System.out.println("Client_" + clientId 
                    + " débloqé, passe à INACTIF");
                fileGen.appendLine("Client_" + clientId 
                        + " débloqé, passe à INACTIF");
                break;
                
            case INACTIF:
                setVar("etat", INACTIF);
                break;
        }
    }
    

    @Override
    public void deltaExt(int e) throws DEVS_Exception{
        String etatActuel = (String) getVar("etat").getValue();
        
       
        String ordreRecu = getInput("ordre").toString();
        
        if (ordreRecu != null) {
            String ordre = ordreRecu;

            if (ordre.equals("bloquer")) {
                if (etatActuel.equals(ACTIF_LEGITIME) || etatActuel.equals(ACTIF_BOT)) {
                    setVar("etat", BLOQUE);
                    System.out.println("Client_" + clientId 
                        + " BLOQUÉ par le serveur");
                    
                    fileGen.appendLine("Client_" + clientId 
                            + " BLOQUÉ par le serveur");
                }
            }
        }
    }

    @Override
    public int ta() {
        String etatActuel = (String) getVar("etat").getValue();
        
        switch (etatActuel) {
            case ACTIF_LEGITIME:
                return 5;
                
            case ACTIF_BOT:
                return 1;
                
            case BLOQUE:
                return 10;
                
            case INACTIF:
                return DEVS_Integer.POSITIVE_INTINITY;
                
            default:
                return DEVS_Integer.POSITIVE_INTINITY;
        }
    }
    
    
//    private double getCurrentTime() {
//        return 0.0;
//    }
    
    public int getClientId() {
        return clientId;
    }
}
