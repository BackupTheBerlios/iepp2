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
package iepp.application.areferentiel;

import iepp.Application;
import iepp.application.CommandeNonAnnulable;

import javax.swing.JOptionPane;

/**
 * retire une définition de processus du référentiel.
 */
public class CRetirerDP extends CommandeNonAnnulable
{
	private long idDP ;


	/**
	 * Construit la commande.
	 * @param idDefProc identifiant de la définition de processus dans le référentiel
	 */
	public CRetirerDP (long idDefProc)
	{
		this.idDP = idDefProc ;
	}


	/**
	 * Retire la définition de processus indiquée du référentiel.
	 * @see iepp.application.Commande#executer()
	 * @return true si la commande s'est exécutée correctement
	 */
	public boolean executer()
	{
		// Vérifier que la DP ne se trouve pas dans le projet en cours
		Referentiel ref = Application.getApplication().getReferentiel() ;
		if (ref.chercherReference(this.idDP) != null)
		{
			JOptionPane.showMessageDialog ( Application.getApplication().getFenetrePrincipale(),
				Application.getApplication().getTraduction("ERR_SUP_REF_ED"),
				Application.getApplication().getTraduction("ERR"),
				JOptionPane.ERROR_MESSAGE );
			return false ;
		}

		// Demander confirmation à l'utilisateur
		int choice = JOptionPane.showConfirmDialog( Application.getApplication().getFenetrePrincipale(),
				 Application.getApplication().getTraduction("BD_SUPP_DP_REF"),
				 Application.getApplication().getTraduction("Confirmation"),
				 JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE );
		if (choice == JOptionPane.OK_OPTION)
		{
			// Demander au référentiel de retirer cette DP
			ref.supprimerElement (this.idDP, ElementReferentiel.DP) ;
			return true ;
		}
		// Si on arrive ici, la commande a été annulée
		return false ;
	}

}
