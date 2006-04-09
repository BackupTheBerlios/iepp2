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
import iepp.domaine.PaquetagePresentation;

import java.io.*;

/**
 * Classe permettant de gérer la publication d'un paquetage de présentation
 */
public class GPaquetagePresentation extends GElement
{

	/**
	 * Paquetage de présentation à publier
	 */
	private PaquetagePresentation paquetage ;


	/**
	 * Constructeur du gestionnaire de génération
	 * @param paquetage paquetage de présentation à publier
	 * @param pwFicTree lien vers le fichier tree.js ) remplir
	 */
	public GPaquetagePresentation (ElementPresentation elem, PaquetagePresentation paquetage , PrintWriter pwFicTree)
	{
		super (elem, pwFicTree);
		this.paquetage = paquetage ;
	}

	/**
	 * Traitement commun à tous les éléments à générer
	 * ecriture dans l'arbre et création du fichier de contenu
	 * @param feuille, indique si l'élément courant est une feuille ou non
	 * @param id
	 */
	public void traiterGeneration(long id) throws IOException
	{
		GenerationManager.print(Application.getApplication().getTraduction("traitement_paquetage") + element.getNomPresentation());

		this.IDParent = id;
		// créer le répertoire
		this.creerRep();
		// récupérer les icones et les contenuts pour chaque paquetage
		this.extraireIconeContenu(paquetage);
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
                rep = new File(this.cheminParent + File.separator + GenerationManager.CONTENU_PATH + File.separator + GenerationManager.IMAGES_PATH);
                rep.mkdirs();
              }

	/**
	 *
	 */
	public void recenser()
	{
		Integer oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbPaquetagesPresentation");
		ArbreGeneration.mapCompteur.put("nbPaquetagesPresentation", new Integer(oldValue.intValue() + 1));

		oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbPagesTotal");
		ArbreGeneration.mapCompteur.put("nbPagesTotal", new Integer(oldValue.intValue() + 1));
	}

}
