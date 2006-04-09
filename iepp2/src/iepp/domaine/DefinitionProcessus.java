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

import java.util.*;

import iepp.*;


/**
 *
 */
public class DefinitionProcessus extends ObjetModele
{
	/**
	 * Auteur du processus
	 */
	private String auteur ="";

	/**
	 * Commentaires associés au processus
	 */
	private String commentaires ="";

        //modif 2XMI Sébastien
        //ajout Note pour les bas de pages du site Web(copyright)
        /**
         * Notes du Bas de page
         */
        private String piedPage ="";
        //fin modif


	/**
	 * Nom de la définition de processus
	 */
	private String nomDefinition ="";

	/**
	 * Liste des composants qui composent le processus
	 */
	private Vector listeComp ;

	/**
	 * Repertoire de destination du site web généré
	 */
	private String repertoireGeneration = "";

	/**
	 * ID de la définition de processus courante
	 */
	private IdObjetModele idDefProc ;

	/**
	 * E mail de l'auteur
	 */
	private String emailAuteur ="webmaster@web.fr";

	/**
	 * Fichier de contenu pour décrire la def proc
	 */
	private String ficContenu = "";

	/**
	 * Liste des éléments à générer
	 */
	private Vector listeAGenerer;

        /**
         * Liste des Roles et SuperRoles associes
         */
        public HashMap listeAssociationSRole;



	public DefinitionProcessus ()
	{
		this.listeComp = new Vector() ;
		this.listeAGenerer = new Vector();
		this.nomDefinition = Application.getApplication().getTraduction("nomDefinitionDefaut");
		this.idDefProc = new IdObjetModele(this);
		this.repertoireGeneration = new String(Application.getApplication().getConfigPropriete("repertoire_generation"));
		this.listeAssociationSRole=new HashMap();
        }

	public void ajouterComposant (ComposantProcessus comp)
	{
		if (comp != null)
		{
			this.listeComp.add(comp.getIdComposant()) ;
			
			// indiquer à tous les observeurs que la définition a change
			this.maj("COMPOSANT_INSERTED");
			// l'ajouter dans la liste à generer
			this.listeAGenerer.add(comp.getIdComposant());
		}
	}

	public void ajouterComposantChargement (ComposantProcessus comp)
	{
		if (comp != null)
		{
			this.listeComp.add(comp.getIdComposant()) ;
			
			// indiquer à tous les observeurs que la définition a change
			this.maj("COMPOSANT_INSERTED");
		}
	}
	
	public void retirerComposantTous()
	{
		if (listeComp != null)
		{
			this.listeComp.removeAllElements();
			this.listeAGenerer.removeAllElements();
		}
	}

	public void retirerComposant(IdObjetModele compo)

	{
		if (listeComp != null)
		{
			this.maj("COMPOSANT_REMOVED");
			this.listeComp.removeElement(compo);
			this.listeAGenerer.removeElement(compo);
		}
	}

//rajouté par youssef

public void renommerComposant(IdObjetModele compo,String nom)
{
 ((IdObjetModele)(listeComp.get(listeComp.indexOf(compo)))).setNomElement(nom);

 ((IdObjetModele)(listeAGenerer.get(listeAGenerer.indexOf(compo)))).setNomElement(nom);

}
  /**
	 * @param paquet
	 */
	public void retirerPresentation(PaquetagePresentation paquet)
	{
		if (listeAGenerer != null)
		{
			this.listeAGenerer.removeElement(paquet);
			this.maj("COMPOSANT_REMOVED");
		}
	}

	//------------------------------------------------------------------//
	// Getters et setters												//
	//------------------------------------------------------------------//

	public String getNomDefProc()
	{
                return this.nomDefinition;
	}
	public void setNomDefProc(String nom)
	{
		if (! this.nomDefinition.equals(nom))
		{
			this.nomDefinition = nom ;
			// indiquer à tous les observeurs que la définition a change
			this.maj("CHANGED");
		}
	}
	public void setFicContenu(String fichier)
	{
		if (! this.ficContenu.equals(fichier))
		{
			this.ficContenu = fichier;
			this.maj("CHANGED");
		}
	}
	public String getFichierContenu()
	{
		return this.ficContenu;
	}
	public String getAuteur()
	{
		return this.auteur;
	}
	public String getCommentaires()
	{
		return this.commentaires;
	}
        public String getPiedPage()
        {
                return this.piedPage;
        }

