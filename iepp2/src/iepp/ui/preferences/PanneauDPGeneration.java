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

package iepp.ui.preferences;

import iepp.Application;
import iepp.Projet;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.PaquetagePresentation;
import iepp.ui.VueDPArbre;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.IconManager;


public class PanneauDPGeneration extends PanneauOption
{

	private DefinitionProcessus defProc;

	private ComposantsListModel ComposantsListModel = new ComposantsListModel();
	private PPListModel ppListModel = new PPListModel();

	private JList LS_referentiel = new JList();
	private JList LS_arbre = new JList();

	private JButton BP_ajouter = new JButton();
	private JButton BP_retirer = new JButton();
	private JButton BP_monter = new JButton();
	private JButton BP_descendre = new JButton();

	private JScrollPane scrollPaneRef = new JScrollPane();
	private JScrollPane scrollPaneArbre = new JScrollPane();

	private Vector oldListe = null;


	public static final String DP_GENERATION_PANEL_KEY = "ContenuTitle";

	public PanneauDPGeneration(String name)
	{

	    Projet p = Application.getApplication().getProjet();
	    if (p != null)
	    {
	        this.defProc = Application.getApplication().getProjet().getDefProc();
	        //sauvegarder la liste si l'utilisateur annule
	        this.oldListe = (Vector)this.defProc.getListeAGenerer().clone();
	    }
		this.mTitleLabel = new JLabel (name) ;
		this.setLayout(new BorderLayout());
		mPanel = new JPanel() ;
		GridBagLayout gridbag = new GridBagLayout();
		mPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		ManagerButton man = new ManagerButton();

		// Title
		c.weightx = 1.0;
		c.weighty = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row			//	title
		this.mTitleLabel = new JLabel (name);
		TitledBorder titleBor = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
		titleBor.setTitleJustification(TitledBorder.CENTER);
		mTitleLabel.setBorder(titleBor);
		gridbag.setConstraints(mTitleLabel, c);
		mPanel.add(mTitleLabel);

		// linefeed
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);


