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
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;


/**
 * Commande annulable permettant de supprimer un composant se trouvant dans le graphe
 * Supprime toutes les figures liées au composant à supprimer (défait les produits fusion)
 * supprimer les produits simples et le composant lui-même mais uniquement sur le diagramme
 * et pas dans le modèle (arbre)
 */
public class CSupprimerComposantGraphe extends CommandeAnnulable
{
	
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
	public CSupprimerComposantGraphe (ComposantCell compo)
	{
		// initialiser le composant à supprimer
		this.composantCell = compo;
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
		Referentiel ref=Application.getApplication().getReferentiel();
		for(int i=0;i<ref.getNoeudComposants().getChildCount();i++)
		{
			if (((ElementReferentiel)ref.getNoeudComposants().getChildAt(i)).getNomElement().equals(this.composantCell.getNomCompCell()+Application.getApplication().getTraduction("Comp_Present")))
			{
				((ElementReferentiel)ref.getNoeudComposants().getChildAt(i)).setNomElement(this.composantCell.getNomCompCell());
				ref.majObserveurs(Referentiel.CHANGED);
			}
		}
		for (int k=0;k<this.diagramme.getElementsCell().size();k++)
		{
			//System.out.println(this.diagramme.getElementsCell().elementAt(k).toString());
			if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellEntree)
			{
				//System.out.println("produitEntre "+this.diagramme.getElementsCell().elementAt(k).toString());
				ProduitCellEntree prodE=(ProduitCellEntree)this.diagramme.getElementsCell().elementAt(k);
				if (prodE.getCompParent()==this.composantCell)
				{
					this.diagramme.supprimerCellule(prodE);
					k--;
				}
			}
			else if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellSortie)
			{
				//System.out.println("produitSortie "+this.diagramme.getElementsCell().elementAt(k).toString());
				ProduitCellSortie prodS=(ProduitCellSortie)this.diagramme.getElementsCell().elementAt(k);
				if (prodS.getCompParent()==this.composantCell)
				{
					this.diagramme.supprimerCellule(prodS);
					k--;
				}
			}
			else if (this.diagramme.getElementsCell().elementAt(k) instanceof ProduitCellFusion)
			{
				ProduitCellFusion prodF=(ProduitCellFusion)this.diagramme.getElementsCell().elementAt(k);
				
				if (prodF.getProduitCellSortie().getCompParent()==this.composantCell)
				{// Si le produit de sortie est celui du composant alors on affiche tous les prodEntrée
					 Vector vprodE=prodF.getProduitCellEntrees();
					 
					 for(int i=0; i<vprodE.size() ;i++){
						 this.diagramme.AfficherCelluleEntreeMasquee((ProduitCellEntree)vprodE.elementAt(i));
					 }
					 
					 //on suprime les cellule sortie et fusion
					 this.diagramme.supprimerCellule(prodF.getProduitCellSortie());
					 this.diagramme.supprimerCellule(prodF);
					 k=k-2;
					 
				}else{
					
					//pour savoir si le composant est présant dans au moins un des prodEntree
					boolean testAffichable = false;
					
					// Le produit est un produit entree
					for(int i=0; i<prodF.getProduitCellEntrees().size() ;i++){
						
						ProduitCellEntree prodEntree = (ProduitCellEntree)prodF.getProduitCellEntrees().elementAt(i);
						
						if (prodEntree.getCompParent()==this.composantCell)
						{
							testAffichable = true;
						}
					}
					
					if(testAffichable == true){
						for(int i=0; i<prodF.getProduitCellEntrees().size() ;i++){
							
							ProduitCellEntree prodEntree = (ProduitCellEntree)prodF.getProduitCellEntrees().elementAt(i);
							
							if (prodEntree.getCompParent()==this.composantCell)
							{// Si le produit entrée est celui du composant alors on affiche prod Sortie
								 ProduitCellSortie prodSortie=prodF.getProduitCellSortie();
								 this.diagramme.AfficherCelluleSortieMasquee(prodSortie);
								 
								 //	on suprime les cellule entree et fusion
								 this.diagramme.supprimerCellule(prodEntree);
								 this.diagramme.supprimerCellule(prodF);
								 k=k-2;
								
							}else{
								// On affiche toutes les autre entrees // a revoir
								this.diagramme.AfficherCelluleEntreeMasquee(prodEntree);
							}
						}
					}
					
					
					
				}
				
			
			}
		}
			
		this.diagramme.supprimerCellule(this.composantCell);
		// fin modif aldo nit 
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
