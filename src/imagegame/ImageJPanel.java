/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
/**
 *
 * @author ruhuayan
 */
public class ImageJPanel extends javax.swing.JPanel {

    /**
     * Creates new form ImageJPanel
     */
    private final BufferedImage imgs[];
    private final int rows;
    private final int cols;
    private final JLabel imgJLabels[];
    private final int imgWidth;
    private final int imgHeight;
    private final ArrayList<Integer>[] numberGroup;
    private int gi,gj;
    
    
    public ImageJPanel(BufferedImage image, int r, int c) {
        initComponents();
        
        this.setLayout(null);
        
        cols = c;
        rows = r;
        
        //imgs = new BufferedImage[rows*cols]; //Image array to hold image chunks
        numberGroup = new ArrayList[rows*cols];
        //image = loadImage(f);
        int imageW = image.getWidth();
        int imageH = image.getHeight();
        imgWidth = imageW / cols;
        imgHeight = imageH / rows;
        imgs = new BufferedImage[rows*cols];
        cutImage(image, rows, cols);
        //shuffleImgs();
        
        imgJLabels = new JLabel[(rows+1)*cols];
        layImgJLabels(rows, cols);
        this.shuffleJLabels(-1);
        this.setPreferredSize(new Dimension(imageW, imageH+imgHeight));
        //for (int i=0; i<rows*cols;i++){System.out.println(numberGroup[i]); }
    }
    
   
     public final void cutImage(BufferedImage wholeImage, int rows,int cols){
        
        //int chunks = rows * cols;
        int count = 0;
        
         //Image array to hold image chunks
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                imgs[count] = new BufferedImage(imgWidth, imgHeight,wholeImage.getType());
  
                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(wholeImage, 0, 0, imgWidth, imgHeight, imgWidth * y, imgHeight * x, imgWidth * y + imgWidth, imgHeight * x + imgHeight, null);
                gr.dispose();
                
            }
        }
        //return chunkImgs;
    }
  
    public final void layImgJLabels(int rows,int cols){
        int chunks = rows * cols;
        int jLabelsNum = imgJLabels.length;
        for(int i=0; i<jLabelsNum; i++){
            
            if(i<chunks){
                imgJLabels[i] = new JLabel();
                imgJLabels[i].setIcon(new ImageIcon(imgs[i]));
                
                //imgJLabels[i].setToolTipText("i" +i);
                imgJLabels[i].setBorder(BorderFactory.createLineBorder(Color.black));
                numberGroup[i]=new ArrayList<>();
                numberGroup[i].add(i);
            }else{
                imgJLabels[i] = new JLabel();
                imgJLabels[i].setBackground(Color.BLACK);
            }
            
            imgJLabels[i].setBounds((i%cols)*imgWidth, (int)(i/rows)*imgHeight, imgWidth, imgHeight );
            imgJLabels[i].setOpaque(true);
         
            this.add(imgJLabels[i]);
        }
        imgJLabels[jLabelsNum-1].setOpaque(false);
       
    }//end function layImgLabels
    
    public void swapJLabel(int m, int n, boolean mEmpty){
        
        int w, h;
        //m - first JLabel and image; n - second JLabel and Image
        //the function here is to change JLabel and image of number m to JLabel and image of number n, so n to m
        //param bool !full to indicate if int m is emptyJLabel 
        JLabel tempJField = imgJLabels[m];
        BufferedImage tempImg = imgs[m];
        
        w = n%cols*imgWidth;
        h = (int)n/rows * imgHeight;
        imgJLabels[m].setBounds(w, h, imgWidth, imgHeight);
        imgJLabels[m] = imgJLabels[n];
        imgs[m] = imgs[n]; //if m/emptyChunk is the last JLabel number//imgs[m] out of range
         
        w = m%cols*imgWidth;
        h = (int)m/rows * imgHeight;
        imgJLabels[n].setBounds(w, h, imgWidth, imgHeight);
        imgJLabels[n] = tempJField;
        imgs[n]= tempImg;

        //to record the movement of those changes of JLabels and Images
        for(int gc=0; gc<cols*rows-1;gc++){
            
            if (numberGroup[gc].get(numberGroup[gc].size()-1)==m) gi=gc;
            if (numberGroup[gc].get(numberGroup[gc].size()-1)==n) gj=gc;
        }
           addToList(numberGroup[gi],n);
           addToList(numberGroup[gj],m);
//        if(numberGroup[gi].size()==1) numberGroup[gi].add(n);
//        else numberGroup[gi].set(1, n);
//        if(numberGroup[gj].size()==1)  numberGroup[gj].add(m);
//        else numberGroup[gj].set(1, m);
    }

    public final void shuffleJLabels(int emptyChunk){
         Random rand = new Random();
   
         for(int i=0; i<rows*cols-1;i++){
            
            int j = rand.nextInt(cols*rows/4);
            if(i!=emptyChunk && j!=emptyChunk)
                swapJLabel(i,j, false);
         }
    }
    private BufferedImage lastJLabelImg;
    
    public void swapJLabel(int emptyChunk, int movedChunk){
        JLabel temp = new JLabel();
        int lastNum = imgJLabels.length-1;
        int w = emptyChunk%cols * imgWidth;
        int h = (int)(emptyChunk/rows) * imgHeight;
        imgJLabels[movedChunk].setBounds(w, h, imgWidth, imgHeight);
       
        imgJLabels[emptyChunk]=imgJLabels[movedChunk];
        imgJLabels[movedChunk] = temp; 
        if(emptyChunk==lastNum) {
            lastJLabelImg = imgs[movedChunk];
            addToList(numberGroup[movedChunk],lastNum);
            //numberGroup[movedChunk].add(lastNum);
            imgs[movedChunk] = null;
        }else if(movedChunk==lastNum){
            imgs[emptyChunk] = lastJLabelImg;
            lastJLabelImg = null;
        }else{ 
            imgs[emptyChunk] = imgs[movedChunk];
            for(int gc=0; gc<rows*cols;gc++){
               if (numberGroup[gc].get(numberGroup[gc].size()-1)==movedChunk) 
                   addToList(numberGroup[gc],emptyChunk);
                   //numberGroup[gc].add(emptyChunk);
               //return;
            }
        }
        
        for (int i=0; i<rows*cols;i++){System.out.println(numberGroup[i]); }
    }
    
    private void addToList(ArrayList al, int num){
         if(al.size()==1) al.add(num);
         else al.set(1, num);
    }
  //Chunk is Tiled Image
    public int getNumberOfChunk(int x, int y){
        //
        if(testBound(x,y)){
            int noc= (int)(y /imgHeight)*rows + (int)(x /imgWidth) ;
            return noc;
        } else 
            return -1;
    }
  
    public void showNumberHint(){
        int lastNum = imgJLabels.length-1;
        for(int i=0; i<rows*cols;i++){
            Graphics2D gr;
           
            if (numberGroup[i].get(numberGroup[i].size() - 1) == lastNum ){
                if(lastJLabelImg!=null)
                    gr = lastJLabelImg.createGraphics();
                else
                    gr = imgs[cols*rows-1].createGraphics();
                gr.setColor(Color.red);
                gr.setFont(new Font("Courier", Font.BOLD,14));
                gr.drawString((rows*rows)+"", 10, 10);
                gr.dispose();
           } else{
                gr = imgs[numberGroup[i].get(numberGroup[i].size() - 1)].createGraphics();
                gr.setColor(Color.red);
                gr.setFont(new Font("Courier", Font.BOLD,14));
//                
                gr.drawString((i+1)+"", 10, 10);
                repaint(); gr.dispose();
            }
           
        }
    }
    public boolean checkResult(){
        for(int i=0; i<(cols*rows-1);i++){
             if (numberGroup[i].get(numberGroup[i].size()-1) !=i) 
                 return false;    
        }
            return true;
    }
    public boolean testBound(int x, int y){
        return !((x<0) ||(y<0) || x>imgWidth*cols || y>imgHeight*rows);
    }
    
//    public BufferedImage getWholeImage(){
//        return image;
//    }
    public JLabel getImgJLabel(int noc){
    //       
        return imgJLabels[noc];
    }
    public int getChunkWidth(){//Chunk is a tiled Image
        return imgWidth;
    }
    public int getChunkHeight(){
        return imgHeight;
    }
//    public void clearImgJPanel(){
//        for (int i=0; i<imgs.length;i++){
//            
//            Graphics2D gr = imgs[i].createGraphics();
//            gr.clearRect(0, 0, imgWidth, imgHeight);
//            
//            imgJLabels[i] = null;
//            imgs[i] = null;
//            gr.dispose();
//        }
//    }
  /*@Override
    public void paint(Graphics g){
        int count =0;
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                g.drawImage(imgs[count++],y*chunkWidth , x*chunkHeight, this);
                System.out.println( " " + x*chunkHeight);
            }
        }
        //g.draw3DRect(WIDTH, WIDTH, WIDTH, WIDTH, true);
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
