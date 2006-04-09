/*
 * IEPP: Isi Engineering Process Publisher
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
 
package iepp;

import iepp.domaine.DefinitionProcessus;
import iepp.domaine.adaptateur.AdaptateurDPArbre;
import iepp.ui.VueDPArbre;
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.VueDPGraphe;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Construit le projet à éditer dans le logiciel
 */
public class Projet implements Serializable, Observer
{

	/**
	 * Lien vers la fenêtre d'édition qui contient le graphe
	 */
	private FenetreEdition fenEdit = null ;
	
	/**
	 * Lien vers la définition de processus qui est éditée dans le logiciel
	 */
	private DefinitionProcessus defProc = null ;
	
	
	/**
	 * Nombre maximal de commandes annulables
	 */
	private static final int NBCOMMANDES = 5 ;
	
	/**
	 * Gestionnaire des undo/redo
	 */
	//private VGestAnnulation gestionnaireAnnulation ;
	
	/**
	 * Permet de nommer les éléments créés
	 */
	private static int nbSansNom = 0 ;
	
	/**
	 * Indique si le projet a été modifié depuis la dernière sauvegarde ou le chargement
	 */
	private boolean modifie = false ;
	
	
	/**
	 * Créer un projet avec l'ensemble des fenêtres liées au projet
	 *
	 */	
	public Projet()
	{
		// création d'une définition de processus vide
		this.defProc = new DefinitionProcessus() ;
		this.defProc.addObserver(this);
		// création d'un adaptateur qui fournira les données à la vue
		AdaptateurDPArbre adaptateur = new AdaptateurDPArbre(defProc);
		// Création vue arbre
		VueDPArbre vue = new VueDPArbre(adaptateur);
		// création du gestionnaire d'annulation
		//this.gestionnaireAnnulation = new VGestAnnulation(NBCOMMANDES);
		// afficher la vue sur la fenetre principale
		Application.getApplication().getFenetrePrincipale().enregistrerVueArbre (vue) ;
		// Créer la fenêtre d'édition et la vue du graphe
		this.fenEdit = new FenetreEdition (new VueDPGraphe (defProc)) ;
		Application.getApplication().getFenetrePrincipale().setPanneauGenerique(this.fenEdit) ;
		this.rafraichirLangue();
		Projet.nbSansNom = 0;
		this.setModified(true) ;
	}
	
	/**
	 * Constructeur de projet à partir d'éléments sauvegardés
	 * @param dp définition de processus sauvegardé
	 * @param diag diagramme sauvegardé
	 * @param elements	liste des éléments du diagramme
	 * @param liens liste des liens présents sur le diagramme
	 * @param dernierIdUtilise dernier id utilisé pour la dp chargée, permet de continuer là où on s'est arrêté
	 */
	public Projet(DefinitionProcessus dp, Vector elements , Vector liens, int dernierIdUtilise)
	{
		this.defProc = dp;
		this.defProc.addObserver(this);
		// création d'un adaptateur qui fournira les données à la vue
		AdaptateurDPArbre adaptateur = new AdaptateurDPArbre(defProc);
		// Création vue arbre
		VueDPArbre vue = new VueDPArbre(adaptateur);
		// création du gestionnaire d'annulation
		//this.gestionnaireAnnulation = new VGestAnnulation(NBCOMMANDES);
		// afficher la vue sur la fenetre principale
		Application.getApplication().getFenetrePrincipale().enregistrerVueArbre (vue) ;
		// Créer la fenêtre d'édition et la vue du graphe
		VueDPGraphe diagr = new VueDPGraphe (this.defProc);
		//diagr.setElements(elements);
		//diagr.setLiens(liens);
		this.fenEdit = new FenetreEdition (diagr) ;
		Application.getApplication().getFenetrePrincipale().setPanneauGenerique(this.fenEdit) ;
		this.rafraichirLangue();
		Projet.nbSansNom = dernierIdUtilise;
		this.setModified(false) ;
	}
	
	
	public static String getNouveauNom()
	{
		String retour =  "noname" + Projet.nbSansNom ;
		Projet.nbSansNom += 1;
		return retour;
	}
	
	/**
	 * Renvoie le dernier ID utilisé pour les nouveaux éléments
	 * @return un id
	 */
	public static int getDernierId()
	{
		return Projet.nbSansNom;
	}
	
	/**
	 * Récupère le lien vers la fenêtre d'édition
	 * @return la fenetre d'edition
	 */
	public FenetreEdition getFenetreEdition()
	{
		return this.fenEdit ;
	}
	
	/**
	 * Modification de la fenêtre d'édition
	 * @param FE, Nouvelle fenêtre d'édition
	 */
	public void setFenetreEdition(FenetreEdition FE)
	{
		this.fenEdit = FE;
	}
	
	/**
	 * Récupère la définition de processus courante de l'application
	 * @return
	 */
	public DefinitionProcessus getDefProc()
	{
		return this.defProc ;
	}
	
	/*
	public boolean estAnnulerVide()
	{
		return this.gestionnaireAnnulation.estAnnulerVide();
	}
	
	public boolean estRefaireVide()
	{
		return this.gestionnaireAnnulation.estRefaireVide();
	}
	*/
	
	/**
	 * Indique si le projet a été modifié et doit être sauvegardé
	 */
	public boolean estModifie()
	{
		return this.modifie ;
	}
	
	public void setModified(boolean modif)
	{
		this.modifie = modif ;
		if (modif)
		{
			Application.getApplication().getFenetrePrincipale().setTitle(
				Application.getApplication().getConfigPropriete("titre")
				+ " " + Application.getApplication().getReferentiel().getNomReferentiel()
				+ "::" + this.defProc.getNomDefProc() + " *" );
		}
		else
		{
			Application.getApplication().getFenetrePrincipale().setTitle(
					Application.getApplication().getConfigPropriete("titre")
					+ " " + Application.getApplication().getReferentiel().getNomReferentiel()
					+ "::" + this.defProc.getNomDefProc());
		}
		Application.getApplication().getFenetrePrincipale().majEtat();
	}
	
	
	/**
	 * Modifie la définition processus courante
	 * @param DP, nouvelle définition processus
	 */
	public void setDefProc(DefinitionProcessus DP)
	{
		defProc = DP;
		this.setModified(true) ;
		this.defProc.addObserver(this);
	}
	
	/**
	 * Modifie la définition processus courante
	 * @param DP, nouvelle définition processus
	 */
	public void deleteDefProc()
	{
		defProc = null;
		Application.getApplication().getFenetrePrincipale().setTitle(
							Application.getApplication().getConfigPropriete("titre"));
	}
	
	/**
	* Modifie la fenêtre d'édition courante
	* @param FE, fenêtre édition
    */
	public void setFenEdit(FenetreEdition FE)
	{
		fenEdit = FE;
	}
	
	
	/**
	 * Demande de rafraichissement de toutes les fenetres gérées par le projet
	 *
	 */
	public void rafraichirLangue()
	{
		this.fenEdit.rafraichirLangue();
	}


	public void update(Observable o, Object arg)
	{
		this.setModified(true) ;
	}
}
