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

package iepp.domaine;

import iepp.Application;
import iepp.application.aedition.annulation.Memento;
import iepp.application.aedition.annulation.ObjetAnnulable;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.util.HashMap;
import java.util.Vector;

import org.ipsquad.apes.adapters.SpemGraphAdapter;
import org.ipsquad.apes.model.extension.ApesWorkDefinition;
import org.ipsquad.apes.model.extension.ContextDiagram;
import org.ipsquad.apes.model.extension.ResponsabilityDiagram;
import org.ipsquad.apes.model.extension.SpemDiagram;
import org.ipsquad.apes.model.extension.WorkDefinitionDiagram;
import org.ipsquad.apes.model.spem.core.Element;
import org.ipsquad.apes.model.spem.modelmanagement.IPackage;
import org.ipsquad.apes.model.spem.process.components.ProcessComponent;
import org.ipsquad.apes.model.spem.process.structure.Activity;
import org.ipsquad.apes.model.spem.process.structure.ProcessPerformer;
import org.ipsquad.apes.model.spem.process.structure.ProcessRole;
import org.ipsquad.apes.model.spem.process.structure.WorkDefinition;
import org.ipsquad.apes.model.spem.process.structure.WorkProduct;
import org.ipsquad.apes.model.spem.statemachine.StateMachine;

/**
 * Classe métier contenant les données concernant le Composant de processus
 * C'est un objet du modele et est observé par plusieurs vues (arbre et le diagramme)
 * C'est classe est un intermédiaire pour accéder aux données du composant publiable en entrée
 */
public class ComposantProcessus extends ObjetModele implements ObjetAnnulable
{

	/**
	 * Indique le type des objets contenus dans un composant de processus
	 */
	private final int INTERFACES = 0 ;
	private final int PRODUITS_ENTREE = 1 ;
	private final int PRODUITS_SORTIE = 2 ;
	private final int PAQUETAGE = 3 ;
	private final int ACTIVITE = 4 ;
	private final int ROLE = 5 ;
	private final int DIAGRAMME = 6 ;
	private final int PRODUIT = 7 ;
	private final int DEFINITION_TRAVAIL = 8 ;


	// liste des produits
	private Vector interfaceIn ;
	private Vector interfaceOut ;
	private ProcessComponent composantAPES;
	private HashMap mapDiagramme;
	private Vector activites;
	private Vector roles;
	private Vector produits;
	private Vector listePaquetage ;
	private Vector listeDiagramme ;
	private Vector listeDefinition ;
	private PaquetagePresentation paquetage;
	private boolean estComposantVide ;
	private WorkDefinitionDiagram diagramme_flot_definition ;
	private ContextDiagram diagramme_contexte ;

	private String nomFic ; // nom du fichier .pre contenant le modèle
        
	private boolean est_renomme;//modif 2xmi youssef

	/** Constantes de codes d'actions annulables. Le composant stocke la modification
	 * sous la forme suivante : "CODE:paramètres", où le format du paramètre dépend
	 * de la commande à annuler.
	 */
	protected final String ANN_SEP = ":" ;
	protected final String ANN_NOM = "NOM" ;

	/**
	 * Lien vers la définition de processus
	 */
	private DefinitionProcessus defProc = null ;


	/**
	 * Id du composant processus courant
	 */
	private IdObjetModele idComposant = null;


	/**
	 * Liste des liens vers les autres composants
	 */
	private Vector listeLiens = null;

	/**
	 * Nom du composant processus
	 */
	private String nom ;

	/**
	 * Map des IdObjetModele des éléments du modèle gérés par le composant
	 * Cette map fait l'association entre les IdObjetModele
	 * et les éléments réels
	 */
	private HashMap mapId = null;

	private HashMap mapApesPresent = null;



	/**
	 * Construction d'un composant vide
	 */
	public ComposantProcessus (DefinitionProcessus defProc)
	{
		this.defProc = defProc;
		this.idComposant = new IdObjetModele(this);
		// initialiser la liste des liens produit-produit
		this.listeLiens = new Vector();
		// initialiser la map
		this.mapId = new HashMap();
		this.mapApesPresent = new HashMap();
		this.interfaceIn = new Vector();
		this.interfaceOut = new Vector();
		this.activites = new Vector();
		this.roles = new Vector();
		this.listePaquetage = new Vector();
		this.listeDiagramme = new Vector();
		this.produits = new Vector();
		this.listeDefinition = new Vector();
		this.paquetage = new PaquetagePresentation();
		this.est_renomme = false;
	}


	public void setNomFichier(String nom)
	{
		this.nomFic = nom.trim();
	}

	public void setVide(boolean vide)
	{
		this.estComposantVide = vide;
	}
        public void setRenomme(boolean r)
        {
          this.est_renomme = r;
        }
        public boolean estRenomme()
        {
                return this.est_renomme;
        }


	public boolean estVide()
	{
		return this.estComposantVide;
	}

	public String getNomFichier()
	{
		return (this.nomFic);
	}

	public String toString()
	{
		return (this.nom);
	}

	public void initialiser(HashMap elementsPresentation)
	{
		this.mapApesPresent = elementsPresentation;
		this.parcourir(this.composantAPES);
		// associer les éléments du modèle à des éléments de présentation
		// traiter les activités
		this.associerModelePresentation(this.getActivites());
		this.associerModelePresentation(this.getProduits());
		this.associerModelePresentation(this.getRoles());
		this.associerModelePresentation(this.getDiagrammes());
		this.associerModelePresentation(this.getDefinitionTravail());
		this.associerModelePresentation(this.getPaquetages());
		// on associe la présentation au composant courant
		ElementPresentation elem = (ElementPresentation)this.mapApesPresent.get(new Integer(this.idComposant.getID()));
		if ( elem != null )
		{
			elem.setElementModele(this.idComposant);
		}
	}
	
