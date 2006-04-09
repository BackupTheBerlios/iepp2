<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xml.apache.org/fop/extensions" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:iepp="http://2xmi.free.fr" xmlns:xmi="http://www.omg.org/XMI">
    <!-- Format de sortie -->
    <xsl:output method="xml" version="1.0" indent="yes" encoding="UTF-8"/>

    <!-- Inclusion xsl permettant de formater les contenus XHTML -->
    <xsl:include href="ressources/fichiersXml/xhtml2fo.xsl"/>

    <!--variables globales -->
    <xsl:variable name="fileSeparator" select="system-property('file.separator')"/>
    <xsl:variable name="cheminGeneration" select="xmi:XMIPackage/xmi:XMI/iepp:processus/@cheminGeneration"/>

    <!-- Mise en page des bordures -->
    <xsl:variable name="bordure">
        <xsl:text>2px solid #00496C</xsl:text>
    </xsl:variable>

    <!--date generation du xmi-->
    <xsl:variable name="dateGeneration">
        <xsl:variable name="date">
            <xsl:value-of select="xmi:XMIPackage/xmi:XMI/iepp:processus/@dateExport"/>
        </xsl:variable>
        <xsl:value-of select="concat(substring($date,7,2),'/',substring($date,5,2),'/',substring($date,1,4))"/>
    </xsl:variable>

    <xsl:variable name="piedPage" select="xmi:XMIPackage/xmi:XMI/iepp:processus/@piedPage"/>

    <xsl:variable name="apostrophe">
        <xsl:text>'</xsl:text>
    </xsl:variable>

    <!-- clé permettant d'accèder aux élements ds le fichier xmi -->
    <xsl:key name="role-index" match="iepp:role" use="@xmi:id"/>
    <xsl:key name="produit-index" match="iepp:produit" use="@xmi:id"/>
    <xsl:key name="interface-index" match="iepp:interface" use="@xmi:id"/>
    <xsl:key name="definitionTravail-index" match="iepp:definitionTravail" use="@xmi:id"/>
    <xsl:key name="elementPresentation-index" match="iepp:elementPresentation" use="@xmi:id"/>
    <xsl:key name="activite-index" match="iepp:activite" use="@xmi:id"/>
    <xsl:key name="role-index" match="iepp:role" use="@xmi:id"/>
    <xsl:key name="guide-index" match="iepp:guide" use="@xmi:id"/>

    <!-- fonction récursives permettant de remplacer les caracteres speciaux -->
    <xsl:template name="replace-string">
        <xsl:param name="text"/>
        <xsl:variable name="with">
            <xsl:text>_</xsl:text>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="contains($text,' ')">
                <xsl:variable name="tmp">
                    <xsl:value-of select="substring-before($text,' ')"/>
                    <xsl:value-of select="$with"/>
                    <xsl:call-template name="replace-string">
                        <xsl:with-param name="text" select="substring-after($text,' ')"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="replace-string">
                    <xsl:with-param name="text" select="$tmp"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="contains($text,$apostrophe)">
                <xsl:variable name="tmp">
                    <xsl:value-of select="substring-before($text,$apostrophe)"/>
                    <xsl:value-of select="$with"/>
                    <xsl:call-template name="replace-string">
                        <xsl:with-param name="text" select="substring-after($text,$apostrophe)"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="replace-string">
                    <xsl:with-param name="text" select="$tmp"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="contains($text,'é')">
                <xsl:variable name="tmp">
                    <xsl:value-of select="substring-before($text,'é')"/>
                    <xsl:text>e</xsl:text>
                    <xsl:call-template name="replace-string">
                        <xsl:with-param name="text" select="substring-after($text,'é')"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="replace-string">
                    <xsl:with-param name="text" select="$tmp"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="contains($text,'è')">
                <xsl:variable name="tmp">
                    <xsl:value-of select="substring-before($text,'è')"/>
                    <xsl:text>e</xsl:text>
                    <xsl:call-template name="replace-string">
                        <xsl:with-param name="text" select="substring-after($text,'è')"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="replace-string">
                    <xsl:with-param name="text" select="$tmp"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- template pour la création d'un signet -->
    <xsl:template name="signet">
        <xsl:param name="lien"/>
        <xsl:param name="titre"/>
        <xsl:element name="fox:outline">
            <xsl:attribute name="internal-destination">
                <xsl:value-of select="$lien"/>
            </xsl:attribute>
            <xsl:element name="fox:label">
                <xsl:value-of select="$titre"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- template pour la création d'un titre -->
    <xsl:template name="titre">
        <xsl:param name="id"/>
        <xsl:param name="titre"/>
        <xsl:element name="fo:block">
            <xsl:attribute name="space-after">
                <xsl:text>1in</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="id">
                <xsl:value-of select="$id"/>
            </xsl:attribute>
            <xsl:attribute name="text-align">center</xsl:attribute>
            <xsl:attribute name="color">#FFFFFF</xsl:attribute>
            <xsl:attribute name="background-color">#065979</xsl:attribute>
            <xsl:attribute name="border">2px solid #000000</xsl:attribute>
            <xsl:value-of select="$titre"/>
        </xsl:element>
    </xsl:template>

    <!-- template pour la création d'une image -->
    <xsl:template name="image">
        <xsl:param name="chemin"/>
        <xsl:element name="fo:external-graphic">
            <xsl:attribute name="src">
                <xsl:value-of select="concat('url(',$chemin,')')"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>

    <!-- template pour la creation d'un block avec une images seule -->
    <xsl:template name="blockImage">
        <xsl:param name="cheminImage"/>
        <xsl:element name="fo:block">
            <xsl:attribute name="space-after">
                <xsl:text>12pt</xsl:text>
            </xsl:attribute>
            <!-- Contour image -->
            <xsl:attribute name="border">2px solid #000000</xsl:attribute>
            <xsl:call-template name="image">
                <xsl:with-param name="chemin" select="$cheminImage"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <!-- Mise en page des titre des listes d'elements -->
    <xsl:template name="presentationListe">
        <xsl:param name="titre"/>
        <xsl:element name="fo:block">
          <xsl:attribute name="padding-left">
            <xsl:text>6pt</xsl:text>
          </xsl:attribute>
            <xsl:attribute name="space-after">
                <xsl:text>12pt</xsl:text>
            </xsl:attribute>
            <xsl:call-template name="gras"/>
            <xsl:attribute name="border-left">
                <xsl:value-of select="$bordure"/>
            </xsl:attribute>
            <xsl:attribute name="border-bottom">
                <xsl:value-of select="$bordure"/>
            </xsl:attribute>
            <xsl:value-of select="$titre"/>
        </xsl:element>
    </xsl:template>

    <!-- template permettant de crer un bas de page -->
    <xsl:template name="basPage">
        <xsl:param name="contenu"/>
        <xsl:element name="fo:static-content">
            <xsl:attribute name="flow-name">
                <xsl:text>xsl-region-after</xsl:text>
            </xsl:attribute>
            <xsl:element name="fo:block">
                <xsl:attribute name="text-align">
                    <xsl:text>center</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="border-top">
                    <xsl:value-of select="$bordure"/>
                </xsl:attribute>
                <xsl:attribute name="margin-left">
                    <xsl:text>1.5cm</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="margin-right">
                    <xsl:text>1.5cm</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="padding-top">
                    <xsl:text>8pt</xsl:text>
                </xsl:attribute>
                <xsl:text>Date de génération : </xsl:text>
                <xsl:value-of select="$dateGeneration"/>
                <xsl:element name="fo:block">
                    <xsl:value-of select="$contenu"/>
                </xsl:element>
                <xsl:element name="fo:block">
                    <xsl:value-of select="$piedPage"/>
                </xsl:element>
            </xsl:element>
            <xsl:element name="fo:block">
                <xsl:attribute name="text-align">
                    <xsl:text>right</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="margin-right">
                    <xsl:text>1.5cm</xsl:text>
                </xsl:attribute>
                <fo:page-number/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- Template permettant de de formater du texteen gras -->
    <xsl:template name="gras">
        <xsl:attribute name="font-weight">
            <xsl:text>bold</xsl:text>
        </xsl:attribute>
    </xsl:template>

    <!-- Template créant la liste des guides associes à un élement -->
    <xsl:template name="listeGuides">
      <xsl:if test="key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/iepp:guideId">
        <xsl:call-template name="presentationListe">
          <xsl:with-param name="titre">
            <xsl:text>GUIDES ASSOCIES :</xsl:text>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:element name="fo:block">
          <xsl:attribute name="margin-left">
            <xsl:text>2.5cm</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="padding-left">
            <xsl:text>2pt</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="border-left">
            <xsl:value-of select="$bordure"/>
          </xsl:attribute>
          <xsl:attribute name="space-after">
            <xsl:text>12pt</xsl:text>
          </xsl:attribute>
          <xsl:for-each select="key('guide-index',key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/iepp:guideId/@xmi:idref)">
            <xsl:element name="fo:block">
              <xsl:value-of select="@nom"/>
            </xsl:element>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
    </xsl:template>

    <!-- Template permettant de gerer lordre de generation -->
    <xsl:template name="traitementOrdre">
        <xsl:param name="indice"/>
        <xsl:param name="max"/>
         <xsl:apply-templates select="xmi:XMIPackage/xmi:XMI/iepp:paquetagePresentation[@ordreGeneration=$indice] | xmi:XMIPackage/xmi:XMI/iepp:composant[@ordreGeneration=$indice]"/>
       <xsl:if test="$indice &lt; $max">
            <xsl:call-template name="traitementOrdre">
                <xsl:with-param name="indice" select="$indice+1"/>
                <xsl:with-param name="max" select="$max"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:variable name="nbElementGeneration">
        <xsl:variable name="nbComposant">
            <xsl:value-of select="count(xmi:XMIPackage/xmi:XMI/iepp:composant)"/>
        </xsl:variable>
        <xsl:variable name="nbPaquetagePresentation">
            <xsl:value-of select="count(xmi:XMIPackage/xmi:XMI/iepp:paquetagePresentation)"/>
        </xsl:variable>
        <xsl:value-of select="$nbComposant + $nbPaquetagePresentation"/>
    </xsl:variable>


    <xsl:template name="recapitulatif">
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:element name="fo:wrapper">
                    <xsl:element name="fox:outline">
                        <xsl:attribute name="internal-destination">
                            <xsl:value-of select="'Recapitulatif'"/>
                        </xsl:attribute>
                        <xsl:element name="fox:label">
                            <xsl:value-of select="'Récapitulatif'"/>
                        </xsl:element>
                        <xsl:call-template name="signet">
                            <xsl:with-param name="lien" select="'Recapitulatif_roles'"/>
                            <xsl:with-param name="titre" select="'Rôles'"/>
                        </xsl:call-template>
                        <xsl:call-template name="signet">
                            <xsl:with-param name="lien" select="'Recapitulatif_produits'"/>
                            <xsl:with-param name="titre" select="'Produits'"/>
                        </xsl:call-template>
                        <xsl:call-template name="signet">
                            <xsl:with-param name="lien" select="'Recapitulatif_activites'"/>
                            <xsl:with-param name="titre" select="'Activités'"/>
                        </xsl:call-template>
                        <xsl:call-template name="signet">
                            <xsl:with-param name="lien" select="'Recapitulatif_guides'"/>
                            <xsl:with-param name="titre" select="'Guides'"/>
                        </xsl:call-template>
                    </xsl:element>
                    <xsl:call-template name="titre">
                        <xsl:with-param name="id" select="'Recapitulatif'"/>
                        <xsl:with-param name="titre" select="'Récapitulatif'"/>
                    </xsl:call-template>
                    <xsl:element name="fo:block">
                        <xsl:attribute name="space-after">
                            <xsl:text>20pt</xsl:text>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>

        <!-- Liste roles -->
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="'Recapitulatif_roles'"/>
                    <xsl:with-param name="titre" select="'Récapitulatif'"/>
                </xsl:call-template>


                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>ROLES :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="//iepp:composant">
                        <xsl:variable name="nomCompos">
                            <xsl:value-of select="@nom"/>
                        </xsl:variable>
                        <xsl:for-each select="iepp:roleId">
                            <xsl:element name="fo:block">
                                <xsl:value-of select="key('role-index',@xmi:idref)/@nom"/>
                                <xsl:text> - </xsl:text>
                                <xsl:value-of select="$nomCompos"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:for-each>
                  </xsl:element>

            </xsl:element>
        </xsl:element>

        <!-- Liste produits -->
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="'Recapitulatif_produits'"/>
                    <xsl:with-param name="titre" select="'Récapitulatif'"/>
                </xsl:call-template>


                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>PRODUITS :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="//iepp:composant">
                        <xsl:variable name="nomCompos">
                            <xsl:value-of select="@nom"/>
                        </xsl:variable>
                        <xsl:for-each select="iepp:produitId">
                            <xsl:element name="fo:block">
                                <xsl:value-of select="key('produit-index',@xmi:idref)/@nom"/>
                                <xsl:text> - </xsl:text>
                                <xsl:value-of select="$nomCompos"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:for-each>
                  </xsl:element>
            </xsl:element>
        </xsl:element>

        <!-- Liste activités -->
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="'Recapitulatif_activites'"/>
                    <xsl:with-param name="titre" select="'Récapitulatif'"/>
                </xsl:call-template>


                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>ACTIVITES :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="//iepp:composant">
                        <xsl:variable name="nomCompos">
                            <xsl:value-of select="@nom"/>
                        </xsl:variable>
                        <xsl:for-each select="key('definitionTravail-index',iepp:definitionTravailId/@xmi:idref)">
                            <xsl:for-each select="iepp:activiteId">
                                <xsl:element name="fo:block">
                                    <xsl:value-of select="key('activite-index',@xmi:idref)/@nom"/>
                                    <xsl:text> - </xsl:text>
                                    <xsl:value-of select="$nomCompos"/>
                                </xsl:element>
                            </xsl:for-each>
                        </xsl:for-each>

                    </xsl:for-each>
                  </xsl:element>
            </xsl:element>
        </xsl:element>

        <!-- Liste guides -->
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="'Recapitulatif_guides'"/>
                    <xsl:with-param name="titre" select="'Récapitulatif'"/>
                </xsl:call-template>


                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>GUIDES :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="//iepp:guide">
                        <xsl:element name="fo:block">
                                <xsl:value-of select="@nom"/>
                            </xsl:element>
                    </xsl:for-each>
                  </xsl:element>
            </xsl:element>
        </xsl:element>

    </xsl:template>

    <xsl:template name="statistiques">
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:call-template name="signet">
                    <xsl:with-param name="lien" select="'Statistiques'"/>
                    <xsl:with-param name="titre" select="'Statistiques du site'"/>
                </xsl:call-template>

                <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="'Statistiques'"/>
                    <xsl:with-param name="titre" select="'Statistiques du site'"/>
                </xsl:call-template>
                <xsl:element name="fo:block">
                    <xsl:attribute name="space-after">
                        <xsl:text>20pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:element name="fo:block">
                        <xsl:text>Nombre de composants : </xsl:text>
                        <xsl:value-of select="count(//iepp:composant)"/>
                    </xsl:element>
                    <xsl:element name="fo:block">
                        <xsl:text>Nombre de rôles : </xsl:text>
                        <xsl:value-of select="count(//iepp:role)"/>
                    </xsl:element>
                     <xsl:element name="fo:block">
                        <xsl:text>Nombre de produits : </xsl:text>
                        <xsl:value-of select="count(//iepp:produit)"/>
                    </xsl:element>
                    <xsl:element name="fo:block">
                        <xsl:text>Nombre d'activités : </xsl:text>
                        <xsl:value-of select="count(//iepp:activite)"/>
                    </xsl:element>
                    <xsl:element name="fo:block">
                        <xsl:text>Nombre de définitions de travail : </xsl:text>
                        <xsl:value-of select="count(//iepp:definitionTravail)"/>
                    </xsl:element>
                    <xsl:element name="fo:block">
                        <xsl:text>Nombre de guides : </xsl:text>
                        <xsl:value-of select="count(//iepp:guide)"/>
                    </xsl:element>
                    <xsl:element name="fo:block">
                        <xsl:text>Nombre de paquetages de présentation : </xsl:text>
                        <xsl:value-of select="count(//iepp:paquetagePresentation)"/>
                    </xsl:element>
                    <xsl:element name="fo:block">
                        <xsl:text>Nombre d'éléments de présentation : </xsl:text>
                        <xsl:value-of select="count(//iepp:elementPresentation)"/>
                    </xsl:element>
                    <xsl:element name="fo:block"/>
                    <xsl:element name="fo:block">
                        <xsl:text>Total : </xsl:text>
                        <xsl:value-of select="count(//iepp:composant)+count(//iepp:role)+count(//iepp:produit)+count(//iepp:activite)+count(//iepp:definitionTravail)+count(//iepp:guide)+count(//iepp:paquetagePresentation)+count(//iepp:elementPresentation)"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- Début traitement -->
    <xsl:template match="/">
        <xsl:element name="fo:root">
            <xsl:element name="fo:layout-master-set">
                <xsl:element name="fo:simple-page-master">
                    <!-- Définition dimensions de la page -->
                    <xsl:attribute name="page-width">21cm</xsl:attribute>
                    <xsl:attribute name="page-height">29.7cm</xsl:attribute>
                    <xsl:attribute name="master-name">A4</xsl:attribute>
                    <xsl:element name="fo:region-body">
                        <xsl:attribute name="region-name">
                            <xsl:text>xsl-region-body</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="margin-bottom">3cm</xsl:attribute>
                        <xsl:attribute name="margin-left">1.5cm</xsl:attribute>
                        <xsl:attribute name="margin-right">1.5cm</xsl:attribute>
                        <xsl:attribute name="margin-top">1.5cm</xsl:attribute>
                    </xsl:element>
                    <fo:region-after extent="1in"/>
                </xsl:element>
            </xsl:element>
            <!-- Processus -->
            <xsl:apply-templates select="xmi:XMIPackage/xmi:XMI/iepp:processus"/>

            <!-- traiment des composants et paquetages de présenation en fonction de l'ordre -->
            <xsl:call-template name="traitementOrdre">
                <xsl:with-param name="indice" select="0"/>
                <xsl:with-param name="max" select="$nbElementGeneration"/>
            </xsl:call-template>

            <!-- traitement du recapitulatif -->
            <xsl:call-template name="recapitulatif"/>

            <!-- traitement du statistiques -->
            <xsl:call-template name="statistiques"/>

        </xsl:element>
    </xsl:template>

    <!-- Processus -->
    <xsl:template match="xmi:XMIPackage/xmi:XMI/iepp:processus">
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:element name="fo:wrapper">
                    <xsl:attribute name="text-align">
                        <xsl:text>center</xsl:text>
                    </xsl:attribute>
                    <xsl:call-template name="signet">
                        <xsl:with-param name="lien" select="@xmi:id"/>
                        <xsl:with-param name="titre" select="@nom"/>
                    </xsl:call-template>
                    <xsl:call-template name="titre">
                        <xsl:with-param name="id" select="@xmi:id"/>
                        <xsl:with-param name="titre" select="@nom"/>
                    </xsl:call-template>
                    <xsl:element name="fo:block">
                        <xsl:attribute name="space-after">
                            <xsl:text>20pt</xsl:text>
                        </xsl:attribute>
                        <xsl:value-of select="@description"/>
                    </xsl:element>
                    <xsl:call-template name="blockImage">
                        <xsl:with-param name="cheminImage" select="concat(@cheminGeneration,$fileSeparator,'main.png')"/>
                    </xsl:call-template>
                    <xsl:element name="fo:block">
                        <xsl:text>Diagramme d'assemblage des composants du processus</xsl:text>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- COMPOSANTS -->
    <xsl:template match="xmi:XMIPackage/xmi:XMI/iepp:composant">

      <!-- id du composant -->
      <xsl:variable name="idComposant">
        <xsl:value-of select="@xmi:id"/>
      </xsl:variable>

        <!-- Date dernière modification-->
        <xsl:variable name="dateGeneration">
            <xsl:variable name="date">
                <xsl:value-of select="@datePlacement"/>
            </xsl:variable>
            <xsl:value-of select="concat(substring($date,7,2),'/',substring($date,5,2),'/',substring($date,1,4))"/>
        </xsl:variable>

        <!-- chemin de l'icone du composant -->
        <xsl:variable name="cheminIconeComposant">
            <xsl:value-of select="concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminIcone,1) )"/>
        </xsl:variable>

        <!-- nom du composant -->
        <xsl:variable name="nomComposant">
            <xsl:value-of select="@nom"/>
        </xsl:variable>

        <!-- chemin des images au sein des contenus -->
        <xsl:variable name="cheminImageContenuComposant">
          <xsl:variable name="cheminComposant">
            <xsl:call-template name="replace-string">
              <xsl:with-param name="text">
                <xsl:value-of select="$nomComposant"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="concat($cheminGeneration,$fileSeparator,$cheminComposant,$fileSeparator,'contenu',$fileSeparator,'images')"/>
        </xsl:variable>

        <!-- Corps composant -->
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage">
                <xsl:with-param name="contenu">
                    <xsl:text>Dernière modification le :</xsl:text>
                    <xsl:value-of select="$dateGeneration"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>

                <!--signets -->
                <xsl:element name="fox:outline">
                    <xsl:attribute name="internal-destination">
                        <xsl:value-of select="@xmi:id"/>
                    </xsl:attribute>
                    <xsl:element name="fox:label">
                        <xsl:value-of select="@nom"/>
                    </xsl:element>
                    <xsl:call-template name="signet">
                        <xsl:with-param name="lien" select="concat('interfaces-',@xmi:id)"/>
                        <xsl:with-param name="titre" select="'Interfaces composant'"/>
                    </xsl:call-template>
                    <xsl:call-template name="signet">
                        <xsl:with-param name="lien" select="concat('workflow-',@xmi:id)"/>
                        <xsl:with-param name="titre" select="'Workflow composant'"/>
                    </xsl:call-template>
                    <xsl:for-each select="key('definitionTravail-index',iepp:definitionTravailId/@xmi:idref)">
                        <xsl:element name="fox:outline">
                            <xsl:attribute name="internal-destination">
                                <xsl:value-of select="@xmi:id"/>
                            </xsl:attribute>
                            <xsl:element name="fox:label">
                                <xsl:value-of select="@nom"/>
                            </xsl:element>
                            <xsl:call-template name="signet">
                                <xsl:with-param name="lien" select="concat('activite-',@xmi:id)"/>
                                <xsl:with-param name="titre" select="'Activités'"/>
                            </xsl:call-template>
                            <xsl:call-template name="signet">
                                <xsl:with-param name="lien" select="concat('workflow-',@xmi:id)"/>
                                <xsl:with-param name="titre" select="'Workflow'"/>
                            </xsl:call-template>
                            <xsl:for-each select="key('activite-index',iepp:activiteId/@xmi:idref)">
                              <xsl:call-template name="signet">
                                <xsl:with-param name="lien" select="@xmi:id"/>
                                <xsl:with-param name="titre" select="@nom"/>
                              </xsl:call-template>
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:for-each>

                    <!-- Signet Responsabilité produits -->
                    <xsl:for-each select="iepp:diagrammeResponsabilites">
                      <xsl:call-template name="signet">
                        <xsl:with-param name="lien" select="@cheminDiagramme"/>
                        <xsl:with-param name="titre" select="'Responsabilité produits'"/>
                      </xsl:call-template>
                    </xsl:for-each>

                    <!-- Signets Produits -->
                    <xsl:for-each select="key('produit-index',iepp:produitId/@xmi:idref)[iepp:elementPresentationId]">
                      <xsl:element name="fox:outline">
                        <xsl:attribute name="internal-destination">
                          <xsl:value-of select="concat($idComposant,@xmi:id)"/>
                        </xsl:attribute>
                        <xsl:element name="fox:label">
                          <xsl:value-of select="@nom"/>
                        </xsl:element>
                        <xsl:for-each select="key('guide-index',key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/iepp:guideId/@xmi:idref)">
                          <xsl:call-template name="signet">
                            <xsl:with-param name="lien" select="concat($idComposant,@xmi:id)"/>
                            <xsl:with-param name="titre" select="@nom"/>
                          </xsl:call-template>
                        </xsl:for-each>
                      </xsl:element>
                    </xsl:for-each>

                    <!-- signets roles -->
                    <xsl:for-each select="key('role-index',iepp:roleId/@xmi:idref)">
                      <xsl:call-template name="signet">
                        <xsl:with-param name="lien" select="@xmi:id"/>
                        <xsl:with-param name="titre" select="@nom"/>
                      </xsl:call-template>
                    </xsl:for-each>

                    <!-- signets guides composant -->
                    <xsl:for-each select="key('guide-index',key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/iepp:guideId/@xmi:idref)">
                      <xsl:call-template name="signet">
                        <xsl:with-param name="lien" select="@xmi:id"/>
                        <xsl:with-param name="titre" select="@nom"/>
                      </xsl:call-template>
                    </xsl:for-each>

                </xsl:element>

                <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="@xmi:id"/>
                    <xsl:with-param name="titre" select="@nom"/>
                </xsl:call-template>

                <!-- Barre navigation -->
                <xsl:element name="fo:block">
                    <xsl:attribute name="space-after">
                        <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:call-template name="image">
                        <xsl:with-param name="chemin" select="$cheminIconeComposant"/>
                    </xsl:call-template>
                    <xsl:value-of select="@nom"/>
                </xsl:element>

                <!-- Liste ROLES -->
                <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                        <xsl:text>ROLES :</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                    <xsl:text>2pt</xsl:text>
                  </xsl:attribute>
                    <xsl:attribute name="margin-left">
                        <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                        <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                        <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="iepp:roleId">
                        <xsl:element name="fo:block">
                            <xsl:value-of select="key('role-index',@xmi:idref)/@nom"/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>

                <!-- Liste PRODUITS -->
                <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                        <xsl:text>PRODUITS :</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:element name="fo:block">
                    <xsl:attribute name="space-after">
                        <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:element name="fo:table">
                        <xsl:attribute name="table-layout">
                            <xsl:text>fixed</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="margin-left">
                            <xsl:text>1cm</xsl:text>
                        </xsl:attribute>
                        <xsl:element name="fo:table-column">
                            <xsl:attribute name="column-width">
                                <xsl:text>6cm</xsl:text>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="fo:table-column">
                            <xsl:attribute name="column-width">
                                <xsl:text>6cm</xsl:text>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="fo:table-column">
                            <xsl:attribute name="column-width">
                                <xsl:text>6cm</xsl:text>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="fo:table-header">
                            <xsl:element name="fo:table-row">
                                <xsl:element name="fo:table-cell">
                                    <xsl:element name="fo:block">
                                        <xsl:call-template name="gras"/>
                                        <xsl:attribute name="border-left">
                                            <xsl:value-of select="$bordure"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="padding-left">
                                            <xsl:text>2pt</xsl:text>
                                        </xsl:attribute>
                                        <xsl:text>PRODUITS EN ENTREE</xsl:text>
                                    </xsl:element>
                                </xsl:element>
                                <xsl:element name="fo:table-cell">
                                    <xsl:element name="fo:block">
                                        <xsl:call-template name="gras"/>
                                        <xsl:attribute name="border-left">
                                            <xsl:value-of select="$bordure"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="padding-left">
                                          <xsl:text>2pt</xsl:text>
                                        </xsl:attribute>
                                        <xsl:text>PRODUITS INTERNES</xsl:text>
                                    </xsl:element>
                                </xsl:element>
                                <xsl:element name="fo:table-cell">
                                    <xsl:element name="fo:block">
                                        <xsl:call-template name="gras"/>
                                        <xsl:attribute name="border-left">
                                            <xsl:value-of select="$bordure"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="padding-left">
                                          <xsl:text>2pt</xsl:text>
                                        </xsl:attribute>
                                        <xsl:text>PRODUITS EN SORTIE</xsl:text>
                                    </xsl:element>
                                </xsl:element>
                            </xsl:element>
                        </xsl:element>
                        <xsl:element name="fo:table-body">
                            <xsl:element name="fo:table-row">
                                <!-- produit en entree -->
                                <xsl:element name="fo:table-cell">
                                    <xsl:element name="fo:block">
                                        <xsl:attribute name="border-left">
                                            <xsl:value-of select="$bordure"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="padding-left">
                                          <xsl:text>2pt</xsl:text>
                                        </xsl:attribute>
                                        <!-- si il y a une interface il y a un produit -->
                                        <xsl:if test="iepp:interfaceRequise">
                                            <xsl:for-each select="key('produit-index',key('interface-index',iepp:interfaceRequise/@xmi:idref)/iepp:interfaceProduit/@xmi:idref)">
                                                <xsl:value-of select="@nom"/>
                                                <xsl:element name="fo:block"/>
                                            </xsl:for-each>
                                        </xsl:if>
                                    </xsl:element>
                                </xsl:element>
                                <!-- produit interne -->
                                <xsl:element name="fo:table-cell">
                                    <xsl:element name="fo:block">
                                        <xsl:attribute name="border-left">
                                            <xsl:value-of select="$bordure"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="padding-left">
                                          <xsl:text>2pt</xsl:text>
                                        </xsl:attribute>
                                        <xsl:if test="iepp:produitId">
                                            <xsl:variable name="idInterfaceRequise">
                                                <xsl:value-of select="iepp:interfaceRequise/@xmi:idref"/>
                                            </xsl:variable>
                                            <xsl:variable name="idInterfaceFournie">
                                                <xsl:value-of select="iepp:interfaceFournie/@xmi:idref"/>
                                            </xsl:variable>
                                            <xsl:for-each select="key('produit-index',iepp:produitId/@xmi:idref)">
                                                <xsl:variable name="idProduit">
                                                    <xsl:value-of select="@xmi:id"/>
                                                </xsl:variable>
                                                <xsl:choose>
                                                    <xsl:when test="key('interface-index',$idInterfaceRequise)/*[@xmi:idref=$idProduit]">                                                    </xsl:when>
                                                    <xsl:when test="key('interface-index',$idInterfaceFournie)/*[@xmi:idref=$idProduit]">                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="@nom"/>
                                                        <xsl:element name="fo:block"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:for-each>
                                        </xsl:if>
                                    </xsl:element>
                                </xsl:element>
                                <!-- produit en sortie -->
                                <xsl:element name="fo:table-cell">
                                    <xsl:element name="fo:block">
                                        <xsl:attribute name="border-left">
                                            <xsl:value-of select="$bordure"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="padding-left">
                                          <xsl:text>2pt</xsl:text>
                                        </xsl:attribute>
                                        <xsl:if test="iepp:interfaceFournie">
                                            <xsl:for-each select="key('produit-index',key('interface-index',iepp:interfaceFournie/@xmi:idref)/iepp:interfaceProduit/@xmi:idref)">
                                                <xsl:value-of select="@nom"/>
                                                <xsl:element name="fo:block"/>
                                            </xsl:for-each>
                                        </xsl:if>
                                    </xsl:element>
                                </xsl:element>
                            </xsl:element>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>

                <!-- Liste DEFINITION DE TRAVAIL -->
                <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                        <xsl:text>DEFINITION DE TRAVAIL :</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:element name="fo:block">
                    <xsl:attribute name="margin-left">
                        <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                        <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                        <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="iepp:definitionTravailId">
                        <xsl:element name="fo:block">
                            <xsl:value-of select="key('definitionTravail-index',@xmi:idref)/@nom"/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>


                <!-- Liste Guides associes -->
                <xsl:call-template name="listeGuides"/>

                <!-- Contenu composant -->
                <xsl:element name="fo:block">
                    <xsl:attribute name="space-before">
                        <xsl:text>20pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-top">
                        <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="padding-top">
                        <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:apply-templates select="document(concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminPage,2)))//div[@class='contenu']"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>

        <!-- pages interfaces -->
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="concat('interfaces-',@xmi:id)"/>
                    <xsl:with-param name="titre" select="'Interfaces composant'"/>
                </xsl:call-template>
                <xsl:call-template name="blockImage">
                  <xsl:with-param name="cheminImage" select="concat($cheminGeneration,substring(@cheminDiagrammeInterfaces,1))"/>
                </xsl:call-template>
            </xsl:element>
        </xsl:element>

        <!-- page workflow -->
        <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="concat('workflow-',@xmi:id)"/>
                    <xsl:with-param name="titre" select="'Workflow composant'"/>
                </xsl:call-template>
                <xsl:call-template name="blockImage">
                  <xsl:with-param name="cheminImage" select="concat($cheminGeneration,substring(@cheminDiagrammeFlots,1))"/>
                </xsl:call-template>
            </xsl:element>
        </xsl:element>

        <!-- Traitement des definitions de travail associées au composant -->
        <xsl:for-each select="key('definitionTravail-index',iepp:definitionTravailId/@xmi:idref)">

            <!-- chemin de l'icone de la definitions de travail -->
            <xsl:variable name="cheminIconeDefinitionTravail">
                <xsl:value-of select="concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminIcone,1) )"/>
            </xsl:variable>

            <!-- nom de la definitions de travail -->
            <xsl:variable name="nomDefinitionTravail">
                <xsl:value-of select="@nom"/>
            </xsl:variable>

            <!-- début définition de travail -->
            <xsl:element name="fo:page-sequence">
                <xsl:attribute name="master-reference">A4</xsl:attribute>
                <xsl:call-template name="basPage"/>
                <xsl:element name="fo:flow">
                    <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                    <xsl:call-template name="titre">
                        <xsl:with-param name="id" select="@xmi:id"/>
                        <xsl:with-param name="titre" select="@nom"/>
                    </xsl:call-template>

                    <!-- Barre de navigation -->
                    <xsl:element name="fo:block">
                        <xsl:attribute name="space-after">
                            <xsl:text>12pt</xsl:text>
                        </xsl:attribute>
                        <xsl:call-template name="image">
                            <xsl:with-param name="chemin" select="$cheminIconeComposant"/>
                        </xsl:call-template>
                        <xsl:value-of select="$nomComposant"/>
                        <xsl:element name="fo:inline">
                            <xsl:text>>></xsl:text>
                        </xsl:element>
                        <xsl:call-template name="image">
                            <xsl:with-param name="chemin" select="$cheminIconeDefinitionTravail"/>
                        </xsl:call-template>
                        <xsl:value-of select="$nomDefinitionTravail"/>
                    </xsl:element>

                    <!-- Liste Activite -->
                    <xsl:call-template name="presentationListe">
                        <xsl:with-param name="titre">
                            <xsl:text>ACTIVITES :</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:element name="fo:block">
                        <xsl:attribute name="padding-left">
                          <xsl:text>2pt</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="margin-left">
                            <xsl:text>2.5cm</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="border-left">
                            <xsl:value-of select="$bordure"/>
                        </xsl:attribute>
                        <xsl:attribute name="space-after">
                            <xsl:text>12pt</xsl:text>
                        </xsl:attribute>
                        <xsl:for-each select="iepp:activiteId">
                            <xsl:element name="fo:block">
                                <xsl:value-of select="key('activite-index',@xmi:idref)/@nom"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>

                    <!-- Liste Guides associes -->
                    <xsl:call-template name="listeGuides"/>

                    <!-- Contenu definition de travail -->
                    <xsl:element name="fo:block">
                        <xsl:attribute name="space-before">
                            <xsl:text>20pt</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="border-top">
                            <xsl:value-of select="$bordure"/>
                        </xsl:attribute>
                        <xsl:attribute name="padding-top">
                            <xsl:text>12pt</xsl:text>
                        </xsl:attribute>
                        <xsl:apply-templates select="document(concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminPage,2)))//div[@class='contenu']"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>

            <!-- diagramme activite definitions de travail -->
            <xsl:element name="fo:page-sequence">
                <xsl:attribute name="master-reference">A4</xsl:attribute>
                <xsl:call-template name="basPage"/>
                <xsl:element name="fo:flow">
                    <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                    <xsl:call-template name="titre">
                        <xsl:with-param name="id" select="concat('activite-',@xmi:id)"/>
                        <xsl:with-param name="titre" select="'Activités'"/>
                    </xsl:call-template>
                    <xsl:call-template name="blockImage">
                        <xsl:with-param name="cheminImage" select="concat($cheminGeneration,substring(@cheminDiagrammeActivites,1))"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:element>

            <!-- diagramme workflow definitions de travail -->
            <xsl:element name="fo:page-sequence">
                <xsl:attribute name="master-reference">A4</xsl:attribute>
                <xsl:call-template name="basPage"/>
                <xsl:element name="fo:flow">
                    <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                    <xsl:call-template name="titre">
                        <xsl:with-param name="id" select="concat('workflow-',@xmi:id)"/>
                        <xsl:with-param name="titre" select="'Workflow'"/>
                    </xsl:call-template>
                    <xsl:call-template name="blockImage">
                      <xsl:with-param name="cheminImage" select="concat($cheminGeneration,substring(@cheminDiagrammeFlots,1))"/>
                    </xsl:call-template>
                </xsl:element>
            </xsl:element>

            <!-- Traitement des activites de la définition de travail -->
            <xsl:for-each select="key('activite-index',iepp:activiteId/@xmi:idref)">

              <!-- chemin de l'icone de la definitions de travail -->
              <xsl:variable name="cheminIconeActivite">
                <xsl:value-of select="concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminIcone,1) )"/>
              </xsl:variable>

              <!-- nom de l'activité -->
              <xsl:variable name="nomActivite">
                <xsl:value-of select="@nom"/>
              </xsl:variable>

              <!-- début activite -->
              <xsl:element name="fo:page-sequence">
                <xsl:attribute name="master-reference">A4</xsl:attribute>
                <xsl:call-template name="basPage"/>
                <xsl:element name="fo:flow">
                  <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                  <xsl:call-template name="titre">
                    <xsl:with-param name="id" select="@xmi:id"/>
                    <xsl:with-param name="titre" select="@nom"/>
                  </xsl:call-template>

                  <!-- Barre de navigation -->
                  <xsl:element name="fo:block">
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:call-template name="image">
                      <xsl:with-param name="chemin" select="$cheminIconeComposant"/>
                    </xsl:call-template>
                    <xsl:value-of select="$nomComposant"/>
                    <xsl:element name="fo:inline">
                      <xsl:text>>></xsl:text>
                    </xsl:element>
                    <xsl:call-template name="image">
                      <xsl:with-param name="chemin" select="$cheminIconeDefinitionTravail"/>
                    </xsl:call-template>
                    <xsl:value-of select="$nomDefinitionTravail"/>
                    <xsl:element name="fo:inline">
                      <xsl:text>>></xsl:text>
                    </xsl:element>
                    <xsl:call-template name="image">
                      <xsl:with-param name="chemin" select="$cheminIconeActivite"/>
                    </xsl:call-template>
                    <xsl:value-of select="$nomActivite"/>
                  </xsl:element>

                  <!-- Liste Produits en entrée -->
                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>PRODUITS EN ENTREE :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="iepp:entreeProduit">
                      <xsl:element name="fo:block">
                        <xsl:value-of select="key('produit-index',@xmi:idref)/@nom"/>
                      </xsl:element>
                    </xsl:for-each>
                  </xsl:element>

                  <!-- Liste Produits en sorite -->
                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>PRODUITS EN SORTIE :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="iepp:sortieProduit">
                      <xsl:element name="fo:block">
                        <xsl:value-of select="key('produit-index',@xmi:idref)/@nom"/>
                      </xsl:element>
                    </xsl:for-each>
                  </xsl:element>

                  <!-- Role responsable -->
                  <xsl:if test="iepp:participationRole">
                    <xsl:call-template name="presentationListe">
                      <xsl:with-param name="titre">
                        <xsl:text>ROLE RESPONSABLE :</xsl:text>
                      </xsl:with-param>
                    </xsl:call-template>
                    <xsl:element name="fo:block">
                      <xsl:attribute name="padding-left">
                        <xsl:text>2pt</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="margin-left">
                        <xsl:text>2.5cm</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="border-left">
                        <xsl:value-of select="$bordure"/>
                      </xsl:attribute>
                      <xsl:attribute name="space-after">
                        <xsl:text>12pt</xsl:text>
                      </xsl:attribute>
                      <xsl:element name="fo:block">
                        <xsl:value-of select="key('role-index',iepp:participationRole/@xmi:idref)/@nom"/>
                      </xsl:element>
                    </xsl:element>
                  </xsl:if>

                  <!-- Liste Guides associes -->
                  <xsl:call-template name="listeGuides"/>

                  <!-- Contenu activite -->
                  <xsl:element name="fo:block">
                    <xsl:attribute name="space-before">
                      <xsl:text>20pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-top">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="padding-top">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:apply-templates select="document(concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminPage,2)))//div[@class='contenu']"/>
                  </xsl:element>
                </xsl:element>
              </xsl:element>
            </xsl:for-each>

        </xsl:for-each>

         <!-- Responsabilité produits -->
         <xsl:for-each select="iepp:diagrammeResponsabilites">
           <xsl:element name="fo:page-sequence">
             <xsl:attribute name="master-reference">A4</xsl:attribute>
             <xsl:call-template name="basPage"/>
             <xsl:element name="fo:flow">
               <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
               <xsl:call-template name="titre">
                 <xsl:with-param name="id" select="@cheminDiagramme"/>
                 <xsl:with-param name="titre" select="'Responsabilité produits'"/>
               </xsl:call-template>
               <xsl:call-template name="blockImage">
                 <xsl:with-param name="cheminImage" select="concat($cheminGeneration,substring(@cheminDiagramme,1))"/>
               </xsl:call-template>
             </xsl:element>
           </xsl:element>
         </xsl:for-each>

        <!-- Produits -->
        <xsl:for-each select="key('produit-index',iepp:produitId/@xmi:idref)[iepp:elementPresentationId]">

          <!-- chemin de l'icone du produit -->
          <xsl:variable name="cheminIconeProduit">
            <xsl:value-of select="concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminIcone,1))"/>
          </xsl:variable>

          <!-- Nom du produit -->
          <xsl:variable name="nomProduit">
            <xsl:value-of select="@nom"/>
          </xsl:variable>

          <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
              <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
              <xsl:call-template name="titre">
                <xsl:with-param name="id" select="concat($idComposant,@xmi:id)"/>
                <xsl:with-param name="titre" select="@nom"/>
              </xsl:call-template>


               <!-- Barre de navigation -->
               <xsl:element name="fo:block">
                 <xsl:attribute name="space-after">
                   <xsl:text>12pt</xsl:text>
                 </xsl:attribute>
                 <xsl:call-template name="image">
                   <xsl:with-param name="chemin" select="$cheminIconeComposant"/>
                 </xsl:call-template>
                 <xsl:value-of select="$nomComposant"/>
                 <xsl:element name="fo:inline">
                   <xsl:text>>></xsl:text>
                 </xsl:element>
                 <xsl:call-template name="image">
                   <xsl:with-param name="chemin" select="$cheminIconeProduit"/>
                 </xsl:call-template>
                 <xsl:value-of select="$nomProduit"/>
               </xsl:element>

                  <!-- Liste en entrée activité -->
                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>EN ENTREE ACTIVITES :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="iepp:entreeActivite">
                      <xsl:element name="fo:block">
                        <xsl:value-of select="key('activite-index',@xmi:idref)/@nom"/>
                      </xsl:element>
                    </xsl:for-each>
                  </xsl:element>

                  <!-- Liste en sortie activité -->
                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>EN SORTIE ACTIVITES :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="iepp:sortieActivite">
                      <xsl:element name="fo:block">
                        <xsl:value-of select="key('activite-index',@xmi:idref)/@nom"/>
                      </xsl:element>
                    </xsl:for-each>
                  </xsl:element>

                  <!-- Role responsable -->
                  <xsl:if test="iepp:responsabiliteRole">
                    <xsl:call-template name="presentationListe">
                      <xsl:with-param name="titre">
                        <xsl:text>ROLE RESPONSABLE :</xsl:text>
                      </xsl:with-param>
                    </xsl:call-template>
                    <xsl:element name="fo:block">
                      <xsl:attribute name="padding-left">
                        <xsl:text>2pt</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="margin-left">
                        <xsl:text>2.5cm</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="border-left">
                        <xsl:value-of select="$bordure"/>
                      </xsl:attribute>
                      <xsl:attribute name="space-after">
                        <xsl:text>12pt</xsl:text>
                      </xsl:attribute>
                      <xsl:element name="fo:block">
                        <xsl:value-of select="key('role-index',iepp:responsabiliteRole/@xmi:idref)/@nom"/>
                      </xsl:element>
                    </xsl:element>
                  </xsl:if>

                  <!-- Liste Guides associes -->
                <xsl:call-template name="listeGuides"/>

                <!-- Contenu produit -->
                <xsl:element name="fo:block">
                    <xsl:attribute name="space-before">
                        <xsl:text>20pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-top">
                        <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="padding-top">
                        <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:apply-templates select="document(concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminPage,2)))//div[@class='contenu']"/>
                </xsl:element>

            </xsl:element>
          </xsl:element>

          <!-- Guide associés au produit -->
          <xsl:for-each select="key('guide-index',key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/iepp:guideId/@xmi:idref)">

            <!-- chemin de l'icone du guide -->
            <xsl:variable name="cheminIconeGuide">
              <xsl:value-of select="concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminIcone,1))"/>
            </xsl:variable>

            <!-- Nom du guide -->
            <xsl:variable name="nomGuide">
              <xsl:value-of select="@nom"/>
            </xsl:variable>

            <xsl:element name="fo:page-sequence">
              <xsl:attribute name="master-reference">A4</xsl:attribute>
              <xsl:call-template name="basPage"/>
              <xsl:element name="fo:flow">
                <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
                <xsl:call-template name="titre">
                  <xsl:with-param name="id" select="concat($idComposant,@xmi:id)"/>
                  <xsl:with-param name="titre" select="@nom"/>
                </xsl:call-template>

                <!-- Barre de navigation -->
                <xsl:element name="fo:block">
                  <xsl:attribute name="space-after">
                    <xsl:text>12pt</xsl:text>
                  </xsl:attribute>
                  <xsl:call-template name="image">
                    <xsl:with-param name="chemin" select="$cheminIconeComposant"/>
                  </xsl:call-template>
                  <xsl:value-of select="$nomComposant"/>
                  <xsl:element name="fo:inline">
                    <xsl:text>>></xsl:text>
                  </xsl:element>
                  <xsl:call-template name="image">
                    <xsl:with-param name="chemin" select="concat($cheminGeneration,'/applet/images/TreePackage.gif')"/>
                  </xsl:call-template>
                  <xsl:text>Produits</xsl:text>
                  <xsl:element name="fo:inline">
                    <xsl:text>>></xsl:text>
                  </xsl:element>
                  <xsl:call-template name="image">
                    <xsl:with-param name="chemin" select="$cheminIconeProduit"/>
                  </xsl:call-template>
                  <xsl:value-of select="$nomProduit"/>
                  <xsl:element name="fo:inline">
                    <xsl:text>>></xsl:text>
                  </xsl:element>
                  <xsl:call-template name="image">
                    <xsl:with-param name="chemin" select="$cheminIconeGuide"/>
                  </xsl:call-template>
                  <xsl:value-of select="$nomGuide"/>
                </xsl:element>

                <!-- Contenu guide-->
                <xsl:element name="fo:block">
                    <xsl:attribute name="space-before">
                        <xsl:text>20pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-top">
                        <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="padding-top">
                        <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:apply-templates select="document(concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminPage,2)))//div[@class='contenu']"/>
                </xsl:element>

              </xsl:element>
            </xsl:element>
          </xsl:for-each>

        </xsl:for-each>

        <!-- Role associes au composant -->
        <xsl:for-each select="key('role-index',iepp:roleId/@xmi:idref)">

          <!-- chemin de l'icone du role -->
          <xsl:variable name="cheminIconeRole">
            <xsl:value-of select="concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminIcone,1))"/>
          </xsl:variable>

          <!-- Nom du role -->
          <xsl:variable name="nomRole">
            <xsl:value-of select="@nom"/>
          </xsl:variable>

          <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
              <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
              <xsl:call-template name="titre">
                <xsl:with-param name="id" select="@xmi:id"/>
                <xsl:with-param name="titre" select="@nom"/>
              </xsl:call-template>


               <!-- Barre de navigation -->
               <xsl:element name="fo:block">
                 <xsl:attribute name="space-after">
                   <xsl:text>12pt</xsl:text>
                 </xsl:attribute>
                 <xsl:call-template name="image">
                   <xsl:with-param name="chemin" select="$cheminIconeComposant"/>
                 </xsl:call-template>
                 <xsl:value-of select="$nomComposant"/>
                 <xsl:element name="fo:inline">
                   <xsl:text>>></xsl:text>
                 </xsl:element>
                 <xsl:call-template name="image">
                   <xsl:with-param name="chemin" select="$cheminIconeRole"/>
                 </xsl:call-template>
                 <xsl:value-of select="$nomRole"/>
               </xsl:element>

                  <!-- Liste activités -->
                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>ACTIVITES :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="iepp:participationActivite">
                      <xsl:element name="fo:block">
                        <xsl:value-of select="key('activite-index',@xmi:idref)/@nom"/>
                      </xsl:element>
                    </xsl:for-each>
                  </xsl:element>

                   <!-- Liste produits -->
                  <xsl:call-template name="presentationListe">
                    <xsl:with-param name="titre">
                      <xsl:text>PRODUITS :</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:element name="fo:block">
                    <xsl:attribute name="padding-left">
                      <xsl:text>2pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="margin-left">
                      <xsl:text>2.5cm</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-left">
                      <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="space-after">
                      <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:for-each select="iepp:responsabiliteProduit">
                      <xsl:element name="fo:block">
                        <xsl:value-of select="key('produit-index',@xmi:idref)/@nom"/>
                      </xsl:element>
                    </xsl:for-each>
                  </xsl:element>

                  <!-- Liste Guides associes -->
                <xsl:call-template name="listeGuides"/>

                <!-- Contenu role -->
                <xsl:element name="fo:block">
                    <xsl:attribute name="space-before">
                        <xsl:text>20pt</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="border-top">
                        <xsl:value-of select="$bordure"/>
                    </xsl:attribute>
                    <xsl:attribute name="padding-top">
                        <xsl:text>12pt</xsl:text>
                    </xsl:attribute>
                    <xsl:apply-templates select="document(concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminPage,2)))//div[@class='contenu']"/>
                </xsl:element>

            </xsl:element>
          </xsl:element>
        </xsl:for-each>

        <!-- Guides associés au composant -->
        <xsl:for-each select="key('guide-index',key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/iepp:guideId/@xmi:idref)">

          <!-- chemin de l'icone du guide -->
          <xsl:variable name="cheminIconeGuide">
            <xsl:value-of select="concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminIcone,1))"/>
          </xsl:variable>

          <!-- Nom du guide -->
          <xsl:variable name="nomGuide">
            <xsl:value-of select="@nom"/>
          </xsl:variable>

          <xsl:element name="fo:page-sequence">
            <xsl:attribute name="master-reference">A4</xsl:attribute>
            <xsl:call-template name="basPage"/>
            <xsl:element name="fo:flow">
              <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
              <xsl:call-template name="titre">
                <xsl:with-param name="id" select="@xmi:id"/>
                <xsl:with-param name="titre" select="@nom"/>
              </xsl:call-template>

              <!-- Barre de navigation -->
              <xsl:element name="fo:block">
                <xsl:attribute name="space-after">
                  <xsl:text>12pt</xsl:text>
                </xsl:attribute>
                <xsl:call-template name="image">
                  <xsl:with-param name="chemin" select="$cheminIconeComposant"/>
                </xsl:call-template>
                <xsl:value-of select="$nomComposant"/>
                <xsl:element name="fo:inline">
                  <xsl:text>>></xsl:text>
                </xsl:element>
                <xsl:call-template name="image">
                  <xsl:with-param name="chemin" select="$cheminIconeGuide"/>
                </xsl:call-template>
                <xsl:value-of select="$nomGuide"/>
              </xsl:element>

              <!-- Contenu guide -->
              <xsl:element name="fo:block">
                <xsl:attribute name="space-before">
                  <xsl:text>20pt</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="border-top">
                  <xsl:value-of select="$bordure"/>
                </xsl:attribute>
                <xsl:attribute name="padding-top">
                  <xsl:text>12pt</xsl:text>
                </xsl:attribute>
                <xsl:apply-templates select="document(concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminPage,2)))//div[@class='contenu']"/>
              </xsl:element>

            </xsl:element>
          </xsl:element>
        </xsl:for-each>

    </xsl:template>

    <!-- Paquetages de présentation -->
    <xsl:template match="xmi:XMIPackage/xmi:XMI/iepp:paquetagePresentation">

      <!-- Variable id paquetage -->
      <xsl:variable name="idPaquetagePresentation">
        <xsl:value-of select="@xmi:id"/>
      </xsl:variable>

      <!-- Variables nom paquetage -->
      <xsl:variable name="nomPaquetagePresentation">
        <xsl:value-of select="@nom"/>
      </xsl:variable>

      <!-- Variables icone paquetage -->
      <xsl:variable name="cheminIconePaquetagePresentation">
        <xsl:value-of select="concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminIcone,1) )"/>
      </xsl:variable>

      <xsl:element name="fo:page-sequence">
        <xsl:attribute name="master-reference">A4</xsl:attribute>
        <xsl:call-template name="basPage">
          <xsl:with-param name="contenu">
            <xsl:text>Dernière modification le :</xsl:text>
            <xsl:value-of select="$dateGeneration"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:element name="fo:flow">
          <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>

          <!-- Signets Paquetage -->
          <xsl:element name="fox:outline">
            <xsl:attribute name="internal-destination">
              <xsl:value-of select="$idPaquetagePresentation"/>
            </xsl:attribute>
            <xsl:element name="fox:label">
              <xsl:value-of select="$nomPaquetagePresentation"/>
            </xsl:element>
            <xsl:for-each select="key('elementPresentation-index',iepp:agregeElementPresentation/@xmi:idref)">
              <xsl:call-template name="signet">
                <xsl:with-param name="lien" select="@xmi:id"/>
                <xsl:with-param name="titre" select="@nom"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:element>

          <xsl:call-template name="titre">
            <xsl:with-param name="id" select="$idPaquetagePresentation"/>
            <xsl:with-param name="titre" select="$nomPaquetagePresentation"/>
          </xsl:call-template>

          <!-- Barre navigation -->
          <xsl:element name="fo:block">
            <xsl:attribute name="space-after">
              <xsl:text>12pt</xsl:text>
            </xsl:attribute>
            <xsl:call-template name="image">
              <xsl:with-param name="chemin" select="$cheminIconePaquetagePresentation"/>
            </xsl:call-template>
            <xsl:value-of select="$nomPaquetagePresentation"/>
          </xsl:element>

          <!-- Liste Guides associes -->
          <xsl:call-template name="listeGuides"/>

          <!-- Contenu paquetage -->
          <xsl:element name="fo:block">
            <xsl:attribute name="space-before">
              <xsl:text>20pt</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="border-top">
              <xsl:value-of select="$bordure"/>
            </xsl:attribute>
            <xsl:attribute name="padding-top">
              <xsl:text>12pt</xsl:text>
            </xsl:attribute>

            <xsl:apply-templates select="document(concat($cheminGeneration,substring(key('elementPresentation-index',iepp:elementPresentationId/@xmi:idref)/@cheminPage,2)))//div[@class='contenu']"/>
          </xsl:element>

        </xsl:element>
      </xsl:element>

      <xsl:for-each select="key('elementPresentation-index',iepp:agregeElementPresentation/@xmi:idref)">

        <!-- Variables icone Presentation -->
        <xsl:variable name="cheminIconeElementPresentation">
          <xsl:value-of select="concat($cheminGeneration,substring(@cheminIcone,1) )"/>
        </xsl:variable>

        <xsl:element name="fo:page-sequence">
          <xsl:attribute name="master-reference">A4</xsl:attribute>
          <xsl:call-template name="basPage"/>
          <xsl:element name="fo:flow">
            <xsl:attribute name="flow-name">xsl-region-body</xsl:attribute>
            <xsl:call-template name="titre">
              <xsl:with-param name="id" select="@xmi:id"/>
              <xsl:with-param name="titre" select="@nom"/>
            </xsl:call-template>

            <!-- Barre navigation -->
            <xsl:element name="fo:block">
              <xsl:attribute name="space-after">
                <xsl:text>12pt</xsl:text>
              </xsl:attribute>
              <xsl:call-template name="image">
                <xsl:with-param name="chemin" select="$cheminIconePaquetagePresentation"/>
              </xsl:call-template>
              <xsl:value-of select="$nomPaquetagePresentation"/>
              <xsl:element name="fo:inline">
                <xsl:text>>></xsl:text>
              </xsl:element>
              <xsl:call-template name="image">
                <xsl:with-param name="chemin" select="$cheminIconeElementPresentation"/>
              </xsl:call-template>
              <xsl:value-of select="@nom"/>
            </xsl:element>

            <!-- Contenu element -->
            <xsl:element name="fo:block">
              <xsl:attribute name="space-before">
                <xsl:text>20pt</xsl:text>
              </xsl:attribute>
              <xsl:attribute name="border-top">
                <xsl:value-of select="$bordure"/>
              </xsl:attribute>
              <xsl:attribute name="padding-top">
                <xsl:text>12pt</xsl:text>
              </xsl:attribute>
              <xsl:apply-templates select="document(concat($cheminGeneration,substring(@cheminPage,2)))//div[@class='contenu']"/>
            </xsl:element>

          </xsl:element>
        </xsl:element>

      </xsl:for-each>

    </xsl:template>

</xsl:stylesheet>
