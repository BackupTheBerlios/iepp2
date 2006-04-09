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


package iepp.application.ageneration;

import iepp.Application;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.ElementPresentation;
import iepp.domaine.Guide;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.domaine.PaquetagePresentation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Classe permettant de garder la configuration de la génération
 * c'est-à-dire, toutes les options de génération que l'utilisateur
 * a rentré dans la boîte de dialogue de génération.
 * Pour accéder à une propriété, il faut faire GenerationManager.getInstance().getxxxx()
 */
public class GenerationManager {

  /**
   * Instance unique de la classe GenerationManager
   */
  private static GenerationManager generation = new GenerationManager();
  private static String cheminGeneration;
  private static String feuilleCss;
  private static String dateGeneration;
  private static Vector listeAGenerer;
  private static Color couleur_surlign;
  private static Vector listeProduitsExterieurs;
  private static HashMap listeProduitsChanges;
  private static HashMap listeProduitsRemplaces;
  private static Vector listeProduitsSortie;
  private static String place_contenu;
  private static String place_page; // 2XMI Albert
  private static String place_assemblage; // 2XMI Albert
  private static String info_bulle;
  private static String info_bulle_activite; //2XMI Amandine
  private static String statistiques;
  private static String recapitulatif;
  private static TacheGeneration tache;

  // mettre tous les autres attributs en private static

  private static int type_page_composant = 0;
  private static int type_page_deftravail = 3;

  public static final String AVANT_CONTENU = "0";
  public static final String APRES_CONTENU = "1";

  public static final String DIAGFLOTS_PAGE = "0";      // 2XMI Albert
  public static final String DIAGACTIVITES_PAGE = "1";  // 2XMI Albert
  public static final String COMPOSANT_PAGE = "2";      // 2XMI Albert

  public static final String DIAGFLOTS2_PAGE = "0";      // 2XMI Albert
  public static final String DIAGCOMP_PAGE = "1";        // 2XMI Albert

  public static final String CONTENU_PATH = "contenu";
  public static final String APPLET_PATH = "applet";
  public static final String STYLES_PATH = "styles";
  public static final String IMAGES_PATH = "images";
  public static final String STATS_PATH = "stats";

  public static final String PRESENT = "1";
  public static final String NON_PRESENT = "0";

  /**
   * Constructeur du manager de la génération
   * Initialise les options avec des valeurs par défaut
   */
  private GenerationManager() {
    // mettre ici des valeurs par défaut
    this.setFeuilleCss(Application.getApplication().getConfigPropriete("feuille_style"));
    GenerationManager.couleur_surlign = new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_arbre")));
    GenerationManager.cheminGeneration = Application.getApplication().getConfigPropriete("repertoire_generation");
    GenerationManager.place_contenu = Application.getApplication().getConfigPropriete("place_contenu");
    GenerationManager.place_page = Application.getApplication().getConfigPropriete("place_page"); // 2XMI Albert
    GenerationManager.place_assemblage = Application.getApplication().getConfigPropriete("place_assemblage"); // 2XMI Albert
    GenerationManager.tache = null;
    GenerationManager.info_bulle = Application.getApplication().getConfigPropriete("info_bulle");
    //modif 2XMI Amandine
    GenerationManager.info_bulle = Application.getApplication().getConfigPropriete("info_bulle_activite");
    //fin modif 2XMI Amandine
    GenerationManager.statistiques = Application.getApplication().getConfigPropriete("statistiques");
    GenerationManager.recapitulatif = Application.getApplication().getConfigPropriete("recapitulatif");
  }

  /**
   * Retourne l'instance du manager de la génération
   */
  public static GenerationManager getInstance() {
    return generation;
  }

  //--------------------------------------------------//
  // Setters de propriétés							//
  //--------------------------------------------------//

