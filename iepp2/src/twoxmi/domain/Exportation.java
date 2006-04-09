package twoxmi.domain;

import java.io.*;
import java.util.*;

import twoxmi.utils.xml.*;

/**
 * Classe mod?lisant une exportation vers un format de fichier ? partir d'un autre
 */
public class Exportation
    implements SimpleXmlInterfacable {
  /**
   * Le nom de l'exportation (en pratique, le format destination)
   */
  private String name = null;

  /**
   * Liste des transformation pour cette exportation
   */
  private Vector listeTransformations = null;

  /**
   * Constructeur par d?faut
   */
  public Exportation() {
    this.listeTransformations = new Vector();
  }

  /**
   * setName
   *
   * @param string String
   */
  private void setName(String _name) {
    this.name = _name;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return this.name;
  }

  public Vector getTransformations() {
    return this.listeTransformations;
  }

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * exporter
   *
   * @return boolean

  public int exporter() {
    int resultat = 0;
    for (int i = 0; i < this.listeTransformations.size(); i++) {
      Transformation transformation = (Transformation)this.listeTransformations.
          elementAt(i);
      if (transformation.transforme() == -1){
        break; // inutile de continuer...
      }
    }

    if (Transformation.ANNULE == true)
      resultat = 1;

    return resultat;
  }*/

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * exporter
   *
   * @return boolean

  public int exporter(String _fichierSource) {
    int resultat = 0;
    File tmp = new File(_fichierSource);
    for (int i = 0; i < this.listeTransformations.size(); i++) {
      Transformation transformation = (Transformation)this.listeTransformations.
          elementAt(i);
      tmp = transformation.transforme(tmp);
      if (tmp == null) {
        resultat = -1;
        break;
      }
    }

    if (Transformation.ANNULE == true)
      resultat = 1;

    return resultat;
  }*/

  /** Non utilise pour le moment
   * Ne pas supprimer : 2xmi est reutilisable, extensible et configurable
   *
   * exporter
   *
   * @return boolean

  public int exporter(String _fichierSource, String _repertoireSortie) {
    int resultat = 0;
    File tmp = new File(_fichierSource);
    for (int i = 0; i < this.listeTransformations.size(); i++) {
      Transformation transformation = (Transformation)this.listeTransformations.
          elementAt(i);
      tmp = transformation.transforme(tmp, _repertoireSortie);
      if (tmp == null) {
        resultat = -1;
        break;
      }
    }

    if (Transformation.ANNULE == true)
      resultat = 1;

    return resultat;
  }*/

  /**
   * exporter
   *
   * @param _formatCible Format du fichier exporté
   * @param _repertoireExport Répertoire par défaut pour proposer la sortie du fichier
   * @param _fichierSource String
   * @return boolean
   */
  public int exporter(String _formatCible, String _repertoireExport, String _fichierSource) {
    //!!!!!!!!!! Utilisee actuellement pour exporter en XMI (XMI seulement)
    int resultat = 0;
    File tmp = new File(_fichierSource);
    for (int i = 0; i < this.listeTransformations.size(); i++) {
      Transformation transformation = (Transformation)this.listeTransformations.
          elementAt(i);
      tmp = transformation.transforme(tmp, this.name, _formatCible, _repertoireExport);
      if (tmp == null) {
        resultat = -1;
        break;
      }
    }

    if (Transformation.ANNULE == true)
      resultat = 1;
    return resultat;
  }

  /**
   * Exporter un fichier source vers un fichier cible dans un format spécifié
   *
   * @param _formatCible String
   * @param _fichierSource File
   * @param _fichierCible File
   * @return boolean
   */
  public int exporter(String _formatCible, File _fichierSource, File _fichierCible) {
    //!!!!!!!!!! Utilisee actuellement pour le fichier XMI utilise pour le PDF
    //et pour celui qui est dans l'archive DPC
    int resultat = 0;
    File tmp = new File(_fichierSource.getAbsolutePath());
    for (int i = 0; i < this.listeTransformations.size(); i++) {
      Transformation transformation = (Transformation)this.listeTransformations.
          elementAt(i);
      tmp = transformation.transforme(_formatCible, tmp, _fichierCible);
      if (tmp == null) {
        resultat = -1;
        break;
      }
    }

    if (Transformation.ANNULE == true)
      resultat = 1;

    return resultat;
  }

  /**
   * addTransformation
   *
   * @param transformation Transformation
   */
  private void addTransformation(Transformation _transformation) {
    this.listeTransformations.add(_transformation);
  }

  //----------------------------------------------------------------------------
  // Interfaces avec un fichier XML
  /**
   * Methode pour retourner un fragment de fichier XML
   *
   * @return Fragment XML
   */
  public SimpleXmlElement toSimpleXmlElement() {
    SimpleXmlElement racineFragment = new SimpleXmlElement("exportation");
    return racineFragment;
  }

  /**
   * M?thode statique pour creer un objet Exportation a partir d'un fragment
   * de fichier XML
   *
   * @param _simpleXMLElement Fragment XML
   * @return Retourne une instance de Exportation
   */
  public static Exportation create(SimpleXmlElement _simpleXMLElement) {
    Exportation instance = new Exportation();
    instance.setName(_simpleXMLElement.getAttribute("name"));

    Iterator transformationIt = _simpleXMLElement.getChildrenByName(
        "transformation", false);
    while (transformationIt.hasNext()) {
      SimpleXmlElement elementCourant = (SimpleXmlElement) transformationIt.next();
      Transformation transformation = Transformation.create(elementCourant);
      instance.addTransformation(transformation);
    }

    _simpleXMLElement.setUserObject(instance);
    return instance;
  }
}
