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

package iepp.application.aedition;

import iepp.Application;
import iepp.Projet;
import iepp.application.CommandeNonAnnulable;
import iepp.ui.iedition.VueDPGraphe;

/**
 * Classe permettant d'effectuer un zoom sur le diagramme d'assemblage
 *
 */
public class CZoomer extends CommandeNonAnnulable 
{
	/**
	 * Facteur du zoom à appliquer lors de la transformation
	 */
	private double facteurZoom ; 
	
	
	/**
	 * Constructeur du zoom à partir du facteur à appliquer
	 * @param facteur facteur de zoom à appliquer
	 */
	public CZoomer (double facteur)
	{
		this.facteurZoom = facteur;
	}
	
	/**
	 * Effectue le zoome si une DP est en cours d'édition
	 */
	public boolean executer()
	{
		// récupérer les projet en cours
		Projet p = Application.getApplication().getProjet();
		// s'il y en a effectivement un
		if (p != null)
		{
			VueDPGraphe vue = p.getFenetreEdition().getVueDPGraphe();
			
			vue.setScale(vue.getScale()*this.facteurZoom);
			
			//vue.getModele().setFacteurZoom(this.facteurZoom);
			
			vue.repaint();
		}
		return false;
	}
}