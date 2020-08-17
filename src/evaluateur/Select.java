package evaluateur;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Classe concrete permettant de traiter une requete SELECT :
 * Standardisation puis analyse de la synthaxe de la requete
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public class Select extends QueryResult { //Crï¿½er une rï¿½ponse ï¿½ partir d'un SELECT
	
	/**
	 * Tableau contenant les arguments suivant le mot SELECT jusqu'a FROM
	 */
	private ArrayList<String> champs = new ArrayList<String>();
	
	/**
	 * Tableau contenant les arguments suivant le mot FROM jusqu'a WHERE
	 */
	private ArrayList<String> tables = new ArrayList<String>();
	
	/**
	 * Tableau contenant les arguments suivant le mot WHERE jusqu'a ";"
	 */
	private ArrayList<String> conditions = new ArrayList<String>();
	

	//TO-DO : amï¿½liorer l'analyse du select (natural join, select dans select...)
	
	/** 
     * Constructeur de la classe SELECT, va permettre de peupler les attributs de la classe (cf attributs), standardiser et anlayser la requete et enfin executer la requete
     * 
     * @param requete        la requete (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param nomFichier        la nom du fichier (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param connexion        la connexion (String) nécéssaire à l'execution des requetes SQL
     */
	public Select(String requete, String nomFichier, Connexion connexion) {
		//TRANSFORMATION DU RESULTSET EN ARRAY
		super(requete, nomFichier);
		//Standardisation de la requete
		String standardised = "";
		int currentChar;
		int lastChar = 0;
		for (int i = 0; i < this.requete.length(); i++) {
			currentChar = this.requete.charAt(i);
			if (i == this.requete.length()-1) {
				standardised += " " + ((char) currentChar);
			} else if (currentChar == 44) {
				standardised += " ";
				lastChar = 32;
				continue;
			} else if (currentChar == 32 && lastChar == 32) {
			} else {
				standardised += ((char) currentChar);
			}
			lastChar = currentChar;
		}
		this.cleanRequete = standardised.toUpperCase();
		//System.out.println("Standard : " + this.cleanRequete);
		
		String[] requeteSplit = this.cleanRequete.split(" ");
		
		//ancien getSelectItems(String[] requeteSplit, ArrayList<String> array);
		int fromPos = 0; //Va permettre de rï¿½cupï¿½rer la position du from dans la requete pour commencer a partir de cette position dans le methode suivante (optimisation nb calcul)
		for (int i = 1; i < requeteSplit.length; i++) {
			if (requeteSplit[i].equals("FROM")) {
				fromPos = i;
				break;
			} else {
				champs.add(requeteSplit[i]);
			}
		}
		
		//ancien getFromItems(int fromPos, String[] requeteSplit, ArrayList<String> array)
		int wherePos = 0;
		for (int i = fromPos + 1; i < requeteSplit.length; i++) {
			if (requeteSplit[i].equals("WHERE")) {
				wherePos = i;
				break;
			} else {
				tables.add(requeteSplit[i]);
			}
		}

		//ancien getWhereItems(int wherePos, String[] requeteSplit, ArrayList<String> array)
		String currentItem = ""; //Permet d'accumuler les elements sï¿½parï¿½s par un espace : age > 40 --> age>40;
		for (int i = wherePos + 1; i < requeteSplit.length; i++) {
			if (requeteSplit[i].equals("AND")) { //Si on tombe sur AND on ajoute l'argument prï¿½cï¿½dant dans l'array et on prepare currentItem pour le prochain argument
				conditions.add(currentItem);
				currentItem = "";
			} else if (requeteSplit[i].equals(";")) { //On est arrivï¿½ au bout de la requete (on l'aura sï¿½parï¿½ du reste de la requete pendant la phase de standardisation)
				conditions.add(currentItem);
				break;
			} else {
				currentItem += requeteSplit[i]; //On accumule les morceaux de chaine dans currentItem;
			}
		}
		
		//Rï¿½cupï¿½ration du resultat de la requete
		connexion.executeSQLfile(this.nomFichier);
		try {
			Statement stmt = connexion.getConnection().createStatement();
			ResultSet resultat = stmt.executeQuery(requete); //Requete "FIXE"
			int nbCol = resultat.getMetaData().getColumnCount(); //Recupere nb colonne du resultat de la requete
			resRequete = new ArrayList<ArrayList<Object>>();
			
			while(resultat.next()) {
				//Une ligne doit pouvoir stocker diffï¿½rent types car une requete peut renvoyer plusieurs types de donnï¿½es (on utilise donc Object)
				ArrayList<Object> ligne = new ArrayList<Object>();
				for (int i = 1; i < nbCol; i++) { //Pour chaque colonne
					ligne.add(resultat.getString(i)); //On ajoute ï¿½ la ligne la valeur de la colonne
				}
				resRequete.add(ligne);
			}
			stmt.close();
		}
		catch (SQLException e) {
			System.out.println(e);
	    	System.exit(1);
		}
		connexion.deleteBD();
	}
	
	/** 
     * Getter de l'attribut champs
     * 
     * @return renvoi le tableau contenu dans champs
     */
	public ArrayList<String> getChamps() {
		return champs;
	}

	/** 
     * Getter de l'attribut tables
     * 
     * @return renvoi le tableau contenu dans tables
     */
	public ArrayList<String> getTables() {
		return tables;
	}

	/** 
     * Getter de l'attribut conditions
     * 
     * @return renvoi le tableau contenu dans conditions
     */
	public ArrayList<String> getConditions() {
		return conditions;
	}
	
	/** 
     * Methode permettant de comparer la synthaxe entre les requetes de deux objets Select.
     * Cette methode doit etre implémentée par chaque classe concrète héritant de Reponse.
     * Compare les tableaux (attributs) des deux objets.
     * 
     * @param reponse        Autre objet reponse avec lequel on veut comparer notre cible
     */
	public void compareSyntaxe(Reponse reponse) {
		//Comparaison des champs
		if (champs.size()>((Select) reponse).getChamps().size()) {
			System.out.println("Vous avez indiquez plus de champs que nï¿½cï¿½ssaires");
		} else if (champs.size()<((Select) reponse).getChamps().size()) {
			System.out.println("Vous avez indiquez moins de champs que nï¿½cï¿½ssaire");
		}
		for (String item : champs) {
			if (!((Select) reponse).getChamps().contains(item)) {
				System.out.println(item + " n'apparait pas dans la rï¿½ponse du prof");
			}
		}
		//Comparaison des tables
		if (tables.size()>((Select) reponse).getTables().size()) {
			System.out.println("Vous avez indiquez plus de tables que nï¿½cï¿½ssaires");
		} else if (tables.size()<((Select) reponse).getTables().size()) {
			System.out.println("Vous avez indiquez moins de tables que nï¿½cï¿½ssaire");
		}
		for (String item : tables) {
			if (!((Select) reponse).getTables().contains(item)) {
				System.out.println(item + " n'apparait pas dans la rï¿½ponse du prof");
			}
		}
		//Comparaison des conditions
		if (conditions.size()>((Select) reponse).getConditions().size()) {
			System.out.println("Vous avez indiquez plus de conditions que nï¿½cï¿½ssaires");
		} else if (conditions.size()<((Select) reponse).getConditions().size()) {
			System.out.println("Vous avez indiquez moins de conditions que nï¿½cï¿½ssaire");
		}
		for (String item : conditions) {
			if (!((Select) reponse).getConditions().contains(item)) {
				System.out.println(item + " n'apparait pas dans la rï¿½ponse du prof");
			}
		}
	}
}