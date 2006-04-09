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
import iepp.Projet;
import iepp.application.CCreerReferentiel;
import iepp.application.CExporterProcessus;
import iepp.application.CExporterProcessusDPC;
import iepp.application.CExporterProcessusPDF;
import iepp.application.CExporterProcessusXMI;
import iepp.application.CFermerProjet;
import iepp.application.CGenererSite;
import iepp.application.CNouveauProjet;
import iepp.application.COuvrirDP;
import iepp.application.aedition.CImprimerDiagramme;
import iepp.application.aedition.CZoomer;
import iepp.application.ageneration.ArbreGeneration;
import iepp.application.ageneration.GenerationManager;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.DefinitionProcessus;
import iepp.ui.apropos.FenetrePropos;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.ireferentiel.VueReferentielArbre;
import iepp.ui.preferences.FenetrePreference;
import iepp.ui.preferences.PanneauGeneration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import util.IconManager;

import com.Ostermiller.util.Browser;

/**
 * Classe permettant de cr\uFFFDer et d'afficher la fen\uFFFDtre principale de l'outil
 */
public class FenetrePrincipale extends JFrame implements ActionListener
{
		/**
		 * place des onglets dans le TabbedPane
		 */
		public static final int INDEXTABREFERENTIEL = 0 ;
		public static final int INDEXTABPROCESSUS = 1 ;

		/**
		 * Arbre de la d\uFFFDfinition de processus
		 */
		private VueDPArbre vueDPArbre;


		/**
		 * Arbre du r\uFFFDf\uFFFDrentiel
		 */
		private VueReferentielArbre vueRefArbre ;

		/**
		 * Composant permettant de s\uFFFDparer la fen\uFFFDtre en 2 parties verticales
		 */
		private JSplitPane frame_split;
		/*private PanneauVerification panneauVerif ;*/


		// El\uFFFDments graphiques \uFFFD afficher

		private JMenuBar barre_menu ;
		private JMenu fichier ;
				private JMenuItem nouveau_projet ;
				private JMenuItem nouveau_proc ;
				private JMenuItem ouvrir_projet ;
				private JMenuItem ouvrir_proc ;
				private JMenuItem fermer ;
				private JMenuItem sauver ;
				private JMenuItem imprimer ;
				private JMenuItem quitter ;

		private JMenu affichage ;
				private JMenuItem zoomin ;
				private JMenuItem zoomout ;
				private JMenuItem propriete ;

		private JMenu outil ;
				/*private JMenuItem verifier ;*/
				private JMenuItem generer ;
				private JMenuItem exporterPDF;

		private JMenu export ;
				private JMenuItem exporterDPE ;
				private JMenuItem exporterDPC;
				private JMenuItem exporterXMI;


		private JMenu aide;
				private JMenuItem propos;

		private JToolBar barre_outil ;
				// TODO mettre un bouton rapide pour la creation d'un nouveau projet
				private JButton bnouveau_proc ;
				private JButton bouvrir_projet ;
				private JButton bouvrir_proc ;
				private JButton bsauver ;
				private JButton bfermer ;
				/*private JButton bverifier ;*/
				private JButton bgenerer ;
				private JButton bexporter ;
				private JButton bimprimer ;
				private JButton bzoomin ;
				private JButton bzoomout ;
				private JButton bpreferences ;


		/**
		 * Panneau dans lequel s'affiche le diagramme
		 */
		private JPanel panneauGenerique;

		/**
		 * Onglets pour afficher l'arbre et le r\uFFFDf\uFFFDrentiel
		 */
		private JTabbedPane tabbedPane;


	/**
	 * Constructeur de la fenetre principale de l'outil
	 */
	public FenetrePrincipale()
	{

		// appel au constructeur de JFrame
		super(Application.getApplication().getConfigPropriete("titre"));
		// Lorsqu'on essaye de fermer l'appli, ne rien faire c'est l'adapter qui se charge de
		// fermer l'appli correctement
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// ecouteur de la fen\uFFFDtre
		this.addWindowListener(new FenetreListener());
		// cr\uFFFDation du menu
		this.creerMenu();
		// rajouter des raccourcis clavier aux items du menu
		this.ajouterRaccourcis();
		// cr\uFFFDation de la barre d'outil
		this.creerBarreOutil();
		// cr\uFFFDaton d'un arbre vide
		this.vueDPArbre = new VueDPArbre();
		// Cr\uFFFDation d'un arbre repr\uFFFDsentant le r\uFFFDf\uFFFDrentiel
		this.vueRefArbre = new VueReferentielArbre () ;
		// taille de la fen\uFFFDtre en fonction du syst\uFFFDme d'accueil
		Rectangle r = this.getGraphicsConfiguration().getBounds();
		this.setBounds(r.x+10, r.y+10, r.width*5/6, r.height*5/6);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		this.setLocation( (screenSize.width - frameSize.width) / 2,
										 (screenSize.height - frameSize.height) / 2);
		// placer les \uFFFDl\uFFFDments au bon endroit dans la fen\uFFFDtre
		this.positionnerElements();
		// permettre de r\uFFFDcup\uFFFDrer les \uFFFDv\uFFFDnements utilisateur
		this.ajouterEcouteurs();
		this.majEtat();
		//this.panneauVerif.setDividerLocation(r.height*5/6);
	}

