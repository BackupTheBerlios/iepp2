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
import iepp.application.aedition.CSupprimerProduit;
import iepp.ui.FenetreRenommerProduit;
import iepp.ui.iedition.FenetreChoixImage;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jgraph.graph.GraphConstants;

/**
 * 
 */

public class PopupProduit extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	private JMenuItem masque;
	private JMenuItem changerImage;
	private JMenuItem supprimerEntree;
	private JMenuItem supprimerSortie;
	private JMenuItem renommer ;
	
	private VueDPGraphe diagramme;
	
	private ProduitCell cell ;
	
	
	/**
	 * @param note
	 */
	public PopupProduit(VueDPGraphe d,ProduitCell cell)
	{
		this.diagramme = d;
		
		this.cell = cell;
		
		// création des items
		this.masque = new JMenuItem(Application.getApplication().getTraduction("Masquer"));
		this.changerImage = new JMenuItem(Application.getApplication().getTraduction("Changer_Image_prod"));
		this.supprimerEntree = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Produit"));
		this.supprimerSortie = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Produit"));
		this.renommer = new JMenuItem(Application.getApplication().getTraduction("Renommer"));
		
		// ajouter les items au menu
		this.add(this.masque);
		this.add(this.changerImage);
		
		if(cell instanceof ProduitCellEntree){
			ProduitCellEntree pe = (ProduitCellEntree)cell;
			if(pe.getCompParent().getCompProc().estVide()){
				this.addSeparator();
				this.add(this.supprimerEntree);
				this.add(this.renommer);
			}
		}else if(cell instanceof ProduitCellSortie){
			ProduitCellSortie pS = (ProduitCellSortie)cell;
			if(pS.getCompParent().getCompProc().estVide()){
				this.addSeparator();
				this.add(this.supprimerSortie);
				this.add(this.renommer);
			}
		}
		
		
		// pouvoir réagr aux clicks des utilisateurs
		this.masque.addActionListener(this);
		this.changerImage.addActionListener(this);
		this.supprimerEntree.addActionListener(this);
		this.supprimerSortie.addActionListener(this);
		this.renommer.addActionListener(this);
	}
	
	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		 if (event.getSource() == this.masque)
		 {
		 	this.cell.masquer();
		 	this.diagramme.MasquerCellule(cell);
		 	Application.getApplication().getProjet().setModified(true);
		 	this.diagramme.repaint();
		 }
		 if (event.getSource() == this.changerImage)
         {//on affiche la fenetre de choix d'image et on change l'image du produit
			 
			 FenetreChoixImage fen = new FenetreChoixImage(IeppCell.cheminImageProduitUser);
			 if(fen.afficheDialog()){
				 Map allAtt = new HashMap();
				 this.cell.setImageComposant(fen.nomImages());
				 allAtt.put(this.cell,this.cell.getAttributs());
				 this.diagramme.getModel().insert(null,allAtt,null,null,null);
			 }
			
         }
		 if (event.getSource() == this.supprimerEntree)
		 {
			 CSupprimerProduit c = new CSupprimerProduit (this.cell.getId(),1);
			 if (c.executer())
			 {
			   	Application.getApplication().getProjet().setModified(true);
			 }
		 }
		 if (event.getSource() == this.supprimerSortie)
		 {
			 CSupprimerProduit c = new CSupprimerProduit (this.cell.getId(),2);
			 if (c.executer())
			 {
			   	Application.getApplication().getProjet().setModified(true);
			 }
		 }
		 if (event.getSource() == this.renommer)
		 {
			 FenetreRenommerProduit fp = new FenetreRenommerProduit(Application.getApplication().getFenetrePrincipale(), this.cell.getId());
			 fp.pack();
			 fp.show();
		 }
	 }
}
