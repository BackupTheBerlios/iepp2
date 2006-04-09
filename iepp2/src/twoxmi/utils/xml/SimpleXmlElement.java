package twoxmi.utils.xml;

import java.util.* ;



/**
 * Classe pour modéliser un élément d'un fichier XML. Cet élément peut contenir
 * d'autres éléments.<br/>
 * Permet de stocker les nom, attributs et sous éléments d'un élément.
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public class SimpleXmlElement
{
  /**
   * Nom de l'élément
   */
  private String nom = null ;

  /**
   * Sous éléments de l'élément. Collection de SimpleXMLElement
   */
  private ArrayList fils = null ;

  /**
   * Attributs de l'élément
   */
  private Map attributs = null ;

  /**
   * PCDATA de l'élément (caractères entre les balises début et fin)
   */
  private String pcData = null ;

  /**
   * Objet utilisateur pour personnaliser l'élément
   */
  private SimpleXmlInterfacable userObject = null ;

  /**
   * Constructeur pour initialiser un élément
   *
   * @param _nodeName Nom de l'élément (nom de la balise XML)
   */
  public SimpleXmlElement (String _nodeName)
  {
    this.nom = _nodeName ;
    this.fils = new ArrayList () ;
    this.attributs = new HashMap () ;
  }



  /**
   * Remplacer l'élément XML générique par un objet utilisateur
   *
   * @param _object L'objet spécifique
   */
  public void setUserObject (SimpleXmlInterfacable _object)
  {
    this.userObject = _object ;
  }



  /**
   * Méthode pour définir le PCDATA de l'élément
   *
   * @param _pcData PCDATA pour l'élément
   */
  public void setPCDATA (String _pcData)
  {
    this.pcData = _pcData ;
  }



  /**
   * Méthode pour obtenir le PCDATA de l'élément
   *
   * @return Le PCDATA sous forme de String
   */
  public String getPCDATA ()
  {
    return this.pcData ;
  }



  /**
   * Obtenir l'objet utilisateur de l'élément
   *
   * @return L'objet utilisateur si définit, null sinon
   */
  public SimpleXmlInterfacable getUserObject ()
  {
    return this.userObject ;
  }



  /**
   * Méthode pour obtenir le nom de l'élément
   *
   * @return Nom de l'élément
   */
  public String getName ()
  {
    return this.nom ;
  }



  /**
   * Méthode pour définir un attribut de l'élément
   *
   * @param _attributeName Nom de l'attribut à définir
   * @param _attributeValue Valeur de l'attribut
   */
  public void setAttribute (String _attributeName, String _attributeValue)
  {
    this.attributs.put (_attributeName, _attributeValue) ;
  }



  /**
   * Méthode pour obtenir la valeur d'un attribut de l'élément
   *
   * @param _attributeName Nom de l'attribut
   * @return Valeur de l'attribut
   */
  public String getAttribute (String _attributeName)
  {
    return (String)this.attributs.get (_attributeName) ;
  }



  /**
   * Méthode pour tester l'existence de certains attributs pour l'élément
   *
   * @param _attributesName Liste d'attributs que l'élément doit vérifier
   * @return Renvoi true si tous les attributs passés en paramètre sont possédés,
   * false sinon
   */
  public boolean hasAttributes (List _attributesName)
  {
    boolean resultat = true ;
    for (Iterator it = _attributesName.iterator () ; it.hasNext () ; )
    {
      if (!this.attributs.containsKey (it.next ()))
      {
	resultat = false ;
	break ;
      }
    }
    return resultat ;
  }



  /**
   * Méthode pour supprimer un attribut de l'élément
   *
   * @param _attributeName Nom de l'attribut à supprimer
   * @return Renvoi la valeur de l'attribut supprimé
   */
  public String removeAttribute (String _attributeName)
  {
    return (String)this.attributs.remove (_attributeName) ;
  }



  /**
   * Ajouter un sous élément à la liste des fils
   *
   * @param _simpleXMLElement Sous élément à ajouter
   * @return Retourne true si l'élément passé en paramètre est ajouté, false sinon
   */
  public boolean addChild (SimpleXmlElement _simpleXMLElement)
  {
    return this.fils.add (_simpleXMLElement) ;
  }



  /**
   * Supprime le sous élément passé en paramètre
   *
   * @param _simpleXMLElement Sous élément à supprimer
   * @return Retourne true si l'élément est supprimé, false sinon
   */
  public boolean removeChild (SimpleXmlElement _simpleXMLElement)
  {
    return this.fils.remove (_simpleXMLElement) ;
  }



  /**
   * Supprime le sous élément dont la position est passée paramètre
   *
   * @param _index Position du sous élément à supprimer
   * @return Retourne l'élément supprimé, null sinon
   */
  public SimpleXmlElement removeChild (int _index)
  {
    return (SimpleXmlElement)this.fils.remove (_index) ; // réussi même si index invalide
  }



  /**
   * Méthode pour pouvoir itérer sur les sous éléments directs
   *
   * @return Itérateur sur les sous éléments de l'élément
   */
  public Iterator childrenIterator ()
  {
    return this.fils.iterator () ;
  }



  /**
   * Méthode pour pouvoir itérer sur les attributs
   *
   * @return Itérateur sur les attributs de l'élément
   */
  public Iterator attributesIterator ()
  {
    return this.attributs.keySet ().iterator () ;
  }



  /**
   * Méthode pour obtenir tous les sous éléments ayant un certain nom.
   *
   * @param _nodeName Le nom des sous éléments à chercher
   * @param _allLevel Définir s'il faut explorer en profondeur ou pas
   * @return Itérateur sur les sous éléments de nom _nodeName
   */
  public Iterator getChildrenByName (String _nodeName, boolean _allLevel)
  {
    LinkedList resultat = new LinkedList () ;
    for (Iterator it = this.childrenIterator () ; it.hasNext () ; )
    {
      SimpleXmlElement simpleXMLElement = (SimpleXmlElement) it.next () ;
      if (_nodeName.equalsIgnoreCase (simpleXMLElement.getName ()))
      {
	resultat.add (simpleXMLElement) ;
      }
      if (_allLevel)
      {
	for (Iterator it2 = simpleXMLElement.getChildrenByName (_nodeName, true) ;
			    it2.hasNext () ; )
	{
	  resultat.add (it2.next ()) ;
	}
      }
    }
    return resultat.iterator () ;
  }



  /**
   * Méthode pour obtenir tous les sous éléments ayant une certaine valeur
   * pour certains attributs
   *
   * @param _attributes Associations {attribut - valeur} à vérifier
   * @return Itérateur sur les sous éléments vérifiant la condition
   */
  public Iterator getChildrenByAttributeValue (HashMap _attributes)
  {
    LinkedList resultat = new LinkedList () ;
    for (Iterator it = this.childrenIterator () ; it.hasNext () ; )
    {
      SimpleXmlElement simpleXMLElement = (SimpleXmlElement) it.next () ;
      if (simpleXMLElement.checkAttributes (_attributes))
      {
	resultat.add (simpleXMLElement) ;
      }
      // appel récursif et récupération du résultat
      for (Iterator it2 = simpleXMLElement.getChildrenByAttributeValue (
	  _attributes) ; it2.hasNext () ; )
      {
	resultat.add (it2.next ()) ;
      }
    }
    return resultat.iterator () ;
  }



  /**
   * Méthode pour vérifier si l'élément possède certains attributs et valeurs
   * associées
   *
   * @param _attributes Les attributs et leur valeur à vérifier
   * @return Renvoi true si l'élément possède les attributs et leur valeur
   * associée, false sinon
   */
  public boolean checkAttributes (HashMap _attributes)
  {
    boolean resultat = true ;
    for (Iterator it = _attributes.keySet ().iterator () ; it.hasNext () ; )
    {
      String attribut = (String) it.next () ;
      if (!this.attributs.containsKey (attribut))
      {
	resultat = false ; // l'attribut n'est pas possédé par l'élément
	break ;
      }
      if (! ( (String) _attributes.get (attribut)).equalsIgnoreCase (this.
	  getAttribute (attribut)))
      {
	resultat = false ; // la valeur attendu n'est pas la valeur obtenu
	break ;
      }
    }
    return resultat ;
  }



  /**
   * Méthode pour obtenir le ième fils de l'élément
   *
   * @param _index Index du fils demandé
   * @return SimpleXMLElement Le fils demandé, null si index non valide
   */
  public SimpleXmlElement getChild (int _index)
  {
    if (_index < this.fils.size ())
    {
      return (SimpleXmlElement)this.fils.get (_index) ;
    }
    return null ;
  }



  /**
   * getChildCount
   *
   * @return int
   */
  public int getChildCount ()
  {
    return this.fils.size () ;
  }



  public String toString ()
  {
    if (this.userObject == null)
    {
      return this.nom ;
    }
    return this.userObject.toString () ;
  }
}
