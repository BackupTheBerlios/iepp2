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
import java.io.Serializable;
import java.util.* ;

/**
 *
 */
public class IdObjetModele implements Serializable, Cloneable
{
	ObjetModele refObjet ;	// Référence sur l'objet à consulter pour demander le service
	int numRang = -1 ;		// Numéro désignant éventuellement un composant non accessible
							// mais géré par le composant référencé
	int numType = -1 ;		// numéro indiquant le type d'objet pointé par l'id
	private String chemin ;	// chemin de l'élément du modèle dans le site web

	public IdObjetModele (ObjetModele obj)
	{
		this.refObjet = obj ;
		this.chemin = "";
	}
	public IdObjetModele (ObjetModele obj, int numExterne, int numtype)
	{
		this.refObjet = obj ;
		this.numRang = numExterne ;
		this.numType = numtype ;
		this.chemin = "";
	}

	public ObjetModele getRef()
	{
		return this.refObjet ;
	}
	public int getNumRang()
	{
		return this.numRang ;
	}

	public int getNumType()
	{
		return this.numType;
	}

	public Object getAdapter()
	{
		if (this.estDiagramme())
		{
			return (((ComposantProcessus)this.getRef()).getAdapteur(this.numRang, this.numType));
		}
		return null;
	}

	public int getNbFils()
	{
		return this.refObjet.getNbFils(this.numRang, this.numType);
	}

	public IdObjetModele getFils(int ieme)
	{
		return this.refObjet.getFils(ieme,this.numRang, this.numType );
	}

	public String toString()
	{
		return this.refObjet.toString(this.numRang, this.numType);
	}

	public boolean estComposant()
	{
		return (this.refObjet.estComposant(this.numRang, this.numType));
	}

	public boolean estScenario()
	{
		return (this.refObjet.estScenario(this.numRang, this.numType));
	}

	public boolean estDefProc()
	{
		return (this.refObjet.estDefProc(this.numRang, this.numType));
	}

	public boolean estDefinitionTravail()
	{
		return (this.refObjet.estDefinitionTravail(this.numRang, this.numType));
	}
public void setVide(boolean b)//youssef
{
  ((ComposantProcessus)this.getRef()).setVide(b);
}
	public boolean estComposantVide()
	{
		return (((ComposantProcessus)this.getRef()).estVide());
	}
        public void setRenomme(boolean b)//youssef
        {
          ((ComposantProcessus)this.getRef()).setRenomme(b);
        }
                public boolean estRenomme()
                {
                        return (((ComposantProcessus)this.getRef()).estRenomme());
                }

	public boolean estProduit()
	{
		return (this.refObjet.estProduit(this.numRang, this.numType));
	}
	public boolean estProduitEntree()
	{
		return (this.refObjet.estProduitEntree(this.numRang, this.numType));
	}
	public boolean estProduitSortie()
	{
		return (this.refObjet.estProduitSortie(this.numRang, this.numType));
	}
	public boolean estInterfaceRequise()
	{
		return (this.refObjet.estInterfaceRequise(this.numRang, this.numType));
	}
	public boolean estInterfaceFournie()
	{
		return (this.refObjet.estInterfaceFournie(this.numRang, this.numType));
	}
	public boolean estPaquetage()
	{
		return (this.refObjet.estPaquetage(this.numRang, this.numType));
	}
	public boolean estActivite()
	{
		return (this.refObjet.estActivite(this.numRang, this.numType));
	}
	public boolean estDiagramme()
	{
		return (this.refObjet.estDiagramme(this.numRang, this.numType));
	}

	/**
	 * @param string
	 */
	public void setNomElement(String chaine)
	{
		this.refObjet.setNomElement(chaine, this.numRang, this.numType);
	}

	public Vector getActivite ()
	{
		if (this.estRole() || this.estDefinitionTravail())
		{
			return (((ComposantProcessus)this.getRef()).getActivite(this.numRang, this.numType));
		}
		if (! this.estComposant() && ! this.estPaquetage() )
		{
			return (new Vector());
		}
		return(((ComposantProcessus)this.getRef()).getActivites());
	}
	
	public Vector getActiviteAssiste ()
	{
		if (this.estRole() || this.estDefinitionTravail())
		{
			return (((ComposantProcessus)this.getRef()).getActiviteAssiste(this.numRang, this.numType));
		}
		if (! this.estComposant() && ! this.estPaquetage() )
		{
			return (new Vector());
		}
		return(((ComposantProcessus)this.getRef()).getActivites());
	}

	public Vector getRole ()
	{
		if (! this.estComposant() && ! this.estPaquetage())
		{
			return (new Vector());
		}
		return( ((ComposantProcessus)this.getRef()).getRoles());
	}

	public Vector getProduitEntree ()
	{
		if (this.estActivite())
		{
			return (((ComposantProcessus)this.getRef()).getProduitEntree(this.numRang, this.numType));
		}
		else if (! this.estComposant())
		{
			return (new Vector());
		}
		return( ((ComposantProcessus)this.getRef()).getProduitEntree());
	}

	public Vector getProduitSortie ()
	{
		if (this.estActivite())
		{
			return (((ComposantProcessus)this.getRef()).getProduitSortie(this.numRang, this.numType));
		}
		else if (! this.estComposant())
		{
			return (new Vector());
		}
		return( ((ComposantProcessus)this.getRef()).getProduitSortie());
	}

