package iepp.application.aedition.aoutil;

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

import iepp.Application;
import iepp.application.aedition.CLier2Produits;
import iepp.application.aedition.CLierCellNote;
import iepp.application.aedition.CLierProduitFusionProduit;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.popup.PopupDiagramme;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Vector;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

/**
 * This tool allows to create edges in the graph It use the prototype design
 * pattern to clone edges
 * 
 * @version $Revision: 1.1 $
 */
public class OLier2Elements {
	protected VueDPGraphe mGraph;

	
	protected EdgeHandler mHandler = new EdgeHandler();

	protected boolean mStable = true;

	/**
	 * Build a new EdgeTool
	 * 
	 */
	public OLier2Elements() {
		
	}

	public void install(JGraph graph) {
		mGraph = (VueDPGraphe)graph;
		graph.setMarqueeHandler(mHandler);
		graph.setMoveable(false);
		graph.setSizeable(false);
		graph.setPortsVisible(true);
	}

	public void uninstall(JGraph graph) {
		mGraph = null;
		graph.setMarqueeHandler(new BasicMarqueeHandler());
		graph.setMoveable(true);
		graph.setSizeable(true);
		graph.setPortsVisible(false);
	}

	public boolean isStable() {
		boolean oldStable = mStable;
		if (!mStable) {
			mStable = true;
		}

		return oldStable;
	}

	protected class EdgeHandler extends BasicMarqueeHandler {
		protected PortView mPort, mFirstPort;

		protected Point mStart, mCurrent;

		public boolean isForceMarqueeEvent(MouseEvent e) {
			mPort = getSourcePortAt(e.getPoint());
			if (mPort != null && mGraph.isPortsVisible())
				return true;
			return false;
		}

		public void mousePressed(MouseEvent e) {
			if (mPort != null && !e.isConsumed() && mGraph.isPortsVisible()) {
				//fireToolStarted();
				mStart = mGraph.toScreen(mPort.getLocation(null));
				mFirstPort = mPort;
				e.consume();
			}
		}

		public void mouseReleased(MouseEvent e) {
			
	        super.mouseReleased(e);
	        
	        if (!((mGraph.getFirstCellForLocation(e.getX(), e.getY()) instanceof IeppCell)||(mGraph.getFirstCellForLocation(e.getX(), e.getY()) instanceof LienEdge)))
			{
				if (e.isPopupTrigger())
	          	{
					PopupDiagramme p = new PopupDiagramme(Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe(), e.getX(), e.getY());
				    p.show(mGraph,e.getX(),e.getY());
			 	}
			}
			 
			if (e != null && !e.isConsumed() && mPort != null
					&& mFirstPort != null && mFirstPort != mPort) {
			
				
		        Object cellSrc = mFirstPort.getParentView().getCell();
		        Object cellDes = mPort.getParentView().getCell();
		        
		        // Modification pour les lien avec les notes
		        if ( (cellSrc instanceof ProduitCell) && (cellDes instanceof ProduitCell) ){
		        	
		     		CLier2Produits c = new CLier2Produits(mGraph, (ProduitCell)cellDes, (ProduitCell)cellSrc, new Vector());
		             if (c.executer())
		             {
		     			
		     			Application.getApplication().getProjet().setModified(true);
		      		 }
		        }else if ( (cellSrc instanceof TextCell) || (cellDes instanceof TextCell) ){
		        	
		        	CLierCellNote c = new CLierCellNote(mGraph, (IeppCell)cellDes, (IeppCell)cellSrc);
		             if (c.executer())
		             {
		      			Application.getApplication().getProjet().setModified(true);
		      		 }
		        }else if ( (cellSrc instanceof ProduitCellFusion) || (cellDes instanceof ProduitCellFusion) ){
		        	
		        	CLierProduitFusionProduit c = new CLierProduitFusionProduit(mGraph, (IeppCell)cellDes, (IeppCell)cellSrc, new Vector());
		             if (c.executer())
		             {
		      			Application.getApplication().getProjet().setModified(true);
		      		 }
		        }
		        
		        
			}else{
				// reprendre l'outil de séléction
				Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
			}
			//fireToolFinished();
			mStart = null;
			mCurrent = null;
		}

		public void mouseDragged(MouseEvent e) {
			if (mStart != null && !e.isConsumed()) {
				Graphics g = mGraph.getGraphics();

				paintConnector(Color.black, mGraph.getBackground(), g);

				mPort = getTargetPortAt(e.getPoint());

				if (mPort != null) {
					mCurrent = mGraph.toScreen(mPort.getLocation(null));
				} else {
					mCurrent = mGraph.snap(e.getPoint());
				}

				paintConnector(mGraph.getBackground(), Color.black, g);

				e.consume();
			}

		}

		public void mouseMoved(MouseEvent e) {
			if (e != null && getSourcePortAt(e.getPoint()) != null
					&& !e.isConsumed() && mGraph.isPortsVisible()) {
				mGraph.setCursor(new Cursor(Cursor.HAND_CURSOR));
				e.consume();
			}
		}

		private PortView getSourcePortAt(Point point) {
			if (point == null || mGraph == null) {
				return null;
			}

			Point tmp = mGraph.fromScreen(new Point(point));

			return (PortView) mGraph.getPortViewAt(tmp.x, tmp.y);
		}

		private PortView getTargetPortAt(Point point) {
			Object cell = mGraph.getFirstCellForLocation(point.x, point.y);

			for (int i = 0; i < mGraph.getModel().getChildCount(cell); i++) {
				Object tmp = mGraph.getModel().getChild(cell, i);

				tmp = mGraph.getGraphLayoutCache().getMapping(tmp, false);

				if (tmp instanceof PortView && tmp != mFirstPort) {
					return (PortView) tmp;
				}
			}

			return getSourcePortAt(point);
		}

		private void paintConnector(Color fg, Color bg, Graphics g) {
			g.setColor(fg);
			g.setXORMode(bg);
			paintPort(mGraph.getGraphics());

			if (mFirstPort != null && mStart != null && mCurrent != null) {
				g.drawLine(mStart.x, mStart.y, mCurrent.x, mCurrent.y);
			}
		}

		private void paintPort(Graphics g) {
			if (mPort != null) {
				boolean o = (GraphConstants.getOffset(mPort.getAttributes()) != null);
				Rectangle r = (o) ? mPort.getBounds() : mPort.getParentView()
						.getBounds();
				r = mGraph.toScreen(new Rectangle(r));
				r.setBounds(r.x - 3, r.y - 3, r.width + 6, r.height + 6);
				mGraph.getUI().paintCell(g, mPort, r, true);
			}
		}
	}
}
