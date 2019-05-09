package model;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import model.strategy.PlayerStrategy;
import utility.FileHelper;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Singleton class containing access to all the game components
 */
public class GameMap extends Observable {

    /**
     * Instance of GameMap class
     */
    private transient static GameMap INSTANCE = null;
    /**
     * Counter for continent
     */
    private static int continentCounter;
    /**
     * Counter for country
     */
    private static int countryCounter;
    /**
     * Variable for currently playing player
     */
    @Expose
    public Player currentPlayer = null;
    /**
     * HashMap for countries
     */
    @Expose
    public HashMap<Integer, Country> countries;

    /**
     * Stack of cards for the game
     */
    @Expose
    public int cardStack;
    /**
     * HashMap for continents
     */
    @Expose
    public HashMap<Integer, Continent> continents;
    /**
     * HashMap for players
     */
    @Expose
    public HashMap<Integer, Player> players;
    /**
     * HashMap for relationship between country and adjacent countries
     */
    @Expose
    public HashMap<Integer, HashSet<Country>> countryGraph;

    /**
     * flag to check if game ended
     */
    public boolean gameEnded = false;
    /**
     * current phase
     */
    @Expose
    public Phase currentPhase = Phase.STARTUP;
    /**
     * previous phase
     */
    @Expose
    public Phase previousPhase = Phase.STARTUP;
    /**
     * status if state has changed
     */
    @Expose
    public boolean stateHasChanged = false;
    /**
     * stores the recent move made in the game
     */
    public String recentMove;
    /**
     * flag to check if game can be saved
     */
    public boolean canSave = true;
    /**
     * max number of rounds per game
     */
    public int maxRounds = 20;
    /**
     * flag to check if the game is tournament
     */
    public boolean tournamentMode;

    /**
     * hashmap to store the maps
     */
    public HashMap<Integer, File> maps;

    /**
     * hashmap to store the game number
     */
    public HashMap<Integer, Integer> gameNumbers;

    /**
     * stores the game number that is being played
     */
    public int gameNumberBeingPlayed;

    /**
     * stores the map id being played
     */
    public int mapBeingPlayed;

    /**
     * stores the tournament result
     * Map number
     * Game number
     * Winner
     */
    public HashMap<Integer, HashMap<Integer, Player>> tournamentModeWinners;

    /**
     * to avoid update of the countries on the view uptil all countries are assigned an owner in tournament mode
     */
    public boolean check;

    /**
     * variable for keeping the count of the loop for the game being played
     */
    public int loopForGameBeingPlayed = 0;

    /**
     * stores the players playing in the loop
     */
    public HashMap<Integer, Integer> playersForCountingLoop;

    /**
     * Initialize countries, continents, players, countryGraph
     * Initialize counter continentCounter, countryCounter
     */
    private GameMap() {
        countries = new HashMap<>();
        continents = new HashMap<>();
        players = new HashMap<>();
        countryGraph = new HashMap<>();
        maps = new HashMap<>();
        gameNumbers = new HashMap<>();
        tournamentModeWinners = new HashMap<>();
        continentCounter = 0;
        countryCounter = 0;
        cardStack = 0;
        check = true;
        loopForGameBeingPlayed = 0;
        FileHelper.writeLog("=================== NEW GAME =====================");
    }

    /**
     * @return returns the singleton instance of the class
     */
    public static GameMap getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GameMap();