  /**
   * méthode appelée lorsque la génération débute, permet
   * de sauvegarde la date de génération à afficher sur toutes les
   * pages du site
   */
  public void debuterGeneration() {
    Date current_date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    GenerationManager.dateGeneration = "[" + formatter.format(current_date) + "] ";

    //this.setFeuilleCss(Application.getApplication().getConfigPropriete("feuille_style"));
    GenerationManager.couleur_surlign = new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_arbre")));
    GenerationManager.place_contenu = Application.getApplication().getConfigPropriete("place_contenu");
    GenerationManager.place_page = Application.getApplication().getConfigPropriete("place_page");      //2XMI Albert
    GenerationManager.place_assemblage = Application.getApplication().getConfigPropriete("place_assemblage");      //2XMI Albert
  }
  /**
   * Méthode permettant d'initialiser le chemin de génération
   */
  public void setCheminGeneration(String chemin) {
    if (!chemin.equals("")) {
      GenerationManager.cheminGeneration = chemin;
    }
    else {
      GenerationManager.cheminGeneration = "./";
    }
  }

  /**
   * Méthode permettant d'initialiser la feuille de style css
   */
  public void setFeuilleCss(String nomFeuille) {
    GenerationManager.feuilleCss = nomFeuille;
  }

  /**
   * Méthode permettant d'initialiser la couleur des éléments surlignés dans l'arbre du site
   */
  public void setCouleurSurligne(Color couleur) {
    GenerationManager.couleur_surlign = couleur;
  }

  public void setListeAGenerer(Vector liste) {
    GenerationManager.listeAGenerer = liste;
  }

  public void setTypeComposant(int type) {
    GenerationManager.type_page_composant = type;
  }

  public void setTypeDefTravail(int type) {
    GenerationManager.type_page_deftravail = type;
  }

  public void setTache(TacheGeneration tache) {
    GenerationManager.tache = tache;
  }

  public static void print(String message) {
    if (GenerationManager.tache != null) {
      GenerationManager.tache.print(message);
    }
  }

  //--------------------------------------------------//
  // Getters de propriétés							//
  //--------------------------------------------------//

  /**
   * Méthode permettant de récupérer le chemin de génération
   */
  public String getCheminGeneration() {
    return (GenerationManager.cheminGeneration);
  }

  /**
   * Méthode permettant de récupérer le nom de la feuille de style css
   */
  public String getFeuilleCss() {
    return (GenerationManager.feuilleCss + "." + Application.getApplication().getConfigPropriete("extensionFeuilleStyle"));
  }

  /**
   * Méthode permettant de récupérer le nom de la dtd pour la génération du site web et du PDF
   */
  public String getDtd() {
    return (Application.getApplication().getConfigPropriete("fichierDtd") + "." + Application.getApplication().getConfigPropriete("extensionDtd"));
  }

  /**
   * Méthode permettant de récupérer la couleur des éléments surlignés dans l'arbre du site
   */

  public String getCouleurSurligne() {
    String result = "#";
    int r = GenerationManager.couleur_surlign.getRed();
    String aux = Integer.toHexString(r);
    if (aux.length() == 1) {
      result += '0';
    }
    result += aux;

    int g = GenerationManager.couleur_surlign.getGreen();
    aux = Integer.toHexString(g);
    if (aux.length() == 1) {
      result += '0';
    }
    result += aux;

    int b = GenerationManager.couleur_surlign.getBlue();
    aux = Integer.toHexString(b);
    if (aux.length() == 1) {
      result += '0';
    }
    result += aux;
    return result;
  }

  public void setPlaceContenu(String place) {
    GenerationManager.place_contenu = place;
  }

  // modif 2XMI Albert
  public void setPlacePage(String place) {
    GenerationManager.place_page = place;
  }
  // modif 2XMI Albert

  // modif 2XMI Albert
  public void setPlaceAssemblage(String place) {
    GenerationManager.place_assemblage = place;
  }
  // modif 2XMI Albert



  public void setInfoBulle(String info) {
    GenerationManager.info_bulle = info;
  }

  public void setInfoBulleActivite(String info) {
    GenerationManager.info_bulle_activite = info;
  }


  /**
   * @param configPropriete
   */
  public void setStatistiques(String configPropriete) {
    GenerationManager.statistiques = configPropriete;
  }

  public void setRecap(String recap) {
    GenerationManager.recapitulatif = recap;
  }

  /**
   * Indique si le contenu doit être placé avant ou après le diagramme
   * @return
   */
  public boolean estContenuAvant() {
    return (GenerationManager.place_contenu.equals(GenerationManager.AVANT_CONTENU));
  }

