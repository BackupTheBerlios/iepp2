package iepp.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import iepp.Application;
import util.IconManager;

public class TwoXmiAboutPanel extends JPanel {
  private static final int HAUTEUR = 100;
  private static final int LARGEUR = 500;
  private static final int Y_TEXTE = 70;

  private static String TEXTE;
  //modif 2XMI Amandine
  //private static final String PARTICIPANTS = "Jean GASTON - Amandine JEAN - Chaoukhi MHAMEDI - Sebastien RENE";
  private static final String SITE = "http://iepp.free.fr - http://2xmi.free.fr";

  public TwoXmiAboutPanel() {
    super ();
    TEXTE = Application.getApplication().getTraduction("2xmiAPropos");
    this.setPreferredSize(new Dimension (LARGEUR, HAUTEUR));
  }


  public void paintComponent (Graphics _graphics)
  {
    super.paintComponent(_graphics);
    _graphics.setColor(Color.BLACK);
    _graphics.drawLine(0,0,this.getParent().getWidth(), 0);
    _graphics.drawLine(0,1,this.getParent().getWidth(), 1);
    String slash = System.getProperty("file.separator");

    ImageIcon image = (ImageIcon) IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "logo_2xmi.gif");
    _graphics.drawImage(image.getImage(),175,5,150,40,Color.WHITE, null);
    _graphics.drawString(TEXTE,10,Y_TEXTE);
    _graphics.drawString(SITE, 150, Y_TEXTE + _graphics.getFontMetrics().getHeight());
  }
}
