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
 
package iepp.ui;

import iepp.Application;

import java.awt.Dimension;
import java.awt.Toolkit;


import javax.swing.JDialog;


/**
 * FenetreProriete.java
 */
public abstract class FenetrePropriete extends JDialog
{
	
	public FenetrePropriete()
	{
		super(Application.getApplication().getFenetrePrincipale());
		this.setTitle(Application.getApplication().getTraduction("Propriete_DP"));
		this.setModal(true);
		this.setSize(400, 450);

	    // Centrer la fenêtre
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = this.getSize();
	    if (frameSize.height > screenSize.height)
	    {
	       frameSize.height = screenSize.height;
	    }
	    if (frameSize.width > screenSize.width)
		{ 
		   frameSize.width = screenSize.width;
		}
	    this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}
}
