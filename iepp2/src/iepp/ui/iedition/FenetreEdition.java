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
 
package iepp.ui.iedition;

import iepp.Application;
import iepp.application.CAjouterComposantVide;
import iepp.application.aedition.aoutil.OCreerElement;
import iepp.ui.iedition.dessin.GraphModelView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import util.IconManager;

/**
 * Classe permettant de créer et d'afficher le panneau d'assemblage des composants
 */
public class FenetreEdition extends JPanel implements ActionListener
{
	/**
	 * Diagrammme d'assemblage
	 */ 
	private VueDPGraphe vueDPGraphe ;
	
	/**
	 * barre d'outils permettant de choisir l'action à effectuer
	 */
	private JPanel barre;
	private JPanel barre_outil;
	private JPanel barre_changement;
	private JPanel barre_outil_sta;
	private JPanel barre_outil_dyn;
	
	/**
	 *  Boutons statiques
	 */
	private JToggleButton oSelection ;
	private JToggleButton oLierElement ;
	private JToggleButton oNote ;
	private JToggleButton oNewCompVide;
	
	private JButton oDiagStatique;
	private JButton oDiagDynamique;
	
	/**
	 *  Boutons Dynamiques
	 */
	private JToggleButton oSelectionDyn ;
	private JToggleButton oLierElementDyn ;
	private JToggleButton oNoteDyn ;
	//private JToggleButton oAutoLier;
	
	private JButton monterLienDyn;
	private JButton decendreLienDyn;
	private JButton versDroiteCompDyn;
	private JButton versGaucheCompDyn;
	private boolean versDroite;
	private boolean versBas;
	
