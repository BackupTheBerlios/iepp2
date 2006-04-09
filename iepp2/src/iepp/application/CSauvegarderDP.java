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
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.infrastructure.jsx.EnregistreurDP;
import iepp.infrastructure.jsx.EnregistreurScenario;
import iepp.ui.iedition.dessin.GraphModelView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.zip.ZipOutputStream;

import util.ErrorManager;

public class CSauvegarderDP extends CommandeNonAnnulable
{

	private String filePath = "";
	
	/**
	 * 
	 * @param path Save the current project in the specified path
	 */
	public CSauvegarderDP (String path)
	{
		this.filePath = path;
	}

	/**
	 * 
	 * @return true if the file has been saved
	 */
	public boolean executer()
	{
		try
		{
			String nomFichier = filePath ;
			// v?rifier qu'il y ait bien l'extension au fichier
			if (!filePath.endsWith(".iepp") && !filePath.endsWith(".IEPP"))
			{
				nomFichier += ".iepp";
			}
			GraphModelView courant=(GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel();
			
			FileOutputStream outstream = new FileOutputStream( new File( nomFichier) );
			BufferedOutputStream buff = new BufferedOutputStream(outstream);
			ZipOutputStream zipFile = new ZipOutputStream( buff );
			
			zipFile.setMethod(ZipOutputStream.DEFLATED);
			zipFile.setLevel(9);
			
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(0));
			
			EnregistreurDP enregistre = new EnregistreurDP(zipFile);
			enregistre.sauver();
			
			for (int i=1;i<Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().size();i++)
			{	
				Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(i));
				EnregistreurScenario enregistreS=new EnregistreurScenario(zipFile);
				enregistreS.sauver(((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel()).getNomDiagModel());
			}
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel(courant);
			
			
			zipFile.close();
			
			/*
			// Sauver tous les composants vides de la DP dans le r?f?rentiel
			Vector v = Application.getApplication().getProjet().getDefProc().getListeComp() ;
			Referentiel ref = Application.getApplication().getReferentiel() ;
			for (int i = 0 ; i <  v.size() ; i++)
			{
				ComposantProcessus comp = (ComposantProcessus) ((IdObjetModele)v.get(i)).getRef() ;
				if (comp.estVide())
					ref.sauverComposantVide (comp) ;
			}
			*/
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			ErrorManager.getInstance().displayError(t.getMessage());
			return false;
		}
		
		Application.getApplication().getProjet().setModified(false);
		return true;
	}

}
