/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagegame;

/**
 *
 * @author ruhuayan
 */


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.filechooser.FileNameExtensionFilter;



public class ImageGame extends JFrame{
    private ImageJPanel imgJPanel;
    private int mouseX;
    private int mouseY;
    private JLabel imgJLabel;
    private int tileImgX;
    private int tileImgY;
    private final int tileImgW;
    private final int tileImgH;
    private int numberOfEmptyChunk;
    private int numberOfChunk;
    private int rows =4;
    private int cols = 4;
    //private final File file;
    private int moveX; 
    private int moveY;
    private final TimeJPanel timerJPanel;
    private Task task;
    private final IGMenuBar bar;
    private BufferedImage image;
    private Clip clip;
    
    public ImageGame(BufferedImage wholeImage, int r, int c){
        super("Tiled Image Game");
        setLayout(new BorderLayout());
        
        cols = c;
        rows = r;
        try {
            File ifile = new File("imgs/icon.png");
            Image im = ImageIO.read(new FileInputStream(ifile));
            ImageGame.this.setIconImage(im);
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
       
        bar = new IGMenuBar();
        setJMenuBar(bar);
        
        this.image = wholeImage;
        imgJPanel = new ImageJPanel(image, rows, cols);
        add(imgJPanel,BorderLayout.NORTH);
 //     to set the empty Chunk       
        setNumberOfEmptyChunk(rows*cols+(cols-1));
        setNumberOfChunk(-1);
        
        timerJPanel = new TimeJPanel();
        add(timerJPanel,BorderLayout.SOUTH);
        JButton showHintJButton = timerJPanel.getHintJButton();
        showHintJButton.addActionListener((ActionEvent ae) ->{
            
            imgJPanel.showNumberHint();
            showHintJButton.setEnabled(false);
        });
        
        JButton pictJButton = timerJPanel.getPictJButton();
        pictJButton.addActionListener((ActionEvent ae)->{
            //BufferedImage img = imgJPanel.getWholeImage();
            Image scaleImage = image.getScaledInstance(image.getWidth()/2, image.getHeight()/2, Image.SCALE_FAST);
            JOptionPane.showMessageDialog(ImageGame.this, "", "Hint", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(scaleImage));
        });
        tileImgW = imgJPanel.getChunkWidth();
        tileImgH = imgJPanel.getChunkHeight();
        
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("Click.wav"));
            clip = AudioSystem.getClip();
            clip.open(ais);
                                
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                 Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.addMouseListener(
                new MouseAdapter(){
                    @Override
                    public  void mousePressed(MouseEvent me){
                
                        mouseX= me.getX();
                        mouseY = me.getY();
                        if(!imgJPanel.testBound(mouseX, mouseY)) setNumberOfChunk(-1);
                        else {
                          
                           //int noc = (int)(mouseY/imageH * rows) + (int)(mouseX/imageW);//this will cause thread prob
                
                            int noc = imgJPanel.getNumberOfChunk(mouseX, mouseY);                      
                            imgJLabel = imgJPanel.getImgJLabel(noc);
                    
                            int noec = getNumberOfEmptyChunk();
                            setNumberOfChunk(noc);
                            //(int)(rows*imgJLabel.getY()/imageH)+(int)(imgJLabel.getX()/imageW);
                            //System.out.println("Mouse pressed "+noec +" "+noc +" chunk available: " + testAvailability(noec,noc))
                           
                            if(testAvailability(noec,noc)){
                                
                                tileImgX=imgJLabel.getX();
                                tileImgY=imgJLabel.getY();}
                            }
                    }
                    @Override
                    public void mouseReleased(MouseEvent me){
                        int noec = getNumberOfEmptyChunk();
                        int noc = getNumberOfChunk();
                        if(testAvailability(noec, noc)){
                            //swapChunk(noec,noc);
                            imgJPanel.swapJLabel(noec,noc);
                            setNumberOfEmptyChunk(noc);
                            setNumberOfChunk(noec);
                            
                            
                            clip.setFramePosition(0);
                            clip.start();
                            //System.out.println("empty: "+ getNumberOfEmptyChunk() +" moved: "+getNumberOfChunk());
                            if(imgJPanel.checkResult()){
                                noec = getNumberOfEmptyChunk();
                                //System.out.println("empty: "+noec);
                                imgJPanel.swapJLabel(noec, noec+cols);
                                setNumberOfEmptyChunk(noec+cols);
                                JOptionPane.showMessageDialog(ImageGame.this, "You Win !");
                                imgJPanel.shuffleJLabels(-1);
                                String record =timerJPanel.getJLabelName() + "\t"+ timerJPanel.getTimerText()+ "\t"+
                                        String.format(" %s%n", LocalDateTime.now())+"";
                                writeToFile(record);
                                timerJPanel.reTimer();
                            }
                        }
                       // System.out.println("Mouse released "+noec +" "+noc);
                    }
         
                }
        );
        this.addMouseMotionListener(
                new MouseMotionAdapter(){
                    @Override
                    public void mouseDragged(MouseEvent me){
                        int noec = getNumberOfEmptyChunk();
                        int noc = getNumberOfChunk();
                        if(testAvailability(noec,noc)){
                            
                            if(Math.abs(noec-noc)==1){
                                moveX = me.getX()- mouseX;
                                //moveX = ((noec-noc)==1)&&(moveX>0)?moveX :0;
                                //moveX = ((noec-noc)==-1)&&(moveX<0)?moveX:0;
                                moveY = 0;
                                if((((noec-noc)==1)&&(moveX>=0))||((noec-noc)==-1)&&(moveX<=0)){
                                    if(Math.abs(moveX)<=tileImgW)
                                        imgJLabel.setBounds(tileImgX+moveX,tileImgY+moveY,tileImgW,tileImgH);
                                }
                            //imgJPanel.add(imgJLabel);
                            }else if(Math.abs(noec-noc)==4){
                                moveX = 0;
                                moveY = me.getY()-mouseY ;
                                //moveY = ((noec-noc)==4) && (moveY>0)? moveY : 0;
                                //moveY = ((noec-noc)==-4) && (moveY<0)? moveY :0;
                                if((((noec-noc)==cols) && (moveY>=0))||((noec-noc)==-cols) && (moveY<=0)){
                                    if(Math.abs(moveY)<=tileImgH)
                                        imgJLabel.setBounds(tileImgX+moveX,tileImgY+moveY,tileImgW,tileImgH);
                                }
                            }                                
                        }//end if(focus)   
                    }
                }
        );
        
        // Game - New button - To reshuffle JLabels by using Task doInBackground
        JMenuItem newJMItem = bar.getNewJMItem();
        newJMItem.addActionListener((ActionEvent ae)->{
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            task = new Task();
            //imgJPanel.shuffleJLabels(this.getNumberOfEmptyChunk());
            task.execute();
            timerJPanel.reTimer();
            //timerJPanel.scheduleTimer();
            this.pack();
        });
        //picture Buda button
        JMenuItem budaJMItem = bar.getBudaJMItem();
        budaJMItem.addActionListener((ActionEvent ae)->{
            try {
                createFrame(loadImage(new File("imgs/buda.jpg")),rows, cols,timerJPanel.getJLabelName());
                
            } catch (IOException ex) {
                Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //picture Ziyi button
        JMenuItem ziyiJMItem = bar.getZiyiJMItem();
        ziyiJMItem.addActionListener((ActionEvent ae)->{
            try {
                createFrame(loadImage(new File("imgs/ziyi.jpg")),rows, cols, timerJPanel.getJLabelName());
            } catch (IOException ex) {
                Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //Picture Castle button
        JMenuItem castJMItem = bar.getCastleJMItem();
        castJMItem.addActionListener((ActionEvent ae)->{
            try {
                createFrame(loadImage(new File("imgs/monalisa.jpg")),rows, cols,timerJPanel.getJLabelName());
            } catch (IOException ex) {
                Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //choose Button
        JMenuItem chooseJMItem = bar.getChooseJMItem();
        chooseJMItem.addActionListener((ActionEvent ae)->{
       
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif","png");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                 try {
                    BufferedImage img = loadImage(selectedFile);
                    int w = img.getWidth();
                    int h = img.getHeight();
                    if(w<250||h<250){
                       JOptionPane.showMessageDialog(this, "The Choosen image is too small");
                       
                    }else if(h>600&&w>800){
                       int ratio = w/600;
                      
                       BufferedImage scaleImage = resize(img, 600, h/ratio); 
                       createFrame(scaleImage, rows, cols, timerJPanel.getJLabelName());
                     
                    }
                     //createFrame(selectedFile,rows, cols,timerJPanel.getJLabelName());
                } catch (IOException ex) {
                    Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }//end if
        });
        //Camerea Button
        JMenuItem cameraJMItem = bar.getCameraJMItem();
        cameraJMItem.addActionListener((ActionEvent ae)->{
            Camera cameraJFrame = new Camera();
            cameraJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            cameraJFrame.setLocationRelativeTo(null);
            cameraJFrame.setVisible(true);
            cameraJFrame.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent me) {
                    BufferedImage cameraImage = cameraJFrame.getCameraImage();
                    int pictOK = JOptionPane.showConfirmDialog(null, "", "Camera Picture", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, new ImageIcon(cameraImage));
                    
                    if(pictOK==JOptionPane.YES_OPTION){
                        try {
                            cameraJFrame.stop();
                            createFrame(cameraImage, rows,cols, timerJPanel.getJLabelName());
                            cameraJFrame.dispose();
                   
                        } catch (IOException ex) {
                            Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                }
            });
            //cameraJFrame.setResizable(true);
        });
        //level1 Button
        JMenuItem level1JMItem = bar.getLevel1JMItem();
        level1JMItem.addActionListener((ActionEvent ae)->{
            try {
                createFrame(image, 3, 3,timerJPanel.getJLabelName());
            } catch (IOException ex) {
                Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //Level 2 button
        JMenuItem level2JMItem = bar.getLevel2JMItem();
        level2JMItem.addActionListener((ActionEvent ae)->{
            try {
                createFrame(image, 4, 4,timerJPanel.getJLabelName());
            } catch (IOException ex) {
                Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //Leve3 Button
        JMenuItem level3JMItem = bar.getLevel3JMItem();
        level3JMItem.addActionListener((ActionEvent ae)->{
            try {
                createFrame(image, 5, 5,timerJPanel.getJLabelName());
            } catch (IOException ex) {
                Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        setSize(tileImgW*cols, tileImgH*(rows+1)+timerJPanel.getHeight());
        this.pack();
    }
    
    private boolean testAvailability(int noec,int noc){
        return ((Math.abs(noec-noc)==1)||(Math.abs(noec-noc)==cols));
    }
   
    public final synchronized void setNumberOfChunk(int noc){
        this.numberOfChunk = noc;
    }
    public final synchronized int getNumberOfChunk(){
        return this.numberOfChunk;
    }
    public final synchronized void setNumberOfEmptyChunk(int noec){
        this.numberOfEmptyChunk = noec;
    }
    public final synchronized int getNumberOfEmptyChunk(){
        return this.numberOfEmptyChunk;
    }
  
    public static void main(String[] args) throws IOException{
        BufferedImage wholeImage = loadImage(new File("imgs/ziyi.jpg"));
        ImageGame imageJFrame;
        imageJFrame = new ImageGame(wholeImage,4,4);
        imageJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageJFrame.setLocationRelativeTo(null);
        imageJFrame.setVisible(true);
        imageJFrame.setResizable(false);
        imageJFrame.pack();
       
        String name = JOptionPane.showInputDialog(imageJFrame, "Please enter your name:","Player Name",3);
        name = (name == null)|| name.isEmpty()? "Visitor":name;
        if(name.length()>8){name = name.substring(0,8);}
        imageJFrame.timerJPanel.setJLabelName(name);
    }
    //create a new frame with a new image or cols/rows and dispose the current frame
    private  void createFrame(BufferedImage img,int r, int c, String userName)throws IOException{
        ImageGame imageJFrame2 = new ImageGame(img,r,c);
        imageJFrame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageJFrame2.setLocationRelativeTo(null);
        imageJFrame2.setVisible(true);
        imageJFrame2.setResizable(false);
        imageJFrame2.timerJPanel.setJLabelName(userName);
        imageJFrame2.pack();
        ImageGame.this.dispose();
    }
   
    
    //to handle reshuffleJLabels
    class Task extends SwingWorker<String,Object>{

        @Override
        protected String doInBackground(){
            imgJPanel.shuffleJLabels(ImageGame.this.getNumberOfEmptyChunk());
            
            return "Loading";
            //throw new UnsupportedOperationException("Not supported yet."); 
        }
        @Override
        protected void done(){
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } 
    }
    private void writeToFile(String record){
        //File recordFile = new File("imgs/record.txt");
	Path filePath = Paths.get("imgs/record.txt");
        byte[] recordInBytes = record.getBytes();
        
            try {
                if (!Files.exists(filePath)) 
                    Files.createFile(filePath);
                Files.write(filePath, recordInBytes, StandardOpenOption.APPEND);
               
            } catch (IOException ex) {
                Logger.getLogger(ImageGame.class.getName()).log(Level.SEVERE, null, ex);
            }	
    }//end writeToFile
    
    //Read Image from a file
    public static final BufferedImage loadImage(File f)throws FileNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(f);
        BufferedImage wholeImage = ImageIO.read(fis); 
        return wholeImage;
    }

    private BufferedImage resize(BufferedImage img, int newW, int newH) {  
        int w = img.getWidth();  
        int h = img.getHeight();  
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());  
        Graphics2D g = dimg.createGraphics();  
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);  
        g.dispose();  
        return dimg;  
    }  
}