	/**
	 * Groupe de boutons à deux états
	 */
	private ButtonGroup gpbouton;
	private ButtonGroup gpbouton_dyn;
	
	
	/**
	 * Construction du panneau d'assemblage avec le diagramme
	 * @param vue, diagramme à afficher
	 */
	public FenetreEdition (VueDPGraphe vue)
	{
		// garder un lien vers le diagramme à afficher
		this.vueDPGraphe = vue ;
		// gestionnaire de mise en forme
		this.setLayout(new BorderLayout());
		// création des barres d'outils
		this.barre = new JPanel(new BorderLayout());
		this.barre_changement = new JPanel(new GridLayout(1,2));
		this.barre_outil = new JPanel(new FlowLayout());
		this.barre_outil_sta = new JPanel(new FlowLayout());
		this.barre_outil_dyn = new JPanel(new FlowLayout());
		
		// création de deux groupes vides de boutons
		this.gpbouton = new ButtonGroup();
		this.gpbouton_dyn = new ButtonGroup();
		
		//-------------------------------------
		//           Boutons statiques
		
		// création du bouton de l'outil de sélection
		this.oSelection = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteArrow.gif"));
		this.oSelection.setPreferredSize(new Dimension(35,35));
		this.barre_outil_sta.add(this.oSelection);
		this.oSelection.addActionListener(this);
		
		// création du bouton de l'outil de liaison
		this.oLierElement = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteTransition.gif"));
		this.oLierElement.setPreferredSize(new Dimension(35,35));
		this.barre_outil_sta.add(this.oLierElement);
		this.oLierElement.addActionListener(this);
		
		//création du bouton de l'outil pour lier une note à un élément
		this.oNote = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteNotes.gif"));
		this.oNote.setPreferredSize(new Dimension(35,35));
		this.barre_outil_sta.add(this.oNote);
		this.oNote.addActionListener(this);
		
		//pour faire un espace
		JPanel espace = new JPanel();
		espace.setSize(35,35);
		this.barre_outil_sta.add(espace);
		
		//création du bouton de création de composant vide
		this.oNewCompVide = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteCompVide.png"));
		this.oNewCompVide.setPreferredSize(new Dimension(35,35));
		this.barre_outil_sta.add(this.oNewCompVide);
		this.oNewCompVide.addActionListener(this);
		
		
		//----------------------------------
		//          Boutons Changement de diagramme
		
		this.oDiagStatique = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "DiagStatique.png"));
		this.oDiagStatique.setPreferredSize(new Dimension(17,35));
		this.barre_changement.add(oDiagStatique);
		this.oDiagStatique.addActionListener(this);
		
		this.oDiagDynamique = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "DiagDynamique.png"));
		this.oDiagDynamique.setPreferredSize(new Dimension(17,35));
		this.barre_changement.add(oDiagDynamique);
		this.oDiagDynamique.addActionListener(this);
		
		//----------------------------------
		//          Boutons Dynamiques
		
		// création du bouton de l'outil de sélection
		this.oSelectionDyn = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteArrow.gif"));
		this.oSelectionDyn.setPreferredSize(new Dimension(35,35));
		this.barre_outil_dyn.add(this.oSelectionDyn);
		this.oSelectionDyn.addActionListener(this);
		
		// création du bouton de l'outil de liaison
		this.oLierElementDyn = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteTransition.gif"));
		this.oLierElementDyn.setPreferredSize(new Dimension(35,35));
		this.barre_outil_dyn.add(this.oLierElementDyn);
		this.oLierElementDyn.addActionListener(this);
				
		//création du bouton de l'outil pour lier une note à un élément
		this.oNoteDyn = new JToggleButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteNotes.gif"));
		this.oNoteDyn.setPreferredSize(new Dimension(35,35));
		this.barre_outil_dyn.add(this.oNoteDyn);
		this.oNoteDyn.addActionListener(this);
		
		//pour faire un espace
		espace = new JPanel();
		espace.setSize(35,35);
		this.barre_outil_dyn.add(espace);
		
		//création du bouton de l'outil pour monter un lien Dyn
		this.monterLienDyn = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteUpLien.png"));
		this.monterLienDyn.setPreferredSize(new Dimension(35,35));
		this.barre_outil_dyn.add(this.monterLienDyn);
		this.monterLienDyn.addActionListener(this);
		
		//création du bouton de l'outil pour decendre un lien Dyn
		this.decendreLienDyn = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteDownLien.png"));
		this.decendreLienDyn.setPreferredSize(new Dimension(35,35));
		this.barre_outil_dyn.add(this.decendreLienDyn);
		this.decendreLienDyn.addActionListener(this);
		
		//création du bouton de l'outil pour déplacer un composant vers la droite
		this.versDroiteCompDyn = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteDroitComp.png"));
		this.versDroiteCompDyn.setPreferredSize(new Dimension(35,35));
		this.barre_outil_dyn.add(this.versDroiteCompDyn);
		this.versDroiteCompDyn.addActionListener(this);
		
		//création du bouton de l'outil pour déplacer un composant vers la gauche
		this.versGaucheCompDyn = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "PaletteGaucheComp.png"));
		this.versGaucheCompDyn.setPreferredSize(new Dimension(35,35));
		this.barre_outil_dyn.add(this.versGaucheCompDyn);
		this.versGaucheCompDyn.addActionListener(this);
		
		this.barre_outil.add(barre_outil_sta);
		this.barre_outil.add(barre_outil_dyn);
		
		this.barre.add(this.barre_outil,BorderLayout.CENTER);
		
		this.barre.add(this.barre_changement,BorderLayout.SOUTH);
		//----------------------------------
		
		// ajouter ces boutons au groupe de bouton
		// cela permet de n'avoir qu'un seul bouton sélectionné à la foi
		this.gpbouton.add(this.oSelection);
		this.gpbouton.add(this.oLierElement);
		this.gpbouton.add(this.oNote);
		this.gpbouton.add(this.oNewCompVide);
		
		// ajouter ces boutons au groupe de bouton
		// cela permet de n'avoir qu'un seul bouton sélectionné à la foi
		this.gpbouton_dyn.add(this.oSelectionDyn);
		this.gpbouton_dyn.add(this.oLierElementDyn);
		this.gpbouton_dyn.add(this.oNoteDyn);
		this.gpbouton_dyn.add(this.monterLienDyn);
		this.gpbouton_dyn.add(this.decendreLienDyn);
		this.gpbouton_dyn.add(this.versDroiteCompDyn);
		this.gpbouton_dyn.add(this.versGaucheCompDyn);
		
		// par défaut on prend l'outil de sélection
		this.oSelection.setSelected(true);
		this.barre_outil.setBorder(BorderFactory.createEtchedBorder());
		this.barre_outil.setPreferredSize(new Dimension(45,550));
		//this.barre_outil_sta.setBorder(BorderFactory.createEtchedBorder());
		this.barre_outil_sta.setPreferredSize(new Dimension(40,300));
		this.oSelectionDyn.setSelected(true);
		//this.barre_outil_dyn.setBorder(BorderFactory.createEtchedBorder());
		this.barre_outil_dyn.setPreferredSize(new Dimension(40,400));
	
		JScrollPane scroler = new JScrollPane(vue);
		scroler.setPreferredSize(new Dimension(500,500));
	
		// ajouter au panneau tous les éléments créés
		this.add(this.barre, BorderLayout.WEST);
		//this.add(this.barre_outil_dyn, BorderLayout.EAST);
		//this.remove(this.barre_outil_dyn);
		this.add(scroler , BorderLayout.CENTER);
		
		this.barre_outil.setVisible(true);
		this.barre_outil_sta.setVisible(true);
		this.barre_outil_dyn.setVisible(false);
	}

	public void setStatique(){
		this.barre_outil_dyn.setVisible(false);
		this.barre_outil_sta.setVisible(true);
		//this.remove(this.barre_outil_dyn);
		//this.add(barre_outil, BorderLayout.WEST);
		
	}
	
	public void setDynamique(){
		this.barre_outil_sta.setVisible(false);
		this.barre_outil_dyn.setVisible(true);
		//this.remove(this.barre_outil);
		//this.add(barre_outil_dyn, BorderLayout.WEST);
	}
	/**
	 * Renvoie le diagramme d'assemblage de composant affiché
	 * @return un diagramme
	 */
	public VueDPGraphe getVueDPGraphe()
	{
		return this.vueDPGraphe ;
	}
	
	public void removeVueDPGraphe()
	{
		this.vueDPGraphe = null;
	}
	
	public void setOutilSelection()
	{
		if (((GraphModelView)vueDPGraphe.getModel()).getType()){
			this.oSelection.setSelected(true);
		}else{
			this.oSelectionDyn.setSelected(true);
		}
		
		this.vueDPGraphe.setOutilSelection();
	}
	
	public void rafraichirLangue()
	{
		this.oSelection.setToolTipText(Application.getApplication().getTraduction("Selection"));
		this.oLierElement.setToolTipText(Application.getApplication().getTraduction("Lien"));
		this.oNote.setToolTipText(Application.getApplication().getTraduction("Note"));
		this.oNewCompVide.setToolTipText(Application.getApplication().getTraduction("Ajouter_Composant_Vide_DP"));
		
		this.oSelectionDyn.setToolTipText(Application.getApplication().getTraduction("Selection"));
		this.oLierElementDyn.setToolTipText(Application.getApplication().getTraduction("Lien"));
		this.oNoteDyn.setToolTipText(Application.getApplication().getTraduction("Note"));
		
		this.oDiagStatique.setToolTipText(Application.getApplication().getTraduction("Passer_Statique"));
		this.oDiagDynamique.setToolTipText(Application.getApplication().getTraduction("Passer_Dynamique"));
		
		this.monterLienDyn.setToolTipText(Application.getApplication().getTraduction("MonterLien"));
		this.decendreLienDyn.setToolTipText(Application.getApplication().getTraduction("DescendreLien"));
		this.versDroiteCompDyn.setToolTipText(Application.getApplication().getTraduction("VersDroiteComp"));
		this.versGaucheCompDyn.setToolTipText(Application.getApplication().getTraduction("VersGaucheComp"));
		
	}
	
	/**
	 * Gestionnaire de click sur les boutons
	 */
	public void actionPerformed( ActionEvent e )
	{
		// selon l'objet source, réagir
		if (e.getSource() == this.oSelection || e.getSource() == this.oSelectionDyn)
		{
			this.vueDPGraphe.setOutilSelection();
		}
		else if (e.getSource() == this.oLierElement || e.getSource() == this.oLierElementDyn)
		{
			// ici on rappel bien l'outil de selection pour eviter les conflits avec le bouton note..
			// sinon lorsque l'on fait un lien, une note apparait si le bouton note a été précédement enfoncé
			this.vueDPGraphe.setOutilSelection();
			// ensuite on passe à l'outil lier
			this.vueDPGraphe.setOutilLier();
		}
		else if (e.getSource() == this.oNote || e.getSource() == this.oNoteDyn)
		{
			this.vueDPGraphe.setOutilCreerElement(OCreerElement.TYPE_NOTE);
		}
		else if (e.getSource() == this.oNewCompVide)
		{
			this.vueDPGraphe.setOutilCreerElement(OCreerElement.TYPE_COMPVIDE);
		}
		else if (e.getSource() == this.versDroiteCompDyn)
		{
			this.versDroite = true;
			this.vueDPGraphe.setDecalerElement(versDroite);
		}
		else if (e.getSource() == this.versGaucheCompDyn)
		{
			this.versDroite = false;
			this.vueDPGraphe.setDecalerElement(versDroite);
		}
		else if (e.getSource() == this.monterLienDyn)
		{
			this.versBas = false;
			this.vueDPGraphe.setMonterOuDecendreLienDyn(versBas);
		}
		else if (e.getSource() == this.decendreLienDyn)
		{
			this.versBas = true;
			this.vueDPGraphe.setMonterOuDecendreLienDyn(versBas);
		}
		else if (e.getSource() == this.oDiagStatique)
		{
			try{
				this.vueDPGraphe.clearSelection();
				this.vueDPGraphe.setModel((GraphModelView)this.vueDPGraphe.getModelesDiagrammes().elementAt(0));
				Application.getApplication().getProjet().getFenetreEdition().setStatique();
		
				this.vueDPGraphe.setOutilSelection();
			}catch(Exception ex){
				// pour les cas a probleme on ne fait rien  (il doit pas en avoir)
			}
		}
		else if (e.getSource() == this.oDiagDynamique)
		{
			try{
				this.vueDPGraphe.clearSelection();
				
				int num = this.vueDPGraphe.getModelesDiagrammes().indexOf(this.vueDPGraphe.getModel());
				int numres = (((num+1)%this.vueDPGraphe.getModelesDiagrammes().size()));
				
				if(numres == 0){
					this.vueDPGraphe.setModel((GraphModelView)this.vueDPGraphe.getModelesDiagrammes().elementAt(1));
				}else{
					this.vueDPGraphe.setModel((GraphModelView)this.vueDPGraphe.getModelesDiagrammes().elementAt(numres));
				}
				Application.getApplication().getProjet().getFenetreEdition().setDynamique();
				this.vueDPGraphe.setOutilSelection();
				
			}catch(Exception ex){
				// pour les cas ou il n'y a pas de scénario :)
			}
		}
	}
}
