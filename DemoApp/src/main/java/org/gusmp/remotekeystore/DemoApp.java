package org.gusmp.remotekeystore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PrivateKeySignature;

public class DemoApp {

	public static void main(String[] args) {
		
		String KEYSTORE = "./certificates/CDA-1_00_12345.p12";
		String PASSWORD = "12345";
		String ALIAS = "alias_cda";
		
		String FILE_PDF = "documentTest.pdf";
		String FILE_PDF_DEST = "documentTest_signed_";
		
		
		System.out.println("DemoApp");
		
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			
			FILE_PDF_DEST = FILE_PDF_DEST + sdf.format(new Date()) + ".pdf";
			
			BouncyCastleProvider provider = new BouncyCastleProvider();
			Security.addProvider(provider);
			
			RemoteKeyStore ks = RemoteKeyStore.getInstance(RemoteKeyStore.getDefaultType());
			//KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(new FileInputStream(KEYSTORE), PASSWORD.toCharArray());
	        //String alias = (String)ks.aliases().nextElement();
	        PrivateKey pk = (PrivateKey) ks.getKey(ALIAS, PASSWORD.toCharArray());
	        Certificate[] chain = ks.getCertificateChain(ALIAS);
	        
	        // Creating the reader and the stamper
	        PdfReader reader = new PdfReader(FILE_PDF);
	        FileOutputStream os = new FileOutputStream(FILE_PDF_DEST);
	        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
	        // Creating the appearance
	        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
	        
	        
	        X509Certificate certificate = (X509Certificate) ks.getCertificate(ALIAS);
	        appearance.setSignatureCreator(certificate.getSubjectDN().getName());
	        appearance.setReason(certificate.getSubjectDN().getName());
	        appearance.setLocation("Barcelona");
	        appearance.setVisibleSignature(new Rectangle(250, 748, 500, 820), 1, "sig");
	        // Creating the signature
	        ExternalDigest digest = new BouncyCastleDigest();
	        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider.getName());
	        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, CryptoStandard.CMS);
	        
	        System.out.println("Document signat: " + FILE_PDF_DEST);
	        
		} catch(Exception exc) {
			System.out.println("Error: " + exc.toString());
		}
	}
}
