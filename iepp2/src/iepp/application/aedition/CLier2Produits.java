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
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeFusion;

import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;


/**
 * Commande annulable permettant de lier 2 éléments (produit)
 * Selon certaines règles suivantes:
 */
public class CLier2Produits extends CommandeAnnulable
{

	/**
	 * Diagramme sur lequel on effectue la liaison
	 */
	private VueDPGraphe diagramme;

	
	/**
	 * Liens créés au niveau du modèle entre les produits
	 */
	private LienProduits lienModele1 ;
	
	/**
	 * Id des éléments sélectionnés pour effectuer la liaison
	 */
	private IdObjetModele src, dest, fusion;

	/**
	 * Indique si la fusion est possible ou pas
	 */
	private boolean executable ;

	/**
	 * ProduitCell des éléments sélectionnés pour effectuer la liaison
	 */
	private ProduitCell cellsource, celldestination;
		
	/**
	* Constructeur de la commande à partir du diagramme sur lequel on va effectuer la liaison
	* les deux éléments que l'on veut fusionner et l'ensemble des points d'ancrage utilisés pour
	* faire la liaison (ne servent qu'à la liaison, ils n'apparaissent pas lorsque le produit fusion est créé)
	* @param d diagramme sur lequel on effectue la liaison
	* @param source figure sur laquelle on a cliqué en premier
	* @param destination figure sur laquelle on a cliqué en second
	* @param pointsAncrageIntermediaires liste des points d'ancrage créée lors de la liaison entre les deux figures
	*/
	public CLier2Produits(VueDPGraphe d,  ProduitCell Cellsource, ProduitCell Celldestination, Vector pointsAncrageIntermediaires)
	{
		// garder un lien vers le diagramme
        this.diagramme = d;
        
        // initiaisation
        this.celldestination = Celldestination;
        this.cellsource = Cellsource;

		// si l'objet source est un produit en entrée on permute le sens du lien entrée
		this.src = Cellsource.getId();
		this.dest = Celldestination.getId();
		
	}



	/**
	* Retourne le nom de l'édition.
	*/
	public String getNomEdition()
	{
		return "Lier element";
	}


	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * Si la fusion est possible, créé les figures du produit fusion s'il n'existe pas
	 * créé les liens fusions entre les produits fusion et les composants concernés
	 * Créer les liens au niveau du modèle, chaque composant connait les liens entre
	 * ses produits et les produits des autres composants
	 * @return true si la liaison s'est bien passée false sinon
	 */
	public boolean executer()
	{
		/////////////////////////////////////////////
		// Ajout pour la prise en compte de JGraph //
		/////////////////////////////////////////////
		

		this.diagramme.clearSelection();
		this.diagramme.setSelectionCells(null);
		
		Object cellSrc = cellsource;
		Object cellDes = celldestination;

		Object cellEnt = null;
		Object cellSor = null;

		if (((cellSrc instanceof ProduitCellEntree) && (cellDes instanceof ProduitCellSortie))
				|| (cellSrc instanceof ProduitCellSortie)
				&& (cellDes instanceof ProduitCellEntree)) {
			// verif ke les 2 soit un produit de type differents

			if (cellDes instanceof ProduitCellEntree) {
				cellEnt = cellDes;
				cellSor = cellSrc;
			} else {
				cellEnt = cellSrc;
				cellSor = cellDes;
			}

			// On essaie de relier un produit en entree et en sortie d'un meme composant
			if (((ProduitCellEntree) cellEnt).getCompParent().equals(
					((ProduitCellSortie) cellSor).getCompParent())) {
				this.diagramme.repaint();
				return false;
			}

			LienEdgeFusion edge1 = new LienEdgeFusion();
			LienEdgeFusion edge2 = new LienEdgeFusion();

			if (src.estProduitSortie())
			{
				this.fusion = src;
			}
			else
			{
				this.fusion = dest;
			}
			
			ProduitCellFusion newProdCell = new ProduitCellFusion(this.fusion,(ProduitCellEntree)cellEnt,(ProduitCellSortie)cellSor);
			newProdCell.ajoutLien(edge1);
			newProdCell.ajoutLien(edge2);
			
			// On declare les sources et les destinations des liens
			edge1.setSourceEdge(((ProduitCellSortie) cellSor).getCompParent());
			edge1.setDestination(newProdCell);
			edge2.setSourceEdge(newProdCell);
			edge2.setDestination(((ProduitCellEntree) cellEnt).getCompParent());
			
			// On supprime les liens et on declare les cellules liees
			((ProduitCellEntree)cellEnt).setCellLiee(true);
			((ProduitCellSortie)cellSor).setCellLiee(true);
			
			this.diagramme.MasquerCellule((IeppCell)cellEnt);
			this.diagramme.MasquerCellule((IeppCell)cellSor);
			
			
			this.diagramme.ajouterCell(newProdCell);
			this.diagramme.ajouterLien(edge1);
			this.diagramme.ajouterLien(edge2);
			
			if (!((ProduitCell) cellSrc).getNomCompCell()
					.equalsIgnoreCase(
							((ProduitCell) cellDes).getNomCompCell())) {
				newProdCell.setNomCompCell(((ProduitCell) cellSrc)
						.getNomCompCell()
						+ "("
						+ ((ProduitCell) cellDes).getNomCompCell()
						+ ")");
			}

			Map AllAttribute = GraphConstants.createMap();

			AllAttribute.put(edge1, edge1.getEdgeAttribute());
			AllAttribute.put(edge2, edge2.getEdgeAttribute());
			AllAttribute.put(newProdCell, newProdCell.getAttributs());

			DefaultPort portS = ((ProduitCellSortie) cellSor)
					.getCompParent().getPortComp();
			DefaultPort portDInt = ((ProduitCellFusion) newProdCell)
					.getPortComp();
			DefaultPort portD = ((ProduitCellEntree) cellEnt)
					.getCompParent().getPortComp();

			ConnectionSet cs1 = new ConnectionSet(edge1, portS,
					portDInt);
			ConnectionSet cs2 = new ConnectionSet(edge2, portDInt,
					portD);

			this.lienModele1 = new LienProduits((((ProduitCellEntree)cellEnt).getId()), (((ProduitCellSortie)cellSor).getId()), edge1);
			((ComposantProcessus)this.lienModele1.getProduitSortie().getRef()).ajouterLien(this.lienModele1);
			
			Vector vecObj = new Vector();
			vecObj.add(newProdCell);
			vecObj.add(edge1);
			vecObj.add(edge2);

			this.diagramme.getModel().insert(vecObj.toArray(), AllAttribute,
					null, null, null);
			this.diagramme.getModel().insert(null, null, cs1, null, null);
			this.diagramme.getModel().insert(null, null, cs2, null, null);

			this.diagramme.setSelectionCell(newProdCell);
			
			this.diagramme.repaint();
			
			// reprendre l'outil de séléction
			//Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();

		} else {
			this.diagramme.repaint();
			// System.out.println("SOURCE & DESTINATION identiques");
		}
		
			return true;

	}

}
