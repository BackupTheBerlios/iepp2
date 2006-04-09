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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import iepp.Application;
import iepp.ui.FenetrePrincipale;
import iepp.ui.preferences.PanneauProprietesRespComp;
import iepp.ui.preferences.PanneauProprietesMailComp;
import iepp.ui.preferences.PanneauProprietesVerComp;
import iepp.ui.preferences.PanneauProprietesPaquetage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import iepp.domaine.IdObjetModele;

public class FenetrePreference
    extends JDialog {
  /**
   * Panneau sur lequel s'affiche l'arbre et la partie de droite
   * qui change selon l'option choisie
   */
  private JPanel panneau;

  private PanneauGenerationOption panneauGenOption;
  private PanneauDP panneauDP;
  private PanneauDPGeneration panneauDPGeneration;
  private PanneauDPDescription panneauDPDesc;
  private PanneauDiagramme panneauDiagramme;
  /**************** modif  du 10 03 06 **********************
  private PanneauReferentiel panneauReferentiel;
  **********************************************************/
  private PanneauDescription panneauDescription;
  private PanneauLangue panneauLangue;
  private PanneauGeneration panneauGeneration;
  //Modif 2XMI Amandine
  private PanneauGenerationRole panneauGenerationRole;
  //fin modif Amandine
  // debut modif 2xmi chaouk
  /**************** modif  du 10 03 06 *********************
  private PanneauRepertoireExport panneauRepertoireExport;
  **********************************************************/
  // fin modif 2xmi chaouk
  private PanneauProprietesRespComp panneauProprietesRespComposant;
  private PanneauProprietesMailComp panneauProprietesMailComposant;
  private PanneauProprietesVerComp panneauProprietesVerComposant;
  private PanneauProprietesPaquetage panneauProprietesPaquetage;

  private JButton apply;
  private JButton ok;
  private JButton cancel;

  private int type_courant;

  private IdObjetModele comp_proprietes;
  private long idPres;

  public static final int TYPE_DP = 0;
  public static final int TYPE_GENERATION = 1;
  public static final int TYPE_PREFERENCES = 2;
  public static final int TYPE_COMPOSANT = 3;
  public static final int TYPE_PAQ = 4;

  public FenetrePreference(FenetrePrincipale parent, int type) {
    super(parent, true);
    initTailleFenetre(type);
    initFenetre(parent,type);
  }

  //constructeur utilise dans le cas ou on souhaite voir les proprietes d'un composant
  //il faut garder une reference sur le composant choisi par l'utilisateur
  //reference conservee par le parametre composant
  public FenetrePreference(FenetrePrincipale parent, int type, IdObjetModele composant) {
      this.comp_proprietes = composant;
      
      this.setSize(400, 300);
      initFenetre(parent, type);
  }

  //constructeur utilise dans le cas ou on souhaite voir les proprietes d'un paquetage
  //il faut garder une reference sur le paquetage choisi par l'utilisateur
  //reference conservee par le parametre idPres
  public FenetrePreference(FenetrePrincipale parent, int type, long idPres) {
      this.idPres = idPres;
      this.setSize(400, 300);
      initFenetre(parent, type);
  }

  public void initTailleFenetre(int type){
      switch(type){
      	case TYPE_GENERATION:
      		this.setSize(780, 600);
      		break;
      	case TYPE_PREFERENCES:
      		this.setSize(400, 300);
      		break;
      	case TYPE_DP:
      		this.setSize(780, 700);
      		break;
      	case TYPE_COMPOSANT:
      		this.setSize(780, 700);
      		break;
      	case TYPE_PAQ:
      		this.setSize(780, 700);
      		break;
      }
  }

  public void initFenetre(FenetrePrincipale parent, int type){
      this.type_courant = type;

      // gestionnaire de mise en page
      this.getContentPane().setLayout(new BorderLayout());

      // cr\uFFFDer l'arbre de navigation
      ArbrePreferences tree = new ArbrePreferences(this);

      Container panelMarge = getContentPane();
      // cr\uFFFDer le panneau qui va changer
      
      this.panneau = new JPanel();
      this.panneau.setLayout(new BorderLayout());
      this.panneau.add(tree, BorderLayout.WEST);

      // ajouter the buttons ok and cancel and apply
      ManagerButton manageButt = new ManagerButton();
      Box bottom = Box.createHorizontalBox();
      this.apply = new JButton(Application.getApplication().getTraduction("Appliquer"));
      this.apply.addActionListener(manageButt);
      this.ok = new JButton(Application.getApplication().getTraduction("OK"));
      this.ok.addActionListener(manageButt);
      this.cancel = new JButton(Application.getApplication().getTraduction("Annuler"));
      this.cancel.addActionListener(manageButt);
      bottom.add(Box.createHorizontalGlue());
      bottom.add(this.apply);
      bottom.add(this.ok);
      bottom.add(this.cancel);

      tree.setBorder(BorderFactory.createLoweredBevelBorder());
      this.panneau.add(bottom, BorderLayout.SOUTH);
      
      
      //this.panneau.add(this.panneauDescription,BorderLayout.CENTER);

      // create the panel
      panelMarge.add(this.panneau, BorderLayout.CENTER);
      panelMarge.add(new JLabel(" "), BorderLayout.NORTH);
      panelMarge.add(new JLabel("   "), BorderLayout.EAST);
      panelMarge.add(new JLabel("   "), BorderLayout.WEST);
      panelMarge.add(new JLabel(" "), BorderLayout.SOUTH);

      this.initPreferencesDialog();

      Rectangle bounds = parent.getBounds();
      this.setLocation(bounds.x + (int) bounds.width / 2 - this.getWidth() / 2,
                       bounds.y + bounds.height / 2 - this.getHeight() / 2);
      this.setResizable(false);
      this.setVisible(true);

  }

  public void initPreferencesDialog() {
    this.panneauDescription = new PanneauDescription(Application.getApplication().getTraduction(PanneauDescription.GENERAL_KEY));
    this.panneauDescription.setVisible(false);

    this.panneauLangue = new PanneauLangue(Application.getApplication().getTraduction(PanneauLangue.LANGUAGE_PANEL_KEY));
    this.panneauLangue.setVisible(false);

    this.panneauGeneration = new PanneauGeneration(Application.getApplication().getTraduction(PanneauGeneration.GENERATION_PANEL_KEY), this);
    this.panneauGeneration.setVisible(false);

    /**************** modif  du 10 03 06 *********************
    this.panneauReferentiel = new PanneauReferentiel(Application.getApplication().getTraduction(PanneauReferentiel.REPOSITORY_PANEL_KEY));
    this.panneauReferentiel.setVisible(false);
    *********************************************************/
    
    this.panneauDiagramme = new PanneauDiagramme(Application.getApplication().getTraduction(PanneauDiagramme.DIAGRAM_PANEL_KEY));
    this.panneauDiagramme.setVisible(false);

    this.panneauDP = new PanneauDP(Application.getApplication().getTraduction(PanneauDP.DP_PANEL_KEY));
    this.panneauDP.setVisible(false);

    this.panneauDPDesc = new PanneauDPDescription(Application.getApplication().getTraduction(PanneauDPDescription.DP_DESCRIPTION_PANEL_KEY));
    this.panneauDPDesc.setVisible(false);

    this.panneauDPGeneration = new PanneauDPGeneration(Application.getApplication().getTraduction(PanneauDPGeneration.DP_GENERATION_PANEL_KEY));
    this.panneauDPGeneration.setVisible(false);

    //modif 2XMI Amandine
    this.panneauGenerationRole = new PanneauGenerationRole(Application.getApplication().getTraduction(panneauGenerationRole.ROLE_GENERATION_PANEL_KEY));
    this.panneauGenerationRole.setVisible(false);
    //fin modif 2XMI Amandine

    this.panneauGenOption = new PanneauGenerationOption(Application.getApplication().getTraduction(PanneauGenerationOption.GENERATION_OPTION_PANEL_KEY));
    this.panneauGenOption.setVisible(false);

    // debut modif 2xmi chaouk
    /**************** modif  du 10 03 06 ***********************
    this.panneauRepertoireExport = new PanneauRepertoireExport(Application.getApplication().getTraduction(PanneauRepertoireExport.EXPORT_DIRECTORY_PANEL_KEY));
    this.panneauRepertoireExport.setVisible(false);
    ***********************************************************/
    // fin modif 2xmi chaouk

    this.panneauProprietesRespComposant = new PanneauProprietesRespComp(Application.getApplication().getTraduction(PanneauProprietesRespComp.RESPONSABLE_KEY), this.comp_proprietes);
    this.panneauProprietesRespComposant.setVisible(false);

    this.panneauProprietesMailComposant = new PanneauProprietesMailComp(Application.getApplication().getTraduction(PanneauProprietesMailComp.MAIL_KEY), this.comp_proprietes);
    this.panneauProprietesMailComposant.setVisible(false);

    this.panneauProprietesVerComposant = new PanneauProprietesVerComp(Application.getApplication().getTraduction(PanneauProprietesVerComp.VERSION_KEY), this.comp_proprietes);
    this.panneauProprietesVerComposant.setVisible(false);

    //this.panneauProprietesPaquetage = new PanneauProprietesPaquetage(Application.getApplication().getTraduction(PanneauProprietesPaquetage.GENERAL_KEY), this.idPres);
    //this.panneauProprietesPaquetage.setVisible(false);

    switch (this.type_courant) {
      case FenetrePreference.TYPE_PREFERENCES:
        this.panneauDescription.setVisible(true);
        this.setInnerPanel(PreferenceTreeItem.DESC_PANEL, PanneauDescription.GENERAL_KEY);
        /**************** modif  du 10 03 06 *********************/
        this.setTitle(Application.getApplication().getTraduction("Langues"));
        //this.setTitle(Application.getApplication().getTraduction("Preferences"));
        /**************** modif  du 10 03 06 *********************/
        break;
      case FenetrePreference.TYPE_DP:
        this.panneauDescription.setVisible(true);
        this.setInnerPanel(PreferenceTreeItem.DESC_PANEL, PanneauDescription.DP_KEY);
        this.setTitle(Application.getApplication().getTraduction("Proprietes_DP"));
        break;
      case FenetrePreference.TYPE_GENERATION:
        this.panneauGeneration.setVisible(true);
        this.setInnerPanel(PreferenceTreeItem.GENERATION_PANEL, PanneauGeneration.GENERATION_PANEL_KEY);
        this.setTitle(Application.getApplication().getTraduction("titre_generer_site"));
        break;
      case FenetrePreference.TYPE_COMPOSANT:
        this.panneauProprietesRespComposant.setVisible(true);
        this.setInnerPanel(PreferenceTreeItem.DESC_PANEL, panneauProprietesRespComposant.RESPONSABLE_KEY);
        this.setTitle(Application.getApplication().getTraduction("Proprietes_COMPOSANT"));
        break;
     /* case FenetrePreference.TYPE_PAQ:
        this.panneauProprietesPaquetage.setVisible(true);
        this.setInnerPanel(PreferenceTreeItem.DESC_PANEL, panneauProprietesPaquetage.GENERAL_KEY);
        this.setTitle(Application.getApplication().getTraduction("Proprietes_PAQ"));
        break;*/
    }
  }

  public void setInnerPanel(int panel, String key) {
    switch (panel){
      case PreferenceTreeItem.GENERATION_OPTION_PANEL_KEY:
       panneau.add(this.panneauGenOption.openPanel(key), BorderLayout.CENTER);
        break;
      case PreferenceTreeItem.DP_GENERATION_PANEL:
        panneau.add(this.panneauDPGeneration.openPanel(key), BorderLayout.CENTER);
        break;
      case PreferenceTreeItem.DP_DESCRIPTION_PANEL:
        panneau.add(this.panneauDPDesc.openPanel(key), BorderLayout.CENTER);
        break;
      case PreferenceTreeItem.DP_PANEL:
        panneau.add(this.panneauDP.openPanel(key), BorderLayout.CENTER);
        break;
      case PreferenceTreeItem.DIAGRAM_PANEL:
        panneau.add(this.panneauDiagramme.openPanel(key), BorderLayout.CENTER);
        break;
        /**************** modif  du 10 03 06 *********************
      case PreferenceTreeItem.REPOSITORY_PANEL:
        panneau.add(this.panneauReferentiel.openPanel(key), BorderLayout.CENTER);
        break;
        **********************************************************/
      case PreferenceTreeItem.GENERATION_PANEL:
        panneau.add(this.panneauGeneration.openPanel(key), BorderLayout.CENTER);
        break;
      case PreferenceTreeItem.DESC_PANEL:
        panneau.add(this.panneauDescription.openPanel(key), BorderLayout.CENTER);
        break;
        
      case PreferenceTreeItem.LANGUAGE_PANEL:
        panneau.add(this.panneauLangue.openPanel(key), BorderLayout.CENTER);
        break;
        /**************** modif  du 10 03 06 *********************
      case PreferenceTreeItem.EXPORT_DIRECTORY_PANEL:
        panneau.add(this.panneauRepertoireExport.openPanel(key), BorderLayout.CENTER);
        break;
        **********************************************************/
      case PreferenceTreeItem.ROLE_GENERATION_PANEL:
        panneau.add(this.panneauGenerationRole.openPanel(key), BorderLayout.CENTER);
        break;
      case PreferenceTreeItem.COMPOSANT_DESCRIPTION_RESP_PANEL:
        panneau.add(this.panneauProprietesRespComposant.openPanel(key), BorderLayout.CENTER);
        break;
      case PreferenceTreeItem.COMPOSANT_DESCRIPTION_MAIL_PANEL:
        panneau.add(this.panneauProprietesMailComposant.openPanel(key), BorderLayout.CENTER);
        break;
      case PreferenceTreeItem.COMPOSANT_DESCRIPTION_VERSION_PANEL:
              panneau.add(this.panneauProprietesVerComposant.openPanel(key), BorderLayout.CENTER);
              break;
      /*case PreferenceTreeItem.PAQ_DESCRIPTION_PANEL:
        panneau.add(this.panneauProprietesPaquetage.openPanel(key), BorderLayout.CENTER);
        break;*/

    }
  }

  public PanneauGenerationOption getGenOptionPanel() {
    return this.panneauGenOption;
  }

  public PanneauDPGeneration getDPGenPanel() {
    return this.panneauDPGeneration;
  }

  public PanneauDPDescription getDPDescPanel() {
    return this.panneauDPDesc;
  }

  public PanneauDP getDPPanel() {
    return this.panneauDP;
  }

  public PanneauDescription getDescriptionPanel() {
    return this.panneauDescription;
  }

  public PanneauLangue getLanguagePanel() {
    return this.panneauLangue;
  }

  public PanneauGeneration getGenerationPanel() {
    return this.panneauGeneration;
  }
  
  /**************** modif  du 10 03 06 *********************
  public PanneauReferentiel getReferentielPanel() {
    return this.panneauReferentiel;
  }
  *********************************************************/

  public PanneauDiagramme getDiagrammePanel() {
    return this.panneauDiagramme;
  }
  
  /**************** modif  du 10 03 06 *********************
  public PanneauRepertoireExport getRepertoireExportPanel () {
    return this.panneauRepertoireExport;
  }
  *********************************************************/
  
  //modif 2XMI Amandine
  public PanneauGenerationRole getGenerationRole () {
    return this.panneauGenerationRole;
  }
  //fin modif 2XMI Amandine

  public PanneauProprietesRespComp getProprietesRespComposant () {
    return this.panneauProprietesRespComposant;
  }

  public PanneauProprietesMailComp getProprietesMailComposant () {
    return this.panneauProprietesMailComposant;
  }

  public PanneauProprietesVerComp getProprietesVerComposant () {
      return this.panneauProprietesVerComposant;
    }


  public PanneauProprietesPaquetage getProprietesPaquetage () {
    return this.panneauProprietesPaquetage;
  }


  public int getType() {
    return this.type_courant;
  }

  public void save() {
    switch (this.type_courant) {
      case FenetrePreference.TYPE_PREFERENCES:

        //sauver tous les param\uFFFDtres
    	/**************** modif  du 10 03 06 *********************
        this.panneauRepertoireExport.save();
        this.panneauReferentiel.save();
        *********************************************************/
        this.panneauLangue.save();
        if (this.panneauLangue.hasLanguageChanged()) {
          Application.getApplication().getFenetrePrincipale().rafraichirLangue();
          this.dispose();
        }
        break;
      case FenetrePreference.TYPE_DP:
        this.panneauDP.save();
        this.panneauDPDesc.save();
        this.panneauDPGeneration.save();
        this.panneauGenOption.save();
        this.panneauGenerationRole.save();
        break;
      case FenetrePreference.TYPE_GENERATION:
        this.panneauDP.save();
        this.panneauDPDesc.save();
        this.panneauDPGeneration.save();
        this.panneauGenOption.save();
        this.panneauGenerationRole.save();
        break;
    }

  }

  private class ManagerButton
      implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      //r\uFFFDcup\uFFFDrer l'objet source de l'\uFFFDv\uFFFDnement re\uFFFDu
      Object source = e.getSource();
      // selon cet objet, r\uFFFDagir en cons\uFFFDquence
      if (source == FenetrePreference.this.ok) {
        FenetrePreference.this.save();
        FenetrePreference.this.dispose();
      }
      else if (source == FenetrePreference.this.cancel) {
        // enlever la bo\uFFFDte sans rien changer
        FenetrePreference.this.dispose();
      }
      else if (source == FenetrePreference.this.apply) {
        // sauver tous les param\uFFFDtres
        FenetrePreference.this.save();
      }
    }
  }
}
