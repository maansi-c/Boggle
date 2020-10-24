package assignment;

import java.io.IOException;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;

public class Boggle extends JComponent{

    private GameManager game;

    private static JFrame frame;

    public static void main(String [] args){
        Boggle b = new Boggle();
        b.createGUI();
    }

    public void createGUI(){
        frame = new JFrame("Boggle");
        frame.setMinimumSize(new Dimension(300, 300));
        JComponent container = (JComponent)frame.getContentPane();
        container.setLayout(new BorderLayout());

        container.add(this, BorderLayout.CENTER);
        container.add(createOutputPanel(), BorderLayout.EAST);

        Container panel = createUserInputPanel();

        JButton end = new JButton("End Turn");
        JButton restart = new JButton("Restart Game");
        Container buttons = new Box(BoxLayout.X_AXIS);

        buttons.add(restart);
        buttons.add(end);

        end.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.changePlayer();
                if (game.getCurrentPlayer() == game.getComputerPlayer()){
                    game.computerTurn();
                    textArea.append("\nComputer's Turn: \n");
                    for (String word: game.playerWords.get(game.getComputerPlayer())){
                        textArea.append(word+"\n");
                    }
                    int[] scores = game.getScores();
                    textArea.append("\n --- SCORES --- \n");

                    for (int s = 0; s < scores.length-1; s++){
                        textArea.append("Player " + (s+1) + " = " + scores[s] + "\n");
                    }
                    textArea.append("Computer = " + scores[scores.length-1]);
                    buttons.remove(end);
                    panel.remove(textField);
                    repaint();
                }
                else{
                    textArea.append("\nPlayer " + game.getCurrentPlayer() + "'s Turn: \n");
                    repaint();
                }
            }
        });

        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.setGame(game.newBoard());
                frame.setVisible(false);
                createGUI();
            }
        });

        createBoggleBoard();

        container.add(boggleBoard, BorderLayout.CENTER);
        JPanel buttonArea = new JPanel();
        buttonArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonArea.add(buttons);
        container.add(buttonArea, BorderLayout.SOUTH);
        container.add(panel, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }


    public Boggle(){
        game = new GameManager();
        GameDictionary trial = new GameDictionary();
        try {
            trial.loadDictionary("words.txt");
        }
        catch(IOException e){
            System.err.println("dictionary file not found");
            System.exit(1);
        }

        try {
            game.newGame(4, 1, "cubes.txt", trial);
        }
        catch(IOException e){
            if (e.getMessage().equals("size must be greater than 0")){
                System.err.println(e.getMessage());
            }
            else if (e.getMessage().equals("number of players must be greater than 0")){
                System.err.println(e.getMessage());
            }
            else{
                System.err.println("cubes file not found");
            }
            System.exit(1);
        }
    }

    private JTextField textField;
    private JTextArea textArea;
    private Container boggleBoard;
    private HashMap<Point, LetterSquare> squares;

    public void createBoggleBoard(){
        boggleBoard = new Container();
        squares = new HashMap<>();
        boggleBoard.setLayout(new GridLayout(game.getBoard().length,game.getBoard()[0].length));
        for (int i = 0; i < game.getBoard().length; i++) {
            for (int j = 0; j < game.getBoard()[i].length; j++) {
                LetterSquare l = new LetterSquare(game.getBoard()[i][j]);
                boggleBoard.add(l.getTile());
                squares.put(new Point(j, i), l);
            }
        }
    }

    public void changeText (Point p, int i){
        LetterSquare letter = squares.get(p);
        letter.setLetter(i);
    }

    public java.awt.Container createOutputPanel(){
        java.awt.Container panel = Box.createHorizontalBox();

        textArea = new JTextArea(5, 15);
        JScrollPane scrollPane = new JScrollPane(textArea);
        setMinimumSize(new Dimension(20, 50));
        textArea.setEditable(false);

        panel.add(scrollPane);
        return panel;
    }

    public java.awt.Container createUserInputPanel(){
        java.awt.Container panel = Box.createHorizontalBox();

        JLabel label = new JLabel("Enter a Word:");
        textField = new JTextField(15);
        textArea.append("Player 1's Turn:\n" );
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                int result = game.addWord(text,game.getCurrentPlayer());
                for (int i = 0; i < game.getBoard().length; i++) {
                    for (int j = 0; j < game.getBoard()[i].length; j++) {
                        changeText(new Point(j, i), -1);
                    }
                }
                if (result == 0){
                    textArea.append(game.getError()+"\n");
                }
                else {
                    textArea.append(text+ " = " + result + " points" + "\n");
                    for(Point p:game.getLastAddedWord()){
                        changeText(p, 1);
                    }
                    repaint();
                }
                textField.setText("");
            }
        });

        panel.add(Box.createRigidArea(new Dimension(100, textField.getHeight())));
        panel.add(label);
        panel.add(textField);
        panel.add(Box.createRigidArea(new Dimension(100, textField.getHeight())));
        return panel;
    }
}

class LetterSquare extends JComponent{
    private char letter;
    private Container square;
    private JPanel tile;
    private JTextArea letterArea;

    public LetterSquare(char l){
        letter = l;
        setLetter();
    }

    public void setLetter(){
        square = Box.createHorizontalBox();
        tile = new JPanel();
        tile.setAlignmentX(Component.CENTER_ALIGNMENT);
        tile.setAlignmentY(Component.CENTER_ALIGNMENT);

        letterArea = new JTextArea();
        Font font = new Font("Calibri", Font.PLAIN, 30);
        letterArea.setFont(font);
        letterArea.setEditable(false);
        letterArea.setText(String.valueOf(letter));
        square.add(letterArea);
        tile.add(square);
    }

    public void setLetter(int i){
        if (i == -1){
            Font font = new Font("Calibri", Font.PLAIN, 30);
            letterArea.setFont(font);
            letterArea.setEditable(false);
            letterArea.setText(String.valueOf(letter));
            square.add(letterArea);
            tile.add(square);
        }
        else if (i == 1){
            Font font = new Font("Calibri", Font.BOLD, 30);
            letterArea.setFont(font);
            letterArea.setEditable(false);
            letterArea.setText(String.valueOf(letter));
            square.add(letterArea);
            tile.add(square);
        }
    }

    public JPanel getTile(){
        return tile;
    }
}
