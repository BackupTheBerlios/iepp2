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
package iepp.application.areferentiel;

import iepp.Application;
import iepp.Projet;
import iepp.application.CEnregistrerInterface;
import iepp.application.CSauvegarderDP;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.PaquetagePresentation;
import iepp.infrastructure.jsx.ChargeurComposant;
import iepp.infrastructure.jsx.ChargeurDP;
import iepp.infrastructure.jsx.ChargeurPaquetagePresentation;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.w3c.dom.Document;

import util.Copie;
import util.ErrorManager;
import util.FileFinder;
import util.TaskMonitorDialog;
import util.ToolKit;
import util.xpath;

/**
 * Classe permettant la gestion du r?f?rentiel
 *
 * 2XMI utilise le nom du referentiel + nom de l'element pour l'export
 */
public class Referentiel
    extends Observable implements TreeModel {
  /**
   * Nom du referentiel
   */
  private String nomReferentiel;

  /**
   * Chemin o? se situe le fichier r?f?rentiel
   */
  private String cheminReferentiel;

  /**
   * Dernier id attribu? ? un composant
   */
  private long lastId = -1;
  
  /**
   * Chemin du répertoire d'exportation
   */
  private String cheminExport;

  // HashMaps contenant les r?f?rences et les id des composants ajout?s au diagramme

  /**
   * HashMap faisant le lien entre la r?f?rence d'un ?l?ment (Composant ou DP) et son id
   */
  private HashMap elementToId = new HashMap();

  /**
   * HashMap faisant le lien entre l'id d'un ?l?ment dans le r?f?rentiel et sa r?f?rence
   */
  private HashMap idToElement = new HashMap();

  /**
   * Arbre contenant les d?finitions processus et les composants
   */
  private DefaultTreeModel arbre;

  /**
   * Racine de l'arbre
   */
  private ElementReferentiel racine = null;

  /**
   * Noeud contenant tous les composants
   */
  private ElementReferentiel composants = null;

  /**
   * Noeud contenant tous les composants
   */
  private ElementReferentiel scenarios = null;
  /**
   * Noeud contenant toutes les DP
   */
  private ElementReferentiel dp = null;

  /**
   * Noeud contenant tous les ?l?ments de pr?sentation
   */
  private ElementReferentiel present = null;

  /**
   * Chemin du r?pository ? utiliser
   */
  private String cheminRepository = null;

  // Constantes repr?sentant les types possibles de mise ? jour sur l'arbre
  // Utile pour les observateurs
  public static final int CHANGED = 0;
  public static final int ELEMENT_INSERTED = 1;
  public static final int ELEMENT_REMOVED = 2;
  private ElementReferentiel dernierComposantAjoute = null;

  private static xpath xp;
  private static Document document;
  
  /**
   * Cr?e le r?f?rentiel, ? partir du fichier r?f?rentiel
   * @param nom : nom du r?f?rentiel
   * @throws FileNotFoundException
   * @throws FileNotFoundException
   */
  public Referentiel(String nom) throws Exception {
	File fic = new File(Application.getApplication().getConfigPropriete("chemin_referentiel") + File.separator + nom);
    //this.cheminRepository = ToolKit.removeSlashTerminatedPath(ToolKit.getAbsoluteDirectory(Application.getApplication().getConfigPropriete("chemin_referentiel")));
    this.cheminRepository = ToolKit.getAbsoluteDirectory(Application.getApplication().getConfigPropriete("chemin_referentiel"));

    if (fic.exists()) {}
    else {
      this.creerReferentiel(nom);
    }
  } // Fin constructeur Referentiel (String chemin)

  /**
   * @param cheminRef
   * @throws FileNotFoundException
   */
  public Referentiel(File cheminRef) throws Exception {
	this.cheminRepository = cheminRef.getParentFile().getParentFile().toString();
	this.cheminRepository = ToolKit.getAbsoluteDirectory(this.cheminRepository);
    this.chargerReferentiel(cheminRef.getName().substring(0, cheminRef.getName().lastIndexOf(".xml")), cheminRef.getParentFile().getName());
  }

  /**
   * Initialise le r?f?rentiel et cr?e un nouveau r?pertoire contenant le r?f?rentiel cit?
   * @param nom : nom du r?f?rentiel ? cr?er
   * @throws FileNotFoundException
   */
  public void creerReferentiel(String nom) throws Exception {
	// Affectation du nom ? l'objet
    nomReferentiel = nom;

    // Id initialis? ? 0
    lastId = 0;

    cheminReferentiel = this.cheminRepository + File.separator + nomReferentiel;

    // Cr?ation du r?pertoire du r?f?rentiel
    File rep = new File(cheminReferentiel);
    rep.mkdirs();

    // Apr?s avoir cr?? le r?f?rentiel, on le charge
    this.chargerReferentiel(nom, nom);

  } // Fin m?thode creerReferentiel
  
  public String chercherNomReferentiel(String nomInter) {
	  String nomref = "";
	  File f = new File(this.cheminRepository + File.separator + nomInter + File.separator + "dependencies.xml");
	  if (f.exists()) {
		  /*
		  try {
			  xp = new xpath(new DataInputStream(new FileInputStream(f)));
			  document = xp.getDocument();
		  } catch (Exception e) {
			// TODO: handle exception
		  }
		  nomref = xp.valeur(document, "//files/@project");
		  */
		  if (nomref == "") {
			  nomref = nomInter;
		  }
	  }
	  else {
		  nomref = nomInter;
	  }
	  return nomref;
  }
  
  /**
   * Charge le r?f?rentiel ? partir du fichier r?f?rentiel
   * @param nom : nom du r?f?rentiel
   * @throws Exception
   */
  public void chargerReferentiel(String nom, String nomInter) throws Exception {
    //RandomAccessFile raf;
    String ligne = null;
    String nomElt = null;
    ChargeurComposant chargeur;
    //boolean ligneObsolete = false;

    // Initialisation du nom du r?f?rentiel
    nomReferentiel = chercherNomReferentiel(nomInter); //nom;

    // Initialisation du chemin du r?f?rentiel
    cheminReferentiel = this.cheminRepository + File.separator + nomInter;
    
    // Initialisation du chemin du répertoire d'exportation
    cheminExport = this.cheminReferentiel + File.separator + "export";
    
    /*
    // Cr?ation de la racine de l'arbre (racine) et des 2 noeuds principaux (composants et dp)
   racine = new ElementReferentiel(nomReferentiel, 0, cheminReferentiel, ElementReferentiel.REFERENTIEL);
    composants = new ElementReferentiel("Composants", 0, cheminReferentiel, ElementReferentiel.PAQ_COMP);
    dp = new ElementReferentiel("D?finitions Processus", 0, cheminReferentiel, ElementReferentiel.PAQ_DP);
    present = new ElementReferentiel("Pr?sentation", 0, cheminReferentiel, ElementReferentiel.PAQ_PRESENTATION);
    scenarios = new ElementReferentiel("Sc?narios", 0, cheminReferentiel, ElementReferentiel.PAQ_SCEN);
    */    
    
    // Cr?ation de la racine de l'arbre (racine) et des 2 noeuds principaux (composants et dp)
    racine = new ElementReferentiel(nomReferentiel, 0, cheminReferentiel, ElementReferentiel.REFERENTIEL);
    composants = new ElementReferentiel(Application.getApplication().getTraduction("components"), 0, cheminReferentiel, ElementReferentiel.PAQ_COMP);
    dp = new ElementReferentiel(Application.getApplication().getTraduction("processus"), 0, cheminReferentiel, ElementReferentiel.PAQ_DP);
    present = new ElementReferentiel(Application.getApplication().getTraduction("presentation"), 0, cheminReferentiel, ElementReferentiel.PAQ_PRESENTATION);
    scenarios = new ElementReferentiel(Application.getApplication().getTraduction("scenarios"), 0, cheminReferentiel, ElementReferentiel.PAQ_SCEN);

    // Cr?ation de l'arbre avec pour racine le nom du r?f?rentiel
    arbre = new DefaultTreeModel(racine);


    // Ajout des 2 noeuds principaux: Composants et D?finitions Processus
    //racine.add(dp);
    racine.add(composants);
    //racine.add(present);
    racine.add(scenarios);
    
    // On declare l'objet de recherche de fichiers
    FileFinder ff;
    
    // On listera tous les composant afinde  savoir les paquetages de presentation a afficher
    ArrayList ListApes = new ArrayList();
    
    // On recupere tous les composants du projets
    ff = new FileFinder(cheminReferentiel + File.separator, ".apes");
    int nb_apes = ff.getNbFichiers();
    for(int i=0 ; i < nb_apes ; i++) {
        /*
    	// Modif Albert 2XMI
        int longueur = ligne.length();
        //version en fin de ligne
        String version = ligne.substring(longueur - (ElementReferentiel.LONGUEUR_VERSION + ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1), longueur - (ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1));
        //+1 a cause de chaque espace separateur

        String datePlacement = ligne.substring(longueur - ElementReferentiel.LONGUEUR_DATEPLACEMENT);
        */
        
    	ligne = ff.getFichier(i);

    	String version = "1.0";
    	
    	Date d = new Date(new File(ligne).lastModified());
    	
    	String datePlacement = "";
    	int annee = d.getYear()+1900;
    	datePlacement =  datePlacement+annee;
    	int mois = d.getMonth();
    	if(mois<10) { datePlacement = datePlacement+'0'+mois; }
    	else { datePlacement = datePlacement+mois; }
    	int jour = d.getDay();
    	if(jour<10) { datePlacement = datePlacement+'0'+jour; }
    	else { datePlacement = datePlacement+jour; }
    	int heure = d.getHours();
    	if(heure<10) { datePlacement = datePlacement+'0'+heure; }
    	else { datePlacement = datePlacement+heure; }
    	int minutes = d.getMinutes();
    	if(minutes<10) { datePlacement = datePlacement+'0'+minutes; }
    	else { datePlacement = datePlacement+minutes; }
    	int seconde = d.getSeconds();
    	if(seconde<10) { datePlacement = datePlacement+'0'+seconde; } 
    	else { datePlacement = datePlacement+seconde; }
    	
    	// Cr?ation du chargeur permettant de r?cup?rer le nom de l'?l?ment
        chargeur = new ChargeurComposant(ligne);

        // Chargement du nom de l'?l?ment ? partir du fichier .apes pour les composants non vides
        nomElt = chargeur.chercherNomComposant(ligne);
        
        ListApes.add(this.extraireNomFichier(ligne));
        
        // si c'est un composant vide
        if (nomElt == null) {
          // Le nom du fichier du composant vide est celui du composant
          nomElt = this.extraireNomFichier(ligne);
          this.getNoeudComposants().add(new ElementReferentiel(nomElt, i+1, ligne, ElementReferentiel.COMPOSANT_VIDE, version, datePlacement));
        }
        else {
        	this.getNoeudComposants().add(new ElementReferentiel(nomElt, i+1, ligne, ElementReferentiel.COMPOSANT, version, datePlacement));
        }
    }
    ff.setExtension(".iepp");
    int nb_iepp = ff.getNbFichiers();
    for(int i=0 ; i < nb_iepp ; i++) {
    	ligne = ff.getFichier(i);

    	this.getNoeudDp().add(new ElementReferentiel(this.extraireNomFichier(ligne), i+nb_apes+1, ligne, ElementReferentiel.DP));
     }    
    ff.setExtension(".pre");
    int nb_pre = ff.getNbFichiers();
    int nb_pre_reel = 0;
    for(int i=0 ; i < nb_pre ; i++) {
    	ligne = ff.getFichier(i);
    	if(!ListApes.contains(this.extraireNomFichier(ligne))) {
    		this.getNoeudPresentation().add(new ElementReferentiel(this.extraireNomFichier(ligne), i+nb_apes+nb_iepp+1, ligne, ElementReferentiel.PRESENTATION));
    		nb_pre_reel++;
    	}
     }
    lastId = new Long( (nb_apes+nb_iepp+nb_pre_reel+1)+"").longValue();
    
    /*
    // Lecture du fichier r?f?rentiel
    raf = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + ".xml", "rw");

    ligne = raf.readLine();

    // R?cup?ration du dernier ID attribu?
    lastId = 0; // Long.parseLong(ligne.substring(ligne.lastIndexOf(">") + 2).trim());

    ligne = raf.readLine();

    while (ligne != null) {
      if (ligne.startsWith("CP:")) { //c'est un composant
        // Modif 2XMI jean : detection des anciens referentiels
        if (!verificationConformite(ligne)) {
          //la ligne ne correspond pas a la derniere version du referentiel
          ligneObsolete = true;
        }
        else {
          //la ligne est bien conforme

          // Modif Albert 2XMI
          int longueur = ligne.length();
          //version en fin de ligne
          String version = ligne.substring(longueur - (ElementReferentiel.LONGUEUR_VERSION + ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1), longueur - (ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1));
          //+1 a cause de chaque espace separateur

          String datePlacement = ligne.substring(longueur - ElementReferentiel.LONGUEUR_DATEPLACEMENT);

          // dans le fichier ce sont des liens relatifs au r?f?rentiel pour pouvoir le d?placer
          ligne = ToolKit.getConcatePath(this.cheminReferentiel, ligne.substring(3, longueur - (ElementReferentiel.LONGUEUR_VERSION + 1 + ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1)));
          //+1 a cause de chaque espace separateur

          // Cr?ation du chargeur permettant de r?cup?rer le nom de l'?l?ment
          chargeur = new ChargeurComposant(ligne);

          //System.out.println("LIGNE : " + ligne);
          // Chargement du nom de l'?l?ment ? partir du fichier .pre pour les composants non vides
          nomElt = chargeur.chercherNomComposant(ligne);

          // si c'est un composant vide
          if (nomElt == null) {
            // Le nom du fichier du composant vide est celui du composant
            nomElt = this.extraireNomFichier(ligne);
          }
          this.getNoeudComposants().add(new ElementReferentiel(nomElt, this.extraireIdChemin(ligne), ligne, ElementReferentiel.COMPOSANT, version, datePlacement));
        }
      }
      else if (ligne.startsWith("PP:")) { //c'est un paquetage de presentation
        ligne = ToolKit.getConcatePath(this.cheminReferentiel, ligne.substring(3));
        if ( (this.extraireNomFichier(ligne)).equals("")) {
          throw new Exception();
        }
        this.getNoeudPresentation().add(new ElementReferentiel(this.extraireNomFichier(ligne), this.extraireIdChemin(ligne), ligne, ElementReferentiel.PRESENTATION));
      }
      else if (ligne.startsWith("DP:")) { //c'est une definition de processus
        ligne = ToolKit.getConcatePath(this.cheminReferentiel, ligne.substring(3));
        if ( (this.extraireNomFichier(ligne)).equals("")) {
          throw new Exception();
        }
        this.getNoeudDp().add(new ElementReferentiel(this.extraireNomFichier(ligne), this.extraireIdChemin(ligne), ligne, ElementReferentiel.DP));
      }
      ligne = raf.readLine();
    }
    // Fermeture du flux
    raf.close();
    
    if (ligneObsolete) {
      //informe que le referentiel est un ancien referentiel
      ErrorManager.getInstance().display("ERR", "ERR_Fic_Ref_Maj2_0");
      throw new Exception("referentiel_ancien_format");
    }
    */
  }

  /*
   * Verifie si la ligne passee en parametre est conforme a la derniere version
   * du referentiel
   * @return true si la ligne est conforme,
   * false si la ligne correspond ? un ancien referentiel
   */
  public boolean verificationConformite(String _ligne) {
    //Referentiel v1.0 : version IEPP 1.1.7
    //Referentiel v2.0 : ajout de version et datePlacement aux lignes CP
    int longueur = _ligne.length();
    if (_ligne.startsWith("CP:")) { //c'est un composant

      String espaceVersion = _ligne.substring(longueur - (ElementReferentiel.LONGUEUR_VERSION + 1 + ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1), longueur - (ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1));
      //+1 a cause de chaque espace separateur
      //espaceVersion doit commencer par un espace
      if (!espaceVersion.startsWith(" ")) {
        return false;
      }

      String espaceDate = _ligne.substring(longueur - (ElementReferentiel.LONGUEUR_DATEPLACEMENT + 1));
      //+1 a cause de chaque espace separateur
      //espaceDate doit commencer par un espace
      if (!espaceDate.startsWith(" ")) {
        return false;
      }
      //espaceDate doit etre une date valide
      if (ElementReferentiel.FORMATEUR.parse(espaceDate, new ParsePosition(1)) == null) {
        return false;
      }
      //new ParsePosition(1): on ne commence pas a 0 car on ignore l'espace de debut
      //parse retourne null si la date n'est pas valide
    }
    return true;
  }

  /**
   * Ajoute un ?l?ment au r?f?rentiel (physique et logique).
   * Pour un composant ou une pr?sentation, mettre le chemin absolu du fichier de l'?l?ment ? ajouter
   * Pour une d?finition processus, mettre UNIQUEMENT le nom de la DP ? cr?er
   * Pour un composant vide, mettre UNIQUEMENT le nom du composant vide ? cr?er
   * @param cheminComp : chemin de l'?l?ment sur le disque
   * @param type : type de l'?l?ment (ElementReferentiel.COMPOSANT, ElementReferentiel.COMPOSANT_VIDE, ElementReferentiel.DP ou ElementReferentiel.PRESENTATION)
   * @return l'id de l'?l?ment ajout?, -1 en cas d'erreur, -2 si le fichier choisi n'est pas de bon type, -3 si le composant est deja insere
   */
  public long ajouterElement(String chemin, int type, String version, String dateDePlacement) {
    String ligne = null;
    String nomRep = null;
    String nomElt = null;
    String pathFic = null;
    File fic = null;

    // Nom du path diff?rent selon le type de l'?l?ment
    switch (type) {
      case (ElementReferentiel.COMPOSANT):
      case (ElementReferentiel.COMPOSANT_VIDE):
        nomRep = "Composants";
        break;

      case (ElementReferentiel.SCENARIO):
          nomRep = "Sc?narios";
          break;
          
      case (ElementReferentiel.DP):
        nomRep = "DP";
        break;

      case (ElementReferentiel.PRESENTATION):
        nomRep = "Presentation";
        break;
    }

    // Cr?ation du path o? sera copi? le fichier
    pathFic = cheminReferentiel;

    try {
      // Cr?ation du r?pertoire o? sera copi? le fichier de l'?l?ment
      fic = new File(pathFic);
      fic.mkdirs();

      // Ajoute l'?l?ment dans l'arbre
      switch (type) {
      	/*case (ElementReferentiel.SCENARIO):
      		this.getNoeudScenarios().add(new ElementReferentiel(nomElt, this.getLastId(), pathFic, ElementReferentiel.SCENARIO, version, dateDePlacement));
      		break;
        */    
        
      	case (ElementReferentiel.COMPOSANT_VIDE):
        case (ElementReferentiel.COMPOSANT):
          if (type == ElementReferentiel.COMPOSANT) {

            // Cr?ation du chargeur permettant de r?cup?rer le nom de l'?l?ment
            ChargeurComposant chargeur = new ChargeurComposant(chemin);

            nomElt = chargeur.chercherNomComposant(chemin);

            // Permet de savoir si le fichier est un composant ou pas
            // Renvoie -2 si c'est pas le cas
            if (nomElt == null) {
              // Supprimer le repertoire cree
              fic.delete();
              return -2;
            }
            /*
                                                             else
                                                             {
                    nomElt = this.extraireNomFichier(chemin);
                                                             }
             */

            // Verifie que ce nom n'existe pas deja
            if (nomComposantToId(nomElt) != -1) {
              // Supprimer le repertoire cree
              fic.delete();
              return -3;
            }

            //Copie du fichier dans le r?f?rentiel (cr?ation de son r?pertoire + attribution de l'ID)
            pathFic += File.separator + nomElt + ".pre";
            Copie.copieFicCh(chemin, pathFic);

          }
          // cas des composants vides
          else {
            // Cr?ation du r?pertoire o? sera copi? le fichier de l'?l?ment
            pathFic += File.separator + chemin + ".apes";
            // sauvegarderInterface(pathFic);
            fic = new File(pathFic);
            fic.createNewFile();

            // Le nom du fichier du composant vide est celui du fichier
            nomElt = this.extraireNomFichier(pathFic);
            
            this.getIdPourNouvelElement();
          }
          this.getNoeudComposants().add(new ElementReferentiel(nomElt, this.getLastId(), pathFic, ElementReferentiel.COMPOSANT, version, dateDePlacement));
          break;

        case (ElementReferentiel.DP):
        	
          // Cr?ation du fichier DP
          pathFic += File.separator + chemin + ".iepp";
          CSauvegarderDP c = new CSauvegarderDP(pathFic);
          c.executer();

          // On r?cup?re le nom du fichier de l'?l?ment
          nomElt = this.extraireNomFichier(pathFic);
          
          // Verifie que ce nom n'existe pas deja
          if (nomDefProcToId(nomElt) != -1) {
            // Supprimer le repertoire et les fichiers crees
            if (fic.exists()) {
              File[] files = fic.listFiles();
              files[0].delete();
            }
            fic.delete();

            return -3;
          }
          
          this.getNoeudDp().add(new ElementReferentiel(nomElt, this.getIdPourNouvelElement(), pathFic, ElementReferentiel.DP));
          
          break;

        case (ElementReferentiel.PRESENTATION):

          // Cr?ation du chargeur permettant de r?cup?rer le nom de la pr?sentation
          ChargeurPaquetagePresentation chargeur = new ChargeurPaquetagePresentation(chemin);

          nomElt = chargeur.chercherNomPresentation(chemin);

          // Permet de savoir si le fichier est un composant ou pas
          // Renvoie -2 si c'est pas le cas
          if (nomElt == null) {
            // Supprimer le repertoire cree
            fic.delete();
            return -2;
          }

          /*
                                                   else
                                                   {
                  nomElt = this.extraireNomFichier(chemin);
                                                   }
           */
          // Verifie que ce nom n'existe pas deja
          if (nomPresentationToId(nomElt) != -1) {
            // Supprimer le repertoire cree
            fic.delete();
            return -3;
          }

          //Copie du fichier dans le r?f?rentiel (cr?ation de son r?pertoire + attribution de l'ID)
          pathFic += File.separator + nomElt + ".pre";
          Copie.copieFicCh(chemin, pathFic);

          this.getNoeudPresentation().add(new ElementReferentiel(nomElt, this.getLastId(), pathFic, ElementReferentiel.PRESENTATION));
          break;
      }
/*
      // Insertion du path de l'?l?ment dans le fichier referentiel
      RandomAccessFile raf = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + ".ref", "rw");

      // On va en fin de fichier
      raf.seek(raf.length());

      //	Ajoute l'?l?ment dans l'arbre

      switch (type) {
        case (ElementReferentiel.COMPOSANT_VIDE):
        case (ElementReferentiel.COMPOSANT): {
          raf.writeBytes("CP:");
          ElementReferentiel er = this.chercherElement(this.getLastId(), ElementReferentiel.COMPOSANT);
          this.dernierComposantAjoute = this.chercherElement(this.getLastId(), ElementReferentiel.COMPOSANT);
          raf.writeBytes("./" + ToolKit.removeSlashTerminatedPath(ToolKit.getRelativePathOfAbsolutePath(pathFic, this.cheminReferentiel)) + " " + er.getVersionRef() + " " + er.getDatePlacement() + "\n");
        }
        break;
        case (ElementReferentiel.DP): {
          raf.writeBytes("DP:");
          raf.writeBytes("./" + ToolKit.removeSlashTerminatedPath(ToolKit.getRelativePathOfAbsolutePath(pathFic, this.cheminReferentiel)) + "\n");
        }
        break;
        case (ElementReferentiel.PRESENTATION): {
          raf.writeBytes("PP:");
          raf.writeBytes("./" + ToolKit.removeSlashTerminatedPath(ToolKit.getRelativePathOfAbsolutePath(pathFic, this.cheminReferentiel)) + "\n");
        }
        break;
      }

      // Fermeture du flux
      raf.close();
*/
      // MAJ du fichier r?f?rentiel
      //this.majDerniereIdDansFichier(this.getLastId());

      // Notifie aux observateurs qu'un ?l?ment a ?t? ajout?
      this.majObserveurs(ELEMENT_INSERTED);

      // On retourne l'id de l'?l?ment ajout?
      return this.getLastId();
    }
    catch (Exception e) {
      e.printStackTrace();
      ErrorManager.getInstance().displayError(e.getMessage());
      // Supprimer le repertoire cree
      fic.delete();
      return -1;
    }
  }

  public long ajouterElement(String chemin, int type) {
    Date dateCourante = new Date();
    String _date = ElementReferentiel.FORMATEUR.format(dateCourante);
    return (ajouterElement(chemin, type, ElementReferentiel.DEFAUT_VERSION, _date));
  }

  /**
   * Supprime l'?l?ment d?sign? du r?f?rentiel (physique + logique).
   * @param idElt : l'identifiant de l'?l?ment ? supprimer
   * @param type : type de l'?l?ment (ElementReferentiel.COMPOSANT, ElementReferentiel.DP ou ElementReferentiel.PRESENTATION)
   * @return bool?en indiquant si la suppression s'est bien pass?e
   */
  public boolean supprimerElement(long idElt, int type) {
    ElementReferentiel feuille = null;
    ElementReferentiel noeud = null;
    RandomAccessFile source = null;
    RandomAccessFile dest = null;
    String ligne = null;
    String nomRep = null;
    File fic = null;
    boolean suppressionOk = true;

    switch (type) {
      case (ElementReferentiel.COMPOSANT):
        feuille = this.chercherElement(idElt, ElementReferentiel.COMPOSANT);
        noeud = this.getNoeudComposants();
        nomRep = "Composants";
        break;

      case (ElementReferentiel.DP):
        feuille = this.chercherElement(idElt, ElementReferentiel.DP);
        noeud = this.getNoeudDp();
        nomRep = "DP";
        break;

      case (ElementReferentiel.PRESENTATION):
        feuille = this.chercherElement(idElt, ElementReferentiel.PRESENTATION);
        noeud = this.getNoeudPresentation();
        nomRep = "Presentation";
        break;
    }

    if (feuille != null) {
      // Suppression du fichier
      fic = new File(feuille.getChemin());
      suppressionOk = fic.delete();

      // Si la suppression du fichier ne se fait pas, on retourne false
      if (suppressionOk == false) {
        return false;
      }
/*
      // Suppression du r?pertoire o? se trouvait le fichier de l'?l?ment
      fic = new File(cheminReferentiel + File.separator + nomRep + File.separator + idElt);
      fic.delete();

      // Suppression du path du fichier de l'?l?ment dans le fichier referentiel
      try {
        // Copie du fichier sans la ligne d?sir?e dans un autre fichier
        source = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + ".ref", "rw");
        dest = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + "2.ref", "rw");

        // ne pas prendre en compte la premiere ligne
        ligne = source.readLine();

        String aux = "";

        // On recopie le fichier en omettant la ligne contenant le path de l'?l?ment ? effacer
        while (ligne != null) {
          if (!aux.startsWith("./" + ToolKit.removeSlashTerminatedPath(ToolKit.getRelativePathOfAbsolutePath(
              feuille.getChemin(), this.cheminReferentiel)))) {
            dest.writeBytes(ligne + "\n");
          }

          // On passe ? la ligne suivante
          ligne = source.readLine();

          if (ligne != null) {
            //	 enlever les DP: ou CP: ou PP:
            aux = ligne.substring(3);
          }
        }

        source.close();
        dest.close();

        // Suppression de l'ancien fichier
        fic = new File(cheminReferentiel + File.separator + nomReferentiel + ".ref");
        fic.delete();

        // On renomme le fichier modifi? avec le nom du fichier qu'on vient de supprimer
        fic = new File(cheminReferentiel + File.separator + nomReferentiel + "2.ref");
        fic.renameTo(new File(cheminReferentiel + File.separator + nomReferentiel + ".ref"));

        // Suppression de l'?l?ment dans l'arbre
        noeud.remove(feuille);

        // Notifie aux observateurs qu'un composant a ?t? supprim?
        this.majObserveurs(ELEMENT_REMOVED);

        return true;
      }
      catch (IOException e) {
        e.printStackTrace();
        ErrorManager.getInstance().displayError(e.getMessage());
        return false;
      }
      */
      
      // Notifie aux observateurs qu'un composant a ?t? supprim?
      this.majObserveurs(ELEMENT_REMOVED);

      return true;
    }
    else {
      System.out.println("Element inexistant dans le référentiel.");
      return false;
    }
  }

  /**
   * Charge un composant publiable du r?f?rentiel en m?moire
   * @param idComp identifiant du composant du r?f?rentiel ? charger
   * @return le composant charg? en m?moire
   */
  public ComposantProcessus chargerComposant(long idComp) {
    TaskMonitorDialog dialogAvancee;
    ElementReferentiel eltRef = this.chercherElement(idComp, ElementReferentiel.COMPOSANT);

    // Construction du chargeur de composant qui est aussi une tache
    ChargeurComposant chargeur = new ChargeurComposant(eltRef.getChemin());

    // Affiche la bo?te d'avancement
    dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), chargeur);
    dialogAvancee.setTitle(Application.getApplication().getTraduction("Chargement"));
    chargeur.setTask(dialogAvancee);
    dialogAvancee.show();

    // Charger le composant processus et le mettre dans un composant
    ComposantProcessus comp = chargeur.getComposantCharge();
    HashMap presentation = chargeur.getMapPresentation();

    // Reinitialiser le chemin de fichier du composant
    //comp.setNomFichier(this.chercherElement(this.nomComposantToId(comp.toString()),ElementReferentiel.COMPOSANT).getChemin());

    // Remplis les HashMap avec la r?f?rence du composant en cl? et l'id du composant en valeur
    this.ajouterReferenceMemoire(comp, idComp);

    // on a bien r?cup?r? un composant
    if (comp != null) {
      // on v?rifie si c'est un composant vide, auquel cas pas besoin de l'initialiser
      if (!comp.estVide()) {
        comp.initialiser(presentation);
      }
      return comp;
    }
    else {
      // Si une erreur s'est produite
      return null;
    }
  }

  /**
   * Charge un projet contenant une d?finition processus du r?f?rentiel en m?moire
   * @param idDP identifiant de la d?finition de processus du r?f?rentiel ? charger
   * @param listeComposant liste des composants qui sont dans la dp mais ne sont plus dans le r?f?rentiel
   * @return le projet charg? en m?moire ou null
   */
  public Projet chargerDefProc(long idDp, Vector listeComposant) {
    TaskMonitorDialog dialogAvancee;
    ElementReferentiel eltRef = this.chercherElement(idDp, ElementReferentiel.DP);
    Projet projet = null;
    DefinitionProcessus def = null;
    Vector tab = null;
    IdObjetModele id = null;
    ComposantProcessus comp;
    long idCompo, idPresent = 0;
    PaquetagePresentation paquet = null;

    // Initialisation du chargeur
    ChargeurDP chargeur = new ChargeurDP(new File(eltRef.getChemin()));

    // Affiche la bo?te d'avancement
    dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), chargeur);
    dialogAvancee.setTitle(Application.getApplication().getTraduction("Chargement"));
    chargeur.setTask(dialogAvancee);
    dialogAvancee.show();

    // R?initialise les associations id-r?f?rences
    //this.supprimerTousLesElementsCharges();

    // Charge la DP et renvoie si le projet construit ? partir de la DP si le chargement s'est bien pass?
    projet = chargeur.getProjetCharge();

    // On r?cup?re la d?finition processus du projet
    def = projet.getDefProc();
    //modif 2XMI
    //si le processus charg? ne contient pas de pied de page on le cr?e
    if(def.getPiedPage()==null)
    {
        def.creerPiedPageVide();
    }

    // On r?cup?re la liste des composants et paquetage utilis?s dans cette d?finition processus
    tab = def.getListeAGenerer();
	// on met le nom du processus au niveau de la racine de l'arbre
	Application.getApplication().getReferentiel().getRacine().setNomElement(def.getNomDefProc());
    // on renomme le noeud du scenario dans le referentiel
    String nomNoeud = Application.getApplication().getTraduction("scenarios");
	Application.getApplication().getReferentiel().getNoeudScenarios().setNomElement(nomNoeud);
	
	
	this.majObserveurs(Referentiel.ELEMENT_INSERTED);
	//Application.getApplication().getProjet().getFenetreEdition().setDynamique();
	//Application.getApplication().getProjet().setModified(true);
	
	
    // Ajout de la r?f?rence et de l'id de la d?finition processus dans les HashMap
    this.ajouterReferenceMemoire(projet.getDefProc(), idDp);
    
    // On remplis les Hashmap avec les r?f?rences et les id des composants charg?s
    for (int i = 0; i < tab.size(); i++) {
      // on s'occupe d'un composant
      if (tab.get(i) instanceof IdObjetModele) {
        // On r?cup?re l'IdObjetModele du composant
        id = (IdObjetModele) tab.get(i);

        // Gr?ce ? laquelle on retrouve l'objet Composant
        comp = (ComposantProcessus) id.getRef();
        
        // si c'est un composant vide on ne v?rifie pas
        if (comp.getNomFichier() != null) {
          // Reinitialisation du nom de fichier du composant
        
          ElementReferentiel eRef = this.chercherElement(this.nomComposantToId(comp.toString()), ElementReferentiel.COMPOSANT);
          
          if (eRef != null) {
            comp.setNomFichier(eRef.getChemin());
            comp.getPaquetage().setNomFichier(eRef.getChemin());
            // On cherche dans le r?f?rentiel l'id correspondant au nom du composant
            idCompo = this.getIdFromFile(comp.getNomFichier());
          }
          else {
            idCompo = -1;
          }

          // si renvoie -1, on essaye de charger un composant qui a ?t? supprim? du r?f?rentiel
          if (idCompo == -1) {
            // on l'ajoute ? la liste des composants ? supprimer
            listeComposant.addElement(comp.getIdComposant());
          }
          else {
            // Remplis les HashMap avec la r?f?rence du composant en cl? et l'id du composant en valeur
            this.ajouterReferenceMemoire(comp, idCompo);
          }
        }
        else {
          idCompo = this.nomComposantToId(comp.getNomComposant());
          // Remplis les HashMap avec la r?f?rence du composant en cl? et l'id du composant en valeur
          this.ajouterReferenceMemoire(comp, idCompo);
        }
      }
      // c'est un paquetage de pr?sentation qu'il faut aussi v?rifier
      else {
        // on r?cup?re le paquetage
        paquet = (PaquetagePresentation) tab.get(i);

        // Reinitialisation du nom de fichier du composant
        ElementReferentiel eRef = this.chercherElement(this.nomPresentationToId(paquet.toString()), ElementReferentiel.PRESENTATION);
        if (eRef != null) {
          paquet.setNomFichier(eRef.getChemin());
          // On cherche dans le r?f?rentiel l'id correspondant au nom du composant
          idPresent = this.nomPresentationToId(this.extraireNomFichier(paquet.getNomFichier()));
        }
        else {
          idPresent = -1;
        }

        //On cherche dans le r?f?rentiel l'id correspondant au paquetage
        idPresent = this.nomPresentationToId(this.extraireNomFichier(paquet.getNomFichier()));

        // si renvoie -1, on essaye de charger un composant qui a ?t? supprim? du r?f?rentiel
        if (idPresent == -1) {
          // on l'ajoute ? la liste des composants ? supprimer
          listeComposant.addElement(paquet);
        }
      }
    }
   

    return projet;
  }

  /**
   * Charge un paquetage de pr?sentation du r?f?rentiel en m?moire
   * @param idPres : identifiant du paquetage de pr?sentation du r?f?rentiel ? charger
   * @return le paquetage de pr?sentation charg? en m?moire
   */
  public PaquetagePresentation chargerPresentation(long idPres) {
    TaskMonitorDialog dialogAvancee = null;
    ElementReferentiel feuille = null;

    feuille = this.chercherElement(idPres, ElementReferentiel.PRESENTATION);

    // Construction du chargeur de composant qui est aussi une tache
    ChargeurPaquetagePresentation chargeur = new ChargeurPaquetagePresentation(feuille.getChemin());

    // Affiche la bo?te d'avancement
    dialogAvancee = new TaskMonitorDialog(Application.getApplication().getFenetrePrincipale(), chargeur);
    dialogAvancee.setTitle(Application.getApplication().getTraduction("Chargement"));

    chargeur.setTask(dialogAvancee);
    dialogAvancee.show();

    // Remplis les HashMap avec la r?f?rence du composant en cl? et l'id du composant en valeur
    PaquetagePresentation paq = chargeur.getPaquetageCharge();
    this.ajouterReferenceMemoire(paq, idPres);

    // Reinitialiser le chemin
    //paq.setNomFichier(this.chercherElement(this.nomComposantToId(paq.toString()),ElementReferentiel.COMPOSANT).getChemin());

    // Retourne le paquetage de pr?sentation
    return paq;
  }

  /**
   * Met ? jour la sauvegarde physique d'une d?finition de processus dans le r?f?rentiel.
   * @param comp d?finition de processus ? sauvegarder dans le r?f?rentiel (mettre ? jour)
   */
  public void sauverDefProc(DefinitionProcessus defProc) {
    ElementReferentiel feuille;
    long idElt;

    //System.out.println(defProc);

    // On r?cup?re l'id de la d?finition processus dans la HashMap gr?ce ? la r?f?rence de l'objet
    idElt = ( (Long) elementToId.get(defProc)).longValue();

    // On va chercher la d?finition processus correspondante dans l'arbre
    feuille = this.chercherElement(idElt, ElementReferentiel.DP);

    // On sauve la d?finition processus ? l'endroit o? elle est stock?e dans le r?f?rentiel
    CSauvegarderDP saveDp = new CSauvegarderDP(feuille.getChemin());
    saveDp.executer();

  }

  /**
   *
   */
  public void sauverComposantVide(ComposantProcessus cp) {
    ElementReferentiel feuille;
    long idElt;

    //System.out.println(cp);

    // On r?cup?re l'id de la d?finition processus dans la HashMap gr?ce ? la r?f?rence de l'objet
    idElt = ( (Long) elementToId.get(cp)).longValue();

    //System.out.println(idElt);

    // On va chercher le composant vide correspondant dans l'arbre
    feuille = this.chercherElement(idElt, ElementReferentiel.COMPOSANT_VIDE);

    // On sauve la d?finition processus ? l'endroit o? elle est stock?e dans le r?f?rentiel
    CEnregistrerInterface saveCp = new CEnregistrerInterface(cp.getIdComposant());
    saveCp.sauvegarderInterface(feuille.getChemin());
    //saveCp.executer();

  }

  //------------------------------------------------------------------//
  //					  Accesseurs et modificateurs                   //
  //------------------------------------------------------------------//

  /**
   * Retourne le nom du r?f?rentiel
   */
  public String getNomReferentiel() {
    return nomReferentiel;
  }

  /**
   * Retourne le chemin physique o? se situe le r?f?rentiel
   */
  public String getCheminReferentiel() {
    return cheminReferentiel;
  }
  
  /**
   * Retourne le chemin de l'exportation
   */
  public String getCheminExport() {
    return cheminExport;
  }

  /**
   * Retourne le dernier Id attricbu?
   */
  public long getLastId() {
    return lastId;
  }

  /**
   * Retourne l'id pour l'?l?ment ? ajouter dans le r?f?rentiel
   */
  public long getIdPourNouvelElement() {
    return (++lastId);
  }

  //modif 2XMI jean
  //id du composant
  //les espaces sont remplac?s par des '_'
  //id du type : Nom_ref?rentiel-Nom_composant
  public String getIdReferentielComposant(ComposantProcessus _ComposantProcessus) {
    return new String(this.getNomReferentiel().replace(' ', '_') + "-" + (this.chercherElement(this.nomComposantToId(_ComposantProcessus.getNomComposant()), ElementReferentiel.COMPOSANT)).getNomElement().replace(' ', '_'));
  }

  //id du processus
  //les espaces sont remplac?s par des '_'
  //id du type : Nom_ref?rentiel-Nom_processus
  public String getIdReferentielDefProc(DefinitionProcessus _DefinitionProcessus) {
    return new String(this.getNomReferentiel().replace(' ', '_') + "-" + _DefinitionProcessus.getNomDefProc().replace(' ', '_'));//this.chercherElement(this.chercherId(_DefinitionProcessus), ElementReferentiel.DP).getNomElement().replace(' ', '_'));
  }

  //fin modif 2XMI jean

  /**
   * Retourne l'arborescence des ?l?ments du r?f?rentiel
   */
  public DefaultTreeModel getArbre() {
    return arbre;
  }

  /**
   * Retourne la racine de l'arbre
   */
  public ElementReferentiel getRacine() {
    return racine;
  }
  
  /**
   * Retourne le noeud des composants
   */
  public ElementReferentiel getNoeudComposants() {
    return composants;
  }

  /**
   * Retourne le noeud des composants
   */
  public ElementReferentiel getNoeudDp() {
    return dp;
  }

  /**
   * Retourne le noeud des presentations
   */
  public ElementReferentiel getNoeudPresentation() {
    return present;
  }
  
  public ElementReferentiel getNoeudScenarios() {
	    return scenarios;
	  }

  /**
   * Renvoie le ieme fils du noeud composant
   * @param index : num?ro du fils que l'on recherche
   * @return le composant recherch?
   */
  public String getFileFromId(long id) {
	  String ret = "";
	  for(int i=0 ; i<this.getNoeudComposants().getChildCount();i++) {
		  ElementReferentiel er = (ElementReferentiel)this.getNoeudComposants().getChildAt(i);
		  if(er.getIdElement()==id) {
			  ret=er.getChemin();
		  }
	  }
	  return getNomFichier(ret);
  }
  
  public long getIdFromFile(String chemin_file) {
	  long idc = -1;
	  for(int i=0 ; i<this.getNoeudComposants().getChildCount();i++) {
		  ElementReferentiel er = (ElementReferentiel)this.getNoeudComposants().getChildAt(i);
		  if(er.getChemin().equals(chemin_file)) {
			  idc=er.getIdElement();
		  }
	  }
	  return idc;//getNomFichier(ret);
  }

  public long getLongFromNom(String chemin_fic) {
	  long id=-1;
	  for(int i=0 ; i<this.getNoeudComposants().getChildCount();i++) {
		  ElementReferentiel er = (ElementReferentiel)this.getNoeudComposants().getChildAt(i);
		  if(getNomFichier(er.getChemin()).equalsIgnoreCase(chemin_fic.toLowerCase())) {
			  id = er.getIdElement();
		  }
	  }
	  return id;
  }

  private String getNomFichier(String s) {
	  return s.substring(s.lastIndexOf(File.separator)+1);
  }
  
  /**
   * Renvoie le ieme fils du noeud composant
   * @param index : num?ro du fils que l'on recherche
   * @return le composant recherch?
   */
  public ElementReferentiel getComposantAt(int index) {
    return ( (ElementReferentiel)this.getNoeudComposants().getChildAt(index));
  }

  
  /**
   * Renvoie le ieme fils du noeud DP
   * @param index : num?ro du fils que l'on recherche
   * @return le DP recherch?
   */
  public ElementReferentiel getDefProcAt(int index) {
    return ( (ElementReferentiel)this.getNoeudDp().getChildAt(index));
  }

  /**
   * Renvoie le ieme fils du noeud presentation
   * @param index : num?ro du fils que l'on recherche
   * @return l'elt de pr?sentation recherch?
   */
  public ElementReferentiel getPresentationAt(int index) {
    return ( (ElementReferentiel)this.getNoeudPresentation().getChildAt(index));
  }

  /**
   * Modifie le nom du r?f?rentiel
   */
  public void setNomReferentiel(String nom) {
    nomReferentiel = nom;
    // Notifie aux observateurs que l'arbre a ?t? modifi?
    this.majObserveurs(CHANGED);
  }

  /**
   * Incr?mente le dernier Id attribu?
   */
  public void incrLastId() {
    lastId++;
  }

  /**
   * Retourne l'id correspondant ? la r?f?rence de l'objet dans la HashMap ayant pour cl?s les r?f?rences des objets
   * Retourne -1 si la r?f?rence n'est pas trouv?e dans la HashMap
   * @param obj : la r?f?rence de l'objet
   * @return l'id de l'objet dans le r?f?rentiel ou -1 si la r?f?rence n'est pas trouv?e
   */
  public long chercherId(Object obj) {
    Long idElt;
    idElt = (Long) elementToId.get(obj);
    if (idElt == null) {
      return -1;
    }
    else {
      return idElt.longValue();
    }
  }

  /**
   * Retourne la r?f?rence de l'objet correspondant ? l'id dans la HashMap ayant pour cl?s les id des ?l?ments
   * Retourne null si la r?f?rence n'est pas trouv?e dans la HashMap
   * @param idElt : l'id de l'objet
   * @return la r?f?rence de l'objet ou null si la r?f?rence n'est pas trouv?e
   */
  public Object chercherReference(long idElt) {
    Long id = new Long(idElt);

    // Retourne la r?f?rence de l'objet
    return idToElement.get(id);
  }

  /**
   * Ajoute aux listes du r?f?rentiel une association id-r?f?rence
   * @param obj r?f?rence sur l'objet ? noter
   * @param id identifiant de l'objet dans le r?f?rentiel
   */
  public void ajouterReferenceMemoire(Object obj, long id) {
    elementToId.put(obj, new Long(id));
    idToElement.put(new Long(id), obj);
  }

  /**
   * Supprime les entr?es dans les 2 HashMap correspondant ? l'?l?ment qu'on veut supprimer du graphe
   * @param obj : la r?f?rence de l'objet
   */
  public void supprimerElementEnMemoire(Object obj) {
	Long idElt;

    // On r?cup?re l'id pour pouvoir supprimer la l'entr?e dans la 2e HashMap
    idElt = (Long) elementToId.get(obj);

    // Supression de l'entr?e dans la 1e HashMap
    elementToId.remove(obj);

    // Supression de l'entr?e dans la 2e HashMap
    idToElement.remove(idElt);
  }


  /**
   * Supprime toutes les entr?es dans les 2 HashMap
   */
  public void supprimerTousLesElementsCharges() {
	// Vide la 1e HashMap
    elementToId.clear();

    // Vide la 2e HashMap
    idToElement.clear();
  }

  /**
   * Indique si l'objet ayant l'IdObjetModele pass? en param?tre est d?j? charg? dans la d?finition processus en cours
   * @param obj : la r?f?rence de l'objet
   */
  public boolean isDejaCharge(IdObjetModele idObj) {
    ComposantProcessus comp;
    Vector tab;
    int ind;

    // On r?cup?re le contenu du HashMap dans un vecteur
    tab = (Vector) elementToId.values();

    // On parcours le vecteur
    for (ind = 0; ind < tab.size(); ind++) {
      // On r?cup?re le composant processus ? partir de sa r?f?rence
      comp = ( (ComposantProcessus) tab.elementAt(ind));
      // Si les 2 objets ont le m?me IdObjetModele, on retourne vrai
      if (idObj == comp.getIdComposant()) {
        return true;
      }
    }
    // Cas o? l'on ne trouve pas la correspondance
    return false;
  }

  /**
   * Cherche l'?l?ment correspondant ? l'id donn?e dans l'arbre
   * @param idComp : Id de l'?l?ment
   * @param type : Cha?ne de caract?res permettant de savoir quel type d'?l?ment (ElementReferentiel.COMPOSANT, ElementReferentiel.DP ou ElementReferentiel.PRESENTATION)
   * est recherch? ("DP" pour une d?finition processus et "Composant" pour un composant)
   * @return l'ElementReferentiel recherch?
   */
  public ElementReferentiel chercherElement(long idComp, int type) {
    ElementReferentiel feuille;
    boolean trouve = false;
    int indice = 0;

    if (type == ElementReferentiel.COMPOSANT || type == ElementReferentiel.COMPOSANT_VIDE) {
      // S?lectionne la 1e feuille de la branche des DP
      feuille = this.getComposantAt(indice++);
    }
    else {
      if (type == ElementReferentiel.DP) {
        // S?lectionne la 1e feuille de la branche des composants
        feuille = this.getDefProcAt(indice++);
      }
      else { // type == PRESENTATION
        // S?lectionne la 1e feuille de la branche des composants
        feuille = this.getPresentationAt(indice++);
      }

    }

    while ( (feuille != null) && (trouve == false)) {
      // Comparaison de l'ID de la feuille avec l'ID pass? en param?tre
      if (feuille.getIdElement() == idComp) {
        trouve = true;
      }
      else {
        // On passe au composant suivant
        feuille = (ElementReferentiel) feuille.getNextLeaf();
      }
    }

    if (trouve == true) {
      return feuille;
    }
    else {
      return null;
    }
  }

  /**
   * Retourne la liste de tous les noms des ?l?ments (dont le type est pass? en param?tre) pr?sents dans le r?f?rentiel
   * @param type : type des ?l?ments dont on veut le nom (ElementReferentiel.COMPOSANT, ElementReferentiel.DP ou ElementReferentiel.PRESENTATION)
   * @return la liste des noms des ?l?ments sous forme de vector
   */
  public Vector getListeNom(int type) {
    Vector tab = new Vector();
    ElementReferentiel feuille = null;
    int indice = 0;

    try {
      do {
        // On récupère les éléments de l'arbre
        switch (type) {
          case (ElementReferentiel.COMPOSANT):
            feuille = this.getComposantAt(indice++);
            break;
          case (ElementReferentiel.DP):
            feuille = this.getDefProcAt(indice++);
            break;
          case (ElementReferentiel.PRESENTATION):
            feuille = this.getPresentationAt(indice++);
            break;
        }

        // Ajout du nom de l'élément dans le vecteur
        tab.add(feuille.getNomElement());
      }
      while (feuille != null);

      return tab;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return tab;
    }
  }

  /**
   * Retourne l'id de la d?finition processus dont on a pass? le nom en param?tre
   * @param nom : nom de la d?finition processus
   * @return l'id de la d?finition processus ou -1 si le nom ne correspond ? aucune DP du r?f?rentiel
   */
  public long nomDefProcToId(String nom) {
    ElementReferentiel feuille = null;
    int indice = 0;

    try {
      do {
        // On consulte les diff?rentes DP
        feuille = this.getDefProcAt(indice++);

        // Si les noms correspondent, on renvoie l'id
        if (feuille.getNomElement().equalsIgnoreCase(nom)) {
          return feuille.getIdElement();
        }
      }
      while (feuille != null);

      // Si pas de correspondance, on renvoie -1
      return -1;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return -1;
    }
  }

  /**
   * Retourne l'id du composant dont on a pass? le nom en param?tre
   * @param nom : nom du composant
   * @return l'id du composant ou -1 si le nom ne correspond ? aucun composant du r?f?rentiel
   */
  public long nomComposantToId(String nom) {
    ElementReferentiel feuille = null;
    int indice = 0;
    
    // Pour enlever les caractères siganlant que le composant est dans le statique
    String fin = Application.getApplication().getTraduction("Comp_Present");
    String f;
    try {
      do {
        // On consulte les diff?rentes DP
        feuille = this.getComposantAt(indice++);
        
        if(feuille.getNomElement().endsWith(fin)) {
        	f = feuille.getNomElement().replace(fin, "");
        } else {
        	f = feuille.getNomElement();
        }
        // Si les noms correspondent, on renvoie l'id
        if (f.equalsIgnoreCase(nom)) {
          return feuille.getIdElement();
        }
      }
      while (feuille != null);

      // Si pas de correspondance, on renvoie -1
      return -1;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return -1;
    }
  }

  /**
   * Retourne l'id du paquetage de pr?sentation dont on a pass? le nom en param?tre
   * @param nom : nom du paquetage de pr?sentation
   * @return l'id du paquetage ou -1 si le nom ne correspond ? aucun paquetage du r?f?rentiel
   */
  public long nomPresentationToId(String nom) {
    ElementReferentiel feuille = null;
    int indice = 0;

    try {
      do {
        // On consulte les diff?rentes pr?sentations
        feuille = this.getPresentationAt(indice++);

        // Si les noms correspondent, on renvoie l'id
        if (feuille.getNomElement().equalsIgnoreCase(nom)) {
          return feuille.getIdElement();
        }
      }
      while (feuille != null);

      // Si pas de correspondance, on renvoie -1
      return -1;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return -1;
    }
  }

  //------------------------------------------------------------------//
  //					       Acc?s au fichier                         //
  //------------------------------------------------------------------//

  /**
   * Extrait uniquement le nom du fichier (sans l'extension) du chemin d'acc?s ? celui-ci
   * @param cheminFichier : le chemin d'acc?s au fichier
   * @return Le nom du fichier (sans l'extension)
   * @throws Exception
   */
  public String extraireNomFichier(String cheminFichier) {
    File f = new File(cheminFichier);
    if (f.exists()) {
      return (f.getName()).substring(0, (f.getName()).indexOf("."));
    }
    return "";
  }

  /**
   * Extrait l'ID de la DP ? partir du chemin d'acc?s ? son fichier
   * @param cheminFichier : le chemin d'acc?s au fichier
   * @return L'ID de la DP
   */
  public long extraireIdChemin(String cheminFichier) {
    File f = new File(cheminFichier);
    if (f.exists()) {
      return new Long(f.getName()).longValue();
    }
    return -1;
  }

  /**
   * Met ? jour le fichier r?f?rentiel en inscrivant le dernier id attribu?
   * @param id : Dernier Id attribu?
   */
  /*
  public void majDerniereIdDansFichier(long id) {
    try {
      RandomAccessFile raf = new RandomAccessFile(cheminReferentiel + File.separator + nomReferentiel + ".ref", "rw");

      //Positionnement dans le fichier pour l'?criture
      raf.seek(14);

      // Ecriture de la nouvelle Id
      raf.writeBytes(String.valueOf(id));
      raf.close();
    }
    catch (IOException e) {
      e.printStackTrace();
      ErrorManager.getInstance().display(e);
    }
  }
  */

  //------------------------------------------------------------------//
  //					  Implementation de TreeModel                   //
  //------------------------------------------------------------------//

  /**
   * M?thode appel?e par les ?couteurs de l'adapteur pour s'enregistrer aupr?s de lui
   * @param ecouteur, ecouteur de l'adapteur (donc du mod?le)
   */
  public void addTreeModelListener(TreeModelListener ecouteur) {
    this.getArbre().addTreeModelListener(ecouteur);
  }

  /**
   * Renvoie le ieme fils d'un objet parent
   * @param parent, objet dont on recherche un fils
   * @param ieme, num?ro du fils que l'on recherche
   * @return l'Id du fils recherch?
   */
  public Object getChild(Object parent, int index) {
    return this.getArbre().getChild(parent, index);
  }

  /**
   * Renvoie le nombre de fils de l'objet courant obj
   */
  public int getChildCount(Object obj) {
    return this.getArbre().getChildCount(obj);
  }

  /**
   * Renvoie l'indice auquel se trouve l'enfant d'un parent donn?
   * @param parent : objet dont on recherche l'indice du fils
   * @param fils : objet dont on recherche l'indice
   * @return l'indice du fils recherch? parmis l'ensemble des fils
   */
  public int getIndexOfChild(Object parent, Object enfant) {
    return this.getArbre().getIndexOfChild(parent, enfant);
  }

  /**
   * Renvoie la racine de l'arbre, l'id de l'objet modele Definition de Processus
   * @return l'Id de la d?finition de processus
   */
  public Object getRoot() {
    return this.getArbre().getRoot();
  }

  /**
   * Indique si l'objet courant obj est une feuille de l'arbre ou non
   */
  public boolean isLeaf(Object obj) {
    return this.getArbre().isLeaf(obj);
  }

  /**
   * M?thode permettant de supprimer un ?couteur dans la liste des
   * ?couteurs de l'adapteur
   * @param ecouteur : ecouteur de l'adapteur ? supprimer
   */
  public void removeTreeModelListener(TreeModelListener ecouteur) {
    this.getArbre().removeTreeModelListener(ecouteur);
  }

  /**
   * M?thode appel?e lorsque l'utilisateur a modifi? la valeur d'un item identifi?
   * par path pour une nouvelle valeur newValue, si la nouvelle valeur est valide,
   *  on prend en compte la modification
   */
  public void valueForPathChanged(TreePath path, Object valeur) {
    this.getArbre().valueForPathChanged(path, valeur);
  }

  //------------------------------------------------------------------//
  //					  Utilisation de Observable                     //
  //------------------------------------------------------------------//

  /**
   * Notifie les observateurs qu'une modification a ?t? apport? ? l'arbre
   */
  public void majObserveurs(int code) {
    this.setChanged();
    this.notifyObservers(new Integer(code));
  }

  public ElementReferentiel getDernierElementAjoute ()
  {
    return this.dernierComposantAjoute;
  }
}
