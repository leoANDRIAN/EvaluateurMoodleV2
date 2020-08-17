package evaluateur;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Classe concrète permettant de stocker les élément nécéssaires à la connection à une base de données grace a l'api JDBC
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public class Connexion { //Cette classe est configurï¿½e pour se connecter au BD locales
	
	//Attributs
	/**
	 * Denomination du driver nécéssaire pour ce connecter à une BD précise ex: pour mySQL = "com.mysql.jdbc.Driver"
	 */
	private String driver;
	
	/**
	 * l'URL de connection contenant le nom de la BD à laquelle on veut se connecter, son adresse, et son type ex : "jdbc:mysql://127.0.0.1:3306/nomBD"
	 */
	private String url;
	
	/**
	 * Nom de l'utilisateur autorisé à se connecter à la BD
	 */
	private String user;
	
	/**
	 * Mot de passe de l'utilisateur autorisé à se connecter à la BD
	 */
	private String pwd;
	
	/**
	 * Objet servant d'autorisation d'accès à la BD
	 * Généré grace aux autre attributs
	 * 
	 */
	private Connection maConnexion;
	
	//Constructeurs
	/** 
     * Constructeur de la classe Connexion, permet de peuplé les attributs, loadé le driver nécéssaire pour utiliser l'API JDBC et généré la connexion (cf l'attribut maConnexion)
     * Les BD sur lequelles le programme peut se connecter sont prédéfinies (cf le corps du constructeur)
     * 
     * @param sgbd        type de la BD à laquelle on veut se connecté
     */
	public Connexion(String sgbd) {
		switch (sgbd) { //Chargement du driver adaptï¿½ et connexion au SGBD choisi par l'enseignant sur codeRunner
		  case "mySQL" :
			  driver = "com.mysql.jdbc.Driver";
			  url = "jdbc:mysql://127.0.0.1:3306/testevaluateur?autoReconnect=true&useSSL=false";
			  user = "root";
			  pwd = "root";
		    break;
		  case "oracle" : //Pas configurï¿½
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
		//System.out.println("Pilote chargï¿½");
		
		try {
			maConnexion = DriverManager.getConnection(url,user,pwd);
		}
		catch (SQLException e) {
			System.out.println(e);
        	System.exit(1);
		}
		//System.out.println("Connectï¿½ ï¿½ " + url);
	}
	
	//Methodes
	/** 
     * Getter de l'attribut maConnexion
     * 
     * @return renvoi un objet Connection configuré pour accéder à une BD
     */
	public Connection getConnection() {
		return maConnexion;
	}
	
	/** 
     * Methode permettant de vider une BD mySQL
     * Cette methode permettra dans un futur proche de vider plusieurs types de BD
     * A utiliser après avoir executer ses requete pour libérer la BD
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
					//System.out.println(requetes.get(cpt)); //J'affiche la requete que l'on vient de dï¿½limiter
					Statement stmtUpdate = maConnexion.createStatement();
					stmtUpdate.executeUpdate(requetes.get(cpt)); //On execute cette fameuse requete
					requetes.add(""); //Je crï¿½e une place pour la requete qui suit dans le fichier
					cpt++;
				}
				else if ((c == 32 && back == 32) || c == 10 || c == 13) { //Si double espace ou retour chariot linux/windows
					//Ne rien faire
				} else { //J'ajouter le character trouvï¿½ ï¿½ la suite de la requete en train d'etre construite
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
