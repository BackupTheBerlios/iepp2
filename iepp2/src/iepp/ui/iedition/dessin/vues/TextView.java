package iepp.ui.iedition.dessin.vues;

import iepp.Application;
import iepp.ui.iedition.dessin.rendu.TextCell;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D.Double;
import java.util.EventObject;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jgraph.JGraph;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.VertexView;

public class TextView extends VertexView {

	//Constructor for Superclass
	public TextView(Object cell, JGraph jg, CellMapper cm) {
		super(cell, jg, cm);
	}
	
	
	/**
	 * Returns the intersection of the bounding rectangle and the
	 * straight line between the source and the specified point p.
	 * The specified point is expected not to intersect the bounds.
	 */
	public Point getPerimeterPoint(Point source, Point p) 
	{
		Line2D.Double line1 = new Line2D.Double(p,getCenterPoint());
		Line2D.Double line2;
		Rectangle r = getBounds();
			
		
		/*
		 * 0       1
		 * ________
		 * |        \ 2
		 * |        |
		 * |        |
		 * |        |
		 * |        |
		 * |        |
		 * |________|
		 * 4         3
		 * 
		 */
		int x[]=new int[5];
		int y[]=new int[5];	
		
		x[0]= r.x;
		y[0]= r.y;
		
		x[1]= r.x + (r.width) - 10;
		y[1]= r.y;
		
		x[2]= r.x + (r.width);
		y[2]= r.y + 10;
		
		x[3]= r.x + (r.width);
		y[3]= r.y + (r.height);
		
		x[4]= r.x;
		y[4]= r.y + (r.height);
		
		
		line2 = new Line2D.Double((double) x[0], (double) y[0], (double) x[1], (double) y[1]);
		
		if (line1.intersectsLine(line2) == true)
		{
			return calculeDuPoint(line1, line2);
		}
		
		line2 = new Line2D.Double((double) x[1], (double) y[1], (double) x[2], (double) y[2]);
		if (line1.intersectsLine(line2) == true)
		{
			return calculeDuPoint(line1, line2);
		}
		
		line2 = new Line2D.Double((double) x[2], (double) y[2], (double) x[3], (double) y[3]);
		if (line1.intersectsLine(line2) == true)
		{
			return calculeDuPoint(line1, line2);
		}
		
		line2 = new Line2D.Double((double) x[3], (double) y[3], (double) x[4], (double) y[4]);
		if (line1.intersectsLine(line2) == true)
		{
			return calculeDuPoint(line1, line2);
		}
		
		line2 = new Line2D.Double((double) x[4], (double) y[4], (double) x[0], (double) y[0]);
		if (line1.intersectsLine(line2) == true)
		{
			return calculeDuPoint(line1, line2);
		}
		
		return getCenterPoint();
	}

	public Point calculeDuPoint(Double line1, Double line2){
		
		double a1 = line1.getY2() - line1.getY1() ; 
		double b1 = line1.getX1() - line1.getX2() ; 
		double c1 = line1.getY1() * line1.getX2() - line1.getX1() * line1.getY2() ;
		double a2 = line2.getY2() - line2.getY1() ; 
		double b2 = line2.getX1() - line2.getX2() ; 
		double c2 = line2.getY1() * line2.getX2() - line2.getX1() * line2.getY2() ;
		double det = a1 * b2 - a2 * b1 ;
		
		double xout = (c2 * b1 - c1 * b2) / det ;
		double yout = (a2 * c1 - a1 * c2) / det ;
		
		return new Point((int)xout, (int)yout);
		
	}
	
	
	protected static transient TextEditor editor = new TextEditor();
	protected static transient TextRenderer renderer = new TextRenderer();
    
    public GraphCellEditor getEditor() {
        return editor;
    }
    