		if (Application.getApplication().getReferentiel() != null)
		{
			this.initPanelArbre(c, gridbag);
		}

		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 2.0;
		// linefeed
		 c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		this.add(new JLabel("    "),BorderLayout.WEST);
		this.add(mPanel,BorderLayout.CENTER);
	}


	public PanneauOption openPanel(String key)
	{
		this.setName(Application.getApplication().getTraduction(key)) ;
		return this ;
	}

	private void initPanelArbre(GridBagConstraints c, GridBagLayout gridbag)
	{
		Referentiel ref = Application.getApplication().getReferentiel();
		Vector liste = new Vector();
		Vector listePaquetage = Application.getApplication().getReferentiel().getListeNom(ElementReferentiel.PRESENTATION);
		Vector v = (Vector)this.defProc.getListeAGenerer();
		Vector listePaquetagesChoisis = new Vector();
		for(int i=0;i<v.size();i++) {
			if(v.get(i) instanceof PaquetagePresentation) {
				listePaquetagesChoisis.add(v.get(i));
			}
		}
		boolean inserer;
		for (int i = 0;i<listePaquetage.size();i++)
		{
			inserer = true;
			String nomPaqPre = listePaquetage.elementAt(i).toString();
			long id= ref.nomPresentationToId(nomPaqPre);
			//System.out.println("id du paq:"+id);
			PaquetagePresentation paq = ref.chargerPresentation(id);
			for(int j=0;j<listePaquetagesChoisis.size();j++) {
				if(listePaquetagesChoisis.elementAt(j).toString().equals(paq.toString())) {
					inserer = false;
				}
			}
			if(inserer) {
				liste.add(paq);
			}
			// Avant
			//liste.add(listePaquetage.elementAt(i)) ;
		}
		this.ppListModel.setListe(liste);
		this.LS_referentiel = new JList(this.ppListModel);
		this.LS_referentiel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		MyListeRenderer rendu = new MyListeRenderer();
		this.LS_arbre.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.LS_arbre.setModel(this.ComposantsListModel);
		this.LS_arbre.setCellRenderer(rendu);
		this.LS_arbre.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				int indexSelect = LS_arbre.getSelectedIndex();
				if((indexSelect != -1) && (indexSelect < ComposantsListModel.getSize()))
				{
					BP_retirer.setEnabled(true);

					Object a_verifier = (Object)ComposantsListModel.getListe().elementAt(indexSelect);
					if (a_verifier instanceof IdObjetModele)
					{
						BP_retirer.setEnabled(false);
					}
				}
				LS_arbre.setSelectedIndex(indexSelect);
			}
		});
		//this.BP_ajouter.setText(Application.getApplication().getTraduction("ajouter_referentiel"));
		this.BP_ajouter.setIcon(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "flecheDroite.jpg"));
		this.BP_ajouter.setHorizontalTextPosition(SwingConstants.LEFT);
		this.BP_ajouter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ajouter(e);
			}
		});
		//this.BP_retirer.setText(Application.getApplication().getTraduction("retirer_referentiel"));
		this.BP_retirer.setIcon(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "flecheGauche.jpg"));
		this.BP_retirer.setHorizontalTextPosition(SwingConstants.RIGHT);
		this.BP_retirer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				retirer(e);
			}
		});
		this.BP_monter.setIcon(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "flecheHaut.jpg"));
		this.BP_monter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				monter(e);
			}
		});
		this.BP_descendre.setIcon(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "flecheBas.jpg"));
		this.BP_descendre.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				descendre(e);
			}
		});
		this.scrollPaneRef.setViewportView(this.LS_referentiel);
		this.scrollPaneArbre.setViewportView(this.LS_arbre);

		c.gridx = 0; c.gridy = 2;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 2;
		c.gridheight = 5;
		this.scrollPaneRef.setPreferredSize(new Dimension(150,100));
		mPanel.add(this.scrollPaneRef, c);

		c.gridx = 6; c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		this.BP_ajouter.setPreferredSize(new Dimension(40,40));
		mPanel.add(this.BP_ajouter, c);

		c.gridx = 6; c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		this.BP_retirer.setPreferredSize(new Dimension(40,40));
		mPanel.add(this.BP_retirer, c);

		c.gridx = 6; c.gridy = 4;
		c.gridheight = 1;
		c.gridwidth = 1;
		this.BP_monter.setPreferredSize(new Dimension(40,40));
		mPanel.add(this.BP_monter, c);

		c.gridx = 6; c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		this.BP_descendre.setPreferredSize(new Dimension(40,40));
		mPanel.add(this.BP_descendre, c);

		c.gridx = 6; c.gridy = 5;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.VERTICAL;
		this.makeLabel(" ", gridbag, c);


		c.gridx = 7; c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 5;
		c.fill = GridBagConstraints.VERTICAL;
		this.scrollPaneArbre.setPreferredSize(new Dimension(150,100));
		mPanel.add(this.scrollPaneArbre, c);

		// initialiser
		if (this.defProc != null)
		{
			this.ComposantsListModel.setListe(this.oldListe);
		}
	}


	public void save ()
	{
		if (this.defProc != null)
		{
			this.defProc.setListeAGenerer(this.ComposantsListModel.getListe());
		}
	}

	private void ajouter(ActionEvent e)
	{
		Referentiel ref = Application.getApplication().getReferentiel() ;
		
		int indexSelect = this.LS_referentiel.getSelectedIndex();

		if((indexSelect != -1) && (indexSelect < LS_referentiel.getModel().getSize()))
		{
			PPListModel listeRef = (PPListModel)LS_referentiel.getModel();
			Object a_inserer = LS_referentiel.getSelectedValue();
			
			/*
			 * Avant
			 * Referentiel ref = Application.getApplication().getReferentiel() ;
			 * String nomPaqPre = a_inserer.toString();
			 * long id= ref.nomPresentationToId(nomPaqPre);
			 * //System.out.println("id du paq:"+id);
			 * PaquetagePresentation paq = ref.chargerPresentation(id);
			 */
			
			PaquetagePresentation paq = (PaquetagePresentation) a_inserer;
			if (paq != null)
			{
				  ComposantsListModel listeComp = (ComposantsListModel)LS_arbre.getModel();
				  listeComp.ajouter(paq);
				  this.LS_referentiel.setSelectedIndex(indexSelect-1);
				  listeRef.enlever(indexSelect);
			}
		}
	}
	private void retirer(ActionEvent e)
	{
		int indexSelect = this.LS_arbre.getSelectedIndex();

		if((indexSelect != -1) && (indexSelect < this.ComposantsListModel.getSize()))
		{
			PPListModel listeRef = (PPListModel)LS_referentiel.getModel();
			ComposantsListModel listeComp = (ComposantsListModel)LS_arbre.getModel();
			Object a_retirer = listeComp.getListe().elementAt(indexSelect);
			/*
			 * Avant
			 * if (a_retirer instanceof PaquetagePresentation)
			 * {
			 * 	String cheminFichier = ((PaquetagePresentation)a_retirer).getNomFichier();
			 * 	a_retirer = (cheminFichier.substring(cheminFichier.lastIndexOf(File.separator) + 1, cheminFichier.lastIndexOf(".")));
			 * }
			 */
			listeRef.ajouter(a_retirer);
			this.LS_arbre.setSelectedIndex(indexSelect-1);
			listeComp.enlever(indexSelect);
		}
	}
	private void descendre(ActionEvent e)
	{
		int indexSelect = this.LS_arbre.getSelectedIndex();
		if((indexSelect != -1) && (indexSelect < this.ComposantsListModel.getSize()-1))
		{
			this.ComposantsListModel.descendre(indexSelect);
			this.LS_arbre.setSelectedIndex(indexSelect+1);
		}
	}
	private void monter(ActionEvent e)
	{
		int indexSelect = this.LS_arbre.getSelectedIndex();
		if((indexSelect != -1) && (indexSelect > 0))
		{
			this.ComposantsListModel.monter(indexSelect);
			this.LS_arbre.setSelectedIndex(indexSelect-1);
		}
	}

	private abstract class ListModel extends AbstractListModel
	{
		public Vector liste = new Vector();

		public Object getElementAt(int index)
		{
			return liste.elementAt(index);
		}

		public int getSize()
		{
			return liste.size();
		}

		public void monter(int index)
		{
			Object buffer = getElementAt(index);
			liste.removeElementAt(index);
			liste.add(index-1, buffer);
			fireContentsChanged(this, index-1, index);
		}

		public void descendre(int index)
		{
			Object buffer = getElementAt(index);
			liste.removeElementAt(index);
			liste.add(index+1, buffer);
			fireContentsChanged(this, index, index+1);
		}

		public void ajouter(Object o)
		{
			liste.add(o);
			fireContentsChanged(this, 0, liste.size());
		}

		public void enlever(int i)
		{
			liste.removeElementAt(i);
			fireContentsChanged(this, 0 , liste.size());
		}

		public Vector getListe()
		{
			return(this.liste);
		}

		public void setListe(Vector liste)
		{
			this.liste = liste;
		}
	}
	private class ComposantsListModel extends ListModel{}
	private class PPListModel extends ListModel{}

	private class MyListeRenderer extends DefaultListCellRenderer
	{
			public MyListeRenderer()
			{
				setOpaque(true);
				setHorizontalAlignment(CENTER);
				setVerticalAlignment(CENTER);
			}

			/*
			* This method finds the image and text corresponding
			* to the selected value and returns the label, set up
			* to display the text and image.
			*/
			public Component getListCellRendererComponent(
			                    JList list,
			                    Object value,
			                    int index,
			                    boolean isSelected,
			                    boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof PaquetagePresentation)
				{
					this.setIcon(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeDefProc));
				}
				else
				{
					this.setIcon(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeComposant));
				}
				return this;
			}
	}

	private class ManagerButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//récupérer l'objet source de l'évènement reçu
			Object source = e.getSource();
			// selon cet objet, réagir en conséquence
		}
	}
}
