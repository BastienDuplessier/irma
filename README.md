irma
====

IRMA project for IA04/NF28

#Description des classes

##Activit�s
### class MainActivity
Activit� d'accueil qui permet de choisir ses crit�res de d�part pour graphActivity

### class GraphActivity
Activit� de tri de recettes interractif.

## Donn�es
### class Information
D�crit une recette ici, un �l�ment sujet de notre recherche en g�n�ral.

### class CriteriaValue
D�crit un couple clef valeur d�crivant une Information
Exemple : "ingredient : poulet", "temps de cuisson : 30min"

## Syst�me agent (graph)
### class GraphContainer
Contiens la liste de agents et g�re leurs interractions, a la facon du conteneur jade.

### class GraphView
Affiche le container et g�re les entr�es tactiles.

### class GraphAgent 
Repr�sente un agent sur le graph, abstrait et ne faisant rien.
### class GraphRecipeAgent
Repr�sente une recette sur le graph, subit la gravit�
### class GraphCriteriaAgent
Repr�sente un crit�re visible sur le graph (pas les crit�res globaux). Influe sur les GraphRecipeAgent.

## Helper classes
### MiseEnPage
Permet la justification du texte
### ExecutableTask
Permet les appels assinchrones

## Ontologies
### class IngredientsManager
???
### class RecipesManager
???
## class OntologyQueryInterfaceConnector
???



#Comment r�cup�rer tous les ingr�dients ?  
C'est assez simple, il suffit d'instancier un nouveau `OntologyQueryInterfaceConnector` avec `getAssets` en argument (pour r�cup�rer les fichiers). 
Ensuite, utiliser un `IngredientsManager`, avec le connector pour r�cup�rer tous les ingr�dients via sa methode `getAll`. le r�sultat est de type 
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

#Comment r�cup�rer toutes les recettes ?
C'est super simple. C'est pareil qu'au dessus sauf qu'on utilise un `RecipesManager` et la methode `getAll()` renvoie un `ArrayList<Recipe>`   

#Et comment charger des recettes en asynchrone ?
Il suffit d'utiliser la m�thode `RecipesManager.asyncLoad(executableTask, sparqlQuery)`. Il existe aussi une m�thode `asyncLoadAll(executableTask)` qui g�re tout seul la requ�te SPARQL.  
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