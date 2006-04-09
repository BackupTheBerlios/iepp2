package iepp.infrastructure.jsx;

import iepp.Application;
import iepp.Projet;
import iepp.application.aedition.CAjouterComposantGrapheDyn;
import iepp.application.aedition.CLier2ComposantDyn;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.TextCell;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jgraph.graph.GraphConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.ErrorManager;
import util.MonitoredTaskBase;
import util.TaskMonitorDialog;

/*
 * permet de charger un scenario associ? ? une definition de processus 
 */

public class ChargeurScenario extends MonitoredTaskBase {
	private File mFile = null;

	private String nomScenario;
	
	/**
	 * Projet ? charger
	 */
	private Projet projetCharge = null ;
	
	/**
	 * Boite de dialogue permettant d'afficher l'avancement des t?ches
	 */
	private TaskMonitorDialog mTask = null;
	
	private static Document document;
	
	private static XPath xpath;
	
	private FenetreEdition fenetre;
	/**
	 * Constructeur ? partir du fichier contenant la d?finition de processus ? ouvrir
	 * @param file
	 */
	public ChargeurScenario(File file)
	{
		mFile = file;
	}

	public void chargerScenario(String nom)
	{
		DataInputStream data = null;
		this.nomScenario=nom;
		try 
		{
			data = findData(nom);
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
			
			// cr?ation de la source
			//source = new InputSource(data);
			  
			DocumentBuilderFactory fabriqueDOM = DocumentBuilderFactory.newInstance();
			DocumentBuilder analyseur;
			try {
				analyseur = fabriqueDOM.newDocumentBuilder();
				document = analyseur.parse(data);
				XPathFactory fabriqueXPath = XPathFactory.newInstance();
				xpath = fabriqueXPath.newXPath();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			// On declare le projet
			this.projetCharge = Application.getApplication().getProjet();
			
			// On charge les proprietes
			chargerProprietes();
			this.print(Application.getApplication().getTraduction("liste_proprietes"));
			
			fenetre = Application.getApplication().getProjet().getFenetreEdition();
			
			// On charge les composants
			chargerComposants();
			this.print(Application.getApplication().getTraduction("liste_composants"));
			
			// on s'ocupe des liens

			chargerLiensDyn();
			this.print(Application.getApplication().getTraduction("liste_liens_dyn"));

			// on s'ocupe des produits de notes
			chargerNotes();
			this.print(Application.getApplication().getTraduction("liste_notes"));
			
			fenetre.getVueDPGraphe().clearSelection();
			
			this.projetCharge.setModified(false);
		}
		else
		{
			// le fichier definition.xml n'a pas ?t? trouv?
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
		this.chargerScenario(nomScenario);
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
	// Proc?dures pour le xpath
	//-------------------------------------------------------------
	
	public static NodeList ListeNoeuds(Object ob, String expression){
		// Renvoit la liste de noeud correspondant a l'expression xpath
		NodeList liste = null;
		try{
			//?valuation de l'expression XPath
			liste = (NodeList)xpath.evaluate(expression, ob ,XPathConstants.NODESET);
		}catch(XPathExpressionException xpee){
			xpee.printStackTrace();
		}
		return liste;
	}
	
	public static Node Noeud(Object ob, String expression){
		// Renvoit le noeud correspondant a l'expression xpath
		Node noeud = null;
		try{
			//?valuation de l'expression XPath
			noeud = (Node)xpath.evaluate(expression, ob ,XPathConstants.NODE);
		}catch(XPathExpressionException xpee){
			xpee.printStackTrace();
		}
		return noeud;
	}

	public String valeur(Object ob, String expression){
		// Renvoit la valeur d'une expression xpath
		String valeur = "";
		try{
			//?valuation de l'expression XPath
			XPathExpression e = xpath.compile(expression);
			valeur = e.evaluate(ob);
		}catch(XPathExpressionException xpee){
			xpee.printStackTrace();
		}
		return valeur;
	}
	
	//-------------------------------------------------------------
	// Differents chargements
	//-------------------------------------------------------------

	private void chargerProprietes() {
		// Chargement des proprietes dans la d?finition de processus
		String expression = "//proprietes";
		NodeList liste = ListeNoeuds(document, expression);
		Node noeudProprietes = liste.item(0);
		// creation du nouveau scenario au referentiel
		GraphModelView jg=new GraphModelView(valeur(noeudProprietes,"scenario"),false);
		jg.setCommmentaire_scenario(valeur(noeudProprietes,"commentaires"));
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel(jg);
		// Modif Julie 05.02.06 met en place le bon outil(dynamique ou statique) pour la gestion des popups
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setOutilSelection();
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().addElement(jg);
		Referentiel ref=Application.getApplication().getReferentiel();
		ref.getNoeudScenarios().add(new ElementReferentiel(jg.getNomDiagModel(), Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().size()+100, ".", 7));
		ref.majObserveurs(Referentiel.ELEMENT_INSERTED);
		Application.getApplication().getProjet().getFenetreEdition().setDynamique();
		Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().clearSelection();
		Application.getApplication().getProjet().setModified(true);
	}

	private void chargerComposants() {
		// Charge les composants
		NodeList liste = ListeNoeuds(document, "//composant");
		if(liste != null){
		   for(int i=0; i<liste.getLength(); i++){
			   chargerComposant(liste.item(i));
		   }
		}
	}
	
	private void chargerComposant(Node composant) {
		// Charge les composants
		String fichier = valeur(composant,"fichier");
		
		// On recupere l'identifiant du composant dans le referentiel a l'aide du nom de fichier
		// Il peut etre different de celui du fixhier xml si de nouveau .iepp on ?t? ajout? au projet
		long idc = Application.getApplication().getReferentiel().getLongFromNom(fichier);
		if(idc==-1) {
			// Le composant n'existe pas
			return;
		}
		
		ComposantProcessus cp = (ComposantProcessus) Application.getApplication().getReferentiel().chercherReference(idc);
		if(cp==null) { System.out.println("erreur");}
		
		IdObjetModele id = cp.getIdComposant();
		
		//AjouterComposantGraphe(identifiant, id, nom, positionx, positiony, largeur, hauteur, imageprod);
		int nbre = ((GraphModelView) Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel()).getElementCells().size();
		CAjouterComposantGrapheDyn c = new CAjouterComposantGrapheDyn(id, new Point(50+nbre*200,25));
		if (c.executer()) {
			Application.getApplication().getProjet().setModified(true);
		}
	}
	
	private void chargerNotes() {
		NodeList liste = ListeNoeuds(document, "//note");
		if(liste != null){
			for(int i=0; i<liste.getLength(); i++){
			   chargerNote(liste.item(i));
		   }
		}
	}

	private void chargerNote(Node noeud) {
		Map AllAttrubiteCell = GraphConstants.createMap();
		
		String texte = valeur(noeud,"texte").replace("#27", "'");
		int positionx= Integer.valueOf(valeur(noeud,"positionx")).intValue();
		int positiony= Integer.valueOf(valeur(noeud,"positiony")).intValue();
		int largeur= Integer.valueOf(valeur(noeud,"largeur")).intValue();
		int hauteur= Integer.valueOf(valeur(noeud,"hauteur")).intValue();
		
		TextCell textCell = new TextCell(texte,positionx,positiony,largeur,hauteur);
		
		AllAttrubiteCell.put(textCell,textCell.getAttributs());
		
		fenetre.getVueDPGraphe().getModel().insert(new Object[]{textCell}, AllAttrubiteCell, null, null,null );
		
		fenetre.getVueDPGraphe().ajouterCell(textCell);
	}
	
	private void chargerLiensDyn() {
		NodeList liste = ListeNoeuds(document, "//liendyn");
		boolean lien_existe_tjs;
		int num_lien_reel = 0;
		if(liste != null){
			for(int i=0; i<liste.getLength(); i++){
				lien_existe_tjs = chargerLienD(liste.item(i),num_lien_reel);
				if(lien_existe_tjs==true) {
					num_lien_reel++;
				}
		   }
		}
	}
	
	private boolean chargerLienD(Node noeud,int i) {
		
		String nomCompoS=valeur(noeud,"source");
		String nomCompoD=valeur(noeud,"destination");
		String valeur=valeur(noeud,"valeur");
		String image=valeur(noeud,"image");
		
		VueDPGraphe mGraph=Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		
		ComposantCellDyn compoS=mGraph.chercherComposantDyn(nomCompoS);
		ComposantCellDyn compoD=mGraph.chercherComposantDyn(nomCompoD);
		if((compoS==null) || (compoD==null)) {
			return false;
		}
		
		DocumentCellDyn s=(DocumentCellDyn)compoS.getDocuments().elementAt(i);
		DocumentCellDyn d=(DocumentCellDyn)compoD.getDocuments().elementAt(i);
		
		CLier2ComposantDyn c = new CLier2ComposantDyn(fenetre.getVueDPGraphe(), compoS,compoD,d,s, new Vector(),valeur,image);
		if (c.executer()) {
			   Application.getApplication().getProjet().setModified(true);
		}
		return true;
	}
	
}
