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

import javax.swing.tree.DefaultMutableTreeNode;
import java.text.SimpleDateFormat;


/**
 * Classe utilis?e pour remplir l'arbre du r?f?rentiel
 * Chaque objet de celle-ci contiendra le nom de l'?l?ment ainsi que son ID
 *
 * 2XMI utilise le nom du referentiel + nom de l'element pour l'export
 */
public class ElementReferentiel extends DefaultMutableTreeNode
{

        private String nomElement;
        private long idElement;
        private String chemin;
        private int type;


        // Modif 2XMI Albert
        private String version = null;
        // Modif 2XMI Jean
        private String datePlacement = null;

        //ATTENTION : ne pas changer LONGUEUR_VERSION et LONGUEUR_DATEPLACEMENT
        //car tous les referentiels correspondants seront corrompus,
        //et il faudra faire un patch
        //Referentiel v1.0 : pas de numero de version
        //Referentiel v2.0 : LONGUEUR_VERSION = 6 et LONGUEUR_DATEPLACEMENT = 14
        public static final int LONGUEUR_VERSION = 6;
        public static final String DEFAUT_VERSION = "_1.0.0";
        //Formateur pour les dates
        public static final int LONGUEUR_DATEPLACEMENT = 14;
        public static final SimpleDateFormat FORMATEUR = new SimpleDateFormat("yyyyMMddHHmmss");


        // Constantes repr?sentant les types possibles de l'?l?ment
        public static final int REFERENTIEL = -1;
        public static final int PAQ_COMP = 0;	// Paquetage de composants
        public static final int PAQ_DP = 1;		// Paquetage de DP
        public static final int PAQ_PRESENTATION = 2; // Paquetage de pr?sentation
        public static final int COMPOSANT = 3;
        public static final int SCENARIO = 7;
        public static final int PAQ_SCEN = 8;	// Paquetage de composants
        // Le type COMPOSANT_VIDE ne sert qu'? l'ajout du composant vide, ce composant vide est ensuite trait? comme un composant
        public static final int COMPOSANT_VIDE = 4;
        public static final int DP = 5;
        public static final int PRESENTATION = 6;


        public ElementReferentiel(String nom, long id, String cheminElt, int typeElt)
       {
               nomElement = nom;
               idElement = id;
               chemin = cheminElt;
               type = typeElt;
       }



        public ElementReferentiel(String nom, long id, String cheminElt, int typeElt, String _version, String _date)
        {
                nomElement = nom;
                idElement = id;
                chemin = cheminElt;
                type = typeElt;
                this.version= new String(_version.replace('_', ' '));
                this.datePlacement= new String(_date);
        }

        public String getNomElement()
        { return nomElement; }

        public long getIdElement()
        { return idElement; }

        public String getChemin()
        { return chemin; }

        public int getType()
        { return type; }

        public String toString()
        { return nomElement; }

        // modif Albert 2XMI
        public String getVersion()
        {
         String resultat;
         if (this.version != null) {
           resultat = this.version;
         }else
           resultat = "";
         return resultat;
        }

        // modif 2XMI jean
        public String getVersionRef()
        {
         String resultat;
         if (this.version != null) {
           resultat = this.version;
           //remplace les espaces par des underscores
           resultat = resultat.replace(' ', '_');

           int cpt = resultat.length();
           if (cpt > LONGUEUR_VERSION) {
             //on doit tronquer la version
             //on ne garde que les 6 premiers caracteres
             resultat = resultat.substring(0, LONGUEUR_VERSION);
           }else if (cpt < LONGUEUR_VERSION) {
             while (cpt!=LONGUEUR_VERSION){
               resultat='_'+resultat;
               cpt++;
             }
           }
         }else
           resultat = DEFAUT_VERSION;
         return resultat;
        }


        public String getDatePlacement()
        {
         String resultat;
         if (this.datePlacement != null)
           resultat = this.datePlacement;
         else
           resultat = "";
         return resultat;
        }



		public void setNomElement(String nomElement) {
			this.nomElement = nomElement;
		}
		
		public void setChemin(String chem){
			this.chemin = chem;
		}
}
