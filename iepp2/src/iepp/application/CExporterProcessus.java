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
package iepp.application;

import iepp.Application;
import iepp.application.ageneration.ArbreGeneration;
import iepp.application.ageneration.CodeHTML;
import iepp.application.ageneration.GenerationManager;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.ElementPresentation;
import iepp.domaine.Guide;
import iepp.domaine.IdObjetModele;
import iepp.domaine.PaquetagePresentation;
import iepp.infrastructure.jsx.ChargeurPresentationComposant;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

import util.ErrorManager;
import util.SimpleFileFilter;
import util.ToolKit;

/**
 * Classe pour exporter une definition de processus
 */
public class CExporterProcessus
    extends CommandeNonAnnulable
{

    protected SimpleFileFilter filter = new SimpleFileFilter("dpe",
                                                             "Exported Process");

    //modif 2XMI amandine
    protected static String DIAG_PATH = "diagramme.png";
    private static String DPC_PATH = "Site";
    private boolean exportDPC; //permet de saoir si l'utilisateur a effectue une exportation complete ou non
    //fin modif 2XMI amandine

    /**
     * Stream vers le fichier exporter
     */
    private FileInputStream sortieExport = null;

    /**
     * Constructeur par defaut
     */
    public CExporterProcessus()
    {
    }

    /**
     * Methode pour executer la commande
     *
     * @return Renvoi true si execution correcte, false sinon
     */
    public boolean executer()
    {
      	//SmartChooser chooser = SmartChooser.getChooser(Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport"));
        //chooser.setAcceptAllFileFilterUsed(false);
        //chooser.setFileFilter(filter);
        //chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Modif 2xmi Julien
        //récupération des répertoires
    	
    	// modification du chemin de l'exportation
    	String repertoireExport = Application.getApplication().getReferentiel().getCheminExport();
        //String repertoireExport = Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport");
        File rep = new File(repertoireExport);
        rep.mkdir();
        //création et affichage des JFileChooser
        JFileChooser chooser = new JFileChooser(rep);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        //Ouverture de la boite de dialogue
        if (chooser.showSaveDialog(Application.getApplication().
                                   getFenetrePrincipale()) !=
            JFileChooser.APPROVE_OPTION)
        {
            return false;
        }
        String selected_file = chooser.getSelectedFile().getAbsolutePath();
        // verifier qu'il y a bien l'extension
        if (!chooser.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(
            ".dpe"))
        {
            selected_file += ".dpe";
        }
        File fic = new File(selected_file);
        // verifier si le fichier existe deja, auquel cas on demande confirmation pour l'ecrasement
        if (fic.exists())
        {
            int choice = JOptionPane.showConfirmDialog(Application.getApplication().
                                                       getFenetrePrincipale(),
                                                       Application.getApplication().
                                                       getTraduction(
                                                           "msgConfirmEcrasement"),
                                                       Application.getApplication().
                                                       getTraduction("msgSauvegarde"),
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE);

            if (choice != JOptionPane.YES_OPTION)
            {
                return false;
            }
            if (!fic.delete())
            {
                return false; // impossible a supprimer, ptet pas les droits...
            }
        }
        try
        {
            if (!fic.createNewFile())
            {
                return false; // impossible a creer, ptet pas les droits...
            }
        }
        catch (IOException ex)
        {
            return false; // impossible a creer, ptet pas les droits...
        }
        return (this.exporter(fic, false));
    }

    /**
     * Methode realisant l'exportation.
     *
     * @param _fichierCible Chemin du fichier destination
     * @return Renvoi true si execution correcte, false sinon
     * @version 2.0 Le format du DPE exporte est la versio 1.8
     */
    public boolean exporter(File _fichierCible, boolean _exportDPC)
    {
    	if(chargerPresentation()==false) {
			// l'utilisateur a annulé la génration
			return false;
		}
    	
    	this.exportDPC = _exportDPC;
        boolean resultat = true;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //formateur de date
        SimpleDateFormat formateur = new SimpleDateFormat("yyyyMMddHHmmss");
        OutputStreamWriter data = null;
        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            ComposantProcessus courant = null;

            //------------------------------------------------------------------------
            // recuperer la liste des composants de processus et des paquetages de
            // presentation du processus courant
            GenerationManager.getInstance().setListeAGenerer(Application.getApplication().getProjet().getDefProc().getListeAGenerer());
            // l'arbre de generation sert aussi a associer
            ArbreGeneration arbre = new ArbreGeneration();
            ArbreGeneration.initialiserMap();
            GenerationManager.recupererProduitsExterieurs();
            GenerationManager.construireArbre(arbre, null);
            File f = new File(GenerationManager.getInstance().getCheminGeneration());
            // permet d'iniatialiser les chemins des elements a exporter
            arbre.initialiserArbre(ToolKit.removeSlashTerminatedPath(f.getAbsolutePath()));

            // recuperer la liste des composants du processus
            Vector listeComposant = Application.getApplication().getProjet().getDefProc().getListeComp();

            //recuperer le referentiel pour les id composants
            Referentiel ref = Application.getApplication().getReferentiel();

            // recuperer la definition de processus
            DefinitionProcessus defProc = Application.getApplication().getProjet().getDefProc();

            ProcessingInstruction pinst = document.createProcessingInstruction("iepp", "version=\"" + Application.NUMVERSION + "\"");
            document.appendChild(pinst);

            //------------------------------------------------------------------------
            // creation des elements XML et assemblage du document
            Element exportExecution = this.creerNoeudExportExecution(document, formateur, defProc, listeComposant, courant, ref);
            document.appendChild(exportExecution);

            Element liste_composant = this.creerNoeudListeComposants(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_composant);

            Element liste_role = this.creerNoeudListeRoles(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_role);

            Element liste_produit = this.creerNoeudListeProduits(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_produit);

            Element liste_definitionTravail = this.creerNoeudListeDefinitionTravail(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_definitionTravail);

            Element liste_activite = this.creerNoeudListeActivites(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_activite);

            Element liste_interface = this.creerNoeudListeInterfaces(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_interface);

            Element liste_typeProduit = this.creerNoeudListeTypeProduits(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_typeProduit);

            Element liste_etat = this.creerNoeudListeEtats(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_etat);

            Element liste_paquetagePresentation = this.creerNoeudListePaquetagePresentation(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_paquetagePresentation);

            Element liste_elementPresentation = this.creerNoeudListeElementPresentation(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_elementPresentation);

            Element liste_guide = this.creerNoeudListeGuides(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_guide);

            Element liste_typeGuide = this.creerNoeudListeTypeGuides(document, formateur, defProc, listeComposant, courant, ref);
            exportExecution.appendChild(liste_typeGuide);

            //------------------------------------------------------------------------
            // ecriture sur le fichier
            //pour l'encoding UTF-16
            FileOutputStream outstream = new FileOutputStream(_fichierCible);
            data = new OutputStreamWriter(outstream, "UTF-16");

            StreamResult result = new StreamResult(data);

            DOMSource source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.transform(source, result);
        }
        catch (Throwable _throwable)
        {
            resultat = false;
            _throwable.printStackTrace();
            ErrorManager.getInstance().display(_throwable);
        }
        finally
        {
            try
            {
                this.sortieExport = new FileInputStream(_fichierCible);
            }
            catch (FileNotFoundException ex)
            {
            }
            try
            {
              sortieExport.close();
              data.close();
            }
            catch (IOException ex1)
            {
                // exception due aux exceptions en amont
            }
        }
        return resultat;
    } // fin exporter

    /**
     * creerNoeudListeTypeGuides
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeTypeGuides(Document document,
                                              SimpleDateFormat formateur,
                                              DefinitionProcessus defProc,
                                              Vector listeComposant,
                                              ComposantProcessus courant,
                                              Referentiel ref)
    {

        // noeud liste_typeGuide
        /*<liste_typeGuide>
                <typeGuide>
                        <id></id>
                        <nom></nom>
                </typeGuide>
                     </liste_typeGuide>*/

        Element liste_typeGuide = document.createElement("liste_typeGuide");
        Vector listeIDType = new Vector();
        Vector listeNomType = new Vector();
        //recuperer chaque paquetagePresentation de chaque composant
        for (int i = 0; i < listeComposant.size(); i++)
        {
            PaquetagePresentation pp = ( (IdObjetModele) listeComposant.elementAt(i)).getPaquetagePresentation();
            Vector elements = pp.getListeElement();
            //recuperer chaque elementPresentation de chaque paquetagePresentation
            for (int j = 0; j < elements.size(); j++)
            {
                ElementPresentation eltPre = (ElementPresentation) elements.elementAt(j);
                Vector guides = eltPre.getGuides();
                for (int k = 0; k < guides.size(); k++)
                {
                    Guide guideElement = (Guide) guides.elementAt(k);
                    String typeGuidestr = guideElement.getType(); //recuperer le type du guide

                    //on va chercher si le type gudie n'a pas deja ete trouve,
                    //pour un element precedent
                    boolean trouve = false;
                    boolean arreter_recherche = false;

                    for (int m = 0; (m < listeComposant.size()) && (!arreter_recherche); m++)
                    {
                        PaquetagePresentation pp2 = ( (IdObjetModele) listeComposant.elementAt(m)).getPaquetagePresentation();
                        Vector elements2 = pp2.getListeElement();
                        //recuperer chaque elementPresentation de chaque paquetagePresentation
                        for (int n = 0; (n < elements2.size()) && (!arreter_recherche); n++)
                        {
                            ElementPresentation eltPre2 = (ElementPresentation) elements2.elementAt(n);
                            Vector guides2 = eltPre2.getGuides();
                            for (int o = 0; (o < guides2.size()) && (!arreter_recherche); o++)
                            {
                                Guide guideElement2 = (Guide) guides2.elementAt(o);
                                String typeGuidestr2 = guideElement2.getType(); //recuperer le type du guide

                                if (m >= i && n >= j && o >= k)
                                {
                                    //on a depasse notre element:
                                    //on n'a pas eu l'element de meme type avant notre element
                                    arreter_recherche = true;
                                    //on sortira dans les boucles for
                                    //pour ensuite creer un type guide
                                }
                                else
                                {
                                    if ( (typeGuidestr != null) && (typeGuidestr2 != null))
                                    {
                                        if (typeGuidestr.equals(typeGuidestr2))
                                        {
                                            //on a deja rencontre ce type avant
                                            trouve = true;
                                            //on sortira dans les boucles for
                                            arreter_recherche = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!trouve)
                    {
                        //c'est la premiere fois que l'on trouve ce guide
                        Element typeGuide = document.createElement("typeGuide");
                        liste_typeGuide.appendChild(typeGuide);

                        Element id = document.createElement("id");
                        id.appendChild(document.createTextNode("_" + i + "_" + guideElement.getID_interne() + "_tyg"));
                        listeIDType.add("_" + i + "_" + guideElement.getID_interne() + "_tyg");
                        Element nom = document.createElement("nom");
                        nom.appendChild(document.createTextNode(typeGuidestr));
                        listeNomType.add(typeGuidestr);

                        typeGuide.appendChild(id);
                        typeGuide.appendChild(nom);
                    }
                }
            }
        }

        //liste des guides pour les elements de presentation
        Element liste_paquetagePresentation = document.createElement("liste_paquetagePresentation");

        Vector paquetageListeAGenerer = Application.getApplication().getProjet().getDefProc().getListeAGenerer();
        for (int i = 0; i < paquetageListeAGenerer.size(); i++) {
          if (paquetageListeAGenerer.elementAt(i)instanceof PaquetagePresentation) {
            PaquetagePresentation paqPres = (PaquetagePresentation) paquetageListeAGenerer.elementAt(i);
            Vector listeElement = paqPres.getListeElement();
            for (int j = 0; j < listeElement.size(); j++) {
              ElementPresentation elementPresentation = (ElementPresentation) listeElement.elementAt(j);
              Vector listeGuides = elementPresentation.getGuides();
              for (int k = 0; k < listeGuides.size(); k++) {
                Guide guideElement = (Guide) listeGuides.elementAt(k);
                String typeGuidestr = guideElement.getType(); //recuperer le type du guide

                String idType = "";
                boolean trouve = false;
                for(int m=0 ; m<listeNomType.size() && !trouve ; m++){
                  if(typeGuidestr.equals(listeNomType.elementAt(m).toString())){
                    idType = listeIDType.elementAt(m).toString();
                    trouve = true;
                  }
                }

                if(!trouve){
                  Element typeGuide = document.createElement("typeGuide");
                  liste_typeGuide.appendChild(typeGuide);

                  Element id = document.createElement("id");
                  id.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guideElement.getID_interne() + "_tyg"));
                  listeIDType.add("_" + i + "_" + j + "_" + guideElement.getID_interne() + "_tyg");
                  Element nom = document.createElement("nom");
                  nom.appendChild(document.createTextNode(typeGuidestr));
                  listeNomType.add(typeGuidestr);

                  typeGuide.appendChild(id);
                  typeGuide.appendChild(nom);
                }

              }
            }
          }
        }

        return liste_typeGuide;
    }

    /**
     * creerNoeudListeGuides
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeGuides(Document document,
                                          SimpleDateFormat formateur,
                                          DefinitionProcessus defProc,
                                          Vector listeComposant,
                                          ComposantProcessus courant,
                                          Referentiel ref)
    {
        // noeud liste_guide
        /*<liste_guide>
                <guide>
                        <id></id>
                        <nom></nom>
                        <typeGuideId>_id_typeGuide_</typeGuideId>
                        <elementPresentationId>_id_elementPresentation_</elementPresentationId>
                </guide>
               </liste_guide>*/
        Vector listeTypeGuideID = new Vector();
        Vector listeTypeGuideStr = new Vector();
        Element liste_guide = document.createElement("liste_guide");

        for (int i = 0; i < listeComposant.size(); i++)
        {
            if (listeComposant.elementAt(i) instanceof IdObjetModele)
            {
                IdObjetModele idObjetModel = (IdObjetModele) listeComposant.elementAt(i);
                //cas (idObjetModel.estPaquetage()) et inversement
                PaquetagePresentation paquetagePresentation = idObjetModel.getPaquetagePresentation();
                Vector listeElementPresentation = paquetagePresentation.getListeElement();
                for (int j = 0; j < listeElementPresentation.size(); j++)
                {

                    ElementPresentation elementPresentation = (ElementPresentation) listeElementPresentation.elementAt(j);
                    Vector listeGuides = elementPresentation.getGuides();
                    for (int k = 0; k < listeGuides.size(); k++)
                    {
                        Guide guide = (Guide) listeGuides.elementAt(k);
                        Element NoeudGuide = document.createElement("guide");
                        liste_guide.appendChild(NoeudGuide);

                        Element id = document.createElement("id");
                        id.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_com_gui"));

                        Element nom = document.createElement("nom");
                        nom.appendChild(document.createTextNode(guide.getNomPresentation()));

                        Element typeGuideId = document.createElement("typeGuideId");
                        String typeGuidestr = guide.getType();
                        //on va chercher si le type guide n'a pas deja ete trouve,
                        //pour un element precedent
                        boolean trouve = false;
                        boolean arreter_recherche = false;

                        for (int m = 0; (m < listeComposant.size()) && (!arreter_recherche); m++)
                        {
                            PaquetagePresentation pp2 = ( (IdObjetModele) listeComposant.elementAt(m)).getPaquetagePresentation();
                            Vector elements2 = pp2.getListeElement();
                            //recuperer chaque elementPresentation de chaque paquetagePresentation
                            for (int n = 0; (n < elements2.size()) && (!arreter_recherche); n++)
                            {
                                ElementPresentation eltPre2 = (ElementPresentation) elements2.elementAt(n);
                                Vector guides2 = eltPre2.getGuides();
                                for (int o = 0; (o < guides2.size()) && (!arreter_recherche); o++)
                                {
                                    Guide guideElement2 = (Guide) guides2.elementAt(o);
                                    String typeGuidestr2 = guideElement2.getType(); //recuperer le type du guide

                                    if (m >= i && n >= j && o >= k)
                                    {
                                        //on a depasse notre element:
                                        //on n'a pas eu l'element de meme type avant notre element
                                        arreter_recherche = true;
                                        //on sortira dans les boucles for
                                        //pour ensuite creer un type guide
                                    }
                                    else
                                    {
                                        if ( (typeGuidestr != null) && (typeGuidestr2 != null))
                                        {
                                            if (typeGuidestr.equals(typeGuidestr2))
                                            {
                                                //on a deja rencontre ce type avant
                                                trouve = true;
                                                //on construit l'id du type guide en se servant du premier
                                                //element qui possede ce type guide.
                                                typeGuideId.appendChild(document.createTextNode("_" + m + "_" + guideElement2.getID_interne() + "_tyg"));
                                                //on sortira dans les boucles for
                                                arreter_recherche = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!trouve)
                        {
                            //c'est la premiere fois que l'on rencontre ce type
                            typeGuideId.appendChild(document.createTextNode("_" + i + "_" + guide.getID_interne() + "_tyg"));
                            listeTypeGuideID.add("_" + i + "_" + guide.getID_interne() + "_tyg");
                            listeTypeGuideStr.add(guide.getType());
                        }

                        // element de presentation
                        Element elementPresentationId = document.createElement("elementPresentationId");
                        elementPresentationId.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_com_elp"));

                        NoeudGuide.appendChild(id);
                        NoeudGuide.appendChild(nom);
                        NoeudGuide.appendChild(typeGuideId);
                        NoeudGuide.appendChild(elementPresentationId);
                    }
                }
            }
        }

        //liste des guides pour les elements de presentation
        Element liste_paquetagePresentation = document.createElement("liste_paquetagePresentation");

        Vector paquetageListeAGenerer = Application.getApplication().getProjet().getDefProc().getListeAGenerer();
        for (int i = 0; i < paquetageListeAGenerer.size(); i++) {
          if (paquetageListeAGenerer.elementAt(i)instanceof PaquetagePresentation) {
            PaquetagePresentation paqPres = (PaquetagePresentation) paquetageListeAGenerer.elementAt(i);
            Vector listeElement = paqPres.getListeElement();
            for (int j = 0; j < listeElement.size(); j++) {
              ElementPresentation elementPresentation = (ElementPresentation) listeElement.elementAt(j);
              Vector listeGuides = elementPresentation.getGuides();
              for (int k = 0; k < listeGuides.size(); k++) {
                Guide guide = (Guide) listeGuides.elementAt(k);
                Element NoeudGuide = document.createElement("guide");
                liste_guide.appendChild(NoeudGuide);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_pap_gui"));

                Element nom = document.createElement("nom");
                nom.appendChild(document.createTextNode(guide.getNomPresentation()));

                Element typeGuideId = document.createElement("typeGuideId");
                String typeGuidestr = guide.getType();
                String typeGuideIdPaq = "";
                boolean trouve = false;
                for(int m=0 ; m<listeTypeGuideStr.size() && !trouve ; m++){
                  if(typeGuidestr.equals(listeTypeGuideStr.elementAt(m).toString())){
                    typeGuideIdPaq = listeTypeGuideID.elementAt(m).toString();
                    trouve = true;
                  }
                }
                if(trouve)
                  typeGuideId.appendChild(document.createTextNode(typeGuideIdPaq));
                else{
                  typeGuideId.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_tyg"));
                  listeTypeGuideID.add("_" + i + "_" + j + "_" + guide.getID_interne() + "_tyg");
                  listeTypeGuideStr.add(typeGuidestr);
                }
                // element de presentation
                Element elementPresentationId = document.createElement("elementPresentationId");
                elementPresentationId.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_pap_elp"));

                NoeudGuide.appendChild(id);
                NoeudGuide.appendChild(nom);
                NoeudGuide.appendChild(typeGuideId);
                NoeudGuide.appendChild(elementPresentationId);
              }
            }
          }
        }
        return liste_guide;
    }

    /**
     * creerNoeudListeElementPresentation
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeElementPresentation(Document document,
                                                       SimpleDateFormat formateur,
                                                       DefinitionProcessus defProc,
                                                       Vector listeComposant,
                                                       ComposantProcessus courant,
                                                       Referentiel ref)
    {
        //noeud liste_elementPresentation
        /*<liste_elementPresentation>
                <elementPresentation>
                        <id></id>
                        <nom></nom>
                        <cheminIcone></cheminIcone>
                        <cheminContenu></cheminContenu>
                        <description></description>
                        <cheminPage></cheminPage>
                        <liste_guideId>
                                <guideId>_id_guide_</guideId>
                        </liste_guideId>
                </elementPresentation>
          </liste_elementPresentation>*/
        Element liste_elementPresentation = document.createElement("liste_elementPresentation");

        for (int i = 0; i < listeComposant.size(); i++) {
          if (listeComposant.elementAt(i)instanceof IdObjetModele) {
            IdObjetModele idObjetModel = (IdObjetModele) listeComposant.elementAt(i);
            PaquetagePresentation paquetage = idObjetModel.getPaquetagePresentation();
            Vector listeElementPresentation = paquetage.getListeElement();
            for (int j = 0; j < listeElementPresentation.size(); j++) {

              ElementPresentation element = (ElementPresentation) listeElementPresentation.elementAt(j);
              //traitement pour l'element de presentation
              {
                Element elementPresentation = document.createElement("elementPresentation");
                liste_elementPresentation.appendChild(elementPresentation);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + i + "_" + j + "_" + element.getID_interne() + "_com_elp"));

                Element nom = document.createElement("nom");
                nom.appendChild(document.createTextNode(element.getNomPresentation()));

                Element cheminIcone = document.createElement("cheminIcone");
                if (element.getNomIcone() != null) {
                  if (this.exportDPC)
                    cheminIcone.appendChild(document.createTextNode(DPC_PATH + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH + "/" + element.getNomIcone()));
                  else
                    cheminIcone.appendChild(document.createTextNode("." + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH + "/" + element.getNomIcone()));
                }

                Element cheminContenu = document.createElement("cheminContenu");
                String contenu = element.getContenu();
                if (contenu != null) {
                  if (this.exportDPC)
                    cheminContenu.appendChild(document.createTextNode(DPC_PATH + "/" + CodeHTML.normalizeName(idObjetModel.toString()) + "/" + GenerationManager.CONTENU_PATH + "/" + contenu));
                  else
                    cheminContenu.appendChild(document.createTextNode("." + "/" + CodeHTML.normalizeName(idObjetModel.toString()) + "/" + GenerationManager.CONTENU_PATH + "/" + contenu));
                }

                Element description = document.createElement("description");
                description.appendChild(document.createTextNode(element.getDescription()));

                Element cheminPage = document.createElement("cheminPage");
                IdObjetModele io = (IdObjetModele) element.getElementModele();
                String chemin;
                if (io != null)
                  chemin = ( (IdObjetModele) element.getElementModele()).getChemin();
                else
                  chemin = element.getCheminPage();

                if (this.exportDPC) {
                  String cheminDPC = DPC_PATH + "/" + chemin.substring(2);
                  cheminPage.appendChild(document.createTextNode(cheminDPC));
                }
                else
                  cheminPage.appendChild(document.createTextNode(chemin));

                Element liste_guideId = document.createElement("liste_guideId");
                Vector guides = element.getGuides();
                for (int k = 0; k < guides.size(); k++) {
                  Guide guide = (Guide) guides.elementAt(k);
                  Element guideId = document.createElement("guideId");
                  guideId.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_com_gui"));
                  liste_guideId.appendChild(guideId);
                }

                elementPresentation.appendChild(id);
                elementPresentation.appendChild(nom);
                elementPresentation.appendChild(cheminIcone);
                elementPresentation.appendChild(cheminContenu);
                elementPresentation.appendChild(description);
                elementPresentation.appendChild(cheminPage);
                elementPresentation.appendChild(liste_guideId);
              }
              //traitement pour les elements de presentation des guides
              //de l'element de presentation actuel
              Vector listeGuides = element.getGuides();
              for (int k = 0; k < listeGuides.size(); k++) {
                Guide guide = (Guide) listeGuides.elementAt(k);
                Element elementPresentation = document.createElement("elementPresentation");
                liste_elementPresentation.appendChild(elementPresentation);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_com_elp"));

                Element nom = document.createElement("nom");
                nom.appendChild(document.createTextNode(guide.getNomPresentation()));

                Element cheminIcone = document.createElement("cheminIcone");
                if (guide.getNomIcone() != null) {
                  if (this.exportDPC)
                    cheminIcone.appendChild(document.createTextNode(DPC_PATH + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH + "/" + guide.getNomIcone()));
                  else
                    cheminIcone.appendChild(document.createTextNode("." + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH + "/" + guide.getNomIcone()));
                }

                Element cheminContenu = document.createElement("cheminContenu");
                String contenu = guide.getContenu();
                if (contenu != null) {
                  if (this.exportDPC)
                    cheminContenu.appendChild(document.createTextNode(DPC_PATH + "/" + CodeHTML.normalizeName(idObjetModel.toString()) + "/" + GenerationManager.CONTENU_PATH + "/" + contenu));
                  else
                    cheminContenu.appendChild(document.createTextNode("." + "/" + CodeHTML.normalizeName(idObjetModel.toString()) + "/" + GenerationManager.CONTENU_PATH + "/" + contenu));
                }

                Element description = document.createElement("description");
                description.appendChild(document.createTextNode(guide.getDescription()));

                Element cheminPage = document.createElement("cheminPage");
                String chemin = guide.getCheminPage();

                if (this.exportDPC) {
                  String cheminDPC = DPC_PATH + "/" + chemin.substring(2);
                  cheminPage.appendChild(document.createTextNode(cheminDPC));
                }
                else
                  cheminPage.appendChild(document.createTextNode(chemin));

                elementPresentation.appendChild(id);
                elementPresentation.appendChild(nom);
                elementPresentation.appendChild(cheminIcone);
                elementPresentation.appendChild(cheminContenu);
                elementPresentation.appendChild(description);
                elementPresentation.appendChild(cheminPage);
              }
            }
          }
        }

        //traitement des elements de presentation des paquetages de presentation
        Element liste_paquetagePresentation = document.createElement("liste_paquetagePresentation");

        Vector paquetageListeAGenerer = Application.getApplication().getProjet().getDefProc().getListeAGenerer();
        for (int i = 0; i < paquetageListeAGenerer.size(); i++) {
          if (paquetageListeAGenerer.elementAt(i)instanceof PaquetagePresentation) {
            PaquetagePresentation paqPres = (PaquetagePresentation) paquetageListeAGenerer.elementAt(i);
            Vector listeElement = paqPres.getListeElement();
            for (int j = 0; j < listeElement.size(); j++) {
              ElementPresentation elt = (ElementPresentation) listeElement.elementAt(j);
              {
                Element elementPresentation = document.createElement("elementPresentation");
                liste_elementPresentation.appendChild(elementPresentation);

                Element id = document.createElement("id");
                if (elt.getID_interne().equals("1")) {
                  id.appendChild(document.createTextNode("_" + ref.nomPresentationToId(elt.getNomPresentation()) + "_pap_1_elp"));
                }
                else {
                  id.appendChild(document.createTextNode("_" + i + "_" + j + "_" + elt.getID_interne() + "_agr_elp"));
                }
                Element nom = document.createElement("nom");
                nom.appendChild(document.createTextNode(elt.getNomPresentation()));

                Element cheminIcone = document.createElement("cheminIcone");
                if (paqPres.getChemin_icone() != null) {
                  if (this.exportDPC)
                    cheminIcone.appendChild(document.createTextNode(DPC_PATH + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH + "/" + elt.getNomIcone()));
                  else
                    cheminIcone.appendChild(document.createTextNode("." + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH + "/" + elt.getNomIcone()));
                }

                Element cheminContenu = document.createElement("cheminContenu");
                String contenu = paqPres.getChemin_contenu();
                if (contenu != null) {
                  if (elt.getContenu() != null) {
                    if (this.exportDPC)
                      cheminContenu.appendChild(document.createTextNode(DPC_PATH + "/" + paqPres.getNomPresentation() + "/" + GenerationManager.CONTENU_PATH + "/" + contenu + "/" + elt.getContenu()));
                    else
                      cheminContenu.appendChild(document.createTextNode("." + "/" + paqPres.getNomPresentation() + "/" + GenerationManager.CONTENU_PATH + "/" + contenu + "/" + elt.getContenu()));
                  }
                }

                Element description = document.createElement("description");
                description.appendChild(document.createTextNode(elt.getDescription()));

                Element cheminPage = document.createElement("cheminPage");
                IdObjetModele io = (IdObjetModele) elt.getElementModele();
                String chemin;
                if (io != null)
                  chemin = ( (IdObjetModele) elt.getElementModele()).getChemin();
                else
                  chemin = elt.getCheminPage();

                if (this.exportDPC) {
                  String cheminDPC = DPC_PATH + "/" + chemin.substring(2);
                  cheminPage.appendChild(document.createTextNode(cheminDPC));
                }
                else
                  cheminPage.appendChild(document.createTextNode(chemin));

                Element liste_guideId = document.createElement("liste_guideId");
                Vector guides = elt.getGuides();
                for (int k = 0; k < guides.size(); k++) {
                  Guide guide = (Guide) guides.elementAt(k);
                  Element guideId = document.createElement("guideId");
                  guideId.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_pap_gui"));
                  liste_guideId.appendChild(guideId);
                }

                elementPresentation.appendChild(id);
                elementPresentation.appendChild(nom);
                elementPresentation.appendChild(cheminIcone);
                elementPresentation.appendChild(cheminContenu);
                elementPresentation.appendChild(description);
                elementPresentation.appendChild(cheminPage);
                elementPresentation.appendChild(liste_guideId);
              }
              Vector listeGuides = elt.getGuides();
              for (int k = 0; k < listeGuides.size(); k++) {
                Guide guide = (Guide) listeGuides.elementAt(k);
                Element elementPresentation = document.createElement("elementPresentation");
                liste_elementPresentation.appendChild(elementPresentation);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + i + "_" + j + "_" + guide.getID_interne() + "_pap_elp"));

                Element nom = document.createElement("nom");
                nom.appendChild(document.createTextNode(guide.getNomPresentation()));

                Element cheminIcone = document.createElement("cheminIcone");
                if (guide.getNomIcone() != null) {
                  if (this.exportDPC)
                    cheminIcone.appendChild(document.createTextNode(DPC_PATH + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH + "/" + guide.getNomIcone()));
                  else
                    cheminIcone.appendChild(document.createTextNode("." + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH + "/" + guide.getNomIcone()));
                }

                Element cheminContenu = document.createElement("cheminContenu");
                String contenu = guide.getContenu();
                if (contenu != null) {
                  if (this.exportDPC)
                    cheminContenu.appendChild(document.createTextNode(DPC_PATH + "/" + paqPres.getNomPresentation() + "/" + GenerationManager.CONTENU_PATH + "/" + contenu));
                  else
                    cheminContenu.appendChild(document.createTextNode("." + "/" + paqPres.getNomPresentation() + "/" + GenerationManager.CONTENU_PATH + "/" + contenu));
                }

                Element description = document.createElement("description");
                description.appendChild(document.createTextNode(guide.getDescription()));

                Element cheminPage = document.createElement("cheminPage");
                String chemin = guide.getCheminPage();

                if (this.exportDPC) {
                  String cheminDPC = DPC_PATH + "/" + chemin.substring(2);
                  cheminPage.appendChild(document.createTextNode(cheminDPC));
                }
                else
                  cheminPage.appendChild(document.createTextNode(chemin));

                elementPresentation.appendChild(id);
                elementPresentation.appendChild(nom);
                elementPresentation.appendChild(cheminIcone);
                elementPresentation.appendChild(cheminContenu);
                elementPresentation.appendChild(description);
                elementPresentation.appendChild(cheminPage);

              }

            }
          }
        }
        return liste_elementPresentation;
      }

    /**
     * creerNoeudListePaquetagePresentation
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListePaquetagePresentation(Document document,
                                                         SimpleDateFormat formateur,
                                                         DefinitionProcessus defProc,
                                                         Vector listeComposant,
                                                         ComposantProcessus courant,
                                                         Referentiel ref)
    {
        //noeud liste_paquetagePresentation
        /*<liste_paquetagePresentation>
                <paquetagePresentation>
                        <id></id>
                        <nom></nom>
                        <dossierIcone></dossierIcone>
                        <dossierContenu></dossierContenu>
                        <ordreGeneration></ordreGeneration>
                        <liste_agregeElementPresentation>
                                <agregeElementPresentation>_id_elementPresentation_</agregeElementPresentation>
                        </liste_agregeElementPresentation>
                        <elementPresentationId>_id_elementPresentation_</elementPresentationId>
                </paquetagePresentation>
         </liste_paquetagePresentation>*/
        Element liste_paquetagePresentation = document.createElement("liste_paquetagePresentation");

        Vector paquetageListeAGenerer = Application.getApplication().getProjet().getDefProc().getListeAGenerer();
        for (int i = 0; i < paquetageListeAGenerer.size(); i++) {
          if (paquetageListeAGenerer.elementAt(i)instanceof PaquetagePresentation) {
            PaquetagePresentation paquetagePresentation = (PaquetagePresentation) paquetageListeAGenerer.elementAt(i);

            Element NoeudPaquetagePresentation = document.createElement("paquetagePresentation");
              liste_paquetagePresentation.appendChild(NoeudPaquetagePresentation);

              Element id = document.createElement("id");
              id.appendChild(document.createTextNode("_" + ref.nomPresentationToId(paquetagePresentation.getNomPresentation()) + "_pap"));

              Element nom = document.createElement("nom");
              nom.appendChild(document.createTextNode(paquetagePresentation.getNomPresentation()));

              Element dossierIcone = document.createElement("dossierIcone");
              if (this.exportDPC)
                dossierIcone.appendChild(document.createTextNode(DPC_PATH + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH));
              else
                dossierIcone.appendChild(document.createTextNode("." + "/" + GenerationManager.APPLET_PATH + "/" + GenerationManager.IMAGES_PATH));

              Element dossierContenu = document.createElement("dossierContenu");
              if (this.exportDPC)
                dossierContenu.appendChild(document.createTextNode(DPC_PATH + "/" + paquetagePresentation.getNomPresentation() + "/" + GenerationManager.CONTENU_PATH));
              else
                dossierContenu.appendChild(document.createTextNode("." + "/" + paquetagePresentation.getNomPresentation() + "/" + GenerationManager.CONTENU_PATH));

              Element ordreGeneration = document.createElement("ordreGeneration");
              ordreGeneration.appendChild(document.createTextNode("" + defProc.getListeAGenerer().indexOf(paquetagePresentation)));

              Vector listeElementPresentation = paquetagePresentation.getListeElement();
              Element liste_agregeElementPresentation = document.createElement("liste_agregeElementPresentation");
              for (int j = 0; j < listeElementPresentation.size(); j++) {
                ElementPresentation elementPresentation = (ElementPresentation) listeElementPresentation.elementAt(j);
                if (!(elementPresentation.getID_interne().equals("1"))) {
                  Element agregeElementPresentation = document.createElement("agregeElementPresentation");
                  liste_agregeElementPresentation.appendChild(agregeElementPresentation);
                  agregeElementPresentation.appendChild(document.createTextNode("_" + i + "_" + j + "_" + elementPresentation.getID_interne() + "_agr_elp"));
                }
              }

              Element elementPresentationId = document.createElement("elementPresentationId");
              elementPresentationId.appendChild(document.createTextNode("_" + ref.nomPresentationToId(paquetagePresentation.getNomPresentation()) + "_pap_1_elp"));

              NoeudPaquetagePresentation.appendChild(id);
              NoeudPaquetagePresentation.appendChild(nom);
              NoeudPaquetagePresentation.appendChild(dossierIcone);
              NoeudPaquetagePresentation.appendChild(dossierContenu);
              NoeudPaquetagePresentation.appendChild(ordreGeneration);
              NoeudPaquetagePresentation.appendChild(liste_agregeElementPresentation);
              NoeudPaquetagePresentation.appendChild(elementPresentationId);
            }
        }

        return liste_paquetagePresentation;
    }

    /**
     * creerNoeudListeEtats
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeEtats(Document document,
                                         SimpleDateFormat formateur,
                                         DefinitionProcessus defProc,
                                         Vector listeComposant,
                                         ComposantProcessus courant,
                                         Referentiel ref)
    {

        // noeud liste_etat
        /*<liste_etat>
                <etat>
                        <id></id>
                        <nom></nom>
                </etat>
          </liste_etat>
         */

        Element liste_etat = document.createElement("liste_etat");

        //recuperer chaque composant
        for (int i = 0; i < listeComposant.size(); i++)
        {
            //pour chaque composant recuperer la liste de leurs produits
            Vector produits = ( (IdObjetModele) listeComposant.elementAt(i)).getProduit();
            //pour chaque produit recuperer la liste de leurs etats
            for (int j = 0; j < produits.size(); j++)
            {
                IdObjetModele io = (IdObjetModele) produits.elementAt(j);
                Vector listeEtat = io.getEtats();

                //chaque etat constitue alors une balise xml
                for (int k = 0; k < listeEtat.size(); k++)
                {
                    Element etat = document.createElement("etat");
                    liste_etat.appendChild(etat);
                    //id de l'etat : "id du produit+nb_produits" concatene avec nb_etat
                    Element id = document.createElement("id");
                    id.appendChild(document.createTextNode("_" + new Integer(io.getID() + ( (i + 1) * 10000) + (j + 1)).toString() + k + "_eta"));

                    Element nom = document.createElement("nom");
                    nom.appendChild(document.createTextNode(listeEtat.elementAt(k).toString()));

                    etat.appendChild(id);
                    etat.appendChild(nom);
                }
            }
        }

        return liste_etat;
    }

    /**
     * creerNoeudListeTypeProduits
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeTypeProduits(Document document,
                                                SimpleDateFormat formateur,
                                                DefinitionProcessus defProc,
                                                Vector listeComposant,
                                                ComposantProcessus courant,
                                                Referentiel ref)
    {
        // noeud liste_typeProduit
        /*<liste_typeProduit>
                  <typeProduit>
                          <id></id>
                          <nom></nom>
                  </typeProduit>
            </liste_typeProduit>*/
        Element liste_typeProduit = document.createElement("liste_typeProduit");

        //recuperer chaque paquetagePresentation de chaque composant
        for (int i = 0; i < listeComposant.size(); i++)
        {
            PaquetagePresentation test = ( (IdObjetModele) listeComposant.elementAt(i)).getPaquetagePresentation();
            Vector elements = test.getListeElement();
            //recuperer chaque elementPresentation de chaque paquetagePresentation
            for (int k = 0; k < elements.size(); k++)
            {
                ElementPresentation eltPre = (ElementPresentation) elements.elementAt(k);
                String typeProduitstr = eltPre.getTypeProduit(); //recuperer le type du produit

                //on va chercher si le type produit n'a pas deja ete trouve,
                //pour un element precedent
                boolean trouve = false;
                boolean arreter_recherche = false;

                for (int m = 0; (m < listeComposant.size()) && (i <= m) && (!arreter_recherche); m++)
                {
                    PaquetagePresentation test2 = ( (IdObjetModele) listeComposant.elementAt(m)).getPaquetagePresentation();
                    Vector elements2 = test2.getListeElement();
                    for (int n = 0; (n < elements2.size()) && (!arreter_recherche); n++)
                    {
                        ElementPresentation eltPre2 = (ElementPresentation) elements2.elementAt(n);
                        String typeProduitstr2 = eltPre2.getTypeProduit(); //recuperer le type du produit

                        if (m >= i && n >= k)
                        {
                            //on a depasse notre element:
                            //on n'a pas eu l'element de meme type avant notre element
                            arreter_recherche = true;
                            //on sortira dans les boucles for
                            //pour ensuite creer un type produit
                        }
                        else
                        {
                            if ( (typeProduitstr != null) && (typeProduitstr2 != null))
                            {
                                if (typeProduitstr.equals(typeProduitstr2))
                                {
                                    //on a deja rencontre ce type avant
                                    trouve = true;
                                    //on sortira dans les boucles for
                                    arreter_recherche = true;
                                }
                            }
                        }
                    }
                }

                //un elementPresentation peut etre un produit,ou autre.
                //Un produit ne peut avoir qu'un type de produit
                if (typeProduitstr != null && !trouve)
                {
                    Element typeProduit = document.createElement("typeProduit");
                    liste_typeProduit.appendChild(typeProduit);

                    //id du typeProduit : "id du produit + 0"
                    Element id = document.createElement("id");
                    //il est possible de preciser l'id en prefixant par "_" + i + "_" + k +
                    id.appendChild(document.createTextNode("_" + new Integer(eltPre.getID_Apes() + 10000).toString() + "_typ"));

                    //type produit : referentiel, document, exemple..
                    Element nom = document.createElement("nom");
                    nom.appendChild(document.createTextNode(typeProduitstr));

                    typeProduit.appendChild(id);
                    typeProduit.appendChild(nom);
                }
            }
        }
        return liste_typeProduit;
    }

    /**
     * creerNoeudListeInterfaces
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeInterfaces(Document document,
                                              SimpleDateFormat formateur,
                                              DefinitionProcessus defProc,
                                              Vector listeComposant,
                                              ComposantProcessus courant,
                                              Referentiel ref)
    {
        // noeud liste_interface
        /*<liste_interface>
                <interface>
                        <id></id>
                        <interfaceRequiseComposant>_id_composant_</interfaceRequiseComposant>
                        <interfaceFournieComposant>_id_composant_</interfaceFournieComposant>
                        <liste_interfaceProduit>
                                <interfaceProduit>_id_produit_</interfaceProduit>
                        </liste_interfaceProduit>
                </interface>
         </liste_interface>*/

        Element liste_interface = document.createElement("liste_interface");

        for (int i = 0; i < listeComposant.size(); i++)
        {
            courant = (ComposantProcessus) ( (IdObjetModele) listeComposant.elementAt(i)).getRef();
            Vector interfacesRequises = courant.getInterfaceIn();
            Vector interfacesFournies = courant.getInterfaceOut();

            if (!interfacesRequises.isEmpty())
            {
                Element _interface = document.createElement("interface");
                liste_interface.appendChild(_interface);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + new Integer(courant.getIdComposant().getID()).toString() + "_" + interfacesRequises.size() + i + "_inr"));

                //id du composant
                //les espaces sont remplaces par des '_'
                //id du type : Nom_referentiel-Nom_composant

                Element interfaceRequiseComposant = document.createElement("interfaceRequiseComposant");
                interfaceRequiseComposant.appendChild(document.createTextNode(ref.getIdReferentielComposant(courant)));

                Element interfaceFournieComposant = document.createElement("interfaceFournieComposant");
                interfaceFournieComposant.appendChild(document.createTextNode(""));

                Element liste_interfaceProduit = document.createElement("liste_interfaceProduit");

                for (int j = 0; j < interfacesRequises.size(); j++)
                {
                    IdObjetModele io = (IdObjetModele) courant.getMapId().get(interfacesRequises.elementAt(j));
                    // Si le produit est change, on remplace par l'id du produit en sortie
                    Integer new_prod;
                    if (GenerationManager.getProduitChange(io) == null)
                    {
                        new_prod = new Integer(io.getID() + ( (i + 1) * 10000));
                    }
                    else
                    {
                        new_prod = new Integer(GenerationManager.getProduitChange(io).getID() +
                                               ((listeComposant.indexOf( ( (ComposantProcessus) GenerationManager.getProduitChange(io).getRef()).getIdComposant()) + 1) * 10000));
                    }

                    Element interfaceProduit = document.createElement("interfaceProduit");
                    interfaceProduit.appendChild(document.createTextNode("_" + new_prod.toString() + "_pro"));
                    liste_interfaceProduit.appendChild(interfaceProduit);
                }

                _interface.appendChild(id);
                _interface.appendChild(interfaceRequiseComposant);
                _interface.appendChild(interfaceFournieComposant);
                _interface.appendChild(liste_interfaceProduit);
            }

            if (!interfacesFournies.isEmpty())
            {
                Element _interface = document.createElement("interface");
                liste_interface.appendChild(_interface);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + new Integer(courant.getIdComposant().getID()).toString() + "_" + interfacesFournies.size() + i + "_inf"));

                Element interfaceRequiseComposant = document.createElement("interfaceRequiseComposant");
                interfaceRequiseComposant.appendChild(document.createTextNode(""));

                Element interfaceFournieComposant = document.createElement("interfaceFournieComposant");
                interfaceFournieComposant.appendChild(document.createTextNode(ref.getIdReferentielComposant(courant)));

                Element liste_interfaceProduit = document.createElement("liste_interfaceProduit");

                for (int j = 0; j < interfacesFournies.size(); j++)
                {
                    IdObjetModele io = (IdObjetModele) courant.getMapId().get(interfacesFournies.elementAt(j));
                    Element interfaceProduit = document.createElement("interfaceProduit");
                    interfaceProduit.appendChild(document.createTextNode("_" + new Integer(io.getID() + ( (i + 1) * 10000)).toString() + "_pro"));
                    liste_interfaceProduit.appendChild(interfaceProduit);
                }
                _interface.appendChild(id);
                _interface.appendChild(interfaceRequiseComposant);
                _interface.appendChild(interfaceFournieComposant);
                _interface.appendChild(liste_interfaceProduit);

            }
        }

        return liste_interface;
    }

    /**
     * creerNoeudListeActivites
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeActivites(Document document,
                                             SimpleDateFormat formateur,
                                             DefinitionProcessus defProc,
                                             Vector listeComposant,
                                             ComposantProcessus courant,
                                             Referentiel ref)
    {
        //noeud liste_activite
        /*<liste_activite>
                <activite>
                        <id></id>
                        <nom></nom>
                        <participationRole>_id_role_</participationRole>
                        <agregatDefinitionTravail>_id_definitionTravail_</agregatDefinitionTravail>
                        <liste_entreeProduit>
                                <entreeProduit>_id_produit_</entreeProduit>
                        </liste_entreeProduit>
                        <liste_sortieProduit>
                                <sortieProduit>_id_produit_</sortieProduit>
                        </liste_sortieProduit>
                        <elementPresentationId>_id_elementPresentation_</elementPresentationId>
                </activite>
               </liste_activite>*/

        Element liste_activite = document.createElement("liste_activite");
        int idRole;
        for (int i = 0; i < listeComposant.size(); i++)
        {
            courant = (ComposantProcessus) ( (IdObjetModele) listeComposant.elementAt(i)).getRef();
            Vector activites = ( (IdObjetModele) listeComposant.elementAt(i)).getActivite();
            for (int j = 0; j < activites.size(); j++)
            {
                IdObjetModele io = (IdObjetModele) activites.elementAt(j);

                Element activite = document.createElement("activite");
                liste_activite.appendChild(activite);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + (new Integer(io.getID() + ( (i + 1) * 10000))).toString() + "_act"));

                Element nom = document.createElement("nom");
                nom.appendChild(document.createTextNode(io.toString()));

                Element participationRole = document.createElement("participationRole");
                idRole = io.getIDRole();
                if (idRole == -1)
                {
                    //aucun role n'y est associe
                    participationRole.appendChild(document.createTextNode(""));
                }
                else
                {
                    participationRole.appendChild(document.createTextNode("_" + new Integer(idRole + ( (i + 1) * 10000)).toString() + "_rol"));
                }

                Element agregatDefinitionTravail = document.createElement("agregatDefinitionTravail");
                agregatDefinitionTravail.appendChild(document.createTextNode("_" + new Integer(io.getLaDefinitionTravail().getID() + ( (i + 1) * 10000)).toString() + "_det"));

                Vector listeEntreeProduit = io.getProduitEntree();
                Element liste_entreeProduit = document.createElement("liste_entreeProduit");
                for (int k = 0; k < listeEntreeProduit.size(); k++)
                {
                    Element entreeProduit = document.createElement("entreeProduit");
                    IdObjetModele prod = (IdObjetModele) listeEntreeProduit.elementAt(k);
                    if (GenerationManager.getProduitChange(prod) == null)
                    {
                        entreeProduit.appendChild(document.createTextNode("_" + new Integer( ( (IdObjetModele) listeEntreeProduit.elementAt(k)).getID() + ( (i + 1) * 10000)) + "_pro"));
                    }
                    else
                    {
                        entreeProduit.appendChild(document.createTextNode("_" + new Integer(GenerationManager.getProduitChange(prod).getID() + ((listeComposant.indexOf( ( (ComposantProcessus) GenerationManager.getProduitChange(prod).getRef()).getIdComposant()) + 1) * 10000)) + "_pro"));
                    }
                    liste_entreeProduit.appendChild(entreeProduit);
                }

                Vector listeSortieProduit = io.getProduitSortie();
                Element liste_sortieProduit = document.createElement("liste_sortieProduit");
                for (int k = 0; k < listeSortieProduit.size(); k++)
                {
                    Element sortieProduit = document.createElement("sortieProduit");
                    IdObjetModele prod = (IdObjetModele) listeSortieProduit.elementAt(k);
                    if (GenerationManager.getProduitChange(prod) == null)
                    {
                        sortieProduit.appendChild(document.createTextNode("_" + new Integer( ( (IdObjetModele) listeSortieProduit.elementAt(k)).getID() + ( (i + 1) * 10000)) + "_pro"));
                    }
                    else
                    {
                        sortieProduit.appendChild(document.createTextNode("_" + new Integer(GenerationManager.getProduitChange(prod).getID() + ((listeComposant.indexOf( ( (ComposantProcessus) GenerationManager.getProduitChange(prod).getRef()).getIdComposant()) +1) * 10000)) + "_pro"));
                    }
                    liste_sortieProduit.appendChild(sortieProduit);
                }

                // element de presentation
                Element elementPresentationId = document.createElement("elementPresentationId");
                String epid = "";
                boolean trouvep = false;
                IdObjetModele compep = (IdObjetModele) listeComposant.elementAt(i);
                PaquetagePresentation packep = compep.getPaquetagePresentation();
                Vector listep = packep.getListeElement();
                for (int u = 0; u < listep.size() && !trouvep; u++)
                {
                  ElementPresentation elemp = (ElementPresentation) listep.elementAt(u);
                  if (elemp == courant.getElementPresentation(io.getID())) {
                    epid = "_" + i + "_" + u + "_" + courant.getElementPresentation(io.getID()).getID_interne() + "_com_elp";
                    trouvep = true;
                  }
                }
                elementPresentationId.appendChild(document.createTextNode(epid));

                activite.appendChild(id);
                activite.appendChild(nom);
                activite.appendChild(participationRole);
                activite.appendChild(agregatDefinitionTravail);
                activite.appendChild(liste_entreeProduit);
                activite.appendChild(liste_sortieProduit);
                activite.appendChild(elementPresentationId);

            }
        }
        return liste_activite;
    }

    /**
     * creerNoeudListeDefinitionTravail
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeDefinitionTravail(Document document,
                                                     SimpleDateFormat formateur,
                                                     DefinitionProcessus defProc,
                                                     Vector listeComposant,
                                                     ComposantProcessus courant,
                                                     Referentiel ref)
    {

        //noeud liste_definitionTravail
        /*<liste_definitionTravail>
         <definitionTravail>
             <id></id>
                    <nom></nom>
                    <cheminDiagrammeActivites></cheminDiagrammeActivites>
                    <cheminDiagrammeFlots></cheminDiagrammeFlots>
                    <agregatComposant>_id_composant_</agregatComposant>
                    <liste_activiteId>
                     <activiteId>_id_activite_</activiteId>
                    </liste_activiteId>
                    <elementPresentationId>_id_elementPresentation_</elementPresentationId>
            </definitionTravail>
          </liste_definitionTravail>*/
        Element liste_definitionTravail = document.createElement("liste_definitionTravail");

        for (int i = 0; i < listeComposant.size(); i++)
        {
            courant = (ComposantProcessus) ( (IdObjetModele) listeComposant.elementAt(i)).getRef();
            Vector definition = ( (IdObjetModele) listeComposant.elementAt(i)).getDefinitionTravail();
            for (int j = 0; j < definition.size(); j++)
            {
                IdObjetModele io = (IdObjetModele) definition.elementAt(j);

                Element definitionTravail = document.createElement("definitionTravail");
                liste_definitionTravail.appendChild(definitionTravail);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + new Integer(io.getID() + ( (i + 1) * 10000)).toString() + "_det"));

                Element nom = document.createElement("nom");
                nom.appendChild(document.createTextNode(io.toString()));

                Element cheminDiagrammeActivites = document.createElement("cheminDiagrammeActivites");
                // recuperer l'id Apes qui va nous permettre de recuperer le diagramme
                int ID_Apes = io.getID();
                ComposantProcessus cp = (ComposantProcessus) io.getRef();
                IdObjetModele io2 = cp.getDiagrammeActivite(ID_Apes);
                String cheminHTML2 = io2.getChemin();
                if (!cheminHTML2.equals("")){
                  String diagPNG2=cheminHTML2.substring(2, cheminHTML2.lastIndexOf("/")+1);
                  if(this.exportDPC)
                    cheminDiagrammeActivites.appendChild(document.createTextNode(DPC_PATH+"/"+diagPNG2+DIAG_PATH));
                  else
                    cheminDiagrammeActivites.appendChild(document.createTextNode("."+"/"+diagPNG2+DIAG_PATH));
                }

                Element cheminDiagrammeFlots = document.createElement("cheminDiagrammeFlots");
                IdObjetModele io3 = cp.getDiagrammeFlotProduit(ID_Apes);
                String cheminHTML3 = io3.getChemin();
                if (!cheminHTML3.equals("")){
                  String diagPNGFlot=cheminHTML3.substring(2, cheminHTML3.lastIndexOf("/")+1);
                  if (this.exportDPC)
                  cheminDiagrammeFlots.appendChild(document.createTextNode(DPC_PATH + "/" + diagPNGFlot + DIAG_PATH));
                else
                  cheminDiagrammeFlots.appendChild(document.createTextNode("." + "/" + diagPNGFlot + DIAG_PATH));
                }

                Element agregatComposant = document.createElement("agregatComposant");
                agregatComposant.appendChild(document.createTextNode(ref.getIdReferentielComposant(courant)));

                Element liste_activiteId = document.createElement("liste_activiteId");
                for (int k = 0; k < io.getIDActivite().size(); k++)
                {
                    Integer activite = (new Integer( ( (Integer) io.getIDActivite().elementAt(k)).intValue() + ( (i + 1) * 10000)));
                    Element activiteId = document.createElement("activiteId");
                    activiteId.appendChild(document.createTextNode("_" + activite.toString() + "_act"));
                    liste_activiteId.appendChild(activiteId);
                }

                // element de presentation
                Element elementPresentationId = document.createElement("elementPresentationId");
                String epid = "";
                boolean trouvep = false;
                IdObjetModele compep = (IdObjetModele) listeComposant.elementAt(i);
                PaquetagePresentation packep = compep.getPaquetagePresentation();
                Vector listep = packep.getListeElement();
                for (int u = 0; u < listep.size() && !trouvep; u++) {
                  ElementPresentation elemp = (ElementPresentation) listep.elementAt(u);
                  if (elemp == courant.getElementPresentation(io.getID())) {
                    epid = "_" + i + "_" + u + "_" + courant.getElementPresentation(io.getID()).getID_interne() + "_com_elp";
                    trouvep = true;
                  }
                }
                elementPresentationId.appendChild(document.createTextNode(epid));

                definitionTravail.appendChild(id);
                definitionTravail.appendChild(nom);
                definitionTravail.appendChild(cheminDiagrammeActivites);
                definitionTravail.appendChild(cheminDiagrammeFlots);
                definitionTravail.appendChild(agregatComposant);
                definitionTravail.appendChild(liste_activiteId);
                definitionTravail.appendChild(elementPresentationId);
            }
        }

        return liste_definitionTravail;
    }

    /**
     * creerNoeudListeProduits
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeProduits(Document document,
                                            SimpleDateFormat formateur,
                                            DefinitionProcessus defProc,
                                            Vector listeComposant,
                                            ComposantProcessus courant,
                                            Referentiel ref)
    {
        //noeud liste_produit
        /*<liste_produit>
                <produit>
                        <id></id>
                        <nom></nom>
                        <agregatComposant>_id_composant_</agregatComposant>
                        <responsabiliteRole>_id_role_</responsabiliteRole>
                        <typeProduitId>_id_typeProduit_</typeProduitId>
                        <liste_interfaceId>
                                <interfaceId>_id_interface_</interfaceId>
                        </liste_interfaceId>
                        <liste_etatId>
                                <etatId>_id_etat_</etatId>
                        </liste_etatId>
                        <liste_entreeActivite>
                                <entreeActivite>_id_activite_</entreeActivite>
                        </liste_entreeActivite>
                        <liste_sortieActivite>
                                <sortieActivite>_id_activite_</sortieActivite>
                        </liste_sortieActivite>
                        <elementPresentationId>_id_elementPresentation_</elementPresentationId>
                </produit>
               </liste_produit>*/

        Element liste_produit = document.createElement("liste_produit");
        int idRole;
        for (int i = 0; i < listeComposant.size(); i++)
        {
            courant = (ComposantProcessus) ( (IdObjetModele) listeComposant.elementAt(i)).getRef();
            Vector produits = ( (IdObjetModele) listeComposant.elementAt(i)).getProduit();

            for (int j = 0; j < produits.size(); j++)
            {
                IdObjetModele io = (IdObjetModele) produits.elementAt(j);

                // On ecrit le produit que s'il ne s'agit pas d'un produit en entree lie vers un autre
                if (GenerationManager.getProduitChange(io) == null)
                {
                    Element produit = document.createElement("produit");
                    liste_produit.appendChild(produit);

                    Element id = document.createElement("id");
                    id.appendChild(document.createTextNode("_" + (new Integer(io.getID() + ( (i + 1) * 10000))).toString() + "_pro"));
                    //il est possible d'avoir egalement l'ID Apes   : courant.getElementPresentation(io.getID()).getID_Apes()

                    Element nom = document.createElement("nom");
                    nom.appendChild(document.createTextNode(io.toString()));

                    Element agregatComposant = document.createElement("agregatComposant");
                    agregatComposant.appendChild(document.createTextNode(ref.getIdReferentielComposant(courant)));

                    Element responsabiliteRole = document.createElement("responsabiliteRole");
                    idRole = io.getIDRole();
                    if (idRole == -1)
                    {
                        //aucun role n'y est associe
                        responsabiliteRole.appendChild(document.createTextNode(""));
                    }
                    else
                    {
                        responsabiliteRole.appendChild(document.createTextNode("_" + new Integer(idRole + ( (i + 1) * 10000)).toString() + "_rol"));
                    }

                    Element typeProduitId = document.createElement("typeProduitId");
                    ElementPresentation elementPresentation = courant.getElementPresentation(io.getID());
                    if (elementPresentation != null)
                    {
                        String typeProduitstr = elementPresentation.getTypeProduit();

                        //on va chercher le type produit de l'element
                        //et constituer l'id
                        boolean arreter_recherche = false;

                        for (int m = 0; (m < listeComposant.size()) && (!arreter_recherche); m++)
                        {
                            PaquetagePresentation test2 = ( (IdObjetModele) listeComposant.elementAt(m)).getPaquetagePresentation();
                            Vector elements2 = test2.getListeElement();

                            for (int n = 0; (n < elements2.size()) && (!arreter_recherche); n++)
                            {
                                ElementPresentation eltPre2 = (ElementPresentation) elements2.elementAt(n);
                                String typeProduitstr2 = eltPre2.getTypeProduit(); //recuperer le type du produit
                                if ( (typeProduitstr != null) && (typeProduitstr2 != null))
                                {
                                    if (typeProduitstr.equals(typeProduitstr2))
                                    {
                                        //on vient de trouver le premier element
                                        //qui a le meme type produit.
                                        //on reconstitue l'id du type produit
                                        //il est possible de preciser l'id en prefixant par "_" + m + "_" + n +
                                        typeProduitId.appendChild(document.createTextNode("_" + new Integer(eltPre2.getID_Apes() + 10000).toString() + "_typ"));
                                        //on sortira dans les boucles for
                                        arreter_recherche = true;
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        typeProduitId.appendChild(document.createTextNode(""));
                    }

                    Element liste_interfaceId = document.createElement("liste_interfaceId");

                    for (int k = 0; k < listeComposant.size(); k++)
                    {
                        ComposantProcessus courantImbrique = (ComposantProcessus) ( (IdObjetModele) listeComposant.elementAt(i)).getRef();
                        Vector interfacesRequises = courantImbrique.getInterfaceIn();
                        Vector interfacesFournies = courantImbrique.getInterfaceOut();
                        //on verifie si le produit apparait comme interfacesRequises,
                        //si c'est le cas on genere l'id de l'interface
                        if (interfacesRequises.contains(produits.elementAt(j)))
                        {
                            Element interfaceId = document.createElement("interfaceId");
                            interfaceId.appendChild(document.createTextNode("_" + new Integer(courant.getIdComposant().getID()).toString() + "_" + interfacesRequises.size() + k + "_inr"));
                            liste_interfaceId.appendChild(interfaceId);
                        }
                        //on verifie si le produit apparait comme interfacesFournies,
                        //si c'est le cas on genere l'id de l'interface
                        if (interfacesFournies.contains(produits.elementAt(j)))
                        {
                            Element interfaceId = document.createElement("interfaceId");
                            interfaceId.appendChild(document.createTextNode("_" + new Integer(courant.getIdComposant().getID()).toString() + "_" + interfacesFournies.size() + k + "_inf"));
                            liste_interfaceId.appendChild(interfaceId);
                        }
                    }

                    Element liste_etatId = document.createElement("liste_etatId");
                    //on recupere tous les etats du produit
                    Vector listeEtat = io.getEtats();
                    for (int k = 0; k < listeEtat.size(); k++)
                    {
                        Element etatId = document.createElement("etatId");
                        //on reconstitue l'id d'apres la regle fixee
                        etatId.appendChild(document.createTextNode("_" + new Integer(io.getID() + ( (i + 1) * 10000) + (j + 1)).toString() + k + "_eta"));
                        liste_etatId.appendChild(etatId);
                    }

                    Vector listeEntreeActivite = GenerationManager.getActivitesEntree(io);
                    Element liste_entreeActivite = document.createElement("liste_entreeActivite");
                    for (int k = 0; k < listeEntreeActivite.size(); k++)
                    {
                        Element entreeActivite = document.createElement("entreeActivite");
                        //on garde une reference sur l'objet modele correspondant a notre activite
                        IdObjetModele ioReel = (IdObjetModele) listeEntreeActivite.elementAt(k);
                        //L'id de l'activite depend d'un autre composant dans le cas d'une fusion.
                        //Explication :
                        //Il faut donc verifier que l'activite en question appartient bien au composant (sans fusion)
                        //ou si elle appartient a un autre (fusion)

                        //En effet, un produit qui est en sortie d'un composant A (en interface fournie),
                        //est conserve (contrairement au produit qui etait en requis de l'autre composant B).
                        //Le produit en sortie, n'avait pas d'entreeActivite avant la fusion.
                        //entreeActivite : activite pour laquelle le produit est en entree.
                        //Ce produit a desormais une activite, mais elle est dans un autre composant.
                        boolean activiteTrouvee = false;
                        //Il va falloir se souvenir de la position du composant de l'activite cherchee
                        int composantPocesseur = i;
                        for (int m = 0; m < listeComposant.size() && !activiteTrouvee; m++) {
                          Vector activitesCurseur = ( (IdObjetModele) listeComposant.elementAt(m)).getActivite();
                          for (int n = 0; n < activitesCurseur.size() && !activiteTrouvee; n++) {
                            //on prend l'objet modele sur lequel on passe
                            IdObjetModele ioCurseur = (IdObjetModele) activitesCurseur.elementAt(n);
                            if (ioReel == ioCurseur){
                              //dans ce cas, nous avons bien retrouve l'activite dans un composant
                              activiteTrouvee = true;
                              //on garde la position du composant dans la liste des composants
                              //car le systeme de generation d'id depend de cette position
                              composantPocesseur = m;
                            }
                          }
                        }
                        entreeActivite.appendChild(document.createTextNode("_" + (new Integer( (ioReel).getID() + ( (composantPocesseur + 1) * 10000))).toString() + "_act"));
                        liste_entreeActivite.appendChild(entreeActivite);
                    }

                    Vector listeSortieActivite = GenerationManager.getActivitesSortie(io);
                    Element liste_sortieActivite = document.createElement("liste_sortieActivite");
                    for (int k = 0; k < listeSortieActivite.size(); k++)
                    {
                        Element sortieActivite = document.createElement("sortieActivite");
                        sortieActivite.appendChild(document.createTextNode("_" + (new Integer( ( (IdObjetModele) listeSortieActivite.elementAt(k)).getID() + ( (i + 1) * 10000))).toString() + "_act"));
                        liste_sortieActivite.appendChild(sortieActivite);
                    }

                    // element de presentation
                    Element elementPresentationId = document.createElement("elementPresentationId");
                    String epid = "";
                    if (courant.getElementPresentation(io.getID()) != null)
                    {
                        boolean trouvep = false;
                        IdObjetModele compep = (IdObjetModele) listeComposant.elementAt(i);
                        PaquetagePresentation packep = compep.getPaquetagePresentation();
                        Vector listep = packep.getListeElement();
                        for (int u = 0; u < listep.size() && !trouvep; u++) {
                          ElementPresentation elemp = (ElementPresentation) listep.elementAt(u);
                          if (elemp == courant.getElementPresentation(io.getID())) {
                            epid = "_" + i + "_" + u + "_" + courant.getElementPresentation(io.getID()).getID_interne() + "_com_elp";
                            trouvep = true;
                          }
                        }
                      }
                    elementPresentationId.appendChild(document.createTextNode(epid));

                    produit.appendChild(id);
                    produit.appendChild(nom);
                    produit.appendChild(agregatComposant);
                    produit.appendChild(responsabiliteRole);
                    produit.appendChild(typeProduitId);
                    produit.appendChild(liste_interfaceId);
                    produit.appendChild(liste_etatId);
                    produit.appendChild(liste_entreeActivite);
                    produit.appendChild(liste_sortieActivite);
                    produit.appendChild(elementPresentationId);
                }
            }
        }
        return liste_produit;
    }

    /**
     * creerNoeudListeRoles
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeRoles(Document document,
                                         SimpleDateFormat formateur,
                                         DefinitionProcessus defProc,
                                         Vector listeComposant,
                                         ComposantProcessus courant,
                                         Referentiel ref)
    {
        //noeud liste_role
        /*<liste_role>
                <role>
                        <id></id>
                        <nom></nom>
                        <agregatComposant>_id_composant_</agregatComposant>
                        <liste_responsabiliteProduit>
                                <responsabiliteProduit>_id_produit_</responsabiliteProduit>
                        </liste_responsabiliteProduit>
                        <liste_participationActivite>
                                <participationActivite>_id_activite_</participationActivite>
                        </liste_participationActivite>
                        <elementPresentationId>_id_elementPresentation_</elementPresentationId>
                </role>
               </liste_role>*/

        Element liste_role = document.createElement("liste_role");

        for (int i = 0; i < listeComposant.size(); i++)
        {
            courant = (ComposantProcessus) ( (IdObjetModele) listeComposant.elementAt(i)).getRef();
            Vector roles = ( (IdObjetModele) listeComposant.elementAt(i)).getRole();
            for (int j = 0; j < roles.size(); j++)
            {
                IdObjetModele io = (IdObjetModele) roles.elementAt(j);

                Element role = document.createElement("role");
                liste_role.appendChild(role);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode("_" + (new Integer( ( (i + 1) * 10000) + io.getID())).toString() + "_rol"));

                Element nom = document.createElement("nom");
                nom.appendChild(document.createTextNode(io.toString()));

                Element agregatComposant = document.createElement("agregatComposant");
                agregatComposant.appendChild(document.createTextNode(ref.getIdReferentielComposant(courant)));

                Element liste_responsabiliteProduit = document.createElement("liste_responsabiliteProduit");
                Integer produit;
                //liste_responsabiliteProduit
                for (int k = 0; k < io.getIDProduit().size(); k++)
                {
                    Element responsabiliteProduit = document.createElement("responsabiliteProduit");
                    liste_responsabiliteProduit.appendChild(responsabiliteProduit);

                    int prodID = ( (Integer) io.getIDProduit().elementAt(k)).intValue();
                    // Recherche de l'ID objet modele du produit courant
                    IdObjetModele prod = null;
                    Vector listeProd = courant.getProduits();
                    for (int l = 0; l < listeProd.size(); l++)
                    {
                        if ( ( (IdObjetModele) listeProd.elementAt(l)).getID() == prodID)
                        {
                            prod = (IdObjetModele) listeProd.elementAt(l);
                        }
                    }

                    // Si le produit est change, on remplace par l'id du produit en sortie
                    if (GenerationManager.getProduitChange(prod) == null)
                    {
                        produit = new Integer(prodID + ( (i + 1) * 10000));
                    }
                    else
                    {
                        produit = new Integer(GenerationManager.getProduitChange(prod).getID() + ((listeComposant.indexOf( ( (ComposantProcessus) GenerationManager.getProduitChange(prod).getRef()).getIdComposant()) + 1) * 10000));
                    }
                    responsabiliteProduit.appendChild(document.createTextNode("_" + produit.toString() + "_pro"));
                }

                Element liste_participationActivite = document.createElement("liste_participationActivite");
                //liste_participationActivite
                //on recupere la liste des definitions de travail pour ne pas considerer
                //une definition de travail venant de APES comme une activite
                Vector definition = ( (IdObjetModele) listeComposant.elementAt(i)).getDefinitionTravail();

                for (int k = 0; k < io.getIDActivite().size(); k++)
                {
                  boolean estDefinition = false;
                  for (int m = 0; m < definition.size(); m++) {
                    if ( ( (IdObjetModele) definition.elementAt(m)).getID() == ( (Integer) io.getIDActivite().elementAt(k)).intValue())
                      estDefinition = true;
                  }
                  if (!estDefinition) {
                    Element participationActivite = document.createElement("participationActivite");
                    liste_participationActivite.appendChild(participationActivite);

                    Integer activite = new Integer( ( (Integer) io.getIDActivite().elementAt(k)).intValue() + ( (i + 1) * 10000));
                    participationActivite.appendChild(document.createTextNode("_" + activite.toString() + "_act"));
                  }
                }

                // element de presentation
                Element elementPresentationId = document.createElement("elementPresentationId");
                String epid = "";
                boolean trouvep = false;
                IdObjetModele compep = (IdObjetModele) listeComposant.elementAt(i);
                PaquetagePresentation packep = compep.getPaquetagePresentation();
                Vector listep = packep.getListeElement();
                for (int u = 0; u < listep.size() && !trouvep; u++) {
                  ElementPresentation elemp = (ElementPresentation) listep.elementAt(u);
                  if (elemp == courant.getElementPresentation(io.getID())) {
                    epid = "_" + i + "_" + u + "_" + courant.getElementPresentation(io.getID()).getID_interne() + "_com_elp";
                    trouvep = true;
                  }
                }
                elementPresentationId.appendChild(document.createTextNode(epid));

                role.appendChild(id);
                role.appendChild(nom);
                role.appendChild(agregatComposant);
                role.appendChild(liste_responsabiliteProduit);
                role.appendChild(liste_participationActivite);
                role.appendChild(elementPresentationId);
            }
        }

        return liste_role;
    }

    /**
     * creerNoeudListeComposants
     *
     * @param document Document
     * @param formateur SimpleDateFormat
     * @param defProc DefinitionProcessus
     * @param listeComposant Vector
     * @param courant ComposantProcessus
     * @param ref Referentiel
     * @return Element
     */
    private Element creerNoeudListeComposants(Document document,
                                              SimpleDateFormat formateur,
                                              DefinitionProcessus defProc,
                                              Vector listeComposant,
                                              ComposantProcessus courant,
                                              Referentiel ref)
    {
        // noeud liste_composant
        /*<liste_composant>
                  <composant>
                          <id></id>
                          <nom></nom>
                          <version></version>
                          <nomAuteur></nomAuteur>
                          <emailAuteur></emailAuteur>
                          <datePlacement></datePlacement>
                          <description></description>
                          <cheminDiagrammeInterfaces></cheminDiagrammeInterfaces>
                          <cheminDiagrammeFlots></cheminDiagrammeFlots>
                          <liste_cheminDiagrammeResponsabilites>
                                  <cheminDiagrammeResponsabilites></cheminDiagrammeResponsabilites>
                          </liste_cheminDiagrammeResponsabilites>
                          <ordreGeneration></ordreGeneration>
                          <liste_roleId>
                                  <roleId>_id_role_</roleId>
                          </liste_roleId>
                          <liste_produitId>
                                  <produitId>_id_produit_</produitId>
                          </liste_produitId>
                          <liste_definitionTravailId>
                                  <definitionTravailId>_id_definitionTravail_</definitionTravailId>
                          </liste_definitionTravailId>
                          <interfaceRequise>_id_interface_</interfaceRequise>
                          <interfaceFournie>_id_interface_</interfaceFournie>
                          <elementPresentationId>_id_elementPresentation_</elementPresentationId>
                  </composant>
          </liste_composant>*/

        Element liste_composant = document.createElement("liste_composant");

        for (int i = 0; i < listeComposant.size(); i++)
        {
            if (listeComposant.elementAt(i) instanceof IdObjetModele)
            {
                IdObjetModele idObjetModel = (IdObjetModele) listeComposant.elementAt(i);
                if (!idObjetModel.estComposantVide())
                {
                    courant = (ComposantProcessus) ( (IdObjetModele) listeComposant.elementAt(i)).getRef();
                    courant.getDiagrammeContexte();
                    Element composant = document.createElement("composant");
                    liste_composant.appendChild(composant);

                    Element id = document.createElement("id");
                    id.appendChild(document.createTextNode(ref.getIdReferentielComposant(courant)));

                    Element nom = document.createElement("nom");
                    nom.appendChild(document.createTextNode(courant.getNomComposant()));

                    Element version = document.createElement("version");
                    ElementReferentiel eltRef = ref.chercherElement(ref.nomComposantToId(courant.getNomComposant()),ElementReferentiel.COMPOSANT);
                    version.appendChild(document.createTextNode(eltRef.getVersion()));

                    Element nomAuteur = document.createElement("nomAuteur");
                    nomAuteur.appendChild(document.createTextNode(defProc.getAuteur()));

                    Element emailAuteur = document.createElement("emailAuteur");
                    emailAuteur.appendChild(document.createTextNode(defProc.getEmailAuteur()));

                    Element datePlacement = document.createElement("datePlacement");
                    datePlacement.appendChild(document.createTextNode(eltRef.getDatePlacement()));

                    Element description = document.createElement("description");
                    description.appendChild(document.createTextNode(courant.getElementPresentation(idObjetModel.getID()).getDescription()));

                    Element cheminDiagrammeInterfaces = document.createElement("cheminDiagrammeInterfaces");
                    String cheminInterfaceHTML = idObjetModel.getDiagrammeContexte().getChemin();
                    if(!cheminInterfaceHTML.equals("")){
                      String diagInterfacePNG = cheminInterfaceHTML.substring(2, cheminInterfaceHTML.lastIndexOf("/") + 1);
                      if(this.exportDPC)
                        cheminDiagrammeInterfaces.appendChild(document.createTextNode(DPC_PATH+"/"+diagInterfacePNG+DIAG_PATH));
                      else
                        cheminDiagrammeInterfaces.appendChild(document.createTextNode("."+"/"+diagInterfacePNG+DIAG_PATH));
                    }

                    Element cheminDiagrammeFlots = document.createElement("cheminDiagrammeFlots");
                    String cheminHTML = idObjetModel.getDiagrammeFlot().getChemin();
                    if(!cheminHTML.equals("")){
                      String diagPNG = cheminHTML.substring(2, cheminHTML.lastIndexOf("/") + 1);
                      if(this.exportDPC)
                        cheminDiagrammeFlots.appendChild(document.createTextNode(DPC_PATH+"/"+diagPNG+DIAG_PATH));
                      else
                        cheminDiagrammeFlots.appendChild(document.createTextNode("."+"/"+diagPNG+DIAG_PATH));
                    }

                    Element liste_cheminDiagrammeResponsabilites = document.createElement("liste_cheminDiagrammeResponsabilites");
                    Vector diagrammeResponsabilites = idObjetModel.getListeDiagrammeResponsabilite();
                    for(int j=0; j<diagrammeResponsabilites.size(); j++)
                    {
                      Element cheminDiagrammeResponsabilites=document.createElement("cheminDiagrammeResponsabilites");
                      String cheminResponsabilitesHTML=((IdObjetModele)diagrammeResponsabilites.elementAt(j)).getChemin();
                      if(!cheminResponsabilitesHTML.equals(""))
                      {
                        String diagResponsabilitesPNG=cheminResponsabilitesHTML.substring(2, cheminResponsabilitesHTML.lastIndexOf("/")+1);
                        if(this.exportDPC)
                          cheminDiagrammeResponsabilites.appendChild(document.createTextNode(DPC_PATH+"/"+diagResponsabilitesPNG+DIAG_PATH));
                        else
                          cheminDiagrammeResponsabilites.appendChild(document.createTextNode("."+"/"+diagResponsabilitesPNG+DIAG_PATH));
                        liste_cheminDiagrammeResponsabilites.appendChild(cheminDiagrammeResponsabilites);
                      }
                    }

                    Element ordreGeneration = document.createElement("ordreGeneration");
                    ordreGeneration.appendChild(document.createTextNode("" + defProc.getListeAGenerer().indexOf(idObjetModel)));

                    Element liste_roleId = document.createElement("liste_roleId");
                    Vector roles = courant.getRoles();
                    for (int j = 0; j < roles.size(); j++)
                    {
                        IdObjetModele io = (IdObjetModele) roles.elementAt(j);
                        Element roleId = document.createElement("roleId");
                        roleId.appendChild(document.createTextNode("_" + (new Integer( ( (i + 1) * 10000) + io.getID())).toString() + "_rol"));
                        liste_roleId.appendChild(roleId);
                    }

                    Element liste_produitId = document.createElement("liste_produitId");

                    //liste permettant de se souvenir quels produits on ete mis
                    Vector produitsEnInterface = new Vector();
                    //cas des produits en interfaces requises
                    Vector interfacesRequises = courant.getInterfaceIn();
                    for (int j = 0; j < interfacesRequises.size(); j++) {
                      IdObjetModele ioInterface = (IdObjetModele) courant.getMapId().get(interfacesRequises.elementAt(j));
                      Integer new_prod;
                      if (GenerationManager.getProduitChange(ioInterface) == null) {
                        new_prod = new Integer(ioInterface.getID() + ( (i + 1) * 10000));
                      }
                      else {
                        produitsEnInterface.add(new Integer(ioInterface.getID() + ( (i + 1) * 10000)));
                        new_prod = new Integer(GenerationManager.getProduitChange(ioInterface).getID() +
                                               ( (listeComposant.indexOf( ( (ComposantProcessus) GenerationManager.getProduitChange(ioInterface).getRef()).getIdComposant()) + 1) * 10000));
                      }
                      produitsEnInterface.add(new_prod);
                      Element produitId = document.createElement("produitId");
                      produitId.appendChild(document.createTextNode("_" + new_prod + "_pro"));
                      liste_produitId.appendChild(produitId);
                    }
                    //cas des produits en interfaces fournies
                    Vector interfacesFournies = courant.getInterfaceOut();
                    for (int j = 0; j < interfacesFournies.size(); j++) {
                      IdObjetModele ioInterface = (IdObjetModele) courant.getMapId().get(interfacesFournies.elementAt(j));
                      Integer new_prod;
                      if (GenerationManager.getProduitChange(ioInterface) == null) {
                        new_prod = new Integer(ioInterface.getID() + ( (i + 1) * 10000));
                      }
                      else {
                        produitsEnInterface.add(new Integer(ioInterface.getID() + ( (i + 1) * 10000)));
                        new_prod = new Integer(GenerationManager.getProduitChange(ioInterface).getID() +
                                               ( (listeComposant.indexOf( ( (ComposantProcessus) GenerationManager.getProduitChange(ioInterface).getRef()).getIdComposant()) + 1) * 10000));
                      }
                      produitsEnInterface.add(new_prod);
                      Element produitId = document.createElement("produitId");
                      produitId.appendChild(document.createTextNode("_" + new_prod + "_pro"));
                      liste_produitId.appendChild(produitId);
                    }

                    //reste a prendre en compte les produits exterieurs
                    Vector produits = courant.getProduits();
                    for (int j = 0; j < produits.size(); j++)
                    {
                        IdObjetModele io = (IdObjetModele) produits.elementAt(j);
                        Integer idProd = new Integer( ( (i + 1) * 10000) + io.getID());
                        if(!produitsEnInterface.contains(idProd)){
                          Element produitId = document.createElement("produitId");
                          produitId.appendChild(document.createTextNode("_" + (new Integer( ( (i + 1) * 10000) + io.getID())).toString() + "_pro"));
                          liste_produitId.appendChild(produitId);
                        }
                    }

                    Element liste_definitionTravailId = document.createElement("liste_definitionTravailId");

                    Vector definitionsTravail = courant.getDefinitionTravail();
                    for (int j = 0; j < definitionsTravail.size(); j++)
                    {
                        IdObjetModele io = (IdObjetModele) definitionsTravail.elementAt(j);
                        Element definitionTravailId = document.createElement("definitionTravailId");
                        definitionTravailId.appendChild(document.createTextNode("_" + new Integer(io.getID() + ( (i + 1) * 10000)).toString() + "_det"));
                        liste_definitionTravailId.appendChild(definitionTravailId);
                    }

                    Element interfaceRequise = document.createElement("interfaceRequise");
                    if (courant.getInterfaceIn().size() != 0)
                      interfaceRequise.appendChild(document.createTextNode("_" + new Integer(courant.getIdComposant().getID()).toString() + "_" + courant.getInterfaceIn().size() + i + "_inr"));
                    else
                      interfaceRequise.appendChild(document.createTextNode(""));

                    Element interfaceFournie = document.createElement("interfaceFournie");
                    if (courant.getInterfaceOut().size() != 0)
                      interfaceFournie.appendChild(document.createTextNode("_" + new Integer(courant.getIdComposant().getID()).toString() + "_" + courant.getInterfaceOut().size() + i + "_inf"));
                    else
                      interfaceFournie.appendChild(document.createTextNode(""));

                    // element de presentation
                    Element elementPresentationId = document.createElement("elementPresentationId");
                    String epid = "";
                    boolean trouvep = false;
                    IdObjetModele compep = (IdObjetModele) listeComposant.elementAt(i);
                    PaquetagePresentation packep = compep.getPaquetagePresentation();
                    Vector listep = packep.getListeElement();
                    for (int u = 0; u < listep.size() && !trouvep; u++) {
                      ElementPresentation elemp = (ElementPresentation) listep.elementAt(u);
                      if (elemp == courant.getElementPresentation(idObjetModel.getID())) {
                        epid = "_" + i + "_" + u + "_" + courant.getElementPresentation(idObjetModel.getID()).getID_interne() + "_com_elp";
                        trouvep = true;
                      }
                    }
                    elementPresentationId.appendChild(document.createTextNode(epid));

                    composant.appendChild(id);
                    composant.appendChild(nom);
                    composant.appendChild(version);
                    composant.appendChild(nomAuteur);
                    composant.appendChild(emailAuteur);
                    composant.appendChild(datePlacement);
                    composant.appendChild(description);
                    composant.appendChild(cheminDiagrammeInterfaces);
                    composant.appendChild(cheminDiagrammeFlots);
                    composant.appendChild(liste_cheminDiagrammeResponsabilites);
                    composant.appendChild(ordreGeneration);
                    composant.appendChild(liste_roleId);
                    composant.appendChild(liste_produitId);
                    composant.appendChild(liste_definitionTravailId);
                    composant.appendChild(interfaceRequise);
                    composant.appendChild(interfaceFournie);
                    composant.appendChild(elementPresentationId);
                }
            }
        }

        return liste_composant;
    }

    /**
     * creerNoeudexportExecution
     *
     * @return Node
     */
    private Element creerNoeudExportExecution(Document document,
                                              SimpleDateFormat formateur,
                                              DefinitionProcessus defProc,
                                              Vector listeComposant,
                                              ComposantProcessus courant,
                                              Referentiel ref)
    {
        // noeud processus
        /*<processus>
                  <id></id>
                  <nom></nom>
                  <nomAuteur></nomAuteur>
                  <emailAuteur></emailAuteur>
                  <description></description>
                  <piedPage></piedPage>
                  <cheminGeneration></cheminGeneration>
                  <liste_composantId>
                          <composantId>_id_composant_</composantId>
                  </liste_composantId>
                  <liste_paquetagePresentationId>
                          <paquetagePresentationId>_id_paquetagePresentation_</paquetagePresentationId>
                  </liste_paquetagePresentationId>
          </processus>*/

        Element exportExecution = document.createElement("exportExecution");
        Element processus = document.createElement("processus");
        exportExecution.appendChild(processus);

        Element id = document.createElement("id");
        id.appendChild(document.createTextNode(ref.getIdReferentielDefProc(defProc)));

        Element nom = document.createElement("nom");
        nom.appendChild(document.createTextNode(defProc.getNomDefProc()));

        Element nomAuteur = document.createElement("nomAuteur");
        nomAuteur.appendChild(document.createTextNode(defProc.getAuteur()));

        Element emailAuteur = document.createElement("emailAuteur");
        emailAuteur.appendChild(document.createTextNode(defProc.getEmailAuteur()));

        Element dateExport = document.createElement("dateExport");
        Date date = new Date();
        dateExport.appendChild(document.createTextNode(formateur.format(date)));

        Element description = document.createElement("description");
        description.appendChild(document.createTextNode(defProc.getCommentaires()));

        Element piedPage = document.createElement("piedPage");
        piedPage.appendChild(document.createTextNode(defProc.getPiedPage()));

        Element cheminGeneration = document.createElement("cheminGeneration");
        if(this.exportDPC)
          cheminGeneration.appendChild(document.createTextNode(DPC_PATH + "/"));
        else
          cheminGeneration.appendChild(document.createTextNode(defProc.getRepertoireGeneration()));

        Element liste_composantId = document.createElement("liste_composantId");
        //voir id composant pour le format de l'id
        for (int i = 0; i < listeComposant.size(); i++)
        {
            if (listeComposant.elementAt(i) instanceof IdObjetModele)
            {
                IdObjetModele idObjetModel = (IdObjetModele) listeComposant.elementAt(i);
                if (!idObjetModel.estComposantVide())
                {
                    courant = (ComposantProcessus) ( (IdObjetModele) listeComposant.elementAt(i)).getRef();
                    Element composantId = document.createElement("composantId");
                    composantId.appendChild(document.createTextNode(ref.getIdReferentielComposant(courant)));
                    liste_composantId.appendChild(composantId);
                }
            }
        }

        // liste de paquetage de presentation
        Element liste_paquetagePresentationId = document.createElement("liste_paquetagePresentationId");
        Vector paquetageListeAGenerer = Application.getApplication().getProjet().getDefProc().getListeAGenerer();
        for (int i = 0; i < paquetageListeAGenerer.size(); i++) {
          if (paquetageListeAGenerer.elementAt(i)instanceof PaquetagePresentation) {
            PaquetagePresentation paquetagePresentation = (PaquetagePresentation) paquetageListeAGenerer.elementAt(i);
            Element paquetagePresentationId = document.createElement("paquetagePresentationId");
            paquetagePresentationId.appendChild(document.createTextNode("_" + ref.nomPresentationToId(paquetagePresentation.getNomPresentation()) + "_pap"));
            liste_paquetagePresentationId.appendChild(paquetagePresentationId);
          }
        }

        processus.appendChild(id);
        processus.appendChild(nom);
        processus.appendChild(nomAuteur);
        processus.appendChild(emailAuteur);
        processus.appendChild(dateExport);
        processus.appendChild(description);
        processus.appendChild(piedPage);
        processus.appendChild(cheminGeneration);
        processus.appendChild(liste_composantId);
        processus.appendChild(liste_paquetagePresentationId);

        return exportExecution;
    }

    /**
     * exporter
     *
     * @return InputStream
     */
    public InputStream getStreamResult()
    {
        return this.sortieExport;
    }
    
    public boolean chargerPresentation() {
		// On charge les presentation de chaque composants
		Vector listcompcell=((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(0)).getComposantCellCells();
		int i=0;
		boolean continuer = true;
		ComposantProcessus cp2; 
		HashMap presentation;
		ChargeurPresentationComposant cpc;
		while((continuer==true) && (i<listcompcell.size())) {
			ComposantCell cp = (ComposantCell)listcompcell.elementAt(i);
			if(cp.isParse()==false) {
				cpc = new ChargeurPresentationComposant(cp.getCompProc().getNomFichier());
				continuer = cpc.chargerPresentationComposant(cp.getCompProc());
				if((continuer==false) && (!cp.getCompProc().estVide())) {
					// On n'a pas trouvé le .pre
					JOptionPane.showMessageDialog(null, Application.getApplication().getTraduction("ERR_ChargeurPresentation4")+" "+cp.getCompProc().getNomComposant()+" "+Application.getApplication().getTraduction("ERR_ChargeurPresentation5")+" "+cp.getCompProc().getNomFichier().replace(".apes", ".pre")+" "+Application.getApplication().getTraduction("ERR_ChargeurPresentation6"),
						      "Error",
						      JOptionPane.OK_OPTION);
					return false;
				}
				if(continuer) {
					cp2 = cpc.getComposantCharge();
					presentation = cpc.getMapPresentation();
					cp2.initialiserPresentation(presentation);
				}
			}
			i++;
		}
		return continuer;
	}
}
