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




import java.awt.Cursor;

import iepp.Application;
import iepp.application.areferentiel.Referentiel;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import util.DependenciesFileFilter;




/**
 * Classe permettant de lancer la procédure de chargement d'un référentiel
 * sauvegardée. Vérifie si un processus n'est pas déjà ouvert, demande confirmation
 * pour perdre le travail en cours et demande le référentiel à charger
 */
public class COuvrirReferentiel extends CommandeNonAnnulable
{

	/**
	 * Filtre utilisé par la boîte de dialogue demandant le référentiel à ouvrr
	 */
	private DependenciesFileFilter filter = new DependenciesFileFilter() ;
	
	
	public boolean executer()
	{
		// vérifier qu'il n'y ait pas déjà de Dp ouverte
		if (Application.getApplication().getProjet() != null)
		{
			// on vérifie si la dp doit être sauvegardée
			if (Application.getApplication().getProjet().estModifie())
			{
				
				int choice = JOptionPane.showConfirmDialog(
		  			 Application.getApplication().getFenetrePrincipale(),
					 Application.getApplication().getTraduction("BD_SAUV_AVAT_OUVRIR_REF"),
					 Application.getApplication().getTraduction("Confirmation"),
					 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

				  // si "oui" l'utilisateur accepte, on demande la sauvegarde
				  if( choice == JOptionPane.YES_OPTION )
				  {
				  		Referentiel ref = Application.getApplication().getReferentiel() ;
				  		ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
				  		Application.getApplication().getProjet().setModified(false);
				  }
				  if ( choice == JOptionPane.CANCEL_OPTION)
				  {
				  	return false;
				  }
			}
		}

		// Demander le composant à l'utilisateur à l'aide d'une boite de dialogue
		JFileChooser chooser = new JFileChooser(Application.getApplication().getConfigPropriete("chemin_referentiel"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// récupère le nom de fichier sélectionné par l'utilisateur
		if(chooser.showOpenDialog(Application.getApplication().getFenetrePrincipale()) != JFileChooser.APPROVE_OPTION )
		{
			return false;
		}
		
		//souris sablier
		Application.getApplication().getFenetrePrincipale().setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		
		// on  ferme le projet en cours
		CFermerProjet c = new CFermerProjet();
		c.executer();
		
		CChargerReferentielDemarrage c2 = new CChargerReferentielDemarrage(chooser.getSelectedFile());
		if (!c2.executer())
		{
			return false;
		}
		return true;
	}
}

