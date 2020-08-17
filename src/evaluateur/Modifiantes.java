package evaluateur;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Classe abstraite heritant de la classe ResultQuery et permettant de stocker le nom de la table sur laquelle faire un select afin de déterminer l'attribut resRequete (cf classe ResultQuery)
 * Cette classe est destinée à etre implémenté par les classes representant les requetes UPDATE, DELETE, INSERT qui ont en commun le fait de devoir d'abord etre executées puis de nécéssité un select sur les tables qu'elles ont modifié pour obtenir un resultat.
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public abstract class Modifiantes extends QueryResult { //Crï¿½er une rï¿½ponse ï¿½ partir d'une requete modifiante
	
	/**
	 * Nom de la table sur laquelle executer un select pour voir l'effet de la requete modifiante
	 */
	protected String table;
	
	/** 
     * Constructeur de la classe Modifiante, permet de peupler l'attribut table et d'executer la requete
     * 
     * @param requete        la requete (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param nomFichier        la nom du fichier (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param connexion        la connexion (String) nécéssaire à l'execution des requetes SQL
     * @param tabSelect 	nom de la table permettant de peupler l'attribut table
     */
	public Modifiantes(String requete, String nomFichier, Connexion connexion, String tabSelect) {
		super(requete, nomFichier);
		this.table = tabSelect;
		//TRANSFORMATION DU RESULTSET EN ARRAY
		connexion.executeSQLfile(this.nomFichier);
		try {
			//Comme la requete est modifiante on modifie d'abord la table puis on remplace la requete par un select de la table pour voir ce qui a ete modifiï¿½
			Statement stmtUpdate = connexion.getConnection().createStatement();
			stmtUpdate.executeUpdate(requete);
			stmtUpdate.close();
			requete = "SELECT * FROM " + tabSelect;
			
			//TRANSFORMATION DU RESULTSET EN ARRAY
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
			//System.out.println("Reponse crï¿½e");
		}
		catch (SQLException e) {
			System.out.println(e);
	    	System.exit(1);
		}
		connexion.deleteBD();
	}
	
	/** 
     * Getter de l'attribut table
     * 
     * @return renvoi l'attribut table
     */
	protected String getTable() {
		return this.table;
	}

}
