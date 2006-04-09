package util;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class FileFinder {

	private String racine;
	private String extension;
	private int nbFichiers=0;
	private ArrayList listeFichiers;
	
	// Constructeur d'Objet
	public FileFinder(String rep, String ext){
		this.racine=rep;
		this.extension=ext;
		listeFichiers = new ArrayList();
		//Lancer la recherche
		this.Recherche(this.racine,this.extension);
		
	}
	
	// Setteurs d'Attributs
	
	// Methode de changement du repertoire racine
	public void setRacine (String rep){
		this.racine=rep;
		listeFichiers = new ArrayList();
		// On relance la recherche par rapport à la nouvelle racine
		this.Recherche(this.racine,this.extension);
		
	}
	// Methode de changement de l'extension
	public void setExtension (String ext){
		this.extension=ext;
		listeFichiers = new ArrayList();
		// On relance la recherche par rapport à la nouvelle racine
		this.Recherche(this.racine,this.extension);
		
	}
	
	// Getteurs d'Attributs
	
	// Methode de renvoie du repertoire racine
	public String getRacine(){
		return this.racine;
	}
	// Methode de renvoie de l'extension
	public String getExtension(){
		return this.extension;
	}
	// Methode de renvoie du nombre de fichiers trouvés
	public int getNbFichiers(){
		return this.nbFichiers;
	}
	// Methode de renvoie d'un Fichier de la liste
	public String getFichier(int pos){
		return (String) this.listeFichiers.get(pos);
	}
	// Methode de renvoie de la Liste des Fichiers
	public ArrayList getListeFichiers (){
		return this.listeFichiers;
	}
	
	// Methode de Recherche de Fichiers
	public void Recherche(String rep, String ext){
		
		File f = new File(rep);
		// On liste les fichiers/repertoires contenus dans la racine
		String [] files = f.list();
		
		if ( files != null ){
			// Si on trouve pas de fichier, met le compteur à 0
			this.nbFichiers = 0;
			
			for (int i=0;i<files.length;i++){
				
				if (new File(rep + files[i]).isDirectory()) {
					// On ne parcours pas les sous dossiers
					// Au cas ou:
					//this.Recherche(rep + files[i],ext);
				}
				else{			
				//-------------------ajout d'un fichier--------------
					if (files[i].endsWith(ext)){
						this.listeFichiers.add(rep + files[i]);
						this.nbFichiers=this.nbFichiers+1;
					}
				//---------------------------------------------------
				}
			}
		}else{
			this.nbFichiers = 0;
		}
	}
}
