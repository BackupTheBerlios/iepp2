package iepp.ui.iedition;

import iepp.Application;
import iepp.application.aedition.CLier2ComposantDyn;
import iepp.domaine.ComposantProcessus;
import iepp.ui.FenetrePrincipale;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeDyn;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;

import org.jgraph.graph.PortView;

import util.FileFinder;

public class FenetreCreationLienDynamique extends JDialog {
	// ------------------------------------
	ImageIcon[] images;

	private String path1;
	private String path2;
	
	// adresse du dossier qui contient les images

	// -----------------------
	final static int HAUTEUR = 200;
	final static int LARGEUR = 300;
	private final VueDPGraphe mGraph;
	private ComposantCellDyn compoS,compoD;
	private DocumentCellDyn cellDes,cellSrc;

	private PortView mStart,mCurrent;

	final static int imgMinL = 10;
	final static int imgMinH = 10;
	final static int imgMaxL = 100;
	final static int imgMaxH = 100;

	
	private FenetrePrincipale fenetrePrincipale = null;


	public FenetreCreationLienDynamique(Frame parent,VueDPGraphe graph,ComposantCellDyn cS,ComposantCellDyn cD,DocumentCellDyn cDes, DocumentCellDyn cSrc) {

		
		super(parent);
		this.mGraph=graph;
		this.compoS=cS;
		this.compoD=cD;
		this.cellDes=cDes;
		this.cellSrc=cSrc;


		//FenetrePrincipale parent;
		//this.fenetrePrincipale = parent;
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - FenetreCreationLienDynamique.LARGEUR / 2, bounds.y + bounds.height / 2 - FenetreCreationLienDynamique.HAUTEUR / 2);
		
		this.path1 = IeppCell.cheminImageProduitIepp;
		this.path2 = IeppCell.cheminImageProduitUser;
			
		
		// Recherche des images
		//FileFinder fichier = new FileFinder(this.path, "");
		FileFinder fichierPng1 = new FileFinder(this.path1, "png");
		FileFinder fichierJpg1 = new FileFinder(this.path1, "jpg");
		FileFinder fichierJpeg1 = new FileFinder(this.path1, "jpeg");
		
		FileFinder fichierPng2 = new FileFinder(this.path2, "png");
		FileFinder fichierJpg2 = new FileFinder(this.path2, "jpg");
		FileFinder fichierJpeg2 = new FileFinder(this.path2, "jpeg");
		
		Vector nomFichierImages = new Vector();

