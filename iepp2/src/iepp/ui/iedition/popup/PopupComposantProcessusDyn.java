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
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.application.aedition.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import iepp.domaine.*;



/**
 * Crée par Julie TAYAC le 27/01/06: Presque identique à PopupFComposantProcessusDyn.java
 * Classe permettant d'afficher un popupmenu contextuel lorsque l'utilisateur
 * clique avec le bouton droit sur un composant
 */
public class PopupComposantProcessusDyn extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	private JMenuItem supprimer;
	private JMenuItem renommer;

	/**
	* composant sur lequel on a cliqué.
	*/
	private ComposantCellDyn compo;

	/**
	 * Création du menu contextuel
	 */
	public PopupComposantProcessusDyn(ComposantCellDyn cp )
	{
		this.compo = cp;

		// création des items
		this.supprimer = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Composant"));
		//this.renommer = new JMenuItem(Application.getApplication().getTraduction("Renommer_Composant"));//modif 2xmi youssef

		// ajouter les items au menu
		this.add(this.supprimer);
		/*if(this.compo.getMdcomp().getId().estComposantVide())
        {
			this.add(this.renommer);//modif 2xmi youssef
        }*/

		// pouvoir réagr aux clicks des utilisateurs
		this.supprimer.addActionListener(this);
		//this.renommer.addActionListener(this);//modif 2xmi youssef
	}

	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		 if (event.getSource() == this.supprimer)
		 {
			CSupprimerComposantDyn c = new CSupprimerComposantDyn(this.compo);
			if (c.executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}
		 }
         /*if (event.getSource() == this.renommer)
         {
           //Modif 2xmi appel du constructeur avec Fenetre Principal en paramètre pour centrage de la fenetre de renommage
           DialogRenommerComposant c = new DialogRenommerComposant(Application.getApplication().getFenetrePrincipale(),this.compo.getModele().getId());
         }*/
	 }
}