        return INSTANCE;
    }

    /**
     * sets an id for player for tournament
     *
     * @param players hashmap of players
     */
    public void setPlayersForCountingLoop(HashMap<Integer, Player> players) {
        playersForCountingLoop = new HashMap<>();
        for (Integer playerId : players.keySet()) {
            playersForCountingLoop.put(playerId, 0);
        }
    }

    /**
     * steps for tournament mode
     *
     * @param startingTournament true if starting the first game of tournament
     */
    public void startTournamentMode(boolean startingTournament) {
        gameEnded = updateTournamentGameMapValues(startingTournament);

        if (gameEnded) {
            outputTournamentResults();
            int action = JOptionPane.showOptionDialog(null, "Tournament Ended", "", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);

            if (action == JOptionPane.OK_OPTION) {
                System.exit(0);
            }
        }
        // if tournament has not ended, load new map and move to reinforcement phase
        if (!gameEnded) {
            try {
                FileHelper.loadToConfig(maps.get(mapBeingPlayed));
            } catch (IllegalStateException exception) {
                System.out.println("File validation failed : " + exception.getMessage());
                return;
            }

            check = false;
            assignCountriesToPlayers();
            check = true;

            distributeInitialArmiesRandomly(players.values());
            changePhase(Phase.REINFORCE);
        }
    }

    /**
     * Updates the game number and map numberfor the tournament cycle
     *
     * @param startingTournament true if starting the first game of tournament
     */
    public boolean updateTournamentGameMapValues(boolean startingTournament) {
        boolean gameOver = false;
        if (startingTournament) {
            loopForGameBeingPlayed = 0;
            gameNumberBeingPlayed = 1;
            mapBeingPlayed = 1;
        } else if (gameNumberBeingPlayed < gameNumbers.get(mapBeingPlayed)) {
            loopForGameBeingPlayed = 0;
            for (Player player : players.values()) {
                players.replace(player.id, new Player(player.id, player.name, player.strategy));
            }
            gameNumberBeingPlayed++;
            setPlayersForCountingLoop(players);
        } else if (gameNumberBeingPlayed >= gameNumbers.get(mapBeingPlayed) && mapBeingPlayed < maps.size()) {
            loopForGameBeingPlayed = 0;
            for (Player player : players.values()) {
                players.replace(player.id, new Player(player.id, player.name, player.strategy));
            }
            mapBeingPlayed++;
            gameNumberBeingPlayed = 1;
            setPlayersForCountingLoop(players);
        } else {
            gameOver = true;
        }
        return gameOver;
    }

    /**
     * Writes the tournament result to the game log
     */
    private void outputTournamentResults() {
        FileHelper.writeLog("Tournament Results:", true);
        String mapsUsed = "|";
        int index = 1;
        for (File file : maps.values()) {
            mapsUsed += file == null ? " --- " : file.getName();
            mapsUsed += " [" + gameNumbers.get(index) + "] ";
            mapsUsed += "|";
            index++;
        }
        FileHelper.writeLog("Maps used: " + mapsUsed, true);
        FileHelper.writeLog("Players: " + players.values(), true);
        FileHelper.writeLog("Draw Limit: " + maxRounds, true);
        for (Map.Entry<Integer, HashMap<Integer, Player>> pair : tournamentModeWinners.entrySet()) {
            FileHelper.writeLog("=========================", true);
            FileHelper.writeLog("Map " + pair.getKey() + ":", true);
            HashMap<Integer, Player> winnerDetails = pair.getValue();
            gameNumberBeingPlayed = 1;
            for (Map.Entry<Integer, Player> innerPair : winnerDetails.entrySet()) {
                Player winner = innerPair.getValue();
                String winnerText = winner == null ? "DRAW" : winner.name + "(" + winner.strategy + ")";
                FileHelper.writeLog("Game " + innerPair.getKey() + ": " + winnerText, true);
            }
        }
    }

    /**
     * distributes the initial available armies to the player countries
     *
     * @param players list of players
     */
    public void distributeInitialArmiesRandomly(Collection<Player> players) {
        for (Player player : players) {
            int availableArmy = getInitialArmy();
            int remainingCountries = player.countries.size();
            int index = 0;
            for (Country country : player.countries) {
                int maxCount = availableArmy - remainingCountries + 1;
                if (index == player.countries.size() - 1) {
                    country.setNumOfArmies(maxCount);
                } else {
                    int random = new Random().nextInt(maxCount) + 1;
                    country.setNumOfArmies(random);
                    availableArmy -= random;
                    remainingCountries--;
                }
                index++;
            }
        }
    }

    /**
     * saves the continent and continent value from the file
     *
     * @param continent      gets continent from file
     * @param continentValue gets confinent value from file
     */
    public void saveContinent(String continent, int continentValue) {
        if (checkContinentExists(continent.trim()) == -1 && !continent.trim().isEmpty()) {
            continentCounter++;
            continents.put(continentCounter, new Continent(continentCounter, continent.trim(), continentValue));
        }
    }

    /**
     * To check if the continent is already added
     *
     * @param continentName Gets the continent name
     * @return false if continent doesnot exist
     */
    private int checkContinentExists(String continentName) {
        for (Map.Entry<Integer, Continent> continent : continents.entrySet()) {
            if (continent.getValue().name.equalsIgnoreCase(continentName)) {
                return continent.getKey();
            }
        }
        return -1;
    }

    /**
     * Updates the armies of countries in which armies are transferred
     *
     * @param numberOfArmiesTransfer armies user select to transfer
     * @param countrySelected        country which user select transfer from
     * @param neighbourSelected      country which user select transfer to
     */
    public void updateArmiesOfCountries(int numberOfArmiesTransfer, Country countrySelected, Country neighbourSelected) {
        setRecentMove(currentPlayer.name + " moved " + numberOfArmiesTransfer + " armies from " + countrySelected.name
                + " to " + neighbourSelected.name);
        int idOfCountry = countrySelected.id;
        countrySelected.deductArmies(numberOfArmiesTransfer);
        int idOfNeighbor = neighbourSelected.id;
        neighbourSelected.addArmies(numberOfArmiesTransfer);
        countries.put(idOfCountry, countrySelected);
        countries.put(idOfNeighbor, neighbourSelected);
    }

    /**
     * Saves the country and their neighbouring countries
     * While saving checks the validity of the map (Case : Continent for a country doesnot exist)
     *
     * @param territories List of all the countries along with its neighbouring countries
     * @return true if country is added to model else false
     */
    public boolean saveCountry(List<String> territories) {
        String country = territories.get(0);

        HashSet<Country> neighbours = new HashSet<>();
        String continent = territories.get(1).trim();
        int continentId = checkContinentExists(continent.trim());
        if (continentId == -1) {
            return false;
        } else {
            int countryId = this.checkCountryExists(country);

            countryId = this.insertCountry(countryId, country.trim());
            continents.get(continentId).countries.add(countries.get(countryId));

            for (String territory : territories.subList(2, territories.size())) {
                int neighbourCountryId = this.checkCountryExists(territory.trim());

                neighbourCountryId = this.insertCountry(neighbourCountryId, territory.trim());
                neighbours.add(countries.get(neighbourCountryId));
                this.saveToGraph(countryId, neighbours);
                countries.get(countryId).neighbours = neighbours;
            }

        }

        return true;
    }

    /**
     * inserts to country hashmap if the country doesnot exist
     *
     * @param countryId id of the country
     * @param country   name of the country
     * @return countryId
     */
    private int insertCountry(int countryId, String country) {
        if (countryId == -1) {
            countryId = ++countryCounter;
            countries.put(countryCounter, new Country(countryId, country));
        }
        return countryId;
    }

    /**
     * Saves the neighbour related to country in a countryGraph
     *
     * @param countryId  id of a country
     * @param neighbours set of neighbouring countries
     */
    private void saveToGraph(int countryId, HashSet<Country> neighbours) {
        countryGraph.put(countryId, neighbours);
    }

    /**
     * To check if the country is already added
     *
     * @param country Gets the country name
     * @return false if country doesnot exist
     */
    private int checkCountryExists(String country) {
        for (Map.Entry<Integer, Country> entry : countries.entrySet()) {
            if (entry.getValue().name.equalsIgnoreCase(country)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * It clears the data of HashMap
     */
    public void clearInformation() {
        continents.clear();
        countries.clear();
        countryGraph.clear();
        if (!tournamentMode) {
            players.clear();
        }
        countryCounter = 0;
        continentCounter = 0;
        currentPlayer = null;
        cardStack = 0;
    }

    /**
     * Get number of countriesOwned by every Players
     *
     * @return List with number of countries owned
     **/
    public List<Integer> getNumberOfCountriesOwnedByPlayers() {
        List<Integer> information = new ArrayList<>();
        for (Player player : players.values()) {
            information.add(player.countries.size());
        }
        return information;
    }

    /**
     * Get number of armies owned by every Players
     *
     * @return List with number of armies owned
     **/
    public List<Integer> getNumberOfArmiesOwnedByPlayers() {
        List<Integer> information = new ArrayList<>();
        for (Player player : players.values()) {
            information.add(player.getTotalArmies());
        }
        return information;
    }

    /**
     * Get number of continentsOwned by every Players
     *
     * @return List with number of continents owned
     **/
    public List<Integer> getNumberOfContinentsOwnedByPlayers() {
        List<Integer> information = new ArrayList<>();
        for (Player player : players.values()) {
            int count = 0;
            for (Continent continent : continents.values()) {
                if (continent.isOwnedBy(player)) count++;
            }
            information.add(count);
        }
        return information;
    }

    /**
     * initializes the card stack value for the game
     */
    public void setCardStack() {
        cardStack = countries.size();
    }

    /**
     * Assign countries randomly to players
     */
    public void assignCountriesToPlayers() {
        resetCurrentPlayer();
        setCardStack();
        for (Map.Entry<Integer, Country> entry : countries.entrySet()) {
            entry.getValue().owner = currentPlayer;

            entry.getValue().numOfArmies = 0;
            currentPlayer.initializeCountryToPlayer(entry.getValue());
            changeToNextPlayer(false);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Change the current player to next player in round robin fashion
     *
     * @param checkPlayerLost skips player with 0 countries if set to true
     */
    public void changeToNextPlayer(boolean checkPlayerLost) {
        if (currentPlayer == null)
            currentPlayer = players.get(1);
        else {
            int next = currentPlayer.id + 1;
            if (next > players.size())
                next = 1;

            currentPlayer = players.get(next);
            if (checkPlayerLost && currentPlayer.countries.size() == 0) changeToNextPlayer(checkPlayerLost);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * resets the current player to first player
     */
    public void resetCurrentPlayer() {
        if (!players.isEmpty()) {
            currentPlayer = players.get(1);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * provides initial number of army based on number of players
     *
     * @return number of armies
     */
    public int getInitialArmy() {
        int[] armyCount = new int[]{40, 35, 30, 25, 20};
        if (!players.isEmpty())
            return armyCount[players.size() - 2];

        return 0;
    }

    /**
     * changes the current phase and notifies observers
     *
     * @param phase new phase
     */
    public void changePhase(Phase phase) {
        if (loopForGameBeingPlayed > maxRounds) {
            setRecentMove("Game Over: It is a draw.");
            System.out.println("Game Over: It is a draw.");
            FileHelper.writeLog("========================= Game Over ========================== \n\n\n\n\n");

            if (tournamentMode) startNextGame(true);
            else {
                gameEnded = true;
                int action = JOptionPane.showOptionDialog(null, "Game was a draw", "Game Ended", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, null, null);

                if (action == JOptionPane.OK_OPTION) {
                    System.exit(0);
                }
            }
        } else {
            stateHasChanged = true;
            this.previousPhase = currentPhase;
            this.currentPhase = phase;
            setChanged();
            notifyObservers();
        }

        if (phase == Phase.REINFORCE) {
            for (Integer playerId : new HashMap<Integer, Integer>(playersForCountingLoop).keySet()) {
                if (players.get(playerId).countries == null || !(players.get(playerId).countries.size() > 0)) {
                    playersForCountingLoop.remove(playerId);
                }
            }
            if (currentPlayer.id == Integer.parseInt(String.valueOf(playersForCountingLoop.keySet().toArray()[0]))) {
                if (playersForCountingLoop.get(currentPlayer.id) < loopForGameBeingPlayed) {
                    playersForCountingLoop.replace(currentPlayer.id, loopForGameBeingPlayed);
                } else {
                    loopForGameBeingPlayed++;
                    playersForCountingLoop.replace(currentPlayer.id, loopForGameBeingPlayed);
                }
            } else {
                if (playersForCountingLoop.get(currentPlayer.id) != null) {
                    playersForCountingLoop.replace(currentPlayer.id, loopForGameBeingPlayed);
                }
            }
        }
    }

    /**
     * changes the recent move and notifies the observer
     *
     * @param move new move
     */
    public void setRecentMove(String move) {
        recentMove = move;
        setChanged();
        notifyObservers();

        FileHelper.writeLog(recentMove);
        recentMove = null;
    }

    /**
     * notifies all the observers for the new changes
     */
    public void notifyChanges() {
        setChanged();
        notifyObservers();
    }

    /**
     * A check to see if the current player has conquered all the countries
     */
    public void checkGameEnd() {
        if (currentPlayer.countries.size() == countries.size()) {
            setRecentMove("Game Over: " + currentPlayer.name + " wins the game.");
            FileHelper.writeLog("========================= Game Over ========================== \n\n\n\n\n");
            System.out.println("Game Over: " + currentPlayer.name + " wins the game.");

            if (tournamentMode) startNextGame(false);
            else gameEnded = true;
        } else {
            gameEnded = false;
        }

        if (gameEnded) {
            setChanged();
            notifyChanges();
        }
    }

    /**
     * starts next game of the tournament
     */
    private void startNextGame(boolean isDraw) {
        HashMap<Integer, Player> winnerDetails = tournamentModeWinners.get(mapBeingPlayed);
        if (winnerDetails == null) winnerDetails = new HashMap<>();
        winnerDetails.put(gameNumberBeingPlayed, isDraw ? null : currentPlayer);
        tournamentModeWinners.put(mapBeingPlayed, winnerDetails);

        previousPhase = Phase.STARTUP;
        currentPhase = Phase.STARTUP;
        FileHelper.writeLog("========================= New Game ========================== \n\n\n\n\n");
        startTournamentMode(false);
    }

    /**
     * restores serialized game data
     *
     * @param gameData serialized representation of game data
     */
    public void restoreFromData(String gameData) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PlayerStrategy.Strategy.class, new JsonDeserializer<PlayerStrategy.Strategy>() {
                    @Override
                    public PlayerStrategy.Strategy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String strategy = json.toString().replace("\"", "");
                        if (strategy.equals("Aggressive")) return PlayerStrategy.Strategy.AGGRESSIVE;
                        else if (strategy.equals("Benevolent")) return PlayerStrategy.Strategy.BENEVOLENT;
                        else if (strategy.equals("Random")) return PlayerStrategy.Strategy.RANDOM;
                        else if (strategy.equals("Cheater")) return PlayerStrategy.Strategy.CHEATER;
                        else return PlayerStrategy.Strategy.HUMAN;
                    }
                })
                .registerTypeAdapter(Card.TYPE.class, new JsonDeserializer<Card.TYPE>() {
                    @Override
                    public Card.TYPE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String type = json.toString().replace("\"", "");
                        if (type.equals("CAVALRY")) return Card.TYPE.CAVALRY;
                        else if (type.equals("ARTILLERY")) return Card.TYPE.ARTILLERY;
                        else return Card.TYPE.INFANTRY;
                    }
                })
                .registerTypeAdapter(Phase.class, new JsonDeserializer<Phase>() {
                    @Override
                    public Phase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String phase = json.toString().replace("\"", "");
                        if (phase.equals("STARTUP")) return Phase.STARTUP;
                        else if (phase.equals("ATTACK")) return Phase.ATTACK;
                        else if (phase.equals("FORTIFY")) {
                            return Phase.FORTIFY;
                        } else return Phase.REINFORCE;
                    }
                })
                .create();
        GameMap data = gson.fromJson(gameData, GameMap.class);
        copyFromModel(data);
    }

    /**
     * copies from another GameMap model
     *
     * @param data model to copy from
     */
    public void copyFromModel(GameMap data) {
        clearInformation();
        for (Player p : data.players.values()) {
            Player player = new Player(p.id, p.name, p.strategy);
            player.cards = p.cards;
            players.put(p.id, player);
        }
        for (Country c : data.countries.values()) {
            int ownerId = c.owner.id;
            Country country = new Country(c.id, c.name);
            country.numOfArmies = c.numOfArmies;
            country.owner = players.get(ownerId);
            players.get(ownerId).countries.add(country);
            countries.put(country.id, country);
        }
        for (Continent c : data.continents.values()) {
            ArrayList<Country> list = c.countries;
            Continent continent = new Continent(c.id, c.name, c.controlValue);
            continent.countries = new ArrayList<>();
            for (Country country : list) {
                continent.countries.add(countries.get(country.id));
            }
            continents.put(continent.id, continent);
        }
        for (Map.Entry<Integer, HashSet<Country>> entry : data.countryGraph.entrySet()) {
            Set<Country> set = entry.getValue();
            HashSet<Country> newSet = new HashSet<>();
            countries.get(entry.getKey()).neighbours = new HashSet<>();
            for (Country country : set) {
                countries.get(entry.getKey()).neighbours.add(countries.get(country.id));
                newSet.add(countries.get(country.id));
            }
            countryGraph.put(entry.getKey(), newSet);
        }
        currentPlayer = players.get(data.currentPlayer.id);
        currentPhase = data.currentPhase;
        previousPhase = data.previousPhase;
        setChanged();
        notifyChanges();
    }

    /**
     * adds dummy models for test cases
     */
    public void setDummyData() {
        Player player1 = new Player(1, "Player 1");
        players.put(1, player1);
        Player player2 = new Player(2, "Player 2");
        players.put(2, player2);
        Player player3 = new Player(3, "Player 3");
        players.put(3, player3);

        continents.put(1, new Continent(1, "Continent1", 5));
        continents.put(2, new Continent(2, "Continent2", 7));

        Country country1 = new Country(1, "Country 1");
        country1.owner = players.get(1);
        player1.initializeCountryToPlayer(country1);
        country1.numOfArmies = 10;
        Country country2 = new Country(2, "Country 2");
        country2.owner = players.get(2);
        player2.initializeCountryToPlayer(country2);
        country2.numOfArmies = 20;
        Country country3 = new Country(3, "Country 3");
        country3.owner = players.get(3);
        player3.initializeCountryToPlayer(country3);
        country3.numOfArmies = 40;
        Country country4 = new Country(4, "Country 4");
        country4.owner = players.get(3);
        player3.initializeCountryToPlayer(country4);
        country4.numOfArmies = 30;
        Country country5 = new Country(5, "Country 5");
        country5.owner = players.get(1);
        player1.initializeCountryToPlayer(country5);
        country5.numOfArmies = 3;
        Country country6 = new Country(6, "Country 6");
        country6.owner = players.get(2);
        player2.initializeCountryToPlayer(country6);
        country6.numOfArmies = 2;
        Country country7 = new Country(7, "Country 7");
        country7.owner = players.get(3);
        player3.initializeCountryToPlayer(country7);
        country7.numOfArmies = 1;
        Country country8 = new Country(8, "Country 8");
        country8.owner = players.get(1);
        player1.initializeCountryToPlayer(country8);
        country8.numOfArmies = 14;

        countries.put(1, country1);
        countries.put(2, country2);
        countries.put(3, country3);
        countries.put(4, country4);
        countries.put(5, country5);
        countries.put(6, country6);
        countries.put(7, country7);
        countries.put(8, country8);

        HashSet<Country> n1 = new HashSet<>();
        n1.add(country3);
        n1.add(country4);
        n1.add(country5);
        HashSet<Country> n2 = new HashSet<>();
        n2.add(country4);
        n2.add(country6);
        HashSet<Country> n3 = new HashSet<>();
        n3.add(country1);
        n3.add(country7);
        HashSet<Country> n4 = new HashSet<>();
        n4.add(country1);
        n4.add(country2);
        n4.add(country7);
        HashSet<Country> n5 = new HashSet<>();
        n5.add(country1);
        n5.add(country6);
        HashSet<Country> n6 = new HashSet<>();
        n6.add(country5);
        n6.add(country7);
        n6.add(country2);
        HashSet<Country> n7 = new HashSet<>();
        n7.add(country6);
        n7.add(country8);
        n7.add(country4);
        n7.add(country3);
        HashSet<Country> n8 = new HashSet<>();
        n8.add(country7);


        countryGraph.put(1, n1);
        country1.neighbours = n1;
        countryGraph.put(2, n2);
        country2.neighbours = n2;
        countryGraph.put(3, n3);
        country3.neighbours = n3;
        countryGraph.put(4, n4);
        country4.neighbours = n4;
        countryGraph.put(5, n5);
        country5.neighbours = n5;
        countryGraph.put(6, n6);
        country6.neighbours = n6;
        countryGraph.put(7, n7);
        country7.neighbours = n7;
        countryGraph.put(8, n8);
        country8.neighbours = n8;


        currentPlayer = players.get(1);
        cardStack = 8;
    }


    /**
     * enum for the phases of game
     */
    public enum Phase {
        /**
         * Enum values
         */
        STARTUP("StartUp Phase"),
        REINFORCE("Reinforcement Phase"),
        ATTACK("Attack Phase"),
        FORTIFY("Fortify Phase");
        /**
         * enum name
         */
        String name;

        /**
         * @param name name value for the enum
         */
        Phase(final String name) {
            this.name = name;
        }


        /**
         * Returns the name of this enum constant, as contained in the
         * declaration.
         *
         * @return the name of this enum constant
         */
        @Override
        public String toString() {
            return name;
        }
    }
}
