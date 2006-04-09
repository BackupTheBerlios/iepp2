package iepp.ui.iedition.dessin.rendu;

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
import iepp.domaine.IdObjetModele;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;

import javax.swing.ImageIcon;

import org.jgraph.graph.GraphConstants;

public class ProduitCell extends IeppCell {
	
	protected int abscisse;
	protected int ordonnee;
	protected int largeur;
	protected int hauteur;
	protected IdObjetModele produit ;
	protected ImageIcon i;
	protected boolean masquer;
    
	public ProduitCell(IdObjetModele prod, int x, int y ) {
		
		super(prod.getRef().toString(prod.getNumRang(), prod.getNumType()));
		
		this.produit = prod;
		this.police = new Font("Arial", Font.PLAIN, 12);
		
		this.imageComposant = refImageProduit;
		this.masquer = false;
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		this.abscisse=x;
		this.ordonnee=y;		
		
		// Définition de l'image
		i = new ImageIcon(cheminImageProduitIepp+ imageComposant);
		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		Rectangle2D dim = this.police.getStringBounds(this.getNomCompCell(),new FontRenderContext(new AffineTransform(),false,false));
		this.largeur = Math.max(i.getIconWidth(), (int)dim.getWidth()) + 1;
		this.hauteur=i.getIconHeight()+(int)dim.getHeight() + 7;
		
		// Définition des attributs de la cellule
		GraphConstants.setIcon(getAttributs(), i);
		Rectangle r = new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur);
		GraphConstants.setBounds(getAttributs(), r);
		GraphConstants.setEditable(getAttributs(), false);
		GraphConstants.setSizeable (getAttributs(), false);
		GraphConstants.setFont(getAttributs(),this.police);		
	}

	public ProduitCell(IdObjetModele prod, int x, int y, int l, int h, String imageprod ) {
		
		super(prod.getRef().toString(prod.getNumRang(), prod.getNumType()));
		
		this.produit = prod;
		this.police = new Font("Arial", Font.PLAIN, 12);
		
		this.masquer = false;
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		this.abscisse=x;
		this.ordonnee=y;		
		
		// Définition de l'image du composant
		File fimg = new File(cheminImageProduitIepp+ imageprod);
		
		if(fimg.exists()){
			i = new ImageIcon(cheminImageProduitIepp+ imageprod);
			this.imageComposant = imageprod;
		}else{
			fimg = new File(cheminImageProduitUser+ imageprod);
			
			if(fimg.exists()){
				i = new ImageIcon(cheminImageProduitUser+ imageprod);
				this.imageComposant = imageprod;
			}else{
				i = new ImageIcon(cheminImageProduitIepp+ refImageProduit);
				this.imageComposant = refImageProduit;
			}
		}
		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		this.largeur = l;
		this.hauteur=h;
		
		// Définition des attributs de la cellule
		GraphConstants.setIcon(getAttributs(), i);
		Rectangle r = new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur);
		GraphConstants.setBounds(getAttributs(), r);
		GraphConstants.setEditable(getAttributs(), false);
		GraphConstants.setSizeable (getAttributs(), false);
		GraphConstants.setFont(getAttributs(),this.police);		
	}

	public IdObjetModele getId()
    {
    	return this.produit;
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

	public String getImageComposant() {
		return imageComposant;
	}

	public void setImageComposant(String imageComposant) {
		
		// Définition de l'image du composant
		File fimg = new File(cheminImageProduitIepp+ imageComposant);
		
		if(fimg.exists()){
			i = new ImageIcon(cheminImageProduitIepp+ imageComposant);
			this.imageComposant = imageComposant;
		}else{
			fimg = new File(cheminImageProduitUser+ imageComposant);
			
			if(fimg.exists()){
				i = new ImageIcon(cheminImageProduitUser+ imageComposant);
				this.imageComposant = imageComposant;
			}else{
				i = new ImageIcon(cheminImageProduitIepp+ refImageProduit);
				this.imageComposant = refImageProduit;
			}
		}
		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		Rectangle2D dim = this.police.getStringBounds(this.getNomCompCell(),new FontRenderContext(new AffineTransform(),false,false));
		this.largeur = Math.max(i.getIconWidth(), (int)dim.getWidth()) + 1;
		this.hauteur=i.getIconHeight()+(int)dim.getHeight() + 7;
		
		// Définition des attributs du composant
		GraphConstants.setIcon(getAttributs(), i);
		GraphConstants.setBounds(getAttributs(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
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
	
	public void setNomCompCell(String s) {
		this.nomComposantCellule=s;
		
		// On garde aussi une trace de la largeur et de la hauteur du composant
		Rectangle2D dim = this.police.getStringBounds(this.getNomCompCell(),new FontRenderContext(new AffineTransform(),false,false));
		this.largeur = Math.max(i.getIconWidth(), (int)dim.getWidth()) + 1;
		this.hauteur=i.getIconHeight()+(int)dim.getHeight() + 7;
		
		this.setUserObject(nomComposantCellule);
		
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse() - ((largeur - getLargeur())/2) ,getOrdonnee(),largeur,hauteur));
	}
	
	public void masquer(){
		this.masquer = true;
	}
	
	public void deMasquer(){
		this.masquer = false;
	}
	
	public boolean isMasquer(){
		return this.masquer;
	}
}
