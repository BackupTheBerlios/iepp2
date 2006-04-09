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
import iepp.Projet;
import iepp.application.CSupprimerPresentationDP;
import iepp.application.CommandeNonAnnulable;
import iepp.application.aedition.CSupprimerComposant;
import iepp.domaine.IdObjetModele;
import iepp.domaine.PaquetagePresentation;
import iepp.infrastructure.jsx.ChargeurScenario;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

/**
 * Chargement d'une d?finition de processus depuis le r?f?rentiel dans l'application.
 */
public class CChargerDP extends CommandeNonAnnulable
{

	private long idDP ;


	/**
	 * Constructeur.
	 * @param idDP Identifiant dans le r?f?rentiel de la DP ? charger.
	 */
	public CChargerDP (long idDP)
	{
		this.idDP = idDP ;
	}


	/**
	 * Charge une d?finition de processus depuis le r?f?rentiel dans l'application.
	 * @see iepp.application.Commande#executer()
	 * @return true si la commande s'est ex?cut?e correctement
	 */
	public boolean executer()
	{
		Referentiel ref = Application.getApplication().getReferentiel() ;
		// Si une DP est ouverte
		if (Application.getApplication().getProjet() != null)
		{
			// Demander ? l'utilisateur s'il accepte de la fermer
			// (efface les r?f?rences aux objets de la DP)
			//	on v?rifie si la dp doit ?tre sauvegard?e
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
						 ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
						 Application.getApplication().getProjet().setModified(false);
				   }
				   if ( choice == JOptionPane.CANCEL_OPTION)
				   {
					 return false;
				   }
			 }
		}
		// Charger la DP
		// (l'enregistrement des associations id-r?f?rences m?moire est effectu?e par le r?f?rentiel)
		Vector listeComposantSupprimer = new Vector();
		Projet p = ref.chargerDefProc (this.idDP, listeComposantSupprimer) ;
		if (p != null)
		{
			Application.setProjet(p);
			for (int i = 0; i < listeComposantSupprimer.size(); i++)
			{
				if (listeComposantSupprimer.elementAt(i) instanceof IdObjetModele)
				{
					IdObjetModele id = (IdObjetModele)listeComposantSupprimer.elementAt(i);
					CSupprimerComposant c = new CSupprimerComposant (id);
					c.executer();
					
					JOptionPane.showMessageDialog ( Application.getApplication().getFenetrePrincipale(),
									Application.getApplication().getTraduction("ERR_Composant_Supprimer_partie1")+ " " +
									 id.toString() + " " + Application.getApplication().getTraduction("ERR_Composant_Supprimer_partie2"),
									Application.getApplication().getTraduction("ERR"),
									JOptionPane.INFORMATION_MESSAGE );
				}
				// paquetage de pr?sentation
				else
				{
					PaquetagePresentation paquet = (PaquetagePresentation)listeComposantSupprimer.elementAt(i);
					CSupprimerPresentationDP c = new CSupprimerPresentationDP (paquet);
					c.executer();
					
					JOptionPane.showMessageDialog ( Application.getApplication().getFenetrePrincipale(),
									Application.getApplication().getTraduction("ERR_Presentation_Supprimer_partie1")+ " " +
									paquet.toString() + " " + Application.getApplication().getTraduction("ERR_Presentation_Supprimer_partie2"),
									Application.getApplication().getTraduction("ERR"),
									JOptionPane.INFORMATION_MESSAGE );
				}
			}
			for (int j=0;j<Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getComposantCellCells().size();j++)
			{
				ComposantCell courant=(ComposantCell)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getComposantCellCells().elementAt(j);
				for(int i=0;i<ref.getNoeudComposants().getChildCount();i++)
				{
					if (((ElementReferentiel)ref.getNoeudComposants().getChildAt(i)).getNomElement().equals(courant.getNomCompCell()))
					{
						((ElementReferentiel)ref.getNoeudComposants().getChildAt(i)).setNomElement(courant.getNomCompCell()+Application.getApplication().getTraduction("Comp_Present"));
						ref.majObserveurs(Referentiel.CHANGED);
					}
				}
			}
			//on supprime les scenario de l'arbre
			if (ref.getNoeudScenarios().getChildCount()!=0){
				ref.getNoeudScenarios().removeAllChildren();
			}
			ref.majObserveurs(Referentiel.CHANGED);
			// chargement de tous les sc?narios
			File mFile=new File(Application.getApplication().getReferentiel().getCheminReferentiel()+File.separator+Application.getApplication().getProjet().getDefProc()+".iepp");
			
			ChargeurScenario cs=new ChargeurScenario(new File(Application.getApplication().getReferentiel().getCheminReferentiel()+File.separator+Application.getApplication().getProjet().getDefProc()+".iepp"));//eltRef.getChemin()));
			try{
				ZipInputStream zipFile = new ZipInputStream( new FileInputStream(new File(mFile.getAbsolutePath())));
				try{
					ZipEntry zipEntry = zipFile.getNextEntry();
					while( zipEntry != null )

					{

						if (!zipEntry.getName().equals("DefinitionProcessus.xml")){
							cs.chargerScenario(zipEntry.getName());
						}
						zipEntry = zipFile.getNextEntry();
					}
					zipFile.close();
					Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(0));
					Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setOutilSelection();
					Application.getApplication().getProjet().getFenetreEdition().setStatique();	
				}
				catch(IOException io){
						
				}
			}
			catch(FileNotFoundException f){
					
			}	
			return true;
		}
		return false ;
	}


}
