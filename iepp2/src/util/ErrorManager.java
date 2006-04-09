/*
 * APES is a Process Engineering Software
 * Copyright (C) 2002-2003 IPSquad
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

import iepp.Application;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * The ErrorManager contains all the methods needed to log messages and errors
 *
 * @version $Revision: 1.1 $
 */
public class ErrorManager {

	private static ErrorManager msErrorManager = new ErrorManager();
	
	private Component mOwner = null;
	private JTextArea mErrorArea = null;

	private ErrorManager()
	{
	    this.setOwner(Application.getApplication().getFenetrePrincipale());
	}

	public static ErrorManager getInstance()
	{
		return msErrorManager;
	}

	public void setOwner(Component owner)
	{
		mOwner = owner;
	}

	public void setErrorArea(JTextArea area)
	{
		mErrorArea = area;
	}

	/**
	 * Display a given a Throwable object.
	 *
	 * @param t Throwable
	 */
	public void display(Throwable t)
	{
		t.printStackTrace();
		display(t.getClass().getName(), t.getMessage());
	}

	/**
	 * Display a message
	 *
	 * @param title the message's title
	 * @param msg the message itself
	 */
	public void display(String title, String msg)
	{
		JOptionPane.showMessageDialog(mOwner, Application.getApplication().getTraduction(msg),
									  			Application.getApplication().getTraduction(title),
					      						JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Display a message

	 * @param msg the message itself
	 */
	public void displayError(String msg)
	{
		JOptionPane.showMessageDialog(mOwner, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Add a log message to the JTextArea corresponding
	 * to the given message key
	 *
	 * @param messageKey
	 */
	public void printKey(String messageKey)
	{
		println(Application.getApplication().getTraduction(messageKey));
	}


	/**
	 * Add a log message to the JTextArea
	 *
	 * @param message
	 */
	public synchronized void println(String message)
	{
		Date current_date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String result = "";
		
		if(message!=null && !message.equals(""))
		{
			result = "[" + formatter.format(current_date) + "] " + message;
		}
		
		if(mErrorArea == null)
		{
			System.err.println(result);
		}
		else
		{
			mErrorArea.append(result+"\n");
		}
	}
}
