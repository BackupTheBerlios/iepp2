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
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.rendu.liens.LigneEdgeDyn;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.jgraph.graph.GraphConstants;

/**
 * @author Hubert
 *
 */
public class DocumentCellDyn extends IeppCell {

	// ICONES pour les documents
	protected String cheminImageDocument = Application.getApplication().getConfigPropriete("dossierImagesIepp");
	public static final String refImageProduit = "produit.png";
	public static final String refImageComposant = "composant.png";
	public static final String refImageProduitLier = "produitLie.png";
	
	// ETATS pour les documents
	protected static final int NESTPASLIE = 0;					// Etat signifiant que le document n'est pas relié à un autre
	protected static final int ESTSOURCE = 1;					// Etat signifiant que le document est à la source d'un lien classic
	protected static final int ESTDESTINATION = 2;				// Etat signifiant que le document est à la destination d'un lien classic
	protected static final int ESTSOURCEBITOUGNOU = 3;			// Etat signifiant que le document est à la source d'une boucle
	protected static final int ESTDESTINATIONBITOUGNOU = 4;		// Etat signifiant que le document est à la destination d'une boucle
	
	// Contexte du document
	protected ComposantCellDyn composant;
	protected Map attributs;
	protected LienEdge lien;
	protected int etat;
	
	// Apparence du document
	protected String imageDocument;
	protected ImageIcon i;
	protected boolean iconified;

	// Donnees du document
	protected int abscisse;
	protected int ordonnee;
	protected int largeur;
	protected int hauteur;
	protected int niveau;
	
	protected static int largeurMax = 34;
	protected static int hauteurMax = 44;
	/*
	// attributs de la classe
	protected Map attributs;
	protected Vector listeLien;
	protected String imageDocument;
	protected int abscisse;
	protected int ordonnee;
	protected int largeur;
	protected int hauteur;
	protected ImageIcon i;
	protected ComposantCellDyn composant;
	protected boolean iconified;
	protected int position;
	protected int largeurMax;
	protected int hauteurMax;*/
	protected LigneEdgeDyn ligneEdgeHaut;
	protected LigneEdgeDyn ligneEdgeBas;

// Constructeurs
	public DocumentCellDyn(ComposantCellDyn c) {
		super();
		listeLien = new Vector();
		attributs=GraphConstants.createMap();
		composant=c;
		iconified=false;
		niveau=getComposant().getDocuments().size()+1;
		setImageDocument(refImageProduit);
		largeur=largeurMax;
		hauteur=hauteurMax;//i.getIconHeight();
		GraphConstants.setSizeable(getAttributs(), false);
		GraphConstants.setMoveable(getAttributs(),false);
		GraphConstants.setBounds(getAttributs(),new Rectangle(
				getComposant().getComposantCellElementDyn().getAbscisse()+
				(getComposant().getComposantCellElementDyn().getLargeur()/2)-(getLargeur()/2),
				getComposant().getComposantCellElementDyn().getOrdonnee()+
				(getComposant().getComposantCellElementDyn().getHauteur()/2)+(DocumentCellDyn.hauteurMax/2)+( 70 * getNiveau() ),
				largeur,
				hauteur));
	}

	public void IconifierDocument(String refImage, int position)
	{
		iconified=true;
		
		
		setImageDocument(refImage);
		i = new ImageIcon(getCheminImageDocument()+ imageDocument);
		
		//Définition de l'image du composant
		File fimg = new File(cheminImageProduitIepp+ refImage);
		
		if(fimg.exists()){
			i = new ImageIcon(cheminImageProduitIepp+ refImage);
			this.imageComposant = refImage;
		}else{
			fimg = new File(cheminImageProduitUser+ refImage);
			
			if(fimg.exists()){
				i = new ImageIcon(cheminImageProduitUser+ refImage);
				this.imageComposant = refImage;
			}else{
				i = new ImageIcon(cheminImageProduitIepp+ refImageProduitLier);
				this.imageComposant = refImageProduitLier;
			}
		}
		
		
		this.largeur = largeurMax;
		this.hauteur = hauteurMax;
		
		GraphConstants.setIcon(getAttributs(), i);
		GraphConstants.setSize(getAttributs(),new Dimension(largeur,hauteur));
		//GraphConstants.setBounds(getAttributs(), new Rectangle(95,95,largeur,hauteur));
		GraphConstants.setBounds(getAttributs(),new Rectangle(getComposant().getComposantCellElementDyn().getAbscisse()+(getComposant().getComposantCellElementDyn().getLargeur()/2)-(getLargeur()/2),getComposant().getComposantCellElementDyn().getOrdonnee()+(getComposant().getComposantCellElementDyn().getHauteur()/2)+(DocumentCellDyn.hauteurMax/2)+( 70 * getNiveau() ),getLargeur(),getHauteur()));
	}

