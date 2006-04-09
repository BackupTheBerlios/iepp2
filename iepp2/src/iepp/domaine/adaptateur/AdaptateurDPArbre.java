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

package iepp.domaine.adaptateur;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.*;

import iepp.Application;
import iepp.domaine.*;

/**
 * Classe adapteur permettant de faire l'intermédiaire entre la vue (arbre) et les données à afficher
 */
public class AdaptateurDPArbre implements TreeModel, Observer
{

	/**
	 * Lien vers les données à mette à disposition de la vue
	 */
	private DefinitionProcessus defProc ;
	
	/**
	 * Liste des ecouteurs de l'adaptateur, notamment la vue
	 */
	private EventListenerList ecouteursModele ;
	

	
	/**
	 * Construction d'un adaptateur permettant d'accéder aux données du domaine
	 * @param defProc, définition de processus à afficher dans l'arbre
	 */
	public AdaptateurDPArbre( DefinitionProcessus defProc )
	{
		// récupérer le processus à afficher
		this.defProc = defProc ;
		// indiquer à la définition que l'adapteur ecoute les éventuelles modifications des données
		this.defProc.addObserver(this);
		// créer une nouvelle liste d'écouteurs (l'arbre par exemple) de l'adapteur
		this.ecouteursModele = new EventListenerList();
	}


	//------------------------------------------------------------------
	//						Implemente TreeModel
	//------------------------------------------------------------------
	
	/**
	 * Renvoie la racine de l'arbre, l'id de l'objet modele Definition de Processus
	 * @return l'Id de la définition de processus
	 */
	public Object getRoot()
	{
		return this.defProc.getIdDefProc();
	}

	/**
	 * Renvoie le nombre de fils de l'objet courant obj
	 */
	public int getChildCount(Object obj)
	{
		// renvoyer le nombre de fils qui ont pour parent obj
		return ((IdObjetModele)obj).getNbFils();
	}

	/**
	 * Indique si l'objet courant obj est une feuille de l'arbre ou non
	 */
	public boolean isLeaf(Object obj)
	{
		return (((IdObjetModele)obj).getNbFils() == 0 );
	}

	/**
	 * Méthode appelée par les écouteurs de l'adapteur pour s'enregistrer auprès de lui
	 * @param ecouteur, ecouteur de l'adapteur (donc du modèle)
	 */
	public void addTreeModelListener(TreeModelListener ecouteur)
	{
		this.ecouteursModele.add(TreeModelListener.class, ecouteur);

	}

	/**
	 * Méthode permettant de supprimer un écouteur dans la liste des 
	 * écouteurs de l'adapteur
	 * @param ecouteur, ecouteur de l'adapteur à supprimer
	 */
	public void removeTreeModelListener(TreeModelListener ecouteur)
	{
		this.ecouteursModele.remove(TreeModelListener.class, ecouteur);
	}

	/**
	 * Renvoie le ieme fils d'un objet parent
	 * @param parent, objet dont on recherche un fils
	 * @param ieme, numéro du fils que l'on recherche
	 * @return l'Id du fils recherché
	 */
	public Object getChild(Object parent, int ieme)
	{
		return ((IdObjetModele)parent).getFils(ieme);
	}

	/**
	 * Renvoie l'indice auquel se trouve l'enfant d'un parent donné
	 * @param parent, objet dont on recherche l'indice du fils
	 * @param fils, objet dont on recherche l'indice
	 * @return l'indice du fils recherché parmis l'ensemble des fils
	 */
	public int getIndexOfChild(Object parent, Object enfant)
	{
		// si le parent est une définition de processus
		if (((IdObjetModele)parent).estDefProc())
		{
			// on renvoie l'indice du composant enfant dans la liste des composants
			return (this.defProc.getListeComp().indexOf(enfant));
		}
		// sinon l'indice est directement trouvé dans l'IdobjetModele
		return ((IdObjetModele)parent).getNumRang();
	}

	/**
	 * Méthode appelée lorsque l'utilisateur à modifier la valeur d'un item identifié
	 * par path pour une nouvelle valeur newValue, si la nouvelle valeur est valide,
	 *  on prend en compte la modification
	 */
	public void valueForPathChanged(TreePath path, Object valeur)
	{
		
	}
	
	//------------------------------------------------------------------
	//						Implemente Observer
	//------------------------------------------------------------------
	
	/**
	 * Méthode appelée par le modèle lorsque celui-ci a été modifié
	 * @param observe, objet modifié ayant appelé la méthode
	 * @param param, objet passé en paramètre
	 */
	public void update(Observable observe, Object param)
	{
		// Peu elegant, peu performant, mais beaucoup plus simple
		// que les treemodelevent, etc...
		Application.getApplication().getFenetrePrincipale().getVueDPArbre().updateUI();
	}
}
