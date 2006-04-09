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

package iepp.ui.preferences;

import iepp.Application;

import java.awt.Dimension;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ArbrePreferences
    extends JTree {
  /**
   * Lien vers la boîte de dialogue contenant l'arbre
   */
  private FenetrePreference mPrefDiag;

  public ArbrePreferences(FenetrePreference prefDiag) {
    this.mPrefDiag = prefDiag;

    // gestionnaire d'évènement sur l'arbre
    ManagerTree gp = new ManagerTree();

    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new PreferenceTreeItem(
        Application.getApplication().getTraduction("Preferences"),
        Application.getApplication().getTraduction("Preferences"), 0));

    DefaultMutableTreeNode generalItem = new DefaultMutableTreeNode(new PreferenceTreeItem
        (PanneauDescription.GENERAL_KEY, Application.getApplication().getTraduction(PanneauDescription.GENERAL_KEY), PreferenceTreeItem.DESC_PANEL));

    generalItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                               (PanneauLangue.LANGUAGE_PANEL_KEY, Application.getApplication().getTraduction(PanneauLangue.LANGUAGE_PANEL_KEY), PreferenceTreeItem.LANGUAGE_PANEL)));

    
    /**************************************************************************************
    generalItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                               (PanneauDiagramme.DIAGRAM_PANEL_KEY, Application.getApplication().getTraduction(PanneauDiagramme.DIAGRAM_PANEL_KEY), PreferenceTreeItem.DIAGRAM_PANEL)));
     **************************************************************************************/
    
    /**************** modif  du 10 03 06 *********************
    generalItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                               (PanneauRepertoireExport.EXPORT_DIRECTORY_PANEL_KEY, Application.getApplication().getTraduction(PanneauRepertoireExport.EXPORT_DIRECTORY_PANEL_KEY), PreferenceTreeItem.EXPORT_DIRECTORY_PANEL)));
    *********************************************************/
    
    DefaultMutableTreeNode referentielItem = new DefaultMutableTreeNode(new PreferenceTreeItem
        (PanneauDescription.REPOSITORY_KEY, Application.getApplication().getTraduction(PanneauDescription.REPOSITORY_KEY), PreferenceTreeItem.DESC_PANEL));

    referentielItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
        (PanneauReferentiel.REPOSITORY_PANEL_KEY, Application.getApplication().getTraduction(PanneauReferentiel.REPOSITORY_PANEL_KEY), PreferenceTreeItem.REPOSITORY_PANEL)));

    DefaultMutableTreeNode DPItem = new DefaultMutableTreeNode(new PreferenceTreeItem
        (PanneauDescription.DP_KEY, Application.getApplication().getTraduction(PanneauDescription.DP_KEY), PreferenceTreeItem.DESC_PANEL));

    DPItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                          (PanneauDP.DP_PANEL_KEY, Application.getApplication().getTraduction(PanneauDP.DP_PANEL_KEY), PreferenceTreeItem.DP_PANEL)));

    DPItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                          (PanneauDPDescription.DP_DESCRIPTION_PANEL_KEY, Application.getApplication().getTraduction(PanneauDPDescription.DP_DESCRIPTION_PANEL_KEY), PreferenceTreeItem.DP_DESCRIPTION_PANEL)));

    DefaultMutableTreeNode generationItem = new DefaultMutableTreeNode(new PreferenceTreeItem
        (PanneauDescription.GENERATION_KEY, Application.getApplication().getTraduction(PanneauDescription.GENERATION_KEY), PreferenceTreeItem.DESC_PANEL));

    generationItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                                  (PanneauGenerationOption.GENERATION_OPTION_PANEL_KEY, Application.getApplication().getTraduction(PanneauGenerationOption.GENERATION_OPTION_PANEL_KEY), PreferenceTreeItem.GENERATION_OPTION_PANEL_KEY)));

    generationItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                                  (PanneauDPGeneration.DP_GENERATION_PANEL_KEY, Application.getApplication().getTraduction(PanneauDPGeneration.DP_GENERATION_PANEL_KEY), PreferenceTreeItem.DP_GENERATION_PANEL)));

    //modif 2XMI Amandine
    generationItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                                  (PanneauGenerationRole.ROLE_GENERATION_PANEL_KEY, Application.getApplication().getTraduction(PanneauGenerationRole.ROLE_GENERATION_PANEL_KEY), PreferenceTreeItem.ROLE_GENERATION_PANEL)));

    generationItem.add(new DefaultMutableTreeNode(new PreferenceTreeItem
                                                  (PanneauGeneration.GENERATION_PANEL_KEY, Application.getApplication().getTraduction(PanneauGeneration.GENERATION_PANEL_KEY), PreferenceTreeItem.GENERATION_PANEL)));

    DefaultMutableTreeNode proprietesResponsableItem = new DefaultMutableTreeNode(new PreferenceTreeItem
        (PanneauProprietesRespComp.RESPONSABLE_KEY, Application.getApplication().getTraduction(PanneauProprietesRespComp.RESPONSABLE_KEY), PreferenceTreeItem.COMPOSANT_DESCRIPTION_RESP_PANEL));

    DefaultMutableTreeNode proprietesMailItem = new DefaultMutableTreeNode(new PreferenceTreeItem
        (PanneauProprietesRespComp.MAIL_KEY, Application.getApplication().getTraduction(PanneauProprietesRespComp.MAIL_KEY), PreferenceTreeItem.COMPOSANT_DESCRIPTION_MAIL_PANEL));

    DefaultMutableTreeNode proprietesVersionItem = new DefaultMutableTreeNode(new PreferenceTreeItem
            (PanneauProprietesRespComp.VERSION_KEY, Application.getApplication().getTraduction(PanneauProprietesRespComp.VERSION_KEY), PreferenceTreeItem.COMPOSANT_DESCRIPTION_VERSION_PANEL));

    DefaultMutableTreeNode proprietesPAQItem = new DefaultMutableTreeNode(new PreferenceTreeItem
            (PanneauProprietesPaquetage.GENERAL_KEY, Application.getApplication().getTraduction(PanneauProprietesPaquetage.GENERAL_KEY), PreferenceTreeItem.PAQ_DESCRIPTION_PANEL));

    switch (this.mPrefDiag.getType()) {
      case FenetrePreference.TYPE_PREFERENCES:
        root.add(generalItem);
        /**************** modif  du 10 03 06 *********************
        root.add(referentielItem);
        **********************************************************/
        break;
      case FenetrePreference.TYPE_DP:
        root.add(DPItem);
        root.add(generationItem);
        break;
      case FenetrePreference.TYPE_GENERATION:
        root.add(DPItem);
        root.add(generationItem);
        break;
      case FenetrePreference.TYPE_COMPOSANT:
        root.add(proprietesResponsableItem);
        root.add(proprietesMailItem);
        root.add(proprietesVersionItem);
        break;
      case FenetrePreference.TYPE_PAQ:
        root.add(proprietesPAQItem);
        break;
    }

    this.setRootVisible(false);
    this.addTreeSelectionListener(gp);
    this.setModel(new DefaultTreeModel(root));
    if (this.mPrefDiag.getType()== FenetrePreference.TYPE_COMPOSANT | this.mPrefDiag.getType()== FenetrePreference.TYPE_PAQ)
      this.setPreferredSize(new Dimension(110, 50));
    else
      this.setPreferredSize(new Dimension(170, 50));
    this.expandAll(this, true);
  }

  private class ManagerTree
      implements TreeSelectionListener {
    public void valueChanged(TreeSelectionEvent e) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
      if (node != null) {
        Object o = e.getSource();
        int panel = ( (PreferenceTreeItem) node.getUserObject()).getPanel();
        String key = ( (PreferenceTreeItem) node.getUserObject()).getKey();

        if (panel == PreferenceTreeItem.GENERATION_OPTION_PANEL_KEY) {
          mPrefDiag.getGenOptionPanel().setVisible(true);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          *********************************************************/
          
          mPrefDiag.getGenerationPanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          //mPrefDiag.getRepertoireExportPanel().setVisible(false);
          *********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.DP_GENERATION_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(true);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          *********************************************************/
          
          mPrefDiag.getGenerationPanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          **********************************************************/
          
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.DP_DESCRIPTION_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(true);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          *********************************************************/
          mPrefDiag.getGenerationPanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          *********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
    
        /**********************************************************************
        else if (panel == PreferenceTreeItem.DIAGRAM_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(true);
          mPrefDiag.getReferentielPanel().setVisible(false);
          mPrefDiag.getGenerationPanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        
        ***********************************************************************/
        else if (panel == PreferenceTreeItem.DP_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(true);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          *********************************************************/
          mPrefDiag.getGenerationPanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          **********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.REPOSITORY_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(true);
          **********************************************************/
          mPrefDiag.getGenerationPanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          *********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.GENERATION_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          mPrefDiag.getGenerationPanel().setVisible(true);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          **********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.DESC_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(true);
          mPrefDiag.getLanguagePanel().setVisible(false);
          mPrefDiag.getGenerationPanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          **********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.LANGUAGE_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(true);
          mPrefDiag.getGenerationPanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          **********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.EXPORT_DIRECTORY_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          mPrefDiag.getGenerationPanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          mPrefDiag.getRepertoireExportPanel().setVisible(true);
          **********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.ROLE_GENERATION_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          mPrefDiag.getGenerationPanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          **********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(true);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.COMPOSANT_DESCRIPTION_RESP_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          mPrefDiag.getGenerationPanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          **********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(true);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if (panel == PreferenceTreeItem.COMPOSANT_DESCRIPTION_MAIL_PANEL) {
          mPrefDiag.getGenOptionPanel().setVisible(false);
          mPrefDiag.getDPGenPanel().setVisible(false);
          mPrefDiag.getDPDescPanel().setVisible(false);
          mPrefDiag.getDPPanel().setVisible(false);
          mPrefDiag.getDiagrammePanel().setVisible(false);
          mPrefDiag.getDescriptionPanel().setVisible(false);
          mPrefDiag.getLanguagePanel().setVisible(false);
          mPrefDiag.getGenerationPanel().setVisible(false);
          /**************** modif  du 10 03 06 *********************
          mPrefDiag.getReferentielPanel().setVisible(false);
          mPrefDiag.getRepertoireExportPanel().setVisible(false);
          *********************************************************/
          //modif 2XMI Amandine
          mPrefDiag.getGenerationRole().setVisible(false);
          mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(true);
            mPrefDiag.getProprietesVerComposant().setVisible(false);
          //mPrefDiag.getProprietesPaquetage().setVisible(false);
        }
        else if(panel==PreferenceTreeItem.COMPOSANT_DESCRIPTION_VERSION_PANEL){
            mPrefDiag.getGenOptionPanel().setVisible(false);
            mPrefDiag.getDPGenPanel().setVisible(false);
            mPrefDiag.getDPDescPanel().setVisible(false);
            mPrefDiag.getDPPanel().setVisible(false);
            mPrefDiag.getDiagrammePanel().setVisible(false);
            mPrefDiag.getDescriptionPanel().setVisible(false);
            mPrefDiag.getLanguagePanel().setVisible(false);
            mPrefDiag.getGenerationPanel().setVisible(false);
            /**************** modif  du 10 03 06 *********************
            mPrefDiag.getReferentielPanel().setVisible(false);
            mPrefDiag.getRepertoireExportPanel().setVisible(false);
            *********************************************************/
            //modif 2XMI Amandine
            mPrefDiag.getGenerationRole().setVisible(false);
            mPrefDiag.getProprietesRespComposant().setVisible(false);
            mPrefDiag.getProprietesMailComposant().setVisible(false);
            mPrefDiag.getProprietesVerComposant().setVisible(true);
            //mPrefDiag.getProprietesPaquetage().setVisible(false);
          }
          else if(panel==PreferenceTreeItem.PAQ_DESCRIPTION_PANEL){
              mPrefDiag.getGenOptionPanel().setVisible(false);
              mPrefDiag.getDPGenPanel().setVisible(false);
              mPrefDiag.getDPDescPanel().setVisible(false);
              mPrefDiag.getDPPanel().setVisible(false);
              mPrefDiag.getDiagrammePanel().setVisible(false);
              mPrefDiag.getDescriptionPanel().setVisible(false);
              mPrefDiag.getLanguagePanel().setVisible(false);
              mPrefDiag.getGenerationPanel().setVisible(false);
              /**************** modif  du 10 03 06 *********************
              mPrefDiag.getReferentielPanel().setVisible(false);
              mPrefDiag.getRepertoireExportPanel().setVisible(false);
              **********************************************************/
              //modif 2XMI Amandine
              mPrefDiag.getGenerationRole().setVisible(false);
              mPrefDiag.getProprietesRespComposant().setVisible(false);
              mPrefDiag.getProprietesMailComposant().setVisible(false);
              mPrefDiag.getProprietesVerComposant().setVisible(false);
              //mPrefDiag.getProprietesPaquetage().setVisible(true);
        }
        mPrefDiag.setInnerPanel(panel, key);
      }
    }
  }

  //	 If expand is true, expands all nodes in the tree.
  // Otherwise, collapses all nodes in the tree.
  public void expandAll(JTree tree, boolean expand) {
    TreeNode root = (TreeNode) tree.getModel().getRoot();

    // Traverse tree from root
    expandAll(tree, new TreePath(root), expand);
  }

  private void expandAll(JTree tree, TreePath parent, boolean expand) {
    // Traverse children
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements(); ) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandAll(tree, path, expand);
      }
    }

    // Expansion or collapse must be done bottom-up
    if (expand) {
      tree.expandPath(parent);
    }
    else {
      tree.collapsePath(parent);
    }
  }
}
