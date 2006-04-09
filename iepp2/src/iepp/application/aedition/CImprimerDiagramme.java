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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;


import javax.swing.RepaintManager;

import util.ErrorManager;


import iepp.Application;
import iepp.application.CommandeNonAnnulable;
import iepp.ui.iedition.VueDPGraphe;

/**
 * Classe permet d'imprimer le diagramme, adapte la taille de l'image imprimée à
 * la taille de la page sur laquelle on imprime
 */
public class CImprimerDiagramme extends CommandeNonAnnulable implements Printable
{
	/**
	 * Diagramme à imprimer
	 */
	private VueDPGraphe diagramme ;
	
	/**
	 * Constructeur de la commande imprimer diagramme
	 * @param diagramme, diagramme à imprimer
	 */
	public CImprimerDiagramme(VueDPGraphe diagramme)
	{
		this.diagramme = diagramme ;
	}
	
	/**
	 * La commande renvoie si elle s'est bien passée ou non
	 * Affiche la boîte de dialogue d'impression et imprime le diagramme
	 */
	public boolean executer()
	{
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setJobName(Application.getApplication().getConfigPropriete("titre"));
		printJob.setPrintable( this );

		// affichage de la boîte de dialogue d'imprission
		if (printJob.printDialog())
		{
			try
			{
				// lancer l'impression
				printJob.print();
			}
			catch (PrinterException exception)
			{
				exception.printStackTrace();
				ErrorManager.getInstance().display("ERR", "ERR_Imprimante");
				return false;
			}
		}
		return true;
	}



	//--------------------------------------------------
	// Interface Printable	
	//--------------------------------------------------
	/**
	 * Méthode permettant de construire l'image envoyée à l'imprimante
	 */
	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException
	{
		if( pi >= 1 )
		{
			return NO_SUCH_PAGE;
		}
		// récupérer un contexte graphique
		Graphics2D g2 = (Graphics2D)g;
		
		// récupérer la zone à imprimer
		int largeur = this.diagramme.getZoneAffichage().width;
		int hauteur = this.diagramme.getZoneAffichage().height;
		
		g2.translate(pf.getImageableX(), pf.getImageableY());
		
		// trouver le facteur pour le redimensionnement à effectuer si l'image est plus grande
		// que le format page, par défaut on ne redimensionne pas
		double dx = 1;
		double dy = 1;
		if( largeur > pf.getWidth() ) 
		{
			dx = (double)(pf.getImageableWidth() / largeur );
		}
		if( hauteur > pf.getHeight() )
		{
			dy = (double)(pf.getImageableHeight() / hauteur );
		}

		// enlever le double buffering qui ne sert à rien pour l'impression
		// et prend de la place dans le spooler
		disableDoubleBuffering(this.diagramme);
		// redimensionner si besoin l'image à imprimer
		g2.scale(dx>dy ? dy : dx,dx>dy ? dy : dx);
		// imprimer le diagramme
		this.diagramme.paint(g2);
		// remettre le double buffering sur le jcomponent
		enableDoubleBuffering(this.diagramme);
		// il n'y a plus besoin du context graphique de l'imprimante
		g2.dispose();
		
		return PAGE_EXISTS;
	}
	
	/**
	 * Enleve le double buffering qui ne sert à rien pour l'impression
	 * et prend de la place dans le spooler
	 * @param c composant auquel on enlève le double buffering
	 */
	public static void disableDoubleBuffering(Component c)
	{
	  RepaintManager currentManager = RepaintManager.currentManager(c);
	  currentManager.setDoubleBufferingEnabled(false);
	}

	/**
	 * Remet le double buffering utilisé pour l'affiche du composant
	 * dans l'application
	 * @param c composant auquel on remet le double buffering
	 */
	public static void enableDoubleBuffering(Component c)
	{
	  RepaintManager currentManager = RepaintManager.currentManager(c);
	  currentManager.setDoubleBufferingEnabled(true);
	}
}
