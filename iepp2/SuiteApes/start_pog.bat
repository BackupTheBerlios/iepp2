set APPLICATION=pog/pog.jar
set MAIN_CLASS=POG.interfaceGraphique.fenetre.FenetrePrincipale

:: APPLICATION : chemin relatif de l'application
:: MAIN_CLASS : classe principale de l'application

env_suite.bat %APPLICATION% %MAIN_CLASS%

:: utilise le bat env_suite pour se placer dans l'environnement Java nécessaire