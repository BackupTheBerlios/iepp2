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
import iepp.domaine.ElementPresentation;
import iepp.domaine.IdObjetModele;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;
import util.TransformationXHTML;

/**
 * Classe permettant de créer une page dont le contenu correspond à un produit
 */
public class GProduit extends GElementModele
{

	/**
	 * Constructeur du gestionnaire de génération
	 * @param elem element de présentation associé à l'activité courante
	 * @param pwFicTree lien vers le fichier tree.js construit durant la génération du site
	 */
	public GProduit(ElementPresentation elem, PrintWriter pwFicTree)
	{
		super(elem,  pwFicTree);
	}

	/**
	 * Méthode permettant de créer le contenu de la page associée à l'activité courante
	 * Affichage des listes d'activités et du rôle responsable du produit
	 */
	public void creerFichierDescription() throws IOException
	{
		// création du fichier de contenu
		File ficHTML = new File (this.cheminAbsolu) ;
		FileWriter fd = new FileWriter(ficHTML);

		fd.write("<HTML><head> <link rel='STYLESHEET' type='text/css' href='" + this.getCheminStyle() + "'>"
										+ "</head>" + "<body><center>\n"
										+ "<table width=\"84%\" align=\"center\">\n"
										+ "<tr><td width=\"100%\" class=\"titrePage\">\n"
										+ "<p align=\"center\" class=\"entete\">\n"
										+ "<b>" + this.element.getNomPresentation() + "</b>\n"
										+ "</p></td></tr></table></center><BR>\n");

		fd.write(getBarreNavigation() + "<br>");

		// affiche les activités dont le produit est en entrée
		fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_ENTREE_ACT") + " </div>\n");
		Vector listeActivites = GenerationManager.getActivitesEntree(this.modele);
		for (int i = 0; i < listeActivites.size(); i++)
		{
			IdObjetModele id = (IdObjetModele) listeActivites.elementAt(i);
			fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
		}
		fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_SORTIE_ACT") + " </div>\n");

		listeActivites = GenerationManager.getActivitesSortie(this.modele);
		for (int i = 0; i < listeActivites.size(); i++)
		{
			IdObjetModele id = (IdObjetModele) listeActivites.elementAt(i);
			fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
		}

		//affiche le rôle responsable de l'activité
		 fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_ROLE_RESP") + "</div>\n");
		 IdObjetModele id = this.modele.getRoleResponsable();
		 if (id != null)
		 {
			 fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
		 }

                //modif 2XMI Amandine
                //affichage de la liste de guides
                this.ajouterGuidesAssocies(fd);

                this.ajouterDescription(fd);
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
                //si erreur on ajoute l'erreur ds la liste
                {
                    ArbreGeneration.listeErreurs.add(Application.getApplication().getTraduction("ERR_Produit_XHTML") + " - " + this.element.getNomPresentation() + " :\n" + transformateur.getErreur());
                }
                //fin modif Sébastien
	}

	/**
	 *
	 */
	public void recenser()
	{
		Integer oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbProduits");
		ArbreGeneration.mapCompteur.put("nbProduits", new Integer(oldValue.intValue() + 1));

		oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbPagesTotal");
		ArbreGeneration.mapCompteur.put("nbPagesTotal", new Integer(oldValue.intValue() + 1));

		Vector oldVecteur = (Vector)ArbreGeneration.mapRecap.get("produits");
		oldVecteur.addElement(this);
	}

}
