package utility;

import com.google.gson.JsonParseException;
import model.GameMap;

import java.io.*;
import java.util.*;

/**
 * This is the helper class which contains all the functionalities for File Handling
 */
public class FileHelper {
    /**
     * Loads the txt file that user chooses by loading countryGraph to config
     *
     * @param selectedFile input file from {@link javafx.stage.FileChooser}
     * @throws IllegalStateException if continent doesnot exist in territory
     */
    public static void loadToConfig(File selectedFile) throws IllegalStateException {
        GameMap.getInstance().clearInformation();
        try {
            FileReader fileReader = new FileReader(selectedFile);
            BufferedReader bufferReader = new BufferedReader(fileReader);

            String line;
            boolean statusContinent = false;
            boolean statusTerritories = false;
            while ((line = bufferReader.readLine()) != null) {
                if (line.equalsIgnoreCase("[Continents]")) {
                    statusContinent = true;
                    statusTerritories = false;
                    continue;
                }
                if (line.equalsIgnoreCase("[Territories]")) {
                    statusContinent = false;
                    statusTerritories = true;
                    continue;
                }
                if (statusContinent && !line.isEmpty() && !statusTerritories) {
                    String[] continent = line.split("=");
                    GameMap.getInstance().saveContinent(continent[0], Integer.valueOf(continent[1].trim()));

                }
                if (statusTerritories && !line.isEmpty() && !statusContinent) {
                    String[] data = line.replaceAll("\\d+ ,|\\d+,", "").split(",");
                    List<String> territories = Arrays.asList(data);
                    if (!GameMap.getInstance().saveCountry(territories)) {
                        throw new IllegalStateException("Continent does not exist");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the txt file that user chooses to edit
     *
     * @param selectedFile input file from {@link javafx.stage.FileChooser}
     * @return Hashmap of countryItems and continentItems
     */
    public static HashMap<String, List<String>> loadToForm(File selectedFile) {
        HashMap<String, List<String>> data = new HashMap<>();
        data.put("continent", new ArrayList<>());
        data.put("country", new ArrayList<>());
        try {
            FileReader fileReader = new FileReader(selectedFile);
            BufferedReader bufferReader = new BufferedReader(fileReader);

            String line;
            boolean statusContinent = false;
            boolean statusTerritories = false;
            while ((line = bufferReader.readLine()) != null) {
                if (line.equalsIgnoreCase("[Continents]")) {
                    statusContinent = true;
                    statusTerritories = false;
                    continue;
                }
                if (line.equalsIgnoreCase("[Territories]")) {
                    statusContinent = false;
                    statusTerritories = true;
                    continue;
                }
                if (statusContinent && !line.trim().isEmpty()) {
                    data.get("continent").add(line.trim());

                }
                if (statusTerritories && !line.trim().isEmpty() && !statusContinent) {
                    data.get("country").add(line.replaceAll("\\d+ ,|\\d+,", "").trim());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Saves the form inputs to .map file
     *
     * @param file            File to save map to
     * @param continentValues list of texts from continent fields
     * @param countryValues   list oftexts from country fields
     */
    public static void saveMapToFile(File file, List<String> continentValues, List<String> countryValues) {
        BufferedWriter bufferedWriter;
        FileWriter fileWriter;

        String EOL = System.getProperty("line.separator");

        try {
            fileWriter = new FileWriter(file, false);

            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("[CONTINENTS]" + EOL);
            for (String value : continentValues) {
                bufferedWriter.write(value + EOL);
            }
            bufferedWriter.write(EOL);
            bufferedWriter.write("[TERRITORIES]" + EOL);
            for (String value : countryValues) {
                bufferedWriter.write(value + EOL);
            }

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * empty continents and countries from config
     */
    public static void emptyConfig() {
        GameMap.getInstance().clearInformation();
    }


    /**
     * Writes the data to a game file with current datetime as filename
     *
     * @param gameData serialized gamData
     */
    public static void saveGameToFile(String gameData) {
        String filename = new Date().toString();
        saveGameToFile(gameData, filename);
    }

    /**
     * Writes the data to a game file with current datetime as filename
     *
     * @param gameData serialized gamData
     * @param filename filename for te save game
     */
    public static void saveGameToFile(String gameData, String filename) {
        try {
            File folder = new File("savedgames");
            folder.mkdir();
            File file = new File("savedgames/" + filename + ".game");
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.write(gameData);
            printWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the game from the file
     *
     * @param file saved game file
     */
    public static void loadGame(File file) throws JsonParseException, IllegalArgumentException {
        String[] fileName = file.getName().split("\\.");
        if (fileName.length != 2 || !fileName[1].equalsIgnoreCase("game")) {
            throw new IllegalArgumentException("Not a valid game file.");
        }
        try {
            String gameData = new Scanner(file).useDelimiter("\\A").next();
            GameMap.getInstance().restoreFromData(gameData);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File cannot be null.");
        }
    }

    /**
     * Function to write logs during game play
     *
     * @param message string to print
     */
    public static void writeLog(String message) {
        try {
            File folder = new File("logs");
            folder.mkdir();
            File file = new File("logs/gameLog");
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.append(message);
            printWriter.append("\n\n");
            printWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Function to write logs during game play and also to the system log
     *
     * @param message string to print
     * @param sysLog  true will write a system output log
     */
    public static void writeLog(String message, boolean sysLog) {
        writeLog(message);
        if (sysLog) System.out.println(message);
    }
}
