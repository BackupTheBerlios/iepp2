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

package iepp.ui.iedition.popup;


import iepp.Application;
import iepp.application.aedition.CAjouterProduitEntree;
import iepp.application.aedition.CAjouterProduitSortie;
import iepp.application.aedition.CSupprimerComposant;
import iepp.application.aedition.DialogRenommerComposant;
import iepp.ui.FenetreRenommerProduit;
import iepp.ui.iedition.FenetreChoixImage;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;



/**
 *
 */
public class PopupComposantProcessus extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	private JMenuItem supprimer;
	private JMenuItem afficher;
	private JMenuItem changerImage;
	private JMenuItem renommer ;
	
	private JMenuItem selectionner;
	
	private JMenuItem ajoutProEntree;
	private JMenuItem ajoutProSortie;
	
	/**
	* composant sur lequel on a cliqué.
	*/
	// modif aldo 15/01/06
	private ComposantCell compo;
	
	private VueDPGraphe diagramme;

	/**
	 * Création du menu contextuel
	 */
	public PopupComposantProcessus(VueDPGraphe d,ComposantCell cp)
	{
		this.compo = cp;

		this.diagramme = d;
		
		// création des items
		this.supprimer = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Composant"));
		this.afficher = new JMenuItem(Application.getApplication().getTraduction("Afficher_Produit"));
		this.changerImage = new JMenuItem(Application.getApplication().getTraduction("Changer_Image_comp"));
		this.renommer = new JMenuItem(Application.getApplication().getTraduction("Renommer_Composant"));
		
		this.selectionner = new JMenuItem(Application.getApplication().getTraduction("Selectionner_Comp"));
		
		this.ajoutProEntree = new JMenuItem(Application.getApplication().getTraduction("Ajouter_produit_entree"));
		this.ajoutProSortie = new JMenuItem(Application.getApplication().getTraduction("Ajouter_produit_sortie"));

		// ajouter les items au menu
		this.add(this.supprimer);
		this.add(this.changerImage);
		/*if(this.compo.getMdcomp().getId().estComposantVide())
        {
			this.add(this.renommer);//modif 2xmi youssef
        }*/
		
		if(this.compo.isContientProdMasquer())
        {
			this.add(this.afficher);
        }
		
		this.addSeparator();
		this.add(this.selectionner);
		
		if(compo.getCompProc().estVide()){
			this.addSeparator();
			this.add(this.ajoutProEntree);
			this.add(this.ajoutProSortie);
			this.add(this.renommer);
		}

		// pouvoir réagr aux clicks des utilisateurs
		this.supprimer.addActionListener(this);
		this.afficher.addActionListener(this);
		this.changerImage.addActionListener(this);
		this.selectionner.addActionListener(this);
		this.ajoutProEntree.addActionListener(this);
		this.ajoutProSortie.addActionListener(this);
		this.renommer.addActionListener(this);
	}

	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		 if (event.getSource() == this.supprimer)
		 {
			CSupprimerComposant c = new CSupprimerComposant(this.compo);
			if (c.executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}
		 }
		 if (event.getSource() == this.afficher)
         {//on affiche tout les produits masques du composant
			 // pour tout les produit en entree
			 for(int i = 0; i < ((GraphModelView)this.diagramme.getModel()).getProduitCellEntreeCells().size() ;i++){
				 ProduitCellEntree prodE = (ProduitCellEntree)((GraphModelView)this.diagramme.getModel()).getProduitCellEntreeCells().elementAt(i);
				 //on verifie si il appartienent bien au composant
				 if (prodE.getCompParent().equals(this.compo)){
					 // on verifi qu'il sont bien masque
					 if(prodE.isMasquer())
					 {
						 //Si il ne sont pas lier , on peut les afficher
						 if(!prodE.isCellLiee()){
							 this.diagramme.AfficherCelluleMasquee(prodE);
						 }
						 // on met leur propriété a demasque
						 prodE.deMasquer();
					 }
				 }
			 }
			 for(int i = 0; i < ((GraphModelView)this.diagramme.getModel()).getProduitCellSortieCells().size() ;i++){
				 ProduitCellSortie prodS = (ProduitCellSortie)((GraphModelView)this.diagramme.getModel()).getProduitCellSortieCells().elementAt(i);
				 if (prodS.getCompParent().equals(this.compo)){
					
					 if (prodS.isMasquer()){
						 if(!prodS.isCellLiee()){
							this.diagramme.AfficherCelluleMasquee(prodS);
						 }
						 prodS.deMasquer();
					 }
				 }
			 }
			 this.compo.setEnleverProdMasquer();
			 Application.getApplication().getProjet().setModified(true);
         }
		 if (event.getSource() == this.changerImage)
         {//on affiche la fenetre de choix d'image et on change l'image du composant
			 
			 FenetreChoixImage fen = new FenetreChoixImage(IeppCell.cheminImageComposantUser);
			 if(fen.afficheDialog()){
				 Map allAtt = new HashMap();
				 this.compo.setImageComposant(fen.nomImages());
				 allAtt.put(this.compo,this.compo.getAttributs());
				 this.diagramme.getModel().insert(null,allAtt,null,null,null);
			 }
			
         }
         if (event.getSource() == this.ajoutProEntree)
         {
        	 CAjouterProduitEntree c = new CAjouterProduitEntree (this.compo.getId());
				if (c.executer())
				{
					Application.getApplication().getProjet().setModified(true);
				}
        	 
         }
         if (event.getSource() == this.ajoutProSortie)
         {
        	 CAjouterProduitSortie c = new CAjouterProduitSortie (this.compo.getId());
				if (c.executer())
				{
				   		Application.getApplication().getProjet().setModified(true);
				}
         }
         if (event.getSource() == this.selectionner)
         {
        	 GraphModelView graph = (GraphModelView)diagramme.getModel();
        	 Vector select = new Vector();
        	 Vector entree = graph.getProduitCellEntreeCells();
        	 Vector sortie = graph.getProduitCellSortieCells();
        	 
        	 select.add(this.compo);
        	 
        	 for(int i=0;i<entree.size();i++){
        		ProduitCellEntree pe = (ProduitCellEntree)entree.elementAt(i);
        		 if(pe.getCompParent().equals(this.compo)){
        			 if(!pe.isCellLiee()&&!pe.isMasquer()){
        				 select.add(pe);
        			 }
        		 }
        	 }
        	 
        	 for(int i=0;i<sortie.size();i++){
         		ProduitCellSortie ps = (ProduitCellSortie)sortie.elementAt(i);
         		 if(ps.getCompParent().equals(this.compo)){
         			if(!ps.isCellLiee()&&!ps.isMasquer()){
         				select.add(ps);
         			}
         		 }
         	 }
        	 
        	 diagramme.setSelectionCells(select.toArray());
				
         }
         if (event.getSource() == this.renommer)
		 {
        	 DialogRenommerComposant c=new DialogRenommerComposant(Application.getApplication().getFenetrePrincipale(), this.compo.getId());
		 }

	 }
}
