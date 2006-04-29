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
import iepp.Projet;
import iepp.application.aedition.CLierInterface;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.domaine.PaquetagePresentation;
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeFusion;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeNote;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

import org.jdom.Element;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.ErrorManager;
import util.MonitoredTaskBase;
import util.TaskMonitorDialog;
import util.xpath;

public class ChargeurDP extends MonitoredTaskBase
{
	private File mFile = null;

	private double zoom;
	/**
	 * Projet à charger
	 */
	private Projet projetCharge = null ;
	
	private DefinitionProcessus dp;
	/**
	 * Boite de dialogue permettant d'afficher l'avancement des tâches
	 */
	private TaskMonitorDialog mTask = null;
	
	/**
	 * Liens créés au niveau du modèle entre les produits
	 */
	private LienProduits lienModele1 ;

	private static xpath xp;
	private static Document document;
	
	private HashMap listegen;
	
	private HashMap rolegen;
	
	private FenetreEdition fenetre;
	/**
	 * Constructeur à partir du fichier contenant la définition de processus à ouvrir
	 * @param file
	 */
	public ChargeurDP(File file)
	{
		mFile = file;
	}

	// getteur du fichier du Chargeur
	// Utile pour les tests JUnit
	// par Bouchikhi Mohamed-Amine
	
	public ArrayList getChargeurDPAttributs()
	{
		ArrayList Tab = new ArrayList();
		Tab.add(this.mFile);
		Tab.add(this.mFile);
		Tab.add(this.projetCharge);
		Tab.add(this.dp);
		Tab.add(this.mTask);
		Tab.add(this.xp);
		Tab.add(this.document);
		Tab.add(this.fenetre);
		
		
		return Tab;
	}
	
	public void setprojetCharge(Projet p){
		this.projetCharge=p;
	}
	
