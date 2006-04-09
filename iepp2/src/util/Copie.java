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


package util;

import iepp.Application;

import java.io.* ;

/**
 * Classe permettant de copier des fichiers ou des répertoires
 */
public class Copie
{
	
	/**
	* Recopie une arborescence (fichiers et sous-repertoires) dans un repertoire
	* Cree ce repertoire s'il n'existe pas
	* Leve une exception si repSource n'est pas un repertoire ou s'il n'existe pas
	* Utilise le caractere de separation de repertoires donne par JAVA (variable suivant systeme)
	*@param repSource repertoire à recopier
	*@param repDest repertoire de destination de la copie
	*/
	public static void copieRep (String repSource, String repDest) throws Exception
	{
		// Recuperer le caractere de separation des repertoires (depend du systeme)
		String separ = File.separator ;

		// Creer un objet File designant le repertoire source et verifier que c'est un repertoire
		File fRepSource = new File (repSource) ;
		if (!fRepSource.exists()) throw new Exception (Application.getApplication().getTraduction("ERR_Fichier_Non_Trouve")) ;
		if (!fRepSource.isDirectory()) throw new Exception (Application.getApplication().getTraduction("ERR_Pas_Repertoire")) ;

		// Creer un objet representant le repertoire destination et le creer s'il n'existe pas deja
		File fRepDest = new File (repDest) ;
		if (!fRepDest.exists())
			if (!fRepDest.mkdirs()) throw new Exception (Application.getApplication().getTraduction("ERR_Impos_Creer_Rep"));

		// Tableau contenant les fichiers et sous-repertoires du repertoire source
		File listeFic[] ;
		listeFic = fRepSource.listFiles() ;

		// Parcourir les fichiers recuperes
		for (int i=0 ; i<listeFic.length ; i++)
		{
			File ficCour = listeFic[i] ;	// Reference plus commode
			// Si c'est un repertoire
			if (ficCour.isDirectory())
			{
				// Rappeler la procedure pour ce repertoire
				copieRep (ficCour+"", repDest+separ+ficCour.getName()) ;
			}
			// Sinon, c'est un fichier
			else
			{
				copieFic (ficCour.toString(), repDest) ;
			}
		}
	}

	/**
	* Recopie un fichier dans le repertoire indique
	* Leve une exception si le fichier source ou le repertoire destination n'existe pas
	* Utilise le caractere de separation de repertoires donne par JAVA (variable suivant systeme)
	* 
	*/
	public static void copieFic (String ficSource, String repDest) throws Exception
	{
		
		// Recuperer le caractere de separation des repertoires (depend du systeme)
		String separ = File.separator ;
			
		// Verifier que le fichier et le repertoire existent
		File fSource = new File (ficSource) ;
		if (!fSource.exists()) throw new Exception (Application.getApplication().getTraduction("ERR_Rep_Source_Non_Trouve")) ;
		File fDest = new File (repDest) ;
		if (!fDest.exists()) throw new Exception (Application.getApplication().getTraduction("ERR_Rep_Dest_Non_Trouve"));
		Copie.copieFicCh(ficSource, repDest+separ+fSource.getName());
 
	}
	
	/**
	* Recopie un fichier vers un autre répertoire, les noms des fichiers peuvent être différents
	* Leve une exception si le fichier source ou le repertoire destination n'existe pas
	* Utilise le caractere de separation de repertoires donne par JAVA (variable suivant systeme)
	* 
	*/
	public static void copieFicCh (String ficSource, String ficDest) throws Exception
	{
		// Recuperer le caractere de separation des repertoires (depend du systeme)
		String separ = File.separator ;
			
		// Verifier que le fichier et le repertoire existent
		File fSource = new File (ficSource) ;
		if (!fSource.exists()) throw new Exception (Application.getApplication().getTraduction("ERR_Rep_Source_Non_Trouve")) ;
		File fDest = new File (ficDest) ;
		File path = fDest.getParentFile();
		if (!path.exists()) throw new Exception (Application.getApplication().getTraduction("ERR_Rep_Dest_Non_Trouve"));
  
  		// Ouvrir le fichier source en ecriture
		FileInputStream in = new FileInputStream(ficSource) ;
		// Ouvrir un fichier en ecriture dans le repertoire destination
  		FileOutputStream out = new FileOutputStream(ficDest) ;
  
  		// optimise la copie d'un fichier à l'aide d'un buffer
		BufferedOutputStream bos = new BufferedOutputStream(out);
		BufferedInputStream bis = new BufferedInputStream(in);
		
		
		// Lecture par segments disponibles 
		int taille = in.available();
		// si le fichier est vide, on ne fait rien
		if (taille != 0)
		{
			byte buffer[]=new byte[taille];
			int nbLecture;
	
			// lecture + écriture
			while( (nbLecture = bis.read(buffer)) != -1 )
			{
	        		bos.write(buffer, 0, nbLecture);
			} 
		}
		// Fermer les flux vers les fichiers
		bos.close();
		bis.close();
 
	}
	
}
