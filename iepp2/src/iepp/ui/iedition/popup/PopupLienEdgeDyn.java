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
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeDyn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Crée par Julie TAYAC le 06/02/06
 * Classe permettant d'afficher un popupmenu contextuel lorsque l'utilisateur
 * clique avec le bouton droit sur un lien entre deux Composants du diagramme 
 * dynamique
 */

public class PopupLienEdgeDyn extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	private JMenuItem suppr;
	//private JMenuItem propriete;
	
	private VueDPGraphe diagramme;
	
	private LienEdgeDyn lienEdgeDyn ;
	
	
	/**
	 * @param note
	 */
	public PopupLienEdgeDyn(VueDPGraphe d,LienEdgeDyn lien)
	{
		this.diagramme = d;
		
		this.lienEdgeDyn = lien;
		
		// création des items
		this.suppr = new JMenuItem(Application.getApplication().getTraduction("Supprimer"));
		//this.propriete = new JMenuItem(Application.getApplication().getTraduction("Proprietes"));
		
		// ajouter les items au menu
		this.add(this.suppr);
		//this.add(this.propriete);
		
		// pouvoir réagr aux clicks des utilisateurs
		this.suppr.addActionListener(this);
		//this.propriete.addActionListener(this);
	}
	
	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		 if (event.getSource() == this.suppr)
		 {
			 DocumentCellDyn docSource = ((DocumentCellDyn)(this.lienEdgeDyn.getSourceEdge()));
		 
			 GraphModelView graph = (GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel();
			 
			 for(int i=0;i<graph.getComposantCellCells().size();i++){
				 ((ComposantCellDyn)graph.getComposantCellCells().elementAt(i)).supprimerNiveauLigneDeVie(docSource.getNiveau());
			 }
			 
			 //System.out.println("Doc actionPerformed "+docSource.getNomCompCell()+" niveau : "+docSource.getNiveau());
			 Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().supprimerLien(this.lienEdgeDyn);
			 Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().remove(new Object[]{this.lienEdgeDyn});
		 	//this.diagramme.repaint();
			 
			
			 
		 }
		
	 }
}