	public void initialiserPresentation(HashMap elementsPresentation)
	{
		this.mapApesPresent = elementsPresentation;
		// traiter les activités
		this.associerModelePresentation(this.getActivites());
		this.associerModelePresentation(this.getProduits());
		this.associerModelePresentation(this.getRoles());
		this.associerModelePresentation(this.getDiagrammes());
		this.associerModelePresentation(this.getDefinitionTravail());
		this.associerModelePresentation(this.getPaquetages());
		// on associe la présentation au composant courant
		ElementPresentation elem = (ElementPresentation)this.mapApesPresent.get(new Integer(this.idComposant.getID()));
		if ( elem != null )
		{
			elem.setElementModele(this.idComposant);
		}
	}

	public void associerModelePresentation(Vector listeId)
	{

		for (int i = 0; i < listeId.size(); i++)
		{
			IdObjetModele id = (IdObjetModele)listeId.elementAt(i);
			// récupérer l'élément du
			ElementPresentation elem = (ElementPresentation)this.mapApesPresent.get(new Integer(id.getID()));
			if ( elem != null )
			{
				elem.setElementModele(id);
			}
		}
	}

	// construit les listes d'éléments
	public void parcourir(IPackage md)
	{
		for ( int i = 0; i < md.modelElementCount(); i++ )
		{
			//System.out.println(md.getModelElement(i) + "-" + md.getModelElement(i).getClass() + "-" + md.getModelElement(i).getID());
			if ( md.getModelElement(i) instanceof WorkProduct)
			{
				this.produits.addElement((WorkProduct)md.getModelElement(i));
			}
			else if ( md.getModelElement(i) instanceof Activity)
			{
				this.activites.addElement((Activity)md.getModelElement(i));
			}
			else if ( md.getModelElement(i) instanceof ApesWorkDefinition)
			{
				this.listeDefinition.addElement((ApesWorkDefinition)md.getModelElement(i));
				this.parcourir((IPackage)md.getModelElement(i));
			}
			else if ( md.getModelElement(i) instanceof IPackage )
			{
				this.listePaquetage.addElement((IPackage)md.getModelElement(i));
				this.parcourir((IPackage)md.getModelElement(i));
			}
			else if ( md.getModelElement(i) instanceof ProcessRole)
			{
				this.roles.addElement((ProcessRole)md.getModelElement(i));
			}
			// diagrammes
			else if ( md.getModelElement(i) instanceof WorkDefinitionDiagram)
			{
				this.diagramme_flot_definition = ((WorkDefinitionDiagram)md.getModelElement(i));
				this.listeDiagramme.addElement((SpemDiagram)md.getModelElement(i));
			}
			else if ( md.getModelElement(i) instanceof ContextDiagram)
			{
				this.diagramme_contexte = ((ContextDiagram)md.getModelElement(i));
				this.listeDiagramme.addElement((SpemDiagram)md.getModelElement(i));
			}
			else if ( md.getModelElement(i) instanceof SpemDiagram)
			{
				this.listeDiagramme.addElement((SpemDiagram)md.getModelElement(i));
			}
		}
	}
	
	public void setProcessComponent(ProcessComponent pc)
	{
		this.composantAPES = pc;
		//this.parcourir(this.composant, this);
		this.nom = this.composantAPES.getName().trim();
	}

	public void setMapDiagram(HashMap hm)
	{
		this.mapDiagramme = hm ;
	}

	public void ajouterProduit(String nomProd, int type)
	{
		switch (type)
		{
			case 0: this.interfaceIn.add(new Produit(nomProd)); break;
			case 1: this.interfaceOut.add(new Produit(nomProd)); break;
		}
	}

	public PaquetagePresentation getPaquetage()
	{
		return this.paquetage;
	}


	public Vector getInterfaceIn()
	{
		return this.interfaceIn;
	}

	public Vector getInterfaceOut()
	{
		return this.interfaceOut;
	}


	/**
	 * Renvoie le nom du composant courant
	 * @return nom du composant de processus courant
	 */
	public String getNomComposant()
	{
		return (this.nom);
	}

	/**
	 * Modifie le nom du composant courant
	 * @param nom, nouveau du composant courant
	 */
	public void setNomComposant (String nom)
	{
		// modifie le nom du composant
		this.nom = nom.trim();
		// indique que l'on a modifié le composant courant
		this.defProc.maj("CHANGED");
	}

	/**
	 * Renvoie l'ID du composant courant
	 * @return id du composant courant
	 */
	public IdObjetModele getIdComposant()
	{
		return this.idComposant;
	}


	public void ajouterLien(LienProduits lien)
	{
		this.listeLiens.add(lien);
	}

	public Vector getLien()
	{
		return this.listeLiens;
	}

	public void supprimerLien(LienProduits lien)
	{
		this.listeLiens.removeElement(lien);
	}

	public void supprimerLien(LienEdge lien)
	{
		for (int i = 0; i < this.listeLiens.size(); i++)
		{
			if ( ((LienProduits)(this.listeLiens.elementAt(i))).getLienFusion() == lien )
			{
				this.listeLiens.removeElementAt(i);
			}
		}
	}

	public void supprimerLien(IdObjetModele id)
	{
		for (int i = 0; i < this.listeLiens.size(); i++)
		{
			if ( ((LienProduits)(this.listeLiens.elementAt(i))).getProduitEntree() == id || ((LienProduits)(this.listeLiens.elementAt(i))).getProduitSortie() == id  )
			{
				this.listeLiens.removeElementAt(i);
			}
		}
	}

	//-------------------------------------------------------------------
	// Getters pour le parcours du composant
	//-------------------------------------------------------------------

	/**
	 * Retourne la liste des activites du composant
	 * @return la liste des activites du composant
	 */
	public Vector getActivites()
	{
		Vector listeActivites = new Vector();
		Object o;

		 for (int i = 0; i < this.activites.size(); i++ )
		 {
			 o = this.activites.elementAt(i);
			 if (!this.mapId.containsKey(o))
			 {
				 this.mapId.put(o,new IdObjetModele(this,i,ACTIVITE));
			 }
			 listeActivites.addElement(this.mapId.get(o));
		 }
		return listeActivites;
	}

