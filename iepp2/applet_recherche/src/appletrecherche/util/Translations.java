package appletrecherche.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Translations
{

    public static String FR = "Francais";
    private static ResourceBundle resourceBundle = null;
   private static Locale locale = null;

   static {
     try {
       Translations.locale = Locale.getDefault();
       Translations.resourceBundle = ResourceBundle.getBundle("appletrecherche/languesApplet/languesApplet", Locale.getDefault());
     }
     catch (Throwable ex) {
       ex.printStackTrace();
     }
   }

   public static String getText(String _cle) {
     if (Locale.getDefault() != Translations.locale) {
       try {
         Translations.locale = Locale.getDefault();
         Translations.resourceBundle = ResourceBundle.getBundle("appletrecherche/languesApplet/languesApplet", Locale.getDefault());
       }
       catch (Throwable ex) {
         ex.printStackTrace();
       }
     }
     String resultat = "??";

     if (Translations.resourceBundle != null) {
       String tmp = Translations.resourceBundle.getString(_cle);
       if (tmp != null && !tmp.equalsIgnoreCase("")) {
         resultat = tmp;
       }
     }
     return resultat;
   }


}