  /**
   * Indique si les info-bulles doivent être affichées
   * @return
   */
  public boolean estInfoBulle() {
    return (GenerationManager.info_bulle.equals(GenerationManager.PRESENT));
  }

  public boolean estInfoBulleActivite() {
    return (GenerationManager.info_bulle_activite.equals(GenerationManager.PRESENT));
  }

  public boolean estStatistiques() {
    return (GenerationManager.statistiques.equals(GenerationManager.PRESENT));
  }

  public boolean estRecapitulatif() {
    return (GenerationManager.recapitulatif.equals(GenerationManager.PRESENT));
  }

  /**
   * Renvoie la couleur de surlignement dans l'arbre de navigation
   */
  public Color getCouleur() {
    return GenerationManager.couleur_surlign;
  }

  /**
   * Renvoie la liste à générer (A remplacer par l'arbre)
   * @return
   */
  public Vector getListeAGenerer() {
    return (GenerationManager.listeAGenerer);
  }

  /**
   * @return
   */
  public String getDateGeneration() {
    return dateGeneration;
  }

  /**
   * Remplie la liste des produits exterieurs (n'ayant pas de presentation)
   * @param listeProduitsExterieurs Liste des produits exterieurs
   */
  public static void setListeProduitsExterieurs(Vector listeProduitsExterieurs) {
    GenerationManager.listeProduitsExterieurs = listeProduitsExterieurs;
  }

  /**
   * Recupere la liste des produits exterieurs (n'ayant pas de presentation)
   */
  public static Vector getListeProduitsExterieurs() {
    return (GenerationManager.listeProduitsExterieurs);
  }

  /**
   * Remplie la liste des produits en entree lies
   * @param listeProduitsChanges
   */
  public static void setListeProduitsChanges(HashMap listeProduitsChanges) {
    GenerationManager.listeProduitsChanges = listeProduitsChanges;
  }

  /**
   * Recupere la liste des produits en entree lies
   */
  public static HashMap getListeProduitsChanges() {
    return listeProduitsChanges;
  }

  /**
   * Remplie la liste des produits qui sont remplaces par d'autres
   * @param listeProduitsChanges
   */
  public static void setListeProduitsRemplaces(HashMap listeProduitsRemplaces) {
    GenerationManager.listeProduitsRemplaces = listeProduitsRemplaces;
  }

  /**
   * Recupere la map des produits remplaces par un produit
   */
  public static HashMap getListeProduitsRemplaces() {
    return listeProduitsRemplaces;
  }

  /**
   * Remplie la liste des produits en sortie
   * @param listeProduitsSortie
   */
  public static void setListeProduitsSortie(Vector listeProduitsSortie) {
    GenerationManager.listeProduitsSortie = listeProduitsSortie;
  }

  /**
   * Recupere la liste des produits en sortie
   * @return la liste des produits en sortie
   */
  public static Vector getListeProduitsSortie() {
    return listeProduitsSortie;
  }

  /**
   * Rajouter a la map des produits exterieurs le couple passe en parametre
   * Si la cle existe deja, on ajoute la nouvelle valeur a un vecteur, on ne remplace pas
   * @param map La map sur laquelle il faut operer
   * @param key La cle
   * @param val La valeur
   */
  public static void ajouterMapping(HashMap map, String key, IdObjetModele val) {
    Vector v;
    if (!map.containsKey(key)) {
      v = new Vector();
      map.put(key, v);
    }
    else {
      v = (Vector) map.get(key);
    }

    // V etant le vecteur resultat, on y ajoute la nouvelle valeur
    v.add(val);
  }

