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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


import util.ErrorManager;
import util.SimpleFileFilter;
import util.SmartChooser;
import iepp.Application;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;

/**
 * 
 */
public class CEnregistrerInterface extends CommandeNonAnnulable 
{
	/**
	 * Id du composant dont on veut sauvegarder les interfaces
	 */
	private IdObjetModele idComp ;
	
	protected SimpleFileFilter filter = new SimpleFileFilter("apes", "APES File");
																 
	
	public CEnregistrerInterface(IdObjetModele id)
	{
	  	this.idComp = id ;
	}
	
	
	public boolean executer()
	{
		SmartChooser chooser = SmartChooser.getChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		//Ouverture de la boite de dialogue
		 if (chooser.showSaveDialog(Application.getApplication().getFenetrePrincipale()) !=  JFileChooser.APPROVE_OPTION)
		 {
			 return false;
		 }
		 String selected_file=chooser.getSelectedFile().getAbsolutePath();
		 // vérifier qu'il y a bien l'extension
		  if (! chooser.getSelectedFile().getAbsolutePath().endsWith(".APES")
				 && ! chooser.getSelectedFile().getAbsolutePath().endsWith(".apes"))
		  {
			 selected_file += ".apes";
		  }
		 File fic = new File(selected_file);
		 // vérifier si le fichier existe déjà, auquel cas on demande confirmation pour l'écrasement
		 if(fic.exists())
		 {
			 int choice = JOptionPane.showConfirmDialog( Application.getApplication().getFenetrePrincipale(),
														 Application.getApplication().getTraduction("msgConfirmEcrasement") ,
														 Application.getApplication().getTraduction("msgSauvegarde"),
														 JOptionPane.YES_NO_OPTION,
														 JOptionPane.QUESTION_MESSAGE);

			 if(choice!=JOptionPane.YES_OPTION)
			 {
				 return false;
			 }
		 }
		 
		 return ( this.sauvegarderInterface(selected_file));
	}
	
	public boolean sauvegarderInterface(String filePath)
	{
		try
		{
			FileOutputStream outstream = new FileOutputStream( new File( filePath ) );
			ZipOutputStream zipFile = new ZipOutputStream( outstream );
			
			String entryFile = "Interfaces.xml";
			ZipEntry  entryZip = new ZipEntry( entryFile );
			zipFile.putNextEntry( entryZip );	
			
			//DataOutputStream data = new DataOutputStream( new BufferedOutputStream(zipFile));
			OutputStreamWriter data = new OutputStreamWriter( new BufferedOutputStream(zipFile), "UTF-16");

			data.write("<?apes2 version=\"0.1\"?>\n");
			data.write("<Interfaces>\n");
			data.write("\t<ProvidedInterface>\n");
			
			//ApesProcess ap = Context.getInstance().getProject().getProcess();
			//ApesProcess.Interface in = ap.getProvidedInterface(0);
			Vector sortie = ((ComposantProcessus)idComp.getRef()).getProduitSortie();
			for( int i = 0; i < sortie.size(); i++ )
			{
				IdObjetModele courant = (IdObjetModele)sortie.elementAt(i);
				data.write("\t\t<WorkProductRef>\n");
				data.write("\t\t\t<WorkProduct name=\"");				
				data.write(courant.toString() + "\"/>\n");
				data.write("\t\t</WorkProductRef>\n");
			}
			
			data.write("\t</ProvidedInterface>\n");
			data.write("\t<RequiredInterface>\n");
			
			Vector entree = ((ComposantProcessus)idComp.getRef()).getProduitEntree();
			for( int i = 0; i < entree.size(); i++ )
			{
				IdObjetModele courant = (IdObjetModele)entree.elementAt(i);
				data.write("\t\t<WorkProductRef>\n");
				data.write("\t\t\t<WorkProduct name=\"");				
				data.write(courant.toString() + "\"/>\n");
				data.write("\t\t</WorkProductRef>\n");
			}
			
			data.write("\t</RequiredInterface>\n");
			data.write("</Interfaces>\n");

			data.close();
			zipFile.close();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			ErrorManager.getInstance().display(t);
			return false;
		}
		return true;
	}
	
}
