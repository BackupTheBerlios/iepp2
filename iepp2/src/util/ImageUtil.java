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

package util;

import iepp.ui.iedition.VueDPGraphe;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.ipsquad.apes.adapters.ActivityGraphAdapter;
import org.ipsquad.apes.adapters.ContextGraphAdapter;
import org.ipsquad.apes.adapters.FlowGraphAdapter;
import org.ipsquad.apes.adapters.ResponsabilityGraphAdapter;
import org.ipsquad.apes.adapters.SpemGraphAdapter;
import org.ipsquad.apes.adapters.WorkDefinitionGraphAdapter;
import org.ipsquad.apes.ui.ActivityJGraph;
import org.ipsquad.apes.ui.ContextJGraph;
import org.ipsquad.apes.ui.FlowJGraph;
import org.ipsquad.apes.ui.ResponsabilityJGraph;
import org.ipsquad.apes.ui.WorkDefinitionJGraph;
import org.jgraph.JGraph;

import com.sun.image.codec.jpeg.ImageFormatException;



/**
*
*/
public class ImageUtil
{
	public static void encoderImage(Component c, OutputStream file, String format) throws ImageFormatException, IOException
	{
		try
		{
			int w = c.getWidth(), h = c.getHeight();
			if ( c instanceof VueDPGraphe)
			{
				w = ((VueDPGraphe)c).getZoneAffichage().width;
				h = ((VueDPGraphe)c).getZoneAffichage().height;
			}
			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = image.createGraphics();
			c.paint(g2);
			g2.dispose();
			ImageIO.write(image, format, file);
			file.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void encoderGrapheImage(SpemGraphAdapter adapter, OutputStream file, String format) throws ImageFormatException, IOException
	{
		JGraph mGraph = null;
		if (adapter instanceof ContextGraphAdapter) mGraph=new ContextJGraph(adapter);
		else if(adapter instanceof ResponsabilityGraphAdapter) mGraph=new ResponsabilityJGraph(adapter);
		else if(adapter instanceof ActivityGraphAdapter) mGraph=new ActivityJGraph(adapter);
		else if(adapter instanceof FlowGraphAdapter) mGraph=new FlowJGraph(adapter);
		else if (adapter instanceof WorkDefinitionGraphAdapter) mGraph=new WorkDefinitionJGraph(adapter);

		JFrame frame = new JFrame();
   		frame.getContentPane().add(mGraph);
    	frame.pack();
    	frame.setVisible(false);

		try
		{
			int w = mGraph.getWidth(), h = mGraph.getHeight();
			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = image.createGraphics();
			mGraph.paint(g2);
			g2.dispose();
			ImageIO.write(image, format, file);
			file.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Effectue une homothétie de l'image.
	 * Un coefficient supérieur à 1 correspond à un agrandissement,
	 * un coefficient inférieur à 1 correspond à une réduction.
	 * @param bi l'image.
	 * @param scaleValue la valeur de l'homothétie.
	 * @return une image réduite ou agrandie.
	 */
	public static BufferedImage scale(BufferedImage bi, double scaleValue)
	{
		   AffineTransform tx = new AffineTransform();
		   tx.scale(scaleValue, scaleValue);
		   AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		   BufferedImage biNew = new BufferedImage( (int) (bi.getWidth() * scaleValue),
				   									(int) (bi.getHeight() * scaleValue),
				   									bi.getType());
		   return op.filter(bi, biNew);
   }
}
