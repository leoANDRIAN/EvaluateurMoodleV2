package evaluateur;

import java.util.ArrayList;

/**
 * Classe concrete permettant de traiter une requete DELETE :
 * Standardisation puis analyse de la synthaxe de la requete
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public class Delete extends Modifiantes {
	
	/**
	 * Boolean permettant de savoir si la requete comporte l'argument WHERE
	 */
	private boolean boolWhere;
	
	/**
	 * Tableau contenant les arguments suivant le mot WHERE jusqu'a ";" (uniquement si booWhere est vrai)
	 */
	private ArrayList<String> conditions = new ArrayList<String>();
	
	/** 
     * Constructeur de la classe DELETE, va permettre de peupler les attributs de la classe (cf attributs), standardiser et anlayser la requete et enfin executer la requete
     * 
     * @param requete        la requete (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param nomFichier        la nom du fichier (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param connexion        la connexion (String) nécéssaire à l'execution des requetes SQL
     * @param tabSelect        nom de la table permettant de peupler l'attribut table (String) sera appelé par le super constructeur de la classe
     */
	public Delete(String requete, String nomFichier, Connexion connexion, String tabSelect) {
		super(requete, nomFichier, connexion, tabSelect);
		boolWhere = false;
		String standardised = "";
		int currentChar;
		int lastChar = 0;
		for (int i = 0; i < this.requete.length(); i++) {
			currentChar = this.requete.charAt(i);
			if (i == this.requete.length()-1) { //Au bout de la requete on separe le ";"
				standardised += " " + ((char) currentChar);
			} else if (currentChar == 32 && lastChar == 32) { //Si double espace on fait rien
			} else if (currentChar == 34) { //On remplace les guillemets par des quotes
				standardised += (char) 39;
			} else {
				standardised += ((char) currentChar);
			}
			lastChar = currentChar;
		}
		this.cleanRequete = standardised.toUpperCase();
		//System.out.println("Standard : " + this.cleanRequete);
		
		String[] requeteSplit = this.cleanRequete.split(" ");
		
		if (requeteSplit.length > 4) { //Si on a une clause WHERE
			boolWhere = true;
			String currentItem = ""; //Permet d'accumuler les elements sï¿½parï¿½s par un espace : age > 40 --> age>40;
			for (int i = 4; i < requeteSplit.length; i++) {
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
		}
	}
	
	/** 
     * Methode permettant de comparer la synthaxe entre les requetes de deux objets Delete.
     * Cette methode doit etre implémentée par chaque classe concrète héritant de Reponse.
     * Compare les tableaux (attributs) des deux objets.
     * 
     * @param reponse        Autre objet reponse avec lequel on veut comparer notre cible
     */
	public void compareSyntaxe(Reponse reponse) {
		if (boolWhere) {
			if (conditions.size()>((Delete) reponse).getConditions().size()) {
				System.out.println("Vous avez indiquez plus de conditions que nï¿½cï¿½ssaires");
			} else if (conditions.size()<((Delete) reponse).getConditions().size()) {
				System.out.println("Vous avez indiquez moins de conditions que nï¿½cï¿½ssaire");
			}
			for (String item : conditions) {
				if (!((Delete) reponse).getConditions().contains(item)) {
					System.out.println(item + " n'apparait pas dans la rï¿½ponse du prof");
				}
			}
		}
	}
	
	/** 
     * Getter de l'attribut conditions
     * 
     * @return renvoi le tableau contenu dans conditions
     */
	public ArrayList<String> getConditions() {
		return conditions;
	}
}
