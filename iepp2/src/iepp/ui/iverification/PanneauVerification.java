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

package iepp.ui.iverification;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import iepp.Application;


/**
 * 
 */
public class PanneauVerification extends JSplitPane implements DocumentListener
{
	private JList listeProduitEntre = new JList() ;
	private JList listeProduitSortie =new JList();
	private JList listeComposant =new JList();
	private JButton hide_button;
	private JButton clear_button;
	private JPanel pe;
	private JPanel ps;
	private JPanel pc;
	
	public PanneauVerification(JComponent component)
	{
		super(JSplitPane.VERTICAL_SPLIT);
		
		this.setOneTouchExpandable(true);
		this.setResizeWeight(1);
		this.add(component, TOP);
		
		JPanel top_panel = new JPanel(new GridLayout(1,3,10,10));
		pe = new JPanel(new BorderLayout());
		//pe.setBorder(BorderFactory.createTitledBorder( Application.getApplication().getTraduction("Produits_entree_non_lies")));
		pe.add(new JScrollPane(this.listeProduitEntre), BorderLayout.CENTER);
		ps = new JPanel(new BorderLayout());
		//ps.setBorder(BorderFactory.createTitledBorder( Application.getApplication().getTraduction("Produits_sortie_non_lies")));
		ps.add(new JScrollPane(this.listeProduitSortie), BorderLayout.CENTER);
		pc = new JPanel(new BorderLayout());
		//pc.setBorder(BorderFactory.createTitledBorder( Application.getApplication().getTraduction("Composants_non_lies")));
		pc.add(new JScrollPane(this.listeComposant), BorderLayout.CENTER);
		
		top_panel.add(pe);
		top_panel.add(ps);
		top_panel.add(pc);
			
		JPanel bottom_panel = new JPanel();
		bottom_panel.setLayout(new BorderLayout());
		Box button_box = Box.createHorizontalBox();
		
		hide_button = new JButton();
		hide_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
			hideErrorArea();
			}
		});
		
		
		clear_button = new JButton();
		clear_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
			clearErrorArea();
			}
		});
		
		button_box.add(Box.createHorizontalGlue());
		button_box.add(clear_button);
		button_box.add(hide_button);
		
		bottom_panel.add(top_panel, BorderLayout.CENTER);
		bottom_panel.add(button_box, BorderLayout.SOUTH);
		
		add(bottom_panel, BOTTOM);
		
		this.rafraichirLangue();
	}

	public void setListeErreurs(Vector listee, Vector listes, Vector listec )
	{
		this.listeProduitEntre.setListData(listee);
		this.listeProduitSortie.setListData(listes);
		this.listeComposant.setListData(listec);
	}
	
	public void hideErrorArea()
	{
		int new_location = getHeight() - getInsets().bottom - getDividerSize();
		int current_location = getDividerLocation();
		
		if(current_location < new_location)
		{
			setDividerLocation(new_location);
			setLastDividerLocation(current_location);
		}
	}
	
	public void showErrorArea()
	{
		int last_location = getLastDividerLocation();
		int current_location = getDividerLocation();
		
		if(current_location == (getHeight() - getInsets().bottom - getDividerSize()) )
		{
			setDividerLocation(last_location);
			setLastDividerLocation(current_location);
		}
	}

	public void clearErrorArea()
	{
		this.listeComposant.setListData(new Vector());
		this.listeProduitEntre.setListData(new Vector());
		this.listeProduitSortie.setListData(new Vector());
	}

	public void insertUpdate(DocumentEvent e)
	{
		showErrorArea();
	}
	
	public void removeUpdate(DocumentEvent e) {}
	public void changedUpdate(DocumentEvent e) {}

	/**
	 * Permet de rafraichir les éléments textuels selon la langue courante
	 */
	public void rafraichirLangue()
	{
		clear_button.setText(Application.getApplication().getTraduction("Vider"));
		hide_button.setText(Application.getApplication().getTraduction("Cacher"));
		pe.setBorder(BorderFactory.createTitledBorder( Application.getApplication().getTraduction("Produits_entree_non_lies")));
		ps.setBorder(BorderFactory.createTitledBorder( Application.getApplication().getTraduction("Produits_sortie_non_lies")));
		pc.setBorder(BorderFactory.createTitledBorder( Application.getApplication().getTraduction("Composants_non_lies")));
	}
}