	public Vector getPaquetages()
	{
		Vector listepaq = new Vector();
		Object o;

		 for (int i = 0; i < this.listePaquetage.size(); i++ )
		 {
			 o = this.listePaquetage.elementAt(i);
			 if (!this.mapId.containsKey(o))
			 {
				 this.mapId.put(o,new IdObjetModele(this,i,PAQUETAGE));
			 }
			 listepaq.addElement(this.mapId.get(o));
		 }
		return listepaq;
	}

	/**
	 *
	 */
	public Vector getDefinitionTravail()
	{
		Vector listedef = new Vector();
		Object o;

		 for (int i = 0; i < this.listeDefinition.size(); i++ )
		 {
			 o = this.listeDefinition.elementAt(i);
			 if (!this.mapId.containsKey(o))
			 {
				 this.mapId.put(o,new IdObjetModele(this,i,DEFINITION_TRAVAIL));
			 }
			listedef.addElement(this.mapId.get(o));
		}
		return listedef;
	}
	
	/**
	 * Retourne la liste des roles du composant
	 * @return la liste des roles du composant
	 */
	public Vector getRoles()
	{
		Vector listeRoles = new Vector();
		Object o;

		 for (int i = 0; i < this.roles.size(); i++ )
		 {
			 o = this.roles.elementAt(i);
			 if (!this.mapId.containsKey(o))
			 {
				 this.mapId.put(o,new IdObjetModele(this,i,ROLE));
			 }
			 listeRoles.addElement(this.mapId.get(o));
		 }
		return listeRoles;
	}

	public Vector getListeDefintion()
	{
		Vector listeDef = new Vector();
		Object o;

		 for (int i = 0; i < this.listeDefinition.size(); i++ )
		 {
			 o = this.listeDefinition.elementAt(i);
			 if (!this.mapId.containsKey(o))
			 {
				 this.mapId.put(o,new IdObjetModele(this,i,DEFINITION_TRAVAIL));
			 }
			 listeDef.addElement(this.mapId.get(o));
		 }
		return listeDef;
	}

	/**
	 * Retourne la liste des produits internes à un composant
	 */
	public Vector getProduits()
	{
		Vector listeProduits = new Vector();
		Object o;

		 for (int i = 0; i < this.produits.size(); i++ )
		 {
			 o = this.produits.elementAt(i);
			 if (!this.mapId.containsKey(o))
			 {
				 this.mapId.put(o,new IdObjetModele(this,i,PRODUIT));
			 }
			 listeProduits.addElement(this.mapId.get(o));
		 }
		return listeProduits;
	}


	/**
	 * Retourne la lsite des diagrammes du composant
	 */
	public Vector getDiagrammes()
	{
		Vector listeDiag = new Vector();
		Object o;

		 for (int i = 0; i < this.listeDiagramme.size(); i++ )
		 {
			 o = this.listeDiagramme.elementAt(i);
			 if (!this.mapId.containsKey(o))
			 {
				 this.mapId.put(o,new IdObjetModele(this,i,DIAGRAMME));
			 }
			 listeDiag.addElement(this.mapId.get(o));
		 }
		return listeDiag;
	}



	//------------------------------------------------------------------------
	// Gestion des interfaces
	//------------------------------------------------------------------------

	/**
	 * Renvoie la liste des interfaces requises (produits en entrée)
	 * @return liste des produits en entrée
	 */
	public Vector getProduitEntree()
	{
		Vector interfIn = this.getInterfaceIn();
		Vector listeEntree = new Vector();

		// Produit courant
		Object prod;

		for (int i = 0; i < interfIn.size(); i++ )
		{
			prod = interfIn.elementAt(i);
			if (!this.mapId.containsKey(prod))
			{
				this.mapId.put(prod,new IdObjetModele(this,i,PRODUITS_ENTREE));
			}
			listeEntree.addElement(this.mapId.get(prod));
		}
		return listeEntree;
	}

	/**
	 * Renvoie la liste des interfaces fournies (produits en sortie)
	 * @return liste des produits en sortie
	 */
	public Vector getProduitSortie()
	{
		Vector interfOut = this.getInterfaceOut();
		Vector listeSortie = new Vector();

		// Produit courant
		Object prod;

		for (int i = 0; i < interfOut.size(); i++ )
		{
			prod = interfOut.elementAt(i);
			if (!this.mapId.containsKey(prod))
			{
				this.mapId.put(prod,new IdObjetModele(this,i,PRODUITS_SORTIE));
			}
			listeSortie.addElement(this.mapId.get(prod));
		}
		return listeSortie;
	}


	/**
	 * Permet de rajouter un nouveau produit dans l'interface fournie (produit en sortie)
	 */
	public void ajouterProduitSortie()
	{
		this.interfaceOut.add(new Produit());
		this.defProc.maj("PRODUIT_INSERTED");
	}

	/**
	 * Permet de rajouter un nouveau produit dans l'interface fournie (produit en sortie)
	 */
	public void ajouterProduitEntree()
	{
		this.interfaceIn.add(new Produit());
		this.defProc.maj("PRODUIT_INSERTED");
	}

	//--------------------------------------------------------------------------//
	// Implementation de l'interface ObjetModele								//
	//--------------------------------------------------------------------------//

