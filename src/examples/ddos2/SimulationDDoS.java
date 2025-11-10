package examples.ddos2;

import exception.DEVS_Exception;
import simulator.RootCoordinator;

public class SimulationDDoS {

    public static void main(String[] args) throws DEVS_Exception {
        FileGenerator fileGen = new FileGenerator();

        System.out.println("-------------------------------------------------------------");
        System.out.println("|   SIMULATION AVANCÉE DE DÉTECTION D'ATTAQUES DDoS - DEVS  |");
        System.out.println("|   Architecture: WAF -> Firewall -> IDS/IPS -> Serveur     |");
        System.out.println("-------------------------------------------------------------\n");

        fileGen.appendLine("-------------------------------------------------------------");
        fileGen.appendLine("|   SIMULATION AVANCÉE DE DÉTECTION D'ATTAQUES DDoS - DEVS  |");
        fileGen.appendLine("-------------------------------------------------------------\n");

        // Lancer différents scénarios
        lancerScenario1_TraficNormal();
        // lancerScenario2_HTTPFlood();
        // lancerScenario3_SYNFlood();
        // lancerScenario4_DNSAmplification();
        // lancerScenario5_AttaqueMixte();

        // System.out.println("\n-------------------------------------------------------------");
        // System.out.println("|   ANALYSE DES RÉSULTATS AVEC OLLAMA                        |");
        // System.out.println("-------------------------------------------------------------\n");
        // fileGen.analyzeWithOllama();
        // System.out.println("\n-------------------------------------------------------------");
        // System.out.println("|   ANALYSE TERMINÉE                                         |");
        // System.out.println("-------------------------------------------------------------\n");
    }

    public static void lancerScenario1_TraficNormal() throws DEVS_Exception {
        FileGenerator fileGen = new FileGenerator();

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("|   SCÉNARIO 1: Trafic Normal (Sans Attaque)                |");
        System.out.println("-------------------------------------------------------------\n");

        fileGen.appendLine("\n-------------------------------------------------------------");
        fileGen.appendLine("|   SCÉNARIO 1: Trafic Normal (Sans Attaque)                |");
        fileGen.appendLine("-------------------------------------------------------------\n");

        SystemeDDoS systeme = new SystemeDDoS(
                "Systeme_Trafic_Normal",
                "Scénario 1: Trafic légitime uniquement",
                5,
                0,
                Client.TypeAttaqueDDoS.LEGITIME,
                20,
                50
        );

        executerSimulation(systeme, 30, "Scénario 1");
    }

    public static void lancerScenario2_HTTPFlood() throws DEVS_Exception {
        FileGenerator fileGen = new FileGenerator();

        System.out.println("\n-----------------------------------------------------------");
        System.out.println("|   SCÉNARIO 2: Attaque HTTP Flood (Couche Application)      |");
        System.out.println("-------------------------------------------------------------\n");

        fileGen.appendLine("\n-------------------------------------------------------------");
        fileGen.appendLine("|   SCÉNARIO 2: Attaque HTTP Flood (Couche Application)      |");
        fileGen.appendLine("--------------------------------------------------------------\n");

        SystemeDDoS systeme = new SystemeDDoS(
                "Systeme_HTTP_Flood",
                "Scénario 2: Attaque couche application",
                3,
                7,
                Client.TypeAttaqueDDoS.HTTP_FLOOD,
                15,
                40
        );

        executerSimulation(systeme, 35, "Scénario 2");
    }

    public static void lancerScenario3_SYNFlood() throws DEVS_Exception {
        FileGenerator fileGen = new FileGenerator();

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("|   SCÉNARIO 3: Attaque SYN Flood (Protocol Attack)         |");
        System.out.println("-------------------------------------------------------------\n");

        fileGen.appendLine("\n-------------------------------------------------------------");
        fileGen.appendLine("|   SCÉNARIO 3: Attaque SYN Flood (Protocol Attack)         |");
        fileGen.appendLine("-------------------------------------------------------------\n");

        SystemeDDoS systeme = new SystemeDDoS(
                "Systeme_SYN_Flood",
                "Scénario 3: Attaque protocole TCP",
                2, // 2 clients légitimes
                10, // 10 bots SYN Flood
                Client.TypeAttaqueDDoS.SYN_FLOOD,
                20,
                60
        );

        executerSimulation(systeme, 40, "Scénario 3");
    }

