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
package iepp.application.areferentiel;

import iepp.Application;
import iepp.application.CommandeNonAnnulable;

import java.util.Enumeration;
import java.util.Vector;
import iepp.domaine.PaquetagePresentation;
import iepp.application.CSupprimerPresentationDP;

/** Ajout par 2XMI **/

/**
 * Actualise un paquetage de présentation du référentiel.
 */
public class CActualiserPaqPres extends CommandeNonAnnulable
{
    private long idPres; // Identifiant de la présentation

    /**
     * Construit la commande.
     * @param idComp identifiant du paquetage de présentation dans le référentiel
     */
    public CActualiserPaqPres(long idPres)
    {
        this.idPres=idPres;
    }

    /**
     * Actualise la présentation du référentiel.
     * @see iepp.application.Commande#executer()
     * @return true si la commande s'est exécutée correctement
     */
    public boolean executer()
    {
        boolean resultat=false;
        boolean dansListeAGenerer=false;
        int positionArbre=0;
        String nomElement=null;
        //actualiser revient a supprimer puis ajouter
        Referentiel ref=Application.getApplication().getReferentiel();
        Enumeration enume=ref.getNoeudPresentation().children();
        while(enume.hasMoreElements())
        {
            Object object=enume.nextElement();
            if(object instanceof ElementReferentiel)
            {
                ElementReferentiel elementRef=(ElementReferentiel)object;
                if(this.idPres==elementRef.getIdElement())
                {
                    nomElement=elementRef.getNomElement();
                    Vector vecteur=Application.getApplication().getProjet().getDefProc().getListeAGenerer();
                    for(int i=0; i<vecteur.size(); i++)
                    {
                        if(vecteur.elementAt(i)instanceof PaquetagePresentation)
                        {
                            PaquetagePresentation paqp=(PaquetagePresentation)vecteur.elementAt(i);
                            if(paqp.getNomPresentation().equalsIgnoreCase(nomElement))
                            {
                                dansListeAGenerer=true;
                                positionArbre=i;
                            }
                        }
                    }
                }
            }
        }

        if(new CRemplacerPaqPresRef().executer(this.idPres))
        {
            Application.getApplication().getProjet().setModified(true);
            resultat=true;

            if(dansListeAGenerer)
            {
                enume=ref.getNoeudPresentation().children();
                while(enume.hasMoreElements())
                {
                    Object object=enume.nextElement();
                    if(object instanceof ElementReferentiel)
                    {
                        ElementReferentiel elementRef=(ElementReferentiel)object;
                        if(nomElement.equalsIgnoreCase(elementRef.getNomElement()))
                        {
                            PaquetagePresentation pp=(PaquetagePresentation)ref.chargerPresentation(elementRef.getIdElement());
                            if(pp!=null)
                            {
                                try
                                {
                                    Application.getApplication().getProjet().getDefProc().getListeAGenerer().insertElementAt(pp, positionArbre);
                                } catch(Exception e)
                                {
                                    Application.getApplication().getProjet().getDefProc().getListeAGenerer().add(pp);
                                } finally
                                {
                                    Application.getApplication().getProjet().setModified(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        return resultat;
    }
}
