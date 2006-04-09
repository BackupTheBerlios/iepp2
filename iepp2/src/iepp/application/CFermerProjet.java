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

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import iepp.Application;
import iepp.Projet;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;

/**
 * Commande non annulable permettant de fermer le projet courant
 */
public class CFermerProjet extends CommandeNonAnnulable{

		Projet leProjetCourant;
		
		/**
		 * Indique si on doit demander confirmation si un projet est en cours
		 * et pas encore sauvegardé
		 */
		private boolean confirmation = false;
		
		
		
		/**
		 * Constructeur de la commande
		 * @param confirm indique si lors de la fermeture du projet, il faut demander
		 * la sauvegarde du projet en cours ou non
		 */
		public CFermerProjet (boolean confirm)
		{
			this.confirmation = confirm;
		}
		
		/**
		 * Constructeur par défaut, on ne demande pas de confirmation
		 */
		public CFermerProjet (){}
		
		
		/**
		 * Ferme le projet courant
		 * @see iepp.application.Commande#executer()
		 * @return true si un projet a été fermé
		 */
		public boolean executer()
		{
			// on vérifie qu'il y a un projet en cours d'édition
			if (Application.getApplication().getProjet() != null )
			{
				if ( Application.getApplication().getProjet().estModifie() && confirmation)
				{
					// demander confirmation
					int choice = JOptionPane.showConfirmDialog(
				  			 Application.getApplication().getFenetrePrincipale(),
							 Application.getApplication().getTraduction("BD_SAUV_AVAT_FERMER"),
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
				// sinon dans les autres cas, ou après sauvegarde de la DP, on ferme le projet
				Referentiel ref =Application.getApplication().getReferentiel();
				for(int i=0;i<ref.getNoeudComposants().getChildCount();i++)
				{
					ElementReferentiel courant=((ElementReferentiel)ref.getNoeudComposants().getChildAt(i));
					if (courant.getNomElement().contains(Application.getApplication().getTraduction("Comp_Present")))
					{
						courant.setNomElement(courant.getNomElement().replace(Application.getApplication().getTraduction("Comp_Present"),""));
						ref.majObserveurs(Referentiel.CHANGED);
					}
				}
//				on supprime les scenario de l'arbre
				if (ref.getNoeudScenarios().getChildCount()!=0){
					ref.getNoeudScenarios().removeAllChildren();
				}
				ref.majObserveurs(Referentiel.ELEMENT_REMOVED);
				//copie du projet courant
				this.leProjetCourant = Application.getApplication().getProjet(); 
				//Suppression du contenu du diagramme
				this.leProjetCourant.getFenetreEdition().getVueDPGraphe().effacerDiagramme();
				this.leProjetCourant.getFenetreEdition().removeVueDPGraphe();
				Application.getApplication().getFenetrePrincipale().setPanneauGenerique (new JPanel());
				this.leProjetCourant.setFenEdit(null);				
				//Suppression des composants de la DP
				this.leProjetCourant.getDefProc().retirerComposantTous();
				this.leProjetCourant.getDefProc().maj("DELETED");
				Application.getApplication().getFenetrePrincipale().getVueDPArbre().setModel(null);
				// Retrait de la DP
				this.leProjetCourant.getDefProc().deleteObservers();
				this.leProjetCourant.deleteDefProc();
				// Retrait du porjet courant
				Application.deleteProjet();
				System.gc();
				// Réinitialiser les associations entre références en mémoire et id
				// dans le référentiel
				Application.getApplication().getReferentiel().supprimerTousLesElementsCharges() ;
				Application.getApplication().getFenetrePrincipale().majEtat();
				return true;
			}
			return false;
		}

}
