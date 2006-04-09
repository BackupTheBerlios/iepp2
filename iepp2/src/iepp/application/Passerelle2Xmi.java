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

import java.lang.reflect.*;

import javax.swing.*;

import iepp.*;
import java.io.File;

/**
 * Cette classe a pour but d'interfacer un outil et 2XMI
 */
public class Passerelle2Xmi {
  /**
   * Nom de la classe Facade de 2XMI
   */
  private static final String nomClasseFacade = "twoxmi.Facade2XMI";

  /**
   * Classe facade de 2XMI
   */
  private Class classeFacade = null;

  /**
   * Instance de la facade de 2XMI
   */
  private Object facade2Xmi = null;

  /**
   * Chemin du repertoire par defaut pour les exportations
   */
  private String repertoireSortie = "";

  /**
   * Constructeur par défaut
   */
  public Passerelle2Xmi() throws Throwable
  {
      try
      {
          // on charge la classe en memoire...
          this.classeFacade = Class.forName(Passerelle2Xmi.nomClasseFacade);
          // ...pour instancier un objet facade
          Constructor constructeur = this.classeFacade.getConstructor(null); // constructeur par defaut
          this.facade2Xmi = constructeur.newInstance(null);
      }
      catch (ClassNotFoundException ex)
      {
          String message = Application.getApplication().getTraduction("2xmiNoJAR");
          JOptionPane.showMessageDialog(null, message, Application.getApplication().getTraduction("ERR"), JOptionPane.ERROR_MESSAGE);
          throw ex;
      }
      catch (Throwable ex)
      {
          String message = Application.getApplication().getTraduction("2xmiError");
          JOptionPane.showMessageDialog(null, message, Application.getApplication().getTraduction("ERR"), JOptionPane.ERROR_MESSAGE);
          throw ex;
      }
  }

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * M?thode pour exporter
   *
   * @param _nomAppelant L'outil demandant l'export
   * @param _extensionCible Extension du fichier cible
   * @param _formatCible Format vers lequel exporter (xml, pdf, svg ... voir la classe Driver de FOP)
   */
  public boolean exporter(String _nomAppelant, String _extensionCible, String _formatCible) {
    boolean resultat = true;
    try {
      Method methode = this.classeFacade.getMethod("exporter", new Class[] {String.class, String.class, String.class, String.class});
      Object res = methode.invoke(this.facade2Xmi, new Object[] {_nomAppelant, _extensionCible, _formatCible, this.repertoireSortie});
      if (res instanceof Boolean) {
        resultat = ( (Boolean) res).booleanValue();
      }
    }
    catch (Throwable ex) {
      resultat = false;
    }
    return resultat;
  }

  /**
   * Méthode pour exporter
   *
   * @param _nomAppelant L'outil demandant l'export
   * @param _extensionCible Extension du fichier cible
   * @param _formatCible Format vers lequel exporter (xml, pdf, svg ... voir la classe Driver de FOP)
   * @param _sourceFile Fichier source
   */
  public int exporter(String _nomAppelant, String _extensionCible, String _formatCible, File _sourceFile) {
    //!!!!!!!!!! Utilisee actuellement pour exporter en XMI (XMI seulement)
    int resultat = 0;
    try {
      Method methode = this.classeFacade.getMethod("exporter", new Class[] {String.class, String.class, String.class, String.class, File.class});
      Object res = methode.invoke(this.facade2Xmi, new Object[] {_nomAppelant, _extensionCible, _formatCible, this.repertoireSortie, _sourceFile});
      if (res instanceof Integer) {
        resultat = ( (Integer) res).intValue();
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
      resultat = -1;
    }
    return resultat;
  }

  /**
   * Méthode pour exporter vers un fichier spécifique
   * @param _nomAppelant L'outil demandant l'export
   * @param _extensionCible Extension du fichier cible
   * @param _formatCible Format vers lequel exporter (xml, pdf, svg ... voir la classe Driver de FOP)
   * @param _sourceFile Fichier source
   * @param _fichierCible File
   * @return boolean
   */
  public int exporter(String _nomAppelant, String _extensionCible, String _formatCible, File _sourceFile, File _fichierCible) {
    //!!!!!!!!!! Utilisee actuellement pour le fichier xmi utilise pour le PDF
    //et pour celui qui est dans l'archive DPC
    int resultat = 0;
    try {
      Method methode = this.classeFacade.getMethod("exporter", new Class[] {String.class, String.class, String.class, File.class, File.class});
      Object res = methode.invoke(this.facade2Xmi, new Object[] {_nomAppelant, _extensionCible, _formatCible,  _sourceFile, _fichierCible});
      if (res instanceof Integer) {
        resultat = ( (Integer) res).intValue();
      }
    }
    catch (Throwable ex) {
      resultat = -1;
    }
    return resultat;
  }


  /**
   * Methode pour specifier le repertoire de sortie par defaut
   *
   * @param _repertoireSortie Le chemin du repertoire
   */
  public void setOutputDir(String _repertoireSortie) {
    this.repertoireSortie = _repertoireSortie;
  }

  public String getRepertoireSortie() {
    return repertoireSortie;
  }
}
