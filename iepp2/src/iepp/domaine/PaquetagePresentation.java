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
import java.util.Collections;
import java.util.Vector;

/**
 *
 */
public class PaquetagePresentation implements Serializable
{

	private Vector listeElement;
	private String nomPresentation ;
	private String chemin_contenu ;
	private String chemin_icone ;
	private String nomFichier;

        private String auteur;
        private String mail;
        private String version;
        private String lastExport;


	public PaquetagePresentation()
	{
		this.listeElement = new Vector();
		this.nomFichier = "";
	}

	public PaquetagePresentation(String nomFichier)
	{
		this.listeElement = new Vector();
		this.nomFichier = nomFichier ;
	}

	public void setNomPresentation(String nom)
	{
		this.nomPresentation = nom ;
	}

	public void setCheminContenu(String chemin)
	{
		this.chemin_contenu = chemin;
	}

	public void setCheminIcone(String chemin)
	{
		this.chemin_icone = chemin;
	}

	public void ajouterElement(ElementPresentation e)
	{
		this.listeElement.addElement(e);
	}

	public String toString()
	{
		return (this.nomPresentation);
	}

	/**
	 *
	 */
	public void trierElement()
	{
		Collections.sort(this.listeElement);
	}
	/**
	 * @return
	 */
	public String getChemin_contenu() {
		return chemin_contenu;
	}

	/**
	 * @return
	 */
	public String getChemin_icone() {
		return chemin_icone;
	}

	/**
	 * @return
	 */
	public Vector getListeElement() {
		return listeElement;
	}

	/**
	 * @return
	 */
	public String getNomPresentation() {
		return nomPresentation;
	}

	/**
	 * @return
	 */
	public String getNomFichier() {
		return nomFichier;
	}

	/**
	 * @param string
	 */
	public void setNomFichier(String string) {
		nomFichier = string;
	}

        /**
         * @return
         */
        public String getAuteur() {
                return auteur;
        }

        /**
         * @param string
         */
        public void setAuteur(String string) {
                auteur = string;
        }

        /**
         * @return
         */
        public String getMail() {
                return mail;
        }

        /**
         * @param string
         */
        public void setMail(String string) {
                mail = string;
        }

        /**
         * @return
         */
        public String getVersion()  {
                return version;
        }

        /**
         * @param string
         */
        public void setVersion(String string) {
                version = string;
        }

        /**
         * @return
         */
        public String getLastExport() {
                return lastExport;
        }

        /**
         * @param string
         */
        public void setLastExport(String string) {
                lastExport = string;
        }
}
