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

import javax.swing.JOptionPane;

import iepp.Application;
import iepp.application.CommandeNonAnnulable;
import iepp.application.aedition.CSupprimerComposant;
import iepp.domaine.ComposantProcessus;


/**
 * Retire un composant du référentiel.
 */
public class CRetirerComposant extends CommandeNonAnnulable
{
	private long idComp ;	// Identifiant du composant


	/**
	 * Construit la commande.
	 * @param idComp identifiant du composant dans le référentiel
	 */
	public CRetirerComposant (long idComp)
	{
		this.idComp = idComp ;
	}


	/**
	 * Retire le composant indiqué du référentiel.
	 * @see iepp.application.Commande#executer()
	 * @return true si la commande s'est exécutée correctement
	 */
	public boolean executer()
	{
		String txtMsg ;	// Message à afficher pour demander confirmation
		int typeMsg ;	// Type de la boîte de dialogue (info, avertissement)

		Referentiel ref = Application.getApplication().getReferentiel() ;
		// Regarder si le composant se trouve dans la DP en cours 
		txtMsg = Application.getApplication().getTraduction("BD_SUPP_COMP_REF") ;
		ComposantProcessus comp = (ComposantProcessus) ref.chercherReference (this.idComp) ;
		if (comp != null)
		{
			txtMsg += "\n"+Application.getApplication().getTraduction("BD_CONF_SUPPR_COMP") ;
			typeMsg = JOptionPane.WARNING_MESSAGE ;
		}
		else
		{
			typeMsg = JOptionPane.INFORMATION_MESSAGE ;
		}

		// Demander confirmation à l'utilisateur
		int choice = JOptionPane.showConfirmDialog( Application.getApplication().getFenetrePrincipale(),
				 txtMsg,
				 Application.getApplication().getTraduction("Confirmation"),
				 JOptionPane.OK_CANCEL_OPTION, typeMsg );
		if (choice == JOptionPane.OK_OPTION)
		{
			// Enlever le composant de la DP en cours s'il y est
			if (comp != null)
			{
				CSupprimerComposant c = new CSupprimerComposant (comp.getIdComposant()) ;
				if (c.executer())
					Application.getApplication().getProjet().setModified(true);
			}

			// Demander au référentiel de retirer ce composant
			return (ref.supprimerElement(this.idComp, ElementReferentiel.COMPOSANT)) ;
		}
		// Si on arrive ici, la commande a été annulée
		return false ;
	}
}
