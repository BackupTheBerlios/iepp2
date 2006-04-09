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

package iepp.application;

import iepp.Application;
import iepp.domaine.PaquetagePresentation;
import iepp.domaine.DefinitionProcessus;

public class CSupprimerPresentationDP extends CommandeNonAnnulable
{

	/**
	 * Le paquetage a supprimer de la DP
	 */
	private PaquetagePresentation paquet;

	/**
	 * Constructeur avec le paquet à supprimer
	 * @param paquet
	 */
	public CSupprimerPresentationDP(PaquetagePresentation paquet)
	{
		this.paquet = paquet;
	}



        //modif 2XMI jean
        /**
         * Supprime toutes les occurences du paquetage de presentation
         * dans la listeAGenerer de la definition de processus
         * @return true
         */
        public boolean executer() {
          //modif 2XMI jean
          DefinitionProcessus dp = Application.getApplication().getProjet().getDefProc();
          PaquetagePresentation pp = dp.getPaquetageListeAGenerer(paquet.getNomPresentation());
          while (pp != null) {
            Application.getApplication().getProjet().getDefProc().retirerPresentation(pp);
            pp = dp.getPaquetageListeAGenerer(paquet.getNomPresentation());
          }
          return true;
        }
}
