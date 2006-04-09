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

package iepp.application.ageneration;

import iepp.domaine.ComposantProcessus;
import iepp.domaine.ElementPresentation;
import iepp.domaine.IdObjetModele;

import java.io.*;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.ipsquad.apes.adapters.ActivityCell;
import org.ipsquad.apes.adapters.ActivityGraphAdapter;
import org.ipsquad.apes.adapters.ApesGraphCell;
import org.ipsquad.apes.adapters.ContextGraphAdapter;
import org.ipsquad.apes.adapters.FlowGraphAdapter;
import org.ipsquad.apes.adapters.ProcessComponentCell;
import org.ipsquad.apes.adapters.ProcessRoleCell;
import org.ipsquad.apes.adapters.ResponsabilityGraphAdapter;
import org.ipsquad.apes.adapters.SpemGraphAdapter;
import org.ipsquad.apes.adapters.WorkDefinitionCell;
import org.ipsquad.apes.adapters.WorkDefinitionGraphAdapter;
import org.ipsquad.apes.adapters.WorkProductCell;
import org.ipsquad.apes.adapters.WorkProductStateCell;
import org.ipsquad.apes.ui.ActivityJGraph;
import org.ipsquad.apes.ui.ContextJGraph;
import org.ipsquad.apes.ui.FlowJGraph;
import org.ipsquad.apes.ui.ResponsabilityJGraph;
import org.ipsquad.apes.ui.WorkDefinitionJGraph;
import org.jgraph.JGraph;

import com.sun.image.codec.jpeg.ImageFormatException;

import util.ImageUtil;
import javax.swing.tree.TreeNode;
import iepp.Application;
import org.ipsquad.apes.model.spem.statemachine.StateMachine;

/**
 * Classe permettant de créer une page dont le contenu correspond à un diagramme
 */
