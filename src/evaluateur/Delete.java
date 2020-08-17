package evaluateur;

import java.util.ArrayList;

public class Delete extends Modifiantes {
	
	private boolean boolWhere;
	private ArrayList<String> conditions = new ArrayList<String>();
	
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
			String currentItem = ""; //Permet d'accumuler les elements s�par�s par un espace : age > 40 --> age>40;
			for (int i = 4; i < requeteSplit.length; i++) {
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
		}
	}
	
	public void compareSyntaxe(Reponse reponse) {
		if (boolWhere) {
			if (conditions.size()>((Delete) reponse).getConditions().size()) {
				System.out.println("Vous avez indiquez plus de conditions que n�c�ssaires");
			} else if (conditions.size()<((Delete) reponse).getConditions().size()) {
				System.out.println("Vous avez indiquez moins de conditions que n�c�ssaire");
			}
			for (String item : conditions) {
				if (!((Delete) reponse).getConditions().contains(item)) {
					System.out.println(item + " n'apparait pas dans la r�ponse du prof");
				}
			}
		}
	}
	
	public ArrayList<String> getConditions() {
		return conditions;
	}
}