	/**
	 * Methode permettant de cr\uFFFDer les \uFFFDl\uFFFDments du menu et de les ajouter
	 */
	private void creerMenu()
	{
		// cr\uFFFDation de la barre de menu
		this.barre_menu = new JMenuBar();
		// positionner la barre de menu
		this.setJMenuBar(barre_menu);

		// Fichier
		this.fichier = new JMenu();
		this.barre_menu.add(this.fichier);
		this.nouveau_projet = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FileNew.gif"));
		this.fichier.add(this.nouveau_projet);
		this.nouveau_proc = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FileNew.gif"));
		this.fichier.add(this.nouveau_proc);
		this.ouvrir_projet = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FileOpen.gif"));
		this.fichier.add(this.ouvrir_projet);
		this.ouvrir_proc = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "fileopen.png"));
		this.fichier.add(this.ouvrir_proc);
		this.sauver = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FileSave.gif"));
		this.fichier.add(this.sauver);
		this.fermer = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "fileclose.png"));
		this.fichier.add(this.fermer);
		this.fichier.addSeparator();

		this.imprimer = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FilePrint.gif"));
		this.fichier.add(this.imprimer);
		this.fichier.addSeparator();

		this.quitter = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FileQuit.gif"));
		this.fichier.add(this.quitter);

		// affichage
		this.affichage = new JMenu();
		this.barre_menu.add(this.affichage);
		this.zoomin = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "zoomin.png"));
		this.affichage.add(this.zoomin);
		this.zoomout = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "zoomout.png"));
		this.affichage.add(this.zoomout);
		this.affichage.addSeparator();
		this.propriete = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "configure.png"));
		this.affichage.add(this.propriete);

		 // Outils
		this.outil = new JMenu();
		this.barre_menu.add(outil);
		/*
		this.verifier = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ToolsValidate.gif"));
		this.outil.add(this.verifier);
		*/
		
		this.generer = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ToolsGenerate.gif"));
		this.outil.add(this.generer);
		this.exporterPDF = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ark_extract.png"));
        this.outil.add(this.exporterPDF);

                //Exporter
                this.export = new JMenu();
                this.barre_menu.add(export);
                this.exporterDPE = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ark_extract.png"));
                this.exporterDPC = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ark_extract.png"));
                this.exporterXMI = new JMenuItem(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ark_extract.png"));
                this.export.add(this.exporterDPE);
                this.export.add(this.exporterDPC);
                this.export.add(this.exporterXMI);
        
		 //Aide
		 this.aide = new JMenu();
		 this.barre_menu.add(Box.createHorizontalGlue());
		 this.barre_menu.add(this.aide);
		 this.propos = new JMenuItem();
		 this.aide.add(this.propos);
	}

	/**
	 * M\uFFFDthode permettant d'ajouter des raccourcis \uFFFD certains items du menu
	 */
	private void ajouterRaccourcis()
	{
		this.ouvrir_projet.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK)));
		this.fermer.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK)));
		this.sauver.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK)));
		this.generer.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK)));
		this.propos.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK)));
	}


	/**
	 * M\uFFFDthode permettant de cr\uFFFDer la barre d'outil contenant des boutons raccourcis
	 */
	private void creerBarreOutil()
	{
		// cr\uFFFDer un barre d'outil vide
		this.barre_outil = new JToolBar();
		// cr\uFFFDer et rajouter les boutons \uFFFD la barre
		// TODO Aficher le JButton nouveau projet
		this.bnouveau_proc = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FileNew.gif"));
		this.barre_outil.add(this.bnouveau_proc);
		this.bouvrir_projet = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FileOpen.gif"));
		this.barre_outil.add(this.bouvrir_projet);
		this.bouvrir_proc = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "fileopen.png"));
		this.barre_outil.add(this.bouvrir_proc);
		this.bsauver = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FileSave.gif"));
		this.barre_outil.add(this.bsauver);
		this.bimprimer = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "FilePrint.gif"));
		this.barre_outil.add(this.bimprimer);
		this.bfermer = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "fileclose.png"));
		this.barre_outil.add(this.bfermer);
		this.barre_outil.addSeparator();
		this.barre_outil.addSeparator();

		/*
		this.bverifier = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ToolsValidate.gif"));
		this.barre_outil.add(this.bverifier);
		*/
		this.bexporter = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ark_extract.png"));
		this.barre_outil.add(this.bexporter);
		this.bgenerer = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ToolsGenerate.gif"));
		this.barre_outil.add(this.bgenerer);
		this.barre_outil.addSeparator();
		this.barre_outil.addSeparator();

		this.bzoomin = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "zoomin.png"));
		this.barre_outil.add(this.bzoomin);
		this.bzoomout = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "zoomout.png"));
		this.barre_outil.add(this.bzoomout);

		this.barre_outil.addSeparator();
		this.barre_outil.addSeparator();
		this.bpreferences = new JButton(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "configure.png"));
		this.barre_outil.add(this.bpreferences);

		this.barre_outil.setFloatable(false);
	}

	/**
	 * M\uFFFDthode permettant de positionner correctement tous les \uFFFDl\uFFFDments cr\uFFFD\uFFFDs
	 */
	private void positionnerElements()
	{
		// cr\uFFFDer le panneau contenant le diagramme
		this.panneauGenerique = new JPanel();
		/*this.panneauVerif = new PanneauVerification(this.panneauGenerique);*/
		// division de la fenetre principale
		this.frame_split = new JSplitPane();
		this.frame_split.setOneTouchExpandable(true);
		this.frame_split.setDividerLocation(220);
		// cr\uFFFDation d'onglets
		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.setTabPlacement(SwingConstants.BOTTOM);
		this.tabbedPane.addTab(Application.getApplication().getTraduction("Referentiel"), IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "edittrash.png"), this.vueRefArbre , Application.getApplication().getTraduction("Referentiel"));
		this.tabbedPane.addTab(Application.getApplication().getTraduction("Processus"), IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "TreeExternalDescription.gif"), this.vueDPArbre , Application.getApplication().getTraduction("Processus_courant"));

		// positionner les onglets et le panneau g\uFFFDn\uFFFDrique
		this.frame_split.add(new JScrollPane(this.tabbedPane), JSplitPane.LEFT);
		this.frame_split.add(this.panneauGenerique, JSplitPane.RIGHT);
		/*this.frame_split.add(this.panneauVerif, JSplitPane.RIGHT);*/

	    //this.frame_split.add(this.panneauGenerique, JSplitPane.RIGHT);
		// mettre en place la barre d'outils
		this.getContentPane().add(this.barre_outil, BorderLayout.NORTH);
		this.getContentPane().add(this.frame_split, BorderLayout.CENTER);
	}

	/**
	 * M\uFFFDthode permettant d'\uFFFDcouter les \uFFFDv\uFFFDnements sur les menus, boutons etc... pour pouvoir
	 * les prendre en compte
	 */
	private void ajouterEcouteurs()
	{
		this.nouveau_projet.addActionListener(this) ;
		this.nouveau_proc.addActionListener(this) ;
		this.ouvrir_projet.addActionListener(this) ;
		this.ouvrir_proc.addActionListener(this) ;
		this.sauver.addActionListener(this) ;
		this.fermer.addActionListener(this) ;
		this.imprimer.addActionListener(this) ;
		this.quitter.addActionListener(this) ;

		this.zoomin.addActionListener(this);
		this.zoomout.addActionListener(this);
		this.propriete.addActionListener(this) ;

                /*this.verifier.addActionListener(this);*/
                this.exporterDPE.addActionListener(this);
                this.exporterDPC.addActionListener(this);
                this.exporterXMI.addActionListener(this);
                this.exporterPDF.addActionListener(this);
                this.generer.addActionListener(this);

        // TODO Associer un evenement au JButton de creation d'un nouveau projet
		this.bnouveau_proc.addActionListener(this) ;
		this.bouvrir_projet.addActionListener(this) ;
		this.bouvrir_proc.addActionListener(this) ;
		this.bsauver.addActionListener(this) ;
		this.bimprimer.addActionListener(this);
		this.bfermer.addActionListener(this) ;
		this.bexporter.addActionListener(this) ;
		/*this.bverifier.addActionListener(this) ;*/
		this.bgenerer.addActionListener(this) ;
		this.bzoomin.addActionListener(this) ;
		this.bzoomout.addActionListener(this) ;
		this.bpreferences.addActionListener(this) ;
		this.propos.addActionListener(this) ;
	}


	 //-----------------------------------------------------------------------------//
	 // Gestion des vues															//
	 //-----------------------------------------------------------------------------//

	/**
	 * Une fois la vue cr\uFFFD\uFFFDe (l'arbre) on l'affiche sur la partie gauche de la fenetre principale
	 * @param vue, arbre \uFFFD afficher sur la partie gauche
	 */
	public void enregistrerVueArbre(VueDPArbre vue)
	{
		// garder un lien vers l'arbre
		this.vueDPArbre = vue ;
		// rafraichir l'arbre dans la fenetre principale
		this.tabbedPane.setComponentAt(FenetrePrincipale.INDEXTABPROCESSUS, this.vueDPArbre);
	}

	/**
	 * R\uFFFDcup\uFFFDrer la vue arbre
	 * @return arbre de la d\uFFFDfinition de processus courante
	 */
	public VueDPArbre getVueDPArbre()
	{
		return this.vueDPArbre ;
	}


	public void setReferentielArbre(Referentiel ref)
	{
		this.vueRefArbre = new VueReferentielArbre(ref);
		this.tabbedPane.setComponentAt(FenetrePrincipale.INDEXTABREFERENTIEL, this.vueRefArbre);
	}

	/**
	 * Affiche sur la partie de droite le panneau correspondant \uFFFD l'action courante
	 * par exemple affiche le panneau correspondant \uFFFD l'\uFFFDdition du processus
	 * @param p, panneau \uFFFD afficher sur la droite de la fenetre
	 */
	public void setPanneauGenerique (JPanel p)
	{
		// garder un lien vers le panneau \uFFFD afficher
		this.panneauGenerique = p;
		/*this.panneauVerif = new PanneauVerification(this.panneauGenerique);*/
		// met en place le panneau
		this.frame_split.setRightComponent(this.panneauGenerique);
		/*this.frame_split.setRightComponent(this.panneauVerif);*/
		// remet la division de la fen\uFFFDtre comme au d\uFFFDpart
		this.frame_split.setDividerLocation(220);
	}

	//------------------------------------------------------------------------------//
	// Gestion des \uFFFDv\uFFFDnements														//
	//------------------------------------------------------------------------------//

	/**
	 *  Met \uFFFD jour les labels de la fenetre principale lors d'un changement de langue
	 */
	 public void rafraichirLangue()
	 {
	 	// rafraichit tous les menus et sous menus
		this.fichier.setText( Application.getApplication().getTraduction("Fichier"));
		this.nouveau_projet.setText( Application.getApplication().getTraduction("Nouveau_referentiel")) ;
		this.nouveau_proc.setText( Application.getApplication().getTraduction("Nouveau")) ;
		this.ouvrir_proc.setText( Application.getApplication().getTraduction("Ouvrir_proc")) ;
		this.ouvrir_projet.setText( Application.getApplication().getTraduction("Ouvrir_proj")) ;
		this.sauver.setText( Application.getApplication().getTraduction("Sauver")) ;
		this.fermer.setText( Application.getApplication().getTraduction("Fermer")) ;
		this.imprimer.setText(Application.getApplication().getTraduction("Imprimer"));
		this.quitter.setText( Application.getApplication().getTraduction("Quitter")) ;
		this.affichage.setText(Application.getApplication().getTraduction("Affichage"));
		this.zoomin.setText(Application.getApplication().getTraduction("Agrandir"));
		this.zoomout.setText(Application.getApplication().getTraduction("Reduire"));
		/**************** modif  du 10 03 06 ****************/
		this.propriete.setText( Application.getApplication().getTraduction("Langues")) ;
		//this.propriete.setText( Application.getApplication().getTraduction("Preferences")) ;
		/****************************************************/
		this.outil.setText( Application.getApplication().getTraduction("Outils"));
		/*this.verifier.setText( Application.getApplication().getTraduction("Valider_le_projet"));*/
        this.export.setText( Application.getApplication().getTraduction("Export"));
        this.exporterDPE.setText(Application.getApplication().getTraduction("ExporterDPE"));
        this.exporterDPC.setText(Application.getApplication().getTraduction("ExporterDPC"));
        this.exporterXMI.setText(Application.getApplication().getTraduction("ExporterXMI"));
        this.exporterPDF.setText(Application.getApplication().getTraduction("ExporterPDF"));
        this.generer.setText(Application.getApplication().getTraduction("Generer_le_site_Web"));
		this.aide.setText( Application.getApplication().getTraduction("Aide")) ;
		this.propos.setText( Application.getApplication().getTraduction("Propos")) ;
		
		/*
		// raffrachir les menus de l'arbre de navigation
		this.propos.setText( Application.getApplication().getTraduction("Propos")) ;
		this.propos.setText( Application.getApplication().getTraduction("Propos")) ;
		this.propos.setText( Application.getApplication().getTraduction("Propos")) ;
		this.propos.setText( Application.getApplication().getTraduction("Propos")) ;
		*/


		//raffraichit les onglets du JTabbedPane
		//this.tabbedPane.setTitleAt(FenetrePrincipale.INDEXTABPROCESSUS,Application.getApplication().getTraduction("Processus"));
		this.tabbedPane.setToolTipTextAt(FenetrePrincipale.INDEXTABPROCESSUS,Application.getApplication().getTraduction("Processus_courant"));
		this.tabbedPane.setToolTipTextAt(FenetrePrincipale.INDEXTABREFERENTIEL,Application.getApplication().getTraduction("Referentiel"));

	 	// rafraichit tous les tooltip des boutons
		// TODO Rafraichir le jButton de création d'un ouveau projet
	 	this.bnouveau_proc.setToolTipText(Application.getApplication().getTraduction("Nouveau"));
		this.bouvrir_proc.setToolTipText(Application.getApplication().getTraduction("Ouvrir_proc"));
		this.bouvrir_projet.setToolTipText(Application.getApplication().getTraduction("Ouvrir_proj"));
	 	this.bsauver.setToolTipText(Application.getApplication().getTraduction("Sauver"));
		this.bimprimer.setToolTipText(Application.getApplication().getTraduction("Imprimer"));
		this.bfermer.setToolTipText(Application.getApplication().getTraduction("Fermer"));
		this.bgenerer.setToolTipText(Application.getApplication().getTraduction("Generer_le_site_Web"));
		this.bexporter.setToolTipText( Application.getApplication().getTraduction("Export_rapide"));
		this.bzoomin.setToolTipText( Application.getApplication().getTraduction("Agrandir"));
		this.bzoomout.setToolTipText( Application.getApplication().getTraduction("Reduire"));
		/**************** modif  du 10 03 06 *****************/
		this.bpreferences.setToolTipText(Application.getApplication().getTraduction("Langues"));
		//this.bpreferences.setToolTipText(Application.getApplication().getTraduction("Preferences"));
		/****************************************************/
		/*this.bverifier.setToolTipText(Application.getApplication().getTraduction("Valider_le_projet"));*/

		if (Application.getApplication().getProjet() != null )
		{
			Application.getApplication().getProjet().rafraichirLangue();
		}

		//this.panneauVerif.rafraichirLangue();
	 }

	public void majEtat()
	{
		Projet projet = Application.getApplication().getProjet();
		boolean projetOuvert = (projet != null) ;

		Referentiel ref = Application.getApplication().getReferentiel();
		boolean refOuvert = (ref != null);

		this.bsauver.setEnabled(projetOuvert);
		this.sauver.setEnabled(projetOuvert);
		if (projet != null)
		{
			this.bsauver.setEnabled(projet.estModifie());
			this.sauver.setEnabled(projet.estModifie());
		}

                this.zoomin.setEnabled(projetOuvert);
                this.zoomout.setEnabled(projetOuvert);
                this.bzoomin.setEnabled(projetOuvert);
                this.bzoomout.setEnabled(projetOuvert);
                this.bimprimer.setEnabled(projetOuvert);
                this.imprimer.setEnabled(projetOuvert);
                this.fermer.setEnabled(projetOuvert);
                this.bfermer.setEnabled(projetOuvert);
                this.bgenerer.setEnabled(projetOuvert);
                this.generer.setEnabled(projetOuvert);
               /* this.bverifier.setEnabled(projetOuvert);*/
                this.bexporter.setEnabled(projetOuvert);
                /*this.verifier.setEnabled(projetOuvert);*/
                this.exporterDPE.setEnabled(projetOuvert);
                this.exporterDPC.setEnabled(projetOuvert);
                this.exporterXMI.setEnabled(projetOuvert);
                this.exporterPDF.setEnabled(projetOuvert);
                this.ouvrir_proc.setEnabled(refOuvert);
                this.bouvrir_proc.setEnabled(refOuvert);
                this.nouveau_proc.setEnabled(refOuvert);
                this.bnouveau_proc.setEnabled(refOuvert);
              }

	 //	gestion des \uFFFDv\uFFFDnements (boutons et menus)
	 public void actionPerformed (ActionEvent e)
	 {
	 		// r\uFFFDcup\uFFFDrer l'objet source de l'\uFFFDv\uFFFDnement re\uFFFDu
			 Object source = e.getSource();
			 // selon cet objet, r\uFFFDagir en cons\uFFFDquence
			 if (source == quitter)
			 {
				System.exit(0);
			 }
			 // on peut lancer une m\uFFFDme action \uFFFD partir de sources diff\uFFFDrentes
			 else if ((source == this.nouveau_projet )/* TODO ||(source == this.bnouveau_projet)*/)
			 {
					// creation d'un nouveau projet
				 //FenetreChoixReferentiel fCRef = new FenetreChoixReferentiel(this);
				 CCreerReferentiel c = new CCreerReferentiel ();
					if (c.executer())
					{
						Application.getApplication().getFenetrePrincipale().setTitle(
								Application.getApplication().getConfigPropriete("titre")
								+ " " + Application.getApplication().getReferentiel().getNomReferentiel());
						
						new FenetreChoixProcessus(this);
					}
					this.majEtat();
			 }
			 else if ((source == this.nouveau_proc )||(source == this.bnouveau_proc))
			 {
					// cr\uFFFDation d'un nouveau processus
					 CNouveauProjet c = new CNouveauProjet() ;
					 if (c.executer()== false)
				     {
				     	new CFermerProjet().executer();
				     }
					 this.majEtat();
			 }
			 else if ((source == this.ouvrir_proc )||(source == this.bouvrir_proc))
			 {
					// afficher la liste des DP disponibles dans le r\uFFFDf\uFFFDrentiel
			 		COuvrirDP c = new COuvrirDP() ;
					c.executer() ;
					this.majEtat();

			 }
			 
			 // fermer
			 else if ((source == this.fermer)||(source == this.bfermer))
			 {
			 		// fermeture du projet ouvert
			 		CFermerProjet c = new CFermerProjet(true);
			 		if (c.executer())
			 		{
			 			Application.getApplication().getFenetrePrincipale().setTitle(
								Application.getApplication().getConfigPropriete("titre")
								+ " " + Application.getApplication().getReferentiel().getNomReferentiel());
			 		}
			 		this.majEtat();
			 }
			 else if ((source == this.ouvrir_projet )||(source == this.bouvrir_projet))
			 {
				 Application.getApplication().setConfigPropriete("referentiel_demarrage", "");
		         FenetreChoixProjet fcp = new FenetreChoixProjet(this);
				 /*
				 COuvrirReferentiel c = new COuvrirReferentiel();
				 if (c.executer())
				 {
					 Application.getApplication().getFenetrePrincipale().setTitle(
							 Application.getApplication().getConfigPropriete("titre")
							 + " " + Application.getApplication().getReferentiel().getNomReferentiel());
					 this.dispose();
					 // fenêtre suivante
					 new FenetreChoixProcessus(this);
				 }
				 this.majEtat();
				 */
			 }
			 else if ((source == this.sauver )||(source == this.bsauver))
			 {
			 	Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().clearSelection();
				Referentiel ref = Application.getApplication().getReferentiel() ;
				ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
				this.majEtat();
			 }
			 else  if ((source == this.imprimer )||(source == this.bimprimer))
			 {
				   CImprimerDiagramme c = new CImprimerDiagramme(Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe());
				   c.executer();
			 }
			 /*
			 //Bouton V\uFFFDrification
			 else if ((source == this.bverifier)||(source == this.verifier))
			 {
				 //new FenetreVerification() ;
				 CVerification c = new CVerification();
				 c.executer();
			 }
			 */
			 // exporter
			else if  ((source == this.bexporter)||(source == this.exporterDPE))
			{
				CExporterProcessus c = new CExporterProcessus();
                                // debut modif Julien 2XMI affichage boite de dialogue
                                if(c.executer())
                                {
                                String titre = Application.getApplication().getTraduction("exportSimple");
                                String message = Application.getApplication().getTraduction("exportSimpleOk");
                                JOptionPane.showMessageDialog(null, message, titre, JOptionPane.INFORMATION_MESSAGE);
                                }
                                // fin modif Julien

			}
                        else if ((source == this.exporterXMI))
                        {
                                CExporterProcessusXMI c = new CExporterProcessusXMI();
                                c.executer();
                        }
                        else if ((source == this.exporterDPC))
                        {
                                CExporterProcessusDPC c = new CExporterProcessusDPC();
                                 // debut modif Julien 2XMI affichage boite de dialogue
                                 if (c.executer())
                                 {
                                     String titre = Application.getApplication().getTraduction("exportComplete");
                                     String message = Application.getApplication().getTraduction("exportCompleteOk");
                                     JOptionPane.showMessageDialog(null, message, titre, JOptionPane.INFORMATION_MESSAGE);
                                     //on efface la liste d'erreurs associer à la transformation XHTML
                                     if (!ArbreGeneration.listeErreurs.isEmpty())
                                     {
                                         ArbreGeneration.listeErreurs.clear();
                                     }
                                 }
                                // fin modif Julien
                        }
                        else if ((source == this.exporterPDF))
                        {
                            CExporterProcessusPDF c = new CExporterProcessusPDF();
                            String titre = Application.getApplication().getTraduction("exportComplete");
                            String message;
                            if (c.executer())
                            {
                                if (!CExporterProcessusPDF.erreurFatale)
                                {

                                    String[] options ={"OK", Application.getApplication().getTraduction("bouton_details")};
                                    int reponse = JOptionPane.showOptionDialog(null, Application.getApplication().getTraduction("exportPDFOk"),
                                                                               titre,
                                                                               JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                                    if(!ArbreGeneration.listeErreurs.isEmpty())
                                    {
                                        CExporterProcessusPDF.ajouterErreursGeneration();
                                    }
                                    if (reponse == JOptionPane.NO_OPTION)
                                    {
                                        CExporterProcessusPDF.afficheListeMessages();
                                    }

                                }
                                else
                                {

                                    String[] options ={"OK", Application.getApplication().getTraduction("bouton_details")};
                                       int reponse = JOptionPane.showOptionDialog(null, Application.getApplication().getTraduction("Generation_PDF_Erreur_Fatale"),
                                                                                  titre,
                                                                                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                                       //si il y a des erreurs de code html on les ajoute à la liste d'erreurs de la génération pdf
                                       if (!ArbreGeneration.listeErreurs.isEmpty())
                                       {
                                           CExporterProcessusPDF.ajouterErreursGeneration();
                                       }
                                       if (reponse == JOptionPane.NO_OPTION)
                                       {
                                           CExporterProcessusPDF.afficheListeMessages();
                                       }
                                }
                                //on vide le log de la génération pdf
                                CExporterProcessusPDF.listeMessages.clear();
                            }
                        }

			// zoom in
			else if  ((source == this.bzoomin) || (source == this.zoomin))
			{
				CZoomer c = new CZoomer(1.25);
				if (c.executer())
				{
					Application.getApplication().getProjet().setModified(true);
				}
			}
			// zoom out
			else if  ((source == this.bzoomout) || (source == this.zoomout))
			{
				CZoomer c = new CZoomer(0.8);
				if (c.executer())
				{
					Application.getApplication().getProjet().setModified(true);
				}
			}

			 // génération boîte dialogue
			 else if ((source == this.generer ))
			 {
			 	JDialog dialogueGen = new FenetrePreference(this, FenetrePreference.TYPE_GENERATION);
			 }
			 // génération rapide
			 else if ((source == this.bgenerer))
			 {
				 Date D = new Date();
				 DateFormat format = new SimpleDateFormat("_ddMMyyyy_HHmmss"); 
				 String date = format.format(D);
				 String cheminGen = Application.getApplication().getReferentiel().getCheminReferentiel()+File.separator+"WebSite"+File.separator+Application.getApplication().getProjet().getDefProc().getNomDefProc()+ date;
					
				 DefinitionProcessus defProc = Application.getApplication().getProjet().getDefProc(); 
				 defProc.setRepertoireGeneration(cheminGen+File.separator);
	             
				 CGenererSite c = null;
                 boolean estDansScenario = false;
                 GraphModelView courant = null;
                 if(((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel()).getType())
                 {
                 	c = new CGenererSite(defProc) ;
                 }
                 else
                 {
                     courant = (GraphModelView) Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel();
                     Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(0));
                     c = new CGenererSite(defProc) ;
                     estDansScenario = true;
                 }

                 if (c.executer())
                 {
                          //ajout avertissement en lorsque des erreurs de transformation XHTML sont rencontrés
                          if (ArbreGeneration.listeErreurs.isEmpty())
                          {
                              int retour = JOptionPane.showConfirmDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("Generation_ok"),
                                                            Application.getApplication().getTraduction("Generation_site_titre"),
                                                            JOptionPane.YES_NO_OPTION);
                              if(retour==JOptionPane.YES_OPTION) {
                             	 try
                             	 {
                             		 Browser.init();
                      				
                      				try {
                      					Browser.displayURL("file:///"+GenerationManager.getInstance().getCheminGeneration().replace(Application.filesep, "/")+"index.html");
                      				} catch (IOException e1) {
                      					// TODO Auto-generated catch block
                      					e1.printStackTrace();
                      				}
                             	 }
                             	 catch(Exception exc)
                             	 {
                             	    exc.printStackTrace();
                             	 } 
                              }
                          }
                          else
                          {
                              String[] options = {"OK", Application.getApplication().getTraduction("bouton_details")};
                              int reponse = JOptionPane.showOptionDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("Generation_XHTML_Non_ok"),
                                                                         Application.getApplication().getTraduction("Generation_site_titre"),
                                                                         JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                              if (reponse == JOptionPane.NO_OPTION)
                              {
                                  ArbreGeneration.afficheListeErreurs();
                              }
                              //on vide la liste d'erreurs
                              ArbreGeneration.listeErreurs.clear();
                          }
                          //fin modif Sébastien
                 }
                 if(estDansScenario) Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel(courant);
				 /*CGenererSite c = new CGenererSite(Application.getApplication().getProjet().getDefProc()) ;
				if (c.executer())
				{
                                    //2XMI modif Sébastien
                                    //ajout avertissement en lorsque des erreurs de transformation XHTML sont rencontrés
                                    if(ArbreGeneration.listeErreurs.isEmpty())
                                    {
                                        JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("Generation_ok"),
                                                                        Application.getApplication().getTraduction("Generation_site_titre"),
                                                                        JOptionPane.INFORMATION_MESSAGE);
                                    }
                                    else
                                    {
                                        String[] options = { "OK", Application.getApplication().getTraduction("bouton_details") };
                                        int reponse = JOptionPane.showOptionDialog(null, Application.getApplication().getTraduction("Generation_XHTML_Non_ok"),
                                                                     Application.getApplication().getTraduction("Generation_site_titre"),
                                                                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                                        if(reponse == JOptionPane.NO_OPTION)
                                        {
                                            ArbreGeneration.afficheListeErreurs();
                                        }
                                        //on vide la liste d'erreurs
                                        ArbreGeneration.listeErreurs.clear();
                                    }
                                    //fin modif
				}*/
			 }

			else if ((source == this.propriete ) || source == this.bpreferences)
			{
				JDialog dialogueProp = new FenetrePreference(this, FenetrePreference.TYPE_PREFERENCES);
			
			}
			else if (source == this.propos)
			{
				new FenetrePropos(this);
			}
	  }

	  /*
	  public void afficherPanneauVerification(Vector entree, Vector sortie, Vector composant)
	  {
	  	this.panneauVerif.showErrorArea();
	  	this.panneauVerif.setListeErreurs(entree, sortie, composant);
	  }

	  public PanneauVerification getPanneauVerif()
	  {
	  	return this.panneauVerif;
	  }
	  */

	private class FenetreListener extends WindowAdapter
	{
		/**
		 *
		 */
		public void windowClosing(WindowEvent e)
		{
			if ( Application.getApplication().getProjet() != null)
			{
				if (Application.getApplication().getProjet().estModifie())
				{
					  int choice = JOptionPane.showConfirmDialog(
				  			 Application.getApplication().getFenetrePrincipale(),
							 Application.getApplication().getTraduction("BD_SAUV_AVAT_FERMER"),
							 Application.getApplication().getTraduction("Confirmation"),
							 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

					  // si "oui" l'utilisateur accepte, on demande la sauvegarde
					  if( choice == JOptionPane.YES_OPTION )
					  {
					  		//sauver la dp courante
					  		Referentiel ref = Application.getApplication().getReferentiel() ;
					  		ref.sauverDefProc (Application.getApplication().getProjet().getDefProc()) ;
					  		Application.getApplication().getProjet().setModified(false);
					  }
					  if ( choice == JOptionPane.CANCEL_OPTION)
					  {
					  	return;
					  }
				}
				CFermerProjet c = new CFermerProjet();
				c.executer();
			}
			System.exit(0);
		}
	}
}
