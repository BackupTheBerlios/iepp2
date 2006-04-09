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
import iepp.domaine.ComposantProcessus;
import iepp.domaine.ElementPresentation;
import iepp.domaine.Guide;

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

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import util.ErrorManager;

	/**
	 * Classe permettant de charger un la presentation d'un composant
	 * publiable au moment de la génération du site
	 */
	public class ChargeurPresentationComposant {

		/**
		 * Composant publiable a charger
		 */
		private ComposantProcessus cp = null;

		/**
		 * Nom du fichier  charger
		 */
		private String nomFic = null ;

		private String nomComposant = null;

		private HashMap listePresentation = null ;

		/**
		 * Constructeur du chargeur
		 */
		public ChargeurPresentationComposant(String nomFic)
		{
			this.nomFic = nomFic.replace(".apes", ".pre") ;
			this.listePresentation = new HashMap() ;
		}

		/**
		 * Indique si le fichier component.xml a été trouvé
		 */
		private boolean presentationTrouve = false ;

		/**
		 * Charge la presentation d'un composant publiable à partir d'un fichier ZIP nom_com.pre
		 * - Presentation.xml (outil presentation) contient la liste des éléments de présentation
		 */
		public boolean chargerPresentationComposant (ComposantProcessus cp)
		{
			// Vérifier qu'une définition de processus est chargée
			// Pas de message d'erreur, mais on est censé vérifier ça dans l'interface
			Projet projet = Application.getApplication().getProjet() ;
			if (projet == null)
				return false;
			// création du composant publiable
			this.cp = cp;
			this.nomComposant = cp.getNomComposant();
			
			ZipInputStream zipFile = null ;
			try
			{
				// récupérer un flux vers le fichier zip
				zipFile = new ZipInputStream( new FileInputStream (new File(this.nomFic)));
				chargerPresentation(zipFile);
				return true;
			}
			catch (FileNotFoundException e)
			{
				if(!cp.estVide()) {
					// CGEnerer site avertira et arretera la generation
					return false;
				}
				
				// On signale que l'on a un composant non publiable
				int retour = JOptionPane.showConfirmDialog(null, Application.getApplication().getTraduction("ERR_ChargeurPresentation1")+" "+nomComposant+" "+Application.getApplication().getTraduction("ERR_ChargeurPresentation2")+"\n"+Application.getApplication().getTraduction("ERR_ChargeurPresentation3"),
					      "Warning",
					      JOptionPane.YES_NO_OPTION);
				if(retour==JOptionPane.OK_OPTION) {
					return true;
				}
				else {
					return false;
				}
			}
			catch (FichierException e)
			{
				this.cp = null ;
				String fic = e.getMessage();
				ErrorManager.getInstance().display("ERR","ERR_" + fic + "_Non_Trouve");
				return false;
			}
			catch (ParserConfigurationException e)
			{
				this.cp = null ;
				e.printStackTrace();
				ErrorManager.getInstance().displayError(e.getMessage());
				return false;
			}
			catch (SAXException e)
			{
				this.cp = null ;
				e.printStackTrace();
				ErrorManager.getInstance().displayError(e.getMessage());
				return false;
			}
			catch (IOException e)
			{
				this.cp = null ;
				e.printStackTrace();
				ErrorManager.getInstance().displayError(e.getMessage());
				return false;
			}
			// aucune erreur
		}

		/**
		 *
		 * @param projectZip
		 * @throws ParserConfigurationException
		 * @throws SAXException
		 * @throws IOException
		 * @throws FichierException
		 */

		private boolean chargerPresentation(ZipInputStream projectZip) throws ParserConfigurationException, SAXException, IOException, FichierException
		{
			this.presentationTrouve = false;
			ZipEntry zipEntry = projectZip.getNextEntry();

			while( zipEntry != null && !this.presentationTrouve )
			{
				DataInputStream data = new DataInputStream( new BufferedInputStream(projectZip) );
				if( zipEntry.getName().equals("Presentation.xml") )
				{
					this.presentationTrouve = true;
					// preparation du parsing
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxparser = factory.newSAXParser();
					PresentationHandler handler = new PresentationHandler(this.cp);
					saxparser.parse( data, handler );
				}
				else
				{
					zipEntry = projectZip.getNextEntry();
				}
			}
			projectZip.close();
			if(!this.presentationTrouve) {
				int retour = JOptionPane.showConfirmDialog(null, Application.getApplication().getTraduction("ChargeurPresentation1")+" "+nomComposant+" "+Application.getApplication().getTraduction("ChargeurPresentation2")+"\n"+Application.getApplication().getTraduction("ChargeurPresentation3"),
					      "Warning",
					      JOptionPane.YES_NO_OPTION);
				if(retour==JOptionPane.OK_OPTION) {
					return true;
				}
				else {
					return false;
				}
			}
			return true;
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
			private String valeur;

			private ComposantProcessus cp;
			
			private Vector ListeErreurs;
			
			public PresentationHandler(ComposantProcessus cp)
			{
				this.cp = cp ;
				ChargeurPresentationComposant.this.nomComposant = null;
				ListeErreurs = new Vector();
			}

			/**
			 * On récupère l'évènement "je rentre sur une nouvelle balise"
			 */
			public void startElement (String uri, String localName, String baliseName, Attributes attributes) {
				this.valeur="";
				this.baliseCourante = baliseName ;
				if(baliseName=="element")
				{
					// on trouve un élément de présentation
					this.element = new ElementPresentation();
					ChargeurPresentationComposant.this.cp.getPaquetage().ajouterElement(this.element);
					this.isElement = true;
				}
				else if(baliseName=="exportation_presentation")
				{
					ChargeurPresentationComposant.this.cp.getPaquetage().setNomFichier(ChargeurPresentationComposant.this.nomFic);
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
					ChargeurPresentationComposant.this.cp.getPaquetage().ajouterElement(this.guide);
					this.element.ajouterGuide(this.guide);
				}
			}

			public void endElement(String namespace, String name, String raw)
			{
				if(raw == "proprietes") this.isProprietes = false;
				if(raw == "guide") this.isGuide = false;
				if(raw == "element") this.isElement = false;

				if (!valeur.trim().equals(""))
				{
					if(this.isProprietes)
					{
						if (this.baliseCourante.equals("nom_presentation"))
						{
							ChargeurPresentationComposant.this.cp.getPaquetage().setNomPresentation(valeur);
							//ChargeurComposant.this.nomComposant = valeur;
						}
						else if (this.baliseCourante.equals("auteur")){
							ChargeurPresentationComposant.this.cp.getPaquetage().setAuteur(valeur);
						}
						else if (this.baliseCourante.equals("email")){
							ChargeurPresentationComposant.this.cp.getPaquetage().setMail(valeur);
						}
						else if (this.baliseCourante.equals("version")){
							ChargeurPresentationComposant.this.cp.getPaquetage().setVersion(valeur);
						}
						else if (this.baliseCourante.equals("lastexport")){
							ChargeurPresentationComposant.this.cp.getPaquetage().setLastExport(valeur);
						}
						else if (this.baliseCourante.equals("chemin_contenus"))
						{
							if ( valeur.endsWith("/") || valeur.endsWith("\\"))
							{
								valeur = valeur.substring(0, valeur.length()-1);
							}
							ChargeurPresentationComposant.this.cp.getPaquetage().setCheminContenu(valeur);
						}
						else if (this.baliseCourante.equals("chemin_icones"))
						{
							if ( valeur.endsWith("/") || valeur.endsWith("\\"))
							{
								valeur = valeur.substring(0, valeur.length()-1);
							}
							ChargeurPresentationComposant.this.cp.getPaquetage().setCheminIcone(valeur);
						}
					}
					else if(this.isElement)
					{

						if (this.baliseCourante.equals("nom_presentation")) {
							this.element.setNomPresentation(valeur);
						}
						else if (this.baliseCourante.equals("identificateur_externe"))
						{
							this.element.setIdExterne(new Integer(valeur).intValue());
							// rajouter l'élément dans la map des éléments de présentation
							ChargeurPresentationComposant.this.listePresentation.put(new Integer(valeur),this.element);
						}
						else if (this.baliseCourante.equals("identificateur_interne")) {
							if(valeur.startsWith("-")) { 
								if(!ListeErreurs.contains(valeur)) {
									ListeErreurs.add(valeur);
										JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("ERR_ChargeurPresentation1")+" "+ChargeurPresentationComposant.this.nomFic+" "+Application.getApplication().getTraduction("ERR_ChargeurPresentation2")+"\n"+Application.getApplication().getTraduction("ERR_ChargeurPresentation3"),
											      "Warning",
											      JOptionPane.OK_OPTION);
								}
							}
							this.element.setIdInterne(valeur);
						}
						else if (this.baliseCourante.equals("icone"))
							this.element.setIcone(valeur);
						else if (this.baliseCourante.equals("contenu"))
							this.element.setContenu(valeur);
						else if (this.baliseCourante.equals("description"))
							this.element.setDescription(valeur);
						//modif 2XMI amandine
						else if (this.baliseCourante.equals("typeproduit")) {
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

			public void characters(char buf[], int offset, int len) throws SAXException
			{
				String valeurtmp = new String(buf, offset, len);
				this.valeur += valeurtmp; 
			}
		}


		//-------------------------------------------------------------
		// Résultat du chargement
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
		 * @return true si le fichier a bien été trouvé, false sinon
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

