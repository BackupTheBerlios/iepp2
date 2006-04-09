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

import java.io.IOException;
import java.io.PrintWriter;
import iepp.domaine.ElementPresentation;
import iepp.domaine.IdObjetModele;

/**
 * Classe contenant des méthodes communes à tous les éléments à publier qui sont associés
 * à un élément du modèle
 */
public class GElementModele
    extends GElement {

  /**
   * Id de l'élément du modèle auquel est associé l'élément de présentation courant
   */
  protected IdObjetModele modele;

  /**
   * @param elem element de présentation associé à l'élément courant
   * @param lien vers le fichier tree.js construit durant la génération du site
   */
  public GElementModele(ElementPresentation elem, PrintWriter pwFicTree) {
    super(elem, pwFicTree);
    this.modele = this.element.getElementModele();
  }

  /**
   * Méthode permettant de traiter les éléments de présentation liés à un élément de modèle
   */
  public void traiterGeneration(long id) throws IOException {
    // récupérer le modèle associé
    if (this.element.getID_Apes() != -1) {
      this.IDParent = id;
      // créer le répertoire
      this.creerRep();
      // on écrit dans l'arbre
      this.ecrireArbre();
      // on crée le fichier correspondant
      this.creerFichierDescription();
    }
  }

}
