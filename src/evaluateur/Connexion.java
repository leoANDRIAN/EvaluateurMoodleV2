package evaluateur;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Classe concr�te permettant de stocker les �l�ment n�c�ssaires � la connection � une base de donn�es grace a l'api JDBC
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public class Connexion { //Cette classe est configur�e pour se connecter au BD locales
	
	//Attributs
	/**
	 * Denomination du driver n�c�ssaire pour ce connecter � une BD pr�cise ex: pour mySQL = "com.mysql.jdbc.Driver"
	 */
	private String driver;
	
	/**
	 * l'URL de connection contenant le nom de la BD � laquelle on veut se connecter, son adresse, et son type ex : "jdbc:mysql://127.0.0.1:3306/nomBD"
	 */
	private String url;
	
	/**
	 * Nom de l'utilisateur autoris� � se connecter � la BD
	 */
	private String user;
	
	/**
	 * Mot de passe de l'utilisateur autoris� � se connecter � la BD
	 */
	private String pwd;
	
	/**
	 * Objet servant d'autorisation d'acc�s � la BD
	 * G�n�r� grace aux autre attributs
	 * 
	 */
	private Connection maConnexion;
	
	//Constructeurs
	/** 
     * Constructeur de la classe Connexion, permet de peupl� les attributs, load� le driver n�c�ssaire pour utiliser l'API JDBC et g�n�r� la connexion (cf l'attribut maConnexion)
     * Les BD sur lequelles le programme peut se connecter sont pr�d�finies (cf le corps du constructeur)
     * 
     * @param sgbd        type de la BD � laquelle on veut se connect�
     */
	public Connexion(String sgbd) {
		switch (sgbd) { //Chargement du driver adapt� et connexion au SGBD choisi par l'enseignant sur codeRunner
		  case "mySQL" :
			  driver = "com.mysql.jdbc.Driver";
			  url = "jdbc:mysql://127.0.0.1:3306/testevaluateur?autoReconnect=true&useSSL=false";
			  user = "root";
			  pwd = "root";
		    break;
		  case "oracle" : //Pas configur�
			  driver = "oracle.jdbc.driver.OracleDriver";
			  url = "jdbc:oracle:thin:@";
			  user = "root";
			  pwd = "";
		    break;
		  case "postgre" :
			  driver = "org.postgresql.Driver";
			  url = "jdbc:postgresql://127.0.0.1:5432/testevaluateur";
			  user = "root";
			  pwd = "root";
		    break;
		}
		try {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Impossible de charger le pilote");
            System.exit(1);
        }
		//System.out.println("Pilote charg�");
		
		try {
			maConnexion = DriverManager.getConnection(url,user,pwd);
		}
		catch (SQLException e) {
			System.out.println(e);
        	System.exit(1);
		}
		//System.out.println("Connect� � " + url);
	}
	
	//Methodes
	/** 
     * Getter de l'attribut maConnexion
     * 
     * @return renvoi un objet Connection configur� pour acc�der � une BD
     */
	public Connection getConnection() {
		return maConnexion;
	}
	
	/** 
     * Methode permettant de vider une BD mySQL
     * Cette methode permettra dans un futur proche de vider plusieurs types de BD
     * A utiliser apr�s avoir executer ses requete pour lib�rer la BD
     * 
     */
	public void deleteBD() {
		try {
			  Statement offConstraints = maConnexion.createStatement();
			  offConstraints.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
			  Statement getTables = maConnexion.createStatement();
			  ResultSet resultat = getTables.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'testevaluateur'");
			  while (resultat.next()) {
				  Statement delTable = maConnexion.createStatement();
				  delTable.executeUpdate("DROP TABLE IF EXISTS " + resultat.getString(1));
			  }
			  Statement onConstraints = maConnexion.createStatement();
			  onConstraints.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
		}
		catch (SQLException e) {
			System.out.println(e);
	    	System.exit(1);
		}
	}
	
	/** 
     * Methode permettant d'executer les commandes SQL contenu dans un fichier (.sql, .txt)
     * 
     * @param nomFichier        Chemin menant au fichier dont on veut executer le contenu ex: "/usr/local/createTable.txt"
     */
	public void executeSQLfile(String nomFichier) {
		int c;
		int back = 0;
		int cpt = 0;
		ArrayList<String> requetes = new ArrayList<String>();
		requetes.add("");
		try (FileInputStream in = new FileInputStream(nomFichier)) {
			c = in.read();
			while (c!=-1) { //Tant qu'il reste un character a lire dans le fichier
				if (c == 59) { //Si ";"
					//System.out.println(requetes.get(cpt)); //J'affiche la requete que l'on vient de d�limiter
					Statement stmtUpdate = maConnexion.createStatement();
					stmtUpdate.executeUpdate(requetes.get(cpt)); //On execute cette fameuse requete
					requetes.add(""); //Je cr�e une place pour la requete qui suit dans le fichier
					cpt++;
				}
				else if ((c == 32 && back == 32) || c == 10 || c == 13) { //Si double espace ou retour chariot linux/windows
					//Ne rien faire
				} else { //J'ajouter le character trouv� � la suite de la requete en train d'etre construite
					String temp = requetes.get(cpt);
					requetes.set(cpt, temp + Character.toString((char) c));
				}
				back = (char) c;
				c = in.read();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
