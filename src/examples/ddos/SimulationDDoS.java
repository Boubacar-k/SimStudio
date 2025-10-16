package examples.ddos;

import exception.DEVS_Exception;
import simulator.RootCoordinator;


public class SimulationDDoS {
    
	
    public static void main(String[] args) throws DEVS_Exception {
    	FileGenerator fileGen = new FileGenerator();
        System.out.println("+===============================================+");
        System.out.println("|   SIMULATION DÉTECTION D'ATTAQUE DDoS - DEVS  |");
        System.out.println("+===============================================+\n");
        
        String[] allValue = {"+===============================================+",
        		"|   SIMULATION DÉTECTION D'ATTAQUE DDoS - DEVS  |","+===============================================+\n"};
        for (String value : allValue) {
        	fileGen.appendLine(value);
        }
        
        
//        lancerScenario1();
//         lancerScenario2();
         lancerScenario3();
    }
    
    public static void lancerScenario1() throws DEVS_Exception {
    	
    	FileGenerator fileGen = new FileGenerator();
    	
    	System.out.println("+===============================================+");
        System.out.println("|   SCÉNARIO 1: Trafic Normal (Sans Bots)       |");
        System.out.println("+===============================================+\n");
        
        String[] allValue = {
        		"+===============================================+",
        		"|   SCÉNARIO 1: Trafic Normal (Sans Bots)       |",
        		"+===============================================+\n"
        		};
        for (String value : allValue) {
        	fileGen.appendLine(value);
        }
    	
        
        int nbClientsLegitimes = 3;
        int nbBots = 0;
        int seuilSurcharge = 10;
        int seuilPanne = 20;    
        int dureeSimulation = 30;
        
        // Créer le système
        SystemeDDoS systeme = new SystemeDDoS(
            "Systeme_DDoS_Normal", 
            "Systeme 1",
            nbClientsLegitimes, 
            nbBots, 
            seuilSurcharge, 
            seuilPanne
        );
        
        RootCoordinator root = new RootCoordinator(systeme.getSimulator());
        
        root.init(0);
        
        System.out.println("▶ Démarrage de la simulation pour " + dureeSimulation + " secondes...\n");
        
        fileGen.appendLine("▶ Démarrage de la simulation pour " + dureeSimulation + " secondes...\n");
        
        root.run(dureeSimulation);
        
        System.out.println("\n✓ Simulation terminée (Scénario 1)\n");
        
        fileGen.appendLine("\n✓ Simulation terminée (Scénario 1)\n");
    }
    

    public static void lancerScenario2() throws DEVS_Exception {
    	
    	FileGenerator fileGen = new FileGenerator();
        
        System.out.println("\n+===============================================+");
        System.out.println("|   SCÉNARIO 2: Attaque DDoS Modérée            |");
        System.out.println("+===============================================+\n");
        
        String[] allValue = {
        		"+===============================================+",
        		"|   SCÉNARIO 2: Attaque DDoS Modérée            |",
        		"+===============================================+\n"
        		};
        for (String value : allValue) {
        	fileGen.appendLine(value);
        }
        
        int nbClientsLegitimes = 2;
        int nbBots = 5;     
        int seuilSurcharge = 15;
        int seuilPanne = 30;
        int dureeSimulation = 40;
        
        SystemeDDoS systeme = new SystemeDDoS(
            "Systeme_DDoS_Attaque_Moderee", 
            "Systeme 2",
            nbClientsLegitimes, 
            nbBots, 
            seuilSurcharge, 
            seuilPanne
        );
        
        RootCoordinator root = new RootCoordinator(systeme.getSimulator());
        
        root.init(0);
        System.out.println("▶ Démarrage de la simulation pour " + dureeSimulation + " secondes...\n");
        
        fileGen.appendLine("▶ Démarrage de la simulation pour " + dureeSimulation + " secondes...\n");
        root.run(dureeSimulation);
        
        System.out.println("\n✓ Simulation terminée (Scénario 2)\n");
        
        fileGen.appendLine("\n✓ Simulation terminée (Scénario 2)\n");
    }
    

    public static void lancerScenario3() throws DEVS_Exception {
    	
    	FileGenerator fileGen = new FileGenerator();
        
        System.out.println("\n+===============================================+");
        System.out.println("|   SCÉNARIO 3: Attaque DDoS Massive (PANNE)      |");
        System.out.println("+===============================================+\n");
        
        String[] allValue = {
        		"+=================================================+",
        		"|   SCÉNARIO 3: Attaque DDoS Massive (PANNE)      |",
        		"+=================================================+\n"
        		};
        for (String value : allValue) {
        	fileGen.appendLine(value);
        }

        int nbClientsLegitimes = 1;
        int nbBots = 15;    
        int seuilSurcharge = 10;
        int seuilPanne = 25;
        int dureeSimulation = 50;
        
        // Créer le système
        SystemeDDoS systeme = new SystemeDDoS(
            "Systeme_DDoS_Attaque_Massive",
            "Systeme 3",
            nbClientsLegitimes, 
            nbBots, 
            seuilSurcharge, 
            seuilPanne
        );
        
        RootCoordinator root = new RootCoordinator(systeme.getSimulator());
        
        root.init(0);
        System.out.println("▶ Démarrage de la simulation pour " + dureeSimulation + " secondes...\n");
        fileGen.appendLine("▶ Démarrage de la simulation pour " + dureeSimulation + " secondes...\n");
        root.run(dureeSimulation);
        
        System.out.println("\n✓ Simulation terminée (Scénario 3)\n");
        fileGen.appendLine("\n✓ Simulation terminée (Scénario 3)\n");
    }
    
    public static void lancerScenarioPersonnalise(
            int nbLegitimes, 
            int nbBots, 
            int seuilSurcharge, 
            int seuilPanne, 
            int duree) throws DEVS_Exception {
    	
    	FileGenerator fileGen = new FileGenerator();
    	
        
        System.out.println("+===============================================+");
        System.out.println("|  SCÉNARIO PERSONNALISÉ                        |");
        System.out.println("+===============================================+\\n");
        
        String[] allValue = {
        		"+===============================================+",
        		"|  SCÉNARIO PERSONNALISÉ                        |",
        		"+===============================================+\n"
        		};
        for (String value : allValue) {
        	fileGen.appendLine(value);
        }
        
        SystemeDDoS systeme = new SystemeDDoS(
            "Systeme_DDoS_Personnalise",
            "Systeme 4",
            nbLegitimes, 
            nbBots, 
            seuilSurcharge, 
            seuilPanne
        );
        
        RootCoordinator root = new RootCoordinator(systeme.getSimulator());
        root.init(0);
        
        System.out.println("▶ Démarrage de la simulation pour " + duree + " secondes...\n");
        fileGen.appendLine("▶ Démarrage de la simulation pour " + duree + " secondes...\n");
        root.run(duree);
        
        System.out.println("\n✓ Simulation terminée\n");
        fileGen.appendLine("\n✓ Simulation terminée\n");
    }
}
