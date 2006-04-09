package iepp.ui.iedition.dessin.rendu;

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

import iepp.Application;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.liens.LigneEdgeDyn;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.ParentMap;

/**
 * 
 * 
 * @author Hubert
 *
 */

public class ComposantCellDyn extends IeppCell {
	
	protected int abscisse;
	protected int ordonnee;
	protected int largeur;
	protected int hauteur;
	
	protected ComposantProcessus compProc;
	protected ComposantCellElementDyn composantCellElementDyn;
	protected DocumentCellDyn cell2;
	
	protected ParentMap parentMap;
	protected Map attributesFilsMap;
	
	protected Vector lignesEdges;
	protected Vector connectionsSets;
	protected Vector documents;
	
	public ComposantCellDyn(IdObjetModele comp, int x, int y) { 
		
		super(((ComposantProcessus)comp.getRef()).getNomComposant());
		this.remove(getPortComp());
		this.compProc = (ComposantProcessus)comp.getRef();
		
		// On garde dans l'objet un trace de la position du composant sur le graph
		this.abscisse=x;
		this.ordonnee=y;	
		
		// Init Vector
		lignesEdges = new Vector();
		documents = new Vector();
		connectionsSets = new Vector();
		
		// On créé les deux cellules
		composantCellElementDyn = new ComposantCellElementDyn(comp,x,y,this);
		GraphConstants.setMoveable(composantCellElementDyn.getAttributs(), false);
		
		cell2 = new DocumentCellDyn(this);
		documents.add(cell2);
		//GraphConstants.setBounds(cell2.getAttributs(),new Rectangle(cell1.getAbscisse()+(cell1.getLargeur()/2)-(cell2.getLargeur()/2),cell1.getOrdonnee()+(cell1.getHauteur()/2)+(cell2.getHauteurMax()/2)+( 70 * documents.size() ),cell2.getLargeur(),cell2.getHauteur()));
		
		lignesEdges.add(new LigneEdgeDyn(composantCellElementDyn,cell2));
		
		cell2.setLigneEdgeHaut((LigneEdgeDyn) lignesEdges.get(0));
		
		ConnectionSet cs = new ConnectionSet((LigneEdgeDyn) lignesEdges.get(0), composantCellElementDyn.getPortComp(), cell2.getPortComp());
		//connectionsSets = new Vector();
		connectionsSets.add(cs);
		
		// On créé les deux maps
		parentMap = new ParentMap();
		attributesFilsMap = GraphConstants.createMap();
		
		// On ajoute les attributs des fils dans la map des fils
		attributesFilsMap.put(composantCellElementDyn, composantCellElementDyn.getAttributs());
		attributesFilsMap.put(cell2, cell2.getAttributs());
		attributesFilsMap.put((LigneEdgeDyn) lignesEdges.get(0), ((LigneEdgeDyn) lignesEdges.get(0)).getEdgeAttribute());
		
		// On ajoute les composants a la parent map
		parentMap.addEntry(composantCellElementDyn, this);
		parentMap.addEntry(cell2, this);
		parentMap.addEntry((LigneEdgeDyn) lignesEdges.get(0), this);
		parentMap.addEntry(cs,this);
				
		// Définition des attributs du composant
		GraphConstants.setBounds(getAttributes(), new Rectangle((int)abscisse,(int)ordonnee,(int)largeur,(int)hauteur));
		//GraphConstants.setAutoSize(getAttributs(), true);
		GraphConstants.setEditable(getAttributes(), false);
		
		// le premier lien
		GraphConstants.setEditable(((LigneEdgeDyn) lignesEdges.get(0)).getAttributes(), false);
		GraphConstants.setMoveable(((LigneEdgeDyn) lignesEdges.get(0)).getAttributes(),false);
		GraphConstants.setBendable(((LigneEdgeDyn) lignesEdges.get(0)).getAttributes(),false);
		GraphConstants.setSizeable (getAttributes(), false);
		GraphConstants.setBendable(getAttributes(), false);
		GraphConstants.setMoveable(getAttributes(),false);
		
		
	}
	
	public Map getAttributesFilsMap() {
		return attributesFilsMap;
	}

	public ComposantCellElementDyn getComposantCellElementDyn() {
		return composantCellElementDyn;
	}

	public ParentMap getParentMap() {
		return parentMap;
	}

	public Vector getDocuments() {
		return documents;
	}

	public IdObjetModele getId()
    {
    	return this.compProc.getIdComposant();
    }
	


	public IeppCell getDerniereCellule() {
		return (IeppCell) documents.lastElement();
	}
	