	/**
	 * Renvoie sous forme de chaine de caractères l'élément recherché
	 * @param numrang, rang de l'élément à rechercher, si -1, on veut récupérer le nom du composant courant
	 * @param numtype, indique le type du parent de l'élément recherché (Interafces, produits, ...)
	 */
	public String toString(int numrang, int numtype)
	{
		// on veut accéder au composant courant
		if ( numrang == -1 )
		{
			return this.nom;
		}
		// on recherche l'élément selon son type
		else
		{
			switch (numtype)
			{
				case INTERFACES:
					switch (numrang)
					{
						case 0 : return Application.getApplication().getTraduction("Interface_Requise");
						case 1 : return Application.getApplication().getTraduction("Interface_Fournie");
					}
				case PRODUITS_ENTREE: return this.getInterfaceIn().elementAt(numrang).toString();
				case PRODUITS_SORTIE: return this.getInterfaceOut().elementAt(numrang).toString();
				case ACTIVITE: return (((Element)(this.activites.elementAt(numrang))).getName());
				case ROLE: return (((Element)(this.roles.elementAt(numrang))).getName());
				case PAQUETAGE: return (((Element)(this.listePaquetage.elementAt(numrang))).getName());
				case DIAGRAMME: return (((Element)(this.listeDiagramme.elementAt(numrang))).getName());
				case PRODUIT: return (((Element)(this.produits.elementAt(numrang))).getName());
				case DEFINITION_TRAVAIL: return (((Element)(this.listeDefinition.elementAt(numrang))).getName());
			}
			return null;
		}
	}


	/**
	 * Renvoie le nombre de fils de l'élément de rang numrang et de type du parent numtype
	 * @param numrang, rang du parent dont on veut connaître le nombre de fils
	 * @param numtype, type du parent dont on veut connaître le nombre de fils
	 */
	public int getNbFils(int numrang, int numtype)
	{
		// selon le type de l'objet
		switch (numtype)
		{
			// les fils des composants sont les interfaces
			case -1: return 2  ;
			case INTERFACES :

					switch(numrang)
					{
						case 0: return this.interfaceIn.size();
						case 1: return this.interfaceOut.size();
					}
			default: return 0;
		}
	}

	/**
	 * Indique si l'objet de rang numrang et de type du parent numtype est un composant
	 * @return true si l'objet courant de rang numrang et de type numtype est un composant
	 */
	public boolean estComposant(int numrang, int numtype)
	{
		// si il n'y a pas de numéro externe, c'est un composant
		if ( numrang == -1)
		{
			return true ;
		}
		// ce n'est pas un composant
		return false ;
	}

	public boolean estDiagramme(int numrang, int numtype)
	{
		return ( numtype == DIAGRAMME);
	}

	/**
	 * Indique si l'objet de rang numrang et de type du parent numtype est une interface requise
	 * @return true si l'objet est une interface requise
	 */
	public boolean estInterfaceRequise(int numrang, int numtype)
	{
		// on regarde d'abord si c'est une interface
		if (numtype == INTERFACES)
		{
			// ensuite si c'est une interface requise
			return (numrang == 0);
		}
		return false;
	}

	public boolean estDefinitionTravail(int numrang, int numtype)
	{
		return (numtype == DEFINITION_TRAVAIL);
	}

	/**
	 * Indique si l'objet de rang numrang et de type du parent numtype est une interface fournie
	 * @return true si l'objet est une interface fournie
	 */
	public boolean estInterfaceFournie(int numrang, int numtype)
	{
		//	on regarde d'abord si c'est une interface
		if (numtype == INTERFACES)
		{
			// ensuite si c'est une interface fournie
			return (numrang == 1);
		}
		return false;
	}


	/**
	 * Indique si l'objet de rang numrang et de type numtype est une définition de processus
	 * @return false toujours
	 */
	public boolean estDefProc(int numrang, int numtype)
	{
		return false;
	}

	/**
	 * Indique si l'objet de rang numrang et de type  numtype est un produit
	 * @return true si l'objet demandé est un produit
	 */
	public boolean estProduit(int numrang, int numtype)
	{
		// vérification par rapport à son type
		return (numtype == PRODUITS_ENTREE || numtype == PRODUITS_SORTIE || numtype == PRODUIT) ;
	}

	/**
	 * Indique si l'objet de rang numrang et de type  numtype est un produit en entrée
	 * @return true si l'objet demandé est un produit
	 */
	public boolean estProduitEntree(int numrang, int numtype)
	{
		// vérification par rapport à son type
		return (numtype == PRODUITS_ENTREE ) ;
	}

	/**
	 * Indique si l'objet de rang numrang et de type  numtype est un produit en sortie
	 * @return true si l'objet demandé est un produit
	 */
	public boolean estProduitSortie(int numrang, int numtype)
	{
		// vérification par rapport à son type
		return (numtype == PRODUITS_SORTIE ) ;
	}

	/**
	 * Renvoie l'ID du ieme fils de l'objet de rang numrang et de type numtype
	 * @return id de l'objet recherché
	 */
	public IdObjetModele getFils(int ieme, int numrang, int numtype)
	{
		// vérifier selon le type du parent
		switch (numtype)
		{
			// le parent est un composant de processus
			// il faut donc retourner une interface
			case -1: if (!this.mapId.containsKey("Interf"+ieme))
						{
							this.mapId.put("Interf"+ieme, new IdObjetModele(this, ieme, INTERFACES));
						}
					return ((IdObjetModele)this.mapId.get("Interf"+ieme));

			// si le parent est une interface
			case INTERFACES:
					// on calcule le nombre de produits appartenant aux interfaces précédentes
					// retourner l'ID de l'objet trouvé
					switch(numrang)
					{
						case 0:	if (!this.mapId.containsKey(this.getInterfaceIn().elementAt(ieme)))
								{
									this.mapId.put(this.getInterfaceIn().elementAt(ieme),new IdObjetModele(this,ieme,PRODUITS_ENTREE));
								}
								return ((IdObjetModele)this.mapId.get(this.getInterfaceIn().elementAt(ieme)));
						case 1: if (!this.mapId.containsKey(this.getInterfaceOut().elementAt(ieme)))
								{
									this.mapId.put(this.getInterfaceOut().elementAt(ieme),new IdObjetModele(this,ieme,PRODUITS_SORTIE));
								}
								return ((IdObjetModele)this.mapId.get(this.getInterfaceOut().elementAt(ieme)));
					}
			// probleme au niveau du type du parent (inconnu)
			default: return null;
		}
	}

