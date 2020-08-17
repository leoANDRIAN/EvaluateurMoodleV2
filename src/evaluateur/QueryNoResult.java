package evaluateur;

/**
 * Classe abstraite heritant de la classe Reponse et permettant de servir de patron aux classes g�rant les requetes DDL dont l'execution ne renvoi pas de "resultat".
 * Cette classe et ses sous classes sont cens�es etre peupl� par un autre membre de l'equipe
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public abstract class QueryNoResult extends Reponse {
	
	public QueryNoResult(String requete) {
		super(requete);
	}
	
}