  /**
   * récupérer la liste des produits en entrée et vérifier s'ils sont extérieurs au processus
   * @param idComposant
   */
  public static void recupererProduitsExterieurs() {
    if (GenerationManager.tache != null) {
      GenerationManager.tache.print(Application.getApplication().getTraduction("recup_prod_exterieurs"));
    }
    //liste des produits extérieurs: les produits en entrée qui sont en sortie d'aucun composant
    Vector listeProduitsExterieurs = new Vector();

    // Liste des produits en entree lies avec d'autres
    HashMap listeProduitsChanges = new HashMap();
    HashMap listeProduitsRemplaces = new HashMap();

    // Liste des produits en sortie de composants
    Vector listeProduitsSortie = new Vector();

    Vector liste = GenerationManager.getInstance().getListeAGenerer();
    PaquetagePresentation paquet;
    IdObjetModele idComposant;
    for (int i = 0; i < liste.size(); i++) {
      if (liste.elementAt(i) instanceof IdObjetModele) {
        // composant publiable
        //on recupere l'ID du ième composant de la definition de Processus
        idComposant = (IdObjetModele) liste.elementAt(i);

        // produits en entrée de composant
        Vector listeProduitsEntree = idComposant.getProduitEntree();
        Vector listeLiens = ( (ComposantProcessus) idComposant.getRef()).getLien();

        // Ajouter tous les noms des produits en sortie
        Vector sortie = idComposant.getProduitSortie();
        for (int j = 0; j < sortie.size(); j++) {
          listeProduitsSortie.add(idComposant.getRef().toString() + "::" + sortie.elementAt(j).toString());
        }

        // Verifier s'il s'agit d'un composant vide, auquel cas il faut verifier les produits en sortie
        if (idComposant.estComposantVide()) {
          listeProduitsEntree.addAll(idComposant.getProduitSortie());
        }
        for (int j = 0; j < listeProduitsEntree.size(); j++) {
          IdObjetModele idProduit = (IdObjetModele) listeProduitsEntree.elementAt(j);
          // Si le composant n'a pas de lien, les produits ne peuvent etre lies
          if (listeLiens.size() == 0) {
            listeProduitsExterieurs.addElement(idProduit);
          }
          else {
            boolean lie = false;
            for (int k = 0; k < listeLiens.size() && !lie; k++) {
              LienProduits lien = (LienProduits) listeLiens.elementAt(k);

              if (lien.contient(idProduit)) {
                lie = true;
                // Si le produit en entree est lie, on le note pour changer son lien vers le produit lie
                IdObjetModele produitCible;
                if (lien.getProduitEntree() == idProduit) {
                  produitCible = lien.getProduitSortie();
                }
                else {
                  produitCible = lien.getProduitEntree();
                }
                listeProduitsChanges.put(idProduit.getRef().toString() + "::" + idProduit.toString(), produitCible);
                ajouterMapping(listeProduitsRemplaces, produitCible.getRef().toString() + "::" + produitCible.toString(), idProduit);
              }
            }
            if (!lie) {
              listeProduitsExterieurs.addElement(idProduit);
            }
          }
        }

        // Il faudra peut etre une boucle supplementaire pour gerer correctement le cas des triples fusions et plus
        // Il faudra dans ce cas verifier que le produit cible d'un produit change n'est pas en sortie
      }
    } // fin boucle sur listeAGenerer
    GenerationManager.setListeProduitsChanges(listeProduitsChanges);
    GenerationManager.setListeProduitsRemplaces(listeProduitsRemplaces);
    GenerationManager.setListeProduitsExterieurs(listeProduitsExterieurs);
    GenerationManager.setListeProduitsSortie(listeProduitsSortie);
  }

  /**
   *
   */
  public static void construireArbre(ArbreGeneration arbre, PrintWriter fd) {
    if (GenerationManager.tache != null) {
      GenerationManager.tache.print(Application.getApplication().getTraduction("construct_arbre"));
    }
    Vector liste = GenerationManager.getInstance().getListeAGenerer();
    PaquetagePresentation paquet;
    IdObjetModele idComposant;
    
    for (int i = 0; i < liste.size(); i++) {
      if (liste.elementAt(i) instanceof PaquetagePresentation) {
        paquet = (PaquetagePresentation) liste.elementAt(i);
        construireArbrePaquetage(arbre, paquet, fd);
      }
      else {
        // composant publiable
        //on recupere l'ID du ième composant de la definition de Processus
        idComposant = (IdObjetModele) liste.elementAt(i);
        paquet = idComposant.getPaquetagePresentation();
        if (paquet != null && !paquet.getNomFichier().equals("")) {
        	construireArbreComposant(arbre, paquet, fd);
        }
      }
    }
  }

