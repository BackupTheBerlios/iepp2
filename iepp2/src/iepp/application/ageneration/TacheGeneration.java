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

package iepp.application.ageneration;

import java.io.*;

import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;

import iepp.Application;
import util.*;
import util.index.IndexHTML;


public class TacheGeneration extends MonitoredTaskBase {

	private PrintWriter pwFicTree ;
	private TaskMonitorDialog mTask = null;
	private boolean generationReussie = false;
	private ArbreGeneration arbre = null;

	public TacheGeneration() {
		pwFicTree = null;
		mTask = null;
		generationReussie = false;
		arbre = null;
	}

	protected Object processingTask()
	{
		this.genererSite();
		return null;
	}
	public boolean isGenerationReussie()
	{
		return this.generationReussie;
	}

	private void genererSite()
	{
		try
		{
			//récupérer l'heure de début de la génération
			GenerationManager.getInstance().debuterGeneration();
			GenerationManager.getInstance().setTache(this);
			this.arbre = new ArbreGeneration();
			ArbreGeneration.initialiserMap();
			
			GenerationManager.recupererProduitsExterieurs();
			this.preparerGeneration();
			GenerationManager.construireArbre(this.arbre, pwFicTree);
			File f = new File(GenerationManager.getInstance().getCheminGeneration());

			//mettre ici les rajouts de page
			if (GenerationManager.getInstance().estRecapitulatif())
			{
				ArbreGeneration recap = new ArbreGeneration(new GRecapitulatif(this.pwFicTree));
				this.arbre.ajouterSousArbre(recap);
				ArbreGeneration aux = new ArbreGeneration(new GRecapitulatifObject(GRecapitulatifObject.ROLES, this.pwFicTree));
				recap.ajouterSousArbre(aux);
				aux = new ArbreGeneration(new GRecapitulatifObject(GRecapitulatifObject.PRODUITS, this.pwFicTree));
				recap.ajouterSousArbre(aux);
				aux = new ArbreGeneration(new GRecapitulatifObject(GRecapitulatifObject.ACTIVITES, this.pwFicTree));
				recap.ajouterSousArbre(aux);
				aux = new ArbreGeneration(new GRecapitulatifObject(GRecapitulatifObject.GUIDE, this.pwFicTree));
				recap.ajouterSousArbre(aux);
			}
			//ajout des sous arbres dans l'arbre
			int nombre = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().size()-1;
			
			if(nombre>0)
			{
				GScenario monsce = new GScenario();
				ArbreGeneration aux = new ArbreGeneration(new GRecapitulatifObject(GRecapitulatifObject.SCENARIO,monsce, this.pwFicTree));
				this.arbre.ajouterSousArbre(aux);
				
				
				for(int i=1;i<=nombre;i++)
				{
					//recuperation du nom
					GraphModelView graphM = ((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(i));
					
					//on ajoute le sous arbre
					ArbreGeneration aux3 = new ArbreGeneration(new GRecapitulatifObject(GRecapitulatifObject.SOUSSCENARIO, graphM.getNomDiagModel(),monsce, this.pwFicTree));
					aux.ajouterSousArbre(aux3);
				}
			}
			if (GenerationManager.getInstance().estStatistiques())
			{
				ArbreGeneration stats = new ArbreGeneration(new GStatistiques(this.pwFicTree));
				this.arbre.ajouterSousArbre(stats);
			}
			
			this.arbre.initialiserArbre(ToolKit.removeSlashTerminatedPath(f.getAbsolutePath()));
			// System.out.println(arbre);


			//Création des pages contenues dans la page d'accueil
			this.creerPageAccueil();
			this.print(Application.getApplication().getTraduction("creation_pages"));
			this.arbre.genererSite();

			// fermeture du fichier tree.dat
			this.pwFicTree.close();

                        //Modif 2XMI Youssef
                        //Sébastien --> déplacé + ajout affichage creation_index
                        //indexation du site web
                        this.print(Application.getApplication().getTraduction("creation_index"));

                        IndexHTML index = new IndexHTML(new File(GenerationManager.getInstance().getCheminGeneration()),"index",true);//modif 2xmi youssef
                        index.executer();

                        String cheminIndex = GenerationManager.getInstance().getCheminGeneration() + File.separator + "applet" + File.separator + "index";
                        //ecriture fichier langue
                        File langue = new File(cheminIndex + File.separator + "langue.txt");
                        FileWriter fd = new FileWriter(langue);
                        fd.write(Application.getApplication().getConfigPropriete("langueCourante"));
                        fd.close();
                        //creation jar
                        JarCreator.jarFolder(cheminIndex);

			this.generationReussie = true;
			GenerationManager.getInstance().setTache(null);
		}
		catch(Throwable t)
		{
			this.generationReussie = false;
			t.printStackTrace();
			this.traiterErreur();
			ErrorManager.getInstance().displayError(t.getMessage());

		}
	}



	private void preparerGeneration() throws Exception
	{
		this.print(Application.getApplication().getTraduction("creation_rep"));
		// Creation du dossier du site
		File rep = new File(GenerationManager.getInstance().getCheminGeneration()+ File.separator + "styles" + File.separator);
		rep.mkdirs();
		
		// copie des répertoires ressource (javascript ...)
		Copie.copieRep(Application.getApplication().getConfigPropriete("site"), GenerationManager.getInstance().getCheminGeneration()+ File.separator + GenerationManager.APPLET_PATH);
		
		/**
        *copie de la feuille de style sélectionnée par l'utilisateur
        *ajout de iepp.dtd de le répertoire des feuilles de styles
		*/
		
		String styleChoisi = Application.getApplication().getConfigPropriete("feuille_style")+".css";
		
		// styles présents dans les ressources de suiteApes
		File repApes = new File(Application.getApplication().getConfigPropriete("styles") + styleChoisi);
		// styles présents dans le répertoire css du projet
		File repProjet = new File(Application.getApplication().getReferentiel().getCheminReferentiel()+File.separator+"css"+File.separator+styleChoisi);
		// répertoire de destination du style à la génération du site
		String repDest = GenerationManager.getInstance().getCheminGeneration() + File.separator + GenerationManager.STYLES_PATH;
		
		if (repApes.exists()) {
			Copie.copieFic(repApes.toString(), repDest);
		}
		else if (repProjet.exists()) {
			Copie.copieFic(repProjet.toString(), repDest);
		}
		else {
			Copie.copieFic(Application.getApplication().getConfigPropriete("styles")+"/"+"Blue.css", repDest);
			GenerationManager.getInstance().setFeuilleCss("Blue");
		}
	
		//Création du fichier tree.dat
		this.creerFicTree(GenerationManager.getInstance().getCheminGeneration() + File.separator + GenerationManager.APPLET_PATH);
	}


		/**
		 * permet de creer les fichiers HTML corespondant à l'accueil du site
		 */
		public void creerPageAccueil() throws Exception
		{

			this.print(Application.getApplication().getTraduction("creation_page_acc"));
			// création du fichier index.html
			String nom = GenerationManager.getInstance().getCheminGeneration()+ File.separator + "index.html"  ;
			File ficHTML = new File (nom) ;
			FileWriter fd;

			fd = new FileWriter(nom);
			String fichierAccueil = CodeHTML.getPageAccueil(Application.getApplication().getProjet().getDefProc().getNomDefProc());
			fd.write(fichierAccueil) ;
			fd.close();

			// création de l'image du diagramme pour la page principale
			this.creerImagePng(Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe(), GenerationManager.getInstance().getCheminGeneration() + File.separator + "main") ;
			// création du fichier main.html
			nom =GenerationManager.getInstance().getCheminGeneration()+ File.separator + "main.html"  ;
			ficHTML = new File (nom) ;

			fd = new FileWriter(nom);
			String fichierMain = CodeHTML.getPagePrincipale(Application.getApplication().getProjet().getDefProc().getNomDefProc());
			fd.write(fichierMain) ;
			fd.close();

			// création du fichier arbre.html
			nom = GenerationManager.getInstance().getCheminGeneration()+ File.separator + GenerationManager.APPLET_PATH + File.separator + "arbre.html"  ;
			ficHTML = new File (nom) ;

			fd = new FileWriter(nom);
			String fichierArbre = CodeHTML.getArbre();
			fd.write(fichierArbre) ;
			fd.close();

                        //modif 2XMI ajout de la page de recherche
                        nom = GenerationManager.getInstance().getCheminGeneration()+ File.separator + GenerationManager.APPLET_PATH + File.separator + "recherche.html"  ;
                        ficHTML = new File (nom) ;
                        fd = new FileWriter(nom);
                        String fichierRecherche = CodeHTML.getRecherche();
                        fd.write(fichierRecherche) ;
                        fd.close();
                        //fin modif
		}

		public void creerFicTree(String cheminGen) throws Exception
		{
			this.print(Application.getApplication().getTraduction("creation_menu"));
			// création du fichier tree.dat
			pwFicTree =new PrintWriter(new BufferedWriter(new FileWriter(cheminGen + File.separator + "tree.js")), true);
			pwFicTree.println("USETEXTLINKS = 1");
			pwFicTree.println("STARTALLOPEN = 0");
			pwFicTree.println("HIGHLIGHT = 1");
			pwFicTree.println("HIGHLIGHT_BG = '" + GenerationManager.getInstance().getCouleurSurligne() + "'");
			pwFicTree.println("ICONPATH = 'images/'");
			pwFicTree.println("foldersTree = gFld(\"<i>" + Application.getApplication().getProjet().getDefProc().toString() + "</i>\", \"../main.html\")");
		}

		public void creerImagePng (VueDPGraphe diagramme, String nom)
		{
			try
			{
				FileOutputStream fout = new FileOutputStream(new File(nom + ".png"));
				ImageUtil.encoderImage(diagramme, fout, "png");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}


		//-------------------------------------------
		// Extends MonitoredTaskBase
		//-------------------------------------------
		public void setTask(TaskMonitorDialog task)
		{
			this.mTask = task;
		}

		public void print( String msg )
		{
			this.setMessage(msg);
			if(this.mTask != null )
			{
				this.mTask.forceRefresh();
			}
		}
}
