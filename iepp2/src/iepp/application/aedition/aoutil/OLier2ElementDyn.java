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
import iepp.ui.iedition.FenetreCreationLienDynamique;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

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
public class OLier2ElementDyn {
	protected VueDPGraphe mGraph;

	
	protected EdgeHandler mHandler = new EdgeHandler();

	protected boolean mStable = true;
	
	

	/**
	 * Build a new EdgeTool
	 * 
	 */
	public OLier2ElementDyn() {
		
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
		protected ComposantCellDyn compoS,compoD;
		
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
				if (mGraph.getFirstCellForLocation(e.getX(), e.getY()) instanceof ComposantCellDyn){
					this.compoS=(ComposantCellDyn)mGraph.getFirstCellForLocation(e.getX(), e.getY());
				}
				e.consume();
			}
		}

		public void mouseReleased(MouseEvent e) {
			
	        super.mouseReleased(e);
	        
	        if (mGraph.getFirstCellForLocation(e.getX(), e.getY()) instanceof ComposantCellDyn){
	        		
	        	
	        	
	        	if (e != null && !e.isConsumed() && mPort != null
						&& mFirstPort != null && mFirstPort != mPort) {
					
		        	this.compoD=(ComposantCellDyn)mGraph.getFirstCellForLocation(e.getX(), e.getY());
		        	
		        	Object cellSrc = mFirstPort.getParentView().getCell();
		        	
			       
			        Object cellDes = mPort.getParentView().getCell();
			    
			        // on regarde si on lie bien des documents
			        if ((cellSrc instanceof DocumentCellDyn)&&(cellDes instanceof DocumentCellDyn))
			        {
			        
				        // l'algorithme suivant permet de voir si les produit qu'on lie sur le diagramme sont au meme niveau
				        
				        int p=0;
				        int d=0;
				        for (int i=0;i<compoS.getDocuments().size();i++)
				        {	
				        	if (compoS.getDocuments().elementAt(i)==((DocumentCellDyn)cellSrc)){
				        		p=i;
				        	}
				        }
				        for (int i=0;i<compoD.getDocuments().size();i++)
				        {
				        	if (compoD.getDocuments().elementAt(i)==((DocumentCellDyn)cellDes)){
				        		d=i;
				        	}
				        }
				       if (p==d)
				       {
				    	   if ((p==compoS.getDocuments().size()-1) && (d==compoD.getDocuments().size()-1)){
				    	   
	
				    		   FenetreCreationLienDynamique fen=new FenetreCreationLienDynamique(Application.getApplication().getFenetrePrincipale(),mGraph, compoS,compoD,(DocumentCellDyn)cellDes, (DocumentCellDyn)cellSrc);
				    	   }
				       }
				       else
				       {
				    	   //erreur on ne peut pas lier ces composant
				       }
			       }
			        else{
			        	//erreur
			        }
		        }else{
		        	//reprendre l'outil de séléction
					//Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
		        }
	        }else{
	        	//reprendre l'outil de séléction
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
