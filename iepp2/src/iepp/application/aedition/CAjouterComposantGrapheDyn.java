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

package iepp.application.aedition;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;

import java.awt.Point;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;

import util.ErrorManager;

/**
 * Commande annulable permettant d'ajouter un composant avec son interface
 * au diagramme dynamique. Vérifie si le composant n'est pas déjà dans le diagramme
 */
public class CAjouterComposantGrapheDyn extends CommandeAnnulable
{
	/**
	 * Id du Composant à rajouter dans le graphe
	 */
	private IdObjetModele composant ;

	/**
	 * Endroit où doit être ajouté le composant
	 */
	private Point point = new Point(200, 0);

	/**
	 * Les attributs pour l'affichage des composants
	 */
	private Map AllAttrubiteCell = GraphConstants.createMap();
	
	/**
	 * Création de la commande à partir du composant à ajouter
	 * @param comp
	 */
	public CAjouterComposantGrapheDyn (IdObjetModele comp)
	{
		// sauvegarder le composant
		this.composant = comp ;
		this.AllAttrubiteCell = GraphConstants.createMap();
	}

	/**
	 * Création de la commande à partir du composant à ajouter
	 * @param comp
	 */
	public CAjouterComposantGrapheDyn (IdObjetModele comp, Point endroitClick)
	{
		// sauvegarder le composant
		this.composant = comp ;
		this.point = endroitClick ;
		this.AllAttrubiteCell = GraphConstants.createMap();
	}

	/**
	 * Exécuter la commande, ajouter au diagramme le composant
	 * et les interfaces en entrée et sortie
	 * @return true si la commande s'est bien exécutée
	 */
	public boolean executer()
	{

		FenetreEdition fenetre = Application.getApplication().getProjet().getFenetreEdition() ;
		
		// déselectionner tous les éléments
		fenetre.getVueDPGraphe().clearSelection();
		fenetre.getVueDPGraphe().setSelectionCells(null);

		// vérifier que le composant n'est pas déjà présent dans le diagramme
		
		if (fenetre.getVueDPGraphe().contient(this.composant) != null)
		{
			ErrorManager.getInstance().display("ERR","ERR_Composant_Present");
			return false;

		}else{

			// Construire la vue associéé au composant
			ComposantCellDyn composantCell = new ComposantCellDyn(this.composant,(int)this.point.getX(),(int)this.point.getY());
			
			fenetre.getVueDPGraphe().ajouterCell(composantCell);
			//fenetre.getVueDPGraphe().selectionneCell(composantCell);
	
			AllAttrubiteCell.put(composantCell,composantCell.getAttributes());
			AllAttrubiteCell.putAll(composantCell.getAttributesFilsMap());
			
			 fenetre.getVueDPGraphe().getModel().insert(new Object[]{composantCell}, AllAttrubiteCell, null, composantCell.getParentMap(),null );
			 Vector vcs = composantCell.getVectorConnectionsSets();
		
			 for(int i=0;i<vcs.size();i++)
			 {
				 fenetre.getVueDPGraphe().getModel().insert(null, null, (ConnectionSet) composantCell.getVectorConnectionsSets().get(i), null,null );
			 }
			 fenetre.getVueDPGraphe().setSelectionCells(fenetre.getVueDPGraphe().getVectorSelectionCells().toArray());
			
			// récupération du niveau actuel du diagramme
			if(((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel()).getComposantCellCells().size()>0)
			{
				// on a deja un composant sur le diagramme
				int niveau = ((ComposantCellDyn)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getComposantCellCells().get(0)).getDocuments().size()-1;
				for(int i=0;i<niveau;i++)
				{
					composantCell.incrementerLigneDeVie();
				}
			}
			//composantCell.incrementerLigneDeVie();composantCell.incrementerLigneDeVie();composantCell.incrementerLigneDeVie();
			 return (true);
		}
	}
       
	public boolean est_vide()
    {
    	return (this.composant.estComposantVide());
    }
	
	
	
}
