set CLASSPATH=%1;lib/2xmi.jar;lib/JSX.jar;lib/Tidy.jar;lib/jgraph.jar;lib/ipsquad-2.5.2.jar;lib/avalon-framework-4.2.0.jar;lib/fop.jar;lib/batik-1.5.1.jar;lib/JimiProClasses.zip;lib/lucene-1.4.3.jar

:: CLASSPATH : décrit tous les chemins où Java doit aller chercher des packages
:: cette variable doit être enrichie lorsque de nouvelles librairies sont ajoutées

set JAVA_HOME=C:\JAVA\jre\bin
set PATH=%JAVA_HOME%;%PATH%

:: JAVA_HOME : répertoire où se trouvent les exécutables Java
:: cette variable doit être mise à jour suivant le poste d'exécution

start javaw -Xmx128m -classpath %CLASSPATH% %2