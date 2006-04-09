/*
 * APES is a Process Engineering Software
 * This file was part of : cameleon
 * Copyright (C) 2003 Nathalie Aussenac-Gilles (CNRS)
 * Copyright (C) 2004 IPSquad
 * team@ipsquad.tuxfamily.org
 *
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
 */

package util;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * <p>ErrorDialog</p>
 *
 * <p>Description: Class permettant d'afficher des erreurs dans une fenêtre</p>
 *
 */

public class ErrorDialog extends JDialog implements ActionListener
{
    //zone d'affichage des erreurs
    private JTextArea text;
    //bouton de fermeture de la fenetre
    JButton bouton;

    public ErrorDialog(Frame f,String titre,String msg)
    {
        super();
        text = new JTextArea();
        init(titre,msg);
    }

    /**
     * initialisation de la fenetre
     */
    private void init(String titre,String msg)
    {

        this.setTitle(titre);
        this.setSize(600,500);
        text.setEditable(false);
        bouton = new JButton ("OK");
        bouton.addActionListener (this);
        this.setDefaultCloseOperation (DISPOSE_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane ().add (bouton, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(text);
        this.getContentPane().add(scroll, BorderLayout.CENTER);

        // centrer la boîte
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height)
        {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width)
        {
            frameSize.width = screenSize.width;
        }
        this.setLocation( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        text.append(msg + "\n");
        //fenetre devant les autres
        this.setModal(true);

    }

    //action du bouton ok
    public void actionPerformed(ActionEvent e)
    {
        this.dispose();
    }

    //ajoute un message dans la fenetre
    public void println(String msg)
    {
        text.append(msg + "\n");
    }

    //affiche la fenetre
    public void affiche()
    {
        this.setVisible(true);
    }
}
