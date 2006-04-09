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

package iepp.ui.iedition.dessin.vues;

import iepp.Application;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.awt.event.MouseEvent;

import org.jgraph.JGraph;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphContext;

/**
 * 
 * @version $Revision: 1.1 $
 */
public class LienEdgeView extends EdgeView
{
	public LienEdgeView(Object cell, JGraph graph, CellMapper mapper) 
	{	
		super(cell, graph, mapper);
	}	
	
	public CellHandle getHandle(GraphContext context)
	{
		return new LienEdgeHandle(this, context);
	}
	
	

	public class LienEdgeHandle extends EdgeView.EdgeHandle
	{
		
		public LienEdgeHandle(EdgeView edge, GraphContext cxt) {
			super(edge, cxt);
		}
		
		public void mousePressed(MouseEvent event) {
			super.mousePressed(event);
		}
		
		public void mouseReleased(MouseEvent e)
		{
			LienEdge lien = (LienEdge)this.edge.getCell();
			lien.supprimerToutPointAncrage();
			for(int i=0 ; i<this.edge.getPoints().size() ; i++)	{
				lien.creerPointAncrage(this.edge.getPoint(i));
			}

			Application.getApplication().getProjet().setModified(true);
			
			super.mouseReleased(e);
		}
		
		public void mouseDragged(MouseEvent e)
		{
			super.mouseDragged(e);
		}
		
		public void mouseMoved(MouseEvent e)
		{
				super.mouseMoved(e);
		}
	}
}
