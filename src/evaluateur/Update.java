package evaluateur;

import java.util.ArrayList;

/**
 * Classe concrete permettant de traiter une requete UPDATE :
 * Standardisation puis analyse de la synthaxe de la requete
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public class Update extends Modifiantes {
	
	/**
	 * Boolean permettant de savoir si la requete comporte l'argument WHERE
	 */
	private boolean boolWhere;
	
	/**
	 * Tableau contenant les arguments suivant le mot SET jusqu'a WHERE ou ";" (depend de boolWhere)
	 */
	private ArrayList<String> champs = new ArrayList<String>();
	
	/**
	 * Tableau contenant les arguments suivant le mot WHERE jusqu'a ";" (uniquement si booWhere est vrai)
	 */
	private ArrayList<String> conditions = new ArrayList<String>();

	/** 
     * Constructeur de la classe UPDATE, va permettre de peupler les attributs de la classe (cf attributs), standardiser et anlayser la requete et enfin executer la requete
     * 
     * @param requete        la requete (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param nomFichier        la nom du fichier (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param connexion        la connexion (String) nécéssaire à l'execution des requetes SQL
     * @param tabSelect        nom de la table permettant de peupler l'attribut table (String) sera appelé par le super constructeur de la classe
     */
	public Update(String requete, String nomFichier, Connexion connexion, String tabSelect) {
		super(requete, nomFichier, connexion, tabSelect);
		boolWhere = false;
		String standardised = "";
		int currentChar;
		int lastChar = 0;
		for (int i = 0; i < this.requete.length(); i++) {
			currentChar = this.requete.charAt(i);
			if (i == this.requete.length()-1) { //Au bout de la requete on separe le ";"
				standardised += " " + ((char) currentChar);
			} else if (currentChar == 44) { //Si virgule on remplace par un espace
				if (lastChar != 32) {
					standardised += " ";
				}
				lastChar = 32;
				continue;
			} else if (currentChar == 32 && lastChar == 32) { //Si double espace on fait rien
			} else if (currentChar == 61) { //Si "=" on enleve les espaces qui pourraient le separer de ses arguments
				if (lastChar == 32) {
					standardised = standardised.substring(0, standardised.length()-1) + ((char) currentChar);
				}
				else {
					standardised += ((char) currentChar);
				}
				if (this.requete.charAt(i+1) == 32) {
					i++;
				}
			} else if (currentChar == 34) {
				standardised += (char) 39;
			} else {
				standardised += ((char) currentChar);
			}
			lastChar = currentChar;
		}
		this.cleanRequete = standardised.toUpperCase();
		System.out.println("Standard : " + this.cleanRequete);
		
		String[] requeteSplit = this.cleanRequete.split(" ");
		
		int WherePos = 0; //Va permettre de rï¿½cupï¿½rer la position du from dans la requete pour commencer a partir de cette position dans le methode suivante (optimisation nb calcul)
		for (int i = 3; i < requeteSplit.length; i++) {
			if (requeteSplit[i].equals("WHERE")) {
				WherePos = i;
				boolWhere = true;
				break;
			} else if (requeteSplit[i].equals(";")) {
				break;
			} else {
				champs.add(requeteSplit[i]);
			}
		}
		
		if (boolWhere == true) {
			String currentItem = ""; //Permet d'accumuler les elements sï¿½parï¿½s par un espace : age > 40 --> age>40;
			for (int i = WherePos + 1; i < requeteSplit.length; i++) {
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
     * Getter de l'attribut champs
     * 
     * @return renvoi le tableau contenu dans champs
     */
	public ArrayList<String> getChamps() {
		return champs;
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
     * Methode permettant de comparer la synthaxe entre les requetes de deux objets Update.
     * Cette methode doit etre implémentée par chaque classe concrète héritant de Reponse.
     * Compare les tableaux (attributs) des deux objets.
     * 
     * @param reponse        Autre objet reponse avec lequel on veut comparer notre cible
     */
	public void compareSyntaxe(Reponse reponse) {
		//Comparaison des champs modifiï¿½s
		if (champs.size()>((Update) reponse).getChamps().size()) {
			System.out.println("Vous avez indiquez plus de modifications que nï¿½cï¿½ssaires");
		} else if (champs.size()<((Update) reponse).getChamps().size()) {
			System.out.println("Vous avez indiquez moins de modifications que nï¿½cï¿½ssaire");
		}
		for (String item : champs) {
			if (!((Update) reponse).getChamps().contains(item)) {
				System.out.println(item + " n'apparait pas dans la rï¿½ponse du prof");
			}
		}
		
		//Comparaison des conditions s'il y en a
		if (conditions.size()>((Update) reponse).getConditions().size()) {
			System.out.println("Vous avez indiquez plus de conditions que nï¿½cï¿½ssaires");
		} else if (conditions.size()<((Update) reponse).getConditions().size()) {
			System.out.println("Vous avez indiquez moins de conditions que nï¿½cï¿½ssaire");
		}
		for (String item : conditions) {
			if (!((Update) reponse).getConditions().contains(item)) {
				System.out.println(item + " n'apparait pas dans la rï¿½ponse du prof");
			}
		}
	}
	
}
