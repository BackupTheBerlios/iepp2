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
import java.util.Vector;
import java.util.Collection;

/**
 *
 */
public class ElementPresentation implements Comparable, Serializable
{
	private int ID_Apes ;
	private String ID_interne ;
	private String nomPresentation ;
	private String nomIcone ;
	private String contenu ;
	private String description ;
        //amandine
        private String cheminPage;
        private String typeProduit ;
	private IdObjetModele elementModele = null;

	private Vector listeGuide ;


	public ElementPresentation()
	{
		// par défaut l'élément de présentation n'est pas relié à un élément du modèle
		this.ID_Apes = -1 ;
		this.listeGuide = new Vector();
	}

	public void setNomPresentation(String nom)
	{
		this.nomPresentation = nom;
	}

	public void setIdExterne(int id)
	{
		this.ID_Apes = id ;
	}

	public void setIdInterne(String valeur)
	{
		this.ID_interne = valeur ;
	}

	public void setIcone(String valeur)
	{
		this.nomIcone = valeur ;
	}

	public void setContenu(String contenu)
	{
		this.contenu = contenu ;
	}

	public void ajouterGuide(Guide g)
	{
		this.listeGuide.addElement(g);
	}

	public String toString()
	{
		return (this.nomPresentation + " : " + this.ID_interne);
	}

	public void setElementModele(IdObjetModele id)
	{
		//System.out.println("J'associe : " + id + " à " + this.toString());
		this.elementModele = id;
		id.setNomElement(this.nomPresentation);
	}
	
	/**
	 *
	 */
	public int compareTo(Object elem)
	{
		if (elem instanceof ElementPresentation)
		{
			ElementPresentation e = (ElementPresentation)elem;
			String[] tab1 = this.getTableauID();
			String[] tab2 = e.getTableauID();

			int i = 0;
			while (i < Math.min(tab1.length,tab2.length))
			{
				if(!tab2[i].equals("")) {
					if ( new Integer(tab1[i]).intValue() < new Integer(tab2[i]).intValue() )
					{
						return -1;
					}
					else if ( new Integer(tab1[i]).intValue() > new Integer(tab2[i]).intValue() )
					{
						return 1;
					}
				}
				i++;
			}
			if (tab1.length < tab2.length) return -1;
			if (tab1.length > tab2.length) return 1 ;
			return 0;
		}
		return -1 ;
	}

	/**
	 * @return
	 */
	public String[] getTableauID()
	{
		return (this.ID_interne.split("-"));
	}

	public int getNiveau()
	{
		return this.ID_interne.split("-").length;
	}

	/**
	 * Renvoie sous forme de chaine de caractères le niveau supérieur
	 * Par exemple si le niveau courant (calculé selon l'id interne) est 1-1-2,
	 * le niveau supérieur est 1-1
	 * Ce niveau est utilisé pour récupérer dans la map des liste des dossiers
	 * l'id du dossier du niveau supérieur
	 * @return le niveau supérieur de l'élément courant
	 */
	public String getNiveauSuperieur()
	{
		String[] id = this.getTableauID();
		String retour = "";
		int i;

		if (this.getNiveau() == 1) return "";
		if (this.getNiveau() == 2) return id[0];
		for (i = 0 ; i < this.getNiveau() - 2; i++)
		{
			retour += (id[i] + "-");
		}
		retour += id[i];
		return retour;
	}

	/**
	 * @return
	 */
	public int getID_Apes()
	{
		return ID_Apes;
	}

	/**
	 * @return
	 */
	public String getID_interne() {
		return ID_interne;
	}

	/**
	 * @param string
	 */
	public void setID_interne(String string) {
		ID_interne = string;
	}

	/**
	 * @return
	 */
	public String getNomPresentation()
	{
		return nomPresentation;
	}

	/**
	 * @return
	 */
	public String getNomIcone() {
		return nomIcone;
	}


	/**
	 * @return
	 */
	public String getContenu() {
		return contenu;
	}

        //modif 2XMI sebastien
	/**
	 * @return
	 */
	public String getDescription() {
            String retour="";
            if(description != null)
		retour=this.description;
            return retour;
	}

	/**
	 * @param string
	 */
	public void setDescription(String string) {
		description = string;
	}

	/**
	 * @return
	 */
	public IdObjetModele getElementModele() {
		return elementModele;
	}

        //modif 2XMI amandine
        /**
         * @return
         */
        public String getTypeProduit() {
                return typeProduit;
        }

        //renvoie le chemin de la page dans le cas ou
        //l'element de presentation n'est pas associe a un elementModele
        public String getCheminPage(){
          return this.cheminPage;
        }
        /**
         * @param string
         */
        public void setTypeProduit(String _typeProduit) {
                this.typeProduit = _typeProduit;
        }

        public void setCheminPage(String _path){
          this.cheminPage = _path;
        }
        //fin modif 2XMI amandine

	// modif 2XMI chaouk

        public Vector getGuides ()
        {
          return this.listeGuide;
        }
        // fin modif 2XMI chaouk
}
