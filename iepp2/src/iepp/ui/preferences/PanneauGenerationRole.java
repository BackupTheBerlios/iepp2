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
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import util.IconManager;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;


public class PanneauGenerationRole extends PanneauOption
{
  public static final DataFlavor NODE_FLAVOR=new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Node");

  //definition de processus actuellement ouverte
  private DefinitionProcessus defProc;

  //arbre affiche
  private DNDTree tree;

  public static final String iconeComposant="TreeComponent.gif";
  public static final String iconeRole="srTreeRole.gif";
  public static final String iconeRoleCopie="srcTreeRole.gif";
  public static final String iconeSuperRole="srprocess_tree_node_roles.gif";
  public static final String iconeDossierOuvert="srdosouvert.gif";
  public static final String iconeDossierFerme="srdosferme.gif";
  public static final String ROLE_GENERATION_PANEL_KEY="RoleTitle";

  //permet de se souvenir quel est le noeud correspondant à un rôle
  private HashMap hashmapObjetNoeud;

  //copie de la liste listeAssociationSRole de la definition de processus
  private HashMap listeAssociationSRoleCopie = null;

  public PanneauGenerationRole(String name)
  {
    this.setLayout(new BorderLayout());
    hashmapObjetNoeud = new HashMap();
    mPanel=new JPanel();
    GridBagLayout gridbag=new GridBagLayout();
    mPanel.setLayout(gridbag);
    mPanel.setSize(new Dimension(300, 400));
    GridBagConstraints c=new GridBagConstraints();

    // Title
    c.weightx=1.0;
    c.weighty=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER;
    this.mTitleLabel=new JLabel(name);
    TitledBorder titleBor=BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
    titleBor.setTitleJustification(TitledBorder.CENTER);
    mTitleLabel.setBorder(titleBor);
    gridbag.setConstraints(mTitleLabel, c);
    mPanel.add(mTitleLabel);

    // linefeed
    c.weighty=0;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);
    if(Application.getApplication().getReferentiel()!=null)
    {
      this.initPanelArbre(c, gridbag);
    }

