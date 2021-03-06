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
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ipsquad.apes.model.spem.process.components.ProcessComponent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import util.ErrorManager;
import util.MonitoredTaskBase;
import util.TaskMonitorDialog;

import JSX.ObjIn;

import iepp.Application;
import iepp.Projet;
import iepp.domaine.*;

/**
 * Classe permettant de charger un composant publiable ou composant vide au format XML
 * Les 3 fichiers XML (component, interface et presentation ) doivent �tre obligatoirement dans le zip
 * sinon ce n'est pas un composant publiable
 */
public class ChargeurComposantChargement
{

	/**
	 * Composant publiable a charger
	 */
	private ComposantProcessus cp = null;

	/**
	 * Boite de dialogue permettant d'afficher l'avancement des t�ches
	 */
	private TaskMonitorDialog mTask = null;

	/**
	 * Nom du fichier  charger
	 */
	private String nomFic = null ;

	private String nomComposant = null;

	private HashMap listePresentation = null ;

	/**
	 * Constructeur du chargeur
	 */
	public ChargeurComposantChargement(String nomFic)
	{
		this.nomFic = nomFic ;
		this.listePresentation = new HashMap() ;
	}

	/**
	 * Indique si le fichier component.xml a �t� trouv�
	 */
	private boolean componentTrouve = false ;

	/**
	 * Indique si le fichier interface.xml a �t� trouv�
	 */
	private boolean interfaceTrouve = false ;