	public void incrementerLigneDeVie() {
		
		// Recup du du dernier document
		DocumentCellDyn dernier = (DocumentCellDyn) documents.lastElement();
		
		// Création de la cellule document supplémentaire et on l'ajoute au vecteur de documents
		cell2 = new DocumentCellDyn(this);
		documents.add(cell2);
		// On place le document en fonction de sa position dans le vecteur
		//GraphConstants.setBounds(cell2.getAttributs(),new Rectangle(cell1.getAbscisse()+(cell1.getLargeur()/2)-(cell2.getLargeur()/2),cell1.getOrdonnee()+(cell1.getHauteur()/2)+(cell2.getHauteurMax()/2)+( 70 * documents.size() ),cell2.getLargeur(),cell2.getHauteur()));

		
		// Création du edge utilisé pour afficher la ligne de vie du composant
		// Modif Julie 09/02/06
		LigneEdgeDyn ligneEdge = new LigneEdgeDyn(dernier,cell2);
		lignesEdges.add(ligneEdge);
		
		dernier.setLigneEdgeBas(ligneEdge);
		cell2.setLigneEdgeHaut(ligneEdge);
		
		
		ConnectionSet cs = new ConnectionSet(ligneEdge,dernier.getPortComp(), cell2.getPortComp());
		connectionsSets.add(cs);
		
		attributesFilsMap.put(cell2, cell2.getAttributs());
		attributesFilsMap.put(ligneEdge, ligneEdge.getEdgeAttribute());
		
		parentMap.addEntry(cell2, this);
		parentMap.addEntry(ligneEdge, this);
		parentMap.addEntry(cs, this);
		
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().insert(null, attributesFilsMap, null, getParentMap(),null );
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().insert(null, null, (ConnectionSet) cs, null,null );
	}

