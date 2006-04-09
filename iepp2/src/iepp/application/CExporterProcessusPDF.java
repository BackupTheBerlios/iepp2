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

import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.*;

import iepp.*;
import iepp.domaine.*;
import util.*;
import iepp.application.ageneration.ArbreGeneration;

/**
 * <p>Title: </p>
 *
 * <p>Description: Classe pour exporter une définition de processus en PDF
 * Le site web est généré en meme temps que l'export pdf
 * </p>
 *
 * <p>2XMI </p>
 *
 * @version 1.0
 */
public class CExporterProcessusPDF extends CommandeNonAnnulable
{
    public static Vector listeMessages = new Vector();

    public static boolean erreurFatale = false;

    protected SimpleFileFilter filter = new SimpleFileFilter("pdf", "Portable Document Format");

    private TaskMonitorDialog dialogAvancee = null;
    private TacheExportationPDF tacheExport;

    public CExporterProcessusPDF()
    {
        this.erreurFatale = false;
    }

    public boolean executer()
    {
        //SmartChooser chooser = SmartChooser.getChooser(Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport"));
        //chooser.setAcceptAllFileFilterUsed(false);
        //chooser.setFileFilter(filter);
        //chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Modif 2xmi Julien
        //récupération des répertoires
    	String repertoireExport = Application.getApplication().getReferentiel().getCheminExport();
        // String repertoireExport = Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport");
        File rep = new File(repertoireExport);
        rep.mkdir();
        //création et affichage des JFileChooser
        JFileChooser chooser = new JFileChooser();
        //SmartChooser chooser = new SmartChooser();
        chooser.setCurrentDirectory(rep);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Modif 2xmi Julien

        //Ouverture de la boite de dialogue
        if (chooser.showSaveDialog(Application.getApplication().getFenetrePrincipale()) != JFileChooser.APPROVE_OPTION)
        {
            return false;
        }
        String selected_file = chooser.getSelectedFile().getAbsolutePath();
        // verifier qu'il y a bien l'extension
        if (!chooser.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".pdf"))
        {
            selected_file += ".pdf";
        }
        File fic = new File(selected_file);
        // verifier si le fichier existe deja, auquel cas on demande confirmation pour l'ecrasement
        if (fic.exists())
        {
            int choice = JOptionPane.showConfirmDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("msgConfirmEcrasement"), Application.getApplication().getTraduction("msgSauvegarde"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (choice != JOptionPane.YES_OPTION)
            {
                return false;
            }
            if (!fic.delete())
            {
                return false; // impossible a supprimer
            }
        }
        try
        {
            if (!fic.createNewFile())
            {
                return false; // impossible a creer
            }
            return (this.exporter(fic));
        }
        catch (IOException ex)
        {
            return false; // impossible a creer
        }
    }

    /**
     * Méthode qui exporte en pdf vers le fichier en paramètre
     * @param _fichierCible File
     * @return boolean
     */
    public boolean exporter(File _fichierCible)
    {
        boolean resultat = false;

        //sauvegarde du chemin de génération choisi par l'utilisateur
        String cheminUtilisateur = Application.getApplication().getProjet().getDefProc().getRepertoireGeneration();
        //a la place, on met le chemin du dossier temporaire de format yyyyMMddHHmmss
        SimpleDateFormat formateur = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String cheminRepertoireTmp = formateur.format(date);

        Application.getApplication().getProjet().getDefProc().setRepertoireGenerationNotChanged(cheminRepertoireTmp);

        //creation du fichier xmi a partir duquel le pdf sera construit
        File fichierTmp = new File("tmp.xmi");
        try
        {
            CExporterProcessusXMI exportXMI = new CExporterProcessusXMI();
            //si l'export du xmi ne reussi pas on arrete
            if (exportXMI.executer(fichierTmp )== -1)
            {
                return resultat;
            }

            //on recupere la defintion de processus pour generer le site web
            DefinitionProcessus defProc = Application.getApplication().getProjet().getDefProc();
            CGenererSite site = new CGenererSite(defProc);
            //si la generation du site web ne reussi pas on arrete
            if (!site.executer())
            {
                return resultat;
            }
            this.tacheExport = new TacheExportationPDF();
            this.dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), this.tacheExport);
            this.dialogAvancee.setTitle(Application.getApplication().getTraduction("generation_PDF_en_cours"));

            this.tacheExport.setTask(dialogAvancee);
            this.tacheExport.setFichierSource(fichierTmp);
            this.tacheExport.setFichierDestination(_fichierCible);
            this.dialogAvancee.show();

            resultat = tacheExport.isGenerationReussie();


        }
        catch (Exception eee)
        {
        }
        finally
        {
            //efface le dossier tmp et le fichier xmi
            fichierTmp.delete();
            try
            {
                ToolKit.delFile(cheminRepertoireTmp);
            }
            catch (Exception eee)
            {
            }
            //on retablit le chemin de generation du site defini par l'utilisateur
            Application.getApplication().getProjet().getDefProc().setRepertoireGenerationNotChanged(cheminUtilisateur);
        }
        return resultat;
    }

    public static void afficheListeMessages()
    {
        ErrorDialog erreur = new ErrorDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("ERR_PDF_titre"),
                                             Application.getApplication().getTraduction("ERR_PDF"));
        Iterator it = listeMessages.iterator();
        while (it.hasNext())
        {
            erreur.println("- " + it.next().toString());
        }
        //affichage de la fenetre
        erreur.affiche();
    }

    /**
     * ajouterErreursGeneration
     */
    public static void ajouterErreursGeneration()
    {
        listeMessages.addAll(0,ArbreGeneration.listeErreurs);
        listeMessages.add(0,Application.getApplication().getTraduction("ERR_Code_HTML"));
        //on vide la liste d'erreurs de code html
        ArbreGeneration.listeErreurs.clear();
    }

}
