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
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.ComposantCellElementDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;

import util.ErrorManager;

/**
 * Commande annulable permettant d'ajouter un composant avec son interface
 * au diagramme. Vérifie si le composant n'est pas déjà dans le diagramme
 */
public class CDecalerComposantGrapheDyn extends CommandeAnnulable
{
	/**
	 * Composant à décaler dans le graphe
	 */
	private ComposantCellDyn composant ;

	/**
	 * sens du décalage dans le graphe
	 */
	private boolean droite ;
	
	/**
	 * le graphe
	 */
	private VueDPGraphe diagrame;
	
		
	/**
	 * Création de la commande à partir du composant à ajouter
	 * @param comp
	 */
	public CDecalerComposantGrapheDyn (VueDPGraphe vue,boolean droite)
	{
		// sauvegarder le composant
		
		this.droite = droite;
		
		this.diagrame = vue;
	}

	/**
	 * Exécuter la commande, ajouter au diagramme le composant
	 * et les interfaces en entrée et sortie
	 * @return true si la commande s'est bien exécutée
	 */
	public boolean executer()
	{
		GraphModelView graph = (GraphModelView)diagrame.getModel();
		
		//Récupération du composant sellectionner
		if (diagrame.getSelectionCell() instanceof ComposantCellDyn){
			this.composant = (ComposantCellDyn)diagrame.getSelectionCell();
		}else if (diagrame.getSelectionCell() instanceof ComposantCellElementDyn){
			ComposantCellElementDyn temp = (ComposantCellElementDyn)diagrame.getSelectionCell();
			this.composant = temp.getComposantCellDyn();
		}else if (diagrame.getSelectionCell() instanceof DocumentCellDyn){
			DocumentCellDyn temp = (DocumentCellDyn)diagrame.getSelectionCell();
			this.composant = temp.getComposant();
		}else{
			this.composant = null;
		}
		
		if (this.composant != null){
			// Repositionnement des composants dans le vecteur
			int position = graph.getComposantCellCells().indexOf(this.composant);
			
			if(this.droite){
				if(position != (graph.getComposantCellCells().size()-1)){
					graph.getComposantCellCells().remove(this.composant);
					graph.getComposantCellCells().insertElementAt(this.composant,(position+1));
				}
			}else{
				if(position != 0){
					graph.getComposantCellCells().remove(this.composant);
					graph.getComposantCellCells().insertElementAt(this.composant,(position-1));
				}
			}
			
			// On réorganise les composant restant
			for(int i = 0;i<graph.getComposantCellCells().size();i++){
				
				Map allmap = new HashMap();
				
				ComposantCellDyn cellDyn = ((ComposantCellDyn)graph.getComposantCellCells().elementAt(i));
				
				// Maj coordonnee de la cellule ComposantCellDyn
				cellDyn.setAbscisse(50+graph.getComposantCellCells().indexOf(cellDyn)*200);
				cellDyn.setOrdonnee(25);
				allmap.put(cellDyn,cellDyn.getAttributes());
				
				// Maj coordonnee de la cellule ComposantCellElementDyn
				cellDyn.getComposantCellElementDyn().setAbscisse(50+graph.getComposantCellCells().indexOf(cellDyn)*200);
				cellDyn.getComposantCellElementDyn().setOrdonnee(25);
				allmap.put(cellDyn.getComposantCellElementDyn(),cellDyn.getComposantCellElementDyn().getAttributes());
				
				// Ajout des nouvelle valeur dans le graph pour la prise en compte pour les documents
				graph.insert(null,allmap,null,cellDyn.getParentMap(),null);
				
				// Maj coordonnee de tous les document de la cellule ComposantCellDyn
				for (int j = 0; j < cellDyn.getDocuments().size(); j++){
					DocumentCellDyn doc = (DocumentCellDyn)cellDyn.getDocuments().get(j);
					doc.updatePosition();
					allmap.put(doc,doc.getAttributs());
				}
				
				//Ajout des nouvelle valeur dans le graph
				graph.insert(null,allmap,null,null,null);
			}
			
			diagrame.repaint();
			return true;
		}
		
		return false;
	}
       
	
	
	
	
}
