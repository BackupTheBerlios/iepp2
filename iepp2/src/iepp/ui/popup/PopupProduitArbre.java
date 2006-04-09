package iepp.ui.popup;


import iepp.Application;
import iepp.domaine.IdObjetModele;
import iepp.ui.FenetreRenommerProduit;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import iepp.application.aedition.*;


/**
 *
 */
public class PopupProduitArbre extends JPopupMenu implements ActionListener
{
	
	private IdObjetModele produit ;

	private JMenuItem proprietes ;
	private JMenuItem supprimer ;
	
	
	public PopupProduitArbre(IdObjetModele produit)
	{
		this.produit = produit;
		
		if (produit.estComposantVide())
		{
			this.supprimer = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Element"));
			this.add(this.supprimer);
			this.addSeparator();
			this.proprietes = new JMenuItem(Application.getApplication().getTraduction("Proprietes"));
			this.add(this.proprietes);

			
			this.proprietes.addActionListener(this);
			this.supprimer.addActionListener(this);
		} 
	}
	
	public void actionPerformed(ActionEvent event)
	{
		   if (event.getSource() == this.proprietes)
		   {
				FenetreRenommerProduit fp = new FenetreRenommerProduit(Application.getApplication().getFenetrePrincipale(), this.produit);
				fp.pack();
				fp.show();
		   }
		   else if (event.getSource() == this.supprimer)
		   {
				if (this.produit.estProduitSortie())
				{
					CSupprimerProduit c = new CSupprimerProduit (this.produit,2);
					 if (c.executer())
					 {
					   	Application.getApplication().getProjet().setModified(true);
					 }
				}
			    else
			    {
					CSupprimerProduit c = new CSupprimerProduit (this.produit,1);
					 if (c.executer())
					 {
					   	Application.getApplication().getProjet().setModified(true);
					 }
			    }
		   }
	 }
}
