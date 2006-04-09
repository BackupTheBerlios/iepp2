
package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.zip.ZipInputStream;



public class ToolKit
{

	/** Retourne le chemin relatif du répertoire de la donnée pointée par le chemin absolu spécifi\uFFFD
	   *  Si le r\uFFFDpertoire sp\uFFFDcifi\uFFFD n'est pas un fils de Global.getDataPath() ou si un probl\uFFFDme a \uFFFDt\uFFFD recontr\uFFFD,
	   *  le r\uFFFDpertoire sp\uFFFDcifi\uFFFD sera retourn\uFFFD.
	   *@param absolutePath  Chemin absolu
	   *@param pathRef  Chemin de r\uFFFDf\uFFFDrence \uFFFD partir du quel le chemin relatif sera construit
	   *@return  Chemin relatif du r\uFFFDpertoire point\uFFFD par le chemin absolu <i>absolutePath</i> sp\uFFFDcifi\uFFFD
	   */
	  public static String getRelativePathOfAbsolutePath(String absolutePath, String pathRef)
	  {
	    String absoluteRefPath = (new File(getSlashTerminatedPath(pathRef))).getAbsolutePath();

	    absoluteRefPath = removeDotDotInPath(absoluteRefPath);

	    absolutePath = absolutePath.replace('\\', '/');
	    absoluteRefPath = absoluteRefPath.replace('\\', '/');

	    absolutePath = getSlashTerminatedPath(absolutePath);
	    absoluteRefPath = getSlashTerminatedPath(absoluteRefPath);


	    // Si la donn\uFFFDe est dans le r\uFFFDpertoire de r\uFFFDf\uFFFDrence, on retourne ce r\uFFFDpertoire
	    if (absolutePath.compareTo(absoluteRefPath) == 0) return (getSlashTerminatedPath(pathRef));

	    // Si le r\uFFFDpertoire sp\uFFFDcifi\uFFFD n'est pas un fils du r\uFFFDpertoire de r\uFFFDf\uFFFDrence, on abandonne
	    // sinon, on retourne le chemin relatif
	    if (absolutePath.indexOf(absoluteRefPath) == -1)
	      return (absolutePath);
	    else
	      return (/*getSlashTerminatedPath(pathRef) + */ absolutePath.substring(absoluteRefPath.length()));
	  }


	  /** Retourne le chemin sp\uFFFDcifi\uFFFD en ajoutant un "/" \uFFFD la fin s'il ne se finissait ni par "/" ni par "\"
	   *@param path  Chemin dont on doit retourner une forme normalis\uFFFDe
	   *@return  Chemin sp\uFFFDcifi\uFFFD termin\uFFFD par un "/" \uFFFD la fin s'il ne se finissait ni par "/" ni par "\".<BR>
	             Retourne "" si <I>path</I> == ""
	   */
	  public static String getSlashTerminatedPath(String path)
	  {
	    if (path.equals("")) return "";
	    return ( (path.endsWith("/") || path.endsWith("\\")) ? path : (path + "/") );
	  }

	  public static String removeSlashTerminatedPath(String path)
	  {
            if (path.endsWith("/"))return path.substring(0, path.length() - 1);
            if (path.endsWith("\\"))return path.substring(0, path.length() - 1);
            return path;
          }

	  /** Supprime les "../" (ou "..\") dans le chemin sp\uFFFDcifi\uFFFD.
	   *  ex : removeDotDotInPath("progs/alpaga/../data") retournera la cha\uFFFDne "progs/data"
	   *@param path   Chemin dans lequel les "../" devront \uFFFDtre supprim\uFFFDs
	   *@return  Chemin physiquement identique \uFFFD <b>path</b>, mais ne contenant pas de "../"
	   */
	  public static String removeDotDotInPath(String path)
	  {
	    Stack dirs = new Stack();
	    Object[] dirsArray;
	    int pos = 0, newPos = 0;
	    String formattedPath = "", newDir;

	    // Remplace tous les "\" par des "/"
	    path = path.replace('\\', '/');

	    // Rajoute un "/" \uFFFD la fin si c'est pas d\uFFFDj\uFFFD fait
	    path = getSlashTerminatedPath(path);

	    pos = path.indexOf("/");

	    // Si le chemin sp\uFFFDcifi\uFFFD ne contient aucun slash, on ne peut plus rien pour lui....
	    if (pos == -1) return (path);

	    if (pos == 0)
	    {
	      formattedPath = "/";
	      pos = 1;
	    }
	    else
	      pos = 0;

	    while (newPos != -1)
	    {
	      newPos = path.indexOf("/", pos);
	      if (newPos != -1)
	      {
	        newDir = path.substring(pos, newPos);
	        if (newDir.compareTo("..") == 0)
	          dirs.pop();
	        else
	          dirs.push(newDir);
	        pos = newPos;
	      }
	      pos++;
	    }

	    dirsArray = dirs.toArray();
	    for (int i=0 ; i<dirsArray.length ; i++)
	      formattedPath += ((i!=0) ? "/" : "") + dirsArray[i].toString();

	    return (formattedPath + "/");
	  }

	  /** Retourne le chemin absolu courrant
	   *@return  Chemin absolu courrant
	   */
	  public static String getAbsoluteDirectory(String nomFic)
          {
            if (nomFic.startsWith("./") || nomFic.startsWith(".\\")) {
              nomFic = getCurrentAbsoluteDirectory() + nomFic.substring(2);
            }

            while (nomFic.endsWith("/") || nomFic.endsWith("\\"))
              nomFic = removeSlashTerminatedPath(nomFic);

            File currDir = new File(nomFic);
            if (currDir.exists()) {
              String currDirectory = currDir.getAbsolutePath();
              return removeSlashTerminatedPath(currDirectory);
            }
            else {
              return nomFic;
            }
          }

	  public static String getCurrentAbsoluteDirectory()
	  {
	    File currDir = new File(".");
	    String currDirectory = currDir.getAbsolutePath();
	    return (currDirectory.substring(0, currDirectory.length()-1));
	  }

	  public static String getConcatePath (String path, String nomFic)
	  {
	  	if (nomFic.startsWith("./") || nomFic.startsWith(".\""))
	  	{
	  		nomFic = nomFic.substring(2);
	  	}
	  	return (getSlashTerminatedPath(path) + nomFic);
	  }


	  public static void dezipper( ZipInputStream zin, FileOutputStream fout) throws IOException
		{
			//Copier les flux:
			synchronized (zin)
			{
				synchronized (fout)
				{
					  byte[] buffer = new byte[256];
					  while (true)
					  {
						  int bytesRead = zin.read(buffer);
						  if (bytesRead == -1) break;
						  fout.write(buffer, 0, bytesRead);
					  }
				 }
			 }
			 // Fermer l'entrée et le flux de sortie
			zin.closeEntry();
			fout.close();
		}

        //modif 2XMI Amandine
        /** Supprime le repertoire portant le nom fileName - utiliser oors de l'export DPC
          *@param fileName   Nom du repertoire a supprimer
        */
        public static void delFile(String fileName){
          File fich = new File(fileName);
          if (fich.exists()) {
            while (! (fich.delete()))
              delFile( (fich.listFiles())[0].getAbsolutePath());
            }
        }
}
