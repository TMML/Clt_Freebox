/*CLIENT API FREEBOX
Décembre 2014 - FREEBOX V6 version 3.0.3
Écrit par Thomas Charlès - tomeli76@gmail.com*/

import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.*; 
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.table.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.*;
import java.util.Iterator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;
 
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Clt_Freebox_v2 extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private int i=0, j=0;
	private int hh=0, mm=0, ss=0, mois=0, jour=0, annee=0;

	private String retourDeLaPage = "", inter="", inter1="", track_id="", status="", permission="";
	private String hmac="";
	
	private static final String HMAC = "HmacSHA1";

	private String host = "http://mafreebox.freebox.fr/";

	private String appid = "nao_app";

	private String date1 = "", date2 = "", heure = "", dateappel="", heureappel="", type_appel="", id_appel="", numero="";
	private String challenge = "", valeur_token= "", session_token="";

	private Object[] ligne;

	JSONObject obj, obj2, obj3;
	
	private boolean etat = false, boucle=true, log_appel = false, validation = false;
	
	private JLabel L_CreaSession, L_Connect, L_DeConnect;
	private JButton JB_CreaSession, JB_Connect, JB_Deconnect, JB_LogAppel, JB_EtatFreebox;
	
	private JTextArea jTextAreaLogInput;
	private JScrollPane jScrollPaneLogInput;
	private JTextArea jTextAreaLogOutput;
	private JScrollPane jScrollPaneLogOutput;

	private Dimension screenSize;
	
	private Color fond = new Color(217,217,217);
	
	private ImageIcon icon;

	private Calendar now;

	private DefaultTableModel deftab = new DefaultTableModel();
	private JTable jt = new JTable(deftab);
	private JScrollPane js;

	public Clt_Freebox_v2()
	{
		setTitle("CLIENT API FREEBOX V3.0.3 - V1 - "+System.getProperty ( "os.name" ));
		
		//Détermine la résolution de l'écran et taille de la fenetre
   		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    	//setSize(screenSize.width-5, screenSize.height);
    		setSize(1200, 600);

    		getContentPane().setBackground(fond);
   		getContentPane().setLayout(null);
   		
		L_CreaSession = new JLabel("Creation APP",JLabel.LEFT);
   		L_CreaSession.setBounds(10,10,150,25);
   		getContentPane().add(L_CreaSession);
   	
   		JB_CreaSession = new JButton("FreeBox");
   		JB_CreaSession.setBounds(10,35,100,25);
   		JB_CreaSession.addActionListener(this);
   		getContentPane().add(JB_CreaSession);

   		L_Connect = new JLabel("Connexion FreeBox",JLabel.LEFT);
   		L_Connect.setBounds(10,70,150,25);
   		getContentPane().add(L_Connect);
   	
   		JB_Connect = new JButton(">> FreeBox");
   		JB_Connect.setBounds(10,95,100,25);
   		JB_Connect.addActionListener(this);
   		getContentPane().add(JB_Connect);

   		L_DeConnect = new JLabel("Deconnection FreeBox",JLabel.LEFT);
   		L_DeConnect.setBounds(10,130,150,25);
   		getContentPane().add(L_DeConnect);

   		JB_Deconnect = new JButton("<< FreeBox");
   		JB_Deconnect.setBounds(10,155,100,25);
   		JB_Deconnect.addActionListener(this);
   		getContentPane().add(JB_Deconnect);

   		JB_LogAppel = new JButton("Log Appel");
   		JB_LogAppel.setBounds(10,190,100,25);
   		JB_LogAppel.addActionListener(this);
   		getContentPane().add(JB_LogAppel);

   		JB_EtatFreebox = new JButton("Etat ADSL");
   		JB_EtatFreebox.setBounds(10,220,100,25);
   		JB_EtatFreebox.addActionListener(this);
   		getContentPane().add(JB_EtatFreebox);

   		jTextAreaLogInput = new JTextArea();
		jScrollPaneLogInput = new JScrollPane(jTextAreaLogInput);
		jTextAreaLogInput.setBounds(150,10,900,250);
		jScrollPaneLogInput.setBounds(150,10,900,250);
		getContentPane().add(jScrollPaneLogInput);

		deftab.addColumn("NUMERO");
		deftab.addColumn("TYPE");
		deftab.addColumn("ID");
   		deftab.addColumn("DATE");
   		deftab.addColumn("HEURE");

   		ligne = new Object[5];

		jt.setRowHeight(25);
	   	jt.setAutoResizeMode(4);
		js = new  JScrollPane(jt);
		js.setBounds(150,270, 900, 250);
		getContentPane().add(js);
		//jt.setBackground(new Color(255,255,225));
		js.getViewport().setBackground(jt.getBackground());

		Temps();

   		//Position de la fenetre au centre de l'écran       
    		Dimension frameSize = getSize();
    		if (frameSize.height > screenSize.height)
    		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
    		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
   		
   		addWindowListener(new Fermer());	//Ecouteur d'action au niveau de la fenetre
   		setResizable(false);
   		setVisible(true);			//Affichage de la fenetre
    		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE );
 	}
 	@SuppressWarnings(value = "unchecked")
 	public void Crea_Session()
 	{//Création Session sur FreeBox
		obj = new JSONObject();

		obj.put("app_id",appid);
		obj.put("app_name","Nao App");
		obj.put("app_version", "1.0.0");
		obj.put("device_name","Nao");

		retourDeLaPage = post(host+"api/v3/login/authorize/", obj);
		System.out.println("Retour3 : "+retourDeLaPage);
		Traitement_Json1(retourDeLaPage);

		getjTextAreaLogInput().setForeground(Color.red);
           getjTextAreaLogInput().append("ATTENTE VALIDATION SUR FEEBOX" + "\n");

           System.out.println("track_id : "+track_id);

		while(boucle)
		{
			retourDeLaPage = get(host+"api/v3/login/authorize/"+track_id);
			Traitement_Json2(retourDeLaPage);
			if(status.equals("granted"))
			{
				boucle = false;
				getjTextAreaLogInput().setForeground(Color.green);
           		getjTextAreaLogInput().append("OK VALIDATION SUR FREEBOX" + "\n");
			}
		}
		EcrireFichier(valeur_token);
 	}
 	@SuppressWarnings(value = "unchecked")
 	public void Connect()
	{//OUVRIR SESSION
		LireFichier();

		retourDeLaPage = get(host+"api/v3/login/");
		Traitement_Json2(retourDeLaPage);
		System.out.println("Retour2 : "+retourDeLaPage);
		System.out.println("challenge+Token : "+challenge+" - "+valeur_token);

		try
		{
			hmac = calcul_HMAC(challenge,valeur_token);	
		}
		catch(Exception e)
		{
			System.out.println("Exception hmac : "+e);
			new enr_exception(date1+"- hmac : "+e);
		}
		
		obj2 = new JSONObject();

		obj2.put("app_id",appid);
		obj2.put("password", hmac);
		
		retourDeLaPage = post(host+"api/v3/login/session/",obj2);
		System.out.println(retourDeLaPage);

		Traitement_Json3(retourDeLaPage);

		if (validation)
		{
			JB_Connect.setBackground(Color.green);
		}
		else
		{
			JB_Connect.setBackground(Color.red);	
		}
	}
	@SuppressWarnings(value = "unchecked")
	public void Deconnect()
	{//FERMER SESSION
		JB_Connect.setBackground(Color.red);	

		obj3 = new JSONObject();

		retourDeLaPage = post(host+"api/v3/login/logout/",obj3);
		System.out.println(retourDeLaPage);
	}
	public void EtatFreebox()
	{//Etat Connection
		retourDeLaPage = get(host+"api/v3/connection/");
		System.out.println("Retour : "+retourDeLaPage);
		Traitement_Json5(retourDeLaPage);
	}
	public void LogAppel()
	{//LISTE APPEL TELEPHONE
		log_appel = true;

		retourDeLaPage = get(host+"api/v3/call/log/");
		//System.out.println("Retour : "+retourDeLaPage);
		Traitement_Json4(retourDeLaPage);
	}
	public String get(String url)
	{
 		String source ="";

 		try
 		{
			URL url1 = new URL(url);
			URLConnection urlc = url1.openConnection();
			urlc.setRequestProperty("X-Fbx-App-Auth", session_token);

			BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
			String inputLine;
	 		
			while ((inputLine = in.readLine()) != null)
			{
				source +=inputLine;
				//System.out.println("source = "+source);
			}
			in.close();	
		}
		catch(Exception e)
		{
			System.out.println("GET : "+e+" "+source);
			new enr_exception(date1+"- GET : "+e+" "+source);
		}

		return source;
	}
	public String post(String adress, JSONObject valeur)
	{
		String result = "";
		OutputStreamWriter writer = null;
		BufferedReader reader = null;

		try
		{
			//création de la connection
			URL url = new URL(adress);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
 			
			//envoi de la requête
			writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(valeur.toJSONString());
			writer.flush();

			//lecture de la réponse
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String ligne;
			while ((ligne = reader.readLine()) != null)
			{
				result+=ligne;
			}
		}
		catch (Exception e)
		{
			System.out.println("POST : "+e);
			new enr_exception(date1+" - POST : "+e+" - "+result);
		}
		finally
		{
			try{writer.close();}catch(Exception e){System.out.println("W : "+e);new enr_exception(date1+"- W : "+e);}
			try{reader.close();}catch(Exception e){System.out.println("R "+e);new enr_exception(date1+"- R : "+e);}
		}

		return result;
	}
	public void Traitement_Json1(String retour_json1)
	{//Traitement création APP
		try
		{
			JSONParser parser=new JSONParser();

			JSONObject jsonObject = (JSONObject) parser.parse(retour_json1);

			validation = (Boolean) jsonObject.get("success");
			//System.out.println("success: " + validation);

			JSONObject structure = (JSONObject) jsonObject.get("result");
			valeur_token = (String) structure.get("app_token");
			valeur_token = nettoyer(valeur_token);
			//System.out.println("result: " + valeur_token);

			long id =  (long) structure.get("track_id");
			track_id = ""+id;
			//System.out.println("result2: " + track_id);

			getjTextAreaLogInput().setForeground(Color.black);
           	getjTextAreaLogInput().append(validation+" - "+valeur_token+ "\n");
           	getjTextAreaLogInput().append(track_id+ "\n");
		}
		catch (Exception e)
		{
			System.out.println("Traitement_Json1 : "+e);
			new enr_exception(date1+"- Traitement_Json1 : "+e);
		}
	}
	public void Traitement_Json2(String retour_json2)
	{//Recupératio du challange et du status
		try
		{
			JSONParser parser=new JSONParser();

			JSONObject jsonObject = (JSONObject) parser.parse(retour_json2);

			validation = (Boolean) jsonObject.get("success");
			//System.out.println("success: " + validation);

			JSONObject structure = (JSONObject) jsonObject.get("result");
			status = (String) structure.get("status");
			challenge = (String) structure.get("challenge");
			challenge = nettoyer(challenge);
			//System.out.println("result: " + status);
			//System.out.println("result: " + challenge);
		} 
		catch (Exception e)
		{
			System.out.println("Traitement_Json2 : "+e);
			new enr_exception(date1+"- Traitement_Json2 : "+e);
		}
	}
	public void Traitement_Json3(String retour_json3)
	{//Traitement Création session
		try
		{
			JSONParser parser=new JSONParser();

			JSONObject jsonObject = (JSONObject) parser.parse(retour_json3);

			validation = (Boolean) jsonObject.get("success");
			//System.out.println("success: " + validation);

			JSONObject structure = (JSONObject) jsonObject.get("result");
			session_token = (String) structure.get("session_token");
			session_token = nettoyer(session_token);

			Object permissions = structure.get("permissions");

			getjTextAreaLogInput().setForeground(Color.black);
           	getjTextAreaLogInput().append("session_token : "+session_token+ "\n");
           	getjTextAreaLogInput().append("permission : "+permissions+ "\n");
			
		} 
		catch (Exception e)
		{
			System.out.println("Traitement_Json3 : "+e);
			new enr_exception(date1+"- Traitement_Json3 : "+e);
		}
	}
	@SuppressWarnings(value = "rawtypes")
	public void Traitement_Json4(String retour_json4)
	{//Traitement API Appel
		try
		{
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(retour_json4);

            	Boolean firstName = (Boolean) jsonObject.get("success");
            	System.out.println("success: " + firstName);

            	JSONArray lang= (JSONArray) jsonObject.get("result");
            	Iterator i = lang.iterator();

            	while (i.hasNext())
            	{
            		JSONObject innerObj = (JSONObject) i.next();
            		//System.out.println("result "+ innerObj.get("number") + " - " + innerObj.get("datetime") + " - " + innerObj.get("type"));
            		ligne[0] = innerObj.get("number");
            		ligne[1] = innerObj.get("type");
            		ligne[2] = innerObj.get("id");
            		ligne[3] = convertdate((Long) innerObj.get("datetime"));
				ligne[4] = convertheure((Long) innerObj.get("datetime"));
				deftab.addRow(ligne);
            	}
           }
		catch (Exception e)
		{
			System.out.println("Traitement_Json4 : "+e);
			new enr_exception(date1+"- Traitement_Json4 : "+e);
		}
	}
	public void Traitement_Json5(String retour_json5)
	{//Traitement API Connexion
		try
		{
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(retour_json5);

            	Boolean firstName = (Boolean) jsonObject.get("success");
            	System.out.println("success: " + firstName);

            	JSONObject structure = (JSONObject) jsonObject.get("result");
			System.out.println("Type : "+ structure.get("type"));
			System.out.println("State : "+ structure.get("state"));
           }
		catch (Exception e)
		{
			System.out.println("Traitement_Json5 : "+e);
			new enr_exception(date1+"- Traitement_Json5 : "+e);
		}
	}
	private String toHexString(byte[] bytes)
	{
		Formatter formatter = new Formatter();
		
		for (byte b : bytes)
		{
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
	public String calcul_HMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	{//Data Challenge - key valeur token
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC);
			Mac mac = Mac.getInstance(HMAC);
			mac.init(signingKey);
			
			return toHexString(mac.doFinal(data.getBytes()));
	}
	public void LireFichier()
	{
		try
		{
			Scanner scanner = new Scanner(new FileReader("C:/token.txt"));
 			valeur_token = scanner.nextLine();
		}
		catch(Exception e)
		{
			System.out.println("Impossible de lire le fichier");
			new enr_exception(date1+"- Impossible de lire le fichier");
		}
	}
	public void EcrireFichier(String texte)
	{
		FileWriter writer = null;
		
		try
		{
     			writer = new FileWriter("C:/token.txt", true);
     			writer.write(texte,0,texte.length());
		}
		catch(Exception ex)
		{
    			System.out.println("EcrireFichier : "+ex);
    			new enr_exception(date1+"- EcrireFichier : "+ex);
		}
		finally
		{
  			if(writer != null)
  			{
     				try
     				{
     					writer.close();	
     				}
     				catch(Exception e)
     				{
     					System.out.println(e);
     					new enr_exception(date1+"- EcrireFichier : "+e);
     				}
  			}
		}
	}
	public String nettoyer(String clean)
	{//Retire les antislash du token
 		String ok_clean = clean.replaceAll("\\\\","");

 		return ok_clean;
	}
	public String convertdate(long date_appel)
	{
		String date_convert="";

		try
		{
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(date_appel * 1000);
			date_convert = formatter.format(calendar.getTime());	
		}
		catch(Exception e)
		{
			System.out.println("convertdate : "+e);
			new enr_exception(date1+"- convertdate : "+e);
		}

		return date_convert;
	}
	public String convertheure(long heure_appel)
	{
		String heure_convert="";

		try
		{
			DateFormat formatter1 = new SimpleDateFormat("HH:mm");
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTimeInMillis(heure_appel * 1000);
			heure_convert = formatter1.format(calendar1.getTime());	
		}
		catch(Exception e)
		{
			System.out.println("heuredate : "+e);
			new enr_exception(date1+"- heuredate : "+e);
		}

		return heure_convert;
	}
	public JTextArea getjTextAreaLogInput()
	{
		return jTextAreaLogInput;
	}
	public void tempo(int temps)
	{
		try
		{
			Thread.sleep(temps);	
		}
		catch(Exception e)
		{
			System.out.println("Tempo : "+e);
			new enr_exception(date1+"- Tempo : "+e);
		}
	}
  	public void actionPerformed(ActionEvent ae)
	{
			Object source1 = ae.getSource();

			if (source1 == JB_CreaSession)
			{		
				Crea_Session();
			}
			if (source1 == JB_Connect)
			{
				Connect();
			}
			if (source1 == JB_Deconnect)
			{
				Deconnect();
			}
			if (source1 == JB_LogAppel)
			{
				LogAppel();
			}
			if (source1 == JB_EtatFreebox)
			{
				EtatFreebox();
			}
	}
	public void Temps()
	{
		now = Calendar.getInstance();
		
		mois = now.get(Calendar.MONTH) +  1;         
		jour= now.get(Calendar.DAY_OF_MONTH);         
		annee = now.get(Calendar.YEAR);
		
		date1 = Integer.toString(jour)+"/"+Integer.toString(mois)+"/"+Integer.toString(annee);
		
		hh = now.get(Calendar.HOUR_OF_DAY);         
		mm = now.get(Calendar.MINUTE);         
		//ss = now.get(Calendar.SECOND);
		
		heure = Integer.toString(hh)+":"+Integer.toString(mm); //+":"+Integer.toString(ss);
	}
	public static void main(String [] args)
	{
		new Clt_Freebox_v2();
	}
class Info extends JFrame 
{
	private static final long serialVersionUID = 1L;
	int count=0;
	
	public Info(String titre, String message, int qmessage, int option)
	{
		JOptionPane pane = new JOptionPane(message, qmessage, option );
		JDialog dial = pane.createDialog(null, titre);
    		dial.setVisible(true);
		Object selectedValue = pane.getValue();
     	     	
		if(selectedValue instanceof Integer)
		{
			count = ((Integer)selectedValue).intValue();
			if(count == 0)
			{	
				etat=true;
			}
			else
			{
				etat=false;
			}
		}
	}
}
class Fermer extends WindowAdapter
{
	private static final long serialVersionUID = 1L;

	public void windowClosing(WindowEvent we)
	{
		new Info("Quitter", "Voulez vous quitter?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
		if (etat)
		{
			etat = false;
			System.exit(0);
		}
	}
}
class enr_exception
{//Enregistre les exceptions dans un fichier log
	private static final long serialVersionUID = 1L;

	public enr_exception(String erreur)
	{
		FileWriter writer = null;
		
		try
		{
     			writer = new FileWriter("C:/clt_freebox.log", true);
     			writer.write(erreur,0,erreur.length());
     			writer.write("\r\n");
		}
		catch(Exception ex)
		{
    			new Info("Enregistrement Exception", "Problème enregistrement dans fichier : "+ex, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION);
		}
		finally
		{
  			if(writer != null)
  			{
     				try
     				{
     					writer.close();	
     				}
     				catch(Exception ex1)
     				{
     					new Info("Enregistrement Exception", "Problème enregistrement dans fichier : "+ex1, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION);
     				}
  			}
		}
	}
}
}