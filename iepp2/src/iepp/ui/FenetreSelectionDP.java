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

package iepp.ui;

import iepp.Application;
import iepp.application.CFermerProjet;
import iepp.application.areferentiel.CChargerDP;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


/**
 * 
 */
public class FenetreSelectionDP extends JDialog implements KeyListener, MouseListener {

	BorderLayout bfenetreCentre = new BorderLayout();
	
	JPanel pfenetreCentre = new JPanel();
	JPanel pfenetreSud = new JPanel();
	
	JLabel dp = new JLabel();
	
	JButton valider = new JButton();
	JButton annuler = new JButton();
	
	JList listeDefProc = new JList();
	JScrollPane scrollPane = null;
	
	FenetreChoixProcessus fenetreChoixProc = null;
	Referentiel refChoisi = null;
	
	/**
	 * Indique si une DP a bien été choisie
	 */
	private boolean estDPChoisie ;
	
	public FenetreSelectionDP(FenetrePrincipale parent, FenetreChoixProcessus fcp, Referentiel ref)
	{
		super(parent,Application.getApplication().getTraduction("FLister_processus"),true);
		this.fenetreChoixProc = fcp;
		this.refChoisi = ref;
		listeDefProc = new JList(ref.getListeNom(ElementReferentiel.DP));
		init();
		this.setResizable(false);
		this.pack();
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		this.setVisible(true);
	}
	
	void init()
	{	
		valider.setMnemonic('I');
		valider.setText(Application.getApplication().getTraduction("Valider"));
		valider.setFocusable(true);
		valider.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ok(e);
			}
		});
		
		listeDefProc.addKeyListener(this);
		listeDefProc.addMouseListener(this);
		
		annuler.setMnemonic('N');
		annuler.setText(Application.getApplication().getTraduction("Annuler"));
		annuler.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			annuler(e);
		  }
		});
		pfenetreSud.add(valider, null);
		pfenetreSud.add(annuler, null);
		this.getContentPane().add(pfenetreSud,BorderLayout.SOUTH);
	
		pfenetreCentre.setLayout(bfenetreCentre);
		listeDefProc.setVisibleRowCount(5);
		listeDefProc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listeDefProc.setSelectedIndex(0);
		scrollPane = new JScrollPane(listeDefProc);
		scrollPane.setPreferredSize(new Dimension(300,300));
		
		dp.setText(Application.getApplication().getTraduction("Liste_processus"));
		dp.setDisplayedMnemonic('P');
		dp.setLabelFor(listeDefProc);
		pfenetreCentre.add(scrollPane, BorderLayout.CENTER);
		pfenetreCentre.add(dp, BorderLayout.NORTH);
		this.getContentPane().add(pfenetreCentre,BorderLayout.CENTER);
	}
	
	private void ok(ActionEvent e) 
	{
		if (this.fenetreChoixProc != null)
			this.fenetreChoixProc.dispose();
		long idRef = this.refChoisi.nomDefProcToId((String)this.listeDefProc.getSelectedValue());
		if(idRef==-1)
		{
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("ERR_DpInextitante"),Application.getApplication().getTraduction("ERR"),JOptionPane.WARNING_MESSAGE);
			this.estDPChoisie = false;
		}
		else
		{
			if (Application.getApplication().getProjet() != null)
			{
				CFermerProjet c = new CFermerProjet();
				c.executer();
			}
			
			CChargerDP c = new CChargerDP(idRef);
			c.executer();
			this.estDPChoisie = true;
		}
		this.dispose();
	}

	private void annuler(ActionEvent e)
	{
		this.estDPChoisie = false;
		this.dispose();
	}
	
	public boolean isDPChoisie()
	{
		return this.estDPChoisie ;
	}
	
	/**
	 * Action sur la fenetre par les touches du clavier
	 */		

	public void keyTyped(KeyEvent arg0) {}

	public void keyPressed(KeyEvent arg0) {}

	public void keyReleased(KeyEvent event) {
		int caract = event.getKeyCode();
		if (event.getSource() == listeDefProc) {
			if (caract == KeyEvent.VK_ENTER) {
				valider.doClick ();
			}
			else if (caract == KeyEvent.VK_ESCAPE) {
				annuler.doClick ();
			}
		}
	}

	/**
	 * Ouverture de processus par double-clic
	 */		
	public void mouseClicked(MouseEvent event) {
		if (event.getSource() == listeDefProc) {
			if (event.getClickCount() == 2){
				valider.doClick ();
			}
		}
	}

	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
}