	public void setAuteur(String auteur)
	{
		if (! this.auteur.equals(auteur))
		{
			this.auteur = auteur;
			this.maj("CHANGED");
		}
	}
	public void setCommentaires(String string)
	{
		if (! this.commentaires.equals(string))
		{
			this.commentaires = string;
			this.maj("CHANGED");
		}
	}
        public void setPiedPage(String string)
        {
            if (!this.piedPage.equals(string))
            {
                this.piedPage = string;
                this.maj("CHANGED");
            }
        }
	public IdObjetModele getIdDefProc()
	{
		return this.idDefProc;
	}
	public Vector getListeComp()
	{
		return this.listeComp ;
	}
	public Vector getListeAGenerer()
	{
		return this.listeAGenerer;
	}
        public void setListeAGenerer(Vector liste)
        {
                if (! this.listeAGenerer.equals(liste))
                {
                        this.listeAGenerer = liste;
                        this.maj("CHANGED");
                }
        }


        public HashMap getListeAssociationSRole()
        {
          if (this.listeAssociationSRole == null)
            this.creerListeAssociationSRole();
          return this.listeAssociationSRole;
        }

        public void setListeAssociationSRole(HashMap hm)
        {
          if(!this.listeAssociationSRole.equals(hm))
          {
            this.listeAssociationSRole=(HashMap)hm.clone();
            this.maj("CHANGED");
          }
        }




        //modif 2XMI jean
        /**
         * permet de retourner un paquetage de presentation de la listeAGenerer
         * ayant comme nom le parametre
         * @param _nom
         * @return le paquetage en question s'il est trouve
         * @return null s'il n'est pas trouve
         */
        public PaquetagePresentation getPaquetageListeAGenerer(String _name) {
          if (listeAGenerer != null) {
            for (int i = 0; i < listeAGenerer.size(); i++) {
              if (listeAGenerer.elementAt(i) instanceof PaquetagePresentation) {
                //on a trouve le premier PaquetagePresentation _nom,
                //on le retourne
                PaquetagePresentation pp = (PaquetagePresentation) listeAGenerer.elementAt(i);
                if (pp.getNomPresentation().equals(_name))
                  return pp;
              }
            }
          }
          return null;
        }

	public String getRepertoireGeneration()
	{
		if ( this.repertoireGeneration.equals(""))
		{
			return ("iepp/");
		}
		else
		{
			return this.repertoireGeneration;
		}
	}
	public void setRepertoireGeneration(String string)
	{
		if (! this.repertoireGeneration.equals(string))
		{
			this.repertoireGeneration = string;
			this.maj("CHANGED");
		}
	}

        //modif 2XMI Amandine
        public void setRepertoireGenerationNotChanged(String string)
        {
                if (! this.repertoireGeneration.equals(string))
                {
                        this.repertoireGeneration = string;
                }
        }
        //fin modif 2XMI Amandine

	public String toString()
	{
		return this.nomDefinition;
	}
	public void maj(String code)
	{
		this.setChanged();
		this.notifyObservers(code);
	}
	public String getEmailAuteur() {
		return emailAuteur;
	}
	public void setEmailAuteur(String string)
	{
		if (! this.emailAuteur.equals(string))
		{
			this.emailAuteur = string;
			this.maj("CHANGED");
		}
	}

	//--------------------------------------------------------------------------//
	// Implementation de l'interface ObjetModele								//
	//--------------------------------------------------------------------------//

	public String toString(int numrang, int numtype)
	{
		return this.nomDefinition;
	}

	public boolean estDefProc(int numrang, int numtype)
	{
		return true ;
	}

	/* (non-Javadoc)
	 * @see iepp.domaine.ObjetModele#getNbFils(int)
	 */
	public int getNbFils(int numrang, int numtype)
	{
		return this.listeComp.size();
	}

	/* (non-Javadoc)
	 * @see iepp.domaine.ObjetModele#getFils(int, int, int)
	 */
	public IdObjetModele getFils(int ieme, int numrang, int numtype)
	{
		return ((IdObjetModele)this.listeComp.elementAt(ieme)) ;
	}

	/* (non-Javadoc)
	 * @see iepp.domaine.ObjetModele#setNomElement(java.lang.String, int, int)
	 */
	public void setNomElement(String chaine, int i, int j)
	{
		if (! this.nomDefinition.equals(chaine))
		{
			this.nomDefinition = chaine ;
			this.maj("CHANGED");
		}
	}

