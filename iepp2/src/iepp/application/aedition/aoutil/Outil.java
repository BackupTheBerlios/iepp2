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
 
package iepp.application.aedition.aoutil;


import iepp.Application;
import iepp.ui.iedition.VueDPGraphe;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

import util.Vecteur;

/**
* Outil est la classe de base pour l'implémentation des outils.
* Le but principal des outils est de gérer les entrées (souris et clavier) dans le but
* d'implémenter différents comportements comme la sélection ou la création d'éléments.
*/
public class Outil implements MouseListener, MouseMotionListener, KeyListener, Serializable
{

    /**
    * Etat initial de tous les éléments.
    */
    protected static final int IDLE_STATE = 0;

    /**
    * Etat courant de l'outil.
    */
	protected int state;

    /**
    * start est l'endroit du click de la souris, ou de son premier emplacement si on est
    * en train de dragger.
    */
	protected Vecteur start;

    /**
    * current est la position courante de la souris.
    */
	protected Vecteur current;

	/**
	 * Diagramme sur lequel on va utiliser l'outil courant
	 */
	protected VueDPGraphe diagramme;

	
    /**
     * Constructeur d'Outil
	 * @param diag, diagramme sur lequel on va appliquer l'outil créé
	 */
	public Outil(VueDPGraphe diag)
    {
    	// état initial
        this.state = IDLE_STATE;
        // initialisation  du premier click
        this.start = new Vecteur();
        // initialisation vide de la position courante
        this.current = new Vecteur();
        // garder un lien vers le diagramme
        this.diagramme = diag;
    }

    /**
    Ajoute une édition dans la pile undo du diagramme actif, après l'avoir exécutée.
    */
    /*
    void ajouterEditionDiagramme(Edition edition)
    {
       // ConsistanceDiagramme c = new ConsistanceDiagramme(edition);

        if (c.evaluer()) {
          edition.execute();
          diagramme.push(edition);
        }
        else
          c.traiterInvalide();

        terminer();
    }
	*/
	
   /**
    * Détruit l'outil courant et fixe OSelection comme nouvel outil courant.
    */
    public void terminer()
    {
       
        this.update();
	    // reprendre l'outil de sélection
		Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
    }

    /**
    * Rafraîchit la fenêtre principale après des modifications sur le diagramme
    */
    public void update()
    {
        this.diagramme.repaint();
    }

    /**
    * Retourne la position du click de la souris.
    */
    public Vecteur getStart()
    {
        return this.start;
    }

    /**
    * Retourne la position courante de la souris.
    */
    public Vecteur getCurrent()
    {
        return this.current;
    }

    /**
    * Certains outils (commme la sélection) nécessitent une méthode de dessin.
    */
    public void draw( Graphics g ) {}

    //------------------------------------------------------------------------
    //                      Méthodes MouseListener
    //------------------------------------------------------------------------
    public void mouseClicked( MouseEvent e ) {}
    public void mouseEntered( MouseEvent e ) {}
    public void mouseExited( MouseEvent e ) {}
	public void mousePressed( MouseEvent event )
    {
	   this.start.setValue(event.getX(),event.getY());
    }
    
    public void mouseReleased( MouseEvent event )
    {
		this.current.setValue(event.getX(),event.getY());
    }

    //------------------------------------------------------------------------
    //                   Méthodes MouseMotionListener
    //------------------------------------------------------------------------
    public void mouseDragged( MouseEvent event )
    {
		this.current.setValue(event.getX(),event.getY());
    }

    public void mouseMoved( MouseEvent event )
    {
        this.current.setValue(event.getX(),event.getY());
    }

    //------------------------------------------------------------------------
    //                     Méthodes KeyListener
    //------------------------------------------------------------------------
    public void keyTyped( KeyEvent event ) {}
    public void keyPressed( KeyEvent event ) {}
    public void keyReleased( KeyEvent event ) {}
    
}
