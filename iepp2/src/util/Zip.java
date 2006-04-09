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

import java.io.Serializable;
import java.util.Vector;
import java.io.File;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;


public class Zip implements Serializable{

  //Nom du fichier
  private String fichier;
  //Liste des fichiers contenus dans le zip
  private Vector liste = new Vector();

  public Zip(String fichier) throws Exception {
    this.fichier = fichier;
    File f = new File(fichier);
    //Si le fichier existe, on recupere le nom et les attributs des fichiers compresses dans le zip
    if (f.exists()) {
      //C'est un fichier zip
      ZipFile fic = new ZipFile(fichier);
      //Recuperation des fichiers
      //enum Mot réservé -> enumeration
      Enumeration enumeration = fic.entries();
      while (enumeration.hasMoreElements())
        liste.addElement(enumeration.nextElement());
      fic.close();
    }
  }

    public void ajouteFichier(File f, String fichier, String commentaire) {
      ZipEntry entree;
      //Transforme le fichier en entree du zip
      entree = new ZipEntry(fichier);
      entree.setSize(f.length());
      entree.setTime(f.lastModified());
      if (commentaire != null)
        entree.setComment(commentaire);
      //Ajoute l'entree
      liste.addElement(entree);
    }

    public void ajouteRepertoire(File frep, String rep, String commentaire) {
      this.ajouteFichier(new File(frep + "/"), rep + "/", "Fichiers Site");
      File[] sousf = frep.listFiles();
      for (int i = 0; i < sousf.length; i++) {
        if (sousf[i].isFile()) {
          this.ajouteFichier(new File(frep + "/" + sousf[i].getName()), rep + "/" + sousf[i].getName(), "Fichiers Site");
        }
        else{
          this.ajouteRepertoire(new File(frep + "/" + sousf[i].getName()), rep + "/" + sousf[i].getName(), "Fichiers Site");
        }
      }
    }

    public String getFichier() {
      return fichier;
    }

    public void ziper() throws Exception {
      //Si le fichier n'existe pas, le creer
      File f = new File(fichier);
      if (!f.exists()) {
        f.createNewFile();
      }
      //Ouverture du fichier zip en ecriture
      ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fichier));
      //Pour chaque entree du zip
      int nb = liste.size();
      int index;
      for (int i = 0; i < nb; i++) {
        //Recuperer l'entree courante
        ZipEntry entre = (ZipEntry) liste.elementAt(i);
        //Recuperer le nom de l'entree
        String nom = entre.getName();
        //Ajouter l'entree au fichier physique zip
        zos.putNextEntry(entre);
        //Ouvrir l'entree en lecture
        if ( (new File(f.getParent() + File.separator + nom)).isDirectory())
          continue;
        FileInputStream fis = new FileInputStream(f.getParent() + File.separator + nom);
        //Ziper l'entree dans le fichier zip
        byte[] tab = new byte[4096];
        int lu = -1;
        do {
          lu = fis.read(tab);
          if (lu > 0)
            zos.write(tab, 0, lu);
        }
        while (lu > 0);
        //Fermer l'entree
        fis.close();
      }
      //Force e finir le zipage, si jamais il reste des bits non traites
      zos.flush();
      //Ferme le fichier zip
      zos.close();
    }

}