    public static void lancerScenario4_DNSAmplification() throws DEVS_Exception {
        FileGenerator fileGen = new FileGenerator();

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("|   SCÉNARIO 4: DNS Amplification (Reflection Attack)       |");
        System.out.println("-------------------------------------------------------------\n");

        fileGen.appendLine("\n-------------------------------------------------------------");
        fileGen.appendLine("|   SCÉNARIO 4: DNS Amplification (Reflection Attack)       |");
        fileGen.appendLine("-------------------------------------------------------------\n");

        SystemeDDoS systeme = new SystemeDDoS(
                "Systeme_DNS_Amplification",
                "Scénario 4: Attaque par amplification DNS",
                2, // 2 clients légitimes
                8, // 8 bots DNS Amplification
                Client.TypeAttaqueDDoS.DNS_AMPLIFICATION,
                25,
                70
        );

        executerSimulation(systeme, 30, "Scénario 4");
    }

    public static void lancerScenario5_AttaqueMixte() throws DEVS_Exception {
        FileGenerator fileGen = new FileGenerator();

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("|   SCÉNARIO 5: Attaque Mixte Multi-Vectorielle             |");
        System.out.println("-------------------------------------------------------------\n");

        fileGen.appendLine("\n-------------------------------------------------------------");
        fileGen.appendLine("|   SCÉNARIO 5: Attaque Mixte Multi-Vectorielle             |");
        fileGen.appendLine("-------------------------------------------------------------\n");

        // Pour une attaque mixte, vous devriez modifier SystemeDDoS pour accepter
        // différents types de bots. Pour l'instant, on simule avec UDP Flood
        SystemeDDoS systeme = new SystemeDDoS(
                "Systeme_Attaque_Mixte",
                "Scénario 5: Combinaison d'attaques",
                4, // 4 clients légitimes
                12, // 12 bots
                Client.TypeAttaqueDDoS.VOLUMETRIC_UDP_FLOOD,
                30,
                100
        );

        executerSimulation(systeme, 50, "Scénario 5");
    }

    public static void lancerScenario6_Slowloris() throws DEVS_Exception {
        FileGenerator fileGen = new FileGenerator();

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("|   SCÉNARIO 6: Slowloris (Low & Slow Attack)               |");
        System.out.println("-------------------------------------------------------------\n");

        fileGen.appendLine("\n-------------------------------------------------------------");
        fileGen.appendLine("|   SCÉNARIO 6: Slowloris (Low & Slow Attack)               |");
        fileGen.appendLine("-------------------------------------------------------------\n");

        SystemeDDoS systeme = new SystemeDDoS(
                "Systeme_Slowloris",
                "Scénario 6: Attaque lente et furtive",
                3, // 3 clients légitimes
                5, // 5 bots Slowloris (peu mais efficaces)
                Client.TypeAttaqueDDoS.SLOWLORIS,
                15,
                35
        );

        executerSimulation(systeme, 60, "Scénario 6");
    }

    private static void executerSimulation(SystemeDDoS systeme, int duree, String nomScenario)
            throws DEVS_Exception {
        FileGenerator fileGen = new FileGenerator();

        RootCoordinator root = new RootCoordinator(systeme.getSimulator());
        root.init(0);

        long timeout = duree * 1000L + 30000L; // Simulation time + 30s buffer
        long startTime = System.currentTimeMillis();

        root.run(duree);

        long endTime = System.currentTimeMillis();
        if (endTime - startTime > timeout) {
            System.out.println("WARNING: Simulation exceeded expected time");
        }

        String debut = String.format("Démarrage de la simulation pour %d secondes...\n", duree);
        System.out.println(debut);
        fileGen.appendLine(debut);

        // Générer le rapport final du serveur
        systeme.getServeur().genererRapport();

        // Statistiques globales de la simulation
        String fin = String.format(
                "\n-------------------------------------------------------------\n"
                + "| Simulation terminée: %s                                  |\n"
                + "| Temps d'exécution réel: %d ms                            |\n"
                + "| Durée simulée: %d secondes                               |\n"
                + "------------------------------------------------------------\n",
                nomScenario, (endTime - startTime), duree
        );

        System.out.println(fin);
        fileGen.appendLine(fin);
    }

    public static void lancerScenarioPersonnalise(
            int nbLegitimes,
            int nbBots,
            Client.TypeAttaqueDDoS typeAttaque,
            int seuilSurcharge,
            int seuilPanne,
            int duree) throws DEVS_Exception {

        FileGenerator fileGen = new FileGenerator();

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("|   SCÉNARIO PERSONNALISÉ                                    |");
        System.out.println("-------------------------------------------------------------\n");

        fileGen.appendLine("\n-------------------------------------------------------------");
        fileGen.appendLine("|   SCÉNARIO PERSONNALISÉ                                    |");
        fileGen.appendLine("-------------------------------------------------------------\n");

        SystemeDDoS systeme = new SystemeDDoS(
                "Systeme_Personnalise",
                "Scénario personnalisé",
                nbLegitimes,
                nbBots,
                typeAttaque,
                seuilSurcharge,
                seuilPanne
        );

        executerSimulation(systeme, duree, "Personnalisé");
    }
}
