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

package iepp.application;

import java.util.Vector;

import javax.swing.JOptionPane;
import iepp.Application;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.ui.FenetreSelectionDP;



/**
 * Classe permettant de lancer la procédure de chargement d'une définition de processus
 * sauvegardée. Vérifie si un processus n'est pas déjà ouvert, demande confirmation
 * pour perdre le travail en cours et demande l	 DP à charger dans la liste des DP
 * du référentiel
 * 
 */
public class COuvrirDP extends CommandeNonAnnulable
{

	public boolean executer()
	{
		// vérifier qu'il n'y ait pas déjà de Dp ouverte
		if (Application.getApplication().getProjet() == null)
		{
			// choisir dans la liste des dp présentes
			Vector liste = Application.getApplication().getReferentiel().getListeNom(ElementReferentiel.DP);
			if(liste.size() == 0)
				JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),Application.getApplication().getTraduction("ERR_AucuneDP"),Application.getApplication().getTraduction("ERR"),JOptionPane.WARNING_MESSAGE);
			else
			{
				FenetreSelectionDP f = new FenetreSelectionDP(Application.getApplication().getFenetrePrincipale(),null,Application.getApplication().getReferentiel());
				return (f.isDPChoisie());
			}
		}
		else
		{
			// on vérifie si la dp doit être sauvegardée
			if (Application.getApplication().getProjet().estModifie())
			{
				
				int choice = JOptionPane.showConfirmDialog(
		  			 Application.getApplication().getFenetrePrincipale(),
					 Application.getApplication().getTraduction("BD_SAUV_AVAT_OUVRIR"),
					 Application.getApplication().getTraduction("Confirmation"),
					 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

				  // si "oui" l'utilisateur accepte, on demande la sauvegarde
				  if( choice == JOptionPane.YES_OPTION )
				  {
				  		// sauver la dp courante
				  		Referentiel ref = Application.getApplication().getReferentiel() ;
				  		ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
				  		Application.getApplication().getProjet().setModified(false);
				  }
				  if ( choice == JOptionPane.CANCEL_OPTION)
				  {
				  	return false;
				  }
			}
			 
			// choisir dans la liste des dp présentes
			Vector liste = Application.getApplication().getReferentiel().getListeNom(ElementReferentiel.DP);
			
			if(liste.size() == 0)
				JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),Application.getApplication().getTraduction("ERR_AucuneDP"),Application.getApplication().getTraduction("ERR"),JOptionPane.WARNING_MESSAGE);
			else
			{
				FenetreSelectionDP f = new FenetreSelectionDP(Application.getApplication().getFenetrePrincipale(),null,Application.getApplication().getReferentiel());
				return (f.isDPChoisie());
			}
		
		}
		return true;
	}
}

