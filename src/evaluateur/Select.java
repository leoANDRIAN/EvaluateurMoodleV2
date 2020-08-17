package evaluateur;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Select extends QueryResult { //Cr�er une r�ponse � partir d'un SELECT
	
	private ArrayList<String> champs = new ArrayList<String>();
	private ArrayList<String> tables = new ArrayList<String>();
	private ArrayList<String> conditions = new ArrayList<String>();
	
	public Select(String requete, String nomFichier) { //Constructeur dummy
		super(requete, nomFichier);
	}

	//TO-DO : am�liorer l'analyse du select (natural join, select dans select...)
	
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
		int fromPos = 0; //Va permettre de r�cup�rer la position du from dans la requete pour commencer a partir de cette position dans le methode suivante (optimisation nb calcul)
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
		String currentItem = ""; //Permet d'accumuler les elements s�par�s par un espace : age > 40 --> age>40;
		for (int i = wherePos + 1; i < requeteSplit.length; i++) {
			if (requeteSplit[i].equals("AND")) { //Si on tombe sur AND on ajoute l'argument pr�c�dant dans l'array et on prepare currentItem pour le prochain argument
				conditions.add(currentItem);
				currentItem = "";
			} else if (requeteSplit[i].equals(";")) { //On est arriv� au bout de la requete (on l'aura s�par� du reste de la requete pendant la phase de standardisation)
				conditions.add(currentItem);
				break;
			} else {
				currentItem += requeteSplit[i]; //On accumule les morceaux de chaine dans currentItem;
			}
		}
		
		//R�cup�ration du resultat de la requete
		connexion.executeSQLfile(this.nomFichier);
		try {
			Statement stmt = connexion.getConnection().createStatement();
			ResultSet resultat = stmt.executeQuery(requete); //Requete "FIXE"
			int nbCol = resultat.getMetaData().getColumnCount(); //Recupere nb colonne du resultat de la requete
			resRequete = new ArrayList<ArrayList<Object>>();
			
			while(resultat.next()) {
				//Une ligne doit pouvoir stocker diff�rent types car une requete peut renvoyer plusieurs types de donn�es (on utilise donc Object)
				ArrayList<Object> ligne = new ArrayList<Object>();
				for (int i = 1; i < nbCol; i++) { //Pour chaque colonne
					ligne.add(resultat.getString(i)); //On ajoute � la ligne la valeur de la colonne
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
	
	public ArrayList<String> getChamps() {
		return champs;
	}

	public ArrayList<String> getTables() {
		return tables;
	}

	public ArrayList<String> getConditions() {
		return conditions;
	}
	
	public void compareSyntaxe(Reponse reponse) {
		//Comparaison des champs
		if (champs.size()>((Select) reponse).getChamps().size()) {
			System.out.println("Vous avez indiquez plus de champs que n�c�ssaires");
		} else if (champs.size()<((Select) reponse).getChamps().size()) {
			System.out.println("Vous avez indiquez moins de champs que n�c�ssaire");
		}
		for (String item : champs) {
			if (!((Select) reponse).getChamps().contains(item)) {
				System.out.println(item + " n'apparait pas dans la r�ponse du prof");
			}
		}
		//Comparaison des tables
		if (tables.size()>((Select) reponse).getTables().size()) {
			System.out.println("Vous avez indiquez plus de tables que n�c�ssaires");
		} else if (tables.size()<((Select) reponse).getTables().size()) {
			System.out.println("Vous avez indiquez moins de tables que n�c�ssaire");
		}
		for (String item : tables) {
			if (!((Select) reponse).getTables().contains(item)) {
				System.out.println(item + " n'apparait pas dans la r�ponse du prof");
			}
		}
		//Comparaison des conditions
		if (conditions.size()>((Select) reponse).getConditions().size()) {
			System.out.println("Vous avez indiquez plus de conditions que n�c�ssaires");
		} else if (conditions.size()<((Select) reponse).getConditions().size()) {
			System.out.println("Vous avez indiquez moins de conditions que n�c�ssaire");
		}
		for (String item : conditions) {
			if (!((Select) reponse).getConditions().contains(item)) {
				System.out.println(item + " n'apparait pas dans la r�ponse du prof");
			}
		}
	}
}