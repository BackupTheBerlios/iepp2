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
package iepp.application.ageneration;


import iepp.Application;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.ElementPresentation;
import iepp.domaine.Guide;
import iepp.domaine.IdObjetModele;
import iepp.domaine.PaquetagePresentation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;
import util.TransformationXHTML;


/**
 * Classe permettant de gérer la génération de tout un composant publiable
 */
public class GComposantPubliable  extends GElementModele
{


	/**
	 * Lien vers le fichier tree.js à construire durant la génération
	 */
	private PrintWriter pwFicTree ;

	/**
	 * Paquetage de présentation associé au composant
	 */
	private PaquetagePresentation paquetage ;


	/**
	 * Id du composant que l'on publie
	 */
	private IdObjetModele composant ;



	/**
	 * Constructeur du gestionnaire de publication du composant publiable
	 * @param idComposant id du composant (modèle) à générer
	 * @param pwFicTree lien vers le fichier tree.js à remplir
	 */
	public GComposantPubliable (IdObjetModele idComposant, ElementPresentation elem,  PrintWriter pwFicTree)
	{
		super (elem, pwFicTree);
		this.composant = idComposant;
		this.paquetage = this.composant.getPaquetagePresentation();
	}

	/**
	 * Traitement commun à tous les éléments à générer
	 * ecriture dans l'arbre et création du fichier de contenu
	 * @param feuille, indique si l'élément courant est une feuille ou non
	 * @param id
	 */
	public void traiterGeneration(long id) throws IOException
	{
		GenerationManager.print(Application.getApplication().getTraduction("traitement_comp") + element.getNomPresentation());

		this.IDParent = id;
		// créer le répertoire
		this.creerRep();
		// récupérer les icones et les contenuts pour chaque paquetage
		this.extraireIconeContenu(this.paquetage);
		// on écrit dans l'arbre
		this.ecrireArbre();
		// on crée le fichier correspondant
		this.creerFichierDescription();
	}

	/**
	 *
	 */
	public void creerRep()
	{
		super.creerRep();
		// Création du dossier contenu
		File rep = new File(this.cheminParent + File.separator + GenerationManager.CONTENU_PATH  );
		rep.mkdirs();

		// Création du dossier images
                //modif 2XMI pour la demande de SPWIZ : ajout de GenerationManager.CONTENU_PATH +
                rep = new File(this.cheminParent + File.separator + GenerationManager.CONTENU_PATH + File.separator + GenerationManager.IMAGES_PATH );
		rep.mkdirs();
	}


	/**
	 * Méthode permettant de créer le contenu de la page associée au composant courant
	 * Affiche selon les options de la génération, des listes ou des diagrammes
	 */
	public void creerFichierDescription() throws IOException
	{
		// création du fichier HTML
		File ficHTML = new File (this.cheminAbsolu) ;
		FileWriter fd = new FileWriter(ficHTML);

		fd.write("<HTML><head>");
		fd.write("<link rel='STYLESHEET' type='text/css' href='" + this.getCheminStyle() + "'>");
		fd.write("</head>" + "<body><center>\n"
				+ "<table width=\"84%\" align=\"center\">\n"
				+ "<tr><td width=\"100%\" class=\"titrePage\">\n"
				+ "<p align=\"center\" class=\"entete\">\n"
				+ "<b>" + this.modele.toString() + "</b>\n"
				+ "</p></td></tr></table></center><BR>\n");

		fd.write(getBarreNavigation() + "<br>");
		// tableau
		fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_ROLES") + " </div>\n");
		Vector listeRole = this.modele.getRole();
		for (int i = 0; i < listeRole.size(); i++)
		{
			IdObjetModele id = (IdObjetModele) listeRole.elementAt(i);
			fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
		}
		fd.write("<br><br><div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_PRODUITS") + " </div>\n");
		Vector listeProduits = this.modele.getProduit();

		// Separer les produits en deux tableaux
		fd.write("<TABLE border=\"0\" width=\"100%\">");
		fd.write("<TR><TH>Produits en entr&eacute;e</TH><TH>Produits internes</TH><TH>Produits en sortie</TH></TR>");

		String entree = "";
		String internes = "";
		String sortie = "";
		String ajout;

		boolean trouve;
		boolean in;

