package iepp.ui.iedition.dessin.rendu;

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

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;

import org.jgraph.graph.GraphConstants;

public class TextCell extends IeppCell {
	
	protected int abscisse;
	protected int ordonnee;
	protected int largeur;
	protected int hauteur;
	private String message;
	
	public TextCell( int x, int y ) {
		
		super();
	
		this.message="";
		
		this.police = new Font("Arial", Font.PLAIN, 12);
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		this.abscisse=x;
		this.ordonnee=y;		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		this.largeur=90;
		this.hauteur=40;
		
		// Mise en place des Attributs de la cellule text pas défaut
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
		GraphConstants.setEditable(getAttributs(), true);
		GraphConstants.setOpaque(getAttributs(), true);
		GraphConstants.setFont(getAttributs(),this.police);
		GraphConstants.setBackground(getAttributs(),new Color(255, 255, 204));
	 	GraphConstants.setBorder(getAttributs(),BorderFactory.createRaisedBevelBorder());
	}

	public TextCell( String texte, int x, int y, int l, int h ) {
		
		super(texte);
	
		this.message=texte;
		
		this.police = new Font("Arial", Font.PLAIN, 12);
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		this.abscisse=x;
		this.ordonnee=y;		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		this.largeur=l;
		this.hauteur=h;
		
		// Mise en place des Attributs de la cellule text pas défaut
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
		GraphConstants.setEditable(getAttributs(), true);
		GraphConstants.setOpaque(getAttributs(), true);
		GraphConstants.setFont(getAttributs(),this.police);
		GraphConstants.setBackground(getAttributs(),new Color(255, 255, 204));
		GraphConstants.setBorder(getAttributs(),BorderFactory.createRaisedBevelBorder());
	}
		
	public int getAbscisse() {
		return (int)(GraphConstants.getBounds(getAttributs()).getX());
	}


	public void setAbscisse(int abscisse) {
		this.abscisse = abscisse;
		GraphConstants.setBounds(getAttributs(), new Rectangle(abscisse,getOrdonnee(),getLargeur(),getHauteur()));
	}


	public int getHauteur() {
		return (int)(GraphConstants.getBounds(getAttributs()).getHeight());
	}


	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),getOrdonnee(),getLargeur(),hauteur));
	}


	public int getLargeur() {
		return (int)(GraphConstants.getBounds(getAttributs()).getWidth());
	}


	public void setLargeur(int largeur) {
		this.largeur = largeur;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),getOrdonnee(),largeur,getHauteur()));
	}


	public int getOrdonnee() {
		return (int)(GraphConstants.getBounds(getAttributs()).getY());
	}


	public void setOrdonnee(int ordonnee) {
		this.ordonnee = ordonnee;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),ordonnee,getLargeur(),getHauteur()));
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	
}
