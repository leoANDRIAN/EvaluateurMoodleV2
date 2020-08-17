package evaluateur;

import java.util.ArrayList;

/**
 * Classe abstraite heritant de la classe Reponse et permettant de stocker le r�sultat d'une requete SQL
 * les requetes dont l'execution produit un "r�sultat" (visible grace � un select = SELECT, UPDATE, DELETE...) doivent chacune poss�der une classe associ�e qui impl�mente cette classe
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public abstract class QueryResult extends Reponse {
	
	/**
	 * Chemin menant au fichier contenant les requetes permettant de g�n�rer les donn�es sur lesquelles tester nos requetes.
	 */
	protected String nomFichier;
	
	/**
	 * Resultat de la requete stock�e sous forme de tableau 2D
	 */
	protected ArrayList<ArrayList<Object>> resRequete;
	
	/** 
     * Constructeur de la classe QueryResult, permet de stocker la requete
     * 
     * @param requete        la requete (String) � stocker qui sera appel� par le super constructeur de la classe
     * @param nomFichier	chemin du fichier permettant de cr�er la BD de test ex: "/usr/local/createTable.txt"
     */
	public QueryResult(String requete, String nomFichier) {
		super(requete);
		this.nomFichier = nomFichier;
	}
	
	/** 
     * Getter de l'attribut resRequete
     * 
     * @return renvoi l'attribut contenant le resultat de la requete (tableau 2D)
     */
	public ArrayList<ArrayList<Object>> getResRequete () {
		return resRequete;
	}
	
	/** 
     * Permet d'afficher le contenu du tableau 2D contenant le resultat de la requete
     */
	public void afficheTable() {
		System.out.println(resRequete.toString());
	}
	
	/** 
     * Getter de l'attribut requete
     * 
     * @return renvoi l'attribut fichier
     */
	public String getFichier() {
		return nomFichier;
	}
	
	/** 
     * Methode permettant de comparer le resultat de deux objets QueryResult.
     * Cette methodes compare les tableaux 2D contenu dans l'attribut resRequete de la classe QueryResult
     * 
     * @param reponse2        Autre objet reponse avec lequel on veut comparer notre cible
     */
	public void comparaisonTable(QueryResult reponse2) {
		//System.out.println(reponse2.getResRequete()); //Affiche la r�ponse du prof
		//System.out.println(resRequete); //Affiche la r�ponse de l'�l�ve
		if (reponse2.getResRequete().equals(resRequete)) {
			System.out.println("resultats identiques");
		} else {
			System.out.println("resultats differents");
		}
	}
}
