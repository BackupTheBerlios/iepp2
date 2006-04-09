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
import iepp.application.CEnregistrerInterface;
import iepp.application.CommandeAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;

import java.util.Vector;


/**
 * Commande permettant de supprimer un produit en sortie d'un composant vide
 * Supprime le produit au niveau du diagramme et aussi au niveau du modèle
 */
public class CSupprimerProduit extends CommandeAnnulable
{
	
	/**
	 * Composant auquel on enlève un produit en sortie
	 */
	private ComposantProcessus cp ;
	
	/**
	 * Id du produit à supprimer
	 */
	private IdObjetModele produit;
	
	/**
	 * Indicateur d'entree ou sortie
	 */
	private int es;

	/**
	 * Constructeur de la commande, récupère le composant concerné par la suppression
	 * @param produit id du produit à supprimer
	 */
	public CSupprimerProduit(IdObjetModele produit, int es) 
	{
		// récupère le composant de processus concerné
		this.cp = (ComposantProcessus)produit.getRef();
		// garder un lien vers le produit à supprimer
		this.produit = produit;
		// determiner s'il s'agit d'un produit en entree ou en sortie
		this.es = es;
	}

	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * AJOUTER COMMENTAIRE
	 * @return true si l'export s'est bien passé false sinon
	 */
	public boolean executer()
	{
		// pour composant vide
		VueDPGraphe diagrame = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		
		//Récupération du produit
		
//		IeppCell cellCourant = null;
		IeppCell cell = diagrame.contient(produit);
		
//		// POur toutes les cellules du diagramme courant (stat, Dyn) on cherche si le composant est present
//		for (int i = 0; i < ((GraphModelView)diagrame.getModel()).getElementCells().size(); i++) {
//			cellCourant = (IeppCell) ((GraphModelView)diagrame.getModel()).getElementCells().elementAt(i);
//			if (cellCourant.getId() != null) {
//				if (cellCourant.getId().equals(produit)) {
//					cell = cellCourant;
//				}
//			}
//		}
	
		
		// Si le produit est afficher
		if(cell != null){
			
			Vector fusions = ((GraphModelView)diagrame.getModel()).getProduitCellFusionCells();
			
			// Supprimer les produits fusion de ce produit
			for(int i=0; i<fusions.size(); i++){
				ProduitCellFusion fusion = (ProduitCellFusion)fusions.elementAt(i);
				if(fusion.getProduitCellSortie().equals(cell)){
					CSupprimerProduitFusion cslf = new CSupprimerProduitFusion(diagrame,fusion);
					cslf.executer();
				}
				Vector entrees = fusion.getProduitCellEntrees();
				for(int j=0; j<entrees.size();j++){
					ProduitCellEntree entree = (ProduitCellEntree)entrees.elementAt(j);
					if(entree.equals(cell)){
						CSupprimerProduitFusion cslf = new CSupprimerProduitFusion(diagrame,fusion);
						cslf.executer();
					}
				}
				
			}
			// On supprime la cellule
			diagrame.supprimerCellule(cell);
		}
	
		// Recaler tous les IDs qui suivent ce composant dans la liste
		// Dans le vecteur interface se trouvent les veritables produits
		
		IdObjetModele courant = null;
		Vector interf;
		
		if (this.es == 1)
		{
			interf = cp.getInterfaceIn();
		}
		else
		{
			interf = cp.getInterfaceOut();
		}
		
		for (int i = produit.getNumRang()+1; i < interf.size(); i++)
		{
			// Recuperer l'ID associee
			courant = (IdObjetModele)cp.getMapId().get(interf.elementAt(i));
			// Decrementer le rang de cet ID
			courant.decrementerRang();
		}
		
		// Supprimer du vecteur le produit à enlever
		interf.removeElementAt(produit.getNumRang());
		
		// Sauvegarde du nouveau point APES
		String chemin = Application.getApplication().getReferentiel().getCheminReferentiel()+Application.filesep;
		CEnregistrerInterface saveCp = new CEnregistrerInterface(cp.getIdComposant());
        saveCp.sauvegarderInterface(chemin+cp+".apes");
        
		// Rafraichir l'arbre
		Application.getApplication().getFenetrePrincipale().getVueDPArbre().updateUI();
		
		// Rafraichir le graphe
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().repaint();
		
		return true;
		
	}
	
}