		// traitement pour la recherche des images dans un répertoire 1
		for (int i = 0; i < fichierPng1.getNbFichiers(); i++) {
			ImageIcon img = new ImageIcon(fichierPng1.getFichier(i));
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) ){
				nomFichierImages.add(fichierPng1.getFichier(i));
			}
		}
		for (int i = 0; i < fichierJpg1.getNbFichiers(); i++) {
			ImageIcon img = new ImageIcon(fichierJpg1.getFichier(i));
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) ){
				nomFichierImages.add(fichierJpg1.getFichier(i));
			}
		}
		for (int i = 0; i < fichierJpeg1.getNbFichiers(); i++) {
			ImageIcon img = new ImageIcon(fichierJpeg1.getFichier(i));
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) ){
				nomFichierImages.add(fichierJpeg1.getFichier(i));
			}
		}
		
		//traitement pour la recherche des images dans un répertoire 2
		for (int i = 0; i < fichierPng2.getNbFichiers(); i++) {
			ImageIcon img = new ImageIcon(fichierPng2.getFichier(i));
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) ){
				nomFichierImages.add(fichierPng2.getFichier(i));
			}
		}
		for (int i = 0; i < fichierJpg2.getNbFichiers(); i++) {
			ImageIcon img = new ImageIcon(fichierJpg2.getFichier(i));
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) ){
				nomFichierImages.add(fichierJpg2.getFichier(i));
			}
		}
		for (int i = 0; i < fichierJpeg2.getNbFichiers(); i++) {
			ImageIcon img = new ImageIcon(fichierJpeg2.getFichier(i));
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) ){
				nomFichierImages.add(fichierJpeg2.getFichier(i));
			}
		}
		
		if (nomFichierImages.size() == 0){
		
			JPanel monpanneau = new JPanel();
			TitledBorder titledBorder1 = new TitledBorder(Application.getApplication().getTraduction("Changer_Image"));
			JPanel panneaubouton = new JPanel();
			panneaubouton.setLayout(new FlowLayout());
			JButton annuler = new JButton(Application.getApplication().getTraduction("Annuler"));
			monpanneau.setLayout(new BorderLayout(1,1));
			monpanneau.setSize(LARGEUR, HAUTEUR);
			
			// création du deuxieme panel de la fenetre
			JPanel deuxiemepanel = new JPanel();
			deuxiemepanel.setLayout(new GridLayout(7,1));
	
			// seconde liste deroulant
			JLabel lab1 = new JLabel(Application.getApplication().getTraduction("Pas_Image") + " :" );
			JLabel lab2 = new JLabel("  -" + this.path1);
			JLabel lab7 = new JLabel("  -" + this.path2);
			JLabel lab3 = new JLabel("Size :");
			JLabel lab4 = new JLabel("         - Min: 10x10,");
			JLabel lab5 = new JLabel("         - Max: 100x100");
			JLabel lab6 = new JLabel("Format : png, jpg, jpeg");
			
			deuxiemepanel.add(lab1);
			deuxiemepanel.add(lab2);
			deuxiemepanel.add(lab7);
			deuxiemepanel.add(lab3);
			deuxiemepanel.add(lab4);
			deuxiemepanel.add(lab5);
			deuxiemepanel.add(lab6);
			
			deuxiemepanel.setBorder(titledBorder1);
	
			monpanneau.add(deuxiemepanel,BorderLayout.CENTER);
	
			panneaubouton.add(annuler);
			monpanneau.add(panneaubouton,BorderLayout.SOUTH);
	
			
			getContentPane().add(monpanneau);
			pack();
			setVisible(true);
			
			annuler.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					dispose();
				}
			});
			
		}else{
		
			// repertoire qui contient les images
			int nombre = nomFichierImages.size();
	
			final String petStrings[] = new String[nombre];
			setTitle(Application.getApplication().getTraduction("new_link"));
	
			setSize(LARGEUR, HAUTEUR);
	
			TitledBorder titledBorder2 = new TitledBorder(Application.getApplication().getTraduction("define"));
			TitledBorder titledBorder1 = new TitledBorder(Application.getApplication().getTraduction("not_define"));
	
			Vector listpcf=((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(0)).getProduitCellFusionCells();
			
			final Vector elements1 = new Vector();
			for(int i=0 ; i<listpcf.size() ; i++) {
				ProduitCellFusion pcf = (ProduitCellFusion) listpcf.get(i);
				//produit en entr?e
				
				//produit sortie
				ProduitCellSortie pcs = pcf.getProduitCellSortie();
				
				//recuperation de l'ID
				ComposantProcessus idcs = pcs.getCompParent().getCompProc();
				
				for(int s=0;s<pcf.getProduitCellEntrees().size();s++){
				
					ProduitCellEntree pce = (ProduitCellEntree)pcf.getProduitCellEntrees().elementAt(s); 
					//recuperation de l'ID
					ComposantProcessus idce = pce.getCompParent().getCompProc();
					
					//recuperation de l'ID
					ComposantProcessus idcS = cS.getCompProc();
					ComposantProcessus idcD = cD.getCompProc();	
					//System.out.println("premier identifiant:"+idce+"deuxieme identifiant"+idcs+"troisieme identifiant"+idcS+"quatrieme identifiant"+idcD);
				    String nomduproduit = pcf.getNomCompCell();
				    if(idce.equals(idcD) && idcs.equals(idcS))
				    {
				    //	System.out.println("nom du produit :"+nomduproduit);
				    	elements1.add(nomduproduit);
				    }
				    
				}
				
				
			    
			}
			
			
			JPanel monpanneau = new JPanel();
	
			JPanel panneaubouton = new JPanel();
			panneaubouton.setLayout(new FlowLayout());
			JButton ok = new JButton(Application.getApplication().getTraduction("ok"));
			JButton annuler = new JButton(Application.getApplication().getTraduction("cancel"));
	
			// traitement pour la recherche des images dans un fichier
	
			for (int i = 0; i < nomFichierImages.size(); i++) {
				petStrings[i] = (String)nomFichierImages.elementAt(i);
			}
	
			monpanneau.setLayout(new BorderLayout());
			
			// cr?ation du premier pannel
			JPanel premierpannel = new JPanel();
			premierpannel.setLayout(new GridLayout(2, 1));
			premierpannel.setBorder(titledBorder2);
	
			// bouton radio
			final JRadioButton defini = new JRadioButton(Application.getApplication().getTraduction("define"));
			premierpannel.add(defini);
	
			defini.setSelected(false);
			
			// premiere liste deroulante
			/*for (int i=0;i<graph.getLiens().size();i++){
				elements1.add(((LienEdgeDyn)graph.getLiens().elementAt(i)).getValeur());
			}*/
			final JComboBox list2 = new JComboBox(elements1);
	
			premierpannel.add(list2);
			if(elements1.size()!= 0)
			{
				defini.setSelected(true);
				monpanneau.add(premierpannel,BorderLayout.NORTH);
			}
			
			// cr?ation du deuxieme panel de la fenetre
			JPanel deuxiemepanel = new JPanel();
			deuxiemepanel.setLayout(new BorderLayout());
	
			final JRadioButton ndefini = new JRadioButton(Application.getApplication().getTraduction("not_define"));
			final ButtonGroup group = new ButtonGroup();
			
			
			
			group.add(defini);
			group.add(ndefini);
	
			
			
			deuxiemepanel.add(ndefini,BorderLayout.NORTH);
	
			// textbox
			final JTextField texte = new JTextField(20);
			texte.setText(Application.getApplication().getTraduction("set_text"));
			deuxiemepanel.add(texte,BorderLayout.CENTER);
	
			
			
			// seconde liste deroulant
			final JComboBox list = CustomComboBoxDemo(petStrings);
			// list2.setBackground(Color.WHITE);
			deuxiemepanel.add(list,BorderLayout.SOUTH);
			deuxiemepanel.setBorder(titledBorder1);
	
			monpanneau.add(deuxiemepanel,BorderLayout.CENTER);
	
			panneaubouton.add(ok);
			panneaubouton.add(annuler);
			monpanneau.add(panneaubouton,BorderLayout.SOUTH);
	
			
	
			if(!defini.isSelected()){
				ndefini.setSelected(true);
				ndefini.setEnabled(false);
				list.setEnabled(true);
				defini.setSelected(false);
				texte.setEnabled(true);
				texte.selectAll();
				list2.setEnabled(false);
			}else{
				defini.setSelected(true);
				defini.grabFocus();
				defini.setEnabled(true);
				list.setEnabled(false);
				texte.setEnabled(false);
				ndefini.setSelected(false);
				ndefini.setEnabled(true);
				list2.setEnabled(true);
			}
			
			getContentPane().add(monpanneau);
			pack();
			setVisible(true);
	
			defini.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					list.setEnabled(false);
					texte.setEnabled(false);
					list2.setEnabled(true);
				}
	
			});
	
			ndefini.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					list.setEnabled(true);
					texte.setEnabled(true);
					texte.requestFocusInWindow();
					texte.selectAll();
					list2.setEnabled(false);
				}
			});
			
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
		
					if (defini.isSelected()){
						String im="produitLie.png";
						for (int i=0;i<mGraph.getLiens().size();i++){
							LienEdgeDyn edge=((LienEdgeDyn)mGraph.getLiens().elementAt(i));
							if(edge.getTextAssocie().equals((String)list2.getSelectedItem())){
								im=edge.getImage();
							}
						}
	
						CLier2ComposantDyn c = new CLier2ComposantDyn(mGraph, compoS,compoD,cellDes,cellSrc, new Vector(),(String)list2.getSelectedItem(),im);
	
						if (c.executer())
			    		   {
			    			   Application.getApplication().getProjet().setModified(true);
			      		 	}
					}
					else{
						File fimg = new File(petStrings[list.getSelectedIndex()]);
						String nomImage = fimg.getName();
						CLier2ComposantDyn c = new CLier2ComposantDyn(mGraph, compoS,compoD,cellDes,cellSrc, new Vector(),texte.getText(),nomImage);
		
						if (c.executer())
			    		   {
			    			   Application.getApplication().getProjet().setModified(true);
			      		 	}
					}
					dispose();
					//Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setOutilLier();
				}
			});
			
			annuler.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					dispose();
				}
			});
		}
	}

	public JComboBox CustomComboBoxDemo(String[] petStrings) {

		// Load the pet images and create an array of indexes.
		images = new ImageIcon[petStrings.length];
		Integer[] intArray = new Integer[petStrings.length];
		for (int i = 0; i < petStrings.length; i++) {

			intArray[i] = new Integer(i);
			images[i] = new ImageIcon(petStrings[i]);
			if (images[i] != null) {
				images[i].setDescription(petStrings[i]);
			}

		}

		// Create the combo box.
		JComboBox petList = new JComboBox(intArray);
		ComboBoxRenderer renderer = new ComboBoxRenderer(petStrings);
		// renderer.setPreferredSize(new Dimension(50, 50));
		petList.setRenderer(renderer);
		petList.setMaximumRowCount(3);

		return petList;

	}

	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		private Font uhOhFont;

		public String[] petStrings;

		public ComboBoxRenderer(String[] petStrings) {
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
			this.petStrings = petStrings;
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			// Get the selected index. (The index param isn't
			// always valid, so just use the value.)
			int selectedIndex = ((Integer) value).intValue();

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			// Set the icon and text. If icon was null, say so.
			ImageIcon icon = images[selectedIndex];
			String pet = petStrings[selectedIndex];
			File fimg = new File(pet); 
			String temp = fimg.getName().substring(0,fimg.getName().lastIndexOf("."));
			
			setIcon(icon);
			if (icon != null) {
				setText(temp);
				setFont(list.getFont());
			} else {
				setUhOhText(temp + Application.getApplication().getTraduction("no_icon"), list.getFont());
			}

			return this;
		}

		// Set the font and text when no image was found.
		protected void setUhOhText(String uhOhText, Font normalFont) {
			if (uhOhFont == null) { // lazily create this font
				uhOhFont = normalFont.deriveFont(Font.ITALIC);
			}
			setFont(uhOhFont);
			setText(uhOhText);
		}
	}

}