	public void supprimerNiveauLigneDeVie(int niveau) {
		
		Vector vecObj = new Vector();
		DocumentCellDyn docASuppr = null;
		DocumentCellDyn docApres = null;
		IeppCell docAvant = null;
		GraphModelView graph = (GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel();
		
		
		if(niveau == 1){
			
			for(int s = 0;s<documents.size();s++){
				DocumentCellDyn docTemp = (DocumentCellDyn)documents.elementAt(s);
				if(docTemp.getNiveau() == (niveau)){
					docASuppr = docTemp;
					break;
				}
			}
			for(int s = 0;s<documents.size();s++){
				DocumentCellDyn docTemp = (DocumentCellDyn)documents.elementAt(s);
				if(docTemp.getNiveau() == (niveau+1)){
					docApres = docTemp;
					break;
				}
			}
			docAvant = getComposantCellElementDyn();
			
			if(docASuppr == null || docApres == null){
				System.out.println("Document apres ou a supprimer non trouvé");
				return;
			}
			
		}else{
			for(int s = 0;s<documents.size();s++){
				DocumentCellDyn docTemp = (DocumentCellDyn)documents.elementAt(s);
				if(docTemp.getNiveau() == (niveau)){
					docASuppr = docTemp;
					break;
				}
			}
			for(int s = 0;s<documents.size();s++){
				DocumentCellDyn docTemp = (DocumentCellDyn)documents.elementAt(s);
				if(docTemp.getNiveau() == (niveau+1)){
					docApres = docTemp;
					break;
				}
			}
			for(int s = 0;s<documents.size();s++){
				DocumentCellDyn docTemp = (DocumentCellDyn)documents.elementAt(s);
				if(docTemp.getNiveau() == (niveau-1)){
					docAvant = docTemp;
					break;
				}
			}
			
			if(docASuppr == null || docApres == null || docAvant == null){
				System.out.println("Document avant,apres ou a supprimer non trouvé");
				return;
			}
					
		}
		
		LigneEdgeDyn ligneBas = docASuppr.getLigneEdgeBas();
		LigneEdgeDyn ligneHaut = docASuppr.getLigneEdgeHaut();
			
		if(docAvant instanceof DocumentCellDyn){
			((DocumentCellDyn)docAvant).setLigneEdgeBas(ligneHaut);
		}
		docApres.setLigneEdgeHaut(ligneHaut);
		
		
		ligneHaut.setSource(docAvant);
		ConnectionSet cs = new ConnectionSet(ligneHaut,docAvant.getPortComp(), docApres.getPortComp());
		
		
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().insert(null, null, cs, null,null );
		
		//vecObj.add(ligneBas);
		vecObj.add(docASuppr.getPortComp());
		vecObj.add(docASuppr);
		vecObj.add(getConnectionsSets().get(niveau-1));
		vecObj.add(getConnectionsSets().get(niveau));
	
		graph.remove(vecObj.toArray());
		
		// on retire les objets a supprimer des vecteurs
		documents.removeElement(docASuppr);
		
		//diminue le niveau de chaque document
		for(int i = 0; i<documents.size();i++){
			
			DocumentCellDyn docTemp = (DocumentCellDyn)documents.elementAt(i);
			
			if(docTemp.getNiveau()>niveau){
				docTemp.setNiveau(docTemp.getNiveau()-1);
			}
			
			Map map = GraphConstants.createMap();
			
			// On ajoute les attributs des fils dans la map des fils
			map.put(docTemp, docTemp.getAttributs());
			
			//GraphConstants.setMoveable(this.getAttributs(),true);
			GraphConstants.setMoveable(docTemp.getAttributs(),true);
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().insert(null, map, null, null,null );
			
			docTemp.setOrdonnee(					
					this.getComposantCellElementDyn().getOrdonnee()+(this.getComposantCellElementDyn().getHauteur()/2)+(DocumentCellDyn.hauteurMax/2)+( 70 * (docTemp.getNiveau()) )
					);

			//GraphConstants.setMoveable(this.getAttributs(),false);
			GraphConstants.setMoveable(docTemp.getAttributs(),false);
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().insert(null, map, null, null,null );
			
		}
		
		//Réorganisation des lignes
		connectionsSets.clear();
		for(int j=0;j<documents.size();j++){
			DocumentCellDyn doc = (DocumentCellDyn)documents.elementAt(j);
			int nivo = doc.getNiveau();
			if(nivo == 1){
				LigneEdgeDyn ligneHautDoc = (LigneEdgeDyn)doc.getLigneEdgeHaut();
				ComposantCellElementDyn composantCellElementDyn = this.getComposantCellElementDyn();
				ConnectionSet csDoc = new ConnectionSet(ligneHautDoc, composantCellElementDyn.getPortComp(), doc.getPortComp());
				connectionsSets.add(csDoc);
				graph.insert(null,null,cs,null,null);
			}else{
				LigneEdgeDyn ligneHautDoc = (LigneEdgeDyn)doc.getLigneEdgeHaut();
				DocumentCellDyn doc2 = null;
				for(int s = 0;s<documents.size();s++){
					DocumentCellDyn docTemp = (DocumentCellDyn)documents.elementAt(s);
					if(docTemp.getNiveau() == (nivo-1)){
						doc2 = docTemp;
						break;
					}
				}
				if(doc2 != null){
					ConnectionSet csDoc = new ConnectionSet(ligneHautDoc, doc.getPortComp(), doc2.getPortComp());
					connectionsSets.add(csDoc);
					graph.insert(null,null,cs,null,null);
				}
			}
		}
		
		//On supprime la ligne en trop
		for(int y=0;y<lignesEdges.size();y++){
			LigneEdgeDyn ligne = (LigneEdgeDyn)lignesEdges.elementAt(y);
			boolean aSuppr = true;
			for(int s = 0;s<documents.size();s++){
				DocumentCellDyn docTemp = (DocumentCellDyn)documents.elementAt(s);
				if(docTemp.getLigneEdgeHaut().equals(ligne)){
					aSuppr = false;
					break;
				}
			}
			if(aSuppr == true){
				graph.remove(new Object[]{ligne});
				lignesEdges.removeElement(ligne);
			}
			
		}
		
		updateParentMap();
		
		graph.insert(null, attributesFilsMap, null, getParentMap(),null );
		
	}
	
	public Vector getConnectionsSets() {
		return connectionsSets;
	}

	public ComposantProcessus getCompProc() {
		return compProc;
	}

	public void setCompProc(ComposantProcessus compProc) {
		this.compProc = compProc;
	}

	public void setDocuments(Vector documents) {
		this.documents = documents;
	}

	public Vector getLignesEdges() {
		return lignesEdges;
	}

	public int getAbscisse() {
		return (int)(GraphConstants.getBounds(getAttributs()).getX());
	}

	public void setAbscisse(int abscisse) {
		this.abscisse = abscisse;
		GraphConstants.setBounds(getAttributs(), new Rectangle(abscisse,getOrdonnee(),getLargeur(),getHauteur()));
	}

	public int getOrdonnee() {
		return (int)(GraphConstants.getBounds(getAttributs()).getY());
	}

	public void setOrdonnee(int ordonnee) {
		this.ordonnee = ordonnee;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),ordonnee,getLargeur(),getHauteur()));
	}

	public int getHauteur() {
		return (int)(GraphConstants.getBounds(getAttributs()).getHeight());
	}

	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),getOrdonnee(),getLargeur(),hauteur));
	}

	public int getLargeur() {
		return (int)(GraphConstants.getBounds(getAttributs()).getWidth());
	}

	public void setLargeur(int largeur) {
		this.largeur = largeur;
		GraphConstants.setBounds(getAttributs(), new Rectangle(getAbscisse(),getOrdonnee(),largeur,getHauteur()));
	}

	public Vector getVectorConnectionsSets() {
		return connectionsSets;
	}

	public void setVectorConnectionsSets(Vector connectionsSets) {
		this.connectionsSets = connectionsSets;
	}

	public DocumentCellDyn getCell2() {
		return cell2;
	}

	public void setCell2(DocumentCellDyn cell2) {
		this.cell2 = cell2;
	}
	
	public void updateParentMap(){
		parentMap = new ParentMap();
		for(int i=0;i<documents.size();i++){
			parentMap.addEntry(documents.elementAt(i),this);
		}
		for(int i=0;i<lignesEdges.size();i++){
			parentMap.addEntry(lignesEdges.elementAt(i),this);
		}
		for(int i=0;i<connectionsSets.size();i++){
			parentMap.addEntry(connectionsSets.elementAt(i),this);
		}
	}
	
	
}
