/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagegame;
//import java.awt.*;
import java.awt.Color;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author ruhuayan
 */
public class IGMenuBar extends JMenuBar implements ActionListener{
        //private final JMenuBar bar;
        private final JMenu gameJMenu;
        private final JMenuItem newJMItem;
        private final JMenuItem scoreJMItem;
        private final JMenuItem exitJMItem;
        private final JMenuItem helpJMItem;
        private final JMenuItem budaJMItem;
        private final JMenuItem ziyiJMItem;
        private final JMenuItem chooseJMItem;
        private final JMenuItem level1JMItem;
        private final JMenuItem level2JMItem;
        private final JMenuItem level3JMItem;
        private final JMenuItem castleJMItem;
        private final JMenuItem cameraJMItem;
        
        
    public IGMenuBar() {
        this.add(Box.createHorizontalStrut( 3 ) );
        gameJMenu = new JMenu("Game");
        //gameJMenu.setBackground(Color.LIGHT_GRAY);
        //gameJMenu.setOpaque(true);
        gameJMenu.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        newJMItem = new JMenuItem("New     CTRL+N");
        newJMItem.setMnemonic(KeyEvent.VK_N);
        gameJMenu.add(newJMItem);
        
        scoreJMItem = new JMenuItem("Score   CTRL+S");
        scoreJMItem.setMnemonic(KeyEvent.VK_S);
        scoreJMItem.addActionListener(IGMenuBar.this);
        gameJMenu.add(scoreJMItem);
        
        exitJMItem = new JMenuItem("Exit      CTRL+X");
        exitJMItem.setMnemonic(KeyEvent.VK_X);
        exitJMItem.addActionListener(IGMenuBar.this);
        gameJMenu.add(exitJMItem);
        this.add(gameJMenu);
        
        this.add(Box.createHorizontalStrut( 3 ) );
        JMenu pictureJMenu = new JMenu("Picture");
        budaJMItem = new JMenuItem("Buda (500 x 600)");
        ziyiJMItem = new JMenuItem("Ziyi Z(330 x 480)");
        castleJMItem = new JMenuItem("M.Lisa (330 x 480");
        cameraJMItem = new JMenuItem("Take a picture");
        chooseJMItem = new JMenuItem("Choose your own picture");
        
        pictureJMenu.add(budaJMItem);
        pictureJMenu.add(ziyiJMItem);
        pictureJMenu.add(castleJMItem);
        pictureJMenu.add(chooseJMItem);
        pictureJMenu.add(cameraJMItem);
    
        //pictureJMenu.setOpaque(true);
        pictureJMenu.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        this.add(pictureJMenu);
        
        this.add(Box.createHorizontalStrut( 3 ) );
        JMenu levelJMenu = new JMenu("Level");
        level1JMItem = new JMenuItem("Level 1 - (3 x 3)");
        level2JMItem = new JMenuItem("Level 2 - (4 x 4)");
        level3JMItem = new JMenuItem("Level 3 - (5 x 5)");
        
        levelJMenu.add(level1JMItem);
        levelJMenu.add(level2JMItem);
        levelJMenu.add(level3JMItem);
    
        //pictureJMenu.setOpaque(true);
        levelJMenu.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        this.add(levelJMenu);
        
        this.add(Box.createHorizontalStrut( 3 ) );
        JMenu aboutJMenu = new JMenu("About");
        helpJMItem = new JMenuItem("Help   CTRL+H");
        helpJMItem.addActionListener(IGMenuBar.this);
        helpJMItem.setMnemonic(KeyEvent.VK_H);
        aboutJMenu.add(helpJMItem);
        //aboutJMenu.setBackground(Color.LIGHT_GRAY);
        //aboutJMenu.setOpaque(true);
        aboutJMenu.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        this.add(aboutJMenu);
        //this.setBackground(Color.LIGHT_GRAY);
        //this.add(bar);
    }//end constructor

    @Override
    public void actionPerformed(ActionEvent ae) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //System.out.println(ae.getActionCommand()+" "+ae.getSource());
        //if(ae.getSource()==newJMItem)  {}
        if(ae.getSource()==scoreJMItem){
            Path filePath = Paths.get("imgs/record.txt");
            List<String> records = new ArrayList<>();
            StringBuilder output = new StringBuilder("The Scrores are: \n______________________________");
            if (Files.exists(filePath))
                try {
                    records = Files.readAllLines(filePath);
                    int num = records.size();
                    System.out.println(num);
                    if(num >0){
                        //alway get last 5 results from Arraylist
                        int last5numbers = num >=6 ? 6 : num;
                        for (int i=1;i<=last5numbers; i++){
                            output.append("\nPlayer Name:            ");
                            String temp = records.get(num-i).toString();
                            temp = temp.replaceFirst("\t","\nCompleted Time:        ");
                            temp = temp.replaceAll("\t", "\n@: ");
                            output.append(temp);
                            output.append("\n______________________________");
                            //output.append(records.get(i));
                        }
                    }
            } catch (IOException ex) {
                
                Logger.getLogger(IGMenuBar.class.getName()).log(Level.SEVERE, null, ex);
            }else 
                output.append("\nThere is no scores currently!");
            JOptionPane.showMessageDialog(null, output, "Scores", JOptionPane.PLAIN_MESSAGE);
        }
        if(ae.getSource()== exitJMItem){ System.exit(0);}
        if(ae.getSource() == helpJMItem){
           JOptionPane.showMessageDialog(null,"How to play: \n\t1: ................\n\t2: ................\n\t3: ..................","Need a help ?",JOptionPane.PLAIN_MESSAGE);
        }
        
    }
    public JMenuItem getNewJMItem(){
        return newJMItem;
    }
    public JMenuItem getBudaJMItem(){
        return budaJMItem;
    }
    public JMenuItem getZiyiJMItem(){
        
        return ziyiJMItem;
    }
    public JMenuItem getChooseJMItem(){
        return chooseJMItem;
    }
    public JMenuItem getLevel1JMItem(){
        return level1JMItem;
    }
    public JMenuItem getLevel2JMItem(){
        return level2JMItem;
    }
    public JMenuItem getLevel3JMItem(){
        return level3JMItem;
    }
    public JMenuItem getCastleJMItem(){
        return castleJMItem;
    }
    public JMenuItem getCameraJMItem(){
        return cameraJMItem;
    }
}
