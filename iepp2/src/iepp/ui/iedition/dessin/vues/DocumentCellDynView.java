package iepp.ui.iedition.dessin.vues;

import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.IeppCell;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;

import javax.swing.ImageIcon;

import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

public class DocumentCellDynView extends VertexView {

	//Constructor for Superclass
	public DocumentCellDynView(Object cell, JGraph jg, CellMapper cm) {
		super(cell, jg, cm);
	}
	
	/**
	 * Returns the intersection of the bounding rectangle and the
	 * straight line between the source and the specified point p.
	 * The specified point is expected not to intersect the bounds.
	 */
	public Point getPerimeterPoint(Point source, Point p) 
	{
		Line2D.Double line1 = new Line2D.Double(p,getCenterPoint());
		Line2D.Double line2;
		Rectangle r = getBounds();
		
		
		/*
		 *        
		 * 0________1
		 * |        |
		 * |        |
		 * |   ici  |
		 * |    x   |
		 * |        |
		 * |        |
		 * |________|
		 * 3         2
		 * 
		 */
		if(((DocumentCellDyn)getCell()).isIconified()){
			
//			r = GraphConstants.getBounds(((DocumentCellDyn)getCell()).getAttributes());
//			ImageIcon i = new ImageIcon(((IeppCell)getCell()).getCheminImageComposant()+ ((IeppCell)getCell()).getImageComposant());
//			
//			int x[]=new int[4];
//			int y[]=new int[4];	
//			
//			x[0]= r.x + (r.width/2)-(i.getIconWidth()/2);
//			y[0]= r.y;
//			
//			x[1]= r.x + (r.width/2)+(i.getIconWidth()/2);
//			y[1]= r.y;
//			
//			x[2]= r.x + (r.width/2)+(i.getIconWidth()/2);
//			y[2]= r.y + (int)r.getHeight();
//			
//			x[3]= r.x + (r.width/2)-(i.getIconWidth()/2);
//			y[3]= r.y + (int)r.getHeight();
//			
//			
//			line2 = new Line2D.Double((double) x[0], (double) y[0], (double) x[1], (double) y[1]);
//			
//			if (line1.intersectsLine(line2) == true)
//			{
//				return calculeDuPoint(line1, line2);
//			}
//			
//			line2 = new Line2D.Double((double) x[1], (double) y[1], (double) x[2], (double) y[2]);
//			if (line1.intersectsLine(line2) == true)
//			{
//				return calculeDuPoint(line1, line2);
//			}
//			
//			line2 = new Line2D.Double((double) x[2], (double) y[2], (double) x[3], (double) y[3]);
//			if (line1.intersectsLine(line2) == true)
//			{
//				return calculeDuPoint(line1, line2);
//			}
//			
//			line2 = new Line2D.Double((double) x[3], (double) y[3], (double) x[0], (double) y[0]);
//			if (line1.intersectsLine(line2) == true)
//			{
//				return calculeDuPoint(line1, line2);
//			}
			
			return super.getPerimeterPoint(source,p);//getCenterPoint();
			
		}else{
			
			return getCenterPoint();
		}
	}

	public Point calculeDuPoint(Double line1, Double line2){
		
		double a1 = line1.getY2() - line1.getY1() ; 
		double b1 = line1.getX1() - line1.getX2() ; 
		double c1 = line1.getY1() * line1.getX2() - line1.getX1() * line1.getY2() ;
		double a2 = line2.getY2() - line2.getY1() ; 
		double b2 = line2.getX1() - line2.getX2() ; 
		double c2 = line2.getY1() * line2.getX2() - line2.getX1() * line2.getY2() ;
		double det = a1 * b2 - a2 * b1 ;
		
		double xout = (c2 * b1 - c1 * b2) / det ;
		double yout = (a2 * c1 - a1 * c2) / det ;
		
		return new Point((int)xout, (int)yout);
		
	}

	//Returns the Renderer for this View
	public CellViewRenderer getRenderer()
	{
		return renderer;
	}
	
}
