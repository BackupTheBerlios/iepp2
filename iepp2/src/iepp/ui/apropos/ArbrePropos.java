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

package iepp.ui.apropos;

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

public class ArbrePropos
    extends JTree {
  /**
   * Lien vers la boîte de dialogue contenant l'arbre
   */
  private FenetrePropos mPrefDiag;

  public ArbrePropos(FenetrePropos prefDiag) {
    this.mPrefDiag = prefDiag;

    // gestionnaire d'évènement sur l'arbre
    ManagerTree gp = new ManagerTree();

    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new ProposTreeItem(
        Application.getApplication().getTraduction("Propos"),
        Application.getApplication().getTraduction("Propos"), 0));

    DefaultMutableTreeNode auteursItem = new DefaultMutableTreeNode(new ProposTreeItem
        (PanneauDescriptionPropos.AUTEURS_KEY, Application.getApplication().getTraduction(PanneauDescriptionPropos.AUTEURS_KEY), ProposTreeItem.AUTEURS_PANEL));

    DefaultMutableTreeNode librairiesItem = new DefaultMutableTreeNode(new ProposTreeItem
        (PanneauDescriptionPropos.LIBRAIRIES_KEY, Application.getApplication().getTraduction(PanneauDescriptionPropos.LIBRAIRIES_KEY), ProposTreeItem.LIBRAIRIES_PANEL));

    DefaultMutableTreeNode licencesItem = new DefaultMutableTreeNode(new ProposTreeItem
           (PanneauDescriptionPropos.LICENCES_KEY, Application.getApplication().getTraduction(PanneauDescriptionPropos.LICENCES_KEY), ProposTreeItem.LICENCES_PANEL));

    root.add(auteursItem);
    root.add(librairiesItem);
    root.add(licencesItem);

    this.setRootVisible(false);
    this.addTreeSelectionListener(gp);
    this.setModel(new DefaultTreeModel(root));
    this.setPreferredSize(new Dimension(170, 50));
    this.expandAll(this, true);
  }

  private class ManagerTree
      implements TreeSelectionListener {
    public void valueChanged(TreeSelectionEvent e) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
      if (node != null) {
        Object o = e.getSource();
        int panel = ( (ProposTreeItem) node.getUserObject()).getPanel();
        String key = ( (ProposTreeItem) node.getUserObject()).getKey();

        if (panel == ProposTreeItem.AUTEURS_PANEL) {
          mPrefDiag.getAuteursPanel().setVisible(true);
          mPrefDiag.getLibrairiesPanel().setVisible(false);
          mPrefDiag.getLicencesPanel().setVisible(false);
        }
        else
        {
          if (panel == ProposTreeItem.LIBRAIRIES_PANEL) {
          mPrefDiag.getAuteursPanel().setVisible(false);
          mPrefDiag.getLibrairiesPanel().setVisible(true);
          mPrefDiag.getLicencesPanel().setVisible(false);

        }
        else
          {
            if (panel == ProposTreeItem.LICENCES_PANEL) {
          mPrefDiag.getAuteursPanel().setVisible(false);
          mPrefDiag.getLibrairiesPanel().setVisible(false);
          mPrefDiag.getLicencesPanel().setVisible(true);
            }
          }
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
