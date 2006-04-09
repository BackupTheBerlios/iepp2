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

package iepp.application.aedition;


import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;


/**
 * Commande annulable permettant de supprimer un composant se trouvant dans la DP courante
 * Supprime le composant au niveau du graphe et au niveau de l'arbre
 */
public class CSupprimerComposant extends CommandeAnnulable
{
	/**
	 * Id du composant à supprimer du graphe
	 */
	private IdObjetModele composant;
	
	/**
	 * Cellule du composant à supprimer du graphe
	 */
	private ComposantCell composantCell;

	/**
	 * Diagramme duquel on veut supprimer un composant
	 */
	private VueDPGraphe diagramme;


	/**
	 * Constructeur de la commande, récupère le composant à supprimer
	 * et le diagramme courant de l'application
	 * @param compo id du composant à supprimer
	 */
	public CSupprimerComposant (ComposantCell compo)
	{
		// initialiser le composant à supprimer
		this.composantCell = compo;
		this.composant = compo.getId() ;
		this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();

	}
	
	/**
	 * Constructeur de la commande, récupère le composant à supprimer
	 * et le diagramme courant de l'application
	 * @param compo id du composant à supprimer
	 */
	public CSupprimerComposant (IdObjetModele compo)
	{
		// initialiser le composant à supprimer
		this.composant = compo;
		this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		this.composantCell = (ComposantCell)diagramme.contient(compo);
			
	}

	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * Parcours la liste des produits du composant, vérifie s'il n'y a pas
	 * de produits fusion "à défusionner", supprime les figures des produits et du composant
	 * @return true si l'export s'est bien passé false sinon
	 */
	public boolean executer()
	{
		//Suppression de tous les roles associes (sous-role et super-role) du composant
 		Application.getApplication().getProjet().getDefProc().removeListeAssociationSRole(this.composant);

		// Verifier si le composant est dans le diagramme, et si oui, le supprimer
		if (diagramme.contient(this.composant) != null)
		{
			new CSupprimerComposantGraphe(composantCell).executer();
		}

		// Enlever le composant de la definition processus
		Application.getApplication().getProjet().getDefProc().retirerComposant(this.composant);

		// Enlever le composant du référentiel (association id-référence)
		Application.getApplication().getReferentiel().supprimerElementEnMemoire (this.composant.getRef()) ;

		// Mettre a jour l'arbre
		Application.getApplication().getFenetrePrincipale().getVueDPArbre().updateUI();

		return true;
	}
}
