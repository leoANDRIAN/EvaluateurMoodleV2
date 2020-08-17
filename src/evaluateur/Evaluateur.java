package evaluateur;

import java.sql.*;

/**
 * Classe servant de corps pour executer le programme
 * Le programme doit prendre en arguments dans l'ordre : le SGBG auquel se connecter (mySQL), la requete de l'enseignant, la requete de l'eleve, le chemin vers le fichier contenant les requetes permettant de créer une BD de test (Creates et insert)
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public class Evaluateur {
	
	public Evaluateur() {}
	
	//NE PAS OUBLIER DE FAIRE UN COMPARATIF DES SGBD POUR RENDU FINAL

	public static void main(String[] args) throws SQLException {
		
		//TO-DO : Trouver pourquoi il rï¿½cupï¿½re pas tout les champs demandï¿½s lors de l'execution de la requete
		//TO-DO : Fermer les connexion et les resultSet aux endroits nï¿½cï¿½ssaires
		
		//RECUPERATION DES DONNEES EXTERNES NECESSAIRES A L'EXECUTION DU PROGRAMME
		String sgbd = args[0];
		String requeteEleve = args[2];
		String requeteEnseignant = args[1];
		String nomFichier = null;
		if (args.length == 4) {
			nomFichier = args[3];
		}
		
		//Si oubli du point virgule NECESSAIRE pour le traitement de la requete
		if (requeteEleve.charAt(requeteEleve.length()-1) != 59) { //S'il manque le point virgule
			requeteEleve += ";";
		}
		if (requeteEnseignant.charAt(requeteEnseignant.length()-1) != 59) { //S'il manque le point virgule
			requeteEnseignant += ";";
		}
		
		//PHASE DE CONNEXION BD
		Connexion connexion = new Connexion(sgbd);
		connexion.deleteBD(); //Permet de nettoyer une BD mySQL au cas ou elle n'aurait pas ï¿½tï¿½ vidï¿½e (a etendre aux autres SGBD)
		
		Reponse reponseEleve = null;
		Reponse reponseEnseignant = null;
		
		//GENERATION DU RESULTAT DE LA REQUETE A TRAITER (voir le constructeur de la classe Reponse)
		String [] splittedRequete1 = requeteEnseignant.split(" ", 4); //On rï¿½cupï¿½re les premiers mots de la requete de l'enseignant
		String typeRequete1 = splittedRequete1[0].toUpperCase(); //On recupere le type de la requete (1er mot)
		String [] splittedRequete2 = requeteEleve.split(" ", 4); //On rï¿½cupï¿½re les premiers mots de la requete de l'eleve
		String typeRequete2 = splittedRequete2[0].toUpperCase(); //On recupere le type de la requete (1er mot)
		boolean queryResult = false; //Permet de savoir si la requete a un resultat pour executer la fonction de comparaison adaptï¿½e (true dans tous les cas car les requetes DLL ne sont pas encore supportï¿½es)
		if (!typeRequete1.equals(typeRequete2)) {
			System.out.println("La requete de l'ï¿½lï¿½ve n'est pas du meme type que celle de l'enseignant");
			System.exit(0);
		}
		String tableSelect1 = null; //Dans le cas d'une requete modifiante, on rï¿½cupï¿½re la table qui est modifiï¿½ pour faire un select ensuite
		String tableSelect2 = null;
		switch (typeRequete1) { //On crï¿½er la rï¿½ponse ï¿½ partir de la requete
		  case "SELECT" :
			  reponseEnseignant = new Select(requeteEnseignant, nomFichier, connexion);
			  reponseEleve = new Select(requeteEleve, nomFichier, connexion);
			  queryResult = true;
		    break;
		  case "UPDATE" :
			  tableSelect1 = splittedRequete1[1];
			  tableSelect2 = splittedRequete2[1];
			  if (!tableSelect1.equals(tableSelect2)) {
					System.out.println("Vous n'executez pas la requete sur la bonne table");
					System.exit(1);
			  }
			  reponseEnseignant = new Update(requeteEnseignant, nomFichier, connexion, tableSelect1);
			  reponseEleve = new Update(requeteEleve, nomFichier, connexion, tableSelect2);
			  queryResult = true;
		    break;
		  case "DELETE" :
			  tableSelect1 = splittedRequete1[2];
			  tableSelect2 = splittedRequete2[2];
			  if (!tableSelect1.equals(tableSelect2)) {
					System.out.println("Vous n'executez pas la requete sur la bonne table");
					System.exit(1);
			  }
			  reponseEnseignant = new Delete(requeteEnseignant, nomFichier, connexion, tableSelect1);
			  reponseEleve = new Delete(requeteEleve, nomFichier, connexion, tableSelect2);
			  queryResult = true;
		    break;
		  case "INSERT" : //A tweak car la "(" peut etre collï¿½ ï¿½ la table je crois
			  tableSelect1 = splittedRequete1[2];
			  tableSelect2 = splittedRequete2[2];
			  if (!tableSelect1.equals(tableSelect2)) {
					System.out.println("Vous n'executez pas la requete sur la bonne table");
					System.exit(1);
			  }
			  reponseEnseignant = new Insert(requeteEnseignant, nomFichier, connexion, tableSelect1);
			  reponseEleve = new Insert(requeteEleve, nomFichier, connexion, tableSelect2);
			  queryResult = true;
			break;
		  default:
			  System.out.println("Requete invalide : type de requete inconnue ou non supportï¿½e");
			  System.exit(1);
		}
		if (queryResult) {
			((QueryResult)reponseEleve).comparaisonTable((QueryResult) reponseEnseignant);
		}
		reponseEleve.compareSyntaxe(reponseEnseignant);
	  //System.out.println(System.getProperty("user.dir"));
	}

}
