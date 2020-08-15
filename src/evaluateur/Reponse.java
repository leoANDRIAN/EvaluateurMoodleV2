package evaluateur;

public abstract class Reponse { //Cette classe permet de rassembler une requete SQL, son resultat et le nom du fichier permettant de créer la BD nécéssaire à l'execution de la requete
	
	//CallableStatement : Appel procédure/fonction
	//PreparedStatement : adapté pour SELECT, INSERT, UPDATE, DELETE (parametres changeables)
	//Statement : adapté pour CREATE, DROP, ALTER, TRUNCATE et pour le reste si requete FIXE

	//Attributs
	protected String requete;
	protected String cleanRequete; //Requete "standardisée"
	
	public Reponse(String requete) {
		this.requete = requete;
	}
	
	//Methodes
	public String getRequete () {
		return requete;
	}
	
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
	
	public abstract void compareSyntaxe(Reponse reponse);
	
}
