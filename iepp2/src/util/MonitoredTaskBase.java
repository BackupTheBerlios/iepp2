/*
 * Cameleon
 * Copyright (C) 2003 Nathalie Aussenac-Gilles (CNRS)
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.
 */
package util;


/**
 * Open a new project in the application
 *
 * @version $Revision: 1.1 $
 */
public abstract class MonitoredTaskBase
{
	public static final int INDETERMINED = -1;
	
	private int mLengthOfTask = INDETERMINED;
	private int mCurrent = 0;
	private String mMessage = null;
	private boolean mDone;

	protected abstract Object processingTask();

	/**
	 * Called to start the task.
	 */
	public void go()
	{
		final SwingWorker worker = new SwingWorker()
		{
			public Object construct()
			{
				mDone = false;
				Object o = processingTask();
				mDone = true;
				return o;
		    }
		};
		
		worker.start();
	}

	/**
	 * Called to find out how much work needs to be done.
	 */
	public int getLengthOfTask()
	{
		return mLengthOfTask;
	}

	protected void setLengthOfTask(int lengthOfTask)
	{
		mLengthOfTask = lengthOfTask;
	}

	/**
	 * Called to find out how much has been done.
	 */
	public int getCurrent()
	{
		return mCurrent;
	}

	protected void setCurrent(int current)
	{
		mCurrent = current;
	}

	public String getMessage()
	{
		return mMessage;
	}

	protected void setMessage(String message)
	{
		mMessage = message;
	}
	
	public void traiterErreur()
	{
		this.mDone = true;
	}
	
	/**
	 * Called to find out if the task has completed.
	 */
	public boolean done()
	{
		return mDone;
	}
}
