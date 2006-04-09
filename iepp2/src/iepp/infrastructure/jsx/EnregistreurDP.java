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
package iepp.infrastructure.jsx;

import iepp.Application;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.PaquetagePresentation;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeFusion;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeNote;

import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class EnregistreurDP
{
	/*
	 * Fichier zip contenant la sauvegarde XML de la définition processus
	 */
	private ZipOutputStream mZipFile;
	
	private VueDPGraphe vdpg; 
	
	// Propriets pour le xml
	boolean statiqueTrouve;
	
	/**
	 * Constructeur à partir du fichier zip à remplir
	 * @param zipFile
	 */
	public EnregistreurDP(ZipOutputStream zipFile)
	{
		mZipFile = zipFile;
	}
	/**
	 * Get de vdpg pour tests JUnit par GHILES Damien
	 */
	public VueDPGraphe getvdpg()
	{return this.vdpg;}
	
	/**
	 * Construit la sauvegarde au format XML et la met dans le fichier zip
	 * @throws IOException
	 */
	public void sauver() throws IOException
	{	
		ZipEntry entryZip = new ZipEntry("DefinitionProcessus.xml");
		mZipFile.putNextEntry(entryZip);
		
		vdpg = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		statiqueTrouve = false;
		
		Element racine = new Element("ieppnit");
		
		//On crée un nouveau Document JDOM basé sur la racine que l'on vient de créer
		Document document = new Document(racine);
		
		Element proprietes = sauverProprietes();
		if (proprietes.getChildren().size()>0) {
			racine.addContent(proprietes);
		}
		
		Element statique = sauverStatique();
		if (statique.getChildren().size()>0) {
			racine.addContent(statique);
		}
		
		Element generation = sauverGeneration();
		if (generation.getChildren().size()>0) {
			racine.addContent(generation);
		}
		
		// On sauve le fichier xml dans le zip
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		sortie.output(document, new DataOutputStream( new BufferedOutputStream(mZipFile)));
		
		// On ferme l'entrée du zip
		mZipFile.closeEntry();
	}
	
	private Element sauverProprietes() {
		//On récupère les propriétés du processus
		Element proprietes = new Element("proprietes");
		
		Element auteur = new Element("auteur");
		auteur.setText(Application.getApplication().getProjet().getDefProc().getAuteur());
		proprietes.addContent(auteur);
		
		Element commentaires = new Element("commentaires");
		commentaires.setText(Application.getApplication().getProjet().getDefProc().getCommentaires());
		proprietes.addContent(commentaires);
		
		Element definition = new Element("definition");
		definition.setText(Application.getApplication().getProjet().getDefProc().getNomDefProc());
		proprietes.addContent(definition);
		
		Element email = new Element("email");
		email.setText(Application.getApplication().getProjet().getDefProc().getEmailAuteur());
		proprietes.addContent(email);
		
		Element zoom = new Element("zoom");
		zoom.setText(Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getScale()+"");
		proprietes.addContent(zoom);
		
		return proprietes;		
	}
	
	private Element sauverStatique() {
		// On sauve le statique, composé d'éléments, de produits fusion, de notes.
		Element statique = new Element("statique");
		
		// On sauve les composants
		Element composants = sauverComposants();
		if (composants.getChildren().size()>0) {
			statique.addContent(composants);
		}
		
		// On sauve les produits
		Element produits = sauverProduits();
		if (produits.getChildren().size()>0) {
			statique.addContent(produits);
		}
		
		// On sauve les liens
		Element liens = sauverLiens();
		if (liens.getChildren().size()>0) {
			statique.addContent(liens);
		}

		// On sauvegarde les notes
		Element notes = sauverNotes();
		if (notes.getChildren().size()>0) {
			statique.addContent(notes);
		}
		
		return statique;
	}
	
	private Element sauverComposants() {
		Element composants = new Element("composants");
		
		// On sauve chaque composants
		Vector listComposants = vdpg.getComposantCellCells();
		for( int i = 0 ; i < listComposants.size() ; i++) {
			ComposantCell cc = (ComposantCell)listComposants.get(i);
			Element compsosant = new Element("composant");
			
			long idc = Application.getApplication().getReferentiel().chercherId( cc.getCompProc() );
			Attribute identifiant = new Attribute("id",idc+"");
			compsosant.setAttribute(identifiant);
			
			Element nom = new Element("nom");
			nom.setText(cc.nomComposantCellule.replace("'", "#27"));
			compsosant.addContent(nom);
			
			Element fichier = new Element("fichier");
			fichier.setText(Application.getApplication().getReferentiel().getFileFromId(idc));
			compsosant.addContent(fichier);
			
			Element positiongeneration = new Element("positiongeneration");
			Vector v = Application.getApplication().getProjet().getDefProc().getListeAGenerer();
			int ret = -1;
			for(int j=0;j<v.size();j++) {
				if(v.elementAt(j) instanceof IdObjetModele) {
					IdObjetModele cpc = (IdObjetModele) v.elementAt(j);
					if(cpc == cc.getCompProc().getIdComposant()) {
						ret = j;
					}
				}
			}
			positiongeneration.setText(ret+"");
			compsosant.addContent(positiongeneration);
			
			Element positionx = new Element("positionx");
			positionx.setText(cc.getAbscisse()+"");
			compsosant.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(cc.getOrdonnee()+"");
			compsosant.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(cc.getLargeur()+"");
			compsosant.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(cc.getHauteur()+"");
			compsosant.addContent(hauteur);
			
			Element imageprod = new Element("imageprod");
			imageprod.setText(cc.getImageComposant());
			compsosant.addContent(imageprod);
			
			composants.addContent(compsosant);
		}
		return composants;
	}

	private Element sauverProduits() {
		Element produits = new Element("produits");
		
		// On sauvegarde les produits en entree
		sauverProduitsEntree(produits);
		
		// On sauvegarde les produits en sortie
		sauverProduitsSortie(produits);
		
		// On sauvegarde les produits fusion
		sauverProduitsFusion(produits);
		
		return produits;
	}
	
	private void sauverProduitsEntree(Element produits) {
		// On sauve chaque produits en entree
		Vector listProduitsEntree = vdpg.getProduitCellEntreeCells();
		int nb = listProduitsEntree.size();
		for( int i = 0 ; i < nb ; i++) {
			ProduitCellEntree pce = (ProduitCellEntree)listProduitsEntree.get(i);
			Element produit = new Element("produitsentree");
			
			Element identifiant = new Element("identifiant");
			identifiant.setText(Application.getApplication().getReferentiel().chercherId(pce.getId().getRef())+"");
			produit.addContent(identifiant);
			
			Element nom = new Element("nom");
			nom.setText(pce.nomComposantCellule.replace("'", "#27"));
			produit.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(pce.getAbscisse()+"");
			produit.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(pce.getOrdonnee()+"");
			produit.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(pce.getLargeur()+"");
			produit.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(pce.getHauteur()+"");
			produit.addContent(hauteur);
			
			Element imageprod = new Element("imageprod");
			imageprod.setText(pce.getImageComposant());
			produit.addContent(imageprod);
			
			Element visible = new Element("masque");
			visible.setText(pce.isMasquer()+"");
			produit.addContent(visible);
			
			Element lie = new Element("lie");
			lie.setText(pce.isCellLiee()+"");
			produit.addContent(lie);

			produits.addContent(produit);
		}
	}

	private void sauverProduitsSortie(Element produits) {
		// On sauve chaque produits en sortie
		Vector listProduitsSortie = vdpg.getProduitCellSortieCells();
		for( int i = 0 ; i < listProduitsSortie.size() ; i++) {
			ProduitCellSortie pcs= (ProduitCellSortie)listProduitsSortie.get(i);
			Element produit = new Element("produitssortie");
			
			Element identifiant = new Element("identifiant");
			identifiant.setText(Application.getApplication().getReferentiel().chercherId(pcs.getId().getRef())+"");
			produit.addContent(identifiant);
			
			Element nom = new Element("nom");
			nom.setText(pcs.nomComposantCellule.replace("'", "#27"));
			produit.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(pcs.getAbscisse()+"");
			produit.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(pcs.getOrdonnee()+"");
			produit.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(pcs.getLargeur()+"");
			produit.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(pcs.getHauteur()+"");
			produit.addContent(hauteur);
			
			Element imageprod = new Element("imageprod");
			imageprod.setText(pcs.getImageComposant());
			produit.addContent(imageprod);
			
			Element visible = new Element("masque");
			visible.setText(pcs.isMasquer()+"");
			produit.addContent(visible);
			
			Element lie = new Element("lie");
			lie.setText(pcs.isCellLiee()+"");
			produit.addContent(lie);

			produits.addContent(produit);
		}
	}

	private void sauverProduitsFusion(Element produits) {
		// On sauve chaque produits en sortie
		Vector listProduitsFusion = vdpg.getProduitCellFusionCells();
		for( int i = 0 ; i < listProduitsFusion.size() ; i++) {
			ProduitCellFusion pcf= (ProduitCellFusion)listProduitsFusion.get(i);
			Element produit = new Element("produitsfusion");
			
			Element identifiant = new Element("identifiant");
			identifiant.setText(Application.getApplication().getReferentiel().chercherId(pcf.getId().getRef())+"");
			produit.addContent(identifiant);
			
			Element nom = new Element("nom");
			nom.setText(pcf.nomComposantCellule.replace("'", "#27"));
			produit.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(pcf.getAbscisse()+"");
			produit.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(pcf.getOrdonnee()+"");
			produit.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(pcf.getLargeur()+"");
			produit.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(pcf.getHauteur()+"");
			produit.addContent(hauteur);
			
			Element imageprod = new Element("imageprod");
			imageprod.setText(pcf.getImageComposant());
			produit.addContent(imageprod);
			
			Element produitsorigine = new Element("produitfusion");
				
			Vector listproduitentree = pcf.getProduitCellEntrees();
			
			for(int j=0;j<listproduitentree.size(); j++) {
				ProduitCellEntree pcetmp = (ProduitCellEntree)listproduitentree.elementAt(j);
				
				Element p1 = new Element("produitfusionentree");
					
					
					Element id1 = new Element("composantproduit");
					id1.setText(pcetmp.getCompParent().nomComposantCellule);
					p1.addContent(id1);
				
					Element nom1 = new Element("nom");
					nom1.setText(pcetmp.getNomCompCell().replace("'", "#27"));
					p1.addContent(nom1);
					produitsorigine.addContent(p1);
			}
				Element p2 = new Element("produitfusionsortie");
					
					Element id2 = new Element("composantproduit");
					id2.setText(pcf.getProduitCellSortie().getCompParent().nomComposantCellule);
					p2.addContent(id2);
				
					Element nom2 = new Element("nom");
					nom2.setText(pcf.getProduitCellSortie().getNomCompCell().replace("'", "#27"));
					p2.addContent(nom2);
				
				produitsorigine.addContent(p2);

			produit.addContent(produitsorigine);
			
			produits.addContent(produit);
		}
	}

	private Element sauverLiens() {
		// On sauve le liens, en faisant attention au type du lien
		Element liens = new Element("liens");
		Element lien;
		
		Vector listLiens = vdpg.getLiens();
		
		for( int i = 0 ; i < listLiens.size() ; i++) {
			if( listLiens.get(i) instanceof LienEdgeNote) {
				lien = sauverLienEdgeNote((LienEdgeNote)listLiens.get(i));
				liens.addContent(lien);
			}
			else if( listLiens.get(i) instanceof LienEdgeFusion) {
				lien = sauverLienEdgeFusion((LienEdgeFusion)listLiens.get(i));
				liens.addContent(lien);
			}
			else {
				lien = sauverLienEdge((LienEdge)listLiens.get(i));
				liens.addContent(lien);
			}
		}
		return liens;
	}
	
	private Element sauverLienEdgeNote(LienEdgeNote len) {
		// On sauve les liens entre une note et une cellule du graphique
		Element LienEdgeNote = new Element("liennote");
		Element source = sauverIeppCell(len.getSourceEdge(),"source");
			Element destination = sauverIeppCell(len.getDestination(),"destination");
		LienEdgeNote.addContent(source);
		LienEdgeNote.addContent(destination);
		
		Element pointsancrage = sauverPointsAncrage(len);
		LienEdgeNote.addContent(pointsancrage);
		
		return LienEdgeNote;
	}
	
	
	private Element sauverLienEdgeFusion(LienEdgeFusion lef) {
		// On sauve les liens de fusion
		Element LienEdgeFusion = new Element("lienfusion");
			Element source = sauverIeppCell(lef.getSourceEdge(),"source");
			Element destination = sauverIeppCell(lef.getDestination(),"destination");
		LienEdgeFusion.addContent(source);
		LienEdgeFusion.addContent(destination);
			
		Element pointsancrage = sauverPointsAncrage(lef);
		LienEdgeFusion.addContent(pointsancrage);
		
		return LienEdgeFusion;
	}
	
	private Element sauverLienEdge(LienEdge le) {
		// On sauve les liens entre composants et produitsentree/produitssortie
		Element LienEdge = new Element("lien");
			Element source = sauverIeppCell(le.getSourceEdge(),"source");
			Element destination = sauverIeppCell(le.getDestination(),"destination");
			LienEdge.addContent(source);
			LienEdge.addContent(destination);
		
		Element pointsancrage = sauverPointsAncrage(le);
		LienEdge.addContent(pointsancrage);
		
		return LienEdge;
	}
	
	private Element sauverPointsAncrage(LienEdge l) {
		// On sauve les points d'encrage
		Element pointsancrage = new Element("pointsancrage");
		for(int i=0 ; i<l.getPointAncrage().size() ; i++) {
			Point p = (Point) l.getPointAncrage().elementAt(i);
			Element point = new Element("point");
				Element x = new Element("x");
				x.setText(p.getX()+"");
				Element y = new Element("y");
				y.setText(p.getY()+"");
			point.addContent(x);
			point.addContent(y);
			pointsancrage.addContent(point);
		}
		return pointsancrage;
	}
	
	private Element sauverIeppCell(IeppCell ic, String nomNoeud) {
		// Methode pour sauver les informations aux extrémités des liens
		if(ic instanceof ProduitCellEntree) {
			Element noeud = new Element(nomNoeud);
				
				Element typeproduit = new Element("type");
				typeproduit.setText("produitentree");
				noeud.addContent(typeproduit);
				
				Element composantproduit = new Element("composantproduit");
				composantproduit.setText(((ProduitCellEntree)ic).getCompParent().nomComposantCellule.replace("'", "#27"));
				noeud.addContent(composantproduit);
				
				Element nomproduit = new Element("nomproduit");
				nomproduit.setText(((ProduitCellEntree)ic).getNomCompCell().replace("'", "#27"));
				noeud.addContent(nomproduit);
			return noeud;
		}
		if(ic instanceof ProduitCellSortie) {
			Element noeud = new Element(nomNoeud);
				
				Element typeproduit = new Element("type");
				typeproduit.setText("produitsortie");
				noeud.addContent(typeproduit);
				
				Element composantproduit = new Element("composantproduit");
				composantproduit.setText(((ProduitCellSortie)ic).getCompParent().nomComposantCellule.replace("'", "#27"));
				noeud.addContent(composantproduit);
				
				Element nomproduit = new Element("nomproduit");
				nomproduit.setText(((ProduitCellSortie)ic).getNomCompCell().replace("'", "#27"));
				noeud.addContent(nomproduit);
			return noeud;
		}
		if(ic instanceof ProduitCellFusion) {
			Element noeud = new Element(nomNoeud);
				
				Element typeproduit = new Element("type");
				typeproduit.setText("produitfusion");
				noeud.addContent(typeproduit);
				
				Element nomproduit = new Element("nomproduit");
				nomproduit.setText(((ProduitCellFusion)ic).getNomCompCell().replace("'", "#27"));
				noeud.addContent(nomproduit);
			return noeud;
		}
		if(ic instanceof ComposantCell) {
			Element noeud = new Element(nomNoeud);
				
				Element typeproduit = new Element("type");
				typeproduit.setText("composant");
				noeud.addContent(typeproduit);
				
				Element nomproduit = new Element("nomcomposant");
				nomproduit.setText(((ComposantCell)ic).getNomCompCell().replace("'", "#27"));
				noeud.addContent(nomproduit);
			return noeud;
		}
		
		Element noeud = new Element(nomNoeud);
			Element typeproduit = new Element("type");
			typeproduit.setText("textcell");
			noeud.addContent(typeproduit);
			
			Element positionx = new Element("positionx");
			positionx.setText(((TextCell)ic).getAbscisse()+"");
			noeud.addContent(positionx);
		
			Element positiony = new Element("positiony");
			positiony.setText(((TextCell)ic).getOrdonnee()+"");
			noeud.addContent(positiony);
		return noeud;
	}
	
	private Element sauverNotes() {
		// On sauve les notes
		Element notes = new Element("notes");
		
		Vector listNotes = vdpg.getNoteCellCells();

		for( int i = 0 ; i < listNotes.size() ; i++) {
			TextCell tc = (TextCell)listNotes.get(i);
			
			Element note = new Element("note");
			
			Element nom = new Element("texte");
			nom.setText(tc.getMessage().replace("'", "#27"));
			note.addContent(nom);
			
			Element positionx = new Element("positionx");
			positionx.setText(tc.getAbscisse()+"");
			note.addContent(positionx);
			
			Element positiony = new Element("positiony");
			positiony.setText(tc.getOrdonnee()+"");
			note.addContent(positiony);
			
			Element largeur = new Element("largeur");
			largeur.setText(tc.getLargeur()+"");
			note.addContent(largeur);
			
			Element hauteur = new Element("hauteur");
			hauteur.setText(tc.getHauteur()+"");
			note.addContent(hauteur);
			
			notes.addContent(note);
		}
		return notes;
	}
	
	private Element sauverGeneration() {
		// On sauve le statique, composé d'éléments, de produits fusion, de notes.
		Element generation = new Element("generation");
		
		// On sauve les paquetages de présentation
		Element paquetage = sauverPaquetages();
		if (paquetage.getChildren().size()>0) {
			generation.addContent(paquetage);
		}
		
		// On sauve les paquetages de présentation
		Element roles = sauverRoles();
		if (roles.getChildren().size()>0) {
			generation.addContent(roles);
		}
		return generation;
	}
	
	private Element sauverPaquetages() {
		// On sauve le liens, en faisant attention au type du lien
		Element paquetages = new Element("paquetages");
		Element paquetage;
		
		Vector v = Application.getApplication().getProjet().getDefProc().getListeAGenerer();
		
		for(int j=0;j<v.size();j++) {
			if(v.elementAt(j) instanceof PaquetagePresentation) {
				PaquetagePresentation pp = (PaquetagePresentation)v.elementAt(j);
				paquetage = new Element("paquetage");
				
				File f = new File(pp.getNomFichier());
				Attribute identifiant = new Attribute("file", f.getName());
				paquetage.setAttribute(identifiant);
				
				Element positiongeneration = new Element("positiongenerationpaquetage");
				positiongeneration.setText(j+"");
				paquetage.addContent(positiongeneration);
				
				paquetages.addContent(paquetage);
			}
		}
		return paquetages;
	}
	
	private Element sauverRoles() {
		// On sauve le liens, en faisant attention au type du lien
		Element roles = new Element("rolesgeneration");
		Element role;
		
		HashMap hm = Application.getApplication().getProjet().getDefProc().getListeAssociationSRole();
		
		Set entrees = hm.entrySet();
		Iterator it = entrees.iterator();
		while(it.hasNext()) {
			role = new Element("rolegeneration");
			
			Map.Entry entree = (Map.Entry)it.next();
			IdObjetModele iomk = (IdObjetModele)entree.getKey();
			IdObjetModele iome = (IdObjetModele)entree.getValue();

			Element role1 = new Element("role1");
			role1.setText(iomk.toString().replace("'", "#27"));
			role.addContent(role1);
			
			Element comprole1 = new Element("comprole1");
			comprole1.setText(((ComposantProcessus)iomk.getRef()).getNomComposant().replace("'", "#27"));
			role.addContent(comprole1);
			
			Element role2 = new Element("role2");
			role2.setText(iome.toString().replace("'", "#27"));
			role.addContent(role2);
			
			Element comprole2 = new Element("comprole2");
			comprole2.setText(((ComposantProcessus)iome.getRef()).getNomComposant().replace("'", "#27"));
			role.addContent(comprole2);
			
			roles.addContent(role);
		}
		return roles;
	}
}