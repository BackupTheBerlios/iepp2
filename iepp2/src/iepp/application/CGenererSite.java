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
import iepp.application.ageneration.GenerationManager;
import iepp.application.ageneration.TacheGeneration;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.infrastructure.jsx.ChargeurPresentationComposant;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCell;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import sun.security.krb5.internal.crypto.c;
import util.TaskMonitorDialog;


/**
 *
 */

public class CGenererSite extends CommandeNonAnnulable
{
	private TaskMonitorDialog dialogAvancee = null;
	private TacheGeneration tacheGener;
	private DefinitionProcessus defProc;
	
	private ChargeurPresentationComposant cpc;

	public CGenererSite (DefinitionProcessus defProc)
	{
		this.defProc = defProc;
		dialogAvancee = null;
	}

	public boolean executer()
	{
		// On charge les presentations d'apres le fichier .pre correspondant aux .apes
		if(chargerPresentation()==false) {
			// l'utilisateur a annulé la génration
			return false;
		}
		
		// Demarrage de la generation
		this.initGeneration();
		this.tacheGener = new TacheGeneration();
		this.dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), this.tacheGener);
		this.dialogAvancee.setTitle(Application.getApplication().getTraduction("generation_en_cours"));

		this.tacheGener.setTask(dialogAvancee);
		this.dialogAvancee.show();

		return tacheGener.isGenerationReussie();
	}
	
	public boolean chargerPresentation() {
		// On charge les presentation de chaque composants
		Vector listcompcell=((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(0)).getComposantCellCells();
		int i=0;
		boolean continuer = true;
		ComposantProcessus cp2; 
		HashMap presentation;
		while((continuer==true) && (i<listcompcell.size())) {
			ComposantCell cp = (ComposantCell)listcompcell.elementAt(i);
			if(cp.isParse()==false) {
				cpc = new ChargeurPresentationComposant(cp.getCompProc().getNomFichier());
				continuer = cpc.chargerPresentationComposant(cp.getCompProc());
				if((continuer==false) && (!cp.getCompProc().estVide())) {
					// On n'a pas trouvé le .pre
					JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("ERR_ChargeurPresentation4")+" "+cp.getCompProc().getNomComposant()+" "+Application.getApplication().getTraduction("ERR_ChargeurPresentation5")+" "+cp.getCompProc().getNomFichier().replace(".apes", ".pre")+" "+Application.getApplication().getTraduction("ERR_ChargeurPresentation6"),
						      "Error",
						      JOptionPane.OK_OPTION);
					return false;
				}
				if(continuer) {
					cp2 = cpc.getComposantCharge();
					presentation = cpc.getMapPresentation();
					cp2.initialiserPresentation(presentation);
					cp.setParse(true);
				}
			}
			i++;
		}
		return continuer;
	}
	
	private void initGeneration()
	{
		// on sauvegarde l'ordre de la liste dans le Generation Manager
		GenerationManager.getInstance().setListeAGenerer(this.defProc.getListeAGenerer());
		// on modifie le chemin de generation
		GenerationManager.getInstance().setCheminGeneration(this.defProc.getRepertoireGeneration());
		//on cree si besoin
		//creerDossier(GenerationManager.getInstance().getCheminGeneration());
		//creerDossier(this.defProc.getRepertoireGeneration());
		
     	//on modifie la couleur des éléments sélectionnés dans l'arbre
		GenerationManager.getInstance().setCouleurSurligne(new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_arbre"))));
		// feuille de style
		GenerationManager.getInstance().setFeuilleCss(Application.getApplication().getConfigPropriete("feuille_style"));
		// contenu
		GenerationManager.getInstance().setPlaceContenu(Application.getApplication().getConfigPropriete("place_contenu"));
		//page AJOUT 2XMI Albert
		GenerationManager.getInstance().setPlacePage(Application.getApplication().getConfigPropriete("place_page"));
		// page AJOUT 2XMI Albert
		GenerationManager.getInstance().setPlaceAssemblage(Application.getApplication().getConfigPropriete("place_assemblage"));
		// info-bulle
		//modif 2XMI Amandine
		GenerationManager.getInstance().setInfoBulle(Application.getApplication().getConfigPropriete("info_bulle"));

		GenerationManager.getInstance().setInfoBulleActivite(Application.getApplication().getConfigPropriete("info_bulle_activite"));
		// statistiques
		GenerationManager.getInstance().setStatistiques(Application.getApplication().getConfigPropriete("statistiques"));
		// recapitulatif
		GenerationManager.getInstance().setRecap(Application.getApplication().getConfigPropriete("recapitulatif"));
	}
	
	// Methode qui cree Dossier de generation si celui-ci n'existe pas
	// AJOUT Mohamed-Amine
	/*private void creerDossier (String unChemin)
	{
		File Dossier = new File(unChemin);
		// Si le dernier dossier du chemin n'existe pas Alors
		if (! Dossier.exists())
		{
			// On récupère le dossier parent
			String unChemin2=unChemin.substring(0,unChemin.lastIndexOf(File.separator));
			// On test si le dossier parent existe
			this.creerDossier(unChemin2);
			// On crée le Dossier manquant
			if (!Dossier.mkdir()){System.out.print("erreur de creation du dossier");};
		}
	}*/
}

