package twoxmi.utils;

import java.io.PrintWriter ;
import java.io.StringWriter ;
import java.util.Date ;
import java.util.logging.FileHandler ;
import java.util.logging.Formatter ;
import java.util.logging.Handler ;
import java.util.logging.Level ;
import java.util.logging.LogRecord ;
import javax.swing.JOptionPane ;

/**
 * Classe pour journaliser des évènements dans un format HTML
 *
 * @author chaoukhi MHAMEDI
 * @version 1.0
 */
public class Tracer
{
  /**
   * Autorise la journalisation
   */
  private static boolean ENABLE_TRACE = false ;

  /**
   * Logger
   */
  private static java.util.logging.Logger LOGGER = java.util.logging.Logger.
      getLogger ("trace_log") ;

  /**
   * Méthode pour journaliser l'entrée dans une méthode.<br/>
   * Ne pas appeler depuis le main sinon exception car le main est exécuté par
   * le thread principal !
   *
   * @param _params Liste des paramètres
   */
  public static void entering (Object[] _params)
  {
    if (Tracer.ENABLE_TRACE)
    {
      Tracer.LOGGER.entering (Tracer.getCallerClass (),
			      Tracer.getCallerMethod (), _params) ;
    }
  }



  /**
   * Méthode pour créer le fichier de log et autoriser la journalisation
   */
  public static void enableTrace (Level _levelMini, String _fileName,
				  final String _date)
  {
    try
    {
      Handler handler = new FileHandler (_fileName + _date + ".html", false) ; // false pour recréer le fichier
      LOGGER.addHandler (handler) ;
      LOGGER.setLevel (_levelMini) ;
      handler.setFormatter (new Formatter ()
      {
	// formatage d’un enregistrement
	public String format (LogRecord _record)
	{
	  StringBuffer stringBuffer = new StringBuffer ("") ;
	  String style ; // style d'affichage du level du _record
	  Level level = _record.getLevel () ;
	  if (level.intValue () < Level.CONFIG.intValue ())
	  {
	    // niveaux FINEST, FINER, FINE
	    style = "color: green ; margin-left: 20px" ;
	  }
	  else if (level.intValue () > Level.INFO.intValue ())
	  {
	    // niveaux WARNING et SEVERE
	    style = "color: red ; margin-left: 20px" ;
	  }
	  else
	  {
	    // niveaux INFO et CONFIG
	    style = "color: blue; margin-left: 20px" ;
	  }

	  stringBuffer.append ("<div style=\"" + style + "\">"
			       + Utilitaire.LS) ; // div global
	  stringBuffer.append ("<u>Niveau</u> : " + Utilitaire.LS) ;
	  stringBuffer.append (_record.getLevel ()) ;
	  stringBuffer.append ("&nbsp;&nbsp;&nbsp;<u>Classe</u> : "
			       + Utilitaire.LS) ;
	  stringBuffer.append (_record.getSourceClassName ()) ;
	  stringBuffer.append ("&nbsp;&nbsp;&nbsp;<u>Méthode</u> : "
			       + Utilitaire.LS) ;
	  String nomMethod = _record.getSourceMethodName () ;
	  if (nomMethod.equals ("<init>")) // constructeur
	  {
	    stringBuffer.append ("constructeur") ;
	  }
	  else
	  {
	    stringBuffer.append (nomMethod) ;
	  }
	  stringBuffer.append ("<br><u>N° séquence</u> : "
			       + _record.getSequenceNumber ()) ;
	  stringBuffer.append ("<br><u>Thread</u> : "
			       + _record.getThreadID ()) ;
	  String message = _record.getMessage () ;
	  //--------------------------------------------------------------------
	  // entrée dans une méthode
	  if (message.toLowerCase ().startsWith ("entry"))
	  {
	    stringBuffer.append ("<div>" + Utilitaire.LS) ;
	    stringBuffer.append (
		"<u>Type</u> :&nbsp;&nbsp;&nbsp;Entrée dans la méthode") ;
	    stringBuffer.append ("<br><u>Appelée par</u> :&nbsp;&nbsp;&nbsp;") ;
	    stringBuffer.append (Tracer.getCaller (9)) ;
	    stringBuffer.append ("</div>" + Utilitaire.LS) ;
	    Object[] parametres = _record.getParameters () ;
	    if (parametres != null && parametres.length > 0)
	    {
	      stringBuffer.append ("<u>Paramètres de l'appel</u> :<ul>") ;
	      for (int i = 0 ; i < parametres.length ; i++)
	      {
		stringBuffer.append ("<li>" + Utilitaire.LS) ;
		stringBuffer.append (parametres[i].getClass ()
				     + " :&nbsp;&nbsp;&nbsp;"
				     + parametres[i].toString ()) ;
		stringBuffer.append ("</li>" + Utilitaire.LS) ;
	      }
	      stringBuffer.append ("</ul>") ;
	    }
	  }
	  //--------------------------------------------------------------------
	  // exception ou erreur interceptée
	  else if (message.toLowerCase ().startsWith ("throw"))
	  {
	    Throwable throwable = _record.getThrown () ;
	    stringBuffer.append ("<div>" + Utilitaire.LS) ;
	    stringBuffer.append ("<u>Type</u> :&nbsp;&nbsp;&nbsp;"
				 + throwable.getClass ()) ;
	    stringBuffer.append ("<br><u>Message</u> :&nbsp;&nbsp;&nbsp;"
				 + throwable.getMessage ()) ;
	    stringBuffer.append ("<br><u>Trace</u> :&nbsp;&nbsp;&nbsp;") ;
	    StackTraceElement[] pile = throwable.getStackTrace () ;
	    stringBuffer.append (
		"<div style=\"margin-left: 30px\">"
		+ Utilitaire.LS) ;
	    for (int i = 0 ; i < pile.length ; i++)
	    {
	      stringBuffer.append (pile[i].toString () + "<br>") ;
	    }
	    stringBuffer.append ("</div>" + Utilitaire.LS) ;
	    stringBuffer.append ("</div>" + Utilitaire.LS) ;
	  }
	  //--------------------------------------------------------------------
	  // sortie d'une méthode
	  else if (message.toLowerCase ().startsWith ("return"))
	  {
	    stringBuffer.append ("<div>" + Utilitaire.LS) ;
	    stringBuffer.append (
		"<u>Type</u> :&nbsp;&nbsp;&nbsp;Sortie de méthode") ;
	    stringBuffer.append ("<br><u>Appelée par</u> :&nbsp;&nbsp;&nbsp;") ;
	    stringBuffer.append (Tracer.getCaller (9)) ;
	    stringBuffer.append ("</div>" + Utilitaire.LS) ;
	  }
	  //--------------------------------------------------------------------
	  // message envoyé par la méthode log (Level, String)
	  else
	  {
	    //System.err.println (message) ;
	    stringBuffer.append ("<div>" + Utilitaire.LS) ;
	    stringBuffer.append ("<u>Message</u> :&nbsp;&nbsp;&nbsp;"
				 + message) ;
	    stringBuffer.append ("</div>" + Utilitaire.LS) ;
	  }
	  stringBuffer.append ("</div>" + Utilitaire.LS) ; // div global
	  stringBuffer.append ("<hr>") ;
	  return stringBuffer.toString () ;
	}



	// entête du fichier de log
	public String getHead (Handler _handler)
	{
	  StringBuffer stringBuffer = new StringBuffer ("") ;
	  stringBuffer.append ("<html>" + Utilitaire.LS) ;
	  stringBuffer.append ("<head>" + Utilitaire.LS) ;
	  stringBuffer.append ("<title>Trace d'exécution</title>"
			       + Utilitaire.LS) ;
	  stringBuffer.append ("</head>" + Utilitaire.LS) ;
	  stringBuffer.append ("<body>" + Utilitaire.LS) ;
	  stringBuffer.append (
	      "<br><h1 align=\"center\">Trace d'exécution du ") ;
	  Date date = new Date (Long.parseLong (_date)) ;
	  stringBuffer.append (date.toString ()) ;
	  stringBuffer.append ("</h1><br>") ;
	  stringBuffer.append ("<div>Niveau minimum: "
			       + LOGGER.getLevel () + "</div>") ;
	  stringBuffer.append ("<br>") ;
	  return stringBuffer.toString () ;
	}



	// fin du fichier de log
	public String getTail (Handler _handler)
	{
	  return "<br><center>Fin</center></body>" + Utilitaire.LS
	      + "</html>" ;
	}
      }) ; // fin new Formatter ()
      Tracer.ENABLE_TRACE = true ;
    }
    catch (Throwable ex)
    {
      ex.printStackTrace () ;
    }
  } // fin enableTrace



