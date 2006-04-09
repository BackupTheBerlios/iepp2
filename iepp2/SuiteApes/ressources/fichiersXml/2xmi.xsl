<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:iepp="http://2xmi.free.fr"
    xmlns:xmi="http://www.omg.org/XMI">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <!-- Commun -->

    <xsl:template name="id">
        <xsl:attribute name="xmi:id">
            <xsl:value-of select="id"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="nom">
        <xsl:attribute name="nom">
            <xsl:value-of select="nom"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="nomAuteur">
        <xsl:attribute name="nomAuteur">
            <xsl:value-of select="nomAuteur"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="emailAuteur">
        <xsl:attribute name="emailAuteur">
            <xsl:value-of select="emailAuteur"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="description">
        <xsl:attribute name="description">
            <xsl:value-of select="description"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="reference">
        <xsl:attribute name="xmi:idref">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="elementPresentationId">
        <xsl:if test="text()">
            <xsl:element name="iepp:elementPresentationId">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="agregatComposant">
        <xsl:if test="text()">
            <xsl:element name="iepp:agregatComposant">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <!-- Racine -->

    <xsl:template match="/">
        <xsl:element name="xmi:XMIPackage">
            <xsl:attribute name="xsi:schemaLocation">http://www.omg.org/XMI xmi20.xsd http://2xmi.free.fr 2xmi.xsd</xsl:attribute>
            <xsl:element name="xmi:XMI">
                <xsl:attribute name="xmi:version">2.0</xsl:attribute>
                <xsl:apply-templates select="exportExecution/processus"/>
                <xsl:apply-templates select="exportExecution/liste_composant"/>
                <xsl:apply-templates select="exportExecution/liste_role"/>
                <xsl:apply-templates select="exportExecution/liste_produit"/>
                <xsl:apply-templates select="exportExecution/liste_definitionTravail"/>
                <xsl:apply-templates select="exportExecution/liste_activite"/>
                <xsl:apply-templates select="exportExecution/liste_interface"/>
                <xsl:apply-templates select="exportExecution/liste_typeProduit"/>
                <xsl:apply-templates select="exportExecution/liste_etat"/>
                <xsl:apply-templates select="exportExecution/liste_paquetagePresentation"/>
                <xsl:apply-templates select="exportExecution/liste_elementPresentation"/>
                <xsl:apply-templates select="exportExecution/liste_guide"/>
                <xsl:apply-templates select="exportExecution/liste_typeGuide"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- Processus -->

    <xsl:template match="exportExecution/processus">
        <xsl:element name="iepp:processus">
            <xsl:call-template name="id"/>
            <xsl:call-template name="nom"/>
            <xsl:call-template name="nomAuteur"/>
            <xsl:call-template name="emailAuteur"/>
            <xsl:call-template name="description"/>
            <xsl:attribute name="piedPage">
              <xsl:value-of select="piedPage"/>
            </xsl:attribute>
            <xsl:attribute name="cheminGeneration">
                <xsl:value-of select="cheminGeneration"/>
            </xsl:attribute>
            <xsl:attribute name="dateExport">
                <xsl:value-of select="dateExport"/>
            </xsl:attribute>
            <xsl:apply-templates select="liste_composantId"/>
            <xsl:apply-templates select="liste_paquetagePresentationId"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="liste_composantId">
        <xsl:for-each select="composantId">
            <xsl:if test="text()">
                <xsl:element name="iepp:composantId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_paquetagePresentationId">
        <xsl:for-each select="paquetagePresentationId">
            <xsl:if test="text()">
                <xsl:element name="iepp:paquetagePresentationId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Composant -->

    <xsl:template match="exportExecution/liste_composant">
        <xsl:for-each select="composant">
            <xsl:element name="iepp:composant">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
                <xsl:attribute name="version">
                    <xsl:value-of select="version"/>
                </xsl:attribute>
                <xsl:call-template name="nomAuteur"/>
                <xsl:call-template name="emailAuteur"/>
                <xsl:attribute name="datePlacement">
                    <xsl:value-of select="datePlacement"/>
                </xsl:attribute>
                <xsl:call-template name="description"/>
                <xsl:attribute name="cheminDiagrammeInterfaces">
                    <xsl:value-of select="cheminDiagrammeInterfaces"/>
                </xsl:attribute>
                <xsl:attribute name="cheminDiagrammeFlots">
                    <xsl:value-of select="cheminDiagrammeFlots"/>
                </xsl:attribute>
                <xsl:attribute name="ordreGeneration">
                    <xsl:value-of select="ordreGeneration"/>
                </xsl:attribute>
                <xsl:apply-templates select="liste_roleId"/>
                <xsl:apply-templates select="liste_produitId"/>
                <xsl:apply-templates select="liste_definitionTravailId"/>
                <xsl:apply-templates select="interfaceRequise"/>
                <xsl:apply-templates select="interfaceFournie"/>
                <xsl:apply-templates select="liste_cheminDiagrammeResponsabilites"/>
                <xsl:apply-templates select="elementPresentationId"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_roleId">
        <xsl:for-each select="roleId">
            <xsl:if test="text()">
                <xsl:element name="iepp:roleId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_produitId">
        <xsl:for-each select="produitId">
            <xsl:if test="text()">
                <xsl:element name="iepp:produitId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_definitionTravailId">
        <xsl:for-each select="definitionTravailId">
            <xsl:if test="text()">
                <xsl:element name="iepp:definitionTravailId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="interfaceRequise">
        <xsl:if test="text()">
            <xsl:element name="iepp:interfaceRequise">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="interfaceFournie">
        <xsl:if test="text()">
            <xsl:element name="iepp:interfaceFournie">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="liste_cheminDiagrammeResponsabilites">
      <xsl:for-each select="cheminDiagrammeResponsabilites">
        <xsl:if test="text()">
          <xsl:element name="iepp:diagrammeResponsabilites">
            <xsl:attribute name="cheminDiagramme">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </xsl:template>

    <!-- Role -->

    <xsl:template match="exportExecution/liste_role">
        <xsl:for-each select="role">
            <xsl:element name="iepp:role">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
                <xsl:apply-templates select="agregatComposant"/>
                <xsl:apply-templates select="liste_responsabiliteProduit"/>
                <xsl:apply-templates select="liste_participationActivite"/>
                <xsl:apply-templates select="elementPresentationId"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_responsabiliteProduit">
        <xsl:for-each select="responsabiliteProduit">
            <xsl:if test="text()">
                <xsl:element name="iepp:responsabiliteProduit">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_participationActivite">
        <xsl:for-each select="participationActivite">
            <xsl:if test="text()">
                <xsl:element name="iepp:participationActivite">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Produit -->

    <xsl:template match="exportExecution/liste_produit">
        <xsl:for-each select="produit">
            <xsl:element name="iepp:produit">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
                <xsl:apply-templates select="agregatComposant"/>
                <xsl:apply-templates select="responsabiliteRole"/>
                <xsl:apply-templates select="typeProduitId"/>
                <xsl:apply-templates select="liste_interfaceId"/>
                <xsl:apply-templates select="liste_etatId"/>
                <xsl:apply-templates select="liste_entreeActivite"/>
                <xsl:apply-templates select="liste_sortieActivite"/>
                <xsl:apply-templates select="elementPresentationId"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="responsabiliteRole">
        <xsl:if test="text()">
            <xsl:element name="iepp:responsabiliteRole">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="typeProduitId">
        <xsl:if test="text()">
            <xsl:element name="iepp:typeProduitId">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="liste_interfaceId">
        <xsl:for-each select="interfaceId">
            <xsl:if test="text()">
                <xsl:element name="iepp:interfaceId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_etatId">
        <xsl:for-each select="etatId">
            <xsl:if test="text()">
                <xsl:element name="iepp:etatId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_entreeActivite">
        <xsl:for-each select="entreeActivite">
            <xsl:if test="text()">
                <xsl:element name="iepp:entreeActivite">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_sortieActivite">
        <xsl:for-each select="sortieActivite">
            <xsl:if test="text()">
                <xsl:element name="iepp:sortieActivite">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>


    <!-- definitionTravail -->

    <xsl:template match="exportExecution/liste_definitionTravail">
        <xsl:for-each select="definitionTravail">
            <xsl:element name="iepp:definitionTravail">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
                <xsl:attribute name="cheminDiagrammeActivites">
                    <xsl:value-of select="cheminDiagrammeActivites"/>
                </xsl:attribute>
                <xsl:attribute name="cheminDiagrammeFlots">
                    <xsl:value-of select="cheminDiagrammeFlots"/>
                </xsl:attribute>
                <xsl:apply-templates select="agregatComposant"/>
                <xsl:apply-templates select="liste_activiteId"/>
                <xsl:apply-templates select="elementPresentationId"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_activiteId">
        <xsl:for-each select="activiteId">
            <xsl:if test="text()">
                <xsl:element name="iepp:activiteId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Activite -->

    <xsl:template match="exportExecution/liste_activite">
        <xsl:for-each select="activite">
            <xsl:element name="iepp:activite">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
                <xsl:apply-templates select="participationRole"/>
                <xsl:apply-templates select="agregatDefinitionTravail"/>
                <xsl:apply-templates select="liste_entreeProduit"/>
                <xsl:apply-templates select="liste_sortieProduit"/>
                <xsl:apply-templates select="elementPresentationId"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="participationRole">
        <xsl:if test="text()">
            <xsl:element name="iepp:participationRole">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="agregatDefinitionTravail">
        <xsl:if test="text()">
            <xsl:element name="iepp:agregatDefinitionTravail">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="liste_entreeProduit">
        <xsl:for-each select="entreeProduit">
            <xsl:if test="text()">
                <xsl:element name="iepp:entreeProduit">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_sortieProduit">
        <xsl:for-each select="sortieProduit">
            <xsl:if test="text()">
                <xsl:element name="iepp:sortieProduit">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Interface -->

    <xsl:template match="exportExecution/liste_interface">
        <xsl:for-each select="interface">
            <xsl:element name="iepp:interface">
                <xsl:call-template name="id"/>
                <xsl:apply-templates select="interfaceRequiseComposant"/>
                <xsl:apply-templates select="interfaceFournieComposant"/>
                <xsl:apply-templates select="liste_interfaceProduit"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="interfaceRequiseComposant">
        <xsl:if test="text()">
            <xsl:element name="iepp:interfaceRequiseComposant">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="interfaceFournieComposant">
        <xsl:if test="text()">
            <xsl:element name="iepp:interfaceFournieComposant">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="liste_interfaceProduit">
        <xsl:for-each select="interfaceProduit">
            <xsl:if test="text()">
                <xsl:element name="iepp:interfaceProduit">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Type Produit -->

    <xsl:template match="exportExecution/liste_typeProduit">
        <xsl:for-each select="typeProduit">
            <xsl:element name="iepp:typeProduit">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <!-- Etat -->

    <xsl:template match="exportExecution/liste_etat">
        <xsl:for-each select="etat">
            <xsl:element name="iepp:etat">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <!-- Paquetage Presentation -->

    <xsl:template match="exportExecution/liste_paquetagePresentation">
        <xsl:for-each select="paquetagePresentation">
            <xsl:element name="iepp:paquetagePresentation">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
                <xsl:attribute name="dossierIcone">
                    <xsl:value-of select="dossierIcone"/>
                </xsl:attribute>
                <xsl:attribute name="dossierContenu">
                    <xsl:value-of select="dossierContenu"/>
                </xsl:attribute>
                <xsl:attribute name="ordreGeneration">
                    <xsl:value-of select="ordreGeneration"/>
                </xsl:attribute>
                <xsl:apply-templates select="liste_agregeElementPresentation"/>
                <xsl:apply-templates select="elementPresentationId"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_agregeElementPresentation">
        <xsl:for-each select="agregeElementPresentation">
            <xsl:if test="text()">
                <xsl:element name="iepp:agregeElementPresentation">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Element Presentation -->

    <xsl:template match="exportExecution/liste_elementPresentation">
        <xsl:for-each select="elementPresentation">
            <xsl:element name="iepp:elementPresentation">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
                <xsl:attribute name="cheminIcone">
                    <xsl:value-of select="cheminIcone"/>
                </xsl:attribute>
                <xsl:attribute name="cheminContenu">
                    <xsl:value-of select="cheminContenu"/>
                </xsl:attribute>
                <xsl:call-template name="description"/>
                <xsl:attribute name="cheminPage">
                    <xsl:value-of select="cheminPage"/>
                </xsl:attribute>
                <xsl:apply-templates select="liste_guideId"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="liste_guideId">
        <xsl:for-each select="guideId">
            <xsl:if test="text()">
                <xsl:element name="iepp:guideId">
                    <xsl:call-template name="reference"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Guide -->

    <xsl:template match="exportExecution/liste_guide">
        <xsl:for-each select="guide">
            <xsl:element name="iepp:guide">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
                <xsl:apply-templates select="typeGuideId"/>
                <xsl:apply-templates select="elementPresentationId"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="typeGuideId">
        <xsl:if test="text()">
            <xsl:element name="iepp:typeGuideId">
                <xsl:call-template name="reference"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <!--Type Guide -->

    <xsl:template match="exportExecution/liste_typeGuide">
        <xsl:for-each select="typeGuide">
            <xsl:element name="iepp:typeGuide">
                <xsl:call-template name="id"/>
                <xsl:call-template name="nom"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
