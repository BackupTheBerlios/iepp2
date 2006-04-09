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
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;


/**
 * Crée par Julie TAYAC le 01/02/06: Presque identique à CSupprimerComposantDyn
 * Commande annulable permettant de supprimer un composant se trouvant dans la DP courante
 * Supprime le composant au niveau du graphe et au niveau de l'arbre
 */
public class CSupprimerComposantDyn extends CommandeAnnulable
{
	/**
	 * Id du composant à supprimer du graphe
	 */
	private IdObjetModele composant;
	
	/**
	 * Cellule du composant à supprimer du graphe
	 */
	private ComposantCellDyn composantCellDyn;

	/**
	 * Diagramme duquel on veut supprimer un composant
	 */
	private VueDPGraphe diagramme;


	/**
	 * Constructeur de la commande, récupère le composant à supprimer
	 * et le diagramme courant de l'application
	 * @param compo id du composant à supprimer
	 */
	public CSupprimerComposantDyn (ComposantCellDyn compo)
	{
		// initialiser le composant à supprimer
		this.composantCellDyn = compo;
		this.composant = compo.getId() ;
		this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();

	}
	
	/**
	 * Constructeur de la commande, récupère le composant à supprimer
	 * et le diagramme courant de l'application
	 * @param compo id du composant à supprimer
	 */
	public CSupprimerComposantDyn (IdObjetModele compo)
	{
		// initialiser le composant à supprimer
		this.composant = compo;
		this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		this.composantCellDyn = (ComposantCellDyn)this.diagramme.contient(this.composant);
	}

	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * Parcours la liste des produits du composant, vérifie s'il n'y a pas
	 * de produits fusion "à défusionner", supprime les figures des produits et du composant
	 * @return true si l'export s'est bien passé false sinon
	 */
	public boolean executer()
	{
		// Verifier si le composant est dans le diagramme, et si oui, le supprimer
		if (diagramme.contient(this.composant) != null)
		{
			new CSupprimerComposantGrapheDyn(composantCellDyn).executer();
		}
	
		// Mettre a jour l'arbre
		Application.getApplication().getFenetrePrincipale().getVueDPArbre().updateUI();

		return true;
	}
}
