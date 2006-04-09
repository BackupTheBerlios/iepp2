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


package twoxmi.utils;

import org.apache.avalon.framework.logger.*;

/**
 * <p>PDFLogger : </p>
 *
 * <p>Classe construite à partir de org.apache.avalon.framework.logger.ConsoleLogger;
 * </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PDFLogger implements Logger
{
    private MessagerPDF messager = iepp.application.TacheExportationPDF.messager;

    /** Typecode for debugging messages. */
    public static final int LEVEL_DEBUG = 0;

    /** Typecode for informational messages. */
    public static final int LEVEL_INFO = 1;

    /** Typecode for warning messages. */
    public static final int LEVEL_WARN = 2;

    /** Typecode for error messages. */
    public static final int LEVEL_ERROR = 3;

    /** Typecode for fatal error messages. */
    public static final int LEVEL_FATAL = 4;

    /** Typecode for disabled log levels. */
    public static final int LEVEL_DISABLED = 5;

    private final int m_logLevel;


    public PDFLogger()
    {
        this( LEVEL_DEBUG );
    }

    /**
     * Creates a new ConsoleLogger.
     * @param logLevel log level typecode
     */
    public PDFLogger( final int logLevel )
    {
        m_logLevel = logLevel;
    }

    /**
     * Logs a debugging message.
     *
     * @param message a <code>String</code> value
     */
    public void debug( final String message )
    {
        debug( message, null );
    }

    /**
     * Logs a debugging message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void debug( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_DEBUG )
        {
            //System.out.print( "[DEBUG] " );
            //System.out.println( message );
            if( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if debug-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if debug-level logging
     */
    public boolean isDebugEnabled()
    {
        return m_logLevel <= LEVEL_DEBUG;
    }

    /**
     * Logs an informational message.
     *
     * @param message a <code>String</code> value
     */
    public void info( final String message )
    {
        info( message, null );
    }

    /**
     * Logs an informational message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void info( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_INFO )
        {
            this.messager.print("[PAGE] " + message);
            if( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if info-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if info-level logging is enabled
     */
    public boolean isInfoEnabled()
    {
        return m_logLevel <= LEVEL_INFO;
    }

    /**
     * Logs a warning message.
     *
     * @param message a <code>String</code> value
     */
    public void warn( final String message )
    {
        warn( message, null );
    }

    /**
     * Logs a warning message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void warn( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_WARN )
        {
            //System.out.print( "[WARNING] " );
            //System.out.println( message );
            this.messager.warning("[WARNING] " + message);
            if( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if warn-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if warn-level logging is enabled
     */
    public boolean isWarnEnabled()
    {
        return m_logLevel <= LEVEL_WARN;
    }

    /**
     * Logs an error message.
     *
     * @param message a <code>String</code> value
     */
    public void error( final String message )
    {
        error( message, null );
    }

    /**
     * Logs an error message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void error( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_ERROR )
        {
            //System.out.print( "[ERROR] " );
            //System.out.println( message );
            this.messager.error("[ERROR] " + message);
            if( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if error-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if error-level logging is enabled
     */
    public boolean isErrorEnabled()
    {
        return m_logLevel <= LEVEL_ERROR;
    }

    /**
     * Logs a fatal error message.
     *
     * @param message a <code>String</code> value
     */
    public void fatalError( final String message )
    {
        fatalError( message, null );
    }

    /**
     * Logs a fatal error message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void fatalError( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_FATAL )
        {
            //System.out.print( "[FATAL ERROR] " );
            //System.out.println( message );
            this.messager.fatalError("[FATAL ERROR] " + message);
            if( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    /**
     * Returns <code>true</code> if fatal-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if fatal-level logging is enabled
     */
    public boolean isFatalErrorEnabled()
    {
        return m_logLevel <= LEVEL_FATAL;
    }

    /**
     * Just returns this logger (<code>ConsoleLogger</code> is not hierarchical).
     *
     * @param name ignored
     * @return this logger
     */
    public Logger getChildLogger( final String name )
    {
        return this;
    }

}
