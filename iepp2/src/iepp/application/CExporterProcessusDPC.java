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

import iepp.application.ageneration.GenerationManager;
import util.Zip;
import java.io.File;
import util.SimpleFileFilter;
import util.SmartChooser;
import javax.swing.JFileChooser;
import iepp.Application;
import javax.swing.JOptionPane;
import java.io.IOException;
import iepp.domaine.DefinitionProcessus;
import util.ToolKit;
import util.Copie;
import javax.swing.filechooser.FileSystemView;


public class CExporterProcessusDPC {
  protected SimpleFileFilter filter = new SimpleFileFilter("dpc",
      "Exported Complete Process");

  public CExporterProcessusDPC() {
  }

  public boolean executer(){

    //SmartChooser chooser = SmartChooser.getChooser(Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport"));
    //chooser.setAcceptAllFileFilterUsed(false);
    //chooser.setFileFilter(filter);
    //chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    //récupération des répertoires
	String repertoireExport = Application.getApplication().getReferentiel().getCheminExport();
    //String repertoireExport=Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport");
    File rep=new File(repertoireExport);
    rep.mkdir();
    //création et affichage des JFileChooser
    JFileChooser chooser = new JFileChooser(rep);
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.setFileFilter(filter);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    //Ouverture de la boite de dialogue
    if (chooser.showSaveDialog(Application.getApplication().
                               getFenetrePrincipale()) !=
        JFileChooser.APPROVE_OPTION) {
      return false;
    }
    // Modification 2XMI Julien
    String selected_file = chooser.getSelectedFile().getAbsolutePath();
    // Modification 2XMI Julien
    // verifier qu'il y a bien l'extension
    if (!chooser.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(
        ".dpc")) {
      selected_file += ".dpc";
    }
    File fic = new File(selected_file);
    // verifier si le fichier existe deja, auquel cas on demande confirmation pour l'ecrasement
    if (fic.exists()) {
      int choice = JOptionPane.showConfirmDialog(Application.getApplication().
                                                 getFenetrePrincipale(),
                                                 Application.getApplication().
                                                 getTraduction(
          "msgConfirmEcrasement"),
                                                 Application.getApplication().
                                                 getTraduction("msgSauvegarde"),
                                                 JOptionPane.YES_NO_OPTION,
                                                 JOptionPane.QUESTION_MESSAGE);

      if (choice != JOptionPane.YES_OPTION) {
        return false;
      }
      if (!fic.delete()) {
        return false; // impossible a supprimer
      }
    }
    try {
      if (!fic.createNewFile()) {
        return false; // impossible a creer
      }
      return (this.exporter(fic));
    }
    catch (IOException ex) {
      return false; // impossible a creer
    }
  }