  /**
   * Construire l'arbre correspondant au paquetage de présentation
   * @param fd
   */
  public static void construireArbrePaquetage(ArbreGeneration arbre, PaquetagePresentation paquetage, PrintWriter pwFicTree) {
    Vector liste; // liste en cours de traitement
    int i;
    ArbreGeneration nouvelArbre = null;

    paquetage.trierElement();
    liste = paquetage.getListeElement();

    // le premier élément est la racine du paquetage
    if (liste.size() >= 1) {
      ElementPresentation elem = (ElementPresentation) liste.elementAt(0);
      GElement noeud = new GPaquetagePresentation(elem, paquetage, pwFicTree);
      nouvelArbre = new ArbreGeneration(noeud);
      arbre.ajouterSousArbre(nouvelArbre);
    }

    for (i = 0; i < liste.size(); i++) {
      ElementPresentation elem = (ElementPresentation) liste.elementAt(i);
      GElement noeud = new GElement(elem, pwFicTree);
      if (elem.getNiveau() == 2) {
        // rajoute directement au nouvel arbre
        ArbreGeneration n = new ArbreGeneration(noeud);
        nouvelArbre.ajouterSousArbre(n);
      }
      else if (elem.getNiveau() >= 2) {
        String nouvelID = elem.getID_interne().substring(elem.getID_interne().indexOf("-") + 1);
        nouvelArbre.ajouterSousArbre(noeud, nouvelID);
      }
    }
  }

  /**
   * @param generation
   * @param fd
   * @param paquet
   */
  public static void construireArbreComposant(ArbreGeneration generation, PaquetagePresentation paquetage, PrintWriter fd) {
    Vector liste; // liste en cours de traitement
    int i;
    ArbreGeneration nouvelArbre = null;

    // trier
    paquetage.trierElement();
    liste = paquetage.getListeElement();
    
    // le premier élément est la racine du composant
    if (liste.size() >= 1) {
      ElementPresentation elem = (ElementPresentation) liste.elementAt(0);
      GElement noeud = getGenerateurCorrepondant(elem, fd);
      nouvelArbre = new ArbreGeneration(noeud);
      generation.ajouterSousArbre(nouvelArbre);
    }

    for (i = 0; i < liste.size(); i++) {
      ElementPresentation elem = (ElementPresentation) liste.elementAt(i);
      GElement noeud = getGenerateurCorrepondant(elem, fd);
      if (elem.getNiveau() == 2) {
        // rajoute directement au nouvel arbre
        ArbreGeneration n = new ArbreGeneration(noeud);
        nouvelArbre.ajouterSousArbre(n);
      }
      else if (elem.getNiveau() >= 2) {
        String nouvelID = elem.getID_interne().substring(elem.getID_interne().indexOf("-") + 1);
        nouvelArbre.ajouterSousArbre(noeud, nouvelID);
      }
    }
  }

  /**
   * Construit les gestionnaires de publication associés au type des éléments à traiter
   * @param elem element de présentation qu'il faut traiter
   */
  public static GElement getGenerateurCorrepondant(ElementPresentation elem, PrintWriter pwFicTree) {
    // selon le type de l'élément de présentation
    if (elem instanceof Guide) {
      GGuide guide = new GGuide(elem, pwFicTree);
      return guide;
    }

    // on vérifie que l'on ait bien un modèle associé
    if (elem.getElementModele() != null) {
      // c'est un élément normal il faut récupérer le type du modèle associé
      IdObjetModele id = elem.getElementModele();

      if (id.estComposant()) {
        // composant
        GComposantPubliable compo = new GComposantPubliable(id, elem, pwFicTree);
        return compo;
      }

      if (id.estActivite()) {
        // activite
        GActivite activite = new GActivite(elem, pwFicTree);
        return activite;
      }

      if (id.estProduit()) {
        // produit
        GProduit produit = new GProduit(elem, pwFicTree);
        return produit;
      }

      if (id.estDefinitionTravail()) {
        // deftravail
    	GDefinitionTravail defTrav = new GDefinitionTravail(elem, pwFicTree);
        return defTrav;
      }

      if (id.estDiagramme()) {
        // diagramme
        GDiagramme diag = new GDiagramme( (ComposantProcessus) id.getRef(), elem, pwFicTree);
        return diag;
      }

      if (id.estRole()) {
        // role
        GRole role = new GRole(elem, pwFicTree);
        return role;
      }

      if (id.estPaquetage()) {
        GPaquetage gelem = new GPaquetage(elem, pwFicTree);
        return gelem;
      }

      else {
        GElementModele gelem = new GElementModele(elem, pwFicTree);
        return gelem;
      }
    }
    return null;
  }

