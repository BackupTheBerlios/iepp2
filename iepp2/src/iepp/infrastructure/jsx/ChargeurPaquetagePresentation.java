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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import util.ErrorManager;
import util.MonitoredTaskBase;
import util.TaskMonitorDialog;

import iepp.Application;
import iepp.domaine.*;

/**
 * Classe permettant de charger un composant publiable au format XML
 * Les 3 fichiers XML (component, interface et presentation ) doivent être obligatoirement dans le zip
 * sinon ce n'est pas un composant publiable
 */
public class ChargeurPaquetagePresentation extends MonitoredTaskBase
{
	/**
	 * Boite de dialogue permettant d'afficher l'avancement des tâches
	 */
	private TaskMonitorDialog mTask = null;

	/**
	 * Nom du fichier  charger
	 */
	private String nomFic = null ;

	/**
	 * Constructeur du chargeur
	 */
	public ChargeurPaquetagePresentation(String nomFic)
	{
		this.nomFic = nomFic ;
	}

	/**
	 * Indique si le fichier component.xml a été trouvé
	 */
	private boolean componentTrouve = false ;

	/**
	 * Indique si le fichier interface.xml a été trouvé
	 */
	private boolean interfaceTrouve = false ;

	/**
	 * Indique si le fichier component.xml a été trouvé
	 */
	private boolean presentationTrouve = false ;

	/**
	 * Nom du paquetage de présentation
	 */
	private String nomPresentation = null;


	/**
	 * Paquetage de présentation à charger
	 */
	private PaquetagePresentation paquetage = null ;


	public void chargerPaquetagePresentation ()
	{
		this.print(Application.getApplication().getTraduction("Initialisation_chargement"));

		ZipInputStream zipFile = null ;
		try
		{
			// verifier qu'il n'y ait que le fichier presentation.xml et non les autres
			zipFile = new ZipInputStream( new FileInputStream (new File(this.nomFic)));
			this.verifierPaquetage(zipFile);

			// récupérer un flux vers le fichier zip
			// revenir au début du flux pour un dernier parsing
			 zipFile = new ZipInputStream( new FileInputStream (new File(this.nomFic)));
			 chargerPresentation(zipFile);

			this.print("------------------------------------");
			this.print(Application.getApplication().getTraduction("paquetage_succes"));

		}
		catch (FileNotFoundException e)
		{
			// ne devrait pas arriver
			this.traiterErreur();
			ErrorManager.getInstance().display("ERR","ERR_Fichier_Non_Trouve");

		}
		catch (FichierException e)
		{
			String fic = e.getMessage();
			this.traiterErreur();
			if (! fic.equals("Presentation"))
			{
				ErrorManager.getInstance().display("ERR","ERR_" + fic + "_Trouve");
			}
			else
			{
				ErrorManager.getInstance().display("ERR","ERR_" + fic + "_Pas_Trouve");
			}

		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());

		}
		catch (SAXException e)
		{
			e.printStackTrace();
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.traiterErreur();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
	}

