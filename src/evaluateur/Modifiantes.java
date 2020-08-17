package evaluateur;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public abstract class Modifiantes extends QueryResult { //Cr�er une r�ponse � partir d'une requete modifiante
	
	protected String table;
	
	public Modifiantes(String requete, String nomFichier, Connexion connexion, String tabSelect) {
		super(requete, nomFichier);
		this.table = tabSelect;
		//TRANSFORMATION DU RESULTSET EN ARRAY
		connexion.executeSQLfile(this.nomFichier);
		try {
			//Comme la requete est modifiante on modifie d'abord la table puis on remplace la requete par un select de la table pour voir ce qui a ete modifi�
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
				//Une ligne doit pouvoir stocker diff�rent types car une requete peut renvoyer plusieurs types de donn�es (on utilise donc Object)
				ArrayList<Object> ligne = new ArrayList<Object>();
				for (int i = 1; i < nbCol; i++) { //Pour chaque colonne
					ligne.add(resultat.getString(i)); //On ajoute � la ligne la valeur de la colonne
				}
				resRequete.add(ligne);
			}
			stmt.close();
			//System.out.println("Reponse cr�e");
		}
		catch (SQLException e) {
			System.out.println(e);
	    	System.exit(1);
		}
		connexion.deleteBD();
	}
	
	protected String getTable() {
		return this.table;
	}

}
