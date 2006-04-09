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
import javax.swing.tree.TreePath;

/**
 * Commande non annulable rend juste le champ éditable 
 */
public class CRendreEditableArbre extends CommandeNonAnnulable
{
	/**
	 * Rend editable l'élément sélectionné s'il y en a un
	 * @return true si un élément a été edité
	 */
	public boolean executer()
	{
		// recuperer l'élément sélectionné dans l'arbre
		TreePath selected = Application.getApplication().getFenetrePrincipale().getVueDPArbre().getSelectionPath();
		// si on a bien récupérer un élément
		if(selected != null)
		{
			// on rend le champ éditable, pour commencer le renommage
			Application.getApplication().getFenetrePrincipale().getVueDPArbre().startEditingAtPath(selected);
			return true;
		}
		// impossible de récupérer un élément
		return false;
	}
}
