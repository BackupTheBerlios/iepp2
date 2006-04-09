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

import org.ipsquad.apes.model.spem.process.structure.WorkProduct;

import iepp.Projet;

/**
 *
 */
public class Produit implements Serializable
{

	private String nom ;

	
	/**
	 * @return
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	public Produit()
	{
		this.nom = Projet.getNouveauNom();
	}
	
	public Produit(String nom)
	{
		this.nom = nom;
	}
	
	public String toString()
	{
		return this.nom;
	}
	
}
