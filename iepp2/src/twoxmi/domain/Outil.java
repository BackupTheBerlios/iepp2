package twoxmi.domain;

import java.util.* ;

import twoxmi.utils.xml.SimpleXmlInterfacable;
import twoxmi.utils.xml.SimpleXmlElement;


/**
 * Classe modélisant un outil autorisé à utiliser 2XMI
 *
 */
public class Outil
    implements SimpleXmlInterfacable
{
  /**
   * Nom de l'outil
   */
  private String name = null ;

  /**
   * Exportations possibles pour cet outil
   */
  private HashMap listeExportations = null ;

  /**
   * Constructeur par défaut
   */
  public Outil ()
  {
    this.listeExportations = new HashMap () ;
  }



  /**
   * getName
   *
   * @return String
   */
  public String getName ()
  {
    return this.name ;
  }



  /**
   * setName
   *
   * @param _name String
   */
  public void setName (String _name)
  {
    this.name = _name ;
  }




  /**
   * getExportationByName
   *
   * @param string String
   * @return Exportation
   */
  public Exportation getExportationByName (String _nomExportation)
  {
    return (Exportation) this.listeExportations.get(_nomExportation) ;
  }



  /**
   * addExportation
   *
   * @param exportation Exportation
   */
  private void addExportation (Exportation _exportation)
  {
    this.listeExportations.put (_exportation.getName (), _exportation) ;
  }




  //----------------------------------------------------------------------------
  // Interfaces avec un fichier XML
  /**
   * Méthode pour retourner un fragment de fichier XML
   *
   * @return Fragment XML
   */
  public SimpleXmlElement toSimpleXmlElement ()
  {
    SimpleXmlElement racineFragment = new SimpleXmlElement ("outil") ;
    return racineFragment ;
  }



  /**
   * Méthode statique pour créer un objet Outil à partir d'un fragment
   * de fichier XML
   *
   * @param _simpleXMLElement Fragment XML
   * @return Retourne une instance de Outil
   */
  public static Outil create (SimpleXmlElement _simpleXMLElement)
  {
    Outil instance = new Outil () ;
    instance.setName (_simpleXMLElement.getAttribute ("name")) ;

    Iterator exportationIt = _simpleXMLElement.getChildrenByName ("exportation", false) ;
    while (exportationIt.hasNext ())
    {
      SimpleXmlElement elementCourant = (SimpleXmlElement) exportationIt.next () ;
      Exportation exportation = Exportation.create (elementCourant) ;
      instance.addExportation (exportation) ;
    }

    _simpleXMLElement.setUserObject (instance) ;
    return instance ;
  }
}
