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
Permet les appels assychrones

## Ontologies
### class CriteriaManager
G�re l'acc�s aux crit�res
### class RecipesManager
G�re l'acc�s aux recettes
## class OntologyQueryInterfaceConnector
Permet d'�tablir une connection entre les ontologies et donne un point d'acc�s pour effectuer des requetes SPARQL.


#HEY RENAN !  
J'ai termin� ce que j'avais a faire, j'ai m�me impl�ment� le fait d'avoir des crit�res en options dans la liste. Je te mes un bout de code en exemple histoire que tu saches comment on l'utilise.
```
 ArrayList<Criteria> test = new ArrayList<Criteria>();
    	    Iterator<Criteria> it = all.iterator();
    	    while(it.hasNext()) {
    	    	// L� je construit une fake liste de crit�res dont un sera optionnel
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
                	// C'est g�nial
                    System.out.println(String.valueOf(recipes.size()));
                }
            }, test);
```    	    

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