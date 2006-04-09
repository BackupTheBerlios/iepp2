package iepp.application.ageneration;

import iepp.domaine.ElementPresentation;

import java.util.ArrayList;
/**
 * class qui permet de gérer les sous scenario dans l'arbre
 */
public class GScenario {
	ArrayList maliste;
	
	//constructeur
	public GScenario()
	{
		maliste = new ArrayList();
	}
	
	//methode qui permet d'ajouter une nouvel ElementPresentation la liste
	public void addelement(ElementPresentation monelem)
	{
		this.maliste.add(monelem);
	}
	//methode qui permet de retourner un element a une position donnée
	public Object getelement(int i)
	{
		return this.maliste.get(i);
	}
    //methode qui retourne la taille de la liste	
	public int getSize()
	{
		return maliste.size();
	}
}

