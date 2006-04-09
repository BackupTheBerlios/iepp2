package twoxmi.utils.xml;

import twoxmi.utils.xml.*;

/**
 * Interface pour pouvoir interfacer une classe avec un fichier XML
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public interface SimpleXmlInterfacable
{
  /*****************************************
   *              ATTENTION                *
   * Rajouter dans la classe implémentant  *
   * cette interface la méthode statique   *
   * dont la javadoc suit (en commentaire) *
   ****************************************/
  // Méthode pour créer un utilisateur à partir d'un élément XML
  // @param _simpleXMLElement L'élément XML
  // @return Renvoi un objet si création possible depuis le paramètre, null sinon
  // public static Object create (SimpleXMLElement _simpleXMLElement) ;



  /**
   * Méthode pour créer un élément XML à partir d'un objet
   *
   * @return Renvoi un élément XML
   */
  public SimpleXmlElement toSimpleXmlElement () ;
}
