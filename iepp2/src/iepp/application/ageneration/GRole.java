
package iepp.application.ageneration;

import iepp.Application;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.ElementPresentation;
import iepp.domaine.IdObjetModele;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import util.TransformationXHTML;

/**
 * Classe permettant de créer une page dont le contenu correspond à un role
 */
public class GRole extends GElementModele
{

	/**
	 * Constructeur du gestionnaire de génération
	 * @param elem element de présentation associé à l'activité courante
	 * @param pwFicTree lien vers le fichier tree.js construit durant la génération du site
	 */
	public GRole(ElementPresentation elem, PrintWriter pwFicTree)
	{
		super(elem, pwFicTree);
	}

	/**
	 * Méthode permettant de créer le contenu de la page associée à l'activité courante
	 * Affichage des listes d'activités et de produits
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

                //modif 2XMI SR
                DefinitionProcessus defProc = Application.getApplication().getProjet().getDefProc();
                if(defProc.containsSousRole(this.modele))
                {
                  //cas ou le role est un sous role
                  IdObjetModele srole=defProc.getSuperRole(this.modele);
                  fd.write("<a href=\""+this.getLienChemin(srole)+"\" target=\"_new\" >"+ Application.getApplication().getTraduction("RoleVersSuperRole") +"</a>\n");
                } else if(Application.getApplication().getProjet().getDefProc().containsSuperRole(this.modele))
                {
                  //cas ou le role est un super role
                  ArrayList listeSousRole=defProc.getListeSousRole(this.modele);
                  Iterator iterateursousr=listeSousRole.iterator();
                  fd.write("<div class=\"titreliste\">"+ Application.getApplication().getTraduction("RoleListeSousRole") +" </div>\n");
                  while(iterateursousr.hasNext())
                  {
                    IdObjetModele idsousrole=(IdObjetModele)iterateursousr.next();
                    fd.write("<div class=\"elementliste\"><a href=\""+this.getLienChemin(idsousrole)+"\" target=\"_new\" >"+ idsousrole.toString() +"</a> - " + defProc.getComposantNameFromRole(idsousrole) + "</div>\n");
                  }
                }


		fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_ACTIVITES") + " </div>\n");

		// affiche la liste des activités dans le role est responsable
		Vector listeActivites = this.modele.getActivite();
		for (int i = 0; i < listeActivites.size(); i++)
		{
			IdObjetModele id = (IdObjetModele) listeActivites.elementAt(i);
			fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
		}
		
		fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_ACTIVITES_ASSIST") + " </div>\n");

		// affiche la liste des activités dans le role est responsable
		Vector listeActivitesAssiste = this.modele.getActiviteAssiste();
		for (int i = 0; i < listeActivitesAssiste.size(); i++)
		{
			IdObjetModele id = (IdObjetModele) listeActivitesAssiste.elementAt(i);
			fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
		}
		
		fd.write("<div class=\"titreliste\">" + Application.getApplication().getTraduction("WEB_PRODUITS") + " </div>\n");

		// affiche la liste des produits dans le role est responsable
                Vector listeProduits = this.modele.getProduit();
                for (int i = 0; i < listeProduits.size(); i++)
                {
                  IdObjetModele id = (IdObjetModele) listeProduits.elementAt(i);
                  if (GenerationManager.estProduitExterieur(id) == 0)
                  {
                    if (GenerationManager.getProduitChange(id) == null)
                    {
                      fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(id) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
                    }
                    else
                    {
                      fd.write("<div class=\"elementliste\"><a href=\"" + this.getLienChemin(GenerationManager.getProduitChange(id)) + "\" target=\"_new\" >" + id.toString() + "</a></div>\n");
                    }
                  }
                  else
                  {
                    fd.write("<div class=\"elementliste\">" + id.toString() + "</div>\n");
                  }
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
        fd.write("</BODY></HTML>");
        fd.close();
        //modif 2XMI Sébastien
        //Transformation du fichier en XHTML pour permettre l'export en PDF
        TransformationXHTML transformateur = new TransformationXHTML();
        if (!transformateur.executer(ficHTML, this.getFichierDtd()))
        //si erreur on ajoute l'erreur ds la liste
        {
            ArbreGeneration.listeErreurs.add(Application.getApplication().getTraduction("ERR_Role_XHTML") + " - " + this.element.getNomPresentation() + " :\n" + transformateur.getErreur());
        }
        //fin modif Sébastien
    }

	/**
	 *
	 */
	public void recenser()
	{
		Integer oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbRoles");
		ArbreGeneration.mapCompteur.put("nbRoles", new Integer(oldValue.intValue() + 1));

		oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbPagesTotal");
		ArbreGeneration.mapCompteur.put("nbPagesTotal", new Integer(oldValue.intValue() + 1));

		Vector oldVecteur = (Vector)ArbreGeneration.mapRecap.get("roles");
		oldVecteur.addElement(this);
	}
}