	/**
	 * Modifie le nom de l'élément de rang numrang et de type numtype
	 *@param chaine, nouveau nom de l'élément
	 *@param numrang, rang de l'élément à modifier
	 *@param numtype, type du parent de l'élément à modifier
	 */
	public void setNomElement(String chaine, int numrang, int numtype)
	{
		switch(numtype)
		{
			case -1: this.nom = chaine.trim(); break;
			case PRODUITS_ENTREE: ((Produit) (this.getInterfaceIn().elementAt(numrang))).setNom(chaine); this.defProc.maj("CHANGED"); break;
			case PRODUITS_SORTIE: ((Produit) (this.getInterfaceOut().elementAt(numrang))).setNom(chaine); this.defProc.maj("CHANGED"); break;
			case ACTIVITE: ((Element)(this.activites.elementAt(numrang))).setName(chaine);break;
			case ROLE: ((Element)(this.roles.elementAt(numrang))).setName(chaine);break;
			case PAQUETAGE: ((Element)(this.listePaquetage.elementAt(numrang))).setName(chaine);break;
			//case DIAGRAMME: ((Element)(this.listeDiagramme.elementAt(numrang))).setName(chaine);break;
			case PRODUIT:
			    		String nomActuel = ((WorkProduct)this.produits.elementAt(numrang)).getName();
			    		// Recherche de l'ancien nom dans les interfaces in
			    		for (int i = 0; i < this.interfaceIn.size(); i++)
			    		{
			    		    if (this.interfaceIn.elementAt(i).toString().equals(nomActuel))
			    		    {
					    		((Produit)this.interfaceIn.elementAt(i)).setNom(chaine);
			    		    }
			    		}
			    		// Recherche de l'ancien nom dans les interfaces out
			    		for (int i = 0; i < this.interfaceOut.size(); i++)
			    		{
			    		    if (this.interfaceOut.elementAt(i).toString().equals(nomActuel))
			    		    {
					    		((Produit)this.interfaceOut.elementAt(i)).setNom(chaine);
			    		    }
			    		}
			    		((Element)(this.produits.elementAt(numrang))).setName(chaine);
						break;
			case DEFINITION_TRAVAIL: ((Element)(this.listeDefinition.elementAt(numrang))).setName(chaine);break;
		}
	}

	/**
	 * Indique si l'objet de rang numrang et de type  numtype est un paquetage
	 * @return true si l'objet demandé est un paquetage
	 */
	public boolean estPaquetage(int numrang, int numtype)
	{
		// vérification par rapport à son type
		return (numtype == PAQUETAGE) ;
	}

	/**
	 * Indique si l'objet de rang numrang et de type numtype est une activite
	 * @return true si l'objet demandé est une activite
	 */
	public boolean estActivite(int numrang, int numtype)
	{
		//vérification par rapport à son type
		 return (numtype == ACTIVITE) ;
	}

	public boolean estRole(int numrang, int numtype)
	{
		//vérification par rapport à son type
		 return (numtype == ROLE) ;
	}


	//---------------------------------------------------
	public int getID(int numrang, int numtype)
	{
		// on veut accéder au composant courant
		if ( numrang == -1 )
		{
                  if (this.composantAPES != null)
			return this.composantAPES.getID();
		}
		// on recherche l'élément selon son type
		else
		{
			switch (numtype)
			{
				case ACTIVITE: return (((Element)(this.activites.elementAt(numrang))).getID());
				case ROLE: return (((Element)(this.roles.elementAt(numrang))).getID());
				case PRODUIT : return (((Element)(this.produits.elementAt(numrang))).getID());
				case PAQUETAGE: return (((Element)(this.listePaquetage.elementAt(numrang))).getID());
				case DIAGRAMME: return (((Element)(this.listeDiagramme.elementAt(numrang))).getID());
				case DEFINITION_TRAVAIL: return (((Element)(this.listeDefinition.elementAt(numrang))).getID());
				case PRODUITS_ENTREE:
				case PRODUITS_SORTIE:
				    					String nomProd = this.toString(numrang,numtype);
				    					for (int i = 0; i < produits.size(); i++)
				    					{
				    					    if (produits.elementAt(i).toString().equals(nomProd))
				    					    {
				    					        return ((WorkProduct)produits.elementAt(i)).getID();
				    					    }
				    					}
			}
			return -1;
		}
                return -1;
	}

