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

import iepp.domaine.ElementPresentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;



/**
 * Classe permettant de créer une page dont le contenu correspond à un guide
 */
public class GGuide extends GElement
{

	/**
	 * @param elem element de présentation associé à l'activité courante
	 * @param writer lien vers le fichier tree.js construit durant la génération du site
	 */
	public GGuide(ElementPresentation elem, PrintWriter writer)
	{
		super(elem, writer);
	}

	/**
	 * Traitement commun à tous les éléments à générer
	 * ecriture dans l'arbre et création du fichier de contenu
	 * @param feuille, indique si l'élément courant est une feuille ou non
	 * @param id
	 */
	public void traiterGeneration(long id) throws IOException
	{
		super.traiterGeneration(id);
	}

	/**
	 *
	 */
	public void recenser()
	{
		Integer oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbGuides");
		ArbreGeneration.mapCompteur.put("nbGuides", new Integer(oldValue.intValue() + 1));

		oldValue = (Integer)ArbreGeneration.mapCompteur.get("nbPagesTotal");
		ArbreGeneration.mapCompteur.put("nbPagesTotal", new Integer(oldValue.intValue() + 1));

                Vector oldVecteur = (Vector)ArbreGeneration.mapRecap.get("guide");
		oldVecteur.addElement(this);
	}

}