    c.fill=GridBagConstraints.VERTICAL;
    c.weighty=2.0;
    // linefeed
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);

    this.add(new JLabel("    "), BorderLayout.WEST);
    this.add(mPanel, BorderLayout.CENTER);
  }

  public PanneauOption openPanel(String key)
  {
    this.setName(Application.getApplication().getTraduction(key));
    return this;
  }

  private void initPanelArbre(GridBagConstraints c, GridBagLayout gridbag)
  {
    Vector listeRole=new Vector();
    // recuperer la liste des composants du processus
    DefaultMutableTreeNode ht=new DefaultMutableTreeNode(Application.getApplication().getTraduction("RoleRacineArbre"));

    Vector listeComposant=Application.getApplication().getProjet().getDefProc().getListeComp();
    for(int i=0; i<listeComposant.size(); i++)
    {
      IdObjetModele iocomp=(IdObjetModele)listeComposant.elementAt(i);
      ComposantNode composant1=new ComposantNode(iocomp, iocomp.toString());
      /** Ajout du JTree au noeud racine*/
      ht.add(composant1);
      hashmapObjetNoeud.put(iocomp, composant1);

      Vector role=iocomp.getRole();
      for(int j=0; j<role.size(); j++){
        IdObjetModele iorole = (IdObjetModele) role.elementAt(j);
        RoleNode role1=new RoleNode(iorole, iorole.toString());
        composant1.add(role1);
        hashmapObjetNoeud.put(iorole, role1);
      }
    }


    c.weighty=0;
    c.weightx=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER;

    //reconstitution des liens sauvegardes lors de precedentes sessions
    defProc=Application.getApplication().getProjet().getDefProc();

    //affichage liste de la DefinitionProcessus : System.out.println(defProc.getListeAssociationSRole());
    listeAssociationSRoleCopie=(HashMap)defProc.getListeAssociationSRole().clone();
    this.tree=new DNDTree(ht, listeAssociationSRoleCopie);
    Set entrees=listeAssociationSRoleCopie.entrySet();
    Iterator iterateur=entrees.iterator();
    while(iterateur.hasNext())
    {
      Map.Entry entree=(Map.Entry)iterateur.next();
      RoleNode source=(RoleNode)hashmapObjetNoeud.get(entree.getKey());
      RoleNode destination=(RoleNode)hashmapObjetNoeud.get(entree.getValue());
      if(source!=null&&destination!=null)
      {
        tree.dtth.executeDrop(tree, source, destination, DnDConstants.ACTION_COPY);
      }
    }

    JScrollPane spane=new JScrollPane(tree);
    mPanel.add(spane);
    gridbag.setConstraints(spane, c);

  }

  public void save()
  {
    if(this.defProc!=null)
    {
      this.defProc.setListeAssociationSRole(this.listeAssociationSRoleCopie);
    }
  }

  class ComposantNode extends DefaultMutableTreeNode
  {
    //IdObjetModele associe a ce noeud
    IdObjetModele io;
    String name;
    public ComposantNode(IdObjetModele _ido, String _name)
    {
      super(_name);
      this.io=_ido;
      this.name=_name;
    }
  }

  class RoleNode extends DefaultMutableTreeNode
  {
    //IdObjetModele associe a ce noeud
    private IdObjetModele io;
    String name;
    boolean isSR;
    boolean hasSR;

    //lien vers le noeud du role original, dans le composant original
    RoleNode link;

    public RoleNode(IdObjetModele _ido, String _name)
    {
      super(_name);
      this.io=_ido;
      this.name=_name;
      this.isSR=false;
      this.hasSR=false;
    }

    public RoleNode(RoleNode _roleNode)
    {
      super(_roleNode.name+" - "+_roleNode.getParent().toString());
      this.io=_roleNode.io;
      this.name=_roleNode.name;
      this.hasSR=true;
      this.link=_roleNode;
    }

    public IdObjetModele getIdObjetModele()
    {
      return io;
    }

    public void deleteChild(DNDTree dndTree)
    {
      DefaultTreeModel treeModel=(DefaultTreeModel)dndTree.getModel();
      for(Enumeration e=this.children(); e.hasMoreElements(); )
      {
        DefaultMutableTreeNode o=(DefaultMutableTreeNode)e.nextElement();
        ((RoleNode)o).link.hasSR=false;
        dndTree.listeAssociationSRoleCopie.remove(((RoleNode)o).getIdObjetModele());
      }
      this.removeAllChildren();
      treeModel.nodeStructureChanged((TreeNode)this);

      this.isSR=false;
    }

    public void delete(DNDTree dndTree)
    {
      //roleNode : role copie
      //this.isSR=false;
      this.hasSR=false;
      if(this.link!=null)
      {
        //roleNode.link : vrai Role
        //this.link.isSR=false;
        this.link.hasSR=false;
        this.link=null;
      }
      DefaultTreeModel treeModel=(DefaultTreeModel)dndTree.getModel();
      dndTree.listeAssociationSRoleCopie.remove(this.getIdObjetModele());
      RoleNode pere = (RoleNode)this.getParent();
      treeModel.removeNodeFromParent(this);
      if (pere.getChildCount() == 0)
        pere.isSR = false;
    }

  }

  class DNDTree extends JTree
  {

    Insets autoscrollInsets=new Insets(20, 20, 20, 20); // insets

    DNDTree dNDTree;
    public DefaultTreeTransferHandler dtth;
    public HashMap listeAssociationSRoleCopie;

    public DNDTree(DefaultMutableTreeNode root, HashMap hm)
    {
      setAutoscrolls(true);
      DefaultTreeModel treemodel=new DefaultTreeModel(root);
      setModel(treemodel);
      setRootVisible(true);
      setShowsRootHandles(false); //to show the root icon
      getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //set single selection for the Tree
      setEditable(false);
      this.addMouseListener(new RoleMouseAdapter());
      dNDTree=this;
      this.setCellRenderer(new MyRenderer());
      dtth = new DefaultTreeTransferHandler(this, DnDConstants.ACTION_MOVE);
      listeAssociationSRoleCopie = hm;
    }

    private class MyRenderer extends DefaultTreeCellRenderer
    {
      private Icon composantImage;
      private Icon sroleImage;
      private Icon roleImage;
      private Icon croleImage;
      private Icon defaultOpenImage;
      private Icon defaultCloseImage;

      public MyRenderer()
      {
        composantImage=IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons")+PanneauGenerationRole.iconeComposant);
        sroleImage=IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons")+PanneauGenerationRole.iconeSuperRole);
        roleImage=IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons")+PanneauGenerationRole.iconeRole);
        croleImage=IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons")+PanneauGenerationRole.iconeRoleCopie);
        defaultOpenImage=IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons")+PanneauGenerationRole.iconeDossierOuvert);
        defaultCloseImage=IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons")+PanneauGenerationRole.iconeDossierFerme);
      }

      public Component getTreeCellRendererComponent(
          JTree tree,
          Object value,
          boolean sel,
          boolean expanded,
          boolean leaf,
          int row,
          boolean hasFocus)
      {

        super.getTreeCellRendererComponent(
            tree, value, sel,
            expanded, leaf, row,
            hasFocus);
        if(value instanceof ComposantNode)
          setIcon(composantImage);
        else if(value instanceof RoleNode)
        {
          if(((RoleNode)value).isSR)
            setIcon(sroleImage);
          else if(((RoleNode)value).hasSR==false)
            setIcon(roleImage);
          else
            if(((RoleNode)value).link==null)
              setIcon(croleImage);
            else
              setIcon(roleImage);
        } else
        {
          if(expanded)
            setIcon(defaultOpenImage);
          else
            setIcon(defaultCloseImage);
        }

        return this;
      }
    }

    public void autoscroll(Point cursorLocation)
    {
      Insets insets=getAutoscrollInsets();
      Rectangle outer=getVisibleRect();
      Rectangle inner=new Rectangle(outer.x+insets.left, outer.y+insets.top, outer.width-(insets.left+insets.right), outer.height-(insets.top+insets.bottom));
      if(!inner.contains(cursorLocation))
      {
        Rectangle scrollRect=new Rectangle(cursorLocation.x-insets.left, cursorLocation.y-insets.top, insets.left+insets.right, insets.top+insets.bottom);
        scrollRectToVisible(scrollRect);
      }
    }

    public Insets getAutoscrollInsets()
    {
      return(autoscrollInsets);
    }

    public DefaultMutableTreeNode makeDeepCopy(DefaultMutableTreeNode node)
    { //static
      DefaultMutableTreeNode copy;
      if(node instanceof RoleNode)
      {
        copy=new RoleNode((RoleNode)node);
      } else
        copy=new DefaultMutableTreeNode(node.getUserObject());
      return(copy);
    }

    private class RoleMouseAdapter extends MouseAdapter
    {
      /**
       * L'utilisateur a cliqué sur l'arbre
       */
      public void mousePressed(MouseEvent e)
      {
        // on récupère la ligne sur laquelle on a cliqué
        int selRow=getRowForLocation(e.getX(), e.getY());
        // récupérer le chemin de l'arbre associé, le path
        TreePath selPath=getPathForLocation(e.getX(), e.getY());

        // the modifiers test is needed in order to make it work on OSes that don't correctly set the isPopupTrigger flag (swing sux0r)
        if(selRow!=-1&&(e.isPopupTrigger()||(e.getModifiers()&MouseEvent.BUTTON3_MASK)!=0))
        {
          // si on clique droit, on affiche le popupmenu associé à l'élément sélectionné
          JPopupMenu popup=getMenuAssocie(selPath.getLastPathComponent());
          // si aucun menu existe, ne rien faire
          if(popup!=null)
          {
            e.consume();
            setSelectionPath(selPath);
            // afficher le menu contextuel
            popup.show(dNDTree, e.getX(), e.getY());
          }
        }
        // on a cliqué sur l'arbre mais sur aucun élément, cliqué deux fois
        else if(selRow!=-1&&e.getClickCount()==2)
        {
          // ne rien faire
          e.consume();
        }
      }
    }

    class PopMenuRole extends JPopupMenu implements ActionListener
    {
      JMenuItem itemSupprimer;
      RoleNode roleNode;

      public PopMenuRole(RoleNode value)
      {
        this.roleNode=value;
        this.itemSupprimer=new JMenuItem();
        this.itemSupprimer.setText(Application.getApplication().getTraduction("RoleSuppression"));
        this.itemSupprimer.addActionListener(this);
        this.add(itemSupprimer);
      }

      public void actionPerformed(ActionEvent event)
      {
        if(event.getSource()==this.itemSupprimer)
        {
          if(roleNode.isSR){

            //cas de suppression d'un super-role
            roleNode.deleteChild(dNDTree);
          }else{

            //cas de suppression d'un role d'un super-role
            roleNode.delete(dNDTree);
          }
        }
      }
    }

    public JPopupMenu getMenuAssocie(Object value)
    {
      if((value instanceof ComposantNode)||(((DefaultMutableTreeNode)value).isRoot()))
        return null;
      if(value instanceof RoleNode)
        if(((RoleNode)value).isSR)
          return(new PopMenuRole((RoleNode)value));
      if((((RoleNode)value).hasSR))
        if(((RoleNode)value).link!=null)
          return(new PopMenuRole((RoleNode)value));
      return null;

    }
  }

  class TransferableNode implements Transferable
  {
    private DefaultMutableTreeNode node;
    private DataFlavor[] flavors=
        {NODE_FLAVOR};

    public TransferableNode(DefaultMutableTreeNode nd)
    {
      node=nd;
    }

    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
    {
      if(flavor==NODE_FLAVOR)
      {
        return node;
      } else
      {
        throw new UnsupportedFlavorException(flavor);
      }
    }

    public DataFlavor[] getTransferDataFlavors()
    {
      return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
      return Arrays.asList(flavors).contains(flavor);
    }
  }

  class DefaultTreeTransferHandler extends AbstractTreeTransferHandler
  {

    public DefaultTreeTransferHandler(DNDTree tree, int action)
    {
      super(tree, action, true);
    }

    public boolean canPerformAction(DNDTree target, DefaultMutableTreeNode draggedNode, int action, Point location)
    {
      TreePath pathTarget=target.getPathForLocation(location.x, location.y);
      if(pathTarget==null)
      {
        target.setSelectionPath(null);
        return(false);
      }

      if(draggedNode instanceof RoleNode)
        if((((RoleNode)draggedNode).isSR)||((RoleNode)draggedNode).hasSR)
          return false;
      if(draggedNode instanceof ComposantNode)
        return false;
      target.setSelectionPath(pathTarget);
      if(action==DnDConstants.ACTION_MOVE)
      {
        DefaultMutableTreeNode targetNode=(DefaultMutableTreeNode)pathTarget.getLastPathComponent();
        if(targetNode instanceof RoleNode)
        {
          if(((RoleNode)targetNode).hasSR)
            return false;
        }
        if(targetNode instanceof ComposantNode)
          return false;

        if((draggedNode==targetNode)||(draggedNode.getParent()==targetNode.getParent()))
          return false;
        if(targetNode instanceof RoleNode)
        {
          return true;
        } else
          return false;
      }
      //if (draggedNode.isNodeAncestor(targetNode)
      else
      {
        return(false);
      }
    }

    public boolean executeDrop(DNDTree target, DefaultMutableTreeNode draggedNode, DefaultMutableTreeNode newParentNode, int action)
    {
      if((action==DnDConstants.ACTION_MOVE) || (action==DnDConstants.ACTION_COPY))
      {
        if(newParentNode instanceof RoleNode)
        {
          ((RoleNode)newParentNode).isSR=true;
        }
        if(draggedNode instanceof RoleNode)
        {
          ((RoleNode)draggedNode).hasSR=true;
        }

        DefaultMutableTreeNode newNode=target.makeDeepCopy(draggedNode);
        ((DefaultTreeModel)target.getModel()).insertNodeInto(newNode, newParentNode, newParentNode.getChildCount());
        TreePath treePath=new TreePath(newNode.getPath());
        target.scrollPathToVisible(treePath);
        target.setSelectionPath(treePath);

        if(action==DnDConstants.ACTION_MOVE){
          //lorsque le deplacement est fait à partir de l'arbre, il faut sauvegarder ce deplacement
          target.listeAssociationSRoleCopie.put(((RoleNode)draggedNode).getIdObjetModele(), ((RoleNode)newParentNode).getIdObjetModele());
        }
        return(true);
      }
      return(false);
    }
  }

  abstract class AbstractTreeTransferHandler implements DragGestureListener, DragSourceListener, DropTargetListener
  {

    private DNDTree tree;
    private DragSource dragSource; // dragsource
    private DropTarget dropTarget; //droptarget
    private DefaultMutableTreeNode draggedNode; //static
    private DefaultMutableTreeNode draggedNodeParent;
    private BufferedImage image=null; //buff image static
    private Rectangle rect2D=new Rectangle();
    private boolean drawImage;

    protected AbstractTreeTransferHandler(DNDTree tree, int action, boolean drawIcon)
    {
      this.tree=tree;
      drawImage=drawIcon;
      dragSource=new DragSource();
      dragSource.createDefaultDragGestureRecognizer(tree, action, this);
      dropTarget=new DropTarget(tree, action, this);
    }

    /* Methods for DragSourceListener */
    public void dragDropEnd(DragSourceDropEvent dsde)
    {
      if(dsde.getDropSuccess()&&dsde.getDropAction()==DnDConstants.ACTION_MOVE&&draggedNodeParent!=null)
      {
        ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(draggedNodeParent);
      }
    }

    public final void dragEnter(DragSourceDragEvent dsde)
    {
      int action=dsde.getDropAction();
      if(action==DnDConstants.ACTION_MOVE)
       {
         dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
       } else
       {
         dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
       }
    }

    public final void dragOver(DragSourceDragEvent dsde)
    {
      int action=dsde.getDropAction();
      if(action==DnDConstants.ACTION_MOVE)
        {
          dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
        } else
        {
          dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
        }
    }

    public final void dropActionChanged(DragSourceDragEvent dsde)
    {
      int action=dsde.getDropAction();
      if(action==DnDConstants.ACTION_MOVE)
        {
          dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
        } else
        {
          dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
        }
    }

    public final void dragExit(DragSourceEvent dse)
    {
      dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    }

    /* Methods for DragGestureListener */
    public final void dragGestureRecognized(DragGestureEvent dge)
    {
      TreePath path=tree.getSelectionPath();
      if(path!=null)
      {
        draggedNode=(DefaultMutableTreeNode)path.getLastPathComponent();
        draggedNodeParent=(DefaultMutableTreeNode)draggedNode.getParent();
        if(drawImage)
        {
          Rectangle pathBounds=tree.getPathBounds(path); //getpathbounds of selectionpath
          JComponent lbl=(JComponent)tree.getCellRenderer().getTreeCellRendererComponent(tree, draggedNode, false, tree.isExpanded(path), ((DefaultTreeModel)tree.getModel()).isLeaf(path.getLastPathComponent()), 0, false); //returning the label
          lbl.setBounds(pathBounds); //setting bounds to lbl
          image=new BufferedImage(lbl.getWidth(), lbl.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE); //buffered image reference passing the label's ht and width
          Graphics2D graphics=image.createGraphics(); //creating the graphics for buffered image
          graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); //Sets the Composite for the Graphics2D context
          lbl.setOpaque(false);
          lbl.paint(graphics); //painting the graphics to label
          graphics.dispose();
        }
        dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop, image, new Point(0, 0), new TransferableNode(draggedNode), this);
      }
    }

    /* Methods for DropTargetListener */
    public final void dragEnter(DropTargetDragEvent dtde)
    {
      Point pt=dtde.getLocation();
      int action=dtde.getDropAction();
      if(drawImage)
      {
        paintImage(pt);
      }
      if(canPerformAction(tree, draggedNode, action, pt))
      {
        dtde.acceptDrag(action);
      } else
      {
        dtde.rejectDrag();
      }
    }

    public final void dragExit(DropTargetEvent dte)
    {
      if(drawImage)
      {
        clearImage();
      }
    }

    public final void dragOver(DropTargetDragEvent dtde)
    {
      Point pt=dtde.getLocation();
      int action=dtde.getDropAction();
      tree.autoscroll(pt);
      if(drawImage)
      {
        paintImage(pt);
      }
      if(canPerformAction(tree, draggedNode, action, pt))
      {
        dtde.acceptDrag(action);
      } else
      {
        dtde.rejectDrag();
      }
    }

    public final void dropActionChanged(DropTargetDragEvent dtde)
    {
      Point pt=dtde.getLocation();
      int action=dtde.getDropAction();
      if(drawImage)
      {
        paintImage(pt);
      }
      if(canPerformAction(tree, draggedNode, action, pt))
      {
        dtde.acceptDrag(action);
      } else
      {
        dtde.rejectDrag();
      }
    }

    public final void drop(DropTargetDropEvent dtde)
    {
      try
      {
        if(drawImage)
        {
          clearImage();
        }
        int action=dtde.getDropAction();
        Transferable transferable=dtde.getTransferable();
        Point pt=dtde.getLocation();
        if(transferable.isDataFlavorSupported(PanneauGenerationRole.NODE_FLAVOR)&&canPerformAction(tree, draggedNode, action, pt))
        {
          TreePath pathTarget=tree.getPathForLocation(pt.x, pt.y);
          DefaultMutableTreeNode node=(DefaultMutableTreeNode)transferable.getTransferData(PanneauGenerationRole.NODE_FLAVOR);
          DefaultMutableTreeNode newParentNode=(DefaultMutableTreeNode)pathTarget.getLastPathComponent();

          if(executeDrop(tree, node, newParentNode, action))
          {
            dtde.acceptDrop(action);
            dtde.dropComplete(true);
            return;
          }
        }
        dtde.rejectDrop();
        dtde.dropComplete(false);
      } catch(Exception e)
      {
        dtde.rejectDrop();
        dtde.dropComplete(false);
      }
    }

    private final void paintImage(Point pt)
    {
      tree.paintImmediately(rect2D.getBounds());
      rect2D.setRect((int)pt.getX(), (int)pt.getY(), image.getWidth(), image.getHeight());
      tree.getGraphics().drawImage(image, (int)pt.getX(), (int)pt.getY(), tree);
    }

    private final void clearImage()
    {
      tree.paintImmediately(rect2D.getBounds());
    }

    public abstract boolean canPerformAction(DNDTree target, DefaultMutableTreeNode draggedNode, int action, Point location);

    public abstract boolean executeDrop(DNDTree tree, DefaultMutableTreeNode draggedNode, DefaultMutableTreeNode newParentNode, int action);
  }

}