    /**
     * creerPiedPage
     *
     * Méthode permettant de creer un pied de page pour les "anciens" processus qui n'en contiennent pas
     */
    public void creerPiedPageVide()
    {
        this.piedPage="";
    }

    /**
     * creerListeAssociationSRole
     *
     * Méthode permettant de creer la listeAssociationSRole pour les "anciens" processus qui n'en contiennent pas
     */
    public void creerListeAssociationSRole()
    {
        this.listeAssociationSRole=new HashMap();
    }

    /**
     * removeListeAssociationSRole
     *
     * Méthode permettant de supprimer un composant de la listeAssociationSRole
     * c'est a dire supprimer tous les roles associes
     */
    public void removeListeAssociationSRole(IdObjetModele composant)
    {
      if(composant!=null)
      {
        if(composant.estComposant())
        {
          Vector roles=composant.getRole();
          for(int i=0; i<roles.size(); i++)
          {
            IdObjetModele role=(IdObjetModele)roles.elementAt(i);
            if(listeAssociationSRole.containsKey(role))
            {
              //ce role est un sous role d'un super role
              //il faut supprimer cette association
              listeAssociationSRole.remove(role);
            }else if (listeAssociationSRole.containsValue(role)){
              //ce role est un super role
              //il faut supprimer toutes les associations sous-role/super-role
              //dont role est le super-role
              ArrayList cleSousRole = getListeSousRole(role);
              Iterator iterateurSousRole=cleSousRole.iterator();
              while(iterateurSousRole.hasNext())
              {
                listeAssociationSRole.remove((IdObjetModele)iterateurSousRole.next());
              }
            }
          }
        }
      }
    }

    /**
     * containsSuperRole
     *
     * Méthode permettant d'indiquer si un role est un super role
     * dans la definition de processus
     */
    public boolean containsSuperRole(IdObjetModele role)
    {
      if (this.listeAssociationSRole == null)
        return false;
      else
        return (this.listeAssociationSRole.containsValue(role));
    }

    /**
     * getListeSousRole
     *
     * Méthode permettant d'obtenir tous les sous roles d'un super role
     */
    public ArrayList getListeSousRole(IdObjetModele role)
    {
      ArrayList cleSousRole = new ArrayList();
      if (this.listeAssociationSRole == null)
        return cleSousRole;
      else{
        Set entrees=listeAssociationSRole.entrySet();
        Iterator iterateurEntree=entrees.iterator();
        while(iterateurEntree.hasNext())
        {
          Map.Entry entree=(Map.Entry)iterateurEntree.next();
          if(role==(IdObjetModele)entree.getValue())
          {
            //on garde le sous-role qui etait sous ce super role
            cleSousRole.add(entree.getKey());
          }
        }
        return cleSousRole;
      }
    }


    /**
     * containsSuperRole
     *
     * Méthode permettant d'indiquer si un role est un sous role
     * dans la definition de processus
     */
    public boolean containsSousRole(IdObjetModele role)
    {
      if (this.listeAssociationSRole == null)
        return false;
      else
        return (this.listeAssociationSRole.containsKey(role));
    }

    /**
     * getSuperRole
     *
     * Méthode permettant d'obtenir le super role d'un sous role
     */
    public IdObjetModele getSuperRole(IdObjetModele role)
    {
      if (this.listeAssociationSRole == null)
        return null;
      else
        return ((IdObjetModele)this.listeAssociationSRole.get(role));
    }

    /**
     * getComposantNameFromRole
     *
     * Méthode permettant d'obtenir le nom du composant d'un role
     */

    public String getComposantNameFromRole(IdObjetModele role)
    {
      Vector listeComposant=Application.getApplication().getProjet().getDefProc().getListeComp();
      for(int i=0; i<listeComposant.size(); i++)
      {
        IdObjetModele idObjetModelComp=(IdObjetModele)listeComposant.elementAt(i);
        Vector roles=idObjetModelComp.getRole();
        for(int j=0; j<roles.size(); j++)
        {
          IdObjetModele idObjetModelRole=(IdObjetModele)roles.elementAt(j);
          if(idObjetModelRole==role)
          {
            ComposantProcessus courant=(ComposantProcessus)(idObjetModelComp).getRef();
            return courant.getElementPresentation(idObjetModelComp.getID()).getNomPresentation();
          }
        }
      }
      return null;
    }

}
