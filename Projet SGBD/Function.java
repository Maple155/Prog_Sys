package function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import relation.*;

public class Function {

    public Function() {
    }

    public static String readConf(String cheminFichier, String need) {
        try {

            String separateur = " ";
            String result = "";

            try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {

                String ligne;
                /*
                 * Exemple : PORT : 5000
                 */
                while ((ligne = br.readLine()) != null) {
                    String[] valeurs = ligne.split(separateur);

                    if (valeurs[0].equalsIgnoreCase(need)) {
                        result = valeurs[valeurs.length - 1];
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }


    public static void modifyLastLine(String filePath, String newLastLine) throws Exception {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        
        if (!lines.isEmpty()) {
            lines.set(lines.size() - 1,"PATH : " +newLastLine); 
        } else {
            System.out.println("Le fichier est vide. Ajout de la ligne PATH.");
            lines.add(newLastLine); 
        }


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }


    public static void deleteDirectory(File directory) throws IOException 
    {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        if (!directory.delete()) {
            throw new IOException("Échec de la suppression du fichier ou dossier : " + directory.getAbsolutePath());
        }
    }


    public static Relation triage(Relation initial, String Attribut_name, String type) 
    {
        // type : ASC, DESC

        try {
            Nuplets nuplet = null;
            Nuplets nuplet1 = null;
            Nuplets nuplet2 = null;
            Nuplets nuplet3 = null;

            int index = initial.getAttributs_relations().indexOf(Attribut_name);

            if (type.equalsIgnoreCase("DESC")) {
                for (int i = 0; i < initial.getNupl().size() - 1; i++) {
                    for (int j = 0; j < initial.getNupl().size() - i - 1; j++) {

                        double compare1 = Double
                                .parseDouble(initial.getNupl().get(j).getValeurs().get(index).toString());
                        double compare2 = Double
                                .parseDouble(initial.getNupl().get(j + 1).getValeurs().get(index).toString());

                        nuplet1 = initial.getNupl().get(j);
                        nuplet2 = initial.getNupl().get(j + 1);

                        if (compare1 < compare2) {
                            nuplet = nuplet1;
                            nuplet3 = nuplet1;
                            nuplet1 = nuplet2;
                            initial.getNupl().set(j, nuplet2);
                            nuplet2 = nuplet;
                            initial.getNupl().set(j + 1, nuplet3);
                        }
                    }
                }
            } else if (type.equalsIgnoreCase("ASC")) {
                for (int i = 0; i < initial.getNupl().size() - 1; i++) {
                    for (int j = 0; j < initial.getNupl().size() - i - 1; j++) {

                        double compare1 = Double
                                .parseDouble(initial.getNupl().get(j).getValeurs().get(index).toString());
                        double compare2 = Double
                                .parseDouble(initial.getNupl().get(j + 1).getValeurs().get(index).toString());

                        nuplet1 = initial.getNupl().get(j);
                        nuplet2 = initial.getNupl().get(j + 1);
                        
                        if (compare1 > compare2) {
                            nuplet = nuplet1;
                            nuplet3 = nuplet1;
                            nuplet1 = nuplet2;
                            initial.getNupl().set(j, nuplet2);
                            nuplet2 = nuplet;
                            initial.getNupl().set(j + 1, nuplet3);
                        }
                    }
                }
            }
            return initial;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

        //SUM(Attribute)
        public static Relation getSum(Relation initial, String attribut_name) {
            Relation sum = new Relation();
    
            try {
    
                String attribute = extractAttribute(attribut_name);
                int index = initial.getAttributs_relations().indexOf(attribute);
                double somme = 0;
    
                for (int i = 0; i < initial.getNupl().size(); i++) {
                    somme += Double.parseDouble(initial.getNupl().get(i).getValeurs().get(index).toString());
                }
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(somme);
    
                ArrayList<Attribut> attributes = new ArrayList<>();

                //tsy asina limite intsony ilay domaine rehefa atao somme
                Domaine d = new Domaine(initial.getAtt().get(index).getDomaine().getType_attribut(), 0);
                attributes.add(new Attribut(attribut_name, d));
    
                ArrayList<Nuplets> nuplets = new ArrayList<>();
                nuplets.add(new Nuplets(attributes, objects));
    
                sum = new Relation("SUM", attributes, nuplets);
    
                return sum;
    
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            return sum;
        }

    //AVG(Attribute)
    public static Relation getAvg(Relation initial, String attribut_name) {
        Relation avg = new Relation();

        System.out.println("Average: " + attribut_name);

        try {

            String attribute = extractAttribute(attribut_name);

            System.out.println("Extacting attribute: " + attribute);

            int index = initial.getAttributs_relations().indexOf(attribute);
            
            System.out.println("Extacting attribute: " + index);

            double somme = 0;
            double average = 0;
            for (int i = 0; i < initial.getNupl().size(); i++) {
                somme += Double.parseDouble(initial.getNupl().get(i).getValeurs().get(index).toString());
            }

            average = somme / initial.getNupl().size();

            ArrayList<Object> objects = new ArrayList<>();
            objects.add(average);

            ArrayList<Attribut> attributes = new ArrayList<>();
            attributes.add(new Attribut(attribut_name, initial.getAtt().get(index).getDomaine()));

            ArrayList<Nuplets> nuplets = new ArrayList<>();
            nuplets.add(new Nuplets(attributes, objects));

            avg = new Relation("AVG", attributes, nuplets);

            return avg;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return avg;
    }

    //MIN(Attribute) MAX(Attribute) 
    public static Relation getMinOrMax (Relation initial, String attribut_name) {
        Relation minOrMax = new Relation();
        
        try {
            double temp;
            Object remplacant;
            String relation_name = "";
            double result = 0;

            String attribute = extractAttribute(attribut_name);
            int index = initial.getAttributs_relations().indexOf(attribute);

            String function = extractFunction(attribut_name);

            initial = triage(initial, attribute, "ASC");

            if (function.equalsIgnoreCase("MIN")) {
                relation_name = "MIN";
                result = Double.parseDouble(initial.getNupl().get(0).getValeurs().get(index).toString());
            }
            else if (function.equalsIgnoreCase("MAX")) {
                relation_name  = "MAX";
                result = Double.parseDouble(initial.getNupl().get(initial.getNupl().size() - 1).getValeurs().get(index).toString());
            }

            ArrayList<Object> objects = new ArrayList<>();
            objects.add(result);

            ArrayList<Attribut> attributes = new ArrayList<>();
            attributes.add(new Attribut(attribut_name, initial.getAtt().get(index).getDomaine()));

            ArrayList<Nuplets> nuplets = new ArrayList<>();
            nuplets.add(new Nuplets(attributes, objects));

            minOrMax = new Relation(relation_name, attributes, nuplets);

            return minOrMax;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return minOrMax;
    }

    // MIN(Attribute) -> Attribute
    public static String extractAttribute(String expression) {
        // Définir une expression régulière pour capturer le contenu entre parenthèses
        Pattern pattern = Pattern.compile("\\(\\s*(\\w+)\\s*\\)");
        Matcher matcher = pattern.matcher(expression);

        // Vérifier si une correspondance est trouvée
        if (matcher.find()) {
            return matcher.group(1); // Retourne le contenu entre parenthèses
        }
        return null; // Aucun attribut trouvé
    }

    // MIN(Attribute) -> MIN
    public static String extractFunction(String expression) {
        // Définir une expression régulière pour capturer le nom de la fonction
        Pattern pattern = Pattern.compile("^(\\w+)\\s*\\(");
        Matcher matcher = pattern.matcher(expression);

        // Vérifier si une correspondance est trouvée
        if (matcher.find()) {
            return matcher.group(1); // Retourne le nom de la fonction
        }
        return null; // Aucune fonction trouvée
    }

}
