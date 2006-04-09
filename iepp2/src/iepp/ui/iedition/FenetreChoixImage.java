package iepp.ui.iedition;

import iepp.Application;
import iepp.ui.iedition.dessin.rendu.IeppCell;

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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;

import util.FileFinder;

public class FenetreChoixImage extends JDialog {
	// ------------------------------------
	ImageIcon[] images;
	
	private String choix;
	private boolean clikok;
	private String pathUser;
	private String pathIepp;

	// adresse du dossier qui contient les images

	// -----------------------
	final static int HAUTEUR = 200;
	final static int LARGEUR = 800;
	
	final static int imgMinL = 10;
	final static int imgMinH = 10;
	final static int imgMaxL = 100;
	final static int imgMaxH = 100;
		
	public FenetreChoixImage(String chemin) {
		
		// Création de la fenetre modale
		super(Application.getApplication().getFenetrePrincipale(),Application.getApplication().getTraduction("Changer_Image"),true);

		// Taille de la fenetre (marche pas !!!)
		this.setSize(LARGEUR, HAUTEUR);
		
		// Récupération de la fenetre parent pour la position de la fenetre
		Frame parent = Application.getApplication().getFenetrePrincipale();
		
		// Calcule de la position
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - FenetreCreationLienDynamique.LARGEUR / 2, bounds.y + bounds.height / 2 - FenetreCreationLienDynamique.HAUTEUR / 2);
		
		this.clikok = false;
		
		this.pathUser = chemin;
		this.pathIepp = IeppCell.cheminImageComposantIepp;
		
		// Recherche des images
		//FileFinder fichier = new FileFinder(this.path, "");
		FileFinder fichierPngIepp = new FileFinder(this.pathIepp, "png");
		FileFinder fichierJpgIepp = new FileFinder(this.pathIepp, "jpg");
		FileFinder fichierJpegIepp = new FileFinder(this.pathIepp, "jpeg");
		FileFinder fichierPngUser = new FileFinder(this.pathUser, "png");
		FileFinder fichierJpgUser = new FileFinder(this.pathUser, "jpg");
		FileFinder fichierJpegUser = new FileFinder(this.pathUser, "jpeg");
		
		Vector nomFichierImages = new Vector();

		// traitement pour la recherche des images dans un fichier
		for (int i = 0; i < fichierPngIepp.getNbFichiers(); i++) {
			
			ImageIcon img = new ImageIcon(fichierPngIepp.getFichier(i));
			
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) )
			{
				nomFichierImages.add(fichierPngIepp.getFichier(i));
			}
		}
		for (int i = 0; i < fichierJpgIepp.getNbFichiers(); i++) {
			
			ImageIcon img = new ImageIcon(fichierJpgIepp.getFichier(i));
			
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) )
			{
				nomFichierImages.add(fichierJpgIepp.getFichier(i));
			}
		}
		for (int i = 0; i < fichierJpegIepp.getNbFichiers(); i++) {
			
			ImageIcon img = new ImageIcon(fichierJpegIepp.getFichier(i));
			
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) )
			{
				nomFichierImages.add(fichierJpegIepp.getFichier(i));
			}
		}
		//traitement pour la recherche des images dans un fichier
		for (int i = 0; i < fichierPngUser.getNbFichiers(); i++) {
			
			ImageIcon img = new ImageIcon(fichierPngUser.getFichier(i));
			
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) )
			{
				nomFichierImages.add(fichierPngUser.getFichier(i));
			}
		}
		for (int i = 0; i < fichierJpgUser.getNbFichiers(); i++) {
			
			ImageIcon img = new ImageIcon(fichierJpgUser.getFichier(i));
			
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) )
			{
				nomFichierImages.add(fichierJpgUser.getFichier(i));
			}
		}
		for (int i = 0; i < fichierJpegUser.getNbFichiers(); i++) {
			
			ImageIcon img = new ImageIcon(fichierJpegUser.getFichier(i));
			
			// pour réglementer la taille de l'image
			if(	!(img.getIconHeight() > imgMaxH || img.getIconWidth() > imgMaxL || img.getIconHeight() < imgMinH || img.getIconWidth() < imgMinL) )
			{
				nomFichierImages.add(fichierJpegUser.getFichier(i));
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
			JLabel lab2 = new JLabel("  -" + this.pathIepp);
			JLabel lab7 = new JLabel("  -" + this.pathUser);
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
			setVisible(false);
		
			annuler.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					choix = null;
					clikok = false;
					setVisible(false);
				}
			});
			
		}else{
		
			final String petStrings[] = new String[nomFichierImages.size()];
	
			for (int i = 0; i < nomFichierImages.size(); i++) {
				petStrings[i] = (String)nomFichierImages.elementAt(i);
			}
			
			JPanel monpanneau = new JPanel();
			TitledBorder titledBorder1 = new TitledBorder(Application.getApplication().getTraduction("Changer_Image"));
			JPanel panneaubouton = new JPanel();
			panneaubouton.setLayout(new FlowLayout());
			JButton ok = new JButton(Application.getApplication().getTraduction("Valider"));
			JButton annuler = new JButton(Application.getApplication().getTraduction("Annuler"));
			monpanneau.setLayout(new BorderLayout(1,1));
			monpanneau.setSize(LARGEUR, HAUTEUR);
			
			// création du deuxieme panel de la fenetre
			JPanel deuxiemepanel = new JPanel();
			deuxiemepanel.setLayout(new BorderLayout());
	
			// seconde liste deroulant
			final JComboBox list = CustomComboBoxDemo(petStrings);
			// list2.setBackground(Color.WHITE);
			deuxiemepanel.add(list,BorderLayout.CENTER);
			deuxiemepanel.setBorder(titledBorder1);
	
			monpanneau.add(deuxiemepanel,BorderLayout.CENTER);
	
			panneaubouton.add(ok);
			panneaubouton.add(annuler);
			monpanneau.add(panneaubouton,BorderLayout.SOUTH);
	
			list.setEnabled(true);
			
			getContentPane().add(monpanneau);
			pack();
			setVisible(false);
	
			
			
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					File fimg = new File(petStrings[list.getSelectedIndex()]);
					choix = fimg.getName();
					clikok = true;
					setVisible(false);
				}
					
			});
			
			annuler.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					choix = null;
					clikok = false;
					setVisible(false);
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

	public boolean afficheDialog() {
		choix = new String();
		setVisible (true); // reste coincé ici jusqu’au clic
		return clikok;
	}
	
	public String nomImages(){
		return this.choix;
	}
	
//	public void setNomImages(String nom){
//		this.choix = nom;
//	}
	
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
				setUhOhText(temp + " (no image available)", list.getFont());
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
