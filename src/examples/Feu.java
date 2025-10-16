package examples;
import model.AtomicModel;
import exception.DEVS_Exception;
import types.*;

public class Feu extends AtomicModel {

	public Feu(String name, String desc) {
		super(name, desc);
		// X
		String[] signaux = {"0","1"};
		addInput(new DEVS_Enum(signaux),"signal","Signal de marche/arr�t du feu");

		//Y
		String[] couleurs = {"vert", "orange", "rouge", "noir"};
		addOutput(new DEVS_Enum(couleurs),"couleur","Couleur annonc�e du feu");
		
		// S
		String[] situation = {"v", "o", "r", "n", "t1", "t2"};
		addStateVariable(new DEVS_Enum(situation),"statut","couleur courante du feu");
		addStateVariable(new DEVS_Enum(),"phase","temps restant dans l'�tat courant du feu");
		
		// Initialisation
		setVar("statut","v");
	}
	
	public void deltaInt() {
		if (getVar("statut").toString().equals("v")) {setVar("statut", "o"); setVar("phase", 1); }
		else if (getVar("statut").toString().equals("o")) {setVar("statut", "r"); setVar("phase", 3); }
		else if (getVar("statut").toString().equals("r")) {setVar("statut", "v"); setVar("phase", 5); }
		else if (getVar("statut").toString().equals("t1")) {setVar("statut", "n"); setVar("phase", Integer.MAX_VALUE); }
		else {setVar("statut", "v"); ; setVar("phase", 5); }
	}
	
	public void deltaExt(int e) {
		if (getInput("signal") == "0") {
			setVar("statut", "n");
			setVar("phase", Integer.MAX_VALUE);
		}
//		else if(getVar("statut").toString().equals("n")){setVar()}
		else setVar("phase", (int) getVar("phase").getValue() - e);
	}
	
	public void lambda() throws DEVS_Exception {
		if (getVar("statut").toString().equals("v")) setOutput("couleur", "orange");
		if (getVar("statut").toString().equals("o")) setOutput("couleur", "rouge");
		if (getVar("statut").toString().equals("r")) setOutput("couleur", "vert");
		if (getVar("statut").toString().equals("t1")) setOutput("couleur", "noir");
		if (getVar("statut").toString().equals("t2")) setOutput("couleur", "vert");
	}
	
	public int ta() {
		return (int) getVar("phase").getValue();
	}

}
