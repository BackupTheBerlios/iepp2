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
import iepp.application.aedition.CSupprimerProduitFusion;
import iepp.ui.iedition.FenetreChoixImage;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * Classe permettant d'afficher un popupmenu contextuel lorsque l'utilisateur
 * clique droit sur un lien de fusion entre deux produits
 */
public class PopupFusion extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	//private JMenuItem 	
	private JMenuItem suppr;
	private JMenuItem changerImage;
	
	private VueDPGraphe diagramme;

	/**
	* Lien sur lequel on a cliqué.
	*/
	private ProduitCellFusion produitF;

   /**
	* coordonnées du click droit
	*/
	private int clickX, clickY;
		 
	
	/**
	 * Création du menu contextuel
	 */
	public PopupFusion(VueDPGraphe d,ProduitCellFusion f,int clickX, int clickY )
	{
		diagramme = d;
		this.produitF = f;
		this.clickX = clickX;
		this.clickY = clickY;
		
		// création des items
		this.suppr = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Lien"));
		this.changerImage = new JMenuItem(Application.getApplication().getTraduction("Changer_Image_prod"));

		// ajouter les items au menu
		this.add(this.suppr);
		this.add(this.changerImage);
		
		
		// pouvoir réagr aux clicks des utilisateurs
		this.suppr.addActionListener(this);
		this.changerImage.addActionListener(this);

	}
	
	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == this.suppr)
		{
			CSupprimerProduitFusion c = new CSupprimerProduitFusion(this.diagramme,this.produitF);
			if (c.executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}  	
		}else  if (event.getSource() == this.changerImage){
			//on affiche la fenetre de choix d'image et on change l'image du produit
			 
			 FenetreChoixImage fen = new FenetreChoixImage(IeppCell.cheminImageProduitUser);
			 if(fen.afficheDialog()){
				 Map allAtt = new HashMap();
				 this.produitF.setImageComposant(fen.nomImages());
				 allAtt.put(this.produitF,this.produitF.getAttributs());
				 this.diagramme.getModel().insert(null,allAtt,null,null,null);
			 }
			
        }
	 }
}
