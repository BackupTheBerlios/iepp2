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
import java.io.File;
import java.io.InputStream;
import java.io.*;
import iepp.Application;

/**
 * Classe pour exporter la définition de processus vers XMI
 */
public class CExporterProcessusXMI extends CommandeNonAnnulable
{
    /**
     * Constructeur par défaut
     */
    public CExporterProcessusXMI()
    {
    }

    /**
     * Méthode pour exécuter la commande
     *
     * @return Renvoi true si exécution correcte, false sinon
     */
    public boolean executer()
    {
      int resultat;
        CExporterProcessus cexporterProcessus = new CExporterProcessus();
        File fichierTmp = new File ("tmp.dpe");
        cexporterProcessus.exporter(fichierTmp,false);
        InputStream inputStream = cexporterProcessus.getStreamResult();
        if (inputStream != null)
        {
            try
            {
                String titre = Application.getApplication().getTraduction("exportXmi");
                Passerelle2Xmi passerelle2Xmi = new Passerelle2Xmi();
                passerelle2Xmi.setOutputDir (Application.getApplication().getReferentiel().getCheminExport());
                //passerelle2Xmi.setOutputDir (Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport"));
                resultat = passerelle2Xmi.exporter("iepp", "xmi", "xml", fichierTmp);
                if (resultat == 0)
                {
                    String message = Application.getApplication().getTraduction("exportXmiOk");
                    JOptionPane.showMessageDialog(null, message, titre, JOptionPane.INFORMATION_MESSAGE);
                }
                else if(resultat == -1)
                {
                    String message = Application.getApplication().getTraduction("exportXmiNok");
                    JOptionPane.showMessageDialog(null, message, titre, JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch (Throwable ex)
            {
              resultat = -1;
            }
            finally
            {
                fichierTmp.delete();
            }
        }
        else
        {
          resultat = -1;
        }
        return resultat == 1;
    }

    /**
     * Methode realisant l'exportation xmi dans un fichier spécifique
     *
     * @param _fichierCible Chemin du fichier destination
     * @return Renvoi true si execution correcte, false sinon
     */
    public int executer(File _fichierCible)
    {
        int resultat ;
        CExporterProcessus cexporterProcessus = new CExporterProcessus();
        File fichierTmp = new File ("tmp.dpe");
        cexporterProcessus.exporter(fichierTmp,false);
        InputStream inputStream = cexporterProcessus.getStreamResult();
        if (inputStream != null)
        {
            try
            {
                String titre = Application.getApplication().getTraduction("exportXmi");
                Passerelle2Xmi passerelle2Xmi = new Passerelle2Xmi();
                passerelle2Xmi.setOutputDir (Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport"));
                resultat = passerelle2Xmi.exporter("iepp", "xmi", "xml", fichierTmp, _fichierCible);
                fichierTmp.delete();
            }
            catch (Throwable ex)
            {
                resultat = -1;
            }
        }
        else
        {
                resultat = -1;
        }

        return resultat;
    }

    /**
     * Methode realisant l'exportation xmi dans un fichier specifique a partir d'un fichier DPE
     * deja genere grace a l'objet _cexporterProcessus
     *
     * @param _cexporterProcessus objet d'export
     * @param _fileDPE Chemin du fichier DPE généré
     * @param _fichierCible Chemin du fichier destination
     *
     * @return Renvoi true si execution correcte, false sinon
     */
    public int executerDPC(CExporterProcessus _cexporterProcessus, File _fileDPE, File _fichierCible)
    {
        int resultat = 1;
        InputStream inputStream = _cexporterProcessus.getStreamResult();
        if (inputStream != null)
        {
            try
            {
                String titre = Application.getApplication().getTraduction("exportXmi");
                Passerelle2Xmi passerelle2Xmi = new Passerelle2Xmi();
                passerelle2Xmi.setOutputDir (Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport"));
                resultat = passerelle2Xmi.exporter("iepp", "xmi", "xml", _fileDPE, _fichierCible);
            }
            catch (Throwable ex)
            {
                resultat = -1;
            }
        }
        else
        {
          resultat = -1;
        }

        return resultat;
    }

}