	/**
	 * Charge un composant publiable � partir d'un fichier ZIP contenant 3 fichiers XML
	 * - Component.xml (outil mod�lisation) contient tout le contenu d'un composant de processus
	 * - Interfaces.xml (outil mod�lisation) contient l'interface d'un composant
	 * - Presentation.xml (outil presentation) contient la liste des �l�ments de pr�sentation
	 */
	public void chargerComposant (Projet projet)
	{
		// V�rifier qu'une d�finition de processus est charg�e
		// Pas de message d'erreur, mais on est cens� v�rifier �a dans l'interface
		if (projet == null)
			return ;
		// cr�ation du composant publiable
		this.cp = new ComposantProcessus(projet.getDefProc());
		this.cp.setNomFichier(this.nomFic);

		ZipInputStream zipFile = null ;
		try
		{
			// r�cup�rer un flux vers le fichier zip
			zipFile = new ZipInputStream( new FileInputStream (new File(this.nomFic)));
			chargerInterfaces(zipFile);

			// revenir au d�but du flux pour un autre parsing
			zipFile = new ZipInputStream( new FileInputStream (new File(this.nomFic)));
			chargerComposant(zipFile);

			// si on arrive ici c'est que l'interface a �t� trouv�e
			// pour que ce soit un composant vide il ne faut ni de composant ni de presentation
			if (! this.componentTrouve)
			{
				// on a un compo vide
				this.cp.setVide(true);
				return;
			}
		}
		catch (FileNotFoundException e)
		{
			// ne devrait pas arriver
			this.cp = null ;
			ErrorManager.getInstance().display("ERR","ERR_Fichier_Non_Trouve");

		}
		catch (FichierException e)
		{
			this.cp = null ;
			String fic = e.getMessage();
			ErrorManager.getInstance().display("ERR","ERR_" + fic + "_Non_Trouve");

		}
		catch (ParserConfigurationException e)
		{
			this.cp = null ;
			e.printStackTrace();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (SAXException e)
		{
			this.cp = null ;
			e.printStackTrace();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (IOException e)
		{
			this.cp = null ;
			e.printStackTrace();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		catch (ClassNotFoundException e)
		{
			this.cp = null ;
			e.printStackTrace();
			ErrorManager.getInstance().displayError(e.getMessage());
		}
		// aucune erreur
	}

	private void chargerComposant(ZipInputStream projectZip) throws IOException, ClassNotFoundException, FichierException
	{
		ZipEntry zipEntry = projectZip.getNextEntry();
		while( zipEntry != null && !this.componentTrouve)
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(projectZip) );
			if( zipEntry.getName().equals("Component.xml") )
			{
				this.componentTrouve = true ;

				ObjIn in = new ObjIn(data);
				// r�cup�rer le vecteur
				Vector v = (Vector)in.readObject();
				this.cp.setProcessComponent((ProcessComponent)v.get(0));
				this.cp.setMapDiagram((HashMap)v.get(1));
			}
			else
			{
				zipEntry = projectZip.getNextEntry();
			}
		}
		projectZip.close();
	}

	/**
	 * Permet de rajouter au composant publiable � retourner des interfaces pr�sentes dans le fichier
	 * Interfaces.xml.
	 * @param projectZip
	 * @param cp
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void chargerInterfaces(ZipInputStream projectZip) throws ParserConfigurationException, SAXException, IOException, FichierException
	{

		// r�cup�rer le premier fichier du zip
		ZipEntry zipEntry = projectZip.getNextEntry();

		// tant que l'on a pas trouv� le fichier interfaces.xml
		while( zipEntry != null && !this.interfaceTrouve )
		{
			// r�cup�rer un flux de donn�es
			DataInputStream data = new DataInputStream( new BufferedInputStream(projectZip) );
			// on v�rifie si l'on a trouv� le bon fichier
			if( zipEntry.getName().equals("Interfaces.xml") )
			{
				// on a trouv� le fichier, ok
				this.interfaceTrouve = true;

				// pr�paration du parsing du fichier xml, m�thode SAX
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser analyzer = factory.newSAXParser();

				// construction d'un r�cup�rateur d'�v�nement lors du parsing du fichier
				InterfacesHandler handler = new InterfacesHandler(this.cp);
				// lancer le parsing
				analyzer.parse( data, handler );
			}
			else
			{
				// fichier pas trouv�, passer au fichier suivant
				zipEntry = projectZip.getNextEntry();
			}
		}
		// fermer le flux vers le fichier zip
		projectZip.close();
		if (! this.interfaceTrouve)
		{
			throw new FichierException ("Interface");
		}
	}

	//-------------------------------------------------------------
	// INTERFACES
	//-------------------------------------------------------------
	/**
	 * Classe permettant de r�cup�rer les �v�nements survenus lors du parsing d'un fichier xml
	 */
	private class InterfacesHandler extends DefaultHandler
	{
		/**
		 * indique le type du produit sur lequel on est
		 */
		private int typeProduit ;

		/**
		 * composant processus auquel on va ajouter les interfaces
		 */
		private ComposantProcessus cp;

		/**
		 * Constructeur
		 * @param cp, composant publiable auquel on va ajouter les interfaces trouv�es
		 */
		public InterfacesHandler(ComposantProcessus cp)
		{
			super();
			this.cp = cp ;
		}

		/**
		 * On r�cup�re l'�v�nement "je rentre sur une nouvelle balise"
		 */
		public void startElement (String uri, String localName, String baliseName, Attributes attributes)
		{
			// agir selon le nom de la balise courante
			if(baliseName=="ProvidedInterface")
			{
				this.typeProduit = 1 ;
			}
			else if(baliseName=="RequiredInterface")
			{
				this.typeProduit = 0 ;
			}
			else if(baliseName=="WorkProductRef"){}
			else if(baliseName=="WorkProduct")
			{
				this.cp.ajouterProduit(attributes.getValue(0), this.typeProduit);
			}
		}
	}

	//-------------------------------------------------------------
	// COMPOSANT
	//-------------------------------------------------------------
	/**
	 * Classe permettant de r�cup�rer le nom de pr�sentation d'un composant
	 */
	private class ComponentHandler extends DefaultHandler
	{
		private boolean isElement = false;
		private boolean isGuide = false;
		private boolean isIdentificateur = false;
		private String baliseCourante ;
		private String valeur;

		public ComponentHandler()
		{
			ChargeurComposantChargement.this.nomComposant = null;
		}

		/**
		 * On r�cup�re l'�v�nement "je rentre sur une nouvelle balise"
		 */
		public void startElement (String uri, String localName, String baliseName, Attributes attributes)
		{
			this.valeur = "";
			this.baliseCourante = baliseName ;
			if(baliseName=="element")
			{
				// on trouve un �l�ment de pr�sentation
				this.isElement = true;
			}
			else if(baliseName=="guide")
			{
				this.isElement = false;
			}
		}

		public void endElement(String namespace, String name, String raw)
		{
			if(raw == "guide") this.isGuide = false;
			if(raw == "element") this.isElement = false;
			if (!this.valeur.trim().equals(""))
			{
				if(this.isElement)
				{
					if (this.baliseCourante.equals("nom_presentation"))
					{
						// si on se trouve dans la bonne balise
						if (this.isIdentificateur)
						{
								// alors le nom que l'on r�cup�re est le bon
								ChargeurComposantChargement.this.nomComposant = this.valeur;
								// tout le reste est ignor�
								this.isIdentificateur = false;
						}
					}
					else if (this.baliseCourante.equals("identificateur_interne"))
					{
						try
						{
							// l'identificateur interne du composant est 1
							if ((new Integer(this.valeur)).intValue() == 1)
							{
								// onse trouve dans les bonnes balises
								this.isIdentificateur = true;
							}
						}
						catch( Exception excep){}
					}
				}
			}
		}

		public void characters(char buf[], int offset, int len) throws SAXException
		{
			String valeurtmp = new String(buf, offset, len);
			this.valeur += valeurtmp; 
		}
	}

	//-------------------------------------------------------------
	// R�sultat du chargement
	//-------------------------------------------------------------
	public ComposantProcessus getComposantCharge()
	{
		return this.cp;
	}

	public HashMap getMapPresentation()
	{
		return this.listePresentation;
	}


	/**
	 * Recherche le fichier de nom fileName dans le fichier zip
	 * @return true si le fichier a bien �t� trouv�, false sinon
	 */
	public static boolean findData(String fileName, String fileZip) throws IOException
	{
		ZipInputStream zipFile = new ZipInputStream( new FileInputStream(new File(fileZip)));
		ZipEntry zipEntry = zipFile.getNextEntry();
		while( zipEntry != null )
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(zipFile) );
			if( zipEntry.getName().equals(fileName) )
			{
				return true;
			}
			else
			{
				zipEntry = zipFile.getNextEntry();
			}
		}
		zipFile.close();
		return false;
	}
}
