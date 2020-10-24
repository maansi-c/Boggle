package assignment;
import java.util.ArrayList;

public class Trie {
    private TrieNode root;
    private TrieNode current;
    private String currentWord;

    public Trie(){
        root = new TrieNode(null);
        current = root;
        currentWord = "";
    }

    public TrieNode getRoot(){
       return root;
    }

    public String getCurrentWord(){
       return currentWord;
    }

    public void insert(String word){
        TrieNode current = root;
        Character c;
        for (int i = 0; i < word.length(); i++){
            c = word.charAt(i);
            if (current.hasChild(c)){
                current.addWord();
                current = current.getChild(c);
            }
            else{
                TrieNode child = new TrieNode(c);
                current.addChild(child);
                child.setParent(current);
                current = current.getChild(c);
            }
            if (i == word.length()-1){
                current.setEnd();
            }
        }
    }

    public boolean search(String word){
        current = root;
        Character c;
        for (int i = 0; i < word.length(); i++){
            c = word.charAt(i);
            if (current.hasChild(c)){
                current = current.getChild(c);
                if (i == word.length()-1 && !current.getEnd()){
                    return false;
                }
            }
            else{
                return false;
            }
        }
        currentWord = word;
        return true;
    }

    public boolean prefix(String word){
        TrieNode current = root;
        Character c;
        for (int i = 0; i < word.length(); i++){
            c = word.charAt(i);
            if (current.hasChild(c)){
                current = current.getChild(c);
            }
            else{
                return false;
            }
        }
        return true;
    }

    public String next(String word){
       if (word == null){
           current = root;
       }
       else {
           search(word);
       }
        if (current.getChildrenSearched().contains(false)){
            for (int i = 0; i < current.getChildren().size(); i++){
                if (current.getChildrenSearched().get(i) == false){
                    current = current.getChildren().get(i);
                    currentWord = currentWord + current.getLetter();
                    if (search(currentWord)){
                        return currentWord;
                    }
                    return next(currentWord);
                }
            }
        }
        else{
            goBack();
            while (!(current.getChildrenSearched().contains(false))){
                goBack();
            }
            next(currentWord);
        }
        return currentWord;
    }

    public void clearSearched(TrieNode n){
       current = n;
       if (current.getChildren().size() != 0){
            current.resetSearched();
           for (TrieNode child: current.getChildren()){
                clearSearched(child);
           }
       }
    }

    public void goBack(){
        current = current.getParent();
        int index = current.getChildren().indexOf(current.getChild(currentWord.charAt(currentWord.length()-1)));
        current.getChildrenSearched().set(index, true);
        currentWord = currentWord.substring(0, currentWord.length()-1);
    }

    public boolean hasNext(String word){
        if (word == null){
            current = root;
        }
        else {
            search(word);
        }
        if (current.getChildrenSearched().contains(false)){
            return true;
        }
        else{
            goBack();
            while (!(current.getChildrenSearched().contains(false))){
                    if (currentWord==""){
                        return false;
                    }
                    goBack();
                }
                if (hasNext(currentWord)){
                    return true;
                }
                else{
                    return false;
                }
        }
    }
    //first = first child of the current node
    //search = search for a string
    //add = adding a word

}

class TrieNode{
    private Character letter;
    private ArrayList<TrieNode> next;
    private ArrayList<Boolean> searched;
    private TrieNode parent;
    private int words;
    private boolean end;

    public TrieNode(Character c){
        parent = null;
        letter = c;
        next = new ArrayList<>();
        searched = new ArrayList<>();
        words = 0;
        end = false;
    }

    public boolean getEnd(){
        return end;
    }

    public int getWordCount(){
        return words;
    }

    public ArrayList<TrieNode> getChildren(){
        return next;
    }

    public ArrayList<Boolean> getChildrenSearched(){
        return searched;
    }

    public void setEnd(){
        end = true;
    }

    public void addChild(TrieNode child){
        next.add(child);
        searched.add(false);
        addWord();
    }

    public void setParent(TrieNode p){
        parent = p;
    }
    public void addWord(){
        words++;
    }

    public char getLetter(){
        return letter;
    }

    public boolean hasChild(Character c){
        for (int i = 0; i < next.size(); i++){
            if (next.get(i).getLetter() == c){
                return true;
            }
        }
        return false;
    }

    public TrieNode getChild(Character c){
        for (int i = 0; i < next.size(); i++){
            if (next.get(i).getLetter() == c){
                return next.get(i);
            }
        }
        return null;
    }

    public void resetSearched(){
        for (boolean b: searched){
            b = false;
        }
    }
    public TrieNode getParent(){
        return parent;
    }
}