		for (int i = 0; i < listeProduits.size(); i++)
		{
		    ajout = null;
			IdObjetModele id = (IdObjetModele) listeProduits.elementAt(i);

			trouve = false;
			in = false;

			// Chercher les produits exterieurs (sans liens)
			if (GenerationManager.estProduitExterieur(id) != 0)
			{
		        // Le produit est exterieur, il ne faut pas mettre de lien
		        trouve = true;
		        ajout = "<div class=\"elementliste\"> " + id.toString() + "</div>\n";
		        if (GenerationManager.estProduitExterieur(id) == 1)
		        {
		            in = true;
		        }
			}

			// S'ils ne sont pas exterieurs, ils n'ont peut etre pas de presentation mais sont des produits en entree lies
			HashMap listeProduitsChanges = GenerationManager.getListeProduitsChanges();
			IdObjetModele nouveau = GenerationManager.getProduitChange(id);
			if (nouveau != null)
			{
			    trouve = true;
			    in = true;
			    ajout = "<div class=\"elementliste\"><a href=\"" + this.getLienChemin(nouveau) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n";
			}

			// ou des produits en sortie qui ont une presentation
			Vector listeProduitsSortie = GenerationManager.getListeProduitsSortie();
			for (int j = 0; j < listeProduitsSortie.size() && ! trouve; j++)
			{
			    if (listeProduitsSortie.elementAt(j).toString().equals(id.getRef().toString() + "::"+ id.toString()))
			    {
			        trouve = true;
			    }
			}

			// Construire la chaine par defaut s'il n'y a pas eu de cas particulier
			if (ajout == null)
			{
			    ajout = "<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n";
			}

			if (trouve)
			{
			    if (in)
			    {
			        entree += ajout;
			    }
			    else
			    {
				    sortie += ajout;
				}
			}
			else
			{
			    // ou enfin des produits internes
			    internes += ajout;
			}
		}
		// Ecrire le tableau les liens
		fd.write("<TR><TD valign=top>"+ entree +"</TD><TD valign=top>"+ internes +"</TD><TD valign=top>"+ sortie +"</TD></TR>");
		fd.write("</TABLE>");

		fd.write("<br><br><div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_DEFINITIONS") + " </div>\n");
		Vector listeDefinition = this.modele.getDefinitionTravail();
		for (int i = 0; i < listeDefinition.size(); i++)
		{
			IdObjetModele id = (IdObjetModele) listeDefinition.elementAt(i);
			fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
		}

		// voir pour les diagrammes

                //modif 2XMI Amandine
                //affichage de la liste de guides
                this.ajouterGuidesAssocies(fd);
                //modif 2XMI Sébastien
                //ajout ds la page du chemin relatif
                File f=new File(GenerationManager.getInstance().getCheminGeneration());
                fd.write("<span class=\"cheminRelatif\" id=\""+ficHTML.getAbsolutePath().substring(f.getAbsolutePath().length()+1, ficHTML.getAbsolutePath().length())+"\"></span>");
                //fin modif 2XMI Sébastien

                this.ajouterContenu(fd);
		this.ajouterMail(fd);

                //modif 2XMI amandine
                //affichage de la date de dernière modification d'un composant
                this.ajouterDerniereModif(fd);

		this.ajouterVersionDate(fd);
                this.ajouterPiedPage(fd);
		fd.write("</BODY></HTML>") ;
		fd.close();
                //modif 2XMI Sébastien
                //Transformation du fichier en XHTML pour permettre l'export en PDF
                TransformationXHTML transformateur = new TransformationXHTML();
                if( !transformateur.executer(ficHTML,this.getFichierDtd()))
                {
                    ArbreGeneration.listeErreurs.add(Application.getApplication().getTraduction("ERR_Composant_XHTML") + " - " + this.element.getNomPresentation() + " :\n" + transformateur.getErreur());
                }
                //fin modif Sébastien

	}

	/**
	 *
	 */
	public void recenser()
	{
		Integer oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbComposants");
		ArbreGeneration.mapCompteur.put("nbComposants", new Integer(oldValue.intValue() + 1));

		oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbPagesTotal");
		ArbreGeneration.mapCompteur.put("nbPagesTotal", new Integer(oldValue.intValue() + 1));
	}
}




