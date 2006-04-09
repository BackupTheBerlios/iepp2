package twoxmi;

import java.io.File;

import twoxmi.domain.Outil;
import twoxmi.domain.Exportation;
import twoxmi.utils.TwoXmiBundle;
import javax.swing.JOptionPane;

/**
 * Classe Facade pour accéder aux fonctionnalités de 2XMI
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public class Facade2XMI {
  /**
   * Manager pour le fichier de configuration
   */
  private static FichierConfigurationManager fichierConfigurationManager = null;

  /**
   * Le fichier de configuration de 2XMI
   */
  public static final File fichierConfiguration = new File("conf/2xmi_conf.xml");

  /**
   * Constructeur par défaut
   */
  public Facade2XMI() throws Exception {
    Facade2XMI.fichierConfigurationManager = new FichierConfigurationManager(Facade2XMI.fichierConfiguration);
    if (!Facade2XMI.fichierConfigurationManager.marshal()) {
      throw new Exception(TwoXmiBundle.getText("echecInitXmi"));
    }
  }

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * Méthode réalisant une exportation pour un certain outil et vers un certain format
   *
   * @param _nomAppelant L'outil demandant l'exportation
   * @param _formatCible Format vers lequel exporter
   * @return Renvoi true si réussite, false sinon

  public int exporter(String _nomAppelant, String _formatCible)
  {
    int resultat=-1;
    Outil outil=Facade2XMI.fichierConfigurationManager.getOutilByName(_nomAppelant);
    if(outil!=null)
    {
      // outil connu par 2xmi
      Exportation exportation=outil.getExportationByName(_formatCible);
      if(exportation!=null)
      {
        // exportation enregistree pour cet outil
        resultat=exportation.exporter();
      }
    }
    return resultat;
  }*/


  /**
   * Méthode réalisant une exportation pour un certain outil et vers un certain format
   *
   * @param _nomAppelant L'outil demandant l'exportation
   * @param _extensionCible Extension du fichier exporté
   * @param _formatCible Format vers lequel exporter
   * @param _fichierSource Fichier source
   * @return Renvoi true si réussite, false sinon
   */
  public int exporter(String _nomAppelant, String _extensionCible, String _formatCible, String _repertoireExport, File _fichierSource) {
    //!!!!!!!!!! Utilisee actuellement pour exporter en XMI (XMI seulement)
    int resultat = -1;
    Outil outil = Facade2XMI.fichierConfigurationManager.getOutilByName(_nomAppelant);
    if (outil != null) {
      // outil connu par 2xmi
      Exportation exportation = outil.getExportationByName(_extensionCible);
      if (exportation != null) {
        // exportation enregistree pour cet outil
        resultat = exportation.exporter(_formatCible, _repertoireExport, _fichierSource.getAbsolutePath());
      }
    }
    else {
      String message = TwoXmiBundle.getText("outilNonDefini");
      JOptionPane.showMessageDialog(null, message, "2Xmi", JOptionPane.INFORMATION_MESSAGE);
    }
    return resultat;
  }


  /**
   * Méthode réalisant une exportation pour un certain outil et vers un certain format
   *
   * @param _nomAppelant L'outil demandant l'exportation
   * @param _extensionCible Extension du fichier exporté
   * @param _formatCible Format vers lequel exporter
   * @param _fichierSource Fichier source
   * @param _fichierCible Fichier cible
   * @return Renvoi true si réussite, false sinon
   */
  public int exporter(String _nomAppelant, String _extensionCible, String _formatCible, File _fichierSource, File _fichierCible) {
    //!!!!!!!!!! Utilisee actuellement pour le fichier XMI utilise pour le PDF
    //et pour celui qui est dans l'archive DPC
    int resultat = -1;
    Outil outil = Facade2XMI.fichierConfigurationManager.getOutilByName(_nomAppelant);
    if (outil != null) {
      // outil connu par 2xmi
      Exportation exportation = outil.getExportationByName(_extensionCible);
      if (exportation != null) {
        // exportation enregistrée pour cet outil
        resultat = exportation.exporter(_formatCible, _fichierSource, _fichierCible);
      }
    }
    else {
      String message = TwoXmiBundle.getText("outilNonDefini");
      JOptionPane.showMessageDialog(null, message, "2Xmi", JOptionPane.INFORMATION_MESSAGE);
    }
    return resultat;
  }

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * Méthode réalisant une exportation pour un certain outil et vers un certain
   * format et dans un certain repertoire
   *
   * @param _nomAppelant L'outil demandant l'exportation
   * @param _formatCible Format vers lequel exporter
   * @param _fichierSource Fichier source
   * @param _repertoireSortie Chemin du repertoire de sortie
   *
   * @return Renvoi true si reussite, false sinon

  public int exporter(String _nomAppelant, String _formatCible, String _fichierSource, String _repertoireSortie){
    int resultat=-1;
    Outil outil=Facade2XMI.fichierConfigurationManager.getOutilByName(_nomAppelant);
    if(outil!=null)
    {
      // outil connu par 2xmi
      Exportation exportation=outil.getExportationByName(_formatCible);
      if(exportation!=null)
      {
        // exportation enregistree pour cet outil
        resultat=exportation.exporter(_fichierSource, _repertoireSortie);
      }
    }
    return resultat;
  }*/
}
