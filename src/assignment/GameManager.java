package assignment;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GameManager implements BoggleGame {

    private int players;
    private int currentPlayer;
    private int computerPlayer;
    private BoggleDictionary dictionary;
    private char[][] board;
    private SearchTactic searchTactic;
    HashMap<Integer, ArrayList<String>> playerWords;
    int[] scores;
    ArrayList<char[]> cubes;
    private String lastError;
    private ArrayList<Point> lastWord;

    @Override
    public void newGame(int size, int numPlayers, String cubeFile, BoggleDictionary dict) throws IOException {
        if (size <=0){
            throw new IOException("size must be greater than 0");
        }
        else if (numPlayers <= 0){
            throw new IOException("number of players must be greater than 0");
        }
        players = numPlayers;
        currentPlayer = 1;
        computerPlayer = numPlayers+1;
        dictionary = dict;
        board = new char[size][size];
        searchTactic = BoggleGame.SEARCH_DEFAULT;
        cubes = new ArrayList<>();
        playerWords = new HashMap<>();
        lastWord = new ArrayList<>();
        scores = new int[computerPlayer];
        Scanner scan = new Scanner(new File(cubeFile));
        while (scan.hasNextLine()) {
            String next = scan.next();
            next = next.toLowerCase();
            cubes.add(makeCube(next));
        }

        for (int i = 1; i <= computerPlayer; i++) {
            playerWords.put(i, new ArrayList<String>());
        }

        Random r = new Random();
        char[] letters = new char[cubes.size()];
        for (int i = 0; i < letters.length; i++) {
            int side = r.nextInt(cubes.get(0).length);
            letters[i] = cubes.get(i)[side];
        }
        //char [] letters = {'e','e', 'c', 'a', 'a', 'l', 'e', 'p', 'h', 'n', 'b', 'o', 'q','t','t','y'};
        lettersToGrid(letters);

        fisherYates(letters);
        lettersToGrid(letters);
    }

    public char[][] newBoard(){
        Random r = new Random();
        char[] letters = new char[cubes.size()];
        for (int i = 0; i < letters.length; i++) {
            int side = r.nextInt(cubes.get(0).length);
            letters[i] = cubes.get(i)[side];
        }

        lettersToGrid(letters);

        fisherYates(letters);
        lettersToGrid(letters);
        return board;
    }

    public void changePlayer(){
        currentPlayer = currentPlayer + 1;
    }
    public void nextPlayer(int player){
        currentPlayer = player;
    }

    public String checkValid(String word) {
        lastWord.clear();
        if (word.length() < 4) {
            return "Word is too short. Try again.";
        } else if (!onBoard(word)) {
            lastWord.clear();
            return "Word is not on the board. Try again.";
        } else if (!connected(word)) {
            lastWord.clear();
            return "Word is not valid. Try again.";
        } else if (alreadyUsed(word)) {
            lastWord.clear();
            return "Word has already been used. Try again.";
        }
        else if(!search(word)){
            return "Word does not exist. Try again.";
        }
        return null;
    }

    public boolean alreadyUsed(String word) {
        for (ArrayList<String> s: playerWords.values()){
            if (s.contains(word)){
                return true;
            }
        }
        return false;
    }

    public boolean onBoard(String word) {
        char[] letters = word.toCharArray();
        boolean exists = false;
        for (int letter = 0; letter < letters.length; letter++) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    if (letters[letter] == board[i][j]) {
                        exists = true;
                    }
                }
            }
            if (!exists) {
                return false;
            }
        }
        return true;
    }

    public boolean connected(String word) {
        char[] letters = word.toCharArray();
        ArrayList<ArrayList<Point>> possiblePoints = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (letters[0] == (board[r][c])) {
                    ArrayList<Point> path = new ArrayList<>();
                    path.add(new Point(c, r));
                    possiblePoints.add(path);
                }
            }
        }

        if (possiblePoints.size() == 0) {
            return false;
        } else {
            for (ArrayList<Point> path : possiblePoints) {
                if(findPath(letters, path, possiblePoints)){
                    lastWord = path;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findPath(char[] letters, ArrayList<Point> p, ArrayList<ArrayList<Point>> paths) {
        if (p.size() == letters.length) {
            paths.add(p);
            return true;
        }

        boolean found = false;
        char letter = letters[p.size()];
        for (int r = -1; r <= 1; r++) {
            if (p.get(p.size() - 1).y + r >= 0 && p.get(p.size() - 1).y + r < board.length) {
                for (int c = -1; c <= 1; c++) {
                    if (p.get(p.size() - 1).x + c >= 0 && p.get(p.size() - 1).x + c < board[0].length) {
                        if (letter == (board[p.get(p.size() - 1).y + r][p.get(p.size() - 1).x + c]) && !p.contains(new Point(p.get(p.size() - 1).x + c, p.get(p.size() - 1).y + r))) {
                            p.add(new Point(p.get(p.size() - 1).x + c, p.get(p.size() - 1).y + r));
                            if(findPath(letters, p, paths)){
                                return true;
                            }
                            else{
                                p.remove(p.size()-1);
                            }
                        }
                    }
                }
            }
        }
        if (!found){
            return false;
        }
        return true;
    }

    public void lettersToGrid(char[] letters) {
        int i = 0;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[0].length; x++) {
                board[y][x] = letters[i];
                i++;
            }
        }
    }

    public char[] makeCube(String line) {
        char[] sides = line.toCharArray();
        return sides;
    }

    @Override
    public char[][] getBoard() {
        return board;
    }

    @Override
    public int addWord(String word, int player) {
        ArrayList<String> currentWords = playerWords.get(player);
        currentPlayer = player;
            word = word.toLowerCase();
            lastError = checkValid(word);
            if (lastError != null) {
                return 0;
            } else {
                currentWords.add(word);
                scores[player-1]+= word.length()-3;
                return word.length()-3;
            }
    }

    public int getComputerPlayer(){
        return computerPlayer;
    }

    public int computerTurn(){
        ArrayList<String> compWords = (ArrayList<String>) getAllWords();
        int score = 0;
        for(String s : compWords){
            score += s.length()-3;
        }
        playerWords.put(currentPlayer, compWords);
        scores[currentPlayer-1] = score;
        return score;
    }

    public String getError() {
        return lastError;
    }

    @Override
    public List<Point> getLastAddedWord() {
        return lastWord;
    }

    @Override
    public void setGame(char[][] board) {
        this.board = board;
        currentPlayer = 1;
        ((GameDictionary)dictionary).getDictionary().clearSearched(((GameDictionary)dictionary).getDictionary().getRoot());
        for (int i = 1; i <= players+1; i++){
            playerWords.get(i).clear();
        }
        lastError = null;
        lastWord = new ArrayList<>();
        scores = new int[players+1];
    }

    @Override
    public Collection<String> getAllWords() {
        if (searchTactic.equals(SearchTactic.SEARCH_BOARD)) {
            ArrayList<String> compWords = new ArrayList<>();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    searchBoard(i, j, "", new boolean[board.length][board[0].length], compWords);
                }
            }
            return compWords;
        }
        else{
            ArrayList<String> compWords = searchDictionary();
            return compWords;
        }
    }

    @Override
    public void setSearchTactic(SearchTactic tactic) {
        if (tactic.equals(SearchTactic.SEARCH_BOARD)) {
            searchTactic = SearchTactic.SEARCH_BOARD;
        } else if (tactic.equals(SearchTactic.SEARCH_DICT)) {
            searchTactic = SearchTactic.SEARCH_DICT;
        } else {
            searchTactic = BoggleGame.SEARCH_DEFAULT;
        }
    }

    public boolean search(String word){
        return dictionary.contains(word);
    }

    @Override
    public int[] getScores() {
        return scores;
    }

    public static void fisherYates(char[] array) {
        Random r = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = r.nextInt(i);
            char temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public String toString() {
        String b = "";
        for (char[] c : board) {
            b = b + Arrays.toString(c) + "\n";
        }
        return b;
    }

    public void searchBoard(int x, int y, String stem, boolean[][] checked, ArrayList<String> currentWords) {
        char letter = board[y][x];
        stem = stem + letter;
        if (dictionary.contains(stem) && stem.length() >= 4){
            boolean add = true;
            for(ArrayList<String> s: playerWords.values()){
                if (s.contains(stem)){
                    add = false;
                }
            }
            if (currentWords.contains(stem)){
                add = false;
            }
            if (add){
                currentWords.add(stem);
            }
        }

        if (dictionary.isPrefix(stem)) {
            checked[y][x] = true;
            for (int row = -1; row <= 1; row++) {
                for (int column = -1; column <= 1; column++) {
                    if ((y + row) >= 0 && (y + row) < board.length && (x + column) >= 0 && (x + column) < board[0].length && !checked[y + row][x + column]) {
                        searchBoard(x + column, y + row, stem, checked, currentWords);
                    }
                }
            }
        }
        stem = stem.substring(0, stem.length() - 1);
        checked[y][x] = false;
    }

    public ArrayList<String> searchDictionary() {
        ArrayList<String> words = new ArrayList<>();
        Iterator<String> iterator = dictionary.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            if (word.length() >= 4){
                boolean add = true;
                for(ArrayList<String> s: playerWords.values()){
                    if (s.contains(word)){
                        add = false;
                    }
                }
                if (words.contains(word)){
                    add = false;
                }
                if (add){
                    words.add(word);
                }
            }
        }
        return words;
        }

        public int getCurrentPlayer(){
            return currentPlayer;
        }
    }
