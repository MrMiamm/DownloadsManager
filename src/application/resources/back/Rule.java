package application.resources.back;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Rule implements Serializable {
	private transient StringProperty extensionProperty;
    private String extension; //Extension name

    private transient StringProperty operationProperty;
    private String operation; //Is file will be deleted or moved

    private transient StringProperty pathProperty;
    private String path; //Path where the file will be moved
    
    public final static String FILE_LOCATION = System.getenv("LOCALAPPDATA") + "\\Downloads Manager\\rules.dat";
	public final static String CSV_LOCATION = System.getenv("LOCALAPPDATA") + "\\Downloads Manager\\rules.csv";

    public Rule(String extension, String operation, String path) {
        this.extension = extension;
        this.operation = operation;
        this.path = path;
    }

    @Override
    public String toString() {
        return "Rule [extension=" + extensionProperty().get() + ", operation=" + operationProperty().get() + ", path=" + pathProperty().get() + "]";
    }

    public void saveCSV() {

    	File file = new File(CSV_LOCATION);
    	
    	try {
    		if (file.exists() == false) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_LOCATION, true))) {
            String line = extensionProperty().get() + "," + operationProperty().get() + "," + pathProperty().get();
            writer.write(line);
            writer.newLine();
            System.out.println("Enregistrement réussi !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Collection<Rule> loadAllRulesCSV() {
        Collection<Rule> all_rules = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_LOCATION))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String extension = parts[0];
                    String operation = parts[1];
                    String path = parts[2];

                    Rule rule = new Rule(extension, operation, path);
                    all_rules.add(rule);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return all_rules;
    }
    
    public void save() {
        Collection<Rule> existingRules = loadAll();

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(FILE_LOCATION))) {
            for (Rule rule : existingRules) {
                output.writeObject(rule);
            }
            output.writeObject(this);
            output.close();
            System.out.println("Enregistrement réussi !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Collection<Rule> loadAll() {
        Collection<Rule> existingRules = new ArrayList<>();
        Rule currentObject;

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(FILE_LOCATION))) {
            while ((currentObject = (Rule) input.readObject()) != null) {
                existingRules.add(currentObject);
            }
            input.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return existingRules;
    }

    public boolean equals(Rule other) {
        return extensionProperty().get().equals(other.extensionProperty().get())
                && operationProperty().get().equals(other.operationProperty().get())
                && pathProperty().get().equals(other.pathProperty().get());
    }

    public StringProperty extensionProperty() {
        if (extensionProperty == null) {
            extensionProperty = new SimpleStringProperty(extension);
        }
        return extensionProperty;
    }

    public StringProperty operationProperty() {
        if (operationProperty == null) {
            operationProperty = new SimpleStringProperty(operation);
        }
        return operationProperty;
    }

    public StringProperty pathProperty() {
        if (pathProperty == null) {
            pathProperty = new SimpleStringProperty(path);
        }
        return pathProperty;
    }

    public final String getExtension() {
        if (extensionProperty != null) {
            return extensionProperty.get();
        } else {
            return extension;
        }
    }

    public final void setExtension(String extension) {
        if (extensionProperty != null) {
            extensionProperty.set(extension);
        } else {
            this.extension = extension;
        }
    }

    public final String getOperation() {
        if (operationProperty != null) {
            return operationProperty.get();
        } else {
            return operation;
        }
    }

    public final void setOperation(String operation) {
        if (operationProperty != null) {
            operationProperty.set(operation);
        } else {
            this.operation = operation;
        }
    }

    public final String getPath() {
        if (pathProperty != null) {
            return pathProperty.get();
        } else {
            return path;
        }
    }

    public final void setPath(String path) {
        if (pathProperty != null) {
            pathProperty.set(path);
        } else {
            this.path = path;
        }
    }
}
