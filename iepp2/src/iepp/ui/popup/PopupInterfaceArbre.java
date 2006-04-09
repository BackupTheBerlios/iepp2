package iepp.ui.popup;


import iepp.Application;
import iepp.application.aedition.CAjouterProduitEntree;
import iepp.application.aedition.CAjouterProduitSortie;
import iepp.domaine.IdObjetModele;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 *
 */
public class PopupInterfaceArbre extends JPopupMenu implements ActionListener
{
	
	private IdObjetModele interfaceCourante ;
	
	private JMenuItem ajouterProduit;
	private JMenuItem proprietes ;
	
	
	public PopupInterfaceArbre(IdObjetModele inter)
	{
		this.interfaceCourante = inter ;
		
		this.ajouterProduit = new JMenuItem(Application.getApplication().getTraduction("Ajouter_produit"));
		
		if (inter.estComposantVide())
		{
			this.add(this.ajouterProduit);
		} 
		
		this.ajouterProduit.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		   if (event.getSource() == this.ajouterProduit)
		   {
		   		if (this.interfaceCourante.estInterfaceFournie())
		   		{
					CAjouterProduitSortie c = new CAjouterProduitSortie (this.interfaceCourante);
					if (c.executer())
					{
					   		Application.getApplication().getProjet().setModified(true);
					}
		   		}
			    else
			    {
					CAjouterProduitEntree c = new CAjouterProduitEntree (this.interfaceCourante);
					if (c.executer())
					{
						Application.getApplication().getProjet().setModified(true);
					}
			    }
		   }
	 }
}
