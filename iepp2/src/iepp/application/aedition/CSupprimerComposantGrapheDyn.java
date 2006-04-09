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
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.GraphConstants;


/**
 * Crée par Julie TAYAC le 01/02/06: Presque identique à CSupprimerComposantGraphe
 * Commande annulable permettant de supprimer un composant se trouvant dans le graphe
 * Supprime toutes les figures liées au composant à supprimer (défait les produits fusion)
 * supprimer les produits simples et le composant lui-même mais uniquement sur le diagramme
 * et pas dans le modèle (arbre)
 */
public class CSupprimerComposantGrapheDyn extends CommandeAnnulable
{
	
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
	public CSupprimerComposantGrapheDyn (ComposantCellDyn compo)
	{
		// initialiser le composant à supprimer
		this.composantCellDyn = compo;
		this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		
	}
	
	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * Parcours la liste des produits du composant, vérifie s'il n'y a pas
	 * de produits fusion "à défusionner", supprime les figures des produits et du composant
	 * @return true si l'export s'est bien passé false sinon
	 */
	public boolean executer()
	{
		Vector vecObj = new Vector();
		VueDPGraphe diagrame = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		GraphModelView graph = (GraphModelView)diagrame.getModel();
		ComposantCellDyn cell = this.composantCellDyn;
		
		//	Enleve le ComposantCellElementDyn (le haut de la branche) et son point d'attache
		vecObj.add( ((ComposantCellDyn) cell).getComposantCellElementDyn());
		vecObj.add(((ComposantCellDyn) cell).getComposantCellElementDyn().getPortComp());
			
		// On supprime tout les DocumentCellDyn ratachés à cell
		Vector docs = cell.getDocuments();
		for (int i = (docs.size()-1) ; i >= 0; i--) {			
			
			DocumentCellDyn doc = (DocumentCellDyn)docs.get(i);
			vecObj.add(doc);
			vecObj.add(doc.getPortComp());
			Vector v = doc.getListeLien();
			
			// On supprime tous les niveau des LiensEdgeDyn qui sont sur le doc
			if(v.size()!=0){
				//System.out.println("doc.getNiveau: "+doc.getNiveau());
				 for(int j=0;j<graph.getComposantCellCells().size();j++){
					 ((ComposantCellDyn)graph.getComposantCellCells().elementAt(j)).supprimerNiveauLigneDeVie(doc.getNiveau());
				 }
			}
			
		}
			
		
		//	On supprime toutes les lignes de vies (LigneEdgeDyn)
		for (int i = 0; i < ((ComposantCellDyn)cell).getLignesEdges().size(); i++) {
			vecObj.add(((ComposantCellDyn) cell).getLignesEdges().get(i));
		}
		
		// On supprime réelement tout les cellules et lien
		cell.removeAllChildren();
		graph.remove(vecObj.toArray());
		diagrame.supprimerCellule(cell);
		
		// On réorganise les composant restant
		for(int i = 0;i<graph.getComposantCellCells().size();i++){
			
			Map allmap = new HashMap();
			
			ComposantCellDyn cellDyn = ((ComposantCellDyn)graph.getComposantCellCells().elementAt(i));
			
			// Maj coordonnee de la cellule ComposantCellDyn
			cellDyn.setAbscisse(50+i*200);
			cellDyn.setOrdonnee(25);
			allmap.put(cellDyn,cellDyn.getAttributes());
			
			// Maj coordonnee de la cellule ComposantCellElementDyn
			cellDyn.getComposantCellElementDyn().setAbscisse(50+i*200);
			cellDyn.getComposantCellElementDyn().setOrdonnee(25);
			allmap.put(cellDyn.getComposantCellElementDyn(),cellDyn.getComposantCellElementDyn().getAttributes());
			
			// Ajout des nouvelle valeur dans le graph pour la prise en compte pour les documents
			graph.insert(null,allmap,null,cellDyn.getParentMap(),null);
			
			// Maj coordonnee de tous les document de la cellule ComposantCellDyn
			for (int j = 0; j < cellDyn.getDocuments().size(); j++){
				DocumentCellDyn doc = (DocumentCellDyn)cellDyn.getDocuments().get(j);
				doc.updatePosition();
				allmap.put(doc,doc.getAttributs());
			}
			
			//Ajout des nouvelle valeur dans le graph
			graph.insert(null,allmap,null,null,null);
		}
	
		
		
		
		diagramme.repaint();
		return true;
	}
	
	/**
	 * Recherche dans la liste des liens du composant le produitCourant
	 * pour vérifier s'il n'y a pas un produit fusion à supprimer
	 * défusionne le produit fusion s'il existe, supprime le lien interface et le produit
	 * simple recréé lorsqu'on supprimer un lien fusion
	 * @param produitCourant id du produit pour lequel on vérifie s'il fait partie d'un produit fusion
	 * @param listeLiens liens des liens dans le modèle du composant à supprimer
	 */
	public void supprimerFusion(IdObjetModele produitCourant, Vector listeLiens)
	{
		// vérifier que les produits ne soient pas dans des produits fusions
//		for (int j = 0; j < listeLiens.size(); j++)
//		{
//			// récupérer le lien courant
//			LienProduits lp = (LienProduits)listeLiens.elementAt(j);
//			if (lp.getProduitEntree().equals(produitCourant)
//					|| lp.getProduitSortie().equals(produitCourant))
//			{
//				CSupprimerLienFusion c = new CSupprimerLienFusion(this.diagramme, lp.getLienFusion());
//				c.executer();
//			}
//		}
//		
//		// supprimer le lien
//		FProduit fp = (FProduit)this.diagramme.contient(produitCourant);
//		this.diagramme.supprimerFigure(fp.getLienInterface());
//		// supprimer le produit
//		this.diagramme.supprimerFigure(this.diagramme.contient(produitCourant));
	}
}
