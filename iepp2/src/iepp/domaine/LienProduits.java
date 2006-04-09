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

import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.io.Serializable;

/**
 * Classe permettant de lier deux produits de deux composants différents
 * Il n'y a pas de setter car modifier un lien ne veut rien dire
 * il faudra d'abord supprimer un lien pour en recréer un nouveau
 */
public class LienProduits implements Serializable
{
	private IdObjetModele produitSortie ;
	private IdObjetModele produitEntree ;
	private LienEdge lien;
	
	/**
	 * Création d'un lien au niveau du modèle entre deux produits désignés
	 * par leur identifiant
	 * @param sortie, produit en sortie d'un composant
	 * @param destination, produit en entrée d'un autre composant
	 */
	public LienProduits(IdObjetModele sortie, IdObjetModele entree, LienEdge lien)
	{
		this.produitSortie = sortie ;
		this.produitEntree = entree ;
		this.lien = lien;
	}
	
	/**
	 * Renvoie le produit en sortie d'un composant
	 * @return identifiant d'un produit
	 */
	public IdObjetModele getProduitSortie()
	{
		return this.produitSortie;
	}
	
	/**
	 * Renvoie le produit en entrée d'un composant
	 * @return identifiant d'un produit
	 */
	public IdObjetModele getProduitEntree()
	{
		return this.produitEntree;
	}
	
	public LienEdge getLienFusion()
	{
		return this.lien;
	}
	
	/**
	 * Indique si le produit passé en parametre est contenu dans le lien
	 * @param le produit qu'il faut tester
	 * @return true si le produit est concerné par le lien
	 */
	public boolean contient(IdObjetModele produit)
	{
		return (this.produitEntree == produit || this.produitSortie == produit);
	}
	
	public String toString()
	{
		return "( Entree : " + this.produitEntree + ", Sortie : " + this.produitSortie + " )";
	}

}
