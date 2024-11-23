import java.awt.HeadlessException;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class SudokuTesting2 extends JFrame{
        public SudokuTesting2 () throws HeadlessException{
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Sudoku");
            this.setMinimumSize(new Dimension(800,600));

            JMenuBar menuBar = new JMenuBar();
            JMenu file = new JMenu("Game");
            JMenu newGame = new JMenu("New Game");

            JMenuItem sixBySixGame = new JMenuItem ("6 by 6 Game");
            JMenuItem nineByNineGame= new JMenuItem("9 by 9 Game");
            JMenuItem twelveByTwelveGame = new JMenuItem("12 by 12 Game");

            newGame.add(sixBySixGame);
            newGame.add(nineByNineGame);
            newGame.add(twelveByTwelveGame);

            file.add(newGame);
            menuBar.add(file);
            this.setJMenuBar(menuBar);
        }
        public static void main (String [] args){
            SwingUtilities.invokeLater(new Runnable(){

                public void run(){
                    SudokuTesting2 testing = new SudokuTesting2();
                    testing.setVisible(true);
                }
            });

        }
}