  public boolean exporter(File _fichierCible) throws IOException{
    boolean resultat = false;
    try {
      CExporterProcessus c = new CExporterProcessus();
      CExporterProcessusXMI x = new CExporterProcessusXMI();
      String old_s = System.getProperty("user.dir");
      File export = new File(System.getProperty("user.dir") + File.separator + "tempExportDPC");
      if (!export.exists())
        export.mkdirs();

      //Suppression de l'ancienne archive et creation de la nouvelle
      String pathArchive = new String(_fichierCible.getAbsolutePath());
      if ( (new File(pathArchive)).exists()) {
        (new File(pathArchive)).delete();
      }

      File fileDPE = new File(System.getProperty("user.dir") + File.separator + "tempExportDPC" + File.separator + "processus.dpe");
      File fileXMI = new File(System.getProperty("user.dir") + File.separator + "tempExportDPC" + File.separator + "processus.xmi");
      if(c.exporter(fileDPE, true)){ // le DPE est généré
        if(x.executerDPC(c,fileDPE,fileXMI) == 0){

          /*placement_evolution_validationXMI
          ValidationXMI vxmi = new ValidationXMI(Application.getApplication().getConfigPropriete("fichier2xmi"), fileXMI.getAbsolutePath());
          String resultatValidation = vxmi.LancerValidation();*/

          //sauvegarde du chemin de génération choisi par l'utilisateur
          String cheminUtilisateur = Application.getApplication().getProjet().getDefProc().getRepertoireGeneration();
          //a la place, on met le chemin du dossier temporaire
          Application.getApplication().getProjet().getDefProc().setRepertoireGenerationNotChanged(System.getProperty("user.dir") + File.separator + "tempExportDPC" + File.separator + "Site");
          //on recupere la defintion de processus pour generer le site web
          DefinitionProcessus defProc = Application.getApplication().getProjet().getDefProc();
          CGenererSite site = new CGenererSite(defProc);
          site.executer();

          Zip archive = new Zip(export.getAbsolutePath() + File.separator + "test.dpc");
          //ajout du dpe
          archive.ajouteFichier(fileDPE, fileDPE.getName(), "Fichier Export");
          //ajout du xmi
          archive.ajouteFichier(fileXMI, fileXMI.getName(), "Fichier Export");
          //ajout des fichiers xsd necessaires pour la validation
          Copie.copieFic(Application.getApplication().getConfigPropriete("fichier2xmi"), System.getProperty("user.dir") + File.separator + "tempExportDPC" + File.separator );
          Copie.copieFic(Application.getApplication().getConfigPropriete("fichierxmi20"), System.getProperty("user.dir") + File.separator + "tempExportDPC" + File.separator );
          File twoxmi = new File(System.getProperty("user.dir") + File.separator + "tempExportDPC" + File.separator + Application.getApplication().getConfigPropriete("fichier2xmi") );
          archive.ajouteFichier(twoxmi, twoxmi.getName(), "Fichier Export" );
          File xmi = new File(System.getProperty("user.dir") + File.separator + "tempExportDPC" + File.separator + Application.getApplication().getConfigPropriete("fichierxmi20") );
          archive.ajouteFichier(xmi, xmi.getName(), "Fichier Export" );

          //ajout du Site
          File file = new File(System.getProperty("user.dir") + File.separator + "tempExportDPC" + File.separator + "Site");
          if (file.exists() && file.isDirectory())
          {
            archive.ajouteFichier(new File(export.getAbsolutePath() + "/Site/"), "Site/", "Fichiers Site");
            File[] sousf = file.listFiles();
            for (int i = 0; i < sousf.length; i++)
            {
              if (sousf[i].isFile())
              {
                archive.ajouteFichier(new File(export.getAbsolutePath() + "/Site/" + sousf[i].getName()), new String("Site/" + sousf[i].getName()), "Fichiers Site");
              }
              else
              {
                archive.ajouteRepertoire(new File(export.getAbsolutePath() + "/Site/" + sousf[i].getName()), new String("Site/" + sousf[i].getName()), "Fichiers Site");
              }
            }
          }
          archive.ziper();

          //on renomme l'archive zip en _fichierCible
          File srcFile = new File(archive.getFichier());
          try
          {
            if (srcFile.renameTo(new File(_fichierCible.getAbsolutePath())))
            {
            }
          }
          catch (Exception e)
          {
          }

          //suppression du repertoire temporaire tempExportDPC
          try
          {
            ToolKit.delFile(export.getAbsolutePath());
          }
          catch (Exception eee)
          {
          }

          //on retablit le chemin de l'utilisateur par defaut
          System.setProperty("user.dir", old_s);

          //on retablit le chemin de generation du site defini par l'utilisateur
          Application.getApplication().getProjet().getDefProc().setRepertoireGenerationNotChanged(cheminUtilisateur);

          /*placement_evolution_validationXMI
          if (resultatValidation != null)
            JOptionPane.showMessageDialog(null, resultatValidation, Application.getApplication().getTraduction("XMINotValidTitle"), JOptionPane.INFORMATION_MESSAGE);*/
          resultat = true;
        }
      }
    }
    catch (Exception ex) {

    }
    return resultat;
  }

}