  /**
   * Indique si l'ID passee en parametre est celle d'un produit exterieur
   * @param id IdObjectModele a controler
   * @return 0 si le produit n'est pas exterieur, 1 s'il s'agit d'un produit en entree, 2 s'il s'agit d'une produit en sortie
   */
  public static int estProduitExterieur(IdObjetModele id) {
    for (int i = 0; i < listeProduitsExterieurs.size(); i++) {
      if (listeProduitsExterieurs.elementAt(i).toString().equals(id.toString()) && ( ( (IdObjetModele) listeProduitsExterieurs.elementAt(i)).getRef() == id.getRef())) {
        // Le produit est exterieur
        if ( ( (IdObjetModele) listeProduitsExterieurs.elementAt(i)).estProduitSortie()) {
          return 2;
        }
        return 1;
      }
    }
    return 0;
  }

  /**
   * Indique si l'ID passee en parametre est celle d'un produit lie a un autre qui doit etre affiche
   * @param id IdObjectModele a controler
   * @return l'ID du produit qu'il faut afficher si l'objet est trouve, null sinon
   */
  public static IdObjetModele getProduitChange(IdObjetModele id) {
    if (id != null) {
      if (listeProduitsChanges.containsKey(id.getRef().toString() + "::" + id.toString())) {
        return ( (IdObjetModele) listeProduitsChanges.get(id.getRef().toString() + "::" + id.toString()));
      }
    }
    return null;
  }

  /**
   * Indique si la chaine representant un produit passee en parametre est celle d'un produit lie a un autre qui doit etre affiche
   * @param id IdObjectModele a controler
   * @return l'ID du produit qu'il faut afficher si l'objet est trouve, null sinon
   */
  public static IdObjetModele getProduitChange(String id) {
    if (listeProduitsChanges.containsKey(id)) {
      return (IdObjetModele) listeProduitsChanges.get(id);
    }
    return null;
  }

  /**
   * Retourne un vecteur contenant la liste des produits que le produit passe en parametre a remplace
   * ou null s'il n'y en a pas
   * @param id IdObjectModele du produit qui remplace
   * @return vecteur contenant les IdObjetModele des produits qui ont ete remplace
   */
  public static Vector getProduitsRemplaces(IdObjetModele id) {
    return (Vector) (listeProduitsRemplaces.get(id.getRef().toString() + "::" + id.toString()));
  }

  /**
   * Retourne le vecteur des activites qui ont le produit passe en parametre en entree(ses activites propres et les activites des produits qu'il remplace)
   * @param id IdObjectModele du produit
   * @return vecteur contenant les IdObjetModele des activites ayant le produit en entree
   */
  public static Vector getActivitesEntree(IdObjetModele id) {
    Vector activIn = (Vector) id.getActiviteEntree().clone();
    Vector prodRemplaces = getProduitsRemplaces(id);
    if (prodRemplaces != null) {
      for (int i = 0; i < prodRemplaces.size(); i++) {
        activIn.addAll( ( (IdObjetModele) prodRemplaces.elementAt(i)).getActiviteEntree());
      }
    }
    return activIn;
  }

  /**
   * Retourne le vecteur des activites qui ont le produit passe en parametre en sortie (ses activites propres et les activites des produits qu'il remplace)
   * @param id IdObjectModele du produit
   * @return vecteur contenant les IdObjetModele des activites ayant le produit en sortie
   */
  public static Vector getActivitesSortie(IdObjetModele id) {
    Vector activOut = (Vector) id.getActiviteSortie().clone();
    Vector prodRemplaces = getProduitsRemplaces(id);
    if (prodRemplaces != null) {
      for (int i = 0; i < prodRemplaces.size(); i++) {
        activOut.addAll( ( (IdObjetModele) prodRemplaces.elementAt(i)).getActiviteSortie());
      }
    }
    return activOut;
  }
}
