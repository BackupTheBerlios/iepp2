package twoxmi.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class TwoXmiBundle {
  private static ResourceBundle resourceBundle = null;
  private static Locale locale = null;

  static {
    try {
      TwoXmiBundle.locale = Locale.getDefault();
      TwoXmiBundle.resourceBundle = ResourceBundle.getBundle("twoxmi/utils/langues", Locale.getDefault());
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  public static String getText(String _cle) {
    if (Locale.getDefault() != TwoXmiBundle.locale) {
      try {
        TwoXmiBundle.locale = Locale.getDefault();
        TwoXmiBundle.resourceBundle = ResourceBundle.getBundle("twoxmi/utils/langues", Locale.getDefault());
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
    }
    String resultat = "??";

    if (TwoXmiBundle.resourceBundle != null) {
      String tmp = TwoXmiBundle.resourceBundle.getString(_cle);
      if (tmp != null && !tmp.equalsIgnoreCase("")) {
        resultat = tmp;
      }
    }

    return resultat;
  }
}