public class GDiagramme
    extends GElementModele {

  private ComposantProcessus cp;

  /**
   * Constructeur du gestionnaire de génération
   * @param elem element de présentation associé au diagramme courant
   * @param pwFicTree lien vers le fichier tree.js construit durant la génération du site
   */
  public GDiagramme(ComposantProcessus cp, ElementPresentation elem, PrintWriter pwFicTree) {
    super(elem, pwFicTree);
    this.cp = cp;
  }

  /**
   * Méthode permettant de traiter les éléments de présentation liés au diagramme
   */
  public void traiterGeneration(long id) throws IOException {
    super.traiterGeneration(id);
    if (this.element.getID_Apes() != -1) {
      this.creerFichierImages();
    }
  }

  /**
   *
   */
  private void creerFichierImages() throws ImageFormatException, IOException {
    FileOutputStream fout = new FileOutputStream(this.cheminParent + File.separator + "diagramme.png");
    ImageUtil.encoderGrapheImage( (SpemGraphAdapter)this.modele.getAdapter(), fout, "png");
  }

  /**
   * Méthode permettant de créer le contenu de la page associée au diagramme courant
   * Affichage du diagramme
   */
  public void creerFichierDescription() throws IOException {
    // création du fichier de contenu
    File ficHTML = new File(this.cheminAbsolu);
    FileWriter fd = new FileWriter(ficHTML);

    fd.write("<HTML><head><link rel='STYLESHEET' type='text/css' href='" + this.getCheminStyle() + "'>"
             + "</head>" + "<body><center>\n"
             + "<table width=\"84%\" align=\"center\">\n"
             + "<tr><td width=\"100%\" class=\"titrePage\">\n"
             + "<p align=\"center\" class=\"entete\">\n"
             + "<b>" + this.element.getNomPresentation() + "</b>\n"
             + "</p></td></tr></table></center><BR>\n");

    fd.write(getBarreNavigation() + "<br>");

    if(GenerationManager.getInstance().estContenuAvant())
    {
        //modif 2XMI Sébastien
        //ajout ds la page du chemin relatif
        File f=new File(GenerationManager.getInstance().getCheminGeneration());
        fd.write("<span class=\"cheminRelatif\" id=\""+ficHTML.getAbsolutePath().substring(f.getAbsolutePath().length()+1, ficHTML.getAbsolutePath().length())+"\"></span>");
        //fin modif 2XMI Sébastien
        this.ajouterContenu(fd);
    }
    fd.write("<div align=\"center\" class=\"imgdiagramme\">" + this.getMapDiagramme() + "</div>");

    if (!GenerationManager.getInstance().estContenuAvant()) {
      this.ajouterContenu(fd);
    }

    this.ajouterDescription(fd);
    this.ajouterMail(fd);

    //modif 2XMI amandine
    //affichage de la date de dernière modification d'un composant
    this.ajouterDerniereModif(fd);

    this.ajouterVersionDate(fd);
    this.ajouterPiedPage(fd);
    fd.write("</BODY></HTML>");
    fd.close();

  }

  /**
   * Renvoie sous la forme d'une chaine de caractère le code associé à la map du diagramme
   */
  public String getMapDiagramme() {
	SpemGraphAdapter mAdapter = cp.getAdapter(this.modele.getNumRang());
    String mapcode = ("<MAP NAME=\"" + CodeHTML.normalizeName(mAdapter.getName()) + "\">\n");

    JGraph mGraph = null;
    if (mAdapter instanceof ContextGraphAdapter) {
      mGraph = new ContextJGraph(mAdapter);
    }
    else if (mAdapter instanceof ResponsabilityGraphAdapter) {
      mGraph = new ResponsabilityJGraph(mAdapter);
    }
    else if (mAdapter instanceof ActivityGraphAdapter) {
      mGraph = new ActivityJGraph(mAdapter);
    }
    else if (mAdapter instanceof FlowGraphAdapter) {
      mGraph = new FlowJGraph(mAdapter);
    }
    else if (mAdapter instanceof WorkDefinitionGraphAdapter) {
      mGraph = new WorkDefinitionJGraph(mAdapter);
    }

    JFrame frame = new JFrame();
    frame.getContentPane().add(new JScrollPane(mGraph));
    frame.pack();
    frame.setVisible(false);

    Vector tmp = new Vector();
    Object o[] = mGraph.getRoots();

    int x1, x2, y1, y2;
    for (int i = 0; i < o.length; i++) {
      if (o[i] instanceof ActivityCell
          || o[i] instanceof WorkProductCell
          || o[i] instanceof ProcessRoleCell
          || o[i] instanceof WorkProductStateCell
          || o[i] instanceof ProcessComponentCell
          || o[i] instanceof WorkDefinitionCell) {
        x1 = (int) mGraph.getCellBounds(o[i]).getX();
        x2 = x1 + (int) mGraph.getCellBounds(o[i]).getWidth();
        y1 = (int) mGraph.getCellBounds(o[i]).getY();
        y2 = y1 + (int) mGraph.getCellBounds(o[i]).getHeight();

        // récupérer l'ID de l'élément courant
        int ID_Apes;
        // elem = l'élément de présentation vers lequel on va pointé
        // elem2 = l'élément de présentation sur lequel on veut avoir la description
        ElementPresentation elem = null;
        ElementPresentation elem2 = null;

        IdObjetModele nouvelId = GenerationManager.getProduitChange(this.cp.toString() + "::" + o[i].toString());
        if (nouvelId != null) {
          ID_Apes = nouvelId.getID();
          elem = ( (ComposantProcessus) nouvelId.getRef()).getElementPresentation(ID_Apes);
          elem2 = elem;
        }
        else {
          ID_Apes = ( (ApesGraphCell) o[i]).getID();
          elem2 = this.cp.getElementPresentation(ID_Apes);

          // modif 2xmi chaouk : rendre cliquable les produits avec etat
          if (o[i] instanceof WorkProductStateCell) {
            WorkProductStateCell wpsc = (WorkProductStateCell) o[i];

            String contextName = wpsc.getContextName();

            //car on n'arrive pas a recuperer l'id du produit par apes avec
            //getUserObject() sur wpsc puis getContext() puis getID()
            //donc on passe par un parcours de la liste des produits de composant
            Vector produits = this.cp.getProduits();
            for(int j = 0; j<produits.size(); j++){
              if (contextName.equals(produits.elementAt(j).toString()))
              {
                IdObjetModele io = (IdObjetModele) produits.elementAt(j);
                elem = this.cp.getElementPresentation(io.getID());
                if (elem != null)
                  elem2 = elem;
              }
            }
          }
          else if (o[i] instanceof ProcessComponentCell) {
            // un click sur un composant de processus amène vers le diagramme de
            // flot de définition de travail (workflow diagram)
            IdObjetModele id = this.cp.getDiagrammeFlot();
            if (id != null) {
              ID_Apes = id.getID();
              elem = this.cp.getElementPresentation(ID_Apes);
            }
            else {
              elem = null;
            }
          }
          else if (o[i] instanceof WorkDefinitionCell) {
            // un click sur une définition de travail doit ouvrir le diagramme
            // de flots d'activité
            ID_Apes = ( (ApesGraphCell) o[i]).getID();
            // 2xmi Albert modif debut
            IdObjetModele id = null;

            if(Application.getApplication().getConfigPropriete("place_page").equals(GenerationManager.DIAGFLOTS_PAGE))
            {
              id = this.cp.getDiagrammeFlotProduit(ID_Apes);
            }
            else if(Application.getApplication().getConfigPropriete("place_page").equals(GenerationManager.DIAGACTIVITES_PAGE))
            {
              id = this.cp.getDiagrammeActivite(ID_Apes);
            }
            else if(Application.getApplication().getConfigPropriete("place_page").equals(GenerationManager.COMPOSANT_PAGE))
            {
              id = this.cp.getDiagrammeContexte();
            }
            // 2xmi Albert modif fin
            if (id != null) {
              ID_Apes = id.getID();
              elem = this.cp.getElementPresentation(ID_Apes);
            }
            else {
              elem = null;
            }
          }
          else {
            elem = this.cp.getElementPresentation(ID_Apes);
          }
        }

        if (elem != null && elem2 != null) {
          IdObjetModele id = elem.getElementModele();
          if (id != null) {
            // info-bulle contenant la description de l'élément
            String description = "TITLE=\"\"";
            if (mAdapter instanceof ActivityGraphAdapter) {
              if ( (GenerationManager.getInstance().estInfoBulle()) && (GenerationManager.getInstance().estInfoBulleActivite()))
              {
                String infobulle = "";
                if (elem2.getDescription() != null)
                  infobulle = elem2.getDescription();
                if (elem2.getElementModele() != null)
                {
                  if (elem2.getElementModele().getRoleResponsable() != null)
                    infobulle += "Rôle responsable : " + elem2.getElementModele().getRoleResponsable().toString();
                }
                description = "TITLE=\"" + infobulle + "\"";
              }
              else if (GenerationManager.getInstance().estInfoBulleActivite())
              {
                if (elem2.getElementModele().getRoleResponsable() != null)
                  description = "TITLE=\"Rôle responsable : " + elem2.getElementModele().getRoleResponsable().toString() + "\"";
              }
              else if (GenerationManager.getInstance().estInfoBulle())
              {
                if (elem2.getDescription() != null)
                  description = "TITLE=\"" + elem2.getDescription() + "\"";
              }
            }
            else if (GenerationManager.getInstance().estInfoBulle()) {
              if (elem2.getDescription() != null) {
                description = "TITLE=\"" + elem2.getDescription() + "\"";
              }
            }

            mapcode += ("<AREA Shape=\"Polygon\" coords = \"" + x1 + "," + y1 + "," + x2 + "," + y1 + "," + x2 + "," + y2 + "," + x1 + "," + y2 + "\" HREF=\"" + this.getLienChemin(id) + "\" " + description  + ">\n");
          }
        }
        else {
          // S'il s'agit d'un produit exterieur (sans element de presentation)
          if (o[i] instanceof WorkProductCell) {
            // S'occuper du paquetage special
          }
        }
      }
    } // fin for (int i = 0; i < o.length; i++)

    mapcode += ("</MAP>\n");
    mapcode += ("<IMG SRC=\"diagramme.png\" USEMAP=\"#" + CodeHTML.normalizeName(mAdapter.getName()) + "\">\n");

    return mapcode;
  }

  /**
   *
   */
  public void recenser() {
    Integer oldValue = (Integer) ArbreGeneration.mapCompteur.get("nbDiagrammes");
    ArbreGeneration.mapCompteur.put("nbDiagrammes", new Integer(oldValue.intValue() + 1));

    oldValue = (Integer) ArbreGeneration.mapCompteur.get("nbPagesTotal");
    ArbreGeneration.mapCompteur.put("nbPagesTotal", new Integer(oldValue.intValue() + 1));
  }
}
