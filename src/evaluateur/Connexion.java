package evaluateur;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class Connexion { //Cette classe est configurée pour se connecter au BD locales
	
	//Attributs
	private String driver;
	private String url;
	private String user;
	private String pwd;
	private Connection maConnexion;
	
	//Constructeurs
	public Connexion(String sgbd) {
		switch (sgbd) { //Chargement du driver adapté et connexion au SGBD choisi par l'enseignant sur codeRunner
		  case "mySQL" :
			  driver = "com.mysql.jdbc.Driver";
			  url = "jdbc:mysql://localhost:3306/testevaluateur";
			  user = "root";
			  pwd = "";
		    break;
		  case "oracle" : //Pas configuré
			  driver = "oracle.jdbc.driver.OracleDriver";
			  url = "jdbc:oracle:thin:@";
			  user = "root";
			  pwd = "";
		    break;
		  case "postgre" : //Pas configuré
			  driver = "org.postgresql.Driver";
			  url = "jdbc:postgresql://";
			  user = "root";
			  pwd = "";
		    break;
		}
		try {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Impossible de charger le pilote");
            System.exit(1);
        }
		System.out.println("Pilote chargé");
		
		try {
			maConnexion = DriverManager.getConnection(url,user,pwd);
		}
		catch (SQLException e) {
			System.out.println("Impossible de se connecter à la base de données");
        	System.exit(1);
		}
		System.out.println("Connecté à " + url);
	}
	
	//Methodes
	public Connection getConnection() {
		return maConnexion;
	}
	
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
	
	public void executeSQLfile(String nomFichier) {
		int c;
		int back = 0;
		int cpt = 0;
		ArrayList<String> requetes = new ArrayList<String>();
		requetes.add("");
		try (FileReader in = new FileReader(nomFichier)) {
			c = in.read();
			while (c!=-1) { //Tant qu'il reste un character a lire dans le fichier
				if (c == 59) { //Si ";"
					System.out.println(requetes.get(cpt)); //J'affiche la requete que l'on vient de délimiter
					Statement stmtUpdate = maConnexion.createStatement();
					stmtUpdate.executeUpdate(requetes.get(cpt)); //On execute cette fameuse requete
					requetes.add(""); //Je crée une place pour la requete qui suit dans le fichier
					cpt++;
				}
				else if ((c == 32 && back == 32) || c == 10 || c == 13) { //Si double espace ou retour chariot linux/windows
					//Ne rien faire
				} else { //J'ajouter le character trouvé à la suite de la requete en train d'etre construite
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
