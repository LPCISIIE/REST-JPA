package control;

import boundary.Category.CategoryResource;
import boundary.Ingredient.IngredientResource;
import boundary.Sandwich.SandwichResource;
import entity.Category;
import entity.Ingredient;
import entity.Sandwich;

import javax.ejb.EJB;

public class DatabaseSeeder {

    static boolean done = false;

    /**
     * 
     * Method that creates fake insertions into the database
     */
    public static void feedCatalog(IngredientResource ingredientResource, CategoryResource categoryResource, SandwichResource sandwichResource){
        if (!done) {

            Category category = categoryResource.insert(new Category("Pain"));
            Ingredient painBlanc = new Ingredient(category,"Pain Blanc",1.00, "The bread hated by the SJW");

            ingredientResource.insert(painBlanc);
            ingredientResource.insert(new Ingredient(category,"Pain Complet",1.50, "To get healthy !"));
            ingredientResource.insert(new Ingredient(category,"Céréales",1.50, "A bread made with Froot Loops"));

            category = categoryResource.insert(new Category("Salade"));

            Ingredient mache = new Ingredient(category,"Laitue",1.00, "A salad with a french name");
            Ingredient roquette = new Ingredient(category,"Roquette",1.00, "A salad with a french name");

            ingredientResource.insert(mache);
            ingredientResource.insert(roquette);
            ingredientResource.insert(new Ingredient(category,"Mache",1.00, "A salad with a french name"));

            category = categoryResource.insert(new Category("Crudité"));

            Ingredient tomate = new Ingredient(category,"Tomate",1.50, "A tomato with a french name");
            Ingredient oignon = new Ingredient(category,"Onion",1.00, "It is gonna take the soul out of you");

            ingredientResource.insert(new Ingredient(category,"Carotte",1.50, "A carrot with a french name"));
            ingredientResource.insert(new Ingredient(category,"Concombre",1.50, "A cucumber with a french name"));
            ingredientResource.insert(tomate);
            ingredientResource.insert(oignon);

            category = categoryResource.insert(new Category("Charcuterie"));

            Ingredient jambon =new Ingredient(category,"Jambon",1.50, "Vegan people would hate you !");
            Ingredient bacon = new Ingredient(category,"Bacon",2.00, "Vegan people would hate you !");
            Ingredient jambonCru = new Ingredient(category,"Jambon cru",2.50, "Vegan people would hate you !");

            ingredientResource.insert(jambon);
            ingredientResource.insert(bacon);
            ingredientResource.insert(jambonCru);

            category = categoryResource.insert(new Category("Viande"));

            Ingredient merguez = new Ingredient(category,"Merguez",2.00, "An oriental sausage loved by a french YouTuber");
            Ingredient rosBeef = new Ingredient(category,"Rosbeef",2.00, "It's also the name of people who did Brexit");
            Ingredient poulet = new Ingredient(category,"Poulet",2.00, "A tribute to Colonel Sanders");

            ingredientResource.insert(merguez);
            ingredientResource.insert(rosBeef);
            ingredientResource.insert(poulet);
            ingredientResource.insert(new Ingredient(category,"Burger",3.00, "Vegan people would hate you !"));
            ingredientResource.insert(new Ingredient(category,"Confit",3.00, "Vegan people would hate you !"));

            category = categoryResource.insert(new Category("Fromage"));
            Ingredient emmental = new Ingredient(category,"Emmental",1.50, "Is it swiss or french ?");
            Ingredient cheddar = new Ingredient(category,"Cheddar",1.50, "How do you get a mouse to smile? Say cheese!");

            ingredientResource.insert(cheddar);
            ingredientResource.insert(emmental);
            ingredientResource.insert(new Ingredient(category,"Comté",1.50, "For strong people like you!"));

            category = categoryResource.insert(new Category("Sauce"));
            Ingredient beurre = new Ingredient(category,"Beurre",1, "Did you hear the rumour about butter? Never mind, I better not spread it.");
            Ingredient moutarde = new Ingredient(category,"Moutarde",1.50, "You don't choose mustard it's it that chooses you");
            Ingredient bbq = new Ingredient(category,"A1 Original Sauce® (BBQ)",1.75, "The sauce that makes everything taste better");
            ingredientResource.insert(beurre);

            ingredientResource.insert(new Ingredient(category,"Vinaigrette",0.50, "At first it was wine but something weird happened"));
            ingredientResource.insert(moutarde);
            ingredientResource.insert(bbq);


            sandwichResource.insert(new Sandwich(Sandwich.getSandwichSize0(),"The Chicken Ham","A sandwich made with Chicken and Ham", painBlanc, mache, tomate, jambon, poulet, emmental, beurre));
            sandwichResource.insert(new Sandwich(Sandwich.getSandwichSize1(),"Le Marseillais","Le sandwich qui craint dégun", painBlanc, roquette, oignon, bacon, merguez, cheddar, bbq, tomate));

            done = true;
        }
    }
}
