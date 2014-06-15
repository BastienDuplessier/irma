irma
====

IRMA project for IA04/NF28

#Description des classes

##Activités
### class MainActivity
Activité d'accueil qui permet de choisir ses critéres de départ pour graphActivity

### class GraphActivity
Activité de tri de recettes interractif.

## Données
### class Information
Décrit une recette ici, un élément sujet de notre recherche en général.

### class CriteriaValue
Décrit un couple clef valeur décrivant une Information
Exemple : "ingredient : poulet", "temps de cuisson : 30min"

## Systéme agent (graph)
### class GraphContainer
Contiens la liste de agents et gére leurs interractions, a la facon du conteneur jade.

### class GraphView
Affiche le container et gére les entrées tactiles.

### class GraphAgent 
Représente un agent sur le graph, abstrait et ne faisant rien.
### class GraphRecipeAgent
Représente une recette sur le graph, subit la gravité
### class GraphCriteriaAgent
Représente un critére visible sur le graph (pas les critéres globaux). Influe sur les GraphRecipeAgent.

## Helper classes
### MiseEnPage
Permet la justification du texte
### ExecutableTask
Permet les appels assychrones

## Ontologies
### class CriteriaManager
Gère l'accès aux critères
### class RecipesManager
Gère l'accès aux recettes
## class OntologyQueryInterfaceConnector
Permet d'établir une connection entre les ontologies et donne un point d'accès pour effectuer des requetes SPARQL.


#HEY RENAN !  
J'ai terminé ce que j'avais a faire, j'ai même implémenté le fait d'avoir des critères en options dans la liste. Je te mes un bout de code en exemple histoire que tu saches comment on l'utilise.
```
 ArrayList<Criteria> test = new ArrayList<Criteria>();
    	    Iterator<Criteria> it = all.iterator();
    	    while(it.hasNext()) {
    	    	// Là je construit une fake liste de critères dont un sera optionnel
    	        Criteria crit = it.next();
    	        if(crit.getId().contains("agneau")) {
    	            crit.optionnal = true;
    	            test.add(crit);
    	        }
    	        if(crit.getId().contains("poivre"))
    	            test.add(crit);
    	    }
    	    
    	    RecipesManager rcpMng = new RecipesManager(OQIC);
    	    // On utilise asyncLoad(ExecutableTask, ArrayList<Criteria>) pour effecter la requete.
    	    rcpMng.asyncLoad(new ExecutableTask() {
                
                @Override
                public void execute(ArrayList<Recipe> recipes) {
                	// C'est génial
                    System.out.println(String.valueOf(recipes.size()));
                }
            }, test);
```    	    

#Comment récupérer tous les ingrédients ?  
C'est assez simple, il suffit d'instancier un nouveau `OntologyQueryInterfaceConnector` avec `getAssets` en argument (pour récupérer les fichiers). 
Ensuite, utiliser un `IngredientsManager`, avec le connector pour récupérer tous les ingrédients via sa methode `getAll`. le résultat est de type 
`ArrayList<Ingredient>`  
 

```
try {
	OntologyQueryInterfaceConnector bite = new OntologyQueryInterfaceConnector(getAssets());
	IngredientsManager sac = new IngredientsManager(bite);
	Iterator<Ingredient> souche = sac.getAll().iterator();

	while(souche.hasNext())
		System.out.println(souche.next().getName());
} catch (IOException e) {
	System.out.println("Haha, nobody cares");
}
```

#Comment récupérer toutes les recettes ?
C'est super simple. C'est pareil qu'au dessus sauf qu'on utilise un `RecipesManager` et la methode `getAll()` renvoie un `ArrayList<Recipe>`   

#Et comment charger des recettes en asynchrone ?
Il suffit d'utiliser la méthode `RecipesManager.asyncLoad(executableTask, sparqlQuery)`. Il existe aussi une méthode `asyncLoadAll(executableTask)` qui gère tout seul la requête SPARQL.  
```
OntologyQueryInterfaceConnector connector = null;
try {
    connector = new OntologyQueryInterfaceConnector(getAssets());
    RecipesManager manager = new RecipesManager(connector);
    manager.asyncLoadAll(new ExecutableTask() {
        @Override
        public void execute(ArrayList<Recipe> recipes) {
            Iterator<Recipe> recipesIterator = recipes.iterator();
            while(recipesIterator.hasNext())
                System.out.println(recipesIterator.next().toString());                    
        }
    });
} catch (IOException e) {
    System.out.println("Scoubidoubidouuuuuu !!");
}
```