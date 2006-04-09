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
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeDyn;
import iepp.ui.iedition.dessin.rendu.liens.LigneEdgeDyn;

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
public class CMonterOuDecendreLienEdgeDyn extends CommandeAnnulable
{
	/**
	 * Lien à décaler dans le graphe
	 */
	private LienEdgeDyn lien ;

	/**
	 * sens du décalage dans le graphe
	 */
	private boolean bas ;
	
	/**
	 * le graphe
	 */
	private VueDPGraphe diagrame;
	
		
	/**
	 * Création de la commande à partir du sens
	 * @param comp
	 */
	public CMonterOuDecendreLienEdgeDyn (VueDPGraphe vue,boolean descendre)
	{
		this.bas = descendre;
		this.diagrame = vue;
	}

	/**
	 * Exécuter la commande
	 * @return true si la commande s'est bien exécutée
	 */
	public boolean executer()
	{
		GraphModelView graph = (GraphModelView)diagrame.getModel();
		
		//Récupération du composant sellectionner
		if (diagrame.getSelectionCell() instanceof LienEdgeDyn){
			this.lien = (LienEdgeDyn)diagrame.getSelectionCell();
		}else{
			this.lien = null;
		}
		
		if (this.lien != null){
		
			Map allmap = new HashMap();
			IeppCell docAvant;
			
			DocumentCellDyn docSource = ((DocumentCellDyn)(this.lien.getSourceEdge()));
			ComposantCellDyn comp = docSource.getComposant();
			
			// Mémorisation de l'ancien niveau et du nouveau
			int niveauOld = docSource.getNiveau();
			int niveauNew = docSource.getNiveau();
			
			if(this.bas){
				if(niveauOld != comp.getDocuments().size()-1){
					niveauNew = niveauOld + 1;
				}else{
					return false;
				}
			}else{
				if(niveauOld != 1){
					niveauNew = niveauOld - 1;
				}else{
					return false;
				}
			}
			
			//On modifi tout les documents du graph avec leur nouveau niveau
			for(int i=0;i<graph.getComposantCellCells().size();i++){
				//Pour chaque composant on récupere les document a changer
				ComposantCellDyn tempComp = (ComposantCellDyn)graph.getComposantCellCells().elementAt(i);
				
				DocumentCellDyn dold = null;
				DocumentCellDyn dnew = null;
				
				for(int j=0;j<tempComp.getDocuments().size();j++){
					DocumentCellDyn docModif = (DocumentCellDyn)tempComp.getDocuments().elementAt(j);
					
					if(docModif.getNiveau() == niveauOld){
						dold = docModif;
						docModif.setNiveau(niveauNew);
						
					}else if(docModif.getNiveau() == niveauNew){
						dnew = docModif;
						docModif.setNiveau(niveauOld);
					}
					
					docModif.updatePosition();
					allmap.put(docModif,docModif.getAttributs());
				}
				
				//Ajout des nouvelle valeur dans le graph
				graph.insert(null,allmap,null,null,null);
				
				//Réorganisation des lignes
				tempComp.getConnectionsSets().clear();
				for(int j=0;j<tempComp.getDocuments().size();j++){
					DocumentCellDyn doc = (DocumentCellDyn)tempComp.getDocuments().elementAt(j);
					int nivo = doc.getNiveau();
					if(nivo == 1){
						LigneEdgeDyn ligneHaut = (LigneEdgeDyn)doc.getLigneEdgeHaut();
						ComposantCellElementDyn composantCellElementDyn = tempComp.getComposantCellElementDyn();
						ConnectionSet cs = new ConnectionSet(ligneHaut, composantCellElementDyn.getPortComp(), doc.getPortComp());
						tempComp.getConnectionsSets().add(cs);
						graph.insert(null,null,cs,null,null);
					}else{
						LigneEdgeDyn ligneHaut = (LigneEdgeDyn)doc.getLigneEdgeHaut();
						DocumentCellDyn doc2 = null;
						for(int s = 0;s<tempComp.getDocuments().size();s++){
							DocumentCellDyn docTemp = (DocumentCellDyn)tempComp.getDocuments().elementAt(s);
							if(docTemp.getNiveau() == (nivo-1)){
								doc2 = docTemp;
								break;
							}
						}
						if(doc2 != null){
							ConnectionSet cs = new ConnectionSet(ligneHaut, doc.getPortComp(), doc2.getPortComp());
							tempComp.getConnectionsSets().add(cs);
							graph.insert(null,null,cs,null,null);
						}
					}
				}
				
				tempComp.updateParentMap();
				graph.insert(null,null,null,tempComp.getParentMap(),null);
			}
		
			//Ajout des nouvelle valeur dans le graph
			graph.insert(null,allmap,null,null,null);
			
			diagrame.repaint();
			return true;
		}
		
		return false;
	}
       
	
	
	
	
}