	public HashMap getMapId()
	{
		return this.mapId;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Vector getIDActivite(int numrang, int numtype)
	{
		Vector listeId = new Vector();
		switch (numtype)
		{
			case ROLE :				 	ProcessRole pr = (ProcessRole)(this.roles.elementAt(numrang));
										int nbactivites = pr.getFeatureCount();
										for (int i = 0; i < nbactivites; i++)
										{
											//Activity a = (Activity) pr.getFeature(i);
											WorkDefinition a = pr.getFeature(i);
											listeId.addElement(new Integer(a.getID()));
										}
										break;
			case DEFINITION_TRAVAIL :   ApesWorkDefinition apw = (ApesWorkDefinition)this.listeDefinition.elementAt(numrang);
										for (int i = 0; i < apw.subWorkCount(); i++ )
										{
											Activity act = apw.getSubWork(i);
											listeId.addElement(new Integer(act.getID()));
										}
		}
		return listeId;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Vector getIDProduit(int numrang, int numtype)
	{
		Vector listeId = new Vector();

		ProcessRole pr = (ProcessRole)(this.roles.elementAt(numrang));
		int nbproduits= pr.getResponsibilityCount();
		for (int i = 0; i < nbproduits; i++)
		{
			WorkProduct w = pr.getResponsibility(i);
			listeId.addElement(new Integer(w.getID()));
		}
		return listeId;
	}


	public int getIDRole(int numrang, int numtype)
	{
		// le role responsable
		ProcessPerformer pf = null;
		switch(numtype)
		{
			case ACTIVITE : Activity act = (Activity)(this.activites.elementAt(numrang));
							pf = act.getOwner();
							break;
			case PRODUIT : WorkProduct w = (WorkProduct)(this.produits.elementAt(numrang));
						   pf = w.getResponsible();
						   break;
		}
		if (pf == null)
	    {
		     return (-1);
	    }
	    return pf.getID();
	}


	public Vector getIDProduitEntree(int numrang, int numtype)
	{
		Vector listeId = new Vector();

		Activity act = (Activity)(this.activites.elementAt(numrang));

		int nb = act.getInputCount();
		for (int i = 0; i < nb; i++)
		{
			listeId.addElement(new Integer(act.getInput(i).getID()));
		}
		return listeId;
	}

	public Vector getIDProduitSortie(int numrang, int numtype)
	{
		Vector listeId = new Vector();

		Activity act = (Activity)(this.activites.elementAt(numrang));
		int nb = act.getOutputCount();
		for (int i = 0; i < nb; i++)
		{
			listeId.addElement(new Integer(act.getOutput(i).getID()));
		}
		return listeId;
	}


	public Vector getIDActiviteSortie(int numrang, int numtype)
	{
		Vector listeId = new Vector();

		WorkProduct w = (WorkProduct)(this.produits.elementAt(numrang));
		int nb = w.getInputCount();
		for (int i = 0; i < nb; i++)
		{
			listeId.addElement(new Integer(w.getInput(i).getID()));
		}
		return listeId;
	}

	public Vector getIDActiviteEntree(int numrang, int numtype)
	{
		Vector listeId = new Vector();

		WorkProduct w = (WorkProduct)(this.produits.elementAt(numrang));
		int nb = w.getOutputCount();
		for (int i = 0; i < nb; i++)
		{
			listeId.addElement(new Integer(w.getOutput(i).getID()));
		}
		return listeId;
	}


	/**
	 * Renvoie la définition de travail dans laquelle se trouve l'activité courante
	 */
	public IdObjetModele getDefinitionTravail(int numrang, int numtype)
	{
		switch (numtype)
		{
			case ACTIVITE : Activity act = (Activity) this.activites.elementAt(numrang);
							for (int k = 0; k < this.listeDefinition.size(); k++)
							{
								ApesWorkDefinition apw = (ApesWorkDefinition)this.listeDefinition.elementAt(k);
								for (int i = 0; i < apw.subWorkCount(); i++ )
								{
									 Activity a = apw.getSubWork(i);
									 if ( a.equals(act) )
									 {
										 if (!this.mapId.containsKey(apw))
										 {
											 this.mapId.put(apw,new IdObjetModele(this,i,DEFINITION_TRAVAIL));
										 }
										 return (IdObjetModele) (this.mapId.get(apw));
									 }
								}
							}
		}
		return null;
	}

	/**
	 *
	 */
	public Vector getActivite(int numrang, int numtype)
	{
		Vector listeActivites = new Vector();
		switch (numtype)
		{
			case ROLE :				 	ProcessRole pr = (ProcessRole)(this.roles.elementAt(numrang));
										int nbactivites = pr.getFeatureCount();
										// parcourir toutes les activités renvoyées pour cherche l'idobjetmodele correspondant
										for (int i = 0; i < nbactivites; i++)
										{
											// activité courante
											//Activity a = (Activity) pr.getFeature(i);
											WorkDefinition a = pr.getFeature(i);
											// parcours des activités stockées
											for (int j = 0; j < this.activites.size(); j++ )
											{
												 Activity act = (Activity) this.activites.elementAt(j);
												 if ( a.equals(act) )
												 {
													if (!this.mapId.containsKey(act))
													{
														this.mapId.put(act,new IdObjetModele(this,j,ACTIVITE));
													}
													listeActivites.addElement(this.mapId.get(act));
												 }
											 }
										}
										break;

			case DEFINITION_TRAVAIL :   ApesWorkDefinition apw = (ApesWorkDefinition)this.listeDefinition.elementAt(numrang);
										for (int i = 0; i < apw.subWorkCount(); i++ )
										{
											 Activity a = apw.getSubWork(i);
											 // parcours des activités stockées
											 for (int j = 0; j < this.activites.size(); j++ )
											 {
												  Activity act = (Activity) this.activites.elementAt(j);
												  if ( a.equals(act) )
												  {
													 if (!this.mapId.containsKey(act))
													 {
														 this.mapId.put(act,new IdObjetModele(this,j,ACTIVITE));
													 }
													 listeActivites.addElement(this.mapId.get(act));
												  }
											  }
										}
										break;
		}
		return listeActivites;
	}

	public Vector getActiviteAssiste(int numrang, int numtype)
	{
		Vector listeActivites = new Vector();
		switch (numtype)
		{
			case ROLE :	ProcessRole pr = (ProcessRole)(this.roles.elementAt(numrang));
						int nbactivites = pr.getParticipationCount();
						// parcourir toutes les activités renvoyées pour cherche l'idobjetmodele correspondant
						for (int i = 0; i < nbactivites; i++)
						{
							// activité courante
							//Activity a = (Activity) pr.getFeature(i);
							WorkDefinition a = pr.getParticipation(i);
							// parcours des activités stockées
							for (int j = 0; j < this.activites.size(); j++ )
							{
								 Activity act = (Activity) this.activites.elementAt(j);
								 if ( a.equals(act) )
								 {
									if (!this.mapId.containsKey(act))
									{
									this.mapId.put(act,new IdObjetModele(this,j,ACTIVITE));
									}
									listeActivites.addElement(this.mapId.get(act));
								 }
							}
						}
						break;
		}
		return listeActivites;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Vector getProduit(int numrang, int numtype)
	{
		Vector listeProduits = new Vector();

		ProcessRole pr = (ProcessRole)(this.roles.elementAt(numrang));
		int nbproduits= pr.getResponsibilityCount();
		for (int i = 0; i < nbproduits; i++)
		{
			WorkProduct w = pr.getResponsibility(i);
			for (int j = 0; j < this.produits.size(); j++ )
			{
				WorkProduct o = (WorkProduct) this.produits.elementAt(j);
				if ( w.equals(o) )
				{
					if (!this.mapId.containsKey(o))
					{
						this.mapId.put(o,new IdObjetModele(this,j,PRODUIT));
					}
					listeProduits.addElement(this.mapId.get(o));
				}
			}
		}
		return listeProduits;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Vector getActiviteEntree(int numrang, int numtype)
	{
	    Vector listeActivites = new Vector();
	    WorkProduct w = (WorkProduct)(this.produits.elementAt(numrang));

	    if (numtype != 7)
	    {
		    // Trouver le bon workproduct
		    String nomProd;
		    boolean trouve = false;
		    if (numtype == 1)
		    {
		        nomProd = this.interfaceIn.elementAt(numrang).toString();
		    }
		    else
		    {
		        nomProd = this.interfaceOut.elementAt(numrang).toString();
		    }
		    for (int i = 0; i < this.produits.size() && !trouve; i++ )
		    {
		        if ( ((WorkProduct)this.produits.elementAt(i)).toString().equals(nomProd) )
		        {
		            trouve = true;
		            w = (WorkProduct)this.produits.elementAt(i);
		        }
		    }
		    if (!trouve)
		    {
		        System.err.println("ERR Match Interface <-> Prod");
		    }
	    }

		int nb = w.getOutputCount();
		for (int i = 0; i < nb; i++)
		{
			WorkDefinition a = w.getOutput(i);
			for (int j = 0; j < this.activites.size(); j++ )
			{
				 Activity act = (Activity) this.activites.elementAt(j);
				 if ( a.equals(act) )
				 {
					if (!this.mapId.containsKey(act))
					{
						this.mapId.put(act,new IdObjetModele(this,j,ACTIVITE));
					}
					listeActivites.addElement(this.mapId.get(act));
				 }
			 }
		}
		return listeActivites;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Vector getActiviteSortie(int numrang, int numtype)
	{
	    Vector listeActivites = new Vector();
	    WorkProduct w = (WorkProduct)(this.produits.elementAt(numrang));

	    if (numtype != 7)
	    {
		    // Trouver le bon workproduct
		    String nomProd;
		    boolean trouve = false;
		    if (numtype == 1)
		    {
		        nomProd = this.interfaceIn.elementAt(numrang).toString();
		    }
		    else
		    {
		        nomProd = this.interfaceOut.elementAt(numrang).toString();
		    }
		    for (int i = 0; i < this.produits.size() && !trouve; i++ )
		    {
		        if ( ((WorkProduct)this.produits.elementAt(i)).toString().equals(nomProd) )
		        {
		            trouve = true;
		            w = (WorkProduct)this.produits.elementAt(i);
		        }
		    }
		    if (!trouve)
		    {
		        System.err.println("ERR Match Interface <-> Prod");
		    }
	    }

		int nb = w.getInputCount();
		for (int i = 0; i < nb; i++)
		{
			WorkDefinition a = w.getInput(i);
			for (int j = 0; j < this.activites.size(); j++ )
			{
				 Activity act = (Activity) this.activites.elementAt(j);
				 if ( a.equals(act) )
				 {
					if (!this.mapId.containsKey(act))
					{
						this.mapId.put(act,new IdObjetModele(this,j,ACTIVITE));
					}
					listeActivites.addElement(this.mapId.get(act));
				 }
			 }
		}
		return listeActivites;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Vector getEtats (int numrang, int numtype)
	{
		Vector listeEtats = new Vector();
		// récupérer le produit
		WorkProduct w = (WorkProduct)(this.produits.elementAt(numrang));
		int nb = w.behaviorCount();

		for (int i = 0; i < nb; i++)
		{
			StateMachine etat = w.getBehavior(i);
			listeEtats.addElement(etat.getName());
		}
		return listeEtats;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Vector getProduitEntree(int numrang, int numtype)
	{
		Vector listeProduits = new Vector();

		Activity act = (Activity)(this.activites.elementAt(numrang));

		int nb = act.getInputCount();
		for (int i = 0; i < nb; i++)
		{
			WorkProduct w = act.getInput(i);
			for (int j = 0; j < this.produits.size(); j++ )
			{
				WorkProduct o = (WorkProduct) this.produits.elementAt(j);
				if ( w.equals(o) )
				{
					if (!this.mapId.containsKey(o))
					{
						this.mapId.put(o,new IdObjetModele(this,j,PRODUIT));
					}
					listeProduits.addElement(this.mapId.get(o));
				}
			}
		}
		return listeProduits;
	}


	public Vector getProduitSortie(int numrang, int numtype)
	{
		Vector listeProduits = new Vector();

		Activity act = (Activity)(this.activites.elementAt(numrang));

		int nb = act.getOutputCount();
		for (int i = 0; i < nb; i++)
		{
			WorkProduct w = act.getOutput(i);
			for (int j = 0; j < this.produits.size(); j++ )
			{
				WorkProduct o = (WorkProduct) this.produits.elementAt(j);
				if ( w.equals(o) )
				{
					if (!this.mapId.containsKey(o))
					{
						this.mapId.put(o,new IdObjetModele(this,j,PRODUIT));
					}
					listeProduits.addElement(this.mapId.get(o));
				}
			}
		}
		return listeProduits;
	}


	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public IdObjetModele getRoleResponsable(int numrang, int numtype)
	{
		// le role responsable
		ProcessPerformer pf = null;
		switch(numtype)
		{
			case ACTIVITE : Activity act = (Activity)(this.activites.elementAt(numrang));
							pf = act.getOwner();
							break;
			case PRODUIT : WorkProduct w = (WorkProduct)(this.produits.elementAt(numrang));
						   pf = w.getResponsible();
						   break;
		}
		if (pf != null )
		{
			for (int i = 0; i < this.roles.size(); i++ )
			{
				 Object o = this.roles.elementAt(i);
				 if (o.equals(pf))
				 {
					 if (!this.mapId.containsKey(o))
					 {
						 this.mapId.put(o,new IdObjetModele(this,i,ROLE));
					 }
					 return((IdObjetModele)this.mapId.get(o));
				 }
			}
		}
		return null;
	}
	
	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Vector getRoleAssistant(int numrang, int numtype)
	{
		// le role assistant
		//ProcessPerformer pf = null;
		Vector vAss = new Vector();
		switch(numtype)
		{
			case ACTIVITE : Activity act = (Activity)(this.activites.elementAt(numrang));
							for(int i=0;i<act.getAssistantCount(); i++) {
								ProcessRole pr = (ProcessRole) act.getAssistant(i);
								
								if (!this.mapId.containsKey(pr))
								{
									this.mapId.put(pr,new IdObjetModele(this,i,ROLE));
								}
								vAss.add((IdObjetModele)this.mapId.get(pr));
							}
							break;
		}
		return vAss;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Object getAdapteur(int numrang, int numtype)
	{
		SpemDiagram diag = (SpemDiagram) this.listeDiagramme.elementAt(numrang);
		SpemGraphAdapter mAdapter = (SpemGraphAdapter) this.mapDiagramme.get(diag);
		return mAdapter;
	}

	//--------------------------------------------------------------------------//
	// Implémentation de l'interface ObjetAnnulable + sauvegarde				//
	//--------------------------------------------------------------------------//

	/**
	 * Sauvegarde le nom du composant dans un Memento, de manière à pouvoir le restaurer
	 * plus tard.
	 * @return l'objet mémoire créé
	 */
	public Memento sauverNom ()
	{
		// Sauver le nom dans un memento et le renvoyer
		Memento mem = new Memento (this) ;
		mem.sauverEtat (this.ANN_NOM+this.ANN_SEP+this.getNomComposant()) ;
		return mem ;
	}

	/**
	 * Méthode restaurant l'objet dans son état précédent.
	 * Appelée depuis sa référence par la classe Memento.
	 * @see iepp.application.aedition.ObjetAnnulable#restaurer(java.lang.String)
	 */
	public void restaurer(String etat)
	{
		String code ;	// Code de l'action à annuler
		String param ;	// Paramètres
		int posSeparateur ;		// Position du sépareteur entre les deux
		boolean paramExiste ;	// Indique si un paramètre existe

		// Séparer les éléments
		posSeparateur = etat.indexOf (this.ANN_SEP) ;
		if (posSeparateur == -1)
			throw new RuntimeException (Application.getApplication().getTraduction("ERR_Ann_CP_separ")) ;
		code = etat.substring (0, posSeparateur) ;
		if (paramExiste = (posSeparateur >= etat.length()-1))
			param = etat.substring (posSeparateur+1) ;
		else
			param = "" ;

		// Annuler l'action indiquée par le code
		if (code.equals (this.ANN_NOM))
		{
			// Vérifier qu'on a un paramètre et annuler le renommage
			if (!paramExiste)
				throw new RuntimeException (Application.getApplication().getTraduction("ERR_Ann_CP_param")) ;
			this.setNomComposant (param) ;
		}
	}


	//-------------------------------------
	// Gestion des diagrammes
	//-------------------------------------
	/**
	 * @return
	 */
	public IdObjetModele getDiagrammeFlot()
	{
		// récupérer le diagramme
		return this.rechercherDiagramme(this.diagramme_flot_definition);
	}

	public Vector getListeDiagrammeResponsabilite()
	{
		Vector listeId = new Vector();
		for (int i = 0; i < this.listeDiagramme.size(); i++ )
		{
			Object o = this.listeDiagramme.elementAt(i);
			if (o instanceof ResponsabilityDiagram)
			{
				if (!this.mapId.containsKey(o))
				{
					this.mapId.put(o,new IdObjetModele(this,i,DIAGRAMME));
				}
				listeId.addElement((IdObjetModele)this.mapId.get(o));
			}
		}
		return listeId;
	}

	/**
	 * @return
	 */
	public IdObjetModele getDiagrammeContexte()
	{
		// récupérer le diagramme
		return this.rechercherDiagramme(this.diagramme_contexte);
	}

	/**
	 * @return
	 */
	public IdObjetModele getDiagrammeActivite(int ID_Apes)
	{
		for (int i = 0; i < this.listeDefinition.size(); i++)
		{
			ApesWorkDefinition apw = (ApesWorkDefinition)this.listeDefinition.elementAt(i);
			if (apw.getID() == ID_Apes)
			{
				//récupérer le diagramme
				Object o = apw.getActivityDiagram();
				return this.rechercherDiagramme(o);
			}
		}
		return null;
	}

	public IdObjetModele getDiagrammeFlotProduit(int ID_Apes)
	{
		for (int i = 0; i < this.listeDefinition.size(); i++)
		{
			ApesWorkDefinition apw = (ApesWorkDefinition)this.listeDefinition.elementAt(i);
			if (apw.getID() == ID_Apes)
			{
				//récupérer le diagramme
				Object o = apw.getFlowDiagram();
				return this.rechercherDiagramme(o);
			}
		}
		return null;
	}

	public IdObjetModele rechercherDiagramme(Object ob)
	{
		IdObjetModele id = null ;
		for (int i = 0; i < this.listeDiagramme.size(); i++ )
		{
			Object o = this.listeDiagramme.elementAt(i);
			if (o.equals(ob))
			{
				if (!this.mapId.containsKey(o))
				{
					id = new IdObjetModele(this,i,DIAGRAMME);
					this.mapId.put(o,id);
				}
				return((IdObjetModele)this.mapId.get(o));
			}
		}
		return null;
	}


	/**
	 * @param numRang
	 * @return
	 */
	public SpemGraphAdapter getAdapter(int numRang)
	{
		SpemDiagram diag = (SpemDiagram) this.listeDiagramme.elementAt(numRang);
		SpemGraphAdapter mAdapter = (SpemGraphAdapter) this.mapDiagramme.get(diag);
		return mAdapter;
	}


	/**
	 * @param apes
	 */
	public ElementPresentation getElementPresentation(int ID_Apes)
	{
		ElementPresentation elem = (ElementPresentation)this.mapApesPresent.get(new Integer(ID_Apes));
		return elem;
	}
}
