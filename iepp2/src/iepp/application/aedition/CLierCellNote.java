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
import iepp.domaine.LienProduits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeNote;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.apache.batik.gvt.TextNode;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;


/**
 * Commande annulable permettant de lier 2 éléments (produit)
 * Selon certaines règles suivantes:
 */
public class CLierCellNote extends CommandeAnnulable
{

	/**
	 * Diagramme sur lequel on effectue la liaison
	 */
	private VueDPGraphe diagramme;

	/**
	 * ProduitCell des éléments sélectionnés pour effectuer la liaison
	 */
	private IeppCell cell;
	private TextCell note;
	
	private ArrayList pointsAncrage;
	
	/**
	* Constructeur de la commande à partir du diagramme sur lequel on va effectuer la liaison
	* les deux éléments que l'on veut fusionner et l'ensemble des points d'ancrage utilisés pour
	* faire la liaison (ne servent qu'à la liaison, ils n'apparaissent pas lorsque le produit fusion est créé)
	* @param d diagramme sur lequel on effectue la liaison
	* @param source figure sur laquelle on a cliqué en premier
	* @param destination figure sur laquelle on a cliqué en second
	* @param pointsAncrageIntermediaires liste des points d'ancrage créée lors de la liaison entre les deux figures
	*/
	public CLierCellNote(VueDPGraphe d,  IeppCell cellsource, IeppCell celldestination)
	{
		// garder un lien vers le diagramme
        this.diagramme = d;
        
        pointsAncrage = new ArrayList();
        
        // initiaisation
        if (cellsource instanceof TextCell){
        	this.note = (TextCell)cellsource;
        	this.cell = celldestination;
        }else{
        	this.note = (TextCell)celldestination;
        	this.cell = cellsource;
        }
	}

	public CLierCellNote(VueDPGraphe d, ArrayList pointsAncrageIntermediaires, IeppCell cellsource, IeppCell celldestination)
	{
		// garder un lien vers le diagramme
        this.diagramme = d;
        
        pointsAncrage = pointsAncrageIntermediaires;
        
        // initiaisation
        if (cellsource instanceof TextCell){
        	this.note = (TextCell)cellsource;
        	this.cell = celldestination;
        }else{
        	this.note = (TextCell)celldestination;
        	this.cell = cellsource;
        }
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
		
		if(this.cell instanceof TextCell) {
			return false;
		}

		this.diagramme.clearSelection();
		this.diagramme.setSelectionCells(null);
		
		LienEdgeNote edge = new LienEdgeNote(cell,note,pointsAncrage);
		note.ajoutLien(edge);
		cell.ajoutLien(edge);
		
		edge.setSourceEdge(note);
		edge.setDestination(cell);
		
		this.diagramme.ajouterLien(edge);
		Map AllAttribute = GraphConstants.createMap();

		AllAttribute.put(edge, edge.getEdgeAttribute());
		
		DefaultPort portS = note.getPortComp();
		DefaultPort portD = ((IeppCell) cell).getPortComp();
		
		ConnectionSet cs1 = new ConnectionSet(edge, portS,portD);
		
		Vector vecObj = new Vector();
		vecObj.add(edge);
		
		this.diagramme.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		this.diagramme.getModel().insert(null, null, cs1, null, null);
		
		
		this.diagramme.repaint();
		
		// reprendre l'outil de séléction
		//Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
	
		return true;

	}

}
