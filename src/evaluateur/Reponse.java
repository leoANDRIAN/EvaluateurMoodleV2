package evaluateur;

/**
 * Classe abstraite permettant de stocker la requete originelle et sa version standardisée (attribut cleanRequete) d'un objet Reponse
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public abstract class Reponse { //Cette classe permet de rassembler une requete SQL, son resultat et le nom du fichier permettant de créer la BD nécéssaire à l'execution de la requete
	
	//CallableStatement : Appel procédure/fonction
	//PreparedStatement : adapté pour SELECT, INSERT, UPDATE, DELETE (parametres changeables)
	//Statement : adapté pour CREATE, DROP, ALTER, TRUNCATE et pour le reste si requete FIXE

	//Attributs
	/**
	 * Requete SQL telle qu'elle fournit en argument lors de l'execution du programme
	 */
	protected String requete;
	
	/**
	 * Requete "standardisée" pour permettre la comparaison avec une autre requete
	 */
	protected String cleanRequete; //Requete "standardisée"
	
	 /** 
     * Constructeur de la classe Reponse, permet de stocker la requete
     * @param requete        la requete (String) à stocker
     */
	public Reponse(String requete) {
		this.requete = requete;
	}
	
	//Methodes
	/** 
     * Getter de l'attribut requete
     * @return Renvoi l'attribut requete
     */
	public String getRequete () {
		return requete;
	}
	
	/** 
     * Renvoi le plus petit entier parmis les 3 fournis en parametre
     * 
     * @param n1        premier entier
     * @param n2        deuxieme entier
     * @param n3        troisieme entier
     * @return renvoi le plus petit entier
     */
	protected int minimum(int n1, int n2, int n3) {
		if (n1 <= n2 && n1 <= n3) {
			return n1;
		}
		else if (n2 <= n1 && n2 <= n3) {
			return n2;
		}
		else {
			return n3;
		}
	}
	
	//Pourrait etre utilisé pour comparer la différence entre deux chaines de caractere
	/** 
     * Calcul la "différence" entre deux chaines de caractères en utilisant l'algorithme de Wagner et Fischer (distance de Levenshtein) 
     * 
     * @param str1        Première chaine de caractères à comparer
     * @param str2        Deuxieme chaine de caractères à comparer
     * @return Renvoi l'écart entre les deux String
     */
	protected int distanceLev(String str1, String str2) { //Tweekd pour utiliser un tableau à 2 lignes au lieu d'un tableau String1 * String2
		char[] arrChar1 = str1.toCharArray();
		char[] arrChar2 = str2.toCharArray();
		int cout;
		boolean pair = true;
		int[][] tab = new int[2][str1.length()]; //Tableau 2 lignes
		for (int i = 0; i < str2.length(); i++) {
			tab[0][i] = i;
		}
		for (int i = 1; i < str2.length(); i++) {
			//On alterne entre les 2 lignes pour calculer les substitutions
			if (pair) { 
				tab[1][0] = i;
				for (int j = 1; j < str1.length(); j++) {
					cout = (arrChar2[i] == arrChar1[j]) ? 0 : 1;
					tab[1][j] = minimum(tab[0][j-1]+cout, tab[1][j-1]+1, tab[0][j]+1);
				}
				pair = false;
			} else {
				tab[0][0] = i;
				for (int j = 1; j < str1.length(); j++) {
					cout = (arrChar2[i] == arrChar1[j]) ? 0 : 1;
					tab[0][j] = minimum(tab[1][j-1]+cout, tab[0][j-1]+1, tab[1][j]+1);
				}
				pair = true;
			}
		}
		if (pair) {
			return tab[0][str1.length()-1];
		} else {
			return tab[1][str1.length()-1];
		}
	}
	
	/** 
     * Methode permettant de comparer la synthaxe entre les requetes de deux objets Reponse.
     * Cette methode est abstraite et doit etre implémentée par chaque classe concrète héritant de Reponse.
     * Concretement, il faut créer un algorithme de comparaison spécifique a chaque type de requete (select, update, create...)
     * 
     * @param reponse        Autre objet reponse avec lequel on veut comparer notre cible
     */
	public abstract void compareSyntaxe(Reponse reponse);
	
}
