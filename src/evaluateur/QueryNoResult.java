package evaluateur;

/**
 * Classe abstraite heritant de la classe Reponse et permettant de servir de patron aux classes gérant les requetes DDL dont l'execution ne renvoi pas de "resultat".
 * Cette classe et ses sous classes sont censées etre peuplé par un autre membre de l'equipe
 * 
 * @version 	%I%, %G%
 * @author 	ANDRIANTSIZAFY Leo
 */
public abstract class QueryNoResult extends Reponse {
	
	public QueryNoResult(String requete) {
		super(requete);
	}
	
}