	/**
	 * @return
	 */
	public boolean estRole()
	{
		return (this.refObjet.estRole(this.numRang, this.numType));
	}

	/**
	 *
	 * @return
	 */

	public Vector getProduit ()
	{
		if (this.estRole())
		{
			return (((ComposantProcessus)this.getRef()).getProduit(this.numRang, this.numType));
		}
		if (! this.estComposant())
		{
			return (new Vector());
		}
		return( ((ComposantProcessus)this.getRef()).getProduits());
	}

	/**
	 * @return
	 */
	public Vector getActiviteEntree()
	{
		if (!this.estProduit())
		{
			return new Vector();
		}
		return (((ComposantProcessus)this.getRef()).getActiviteEntree(this.numRang, this.numType));
	}

	/**
	 * @return
	 */
	public Vector getActiviteSortie()
	{
		if (!this.estProduit())
		{
			return new Vector();
		}
		return (((ComposantProcessus)this.getRef()).getActiviteSortie(this.numRang, this.numType));
	}

	/**
	 * @return
	 */
	public Vector getDiagramme()
	{
		if (! this.estComposant())
		{
			return (new Vector());
		}
		return( ((ComposantProcessus)this.getRef()).getDiagrammes());
	}

	public Vector getEtats()
	{
		if (! this.estProduit())
		{
			return (new Vector());
		}
		return( ((ComposantProcessus)this.getRef()).getEtats(this.numRang, this.numType));
	}

	public int getID()
	{
		if (this.estDefProc())
		{
			return (0);
		}
		return (((ComposantProcessus)this.getRef()).getID(this.numRang, this.numType));
	}

	public Vector getIDActivite()
	{
		if (! this.estRole() && ! this.estDefinitionTravail())
		{
			return (new Vector());
		}
		return (((ComposantProcessus)this.getRef()).getIDActivite(this.numRang, this.numType));
	}

	public Vector getIDProduit()
	{
		if (! this.estRole())
		{
			return (new Vector());
		}
		return (((ComposantProcessus)this.getRef()).getIDProduit(this.numRang, this.numType));
	}

	public String getChemin()
	{
		return this.chemin;
	}

	public void setChemin(String chemin)
	{
		this.chemin = chemin;
	}

	public int getIDRole()
	{
		if (! this.estActivite() &&  !this.estProduit())
		{
			return (-1);
		}
		return (((ComposantProcessus)this.getRef()).getIDRole(this.numRang, this.numType));
	}

	public Vector getIDProduitEntree()
	{
		if (! this.estActivite())
		{
			return new Vector();
		}
		return (((ComposantProcessus)this.getRef()).getIDProduitEntree(this.numRang, this.numType));
	}

	public Vector getIDProduitSortie()
	{
		if (! this.estActivite())
		{
			return new Vector();
		}
		return (((ComposantProcessus)this.getRef()).getIDProduitSortie(this.numRang, this.numType));
	}

	public Vector getIDActiviteSortie()
	{
		if (! this.estProduit())
		{
			return new Vector();
		}
		return (((ComposantProcessus)this.getRef()).getIDActiviteSortie(this.numRang, this.numType));
	}

	public Vector getIDActiviteEntree()
	{
		if (! this.estProduit())
		{
			return new Vector();
		}
		return (((ComposantProcessus)this.getRef()).getIDActiviteEntree(this.numRang, this.numType));
	}

	public Vector getDefinitionTravail()
	{
		if (! this.estComposant() && ! this.estPaquetage())
		{
			return (new Vector());
		}
		return(((ComposantProcessus)this.getRef()).getDefinitionTravail());
	}

	public PaquetagePresentation getPaquetagePresentation()
	{
		if (! this.estComposant())
		{
			return null;
		}
		return(((ComposantProcessus)this.getRef()).getPaquetage());
	}


	public IdObjetModele getRoleResponsable()
	{
		if (this.estActivite() || this.estProduit())
		{
			return (((ComposantProcessus)this.getRef()).getRoleResponsable(this.numRang, this.numType));
		}
		return null;
	}

	public Vector getRoleAssistant()
	{
		if (this.estActivite() || this.estProduit())
		{
			return (((ComposantProcessus)this.getRef()).getRoleAssistant(this.numRang, this.numType));
		}
		return null;
	}

	public IdObjetModele getDiagrammeFlot()
	{
		if (this.estComposant())
		{
			return (((ComposantProcessus)this.getRef()).getDiagrammeFlot());
		}
		return null;
	}
	/**
	 * @return
	 */
	public IdObjetModele getDiagrammeContexte()
	{
		if (this.estComposant())
		{
			return (((ComposantProcessus)this.getRef()).getDiagrammeContexte());
		}
		return null;
	}

	public Vector getListeDiagrammeResponsabilite()
	{
		if (this.estComposant())
		{
			return (((ComposantProcessus)this.getRef()).getListeDiagrammeResponsabilite());
		}
		return new Vector();
	}

	/**
	 * Decremente le rang de cet objet. Utilise lors de suppressions d'objets precedents
	 */
	public void decrementerRang()
	{
		this.numRang--;
	}

	/**
	 * @return
	 */
	public IdObjetModele getLaDefinitionTravail()
	{
		if (this.estActivite())
		{
			return (((ComposantProcessus)this.getRef()).getDefinitionTravail(this.numRang, this.numType));
		}
		return null;
	}

}
