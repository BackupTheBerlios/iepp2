package iepp.ui.iedition.dessin.rendu.liens;

/* IEPP: Isi Engineering Process Publisher
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

import iepp.ui.iedition.dessin.rendu.IeppCell;

import java.util.Map;

import org.jgraph.graph.GraphConstants;

import util.Vecteur;

/**
 * @author Stéphane
 *
 */
public class LigneEdgeDyn extends LienEdge {

	protected Map edgeAttribute;
	protected IeppCell source, destination;
	
	/**
	 * 
	 */
	public LigneEdgeDyn() {
		super();
		
		this.edgeAttribute = GraphConstants.createMap();
		
		GraphConstants.setLineEnd(edgeAttribute, GraphConstants.ARROW_NONE);
		GraphConstants.setEndFill(edgeAttribute, true);
		GraphConstants.setDashPattern(edgeAttribute, new float[] { 5, 3, 2, 3 });
		GraphConstants.setDisconnectable(edgeAttribute,false);
		GraphConstants.setEditable(edgeAttribute,false);
		GraphConstants.setBendable(edgeAttribute,false);
		
		this.source = null;
		this.destination = null;
	}
	
	public LigneEdgeDyn(IeppCell source, IeppCell destination) {
		super();
		
		edgeAttribute = GraphConstants.createMap();
		
		GraphConstants.setLineEnd(edgeAttribute, GraphConstants.ARROW_NONE);
		GraphConstants.setEndFill(edgeAttribute, true);
		GraphConstants.setDashPattern(edgeAttribute, new float[] { 5, 3, 2, 3 });
		GraphConstants.setDisconnectable(edgeAttribute,false);
		GraphConstants.setEditable(edgeAttribute,false);
		GraphConstants.setBendable(edgeAttribute,false);
		
		this.source = source;
		this.destination = destination;
	}

	/**
	 * @param userObject
	 */
	public LigneEdgeDyn(Object userObject) {
		super(userObject);
		
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LigneEdgeDyn(Object arg0, boolean arg1) {
		super(arg0, arg1);
		
	}

	/**
	 * @return Returns the edgeAttribute.
	 */
	public Map getEdgeAttribute() {
		return edgeAttribute;
	}

	/**
	 * @param edgeAttribute The edgeAttribute to set.
	 */
	public void setEdgeAttribute(Map edgeAttribute) {
		this.edgeAttribute = edgeAttribute;
		changeAttributes(edgeAttribute);
	}

	/**
	 * @return Returns the destination.
	 */
	public IeppCell getDestinationDyn() {
		return destination;
	}

	/**
	 * @param destination The destination to set.
	 */
	public void setDestination(IeppCell destination) {
		this.destination = destination;
	}

	/**
	 * @return Returns the source.
	 */
	public IeppCell getSourceEdgeDyn() {
		return source;
	}

	/**
	 * @param source The source to set.
	 */
	public void setSourceEdge(IeppCell source) {
		this.source = source;
	}
	
	public void creerPointAncrage(Vecteur points){
		pointsAncrage.add(points);
	}

}
