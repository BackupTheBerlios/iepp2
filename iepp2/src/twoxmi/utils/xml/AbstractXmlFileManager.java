package twoxmi.utils.xml;

import java.io.File ;
import java.io.IOException ;
import javax.xml.parsers.ParserConfigurationException ;

import org.xml.sax.SAXException ;
import java.util.Map ;
import java.util.HashMap ;
import twoxmi.utils.Tracer ;
import java.util.logging.Level ;
import twoxmi.utils.*;


/**
 * Classe abstraite pour gérer le contenu d'un fichier XML
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public abstract class AbstractXmlFileManager
{
  /**
   * Interface avec le fichier XML
   */
  protected SimpleXmlInterface simpleXmlInterface = null ;

  /**
   * Fichier XML à manipuler
   */
  protected File fichier = null ;

  /**
   * Element racine de la structure de données. Ne sert qu'à pointer sur le début
   * de la structure (cette racine est purement logique)
   */
  protected SimpleXmlElement racineStructureDonnees = null ;

  /**
   * Elements de la structure de données
   */
  protected Map listeElements = null ;

  /**
   * Constructeur par défaut
   *
   * @param _fichierXML Fichier XML à gérer
   * @throws IOException Problème d'accès au fichier
   */
  protected AbstractXmlFileManager (File _fichierXML) throws IOException
  {
    Tracer.entering (new Object[]{}) ;
    this.listeElements = new HashMap () ;
    this.fichier = _fichierXML ;
    this.simpleXmlInterface = new SimpleXmlInterface (_fichierXML) ;
  }



  /**
   * Méthode pour charger en mémoire le fichier XML (objets SimpleXmlElement)
   *
   * @return Retourne true si chargement sans problème, false sinon
   */
  protected boolean load()
  {
      Tracer.entering(new Object[]{});
      boolean resultat = true;
      try
      {
          this.simpleXmlInterface.read();
          this.racineStructureDonnees = this.simpleXmlInterface.getRoot();
          //System.out.println (this.racineStructureDonnees.getName());
      }
      catch (Throwable ex)
      {
          Tracer.log(Level.SEVERE, "throw", ex);
          resultat = false;
      }
      return resultat;
  }



  /**
   * Méthode pour écrire dans le fichier XML initial les objets en mémoire
   *
   * @return Retourne true si écriture sans problème, false sinon
   */
  protected boolean save ()
  {
    Tracer.entering (new Object[]
                     {}) ;
    boolean resultat = true ;
    resultat = this.simpleXmlInterface.write (this.fichier) ;
    return resultat ;
  }



  /**
   * Méthode pour obtenir la racine de la structure de données
   *
   * @return Renvoi l'élément racine
   */
  public SimpleXmlElement getRoot ()
  {
    return this.simpleXmlInterface.getRoot () ;
  }



  /**
   * Méthode à implémenter dont le but est de 'transformer' les SimpleXmlElement
   * utiles en objets Java. La 'transformation' se fait par appel de la méthode
   * <code>setUserObjets</code> de la classe SimpleXMLElement
   *
   * @return Renvoi true si opération exécutée sans problème, false sinon
   */
  public abstract boolean marshal () ;



  /**
   * Mécanisme contraire à la méthode <code>marshal</code>. Le but est de
   * reconstruire les SimpleXmlElement à partir des objets 'transformés'
   *
   * @return Renvoi true si opération exécutée sans problème, false sinon
   */
  public abstract boolean unMarshal () ;
}
