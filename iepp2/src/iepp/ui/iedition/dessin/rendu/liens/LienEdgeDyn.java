package iepp.ui.iedition.dessin.rendu.liens;

import iepp.ui.iedition.dessin.rendu.IeppCell;

import java.awt.Color;
import java.awt.Point;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.GraphConstants;

public class LienEdgeDyn extends LienEdge {
	protected Map edgeAttribute;
	protected String textAssocie;
	protected String image;
	//private Point Depart,Arrivee;
	
	
	public LienEdgeDyn(String nom,String i,IeppCell source, IeppCell destination) {
		super();
		
		pointsAncrage = new Vector();
		
		textAssocie = nom;
		image=i;
		
		edgeAttribute = GraphConstants.createMap();
		
		GraphConstants.setLineEnd(edgeAttribute, GraphConstants.ARROW_CLASSIC);
		GraphConstants.setBendable(edgeAttribute, false);
		GraphConstants.setEndFill(edgeAttribute, true);
		GraphConstants.setDisconnectable(edgeAttribute,false);
		GraphConstants.setEditable(edgeAttribute,false);
		GraphConstants.setLabelPosition(edgeAttribute,new Point(450,-100));
		GraphConstants.setForeground(edgeAttribute,Color.black);
		GraphConstants.setOpaque(edgeAttribute,false);
		GraphConstants.setBackground(edgeAttribute,Color.white);
		GraphConstants.setValue(edgeAttribute,this.textAssocie);
		
		this.source = source;
		this.destination = destination;
	}

	/**
	 * @param userObject
	 */
	public LienEdgeDyn(Object userObject) {
		super(userObject);
		
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LienEdgeDyn(Object arg0, boolean arg1) {
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

	public String getTextAssocie() {
		return textAssocie;
	}

	public void setTextAssocie(String textAssocie) {
		this.textAssocie = textAssocie;
	}

	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

//	public Point getArrivee() {
//		return Arrivee;
//	}
//
//	public void setArrivee(Point arrivee) {
//		Arrivee = arrivee;
//	}
//
//	public Point getDepart() {
//		return Depart;
//	}
//
//	public void setDepart(Point depart) {
//		Depart = depart;
//	}
	
	

}
