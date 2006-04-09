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

package iepp.ui.popup;

import iepp.Application;
import iepp.application.CAjouterComposantVide;
import iepp.domaine.IdObjetModele;
import iepp.ui.preferences.FenetrePreference;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Classe permettant d'afficher un popupmenu contextuel lorsque l'utilisateur
 * clique droit sur la définition de processus (la racine de l'arbre)
 */
public class PopupDPArbre extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu à afficher
	 */
	private JMenuItem ajouterComposantVide ;
	private JMenuItem proprietes ;

	/**
	 * ID de la définition de processus sur laquelle on a cliqué
	 */
	private IdObjetModele idDefProc ;

	/**
	 * Création du menu contextuel
	 */
	public PopupDPArbre(IdObjetModele idDefProc)
	{
		this.idDefProc = idDefProc ;
		// création des items
		this.ajouterComposantVide = new JMenuItem(Application.getApplication().getTraduction("Ajouter_Composant_Vide_DP"));
		this.proprietes = new JMenuItem(Application.getApplication().getTraduction("Proprietes"));

		// ajouter les items au menu
		this.add(this.ajouterComposantVide);
		this.addSeparator();
		this.add(this.proprietes);

		// pouvoir réagr aux clicks des utilisateurs
		this.ajouterComposantVide.addActionListener(this);
		this.proprietes.addActionListener(this);
	}

	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		 if (event.getSource() == this.ajouterComposantVide)
		 {
		    CAjouterComposantVide c = new CAjouterComposantVide();
		    if (c.executer())
			{
			   		Application.getApplication().getProjet().setModified(true);
			}
		 }
	     else
	     {
			new FenetrePreference(Application.getApplication().getFenetrePrincipale(), FenetrePreference.TYPE_DP);
	     }
	 }
}
