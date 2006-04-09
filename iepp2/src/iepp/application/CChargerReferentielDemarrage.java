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

import java.io.File;
import java.io.FileNotFoundException;

import util.ErrorManager;

import iepp.Application;
import iepp.application.areferentiel.Referentiel;


public class CChargerReferentielDemarrage extends CommandeNonAnnulable {


	private File cheminRef;

	/**
	 *
	 */
	public CChargerReferentielDemarrage(File cheminRef)
	{
			this.cheminRef = cheminRef;
	}

	public boolean executer()
	{
		// on charge le référentiel
		Referentiel nouveau;
		try
		{
			nouveau = new Referentiel(cheminRef);
		}
		catch (Exception e)
		{
                  if (!e.getMessage().equals("referentiel_ancien_format")){
                    e.printStackTrace();
                    ErrorManager.getInstance().display("ERR","ERR_Fic_Ref_Corromp");
                  }
                  return false;
		}
		Application.getApplication().setReferentiel(nouveau);
		return true;
	}
}