	/**
	 * @param zipFile
	 */
	private void verifierPaquetage(ZipInputStream zipFile) throws IOException, FichierException
	{
		// parcourir tout le zip pour voir s'il n'y a pas des fichier xml en trop
		ZipEntry zipEntry = zipFile.getNextEntry();
		while( zipEntry != null )
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(zipFile) );
			if( zipEntry.getName().equals("Component.xml"))
			{
				zipFile.close();
				throw new FichierException("Component");
			}
			else if( zipEntry.getName().equals("Interfaces.xml"))
			{
				zipFile.close();
				throw new FichierException("Interface");
			}
			zipEntry = zipFile.getNextEntry();
		}
		// fermeture du fichier zip
		zipFile.close();
	}


	private void chargerPresentation(ZipInputStream projectZip) throws ParserConfigurationException, SAXException, IOException, FichierException
	{
		ZipEntry zipEntry = projectZip.getNextEntry();

		while( zipEntry != null && !this.presentationTrouve )
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(projectZip) );
			if( zipEntry.getName().equals("Presentation.xml") )
			{
				this.presentationTrouve = true;
				this.print(Application.getApplication().getTraduction("SAX_initialisation"));
				// preparation du parsing
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxparser = factory.newSAXParser();
				this.print(Application.getApplication().getTraduction("presentation_parsing"));
				PresentationHandler handler = new PresentationHandler();
				saxparser.parse( data, handler );
				this.print(Application.getApplication().getTraduction("Presentation_charge"));
			}
			else
			{
				zipEntry = projectZip.getNextEntry();
			}
		}
		projectZip.close();
		if (! this.presentationTrouve )
		{
			throw new FichierException ("Presentation");
		}
	}


	//-------------------------------------------------------------
	// PRESENTATION
	//-------------------------------------------------------------
	/**
	 * Classe permettant de récupérer les évènements survenus lors du parsing d'un fichier xml
	 */
	private class PresentationHandler extends DefaultHandler
	{

		private boolean isProprietes = false;
		private boolean isElement = false;
		private boolean isGuide = false;
		private ElementPresentation element = null;
		private Guide guide = null;
		private String baliseCourante ;


		/**
		 * On récupère l'évènement "je rentre sur une nouvelle balise"
		 */
		public void startElement (String uri, String localName, String baliseName, Attributes attributes)
		{
			this.baliseCourante = baliseName ;
			if(baliseName=="element")
			{
				// on trouve un élément de présentation
				this.element = new ElementPresentation();
				ChargeurPaquetagePresentation.this.paquetage.ajouterElement(this.element);
				this.isElement = true;
			}
			else if(baliseName=="exportation_presentation")
			{
				ChargeurPaquetagePresentation.this.paquetage = new PaquetagePresentation(ChargeurPaquetagePresentation.this.nomFic);
			}
			else if(baliseName=="proprietes")
			{
				this.isProprietes = true;
			}
			else if(baliseName=="guide")
			{
				this.isGuide = true;
				this.isElement = false;
				this.guide = new Guide();
				ChargeurPaquetagePresentation.this.paquetage.ajouterElement(this.guide);
				this.element.ajouterGuide(this.guide);
			}
		}

		public void endElement(String namespace, String name, String raw)
		{
			if(raw == "proprietes") this.isProprietes = false;
			if(raw == "guide") this.isGuide = false;
			if(raw == "element") this.isElement = false;
		}

		public void characters(char buf[], int offset, int len) throws SAXException
		{
			String valeur = new String(buf, offset, len);
			if (!valeur.trim().equals(""))
			{
				if(this.isProprietes)
				{
					if (this.baliseCourante.equals("nom_presentation"))
						ChargeurPaquetagePresentation.this.paquetage.setNomPresentation(valeur);
					else if (this.baliseCourante.equals("auteur")){
						ChargeurPaquetagePresentation.this.paquetage.setAuteur(valeur);
					}
					else if (this.baliseCourante.equals("email")){
						ChargeurPaquetagePresentation.this.paquetage.setMail(valeur);
					}
					else if (this.baliseCourante.equals("version")){
						ChargeurPaquetagePresentation.this.paquetage.setVersion(valeur);
					}
					else if (this.baliseCourante.equals("lastexport")){
						ChargeurPaquetagePresentation.this.paquetage.setLastExport(valeur);
					}
					else if (this.baliseCourante.equals("chemin_contenus"))
					{
						if ( valeur.endsWith("/") || valeur.endsWith("\\"))
						{
							valeur = valeur.substring(0, valeur.length()-1);
						}
						ChargeurPaquetagePresentation.this.paquetage.setCheminContenu(valeur);
					}

					else if (this.baliseCourante.equals("chemin_icones"))
					{
						if ( valeur.endsWith("/") || valeur.endsWith("\\"))
						{
							valeur = valeur.substring(0, valeur.length()-1);
						}
						ChargeurPaquetagePresentation.this.paquetage.setCheminIcone(valeur);
					}
				}
				else if(this.isElement)
				{

					if (this.baliseCourante.equals("nom_presentation")) {
						this.element.setNomPresentation(valeur);
					}
					else if (this.baliseCourante.equals("identificateur_externe"))
						this.element.setIdExterne(new Integer(valeur).intValue());
					else if (this.baliseCourante.equals("identificateur_interne"))
						this.element.setIdInterne(valeur);
					else if (this.baliseCourante.equals("icone"))
						this.element.setIcone(valeur);
					else if (this.baliseCourante.equals("contenu"))
						this.element.setContenu(valeur);
					else if (this.baliseCourante.equals("description"))
						this.element.setDescription(valeur);
					//modif 2XMI amandine
					else if (this.baliseCourante.equals("typeProduit")){
						this.element.setTypeProduit(valeur);
					}
					//fin modif 2XMI amandine
				}
				else if(this.isGuide)
				{
					if (this.baliseCourante.equals("nom_presentation"))
						this.guide.setNomPresentation(valeur);
					else if (this.baliseCourante.equals("identificateur_externe"))
						this.guide.setIdExterne(new Integer(valeur).intValue());
					else if (this.baliseCourante.equals("identificateur_interne"))
						this.guide.setIdInterne(valeur);
					else if (this.baliseCourante.equals("icone"))
						this.guide.setIcone(valeur);
					else if (this.baliseCourante.equals("contenu"))
						this.guide.setContenu(valeur);
					else if (this.baliseCourante.equals("type"))
						this.guide.setType(valeur);
					else if (this.baliseCourante.equals("description"))
						this.guide.setDescription(valeur);
				}
			}
		}
	}

	public PaquetagePresentation getPaquetageCharge()
	{
		return this.paquetage ;
	}


	//-------------------------------------------------------------
	// Monitored task
	//-------------------------------------------------------------
	/*
	 * @see util.MonitoredTaskBase#processingTask()
	 */
	protected Object processingTask()
	{
		this.chargerPaquetagePresentation();
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

	/**
	 * @param chemin
	 * @return
	 * @throws FichierException
	 */
	public String chercherNomPresentation(String projectZip) throws FichierException
	{
	    boolean fini = false;
		this.presentationTrouve = false;
		ZipInputStream zipFile = null ;
		try
		{
			// récupérer un flux vers le fichier zip
		    zipFile = new ZipInputStream( new FileInputStream (new File(projectZip)));

		    // Verifier qu'il s'agisse bien d'un paquetage de presentation
		    this.verifierPaquetage(zipFile);
		    zipFile.close();

			zipFile = new ZipInputStream( new FileInputStream (new File(projectZip)));
			ZipEntry zipEntry = zipFile.getNextEntry();
			while( zipEntry != null && !this.presentationTrouve )
			{
				DataInputStream data = new DataInputStream( new BufferedInputStream(zipFile) );
				if( zipEntry.getName().equals("Presentation.xml") )
				{
					this.presentationTrouve = true;
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxparser = factory.newSAXParser();
					PaquetagePresentationHandler handler = new PaquetagePresentationHandler();
					saxparser.parse( data, handler );
				}
				else
				{
					zipEntry = zipFile.getNextEntry();
				}
			}
			zipFile.close();
			fini = true;
		}
		catch (FichierException e)
		{
			String fic = e.getMessage();
			this.traiterErreur();
			if (! fic.equals("Presentation"))
			{
				ErrorManager.getInstance().display("ERR","ERR_" + fic + "_Trouve");
			}
			else
			{
				ErrorManager.getInstance().display("ERR","ERR_" + fic + "_Pas_Trouve");
			}

		}
		catch (FileNotFoundException e)
		{
			// ne devrait pas arriver
			this.presentationTrouve = false;
			this.traiterErreur();
			ErrorManager.getInstance().display("ERR","ERR_Fichier_Non_Trouve");

		}
		catch (ParserConfigurationException e)
		{
			this.presentationTrouve = false;
			e.printStackTrace();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (SAXException e)
		{
			this.presentationTrouve = false;
			e.printStackTrace();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (IOException e)
		{
			this.presentationTrouve = false;
			e.printStackTrace();
			ErrorManager.getInstance().displayError(e.getMessage());
		}

		if (fini && !this.presentationTrouve)
		{
		    // Afficher une erreur
		    ErrorManager.getInstance().display("ERR","ERR_Presentation_Pas_Trouve");
		}
		return  this.nomPresentation;
	}

//	-------------------------------------------------------------
	// Paquetage de présentation
	//-------------------------------------------------------------
	/**
	 * Classe permettant de récupérer le nom de présentation d'un composant
	 */
	private class PaquetagePresentationHandler extends DefaultHandler
	{
		private boolean isProprietes = false;
		private boolean isElement = false;
		private boolean isGuide = false;
		private String baliseCourante ;

		public PaquetagePresentationHandler()
		{
			ChargeurPaquetagePresentation.this.nomPresentation = null;
		}

		/**
		 * On récupère l'évènement "je rentre sur une nouvelle balise"
		 */
		public void startElement (String uri, String localName, String baliseName, Attributes attributes)
		{
			this.baliseCourante = baliseName ;
			if(baliseName=="proprietes")
			{
				this.isProprietes = true;
			}
		}

		public void endElement(String namespace, String name, String raw)
		{
			if(raw == "proprietes") this.isProprietes = false;
		}

		public void characters(char buf[], int offset, int len) throws SAXException
		{
			String valeur = new String(buf, offset, len);
			if (!valeur.trim().equals(""))
			{
				if(this.isProprietes)
				{
					if (this.baliseCourante.equals("nom_presentation"))
					{
						ChargeurPaquetagePresentation.this.nomPresentation = valeur;
					}
				}
			}
		}
	}

}
