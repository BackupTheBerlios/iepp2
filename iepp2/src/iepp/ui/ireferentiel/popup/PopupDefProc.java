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
package iepp.ui.ireferentiel.popup;

import iepp.Application;
import iepp.application.areferentiel.CChargerDP;
import iepp.application.areferentiel.CRetirerDP;

import java.awt.event.* ;

import javax.swing.* ;

/**
 * Menu contextuel associé aux définitions de processus dans l'arbre du référentiel.
 */
public class PopupDefProc extends JPopupMenu implements ActionListener
{
	private long idDP ;	// Identifiant de la définition de processus concernée

	// Options du menu
	private JMenuItem ouvrirDP ;
	private JMenuItem retirerDP ;


	/**
	 * Constructeur.
	 * @param idDefProc identifiant dans le référentiel de la DP associé au menu.
	 */
	public PopupDefProc (long idDefProc)
	{
		// Enregistrer les paramètres
		this.idDP = idDefProc ;
		// Créer les éléments
		this.ouvrirDP = new JMenuItem (Application.getApplication().getTraduction("Ouvrir_DP")) ;
		this.retirerDP = new JMenuItem (Application.getApplication().getTraduction("Retirer_DP_Ref")) ;
		// Ajouter les éléments au menu
		this.add (this.ouvrirDP) ;
		this.add (this.retirerDP) ;
		// Ecouter les événements
		this.ouvrirDP.addActionListener (this) ;
		this.retirerDP.addActionListener (this) ;
	}


	/** Clics sur les éléments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		// Supprimer la définition du référentiel
		if (source == this.retirerDP)
		{
			(new CRetirerDP (this.idDP)).executer() ;
		}
		// Ouvrir une DP du référentiel
		else if (source == this.ouvrirDP)
		{
			(new CChargerDP (this.idDP)).executer() ;
		}
	}

}
