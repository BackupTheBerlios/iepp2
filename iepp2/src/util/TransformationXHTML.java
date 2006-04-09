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


package util;

import java.io.*;

import org.w3c.tidy.*;

/**
 * Class ajouté par 2XMI : Sébastien
 *
 * <p>Description: Classe permettant de transformer un fichier généré par IEPP (format HTML) en XHTML <br />
 * afin de permettre le "parsage" de ce fichier pour la génération pdf</p>
 *
 */
public class TransformationXHTML
{
    private String erreur = "";

    public TransformationXHTML()
    {
    }

    /**
     * Méthode de tranformation
     * @return boolean
     */
    public boolean executer(File _file, String dtd)
    {
        FileInputStream fileInputStream = null;
        boolean resultat = true;
        try
        {
            //fichier html source
            fileInputStream = new FileInputStream(_file);
            //fichier xhtml de sortie
            File xhtml = new File(_file.getPath() + ".tmp");

            //ecriture de la declaration de la DTD en début de fichier
            //ne peut être géré par jtidy pour le moment car c une DTD système
            FileWriter filewriter = new FileWriter(xhtml);
            filewriter.write("<!DOCTYPE html SYSTEM \""+ dtd +"\">\n");
            filewriter.close();

            //stream de sortie
            FileOutputStream fileOutputStream = new FileOutputStream(xhtml, true);
            //récuperation des erreurs HTML dans un fichier


            Tidy tidy = new Tidy();
            //configuration de jtidy
            tidy.setXHTML(true);
            tidy.setQuiet(true);
            tidy.setShowWarnings(false);
            tidy.setDocType("omit");
            ByteArrayOutputStream strErr = new ByteArrayOutputStream();
            tidy.setErrout(new PrintWriter(strErr, true));

            tidy.setIndentContent(true);
            tidy.setTidyMark(false);

            //transformation
            tidy.parse(fileInputStream, fileOutputStream);
            fileInputStream.close();
            fileOutputStream.close();
            if(strErr.toString().length() != 0)
            {
                //Erreurs lors de la transformation
                resultat = false;
                //récupération des erreurs
                this.erreur = strErr.toString();
                xhtml.delete();
            }
            else
            {
                //Aucune Erreur
                //efface l'ancien fichier HTML et le replace par le XHTML
                _file.delete();
                xhtml.renameTo(_file);
            }

            return resultat;
        }
        catch (Exception ex)
        {
            resultat = false;
            return resultat;
        }
    }

    public void setErreur(String erreur)
    {
        this.erreur = erreur;
    }

    public String getErreur()
    {
        return erreur;
    }

    /**
     * transformationAvecErreur
     */
    public boolean transformationAvecErreur()
    {
        return erreur.equals("");
    }

}