  /**
   * A utility method which extracts the full name of a method on the stack
   * above the calling method.
   *
   * @param callerID is the index of the method that many stack frames above the
   * calling method. The value 0 returns the name of the calling method itself,
   * 1 returns its immediate caller, 2 the caller of that caller, etc.
   * @return full name of the method calling this one a given number of stack
   * frames above that one.
   */
  public static String getCaller (int callerID)
  {
    int stack_base = callerID + 2 ; // +1 to ignore "Throwable" line, +1 to ignore this method
    StringWriter sw = new StringWriter () ;
    (new Throwable ()).printStackTrace (new PrintWriter (sw)) ;
    String trace = sw.toString () ;
    int linestart = -1 ;
    for (int i = 0 ; i < stack_base ; i++)
    {
      linestart = trace.indexOf ("\n", linestart + 1) ;
    }
    return trace.substring (linestart + 5, trace.indexOf ("(", linestart + 5)) ;
  }



  /**
   * Méthode pour journaliser la sortie d'une méthode.<br/>
   * Ne pas appeler depuis le main pour la même raison que pour entering
   *
   * @param objects Object[]
   */
  public static void exiting ()
  {
    if (Tracer.ENABLE_TRACE)
    {
      LOGGER.exiting (Tracer.getCallerClass (),
		      Tracer.getCallerMethod ()) ;
    }
  }



