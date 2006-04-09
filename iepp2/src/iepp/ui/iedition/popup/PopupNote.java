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
import iepp.ui.iedition.dessin.rendu.TextCell;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * 
 */

public class PopupNote extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	private JMenuItem suppr;
	//private JMenuItem propriete;
	
	private VueDPGraphe diagramme;
	
	private TextCell noteCell ;
	
	
	/**
	 * @param note
	 */
	public PopupNote(VueDPGraphe d,TextCell note)
	{
		this.diagramme = d;
		
		this.noteCell = note;
		
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
		 	this.diagramme.supprimerCellule(this.noteCell);
		 	Application.getApplication().getProjet().setModified(true);
		 	this.diagramme.repaint();
		 }
		
	 }
}