    public static class TextEditor extends DefaultGraphCellEditor {
        public class RealCellEditor extends AbstractCellEditor implements GraphCellEditor {
            JTextArea editorComponent = new JTextArea();
            public RealCellEditor() {
                editorComponent.setBorder(UIManager.getBorder("Tree.editorBorder"));
                editorComponent.setLineWrap(true);
                editorComponent.setWrapStyleWord(true);

                //substitute a JTextArea's VK_ENTER action with our own that will stop an edit.
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,KeyEvent.SHIFT_DOWN_MASK), "shiftEnter");
				editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,KeyEvent.CTRL_DOWN_MASK), "metaEnter");
                editorComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
                
                editorComponent.getActionMap().put("enter", new AbstractAction(){
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing();
                    }
                });
                AbstractAction newLineAction = new AbstractAction() {

					public void actionPerformed(ActionEvent e) {
						Document doc = editorComponent.getDocument();
						try {
							doc.insertString(editorComponent
									.getCaretPosition(), "\n", null);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
                };
				editorComponent.getActionMap().put("shiftEnter",newLineAction);
				editorComponent.getActionMap().put("metaEnter",newLineAction);
            }

            
            
            public Component getGraphCellEditorComponent(
                    JGraph graph,
                    Object value,
                    boolean isSelected) {
                editorComponent.setText(value.toString());
                editorComponent.selectAll();
                return editorComponent;
            }

            public Object getCellEditorValue() {
                return editorComponent.getText();
            }

            public boolean stopCellEditing() {
                //set the size of a vertex to that of an editor.
            	CellView view = graph.getGraphLayoutCache().getMapping(graph.getEditingCell(), false);
                Map map = view.getAllAttributes();
                Rectangle2D cellBounds = GraphConstants.getBounds(map);
                Rectangle editingBounds = editorComponent.getBounds();
                GraphConstants.setBounds(map, new Rectangle(((int) cellBounds.getX()), ((int) cellBounds.getY()), editingBounds.width, editingBounds.height));

                ((TextCell)graph.getEditingCell()).setMessage(editorComponent.getText());
                Application.getApplication().getProjet().setModified(true);
                
                return super.stopCellEditing();
            }
            
            public boolean shouldSelectCell(EventObject event) {
				editorComponent.requestFocus();
				return super.shouldSelectCell(event);
			}
        }

        public TextEditor() {
            super();
        }
        /**
         * Overriding this in order to set the size of an editor to that of an edited view.
         */
        public Component getGraphCellEditorComponent(
                JGraph graph,
                Object cell,
                boolean isSelected) {

            Component component = super.getGraphCellEditorComponent(graph, cell, isSelected);

            //set the size of an editor to that of a view
			CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
			Rectangle2D tmp = view.getBounds();
			editingComponent.setBounds((int) tmp.getX(), (int) tmp.getY(),
					((int) tmp.getWidth()), ((int) tmp.getHeight()));

            //I have to set a font here instead of in the RealCellEditor.getGraphCellEditorComponent() because
            //I don't know what cell is being edited when in the RealCellEditor.getGraphCellEditorComponent().
            Font font = GraphConstants.getFont(view.getAllAttributes());
            editingComponent.setFont((font != null) ? font : graph.getFont());

            return component;
        }

        protected GraphCellEditor createGraphCellEditor() {
            return new TextEditor.RealCellEditor();
        }

        /**
         * Overriting this so that I could modify an eiditor container.
         * see http://sourceforge.net/forum/forum.php?thread_id=781479&forum_id=140880
         */
        protected Container createContainer() {
            return new TextEditor.ModifiedEditorContainer();
        }

        class ModifiedEditorContainer extends EditorContainer {
            public void doLayout() {
                super.doLayout();
                //substract 2 pixels that were added to the preferred size of the container for the border.
                Dimension cSize = getSize();
                Dimension dim = editingComponent.getSize();
                editingComponent.setSize(dim.width - 2, dim.height);

                //reset container's size based on a potentially new preferred size of a real editor.
                setSize(cSize.width, getPreferredSize().height);
            }
        }
    }

	//Returns the Renderer for this View
	public CellViewRenderer getRenderer()
	{
		return renderer;
	}
	
	public static class TextRenderer extends JTextArea implements CellViewRenderer {
    	
    	protected transient JGraph graph = null;
    	
    	protected CellView cellView;

    	transient protected Color gradientColor = null;
    	
    	/** Cached hasFocus and selected value. */
    	transient protected boolean hasFocus,
    		selected,
    		preview;

    	public TextRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        public Component getRendererComponent(
                JGraph graph,
                CellView view,
                boolean sel,
                boolean focus,
                boolean preview) {
            setText(view.getCell().toString());
            this.cellView = view;
            this.graph = graph;
            this.selected = sel;
            this.preview = preview;
            this.hasFocus = focus;
            Map attributes = view.getAllAttributes();
            installAttributes(graph, attributes);
            return this;
        }
        
    	public void paint(Graphics g) {
    		try {
    			
    			//this.setText(((TextEditor)this.cellView.getEditor()).getCellEditorValue().toString());
    			
    			//((TextCell)this.cellView.getCell()).setMessage(((TextEditor)this.cellView.getEditor()).getCellEditorValue().toString());
    			
    			
    			Graphics gTemp = g;
    			int x = 0;
			    int y = 0;
			    int l = getWidth()-1;
			    int h = getHeight()-1;
			    
    			gTemp.setClip(x,y,l-10,h);
    			super.paint(gTemp);
    			gTemp.setClip(l-10,y+10,10,h-10);
    			super.paint(gTemp);
    			
    			g.setClip(0,0,getWidth(),getHeight());
    			
    			setOpaque(true);
				Graphics2D g2d = (Graphics2D) g;
				
				//Avoir un dégradé de couleur
				//g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 204), getWidth(),getHeight(), new Color(255, 255, 204), true));
				
				int[] tabXLP={x,x+l-10,x+l,x+l,x};
			    int[] tabYLP={y,y,y+10,y+h,y+h};

			    //g2d.setPaint(new Color(255, 255, 204));
			    //g2d.fillPolygon(tabXLP,tabYLP,5);
			   
			    g2d.setPaint(new Color(153, 0, 51));
			    g2d.drawPolygon(tabXLP,tabYLP,5);
			    g2d.draw(new Line2D.Double(x+l-10, y,x+l-10,y+10));
			    g2d.draw(new Line2D.Double(x+l-10,y+10,x+l,y+10));
			     
			    //System.out.println("TextVeiw Paint "+);
			    paintSelectionBorder(g);
    			
    		} catch (IllegalArgumentException e) {
    			// JDK Bug: Zero length string passed to TextLayout constructor
    		}
    	}

    	/**
    	 * Provided for subclassers to paint a selection border.
    	 */
    	protected void paintSelectionBorder(Graphics g) {
    		((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
    		if (hasFocus && selected)
    			g.setColor(graph.getLockedHandleColor());
    		else if (selected)
    			g.setColor(graph.getHighlightColor());
    		if (selected) {
    			Dimension d = getSize();
    			g.drawRect(0, 0, d.width - 1, d.height - 1);
    		}
    	}

        protected void installAttributes(JGraph graph, Map attributes) {
            setOpaque(GraphConstants.isOpaque(attributes));
            Color foreground = GraphConstants.getForeground(attributes);
            setForeground((foreground != null) ? foreground : graph.getForeground());
            Color background = GraphConstants.getBackground(attributes);
            setBackground((background != null) ? background : graph.getBackground());
            Font font = GraphConstants.getFont(attributes);
            setFont((font != null) ? font : graph.getFont());
            Border border= GraphConstants.getBorder(attributes);
            Color bordercolor = GraphConstants.getBorderColor(attributes);
            if(border != null)
                setBorder(border);
            else if (bordercolor != null) {
                int borderWidth = Math.max(1, Math.round(GraphConstants.getLineWidth(attributes)));
                setBorder(BorderFactory.createLineBorder(bordercolor, borderWidth));
            }
    		//gradientColor = GraphConstants.getGradientColor(attributes);
        }
    }

}
