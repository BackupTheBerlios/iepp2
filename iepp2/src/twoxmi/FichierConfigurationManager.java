package twoxmi;

import java.io.* ;
import java.util.* ;

import twoxmi.domain.* ;
import twoxmi.utils.xml.AbstractXmlFileManager;
import twoxmi.utils.xml.SimpleXmlElement;


/**
 * Classe pour gérer le fichier de configuration pour 2XMI
 */
public class FichierConfigurationManager extends AbstractXmlFileManager
{
  /**
   * Liste des outils connus de 2XMI, pouvant l'utiliser
   */
  private HashMap listeOutils = null ;

  /**
   * Instance de la classe
   */
  public static FichierConfigurationManager instance = null ;

  /**
   * Nom de la balise XML contenant la définition pour un outil
   */
  public static final String OUTIL_TAG = "outil" ;

  /**
   * FichierConfigurationManager
   *
   * @param _fichier File
   * @throws IOException
   */
  public FichierConfigurationManager (File _fichier)
      throws IOException
  {
    super (_fichier) ;
    this.listeOutils = new HashMap () ;
    FichierConfigurationManager.instance = this ;
  }



  /**
   * Méthode pour créer des objets depuis le fichier XML
   * @see Méthode marshal dans utils.xml.AbstractXmlFileManager
   */
  public boolean marshal()
  {
      boolean resultat = false;
      if (this.load())
      {
          SimpleXmlElement noeud2XmiConf = this.racineStructureDonnees.getChild(0);
          //System.out.println(noeud2XmiConf.getName());
          if (noeud2XmiConf != null)
          {
              //------------------------------------------------------------------------
              // instanciation des objets Outil
              Iterator it = noeud2XmiConf.getChildrenByName(
                  FichierConfigurationManager.OUTIL_TAG, false);
              while (it.hasNext())
              {
                  SimpleXmlElement elementCourant = (SimpleXmlElement) it.next();
                  Outil outil = Outil.create(elementCourant);
                  this.listeOutils.put(outil.getName(), outil);
              }
          }
          resultat = true;
      }
      return resultat;
  }



  /**
   * Méthode pour créer le fichier XML depuis les objets métier
   * @see Méthode unmarshal dans utils.xml.AbstractXmlFileManager
   */
  public boolean unMarshal ()
  {
    boolean resultat = true ;
    return resultat ;
  }



  /**
   * getInstance
   *
   * @return FichierConfigurationManager
   */
  public static FichierConfigurationManager getInstance ()
  {
    return FichierConfigurationManager.instance ;
  }



  /**
   * getOutils
   */
  public HashMap getOutils ()
  {
    return this.listeOutils ;
  }



  /**
   * getOutilByName
   *
   * @param _nomAppelant
   * @return Renvoi un objet outil si existant, null sinon
   */
  public Outil getOutilByName (String _nomAppelant)
  {
    return (Outil) this.listeOutils.get(_nomAppelant) ;
  }
}