  /**
   * Méthode pour obtenir le nom de la classe appelante
   *
   * @return String Le nom de la classe appelante
   */
  public static String getCallerClass ()
  {
    String tmp = Tracer.getCaller (2) ; // appelant
    tmp = tmp.substring (0, tmp.lastIndexOf (".")) ;
    return tmp ;
  }



  /**
   * Méthode pour obtenir le nom de la méthode appelante
   *
   * @return String Le nom de la méthode
   */
  public static String getCallerMethod ()
  {
    String tmp = Tracer.getCaller (2) ; // appelant
    tmp = tmp.substring (tmp.lastIndexOf (".") + 1) ;
    return tmp ;
  }

  /**
   * log
   *
   * @param _level Level
   * @param _message String
   * @param _throwable TransformerConfigurationException
   */
  public static void log (Level _level, String _message,
			  Throwable _throwable)
  {
    if (LOGGER != null)
    {
      LOGGER.log (_level, _message, _throwable) ;
    }
    else
    {
      _throwable.printStackTrace () ;
    }
  }



  /**
   * log
   *
   * @param _level Level
   * @param _message String
   */
  public static void log (Level _level, String _message)
  {
    if (LOGGER != null)
    {
      LOGGER.log (_level, _message) ;
    }
    else
    {
      JOptionPane.showMessageDialog (null, _message, "Log",
				     JOptionPane.INFORMATION_MESSAGE) ;
    }
  }

  /**
   * setLogLevel
   *
   * @param level Level
   */
  public static void setLogLevel (Level _level)
  {
    if (LOGGER != null)
    {
      LOGGER.setLevel (_level) ;
    }
  }
}
