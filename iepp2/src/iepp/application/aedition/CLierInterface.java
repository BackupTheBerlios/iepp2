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

import iepp.application.CommandeNonAnnulable;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;


/**
 * Commande annulable permettant de lier 2 éléments (1 produit et un composant) avec un lien
 * en pointillé. Cette commande n'est pas appelée directement après un évènement utilisateur.
 * Elle n'est appelée que lorsqu'un composant est ajouté au diagramme
 * ou lorsqu'on supprime un lien fusion, ce lien est remplacé par le lien interface
 * d'origine
 */
public class CLierInterface extends CommandeNonAnnulable
{

	/**
	 * Diagramme sur lequel on effectue la liaison
	 */
	private VueDPGraphe diagramme;
	
	private Port portS;
	 
	private Port portD;
	
	private IeppCell cellS;
	
	private IeppCell cellD;
	
	private Map AllAttribute;
	
	private ArrayList pointsAncrage;
	
	/**
	* Constructeur de la commande, créé un lien entre un composant et un produit simple
	* Créer une nouvelle figure pour le lien
	* @param d diagramme sur lequel on effectue la liaison
	* @param l lien à créer entre le composant et le produit
	* @param source figure source d'où pour le lien
	* @param destination figure où arrive le lien
	* @param pointsAncrageIntermediaires liste des points d'ancrages du lien à créer
	*/
	public CLierInterface(VueDPGraphe d, Vector pointsAncrageIntermediaires, IeppCell ieppsource, IeppCell ieppdestination)
	{
		this.diagramme = d;
		
		pointsAncrage = new ArrayList();
		
		AllAttribute = GraphConstants.createMap();
		cellS = ieppsource;
		cellD = ieppdestination;
		portS = ieppsource.getPortComp();
		portD = ieppdestination.getPortComp();
	}
	
	public CLierInterface(VueDPGraphe d, ArrayList pointsAncrageIntermediaires, IeppCell ieppsource, IeppCell ieppdestination)
	{
		this.diagramme = d;
						
		pointsAncrage = pointsAncrageIntermediaires;
		
		AllAttribute = GraphConstants.createMap();
		cellS = ieppsource;
		cellD = ieppdestination;
		portS = ieppsource.getPortComp();
		portD = ieppdestination.getPortComp();
	}
	
	/**
	 * La commande renvoie toujours true
	 * Ajoute le nouveau lien créé entre le composant et le produit
	 * @return true 
	 */
	public boolean executer()
	{
		LienEdge lienComp = new LienEdge(cellS,cellD,pointsAncrage);
		  
		AllAttribute.put(lienComp, lienComp.getEdgeAttribute());
	
		Vector vecObj = new Vector();
		
		if (!diagramme.getModel().contains(cellS))
		{
			vecObj.add(cellS);
		}
		if (!diagramme.getModel().contains(cellD))
		{
			vecObj.add(cellD);
		}
		
		((IeppCell)cellS).ajoutLien(lienComp);
		((IeppCell)cellD).ajoutLien(lienComp);
		// On declare les sources et les destinations des liens
		lienComp.setSourceEdge((IeppCell) cellS);
		lienComp.setDestination((IeppCell) cellD);
		
        vecObj.add(lienComp);
        
        diagramme.ajouterLien( lienComp );
        
        ConnectionSet cs = new ConnectionSet(lienComp, portS, portD);
     
        diagramme.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		diagramme.getModel().insert(null, null, cs, null, null);
	        
		diagramme.show();
		
		return true;
	}
}