	// Getteurs et Setteurs
	public void setAttributs(Map map) {
		attributs=map;
	}

	public Map getAttributs() {
		return(attributs);
	}

	public boolean isIconified() {
		return iconified;
	}

	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}

	public LienEdge getLien() {
		return lien;
	}

	public void setLien(LienEdge lien) {
		this.lien = lien;
	}

	public int getNiveau() {
		return niveau;
	}

	public void setNiveau(int niveau) {
		this.niveau = niveau;
	}

	public void setComposant(ComposantCellDyn composant) {
		this.composant = composant;
	}

	public void setIconified(boolean iconified) {
		this.iconified = iconified;

	}

	
	public void ajouterLien(LienEdge lien){
		listeLien.add(lien);
	}
	
	public void supprimerLien(LienEdge lien){
		listeLien.removeElement(lien);
	}
	
	public Vector getListeLien(){
		return listeLien;
	}
	
	public IdObjetModele getId(){
		return null;
	}
	
	// Méthodes spéciales
	public void setCheminImageDocument(String s) {
		cheminImageDocument=s;
	}
		
	public String getCheminImageDocument() {
		return(cheminImageDocument);
	}
	
	public String getImageDocument() {
		return imageDocument;
	}

	public void setImageDocument(String imageDocument) {
		this.imageDocument = imageDocument;
	}

	public ComposantCellDyn getComposant() {
		return composant;
	}

	public int getHauteurMax() {
		return hauteurMax;
	}

	public int getLargeurMax() {
		return largeurMax;
	}

	public LigneEdgeDyn getLigneEdgeBas() {
		//return (LigneEdgeDyn) getComposant().getLignesEdges().get(getNiveau());
		return ligneEdgeBas;
	}



	public LigneEdgeDyn getLigneEdgeHaut() {
		//return (LigneEdgeDyn) getComposant().getLignesEdges().get(getNiveau()-1);
		return ligneEdgeHaut;
	}


	public int getAbscisse() {
		return (int)(GraphConstants.getBounds(getAttributs()).getX());
	}


	public void setAbscisse(int abscisse) {
		this.abscisse = abscisse;
		GraphConstants.setBounds(getAttributs(), new Rectangle(abscisse,getOrdonnee(),getLargeur(),getHauteur()));
	}


	public int getHauteur() {
		return hauteur;//(int)(GraphConstants.getBounds(getAttributs()).getHeight());
	}


	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),getOrdonnee(),getLargeur(),hauteur));
	}
	
	public int getLargeur() {
		return largeur;//(int)(GraphConstants.getBounds(getAttributs()).getWidth());
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
	
	public void updatePosition() {
			GraphConstants.setBounds(getAttributs(),new Rectangle(
				getComposant().getComposantCellElementDyn().getAbscisse()+
				(getComposant().getComposantCellElementDyn().getLargeur()/2)-(getLargeur()/2),
				getComposant().getComposantCellElementDyn().getOrdonnee()+
				(getComposant().getComposantCellElementDyn().getHauteur()/2)+(DocumentCellDyn.hauteurMax/2)+( 70 * getNiveau() ),
				getLargeur(),getHauteur()));
	}

	public void setLigneEdgeBas(LigneEdgeDyn ligneEdgeBas) {
		this.ligneEdgeBas = ligneEdgeBas;
	}

	public void setLigneEdgeHaut(LigneEdgeDyn ligneEdgeHaut) {
		this.ligneEdgeHaut = ligneEdgeHaut;
	}
	
}
