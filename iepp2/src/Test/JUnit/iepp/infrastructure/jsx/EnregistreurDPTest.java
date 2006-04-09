package Test.JUnit.iepp.infrastructure.jsx;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import iepp.Application;
import iepp.Projet;
import iepp.domaine.DefinitionProcessus;
import iepp.infrastructure.jsx.EnregistreurDP;
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.VueDPGraphe;
import junit.framework.TestCase;

public class EnregistreurDPTest extends TestCase {
private Application appli;
private DefinitionProcessus dp;
private Projet pc;
private Vector elts,liens,liste;
private int derIDused;
private HashMap hm;
private VueDPGraphe VDPGraph;
private FenetreEdition FE;
private ZipOutputStream zip;
private BufferedOutputStream buff;
private FileOutputStream outstream;

private OutputStream OS;
private EnregistreurDP EDP;
	protected void setUp() throws Exception {
		super.setUp();
		appli=Application.getApplication();
		hm = new HashMap(0);
		dp = new DefinitionProcessus();
		dp.setAuteur("Auteur");
		dp.setCommentaires("Commentaires");
		dp.setEmailAuteur("email");
		dp.setFicContenu("fichier");
		dp.setListeAGenerer(liste);
		dp.setListeAssociationSRole(hm);
		dp.setNomDefProc("nom");
		dp.setNomElement("chaine",1,2);
		dp.setPiedPage("piedpage");
		dp.setRepertoireGeneration("repgen");
		dp.setRepertoireGenerationNotChanged("repgenNC");
		FE= new FenetreEdition(VDPGraph);
		elts = new Vector(0);
		liens = new Vector(0);
		derIDused=0;
		pc = new Projet(dp,elts,liens,derIDused);
		pc.setFenetreEdition(FE);
		appli.setProjet(pc);
		VDPGraph=new VueDPGraphe(dp);
		outstream = new FileOutputStream( new File( "nomFichier") );
		buff = new BufferedOutputStream(outstream);
		zip = new ZipOutputStream( buff );
		EDP = new EnregistreurDP(zip);

		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'iepp.infrastructure.jsx.EnregistreurDP.sauver()'
	 */
	
	public void testSauver() throws IOException {
		
		EDP.sauver();
		assertEquals(EDP.getvdpg(),Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe());


	}

}
