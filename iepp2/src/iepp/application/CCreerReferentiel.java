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


import iepp.Application;
import iepp.application.areferentiel.Referentiel;
import iepp.ui.FenetreCreerReferentiel;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Commande non annulable permettant de créer un nouveau référentiel dans l'outil
 */
public class CCreerReferentiel extends CommandeNonAnnulable
{

	/**
	 * Crée un projet vide
	 * Si un projet est déjà ouvert, demande confirmation de la fermeture du projet courant
	 * @see iepp.application.Commande#executer()
	 * @return true si un projet a été créé
	 */
	public boolean executer()
	{
		// on vérifie s'il y a un projet en cours d'édition
		if (Application.getApplication().getProjet() != null )
		{
			
			// on vérifie si la dp doit être sauvegardée
			if (Application.getApplication().getProjet().estModifie())
			{
				
				int choice = JOptionPane.showConfirmDialog(
		  			 Application.getApplication().getFenetrePrincipale(),
					 Application.getApplication().getTraduction("BD_SAUV_AVAT_CREER_REF"),
					 Application.getApplication().getTraduction("Confirmation"),
					 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

				  // si "oui" l'utilisateur accepte, on demande la sauvegarde
				  if( choice == JOptionPane.YES_OPTION )
				  {
				  		//sauver la dp courante
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
			JFileChooser fchoix = new JFileChooser(Application.getApplication().getConfigPropriete("chemin_referentiel"));
			fchoix.setApproveButtonText(Application.getApplication().getTraduction("Choisir_Dossier"));
			fchoix.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(fchoix.showOpenDialog(Application.getApplication().getFenetrePrincipale())==JFileChooser.CANCEL_OPTION){
				return false;
			}
			
			String path = fchoix.getSelectedFile().getAbsolutePath();
			//System.out.println(path);
			File frep = new File(path);
			if (frep.exists()){
				//System.out.println("existe");
				
				FenetreCreerReferentiel f =  new FenetreCreerReferentiel(Application.getApplication().getFenetrePrincipale(), path);
				if(!f.isReferentielCree()){
					return false;
				}
				
			}else{
				
				JOptionPane.showConfirmDialog(
			  			 Application.getApplication().getFenetrePrincipale(),
						 Application.getApplication().getTraduction("ERR_Pas_Repertoire"),
						 Application.getApplication().getTraduction("ERR"),
						 JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
				
				return false;
				
			}
			return true;
		
	}
}
