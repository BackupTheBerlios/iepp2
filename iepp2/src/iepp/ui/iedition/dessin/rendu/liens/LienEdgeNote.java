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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.GraphConstants;

/**
 * @author Stéphane
 *
 */
public class LienEdgeNote extends LienEdge {

	protected Map edgeAttribute;
	protected IeppCell source, destination;
	protected Vector pointsAncrage;
	
	/**
	 * 
	 */
	public LienEdgeNote() {
		super();
		
		pointsAncrage = new Vector();
		
		this.edgeAttribute = GraphConstants.createMap();
	
		GraphConstants.setLineEnd(edgeAttribute, GraphConstants.ARROW_NONE);
		GraphConstants.setEndFill(edgeAttribute, true);
		GraphConstants.setDashPattern(edgeAttribute, new float[] { 3, 3 });
		GraphConstants.setDisconnectable(edgeAttribute,false);
		GraphConstants.setEditable(edgeAttribute,false);
		
		this.source = null;
		this.destination = null;
	}
	
	public LienEdgeNote(IeppCell source, IeppCell destination) {
		super();
		
		pointsAncrage = new Vector();
		
		edgeAttribute = GraphConstants.createMap();
		
		GraphConstants.setLineEnd(edgeAttribute, GraphConstants.ARROW_NONE);
		GraphConstants.setEndFill(edgeAttribute, true);
		GraphConstants.setDashPattern(edgeAttribute, new float[] { 3, 3 });
		GraphConstants.setDisconnectable(edgeAttribute,false);
		GraphConstants.setEditable(edgeAttribute,false);
		
		this.source = source;
		this.destination = destination;
	}

	public LienEdgeNote(IeppCell source, IeppCell destination, ArrayList ancrages) {
		super();
		
		pointsAncrage = new Vector();
		for(int i = 0;i<ancrages.size();i++)	{
			creerPointAncrage((Point)ancrages.get(i));
		}
		
		edgeAttribute = GraphConstants.createMap();
		
		GraphConstants.setLineEnd(edgeAttribute, GraphConstants.ARROW_NONE);
		GraphConstants.setEndFill(edgeAttribute, true);
		GraphConstants.setDashPattern(edgeAttribute, new float[] { 3, 3 });
		GraphConstants.setDisconnectable(edgeAttribute,false);
		GraphConstants.setEditable(edgeAttribute,false);
		if(ancrages.size()>0) {
			GraphConstants.setPoints(edgeAttribute,ancrages);
		}
		
		this.source = source;
		this.destination = destination;
	}

	/**
	 * @param userObject
	 */
	public LienEdgeNote(Object userObject) {
		super(userObject);
		
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LienEdgeNote(Object arg0, boolean arg1) {
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
	public IeppCell getDestination() {
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
	public IeppCell getSourceEdge() {
		return source;
	}

	/**
	 * @param source The source to set.
	 */
	public void setSourceEdge(IeppCell source) {
		this.source = source;
	}
	
}
