package assignment;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameDictionary implements BoggleDictionary, Iterable<String>{

    private Trie dictionary;
    private String currentWord;
    public ArrayList<String> words;

    @Override
    public void loadDictionary(String filename) throws IOException {
        words = new ArrayList<>();
        dictionary = new Trie();
        currentWord = null;
        Scanner scan = new Scanner(new File(filename));
        while(scan.hasNextLine()){
            boolean valid = true;
            String next = scan.nextLine();
            for (int i = 0; i < next.length(); i++){
                if (!Character.isLetter(next.charAt(i))){
                    valid = false;
                }
            }
            if (valid){
                dictionary.insert(next);
                words.add(next);
            }
        }
    }

    @Override
    public boolean isPrefix(String prefix) {
        return dictionary.prefix(prefix);
    }

    @Override
    public boolean contains(String word) {
        return dictionary.search(word);
    }

    public void setCurrentWord(String word){
        currentWord = word;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>(){
            @Override
            //iterate through trie...
            public boolean hasNext() {
                if (currentWord == null) {
                    return true;
                }
                return getDictionary().hasNext(currentWord);
            }

            @Override
            public String next() {
                currentWord = dictionary.next(currentWord);
                return currentWord;
            }
        };
    }

    public Trie getDictionary(){
        return dictionary;
    }
}
