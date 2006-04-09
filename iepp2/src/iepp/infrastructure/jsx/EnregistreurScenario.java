package iepp.infrastructure.jsx;

import iepp.Application;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeDyn;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeNote;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/*
 * enregistre un sc?nario d'une d?finition de processus
 */

public class EnregistreurScenario {
	/*
	 * Fichier zip contenant la sauvegarde XML de la definition processus
	 */
	private ZipOutputStream mZipFile;
	
	private VueDPGraphe vdpg; 
	
	private GraphModelView courant=null;
	
	private String nomScenario;
	// Propriets pour le xml
	boolean statiqueTrouve;
	
	/**
	 * Constructeur ? partir du fichier zip ? remplir
	 * @param zipFile
	 */
	public EnregistreurScenario(ZipOutputStream zipFile)
	{
		mZipFile = zipFile;
	}
	
	/**
	 * Construit la sauvegarde au format XML et la met dans le fichier zip
	 * @throws IOException
	 */
	public void sauver(String nom) throws IOException
	{	
		this.nomScenario=nom;
		
		for (int i=0;i<Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().size();i++)
		{
			if (((GraphModelView) Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(i)).getNomDiagModel().equals(this.nomScenario))
			{
				this.courant=((GraphModelView) Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(i));
			}
		}
		ZipEntry entryZip = new ZipEntry(nomScenario+".xml");
		mZipFile.putNextEntry(entryZip);
		
		vdpg = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		statiqueTrouve = false;
		
		Element racine = new Element("ieppnit");
		
		//On cr?e un nouveau Document JDOM bas? sur la racine que l'on vient de cr?er
		Document document = new Document(racine);
		
		Element proprietes = sauverProprietes();
		if (proprietes.getChildren().size()>0) {
			racine.addContent(proprietes);
		}
		
		Element statique = sauverStatique();
		if (statique.getChildren().size()>0) {
			racine.addContent(statique);
		}

		// On sauve le fichier xml dans le zip
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		sortie.output(document, new DataOutputStream( new BufferedOutputStream(mZipFile)));
		
		// On ferme l'entr?e du zip
		mZipFile.closeEntry();
	}
	
	private Element sauverProprietes() {
		//On r?cup?re les propri?t?s du processus
		Element proprietes = new Element("proprietes");
		
		
		Element commentaires = new Element("commentaires");
		commentaires.setText(this.courant.getCommmentaire_scenario());
		proprietes.addContent(commentaires);
		
		
		Element scenario = new Element("scenario");
		scenario.setText(this.nomScenario);
		proprietes.addContent(scenario);
		

		
		return proprietes;		
	}
	
	private Element sauverStatique() {
		// On sauve le statique, compos? d'?l?ments, de produits fusion, de notes.
		Element dynamique = new Element("dynamique");
		
		// On sauve les composants
		Element composants = sauverComposants();
		if (composants.getChildren().size()>0) {
			dynamique.addContent(composants);
		}
		// On sauve les liens
		Element liens = sauverLiens();
		if (liens.getChildren().size()>0) {
			dynamique.addContent(liens);
		}
		// On sauvegarde les notes
		Element notes = sauverNotes();
		if (notes.getChildren().size()>0) {
			dynamique.addContent(notes);
		}
		
		return dynamique;
	}
	
	private Element sauverComposants() {
		Element composants = new Element("composants");
		
		// On sauve chaque composants
		Vector listComposants = vdpg.getComposantCellCells();
		for( int i = 0 ; i < listComposants.size() ; i++) {
			ComposantCellDyn cc = (ComposantCellDyn)listComposants.get(i);
			Element compsosant = new Element("composant");
			
			long idc = Application.getApplication().getReferentiel().chercherId( cc.getCompProc() );
			Attribute identifiant = new Attribute("id",idc+"");
			compsosant.setAttribute(identifiant);
			
			Element fichier = new Element("fichier");
			fichier.setText(Application.getApplication().getReferentiel().getFileFromId(idc));
			compsosant.addContent(fichier);

			composants.addContent(compsosant);
		}
		return composants;
	}


	private Element sauverLiens() {
		// On sauve le liens, en faisant attention au type du lien
		Element liens = new Element("liens");
		Element lien;
		
		Vector listLiens = vdpg.getLiens();
		
		for( int i = 0 ; i < listLiens.size() ; i++) {
			if( listLiens.get(i) instanceof LienEdgeNote) {
				//lien = sauverLienEdgeNote((LienEdgeNote)listLiens.get(i));
				//liens.addContent(lien);
			}
			else if( listLiens.get(i) instanceof LienEdgeDyn) {
				lien = sauverLienEdgeDyn((LienEdgeDyn)listLiens.get(i));
				liens.addContent(lien);
			}
		}
		return liens;
	}
	
	
	private Element sauverLienEdgeDyn(LienEdgeDyn lef) {
		// On sauve les liens de fusion
		Element LienEdgeDyn = new Element("liendyn");
		
			
			Element source = new Element("source");
			source.setText(((DocumentCellDyn)lef.getSourceEdge()).getComposant().getNomCompCell());
			LienEdgeDyn.addContent(source);
			
			Element destination = new Element("destination");
			destination.setText(((DocumentCellDyn)lef.getDestination()).getComposant().getNomCompCell());
			LienEdgeDyn.addContent(destination);
		
			Element valeur = new Element("valeur");
			valeur.setText(lef.getTextAssocie());
			LienEdgeDyn.addContent(valeur);
			
			Element image = new Element("image");
			image.setText(lef.getImage());
			LienEdgeDyn.addContent(image);

		return LienEdgeDyn;
	}
	
	
	private Element sauverNotes() {
		// On sauve les notes
		Element notes = new Element("notes");
		
		Vector listNotes = vdpg.getNoteCellCells();

		for( int i = 0 ; i < listNotes.size() ; i++) {
			TextCell tc = (TextCell)listNotes.get(i);
			
			Element note = new Element("note");
			
			Element nom = new Element("texte");
			nom.setText(tc.getMessage());
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
}
