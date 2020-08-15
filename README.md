# EvaluateurMoodleV2
Version alternative de l'evaluateur de requete SQL présent sur ce compte Git Hub.
Contrairement à l'autre évaluateur présent sur ce git, cette evaluateur ne stock pas la reponse de l'enseignant dans un json pour pouvoir la comparer ensuite a celle des élèves.
Il traite, a chaque fois tentative d'un élève, la requete de l'élève et de l'enseignant.
Cette version de l'evaluateur a l'avantage d'etre facilement implémentable au sein de codeRunner sans trop toucher à ce dernier.
Cependant, il est beaucoup plus "ressource hungry" puisque qu'il traite plusieurs fois la requete d'un enseignant au lieu de stocker le resultat du traitement pour un futur ré-emploi.
