irma
====

IRMA project for IA04/NF28


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