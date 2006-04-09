package twoxmi.domain;

import java.io.*;

import javax.swing.*;

import twoxmi.utils.*;
import twoxmi.utils.xml.*;
import util.*;
import iepp.Application;
/*placement_evolution_validationXMI
import iepp.application.ValidationXMI;*/

/**
 * Classe mod?lisant une transformation
 */
public class Transformation
    implements SimpleXmlInterfacable {
  /**
   * fichierSource
   */
  private String fichierSource = null;

  /**
   * fichierXsl
   */
  private String fichierXsl = null;

  /**
   * fichierDestination
   */
  private String fichierDestination = null;

  /**
   * Variable definissant si l'utilisateur a annule
   */
  public static boolean ANNULE = false;

  /**
   * Constructeur par defaut
   */
  public Transformation() {
  }

  /**
   * setFichierSource
   *
   * @param string String
   */
  public void setFichierSource(String _fichierSource) {
    this.fichierSource = _fichierSource;
  }

  /**
   * setFichierXsl
   *
   * @param _fichierXsl String
   */
  public void setFichierXsl(String _fichierXsl) {
    this.fichierXsl = _fichierXsl;
  }

  /**
   * setFichierDestination
   *
   * @param _fichierDestination String
   */
  public void setFichierDestination(String _fichierDestination) {
    this.fichierDestination = _fichierDestination;
  }

  /**
   * getFichierSource
   *
   * @return Object
   */
  public String getFichierSource() {
    return this.fichierSource;
  }

  /**
   * getFichierSource
   *
   * @return Object
   */
  public String getFichierXsl() {
    return this.fichierXsl;
  }

  /**
   * getFichierSource
   *
   * @return Object
   */
  public String getFichierDestination() {
    return this.fichierDestination;
  }

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * transforme

  public int transforme() {
    int resultat = 0;
    File tmp;
    JFileChooser jfc = new JFileChooser();
    JFileChooser jfc2 = new JFileChooser();
    // fichier source
    tmp = new File(this.fichierSource);
    if (this.fichierSource == null || this.fichierSource.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
      jfc.setDialogTitle(TwoXmiBundle.getText("choixXmlSource"));
      int choix = jfc.showOpenDialog(null);
      if (choix != JFileChooser.CANCEL_OPTION) {
        this.fichierSource = jfc.getSelectedFile().getAbsolutePath();
      }
      else {
        Transformation.ANNULE = true;
        resultat = 1;
      }
    }

    if (resultat == 0) {
      // fichier xsl
      tmp = new File(this.fichierXsl);
      if (this.fichierXsl == null || this.fichierXsl.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
        jfc.setDialogTitle(TwoXmiBundle.getText("choixXsl"));
        int choix = jfc.showOpenDialog(null);
        if (choix != JFileChooser.CANCEL_OPTION) {
          this.fichierXsl = jfc.getSelectedFile().getAbsolutePath();
        }
        else {
        Transformation.ANNULE = true;
          resultat = 1;
        }
      }

      // fichier destination
      tmp = new File(this.fichierDestination);
      if (this.fichierDestination == null || this.fichierDestination.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
        jfc.setDialogTitle(TwoXmiBundle.getText("choixDestination"));
        int choix = jfc.showSaveDialog(null);
        if (choix != JFileChooser.CANCEL_OPTION) {
          this.fichierDestination = jfc.getSelectedFile().getAbsolutePath();
        }
        else {
        Transformation.ANNULE = true;
          resultat = 1;
        }
      }
    }

    try {
      if (resultat == 0) {
        Utilitaire.transformationXSL("xml", this.fichierSource, this.fichierXsl,
                                     this.fichierDestination, null);
      }
    }
    catch (Throwable ex) {
      resultat = -1;
    }
    return resultat;
  }*/

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * transforme

  public File transforme(File _fichier) {
    boolean continu = true;
    File resultat = null;
    File tmp;
    JFileChooser jfc = new JFileChooser();
    JFileChooser jfc2 = new JFileChooser();
    jfc2.setAcceptAllFileFilterUsed(false);
    jfc2.setFileSelectionMode(JFileChooser.FILES_ONLY);

    // fichier xsl
    tmp = new File(this.fichierXsl);
    if (this.fichierXsl == null || this.fichierXsl.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
      jfc.setDialogTitle(TwoXmiBundle.getText("choixXsl"));
      jfc.setFileFilter(new SimpleFileFilter("xsl", ".XSL"));
      int choix = jfc.showOpenDialog(null);
      if (choix != JFileChooser.CANCEL_OPTION) {
        this.fichierXsl = jfc.getSelectedFile().getAbsolutePath();
      }
      else {
        Transformation.ANNULE = true;
        continu = false;
      }
    }

    // fichier destination
    if (continu) {
      tmp = new File(this.fichierDestination);
      if (this.fichierDestination == null || this.fichierDestination.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
        jfc2.setDialogTitle(TwoXmiBundle.getText("choixDestination"));
        jfc2.setFileFilter(new SimpleFileFilter("xmi", ".XMI"));
        int choix = jfc2.showSaveDialog(null);
        if (choix != JFileChooser.CANCEL_OPTION) {
          //Modification Julien 2xmi
          this.fichierDestination = jfc2.getSelectedFile().getAbsolutePath();
          if (!this.fichierDestination.toLowerCase().endsWith(".xmi")) {
            this.fichierDestination = this.fichierDestination + ".xmi";
          }
        }
        else {
        Transformation.ANNULE = true;
          continu = false;
        }
      }
    }

    try {
      if (continu) {
        Utilitaire.transformationXSL("xml", _fichier.getAbsolutePath(), this.fichierXsl,
                                     this.fichierDestination, null);
        resultat = new File(this.fichierDestination);
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }

    return resultat;
  }*/

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * transforme
   *
   * @return File

  public File transforme(File _fichier, String _repertoireSortie) {
    boolean continu = true;
    File resultat = null;
    File tmp;
    // Modif Julien 2xmi
    File rep=new File(_repertoireSortie);
    rep.mkdir();
    JFileChooser jfc = new JFileChooser();
    JFileChooser jfc2 = new JFileChooser(rep);
    //jfc2.setCurrentDirectory(rep);
    jfc2.setAcceptAllFileFilterUsed(false);
    jfc2.setFileSelectionMode(JFileChooser.FILES_ONLY);
    // Modif Julien 2xmi

    // fichier xsl
    tmp = new File(this.fichierXsl);
    if (this.fichierXsl == null || this.fichierXsl.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
      jfc.setDialogTitle(TwoXmiBundle.getText("choixXsl"));
      jfc.setFileFilter(new SimpleFileFilter("xsl", ".XSL"));
      int choix = jfc.showOpenDialog(null);
      if (choix != JFileChooser.CANCEL_OPTION) {
        this.fichierXsl = jfc.getSelectedFile().getAbsolutePath();
      }
      else {
        Transformation.ANNULE = true;
        continu = false;
      }
    }

    // fichier destination
    if (continu) {
      tmp = new File(this.fichierDestination);
      if (this.fichierDestination == null || this.fichierDestination.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
        jfc2.setDialogTitle(TwoXmiBundle.getText("choixDestination"));
        jfc2.setFileFilter(new SimpleFileFilter("xmi", ".XMI"));
        int choix = jfc2.showSaveDialog(null);
        if (choix != JFileChooser.CANCEL_OPTION) {
          this.fichierDestination = jfc2.getSelectedFile().getAbsolutePath();
          if (!this.fichierDestination.toLowerCase().endsWith(".xmi")) {
            this.fichierDestination = this.fichierDestination + ".xmi";
          }
        }
        else {
        Transformation.ANNULE = true;
          continu = false;
        }
      }
    }

    try {
      if (continu) {
        Utilitaire.transformationXSL("xml", _fichier.getAbsolutePath(), this.fichierXsl,
                                     this.fichierDestination, null);
        resultat = new File(this.fichierDestination);
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }

    return resultat;
  }*/

  /**
   * transforme
   *
   * @param _fichierSource File
   * @param _formatCible String
   * @param _repertoireExport String
   * @return File
   */
  public File transforme(File _fichierSource, String _extensionCible, String _formatCible, String _repertoireExport) {
    //!!!!!!!!!! Utilisee actuellement pour exporter en XMI (XMI seulement)
    boolean continu = true;
    File resultat = null;
    File tmp;
    // Modif Julien 2xmi
    File rep=new File(_repertoireExport);
    rep.mkdir();
    JFileChooser jfc = new JFileChooser();
    JFileChooser jfc2 = new JFileChooser(rep);
    //jfc2.setCurrentDirectory(rep);
    jfc2.setAcceptAllFileFilterUsed(false);
    jfc2.setFileSelectionMode(JFileChooser.FILES_ONLY);
    // Modif Julien 2xmi

    // fichier xsl
    tmp = new File(this.fichierXsl);
    if (this.fichierXsl == null || this.fichierXsl.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
      jfc.setDialogTitle(TwoXmiBundle.getText("choixXsl"));
      jfc.setFileFilter(new SimpleFileFilter("xsl", ".XSL"));
      int choix = jfc.showOpenDialog(null);
      if (choix != JFileChooser.CANCEL_OPTION) {
        this.fichierXsl = jfc.getSelectedFile().getAbsolutePath();
      }
      else {
        Transformation.ANNULE = true;
        continu = false;
      }
    }

    // fichier destination
    if (continu) {
      tmp = new File(this.fichierDestination);
      if (this.fichierDestination == null || this.fichierDestination.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
        jfc2.setDialogTitle(TwoXmiBundle.getText("choixDestination"));
        jfc2.setFileFilter(new SimpleFileFilter(_extensionCible, "." + _extensionCible.toUpperCase()));
        int choix = jfc2.showSaveDialog(null);
        if (choix != JFileChooser.CANCEL_OPTION) {
          this.fichierDestination = jfc2.getSelectedFile().getAbsolutePath();
          if (!this.fichierDestination.toLowerCase().endsWith("." + _extensionCible)) {
            this.fichierDestination = this.fichierDestination + "." + _extensionCible;
          }
        }
        else {
        Transformation.ANNULE = true;
          continu = false;
        }
      }
    }

    try
    {
      if (continu)
      {
        // début modif 2xmi - Albert - NCM01 L'export xmi ne dit pas qu'un fichier de même nom existe déjà avant de l'écraser
        resultat = new File(this.fichierDestination);
        // on vérifie si le fichier destination existe déjà
        if(resultat.exists())
        {
          // le fichier existe déjà, une fenetre "oui / non" de confirmation d'écrasement apparaît
          int choice = JOptionPane.showConfirmDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("msgConfirmEcrasement"), Application.getApplication().getTraduction("msgSauvegarde"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
          if (choice != JOptionPane.YES_OPTION)
          {
            //si l'utlisateur nerépond pas oui alors on ne touche pas au fichier
            resultat = null;
          }
          else
          {
              // l'utilisateur désire écraser l'ancien fichier
              // on doit vérifier si le fichier est accessible en ecriture ou s'il n'est pas en cours d'utilisation
              if(resultat.canWrite())
              {
                Utilitaire.transformationXSL(_formatCible, _fichierSource.getAbsolutePath(), this.fichierXsl,this.fichierDestination, null);
                /*placement_evolution_validationXMI
                ValidationXMI vxmi = new ValidationXMI(Application.getApplication().getConfigPropriete("fichier2xmi"), this.fichierDestination);
                String resultatValidation = vxmi.LancerValidation();
                if(resultatValidation != null)
                  JOptionPane.showMessageDialog(null, resultatValidation, Application.getApplication().getTraduction("XMINotValidTitle"), JOptionPane.INFORMATION_MESSAGE);*/

              }
              else
              {
                  // le fichier est protégé en écriture, on doit donc avertir l'utilisateur
                  JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),Application.getApplication().getTraduction("ERR_FichierProtegeEnEcriture"),Application.getApplication().getTraduction("ERR"),JOptionPane.WARNING_MESSAGE);
                  resultat = null;
              }
          }
        }
        else
        {
            // il n'existe aucun fichier suceptible d'être écrasé
            // on peut donc exporter le fichier xmi ( transformation du tmp.dpe en *.xmi ) !!
            Utilitaire.transformationXSL(_formatCible, _fichierSource.getAbsolutePath(), this.fichierXsl,this.fichierDestination, null);
            /*placement_evolution_validationXMI
            ValidationXMI vxmi=new ValidationXMI(Application.getApplication().getConfigPropriete("fichier2xmi"), this.fichierDestination);
            String resultatValidation=vxmi.LancerValidation();
            if(resultatValidation != null)
              JOptionPane.showMessageDialog(null, resultatValidation, Application.getApplication().getTraduction("XMINotValidTitle"), JOptionPane.INFORMATION_MESSAGE);*/

        }
        // fin modif 2xmi Albert NCM01
      }
    }
    catch (Throwable ex)
   {
     ex.printStackTrace();
   }

    return resultat;
  }

  /**
   * Transforme un fichier en un autre dans un format défini
   *
   * @param _formatCible String
   * @param _fichierSource File
   * @param _fichierCible File
   * @return File
   */
  public File transforme(String _formatCible, File _fichierSource, File _fichierCible) {
    //!!!!!!!!!! Utilisee actuellement pour le fichier xmi utilise pour le PDF
    //et pour celui qui est dans l'archive DPC
    boolean continu = true;
    JFileChooser jfc = new JFileChooser();
    File resultat = null;
    File tmp;

    // fichier xsl
    tmp = new File(this.fichierXsl);
    if (this.fichierXsl == null || this.fichierXsl.equalsIgnoreCase("") || !tmp.exists() || !tmp.isFile()) {
      jfc.setDialogTitle(TwoXmiBundle.getText("choixXsl"));
      jfc.setFileFilter(new SimpleFileFilter("xsl", ".XSL"));
      int choix = jfc.showOpenDialog(null);
      if (choix != JFileChooser.CANCEL_OPTION) {
        this.fichierXsl = jfc.getSelectedFile().getAbsolutePath();
      }
      else {
        Transformation.ANNULE = true;
        continu = false;
      }
    }

    try {
      if (continu) {
        Utilitaire.transformationXSL(_formatCible, _fichierSource.getAbsolutePath(), this.fichierXsl,
                                     _fichierCible.getAbsolutePath(), null);
        resultat = _fichierCible;
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }

    return resultat;
  }

//----------------------------------------------------------------------------
// Interfaces avec un fichier XML
  /**
   * M?thode pour retourner un fragment de fichier XML
   *
   * @return Fragment XML
   */
  public SimpleXmlElement toSimpleXmlElement() {
    SimpleXmlElement racineFragment = new SimpleXmlElement("transformation");
    return racineFragment;
  }

  /**
   * M?thode statique pour cr?er un objet Transformation ? partir d'un fragment
   * de fichier XML
   *
   * @param _simpleXMLElement Fragment XML
   * @return Retourne une instance de Transformation
   */
  public static Transformation create(SimpleXmlElement _simpleXMLElement) {
    Transformation instance = new Transformation();
    instance.setFichierSource(_simpleXMLElement.getAttribute("fichierSource"));
    instance.setFichierXsl(_simpleXMLElement.getAttribute("fichierXsl"));
    instance.setFichierDestination(_simpleXMLElement.getAttribute(
        "fichierDestination"));
    _simpleXMLElement.setUserObject(instance);
    return instance;
  }
}
