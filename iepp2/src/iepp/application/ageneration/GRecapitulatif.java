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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class GRecapitulatif extends GElement
{

	/**
	 * Constructeur du gestionnaire de génération pour la page de statistiques
	 */
	public GRecapitulatif(PrintWriter pwFicTree)
	{
		super(null, pwFicTree);

		this.element = new ElementPresentation();
		this.element.setIcone("TreePackage.gif");
		this.element.setNomPresentation(Application.getApplication().getTraduction("WEB_RECAP"));
	}

	/**
	 * Créer le fichier de contenu d'un élément de présentation simple, sans modèle
	 */
	protected void creerFichierDescription() throws IOException
	{
		File ficHTML = new File (this.cheminAbsolu) ;
		FileWriter fd = new FileWriter(ficHTML);

		fd.write("<HTML><head> <link rel='STYLESHEET' type='text/css' href='" + this.getCheminStyle() + "'>"
								+ "</head>" + "<body><center>\n"
								+ "<table width=\"84%\" align=\"center\">\n"
								+ "<tr><td width=\"100%\" class=\"titrePage\">\n"
								+ "<p align=\"center\" class=\"entete\">\n"
								+ "<b>" + Application.getApplication().getTraduction("WEB_RECAP") + "</b>\n"
								+ "</p></td></tr></table></center><BR><BR>\n");

		this.ajouterMail(fd);
		this.ajouterVersionDate(fd);
                this.ajouterPiedPage(fd);
		fd.write("</BODY></HTML>") ;
		fd.close();

	}

	/**
	 * Méthode permettant d'écrire le code html correspondant au bouton mail à chaque bas de page
	 * @param fd lien vers le fichier html de contenu
	 * @throws IOException
	 */
	public void ajouterMail(FileWriter fd) throws IOException
	{
		fd.write("<br><hr>");
		fd.write("<div align=\"center\" class=\"boutonemail\"><a href=\"mailto:"
					+ Application.getApplication().getProjet().getDefProc().getEmailAuteur()
					+ "?subject=" + this.element.getNomPresentation() + "\">"
					+ Application.getApplication().getTraduction("WEB_MAIL") + "</A></div>");
	}

}
