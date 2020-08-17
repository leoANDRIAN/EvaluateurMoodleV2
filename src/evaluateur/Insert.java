package evaluateur;

import java.util.ArrayList;

/**
 * Classe concrete permettant de traiter une requete INSERT :
 * Standardisation puis analyse de la synthaxe de la requete
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public class Insert extends Modifiantes {
	
	/**
	 * Boolean permettant de savoir si la requete insert des données dans tout les champs de la table (pas besoin de préciser les champs)
	 */
	private boolean allField;
	
	/**
	 * Tableau contenant les arguments suivant le nom de la table jusqu'a VALUES (depend de allField)
	 */
	private ArrayList<String> fields = new ArrayList<String>();
	
	/**
	 * Tableau contenant les arguments suivant le mot VALUES jusqu'a ";"
	 */
	private ArrayList<String> values = new ArrayList<String>();
	
	/** 
     * Constructeur de la classe INSERT, va permettre de peupler les attributs de la classe (cf attributs), standardiser et anlayser la requete et enfin executer la requete
     * 
     * @param requete        la requete (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param nomFichier        la nom du fichier (String) à stocker qui sera appelé par le super constructeur de la classe
     * @param connexion        la connexion (String) nécéssaire à l'execution des requetes SQL
     * @param tabSelect        nom de la table permettant de peupler l'attribut table (String) sera appelé par le super constructeur de la classe
     */
	public Insert(String requete, String nomFichier, Connexion connexion, String tabSelect) {
		super(requete, nomFichier, connexion, tabSelect);
		allField = false;
		String standardised = "";
		int currentChar;
		int lastChar = 0;
		for (int i = 0; i < this.requete.length(); i++) {
			currentChar = this.requete.charAt(i);
			if (i == this.requete.length()-1) { //Au bout de la requete on separe le ";"
				standardised += ((char) currentChar);
			} else if (currentChar == 44) { //Si virgule on remplace par un espace
				if (lastChar != 32) {
					standardised += " ";
				}
				lastChar = 32;
				continue;
			} else if (currentChar == 40 || currentChar == 41) { //Si parenthï¿½se
				if (lastChar == 32 && this.requete.charAt(i) == 32) {
					i++;
				} else if (lastChar == 32 || this.requete.charAt(i) == 32) {
				} else {
					standardised += " ";
				}
				lastChar = 32;
				continue;
			} else if (currentChar == 32 && lastChar == 32) { //Si double espace on fait rien
			} else if (currentChar == 34) { //Si guillemets on met des quotes a la place
				standardised += (char) 39;
			} else {
				standardised += ((char) currentChar);
			}
			lastChar = currentChar;
		}
		this.cleanRequete = standardised.toUpperCase();
		//System.out.println("Standard : " + this.cleanRequete);
		
		String[] requeteSplit = this.cleanRequete.split(" ");
		
		if (requeteSplit[3].equals("VALUES")) {
			allField = true;
		}
		
		int valuesPos = 4;
		if (!allField) {
			for (int i = 4; i < requeteSplit.length; i++) {
				if (requeteSplit[i].equals("VALUES")) {
					valuesPos = i + 1;
					break;
				} else {
					fields.add(requeteSplit[i]);
				}
			}
		}
		for (int i = valuesPos; i < requeteSplit.length; i++) {
			if (requeteSplit[i].equals(";")) {
				break;
			} else {
				values.add(requeteSplit[i]);
			}
		}
	}
	
	/** 
     * Methode permettant de comparer la synthaxe entre les requetes de deux objets Insert.
     * Cette methode doit etre implémentée par chaque classe concrète héritant de Reponse.
     * Compare les tableaux (attributs) des deux objets.
     * 
     * @param reponse        Autre objet reponse avec lequel on veut comparer notre cible
     */
	public void compareSyntaxe(Reponse reponse) {
		//boolean sameItems = true;
		if (!allField) {
			if (fields.size()>((Insert) reponse).getFields().size()) {
				System.out.println("Vous avez indiquez plus de champs que necessaires");
			} else if (fields.size()<((Insert) reponse).getFields().size()) {
				System.out.println("Vous avez indiquez moins de champs que necessaire");
			}
			for (String item : fields) {
				if (!((Insert) reponse).getFields().contains(item)) {
					System.out.println(item + " n'apparait pas dans la reponse du prof");
				}
			}
		}
		if (values.size()>((Insert) reponse).getValues().size()) {
			System.out.println("Vous avez indiquez plus de valeurs que necessaires");
		} else if (values.size()<((Insert) reponse).getValues().size()) {
			System.out.println("Vous avez indiquez moins de valeurs que necessaire");
		}
		for (String item : values) {
			if (!((Insert) reponse).getValues().contains(item)) {
				System.out.println(item + " n'apparait pas dans la rï¿½ponse du prof");
			}
		}
	}
	
	/** 
     * Getter de l'attribut fields
     * 
     * @return renvoi le tableau contenu dans fields
     */
	public ArrayList<String> getFields() {
		return fields;
	}
	
	/** 
     * Getter de l'attribut values
     * 
     * @return renvoi le tableau contenu dans values
     */
	public ArrayList<String> getValues() {
		return values;
	}
}