	public void chargerDP()
	{
		DataInputStream data = null;
		try 
		{
			data = findData("DefinitionProcessus.xml");
		} 
		catch (IOException e)
		{
			this.projetCharge = null;
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());
			e.printStackTrace();
		}
		if( data != null )
		{
			this.print(Application.getApplication().getTraduction("dp_trouve"));
			
			// création de la source
			//source = new InputSource(data);
			  
			xp = new xpath(data);
			document = xp.getDocument();
			
			// On declare le projet
			this.projetCharge = new Projet();
			//this.projetCharge.setDefProc(dp);
			dp = this.projetCharge.getDefProc();
			
			this.listegen = new HashMap();
			this.rolegen = new HashMap();
			
			// On charge les proprietes
			chargerProprietes();
			this.print(Application.getApplication().getTraduction("liste_proprietes"));
			
			fenetre = this.projetCharge.getFenetreEdition();
			
			// On charge les composants
			chargerComposants();
			this.print(Application.getApplication().getTraduction("liste_composants"));
			
			// on s'ocupe des produits de fusion
			chargerProduitsFusion();
			this.print(Application.getApplication().getTraduction("liste_produitsfusion"));
			
			// on s'ocupe des produits de notes
			chargerNotes();
			this.print(Application.getApplication().getTraduction("liste_notes"));
			
			// on s'ocupe des produits de notes
			chargerLiensNotes();
			
			// on s'occupe des paquetages de présentation
			chargerPaquetages();
			Vector gen = new Vector();
			int i, i_reel;
			for(i=0, i_reel=0;i_reel<this.listegen.size();i++) {
				if(this.listegen.containsKey(new Integer(i))) {
					gen.add(this.listegen.get(new Integer(i)));
					i_reel++;
				}
			}
			this.dp.setListeAGenerer(gen);
			
			// on s'occupe des roles
			chargerRoles();
			this.dp.setListeAssociationSRole(this.rolegen);
			
			// on s'occupe des propriétés de génération
			chargerProprietesGeneration();
			
			this.print(Application.getApplication().getTraduction("dp_succes"));
			
			fenetre.getVueDPGraphe().clearSelection();
			
			this.projetCharge.setDefProc(this.dp);
			
			this.projetCharge.setModified(false);
		}
		else
		{
			// le fichier definition.xml n'a pas été trouvé
			this.projetCharge = null;
			this.traiterErreur();
			ErrorManager.getInstance().display("ERR","ERR_Fichier_DP_Non_Trouve");
		}
	}

	/**
	 * Recherche et ouvre le fichier de nom fileName dans le fichier zip
	 */
	private DataInputStream findData(String fileName) throws IOException
	{	
		ZipInputStream zipFile = new ZipInputStream( new FileInputStream(new File(mFile.getAbsolutePath())));
		ZipEntry zipEntry = zipFile.getNextEntry();
		while( zipEntry != null )
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(zipFile) );
			if( zipEntry.getName().equals(fileName) )
			{
				return data;
			}
			else
			{
				zipEntry = zipFile.getNextEntry();
			}
		}
		zipFile.close();
		return null;
	}

	public Projet getProjetCharge()
	{
		return this.projetCharge;
	}
	
	//-------------------------------------------------------------
	// Monitored task
	//-------------------------------------------------------------

	protected Object processingTask()
	{
		this.chargerDP();
		return null;
	}
	
	public void setTask( TaskMonitorDialog task )
	{
		this.mTask = task;
	}
	
	/**
	 * Print a new message to the TaskMonitorDialog
	 * 
	 * @param msg
	 */
	private void print( String msg )
	{
		setMessage(msg);
		if( mTask != null )
		{
			mTask.forceRefresh();
		}
	}
	
	//-------------------------------------------------------------
	// Differents chargements
	//-------------------------------------------------------------

	private void chargerProprietes() {
		// Chargement des proprietes dans la définition de processus
		String expression = "//proprietes";
		NodeList liste = xp.ListeNoeuds(document, expression);
		Node noeudProprietes = liste.item(0);
		dp.setAuteur(xp.valeur(noeudProprietes,"auteur"));
		dp.setCommentaires(xp.valeur(noeudProprietes,"commentaires"));
		dp.setNomDefProc(xp.valeur(noeudProprietes,"definition"));
		dp.setEmailAuteur(xp.valeur(noeudProprietes,"email"));
		zoom = Double.valueOf(xp.valeur(noeudProprietes,"zoom")).doubleValue();
	}

	private void chargerComposants() {
		// Charge les composants
		NodeList liste = xp.ListeNoeuds(document, "//composant");
		if(liste != null){
		   for(int i=0; i<liste.getLength(); i++){
			   chargerComposant(liste.item(i));
		   }
		}
	}
	
	private void chargerComposant(Node composant) {
		// Charge les composants
		long identifiant = Long.valueOf(xp.valeur(composant,"@id")).longValue();
		String nom = xp.valeur(composant,"nom").replace("#27", "'");
		String fichier = xp.valeur(composant,"fichier");
		int positionx= Integer.valueOf(xp.valeur(composant,"positionx")).intValue();
		int positiongeneration= Integer.valueOf(xp.valeur(composant,"positiongeneration")).intValue();
		int positiony= Integer.valueOf(xp.valeur(composant,"positiony")).intValue();
		int largeur= Integer.valueOf(xp.valeur(composant,"largeur")).intValue();
		int hauteur= Integer.valueOf(xp.valeur(composant,"hauteur")).intValue();
		String imageprod = xp.valeur(composant,"imageprod");
		
		// On recupere l'identifiant du composant dans le referentiel a l'aide du nom de fichier
		// Il peut etre different de celui du fixhier xml si de nouveau .iepp on été ajouté au projet
		long idc = Application.getApplication().getReferentiel().getLongFromNom(fichier);
		
		if(idc==-1) {
			// Le composant n'existe pas
			JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("Integrite_Composant")+nom+" "+Application.getApplication().getTraduction("Integrite_Composant_Manquant"),
				      "Error",
				      JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		ElementReferentiel eltRef = Application.getApplication().getReferentiel().chercherElement(idc, ElementReferentiel.COMPOSANT);
		
		// On charge le composant
		ChargeurComposantChargement ccc = new ChargeurComposantChargement(eltRef.getChemin());
		
		ccc.chargerComposant(this.projetCharge);			

		ComposantProcessus comp = ccc.getComposantCharge();
		
		if(eltRef.getType()==ElementReferentiel.COMPOSANT) {
			HashMap presentation = ccc.getMapPresentation();
			comp.initialiser(presentation);
		}
		else {
			comp.setNomComposant(Application.getApplication().getReferentiel().extraireNomFichier(comp.getNomFichier()));
		}
		
		IdObjetModele id = comp.getIdComposant();
		
		Application.getApplication().getReferentiel().ajouterReferenceMemoire(comp, idc);

		Application.getApplication().getReferentiel().chargerComposant(idc);
		
		dp.ajouterComposantChargement(comp);
		
		// Pour remplir la future liste des éléments à génrérer
		listegen.put(new Integer(positiongeneration), id);
		
		AjouterComposantGraphe(identifiant, id, nom, positionx, positiony, largeur, hauteur, imageprod);
	}
	
	private void AjouterComposantGraphe(long id_origine, IdObjetModele composant, String nom, int positionx, int positiony, int largeur, int hauteur, String imagecomp) {
		Map AllAttrubiteCell = GraphConstants.createMap();
		
		// déselectionner tous les éléments
		fenetre.getVueDPGraphe().clearSelection();
		fenetre.getVueDPGraphe().setSelectionCells(null);
		
		// Construire la vue associée au composant
		ComposantCell composantCell = new ComposantCell(composant,positionx, positiony, largeur, hauteur, imagecomp);
		
		fenetre.getVueDPGraphe().ajouterCell(composantCell);
		
		AllAttrubiteCell.put(composantCell,composantCell.getAttributs());
		
		// Récupération des produits en entrée du composant
		Vector prod_entree = ((ComposantProcessus)composant.getRef()).getProduitEntree();
		
		// On recupere les produits entree du composant dans le fichier iepp
		NodeList node_entree = xp.ListeNoeuds(document, "//produitsentree[identifiant='"+id_origine+"']");
		Vector vector_entree = new Vector();
		for(int v=0 ; v<node_entree.getLength() ; v++) {
			vector_entree.add(xp.valeur(node_entree.item(v), "nom").replace("#27", "'"));
		}
		for (int i=0; i < prod_entree.size(); i++)
		{
			IdObjetModele prod = (IdObjetModele)prod_entree.elementAt(i);
			// On realise le teste d'integrite
			String nom_prod = prod.getRef().toString(prod.getNumRang(), prod.getNumType());
			if(vector_entree.contains(nom_prod)) {
				// Le produit existe dans la sauvegarde
				AjouterProduitEntreeGraphe(composantCell, prod, id_origine);
				vector_entree.remove(nom_prod);
			} else {
				// Le produit est nouveau
				AjouterProduitEntreeGraphe(composantCell, prod, id_origine);
			}
		}
		if(!vector_entree.isEmpty()) {
			// Il reste des produits dans le fichier de sauvegarde qui ne sont pas affichés.
			for(int w=0 ; w<vector_entree.size() ; w++) {
				String nomc = (String) vector_entree.elementAt(w);
				String v = xp.valeur(document, "//produitfusionentree[nom='"+nomc+"']/nom");
				if(v=="") {
					// Le produit n'apparaisasait pas dans un produit de fusion
					JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("Integrite_Produit")+nomc+" "+Application.getApplication().getTraduction("Integrite_Produit_Manquant"),
						      "Warning",
						      JOptionPane.WARNING_MESSAGE);
				} else {
					// Il etait fusionne
					JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("Integrite_Produit")+nomc+" "+Application.getApplication().getTraduction("Integrite_ProduitFusion_Manquant"),
						      "Error",
						      JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		// On recupere les produits sortie du composant dans le fichier iepp
		NodeList node_sortie = xp.ListeNoeuds(document, "//produitssortie[identifiant='"+id_origine+"']");
		Vector vector_sortie = new Vector();
		for(int v=0 ; v<node_sortie.getLength() ; v++) {
			vector_sortie.add(xp.valeur(node_sortie.item(v), "nom").replace("#27", "'"));
		}
		// Récupération des produits en sortie du composant
		Vector prod_sortie = ((ComposantProcessus)composant.getRef()).getProduitSortie();
		for (int i=0; i < prod_sortie.size(); i++)
		{
			IdObjetModele prod = (IdObjetModele)prod_sortie.elementAt(i);
			// On realise le teste d'integrite
			String nom_prod = prod.getRef().toString(prod.getNumRang(), prod.getNumType());
			if(vector_sortie.contains(nom_prod)) {
				// Le produit existe dans la sauvegarde
				AjouterProduitSortieGraphe(composantCell, (IdObjetModele)prod_sortie.elementAt(i), id_origine);
				vector_sortie.remove(nom_prod);
			} else {
				// Le produit est nouveau
				AjouterProduitSortieGraphe(composantCell, prod, id_origine);
			}
		}
		if(!vector_sortie.isEmpty()) {
			// Il reste des produits dans le fichier de sauvegarde qui ne sont pas affichés.
			for(int w=0 ; w<vector_sortie.size() ; w++) {
				String nomc = (String) vector_sortie.elementAt(w);
				String v = xp.valeur(document, "//produitfusionentree[nom='"+nomc+"']/nom");
				if(v=="") {
					// Le produit n'apparaisasait pas dans un produit de fusion
					JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("Integrite_Produit")+nomc+" "+Application.getApplication().getTraduction("Integrite_Produit_Manquant"),
						      "Warning",
						      JOptionPane.WARNING_MESSAGE);
				} else {
					// Il etait fusionne
					JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("Integrite_Produit")+nomc+" "+Application.getApplication().getTraduction("Integrite_ProduitFusion_Manquant"),
						      "Error",
						      JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		if (!fenetre.getVueDPGraphe().getModel().contains(composantCell)){
			 fenetre.getVueDPGraphe().getModel().insert(new Object[]{composantCell}, AllAttrubiteCell, null, null,null );
		 }
		 else{
			 fenetre.getVueDPGraphe().getModel().insert(null, AllAttrubiteCell, null, null,null );
		 }
		//fenetre.getVueDPGraphe().getModel().insert(new Object[]{composantCell}, AllAttrubiteCell, null, null,null );
		fenetre.getVueDPGraphe().setSelectionCells(fenetre.getVueDPGraphe().getVectorSelectionCells().toArray());
	}
	
	private void AjouterProduitEntreeGraphe(ComposantCell composantCell, IdObjetModele prod, long id_origine) {
		Map AllAttrubiteCell = GraphConstants.createMap();
		
		String nom_prod = prod.getRef().toString(prod.getNumRang(), prod.getNumType());
		
		Node produit = xp.Noeud(document, "//produitsentree[identifiant="+id_origine+"][nom='"+nom_prod.replace("'", "#27")+"']");
		ProduitCellEntree produitCell;
		boolean masque;
		if(produit==null) {
			JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("Integrite_Produit")+prod.getRef().toString(prod.getNumRang(), prod.getNumType())+" "+Application.getApplication().getTraduction("Integrite_Nouveau")+" "+composantCell.getNomCompCell()+" "+Application.getApplication().getTraduction("Integrite_Nouveau2"),
				      "Warning",
				      JOptionPane.WARNING_MESSAGE);
			produitCell = new ProduitCellEntree(prod, 0, 0, composantCell);
			masque=false;
		}
		else {
			int positionx= Integer.valueOf(xp.valeur(produit,"positionx")).intValue();
			int positiony= Integer.valueOf(xp.valeur(produit,"positiony")).intValue();
			int largeur= Integer.valueOf(xp.valeur(produit,"largeur")).intValue();
			int hauteur= Integer.valueOf(xp.valeur(produit,"hauteur")).intValue();
			String imageprod = xp.valeur(produit,"imageprod");
			masque = new Boolean(xp.valeur(produit,"masque")).booleanValue();
			produitCell = new ProduitCellEntree(prod,positionx,positiony,composantCell, largeur, hauteur,imageprod);
		}
		
		fenetre.getVueDPGraphe().ajouterCell(produitCell);
		
		AllAttrubiteCell.put(produitCell,produitCell.getAttributs());
		
		ArrayList pointsAncrage = getPointsAncrage(produitCell,composantCell);
		
		// Liaison du produit avec le composant
		CLierInterface c = new CLierInterface(fenetre.getVueDPGraphe(),
												  pointsAncrage,
												  produitCell,
												  composantCell);
		c.executer();
		
		if(masque) {
			produitCell.masquer();
			fenetre.getVueDPGraphe().MasquerCellule(produitCell);
		}
	}

	private void AjouterProduitSortieGraphe(ComposantCell composantCell, IdObjetModele prod, long id_origine) {
		Map AllAttrubiteCell = GraphConstants.createMap();
		
		String nom_prod = prod.getRef().toString(prod.getNumRang(), prod.getNumType());
		
		// Probleme, le nom retourné reviens avec un espace a la fin !
		while (nom_prod.endsWith(" ")) {
			nom_prod = nom_prod.substring(0,nom_prod.length()-1);
		}
		Node produit = xp.Noeud(document, "//produitssortie[identifiant="+id_origine+"][nom='"+nom_prod.replace("'", "#27")+"']");
		ProduitCellSortie produitCell;
		boolean masque;
		if(produit==null) {
			JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("Integrite_Produit")+prod.getRef().toString(prod.getNumRang(), prod.getNumType())+" "+Application.getApplication().getTraduction("Integrite_Nouveau")+" "+composantCell.getNomCompCell()+" "+Application.getApplication().getTraduction("Integrite_Nouveau2"),
				      "Warning",
				      JOptionPane.WARNING_MESSAGE);
			produitCell = new ProduitCellSortie(prod, 0, 0, composantCell);
			masque=false;
		}
		else {
			int positionx= Integer.valueOf(xp.valeur(produit,"positionx")).intValue();
			int positiony= Integer.valueOf(xp.valeur(produit,"positiony")).intValue();
			int largeur= Integer.valueOf(xp.valeur(produit,"largeur")).intValue();
			int hauteur= Integer.valueOf(xp.valeur(produit,"hauteur")).intValue();
			String imageprod = xp.valeur(produit,"imageprod");
			masque = new Boolean(xp.valeur(produit,"masque")).booleanValue();
			
			produitCell = new ProduitCellSortie(prod,positionx,positiony,composantCell,largeur,hauteur,imageprod);
		}
		fenetre.getVueDPGraphe().ajouterCell(produitCell);
		
		AllAttrubiteCell.put(produitCell,produitCell.getAttributs());
		
		ArrayList pointsAncrage = getPointsAncrage(composantCell, produitCell);
		
		// Liaison du produit avec le composant
		CLierInterface c = new CLierInterface(fenetre.getVueDPGraphe(),
												  pointsAncrage,
												  composantCell,
												  produitCell);
		c.executer();
		
		if(masque) {
			produitCell.masquer();
			fenetre.getVueDPGraphe().MasquerCellule(produitCell);
		}
	}
	
	private void chargerNotes() {
		NodeList liste = xp.ListeNoeuds(document, "//note");
		if(liste != null){
			for(int i=0; i<liste.getLength(); i++){
			   chargerNote(liste.item(i));
		   }
		}
	}

	private void chargerNote(Node noeud) {
		Map AllAttrubiteCell = GraphConstants.createMap();
		
		String texte = xp.valeur(noeud,"texte").replace("#27", "'");
		int positionx= Integer.valueOf(xp.valeur(noeud,"positionx")).intValue();
		int positiony= Integer.valueOf(xp.valeur(noeud,"positiony")).intValue();
		int largeur= Integer.valueOf(xp.valeur(noeud,"largeur")).intValue();
		int hauteur= Integer.valueOf(xp.valeur(noeud,"hauteur")).intValue();
		
		TextCell textCell = new TextCell(texte,positionx,positiony,largeur,hauteur);
		
		AllAttrubiteCell.put(textCell,textCell.getAttributs());
		
		fenetre.getVueDPGraphe().getModel().insert(new Object[]{textCell}, AllAttrubiteCell, null, null,null );
		
		fenetre.getVueDPGraphe().ajouterCell(textCell);
	}
	
	private void chargerProduitsFusion() {
		NodeList liste = xp.ListeNoeuds(document, "//produitsfusion");
		if(liste != null){
			for(int i=0; i<liste.getLength(); i++){
				chargerProduitFusion(liste.item(i));
		   }
		}
	}
	
	private void chargerProduitFusion(Node noeud) {
		long identifiant = Long.valueOf(xp.valeur(noeud,"identifiant")).longValue();
		String nom = xp.valeur(noeud,"nom").replace("#27", "'");
		int positionx= Integer.valueOf(xp.valeur(noeud,"positionx")).intValue();
		int positiony= Integer.valueOf(xp.valeur(noeud,"positiony")).intValue();
		int largeur= Integer.valueOf(xp.valeur(noeud,"largeur")).intValue();
		int hauteur= Integer.valueOf(xp.valeur(noeud,"hauteur")).intValue();
		String imageprod = xp.valeur(noeud,"imageprod");
		
		NodeList produits = xp.ListeNoeuds(document, "//produitsfusion[nom='"+nom+"']/produitfusion/produitfusionentree");
		ProduitCell prodcell1;
		Node pc1;
		Vector vecprod1 = new Vector();
		// On retire les produits qu'y n'existe pas
		for(int nb1=0 ; nb1<produits.getLength(); nb1++) {
			pc1 = produits.item(nb1);
			String nomCompPc1 = xp.valeur(pc1,"composantproduit");
			String nomProdPc1 = xp.valeur(pc1,"nom");
			prodcell1 = fenetre.getVueDPGraphe().chercherProduit(nomCompPc1, nomProdPc1);
			if(prodcell1!=null) {
				vecprod1.add(prodcell1);
			}
		}
		if(vecprod1.size()==0) {
			// Il n'y a pas de produits entree (ils n'existent plus)
			return;
		}
		prodcell1 = (ProduitCell)vecprod1.elementAt(0);
		
		NodeList produits2 = xp.ListeNoeuds(document, "//produitsfusion[nom='"+nom+"']/produitfusion/produitfusionsortie");
		Node pc2 = produits2.item(0);
		String nomCompPc2 = xp.valeur(pc2,"composantproduit");
		String nomProdPc2 = xp.valeur(pc2,"nom");
		ProduitCell prodcell2 = fenetre.getVueDPGraphe().chercherProduit(nomCompPc2, nomProdPc2);
		if (prodcell2==null) {
			// Le produit n'existe plus
			return;
		}
		
		Object cellSrc = prodcell1;
		Object cellDes = prodcell2;

		IdObjetModele src = prodcell1.getId();
		IdObjetModele dest = prodcell2.getId();
		IdObjetModele fusion;
			
		Object cellEnt = null;
		Object cellSor = null;

		if (((cellSrc instanceof ProduitCellEntree) && (cellDes instanceof ProduitCellSortie))
				|| (cellSrc instanceof ProduitCellSortie)
				&& (cellDes instanceof ProduitCellEntree)) {
			// verif ke les 2 soit un produit de type differents
				if (cellDes instanceof ProduitCellEntree) {
				cellEnt = cellDes;
				cellSor = cellSrc;
			} else {
				cellEnt = cellSrc;
				cellSor = cellDes;
			}
			if (src.estProduitSortie())
			{
				fusion = src;
			}
			else
			{
				fusion = dest;
			}
			
			ProduitCellFusion newProdCell = new ProduitCellFusion(fusion,(ProduitCellEntree)cellEnt,(ProduitCellSortie)cellSor, nom, positionx, positiony, largeur, hauteur, imageprod);
			
			ArrayList pointsAncrage1 = getPointsAncrage(((ProduitCellEntree)cellEnt).getCompParent(),newProdCell);
			ArrayList pointsAncrage2 = getPointsAncrage(newProdCell,((ProduitCellSortie)cellSor).getCompParent());
			
			LienEdgeFusion edge1 = new LienEdgeFusion(((ProduitCellSortie) cellSor).getCompParent(), newProdCell, pointsAncrage2);
			LienEdgeFusion edge2 = new LienEdgeFusion(newProdCell, ((ProduitCellEntree) cellEnt).getCompParent(), pointsAncrage1);
			
			newProdCell.ajoutLien(edge1);
			newProdCell.ajoutLien(edge2);
			
			// On supprime les liens et on declare les cellules liees
			((ProduitCellEntree)cellEnt).setCellLiee(true);
			((ProduitCellSortie)cellSor).setCellLiee(true);
			
			fenetre.getVueDPGraphe().MasquerCellule((IeppCell)cellEnt);
			fenetre.getVueDPGraphe().MasquerCellule((IeppCell)cellSor);
				
			fenetre.getVueDPGraphe().ajouterCell(newProdCell);
			fenetre.getVueDPGraphe().ajouterLien(edge1);
			fenetre.getVueDPGraphe().ajouterLien(edge2);
				
			Map AllAttribute = GraphConstants.createMap();
			
			AllAttribute.put(edge1, edge1.getEdgeAttribute());
			AllAttribute.put(edge2, edge2.getEdgeAttribute());
			AllAttribute.put(newProdCell, newProdCell.getAttributs());
			
			DefaultPort portS = ((ProduitCellSortie) cellSor).getCompParent().getPortComp();
			DefaultPort portDInt = ((ProduitCellFusion) newProdCell).getPortComp();
			DefaultPort portD = ((ProduitCellEntree) cellEnt).getCompParent().getPortComp();

			ConnectionSet cs1 = new ConnectionSet(edge1, portS,	portDInt);
			ConnectionSet cs2 = new ConnectionSet(edge2, portDInt, portD);
			
			this.lienModele1 = new LienProduits((((ProduitCellEntree)cellEnt).getId()), (((ProduitCellSortie)cellSor).getId()), edge1);
			((ComposantProcessus)this.lienModele1.getProduitSortie().getRef()).ajouterLien(this.lienModele1);
			
			Vector vecObj = new Vector();
			vecObj.add(newProdCell);
			vecObj.add(edge1);
			vecObj.add(edge2);
			
			fenetre.getVueDPGraphe().getModel().insert(vecObj.toArray(), AllAttribute,
				null, null, null);
			fenetre.getVueDPGraphe().getModel().insert(null, null, cs1, null, null);
			fenetre.getVueDPGraphe().getModel().insert(null, null, cs2, null, null);
			
			for(int k=1 ; k<vecprod1.size(); k++) {
				IeppCell prodcell3 = (ProduitCell)vecprod1.elementAt(k);
				
				newProdCell.AjoutProduitCellEntree((ProduitCellEntree) prodcell3);
				
				ArrayList pointsAncrage3 = getPointsAncrage(((ProduitCellEntree)prodcell3).getCompParent(),newProdCell);
				
				LienEdgeFusion edge3 = new LienEdgeFusion(newProdCell, ((ProduitCellEntree) prodcell3).getCompParent(), pointsAncrage3);
				
				newProdCell.ajoutLien(edge3);
				
				// On supprime les liens et on declare les cellules liees
				((ProduitCellEntree)prodcell3).setCellLiee(true);
				
				fenetre.getVueDPGraphe().MasquerCellule((IeppCell)prodcell3);
					
				fenetre.getVueDPGraphe().ajouterLien(edge3);
					
				Map AllAttribute2 = GraphConstants.createMap();
				
				AllAttribute2.put(edge3, edge3.getEdgeAttribute());
				AllAttribute2.put(newProdCell, newProdCell.getAttributs());
				
				DefaultPort portDe = ((ProduitCellEntree) prodcell3).getCompParent().getPortComp();

				ConnectionSet cs3 = new ConnectionSet(edge3, portDInt, portDe);
				
				this.lienModele1 = new LienProduits((prodcell3.getId()), (newProdCell.getId()), edge3);
				((ComposantProcessus)this.lienModele1.getProduitSortie().getRef()).ajouterLien(this.lienModele1);

				Vector vecObj3 = new Vector();
				vecObj3.add(edge3);
				
				fenetre.getVueDPGraphe().getModel().insert(vecObj3.toArray(), AllAttribute2,
					null, null, null);
				fenetre.getVueDPGraphe().getModel().insert(null, null, cs3, null, null);
				
			}
			fenetre.getVueDPGraphe().repaint();
		}
		else {
			System.out.println("Chargement d'un produit lie avec plus de deux produits non réalisable");
		}
	}
	
	private ArrayList getPointsAncrage(IeppCell cell1, IeppCell cell2) {
		ArrayList pointsAncrage = new ArrayList();
		Node n;
		
		if (cell1 instanceof TextCell) {
			int x = ((TextCell)cell1).getAbscisse();
			int y = ((TextCell)cell1).getOrdonnee();
			NodeList liste = null;
			if(cell2 instanceof ProduitCellFusion) {
				String nomproduit = ((ProduitCellFusion)cell2).getNomCompCell();
				liste = xp.ListeNoeuds(document, "//liennote[destination/type='produitfusion'][source/positionx='"+x+"'][source/positiony='"+y+"'][destination/nomproduit='"+nomproduit.replace("'", "#27")+"']/pointsancrage/point");
			}
			if(cell2 instanceof ComposantCell) {
				String nomcomposant = ((ComposantCell)cell2).getNomCompCell();
				liste = xp.ListeNoeuds(document, "//liennote[destination/type='composant'][source/positionx='"+x+"'][source/positiony='"+y+"'][destination/nomcomposant='"+nomcomposant.replace("'", "#27")+"']/pointsancrage/point");
			}
			if(cell2 instanceof ProduitCellEntree) {
				String composantproduit = ((ProduitCellEntree)cell2).getCompParent().getNomCompCell();
				String nomproduit = ((ProduitCellEntree)cell2).getNomCompCell();
				liste = xp.ListeNoeuds(document, "//liennote[destination/type='produitentree'][source/positionx='"+x+"'][source/positiony='"+y+"'][destination/nomproduit='"+nomproduit.replace("'", "#27")+"'][destination/composantproduit='"+composantproduit.replace("'", "#27")+"']/pointsancrage/point");
			}
			if(cell2 instanceof ProduitCellSortie) {
				String composantproduit = ((ProduitCellSortie)cell2).getCompParent().getNomCompCell();
				String nomproduit = ((ProduitCellSortie)cell2).getNomCompCell();
				liste = xp.ListeNoeuds(document, "//liennote[destination/type='produitsortie'][source/positionx='"+x+"'][source/positiony='"+y+"'][destination/nomproduit='"+nomproduit.replace("'", "#27")+"'][destination/composantproduit='"+composantproduit.replace("'", "#27")+"']/pointsancrage/point");
			}
			if((liste != null) && (liste.getLength()>0)){
				for(int i=0; i<liste.getLength(); i++){
					n = liste.item(i);
					pointsAncrage.add(new Point(new Double(xp.valeur(n,"x")).intValue(),new Double(xp.valeur(n,"y")).intValue()));
				}
			}
		} else if (cell2 instanceof TextCell) {
			int x = ((TextCell)cell2).getAbscisse();
			int y = ((TextCell)cell2).getOrdonnee();
			NodeList liste = null;
			if(cell1 instanceof ProduitCellFusion) {
				String nomproduit = ((ProduitCellFusion)cell1).getNomCompCell();
				liste = xp.ListeNoeuds(document, "//liennote[source/type='produitfusion'][destination/positionx='"+x+"'][destination/positiony='"+y+"'][source/nomproduit='"+nomproduit.replace("'", "#27")+"']/pointsancrage/point");
			}
			if(cell1 instanceof ComposantCell) {
				String nomcomposant = ((ComposantCell)cell1).getNomCompCell();
				liste = xp.ListeNoeuds(document, "//liennote[source/type='composant'][destination/positionx='"+x+"'][destination/positiony='"+y+"'][source/nomcomposant='"+nomcomposant.replace("'", "#27")+"']/pointsancrage/point");
			}
			if(cell1 instanceof ProduitCellEntree) {
				String composantproduit = ((ProduitCellEntree)cell1).getCompParent().getNomCompCell();
				String nomproduit = ((ProduitCellEntree)cell1).getNomCompCell();
				liste = xp.ListeNoeuds(document, "//liennote[source/type='produitentree'][destination/positionx='"+x+"'][destination/positiony='"+y+"'][source/nomproduit='"+nomproduit.replace("'", "#27")+"'][source/composantproduit='"+composantproduit.replace("'", "#27")+"']/pointsancrage/point");
			}
			if(cell1 instanceof ProduitCellSortie) {
				String composantproduit = ((ProduitCellSortie)cell1).getCompParent().getNomCompCell();
				String nomproduit = ((ProduitCellSortie)cell1).getNomCompCell();
				liste = xp.ListeNoeuds(document, "//liennote[source/type='produitsortie'][destination/positionx='"+x+"'][destination/positiony='"+y+"'][source/nomproduit='"+nomproduit.replace("'", "#27")+"'][source/composantproduit='"+composantproduit.replace("'", "#27")+"']/pointsancrage/point");
			}
			if((liste != null) && (liste.getLength()>0)){
				for(int i=0; i<liste.getLength(); i++){
					n = liste.item(i);
					pointsAncrage.add(new Point(new Double(xp.valeur(n,"x")).intValue(),new Double(xp.valeur(n,"y")).intValue()));
				}
			}
		} else if(cell1 instanceof ProduitCellFusion) {
			String nomproduit = ((ProduitCellFusion)cell1).getNomCompCell();
			String nomcomposant = ((ComposantCell)cell2).getNomCompCell();
			NodeList liste = xp.ListeNoeuds(document, "//lienfusion[source/type='composant'][destination/nomproduit='"+nomproduit.replace("'", "#27")+"'][source/nomcomposant='"+nomcomposant.replace("'", "#27")+"']/pointsancrage/point");
			if((liste != null) && (liste.getLength()>0)){
				for(int i=0; i<liste.getLength(); i++){
					n = liste.item(i);
					pointsAncrage.add(new Point(new Double(xp.valeur(n,"x")).intValue(),new Double(xp.valeur(n,"y")).intValue()));
				}
			}
		} else if(cell2 instanceof ProduitCellFusion) {
			String nomcomposant = ((ComposantCell)cell1).getNomCompCell();
			String nomproduit = ((ProduitCellFusion)cell2).getNomCompCell();
			NodeList liste = xp.ListeNoeuds(document, "//lienfusion[destination/type='composant'][source/nomproduit='"+nomproduit.replace("'", "#27")+"'][destination/nomcomposant='"+nomcomposant.replace("'", "#27")+"']/pointsancrage/point");
			if((liste != null) && (liste.getLength()>0)){
				for(int i=0; i<liste.getLength(); i++){
					n = liste.item(i);
					pointsAncrage.add(new Point(new Double(xp.valeur(n,"x")).intValue(),new Double(xp.valeur(n,"y")).intValue()));
				}
			}
		} else if(cell1 instanceof ProduitCellEntree) {
			String nomproduit = ((ProduitCellEntree)cell1).getNomCompCell();
			String nomcomposant = ((ComposantCell)cell2).getNomCompCell();
			NodeList liste = xp.ListeNoeuds(document, "//lien[source/type='produitentree'][destination/type='composant'][source/nomproduit='"+nomproduit.replace("'", "#27")+"'][destination/nomcomposant='"+nomcomposant.replace("'", "#27")+"']/pointsancrage/point");
			if((liste != null) && (liste.getLength()>0)){
				for(int i=0; i<liste.getLength(); i++){
					n = liste.item(i);
					pointsAncrage.add(new Point(new Double(xp.valeur(n,"x")).intValue(),new Double(xp.valeur(n,"y")).intValue()));
				}
			}
		} else if(cell2 instanceof ProduitCellSortie) {
			String nomcomposant = ((ComposantCell)cell1).getNomCompCell();
			String nomproduit = ((ProduitCellSortie)cell2).getNomCompCell();
			NodeList liste = xp.ListeNoeuds(document, "//lien[source/type='composant'][destination/type='produitsortie'][destination/nomproduit='"+nomproduit.replace("'", "#27")+"'][source/nomcomposant='"+nomcomposant.replace("'", "#27")+"']/pointsancrage/point");
			if((liste != null) && (liste.getLength()>0)){
				for(int i=0; i<liste.getLength(); i++){
					n = liste.item(i);
					pointsAncrage.add(new Point(new Double(xp.valeur(n,"x")).intValue(),new Double(xp.valeur(n,"y")).intValue()));
				}
			}
		}

		return pointsAncrage;
	}

	private void chargerLiensNotes() {
		NodeList liste = xp.ListeNoeuds(document, "//liennote");
		if(liste != null){
			for(int i=0; i<liste.getLength(); i++){
			   chargerLienNote(liste.item(i));
		   }
		}
	}

	private void chargerLienNote(Node noeud) {
		String texte, cell;
		if(xp.valeur(noeud,"source/type").equals("textcell")) {
			texte = "source";
			cell = "destination";
		}
		else {
			cell = "source";
			texte = "destination";
		}
		int positionxnote= Integer.valueOf(xp.valeur(noeud,texte+"/positionx")).intValue();
		int positionynote= Integer.valueOf(xp.valeur(noeud,texte+"/positiony")).intValue();
		
		TextCell tc = fenetre.getVueDPGraphe().chercherTextCell(positionxnote,positionynote);
		
		String type= xp.valeur(noeud,cell+"/type");
		
		if(type.equals("produitfusion")) {
			String nomprod =  xp.valeur(noeud,cell+"/nomproduit");
			ProduitCellFusion pcf = fenetre.getVueDPGraphe().chercherProduitFusion(nomprod);
			
			if(pcf==null) {
				// La cellule n'existe pas
				return;
			}
			
			ArrayList pointsAncrage;
			if(texte == "source"){
				pointsAncrage = getPointsAncrage(tc, pcf);
			}
			else {
				pointsAncrage = getPointsAncrage(pcf, tc);
			}
			
			AjouterLienNote(pointsAncrage,tc,pcf);
		}
		if(type.equals("composant")) {
			String nomprod =  xp.valeur(noeud,cell+"/nomcomposant");
			ComposantCell cc = fenetre.getVueDPGraphe().chercherComposant(nomprod);
			
			if(cc==null) {
				// La cellule n'existe pas
				return;
			}
			
			ArrayList pointsAncrage;
			if(texte == "source"){
				pointsAncrage = getPointsAncrage(tc, cc);
			}
			else {
				pointsAncrage = getPointsAncrage(cc, tc);
			}
			
			AjouterLienNote(pointsAncrage,tc,cc);
		}
		if(type.equals("produitentree")) {
			String nomcomp =  xp.valeur(noeud,cell+"/composantproduit");
			String nomprod =  xp.valeur(noeud,cell+"/nomproduit");
			ProduitCell pcs = fenetre.getVueDPGraphe().chercherProduitEntree(nomcomp,nomprod);
			
			if(pcs==null) {
				// La cellule n'existe pas
				return;
			}
			
			ArrayList pointsAncrage;
			if(texte == "source"){
				pointsAncrage = getPointsAncrage(tc, (ProduitCellEntree)pcs);
			}
			else {
				pointsAncrage = getPointsAncrage((ProduitCellEntree)pcs, tc);
			}
			
			AjouterLienNote(pointsAncrage,tc,pcs);
		}
		if(type.equals("produitsortie")) {
			String nomcomp =  xp.valeur(noeud,cell+"/composantproduit");
			String nomprod =  xp.valeur(noeud,cell+"/nomproduit");
			ProduitCell pcs = fenetre.getVueDPGraphe().chercherProduitSortie(nomcomp,nomprod);
			
			if(pcs==null) {
				// La cellule n'existe pas
				return;
			}
			
			ArrayList pointsAncrage;
			if(texte == "source"){
				pointsAncrage = getPointsAncrage(tc, (ProduitCellSortie)pcs);
			}
			else {
				pointsAncrage = getPointsAncrage((ProduitCellSortie)pcs, tc);
			}
			
			AjouterLienNote(pointsAncrage,tc,pcs);
		}
	}

	private void AjouterLienNote(ArrayList pointsAncrage, TextCell tc, IeppCell ic) {
		LienEdgeNote edge = new LienEdgeNote(ic,tc,pointsAncrage);
		ic.ajoutLien(edge);
		tc.ajoutLien(edge);
		
		edge.setSourceEdge(ic);
		edge.setDestination(tc);
		
		fenetre.getVueDPGraphe().ajouterLien(edge);
		Map AllAttribute = GraphConstants.createMap();
		
		AllAttribute.put(edge, edge.getEdgeAttribute());
		
		DefaultPort portS = ic.getPortComp();
		DefaultPort portD = tc.getPortComp();
		
		ConnectionSet cs1 = new ConnectionSet(edge, portS,portD);
		
		Vector vecObj = new Vector();
		vecObj.add(edge);
		
		fenetre.getVueDPGraphe().getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		fenetre.getVueDPGraphe().getModel().insert(null, null, cs1, null, null);
		
		
		fenetre.getVueDPGraphe().repaint();
	}

	private void chargerPaquetages() {
		NodeList liste = xp.ListeNoeuds(document, "//generation/paquetages/paquetage");
		if(liste != null){
			for(int i=0; i<liste.getLength(); i++){
				chargerPaquetage(liste.item(i));
		   }
		}
	}

	private void chargerPaquetage(Node noeud) {
		String fichier = xp.valeur(noeud,"@file");
		int positiongeneration= Integer.valueOf(xp.valeur(noeud,"positiongenerationpaquetage")).intValue();
		
		Vector listePaquetage = Application.getApplication().getReferentiel().getListeNom(ElementReferentiel.PRESENTATION);
		
		Referentiel ref = Application.getApplication().getReferentiel();
		boolean trouve = false;
		PaquetagePresentation paq = null;
		PaquetagePresentation paq_trouve = null;
		for (int i = 0;i<listePaquetage.size();i++)
		{
			String nomPaqPre = listePaquetage.elementAt(i).toString();
			long id= ref.nomPresentationToId(nomPaqPre);
			//System.out.println("id du paq:"+id);
			paq = ref.chargerPresentation(id);
			
			File f = new File(paq.getNomFichier());
			if(fichier.equals(f.getName())) {
				trouve = true;
				paq_trouve = paq;
			}
		}
		if(!trouve) {
			JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("Integrite_Paquetage")+fichier.replace(".pre", "")+" "+Application.getApplication().getTraduction("Integrite_Composant_Manquant"),
				      "Error",
				      JOptionPane.ERROR_MESSAGE);
			return;
		}
		else {
			listegen.put(new Integer(positiongeneration), paq_trouve);
		}
	}
	
	private void chargerRoles() {
		Vector listeComposant=this.dp.getListeComp();
		
		// Construction d'une hashmap avec tous les roles
		HashMap r = new HashMap();
	    for(int i=0; i<listeComposant.size(); i++)
	    {
	    	IdObjetModele iocomp=(IdObjetModele)listeComposant.elementAt(i);
		      
	    	Vector role=iocomp.getRole();
	    	for(int j=0; j<role.size(); j++){
	    		IdObjetModele iorole = (IdObjetModele) role.elementAt(j);
	    		r.put(iorole.toString()+"##"+iocomp.toString(), iorole);
	    	}
	    }
	    NodeList liste = xp.ListeNoeuds(document, "//generation/rolesgeneration/rolegeneration");
		if(liste != null){
			for(int i=0; i<liste.getLength(); i++){
				chargerRole(liste.item(i), r);
		   }
		}
	}

	private void chargerRole(Node noeud, HashMap r) {
		String role1 = xp.valeur(noeud,"role1").replace("#27", "'");
		String comprole1 = xp.valeur(noeud,"comprole1").replace("#27", "'");
		String role2 = xp.valeur(noeud,"role2").replace("#27", "'");
		String comprole2 = xp.valeur(noeud,"comprole2").replace("#27", "'");
		
		// On vérifie que c'est pour le bon composant
		IdObjetModele iorole1 = (IdObjetModele)r.get(role1+"##"+comprole1);
		if(iorole1==null) {
			return;
		}
		IdObjetModele iorole2 = (IdObjetModele)r.get(role2+"##"+comprole2);
		if(iorole2==null) {
			return;
		}
		this.rolegen.put(iorole1, iorole2);
	}
	
	private void chargerProprietesGeneration() {
	    String rep;
	    
	    rep = xp.valeur(document, "//generation/CommentaireDP").replace("#27", "'");
	    this.dp.setCommentaires(rep);
	    
	    rep = xp.valeur(document, "//generation/ContenuDesc").replace("#27", "'");
	    this.dp.setFicContenu(rep);
	    
	    rep = xp.valeur(document, "//generation/PiedPageDP").replace("#27", "'");
	    this.dp.setPiedPage(rep);
	}
}
