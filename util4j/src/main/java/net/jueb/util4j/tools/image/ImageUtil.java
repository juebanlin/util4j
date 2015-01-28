package net.jueb.util4j.tools.image;
import java.awt.Color;    
import java.awt.Font;    
import java.awt.Graphics;    
import java.awt.Image;    
import java.awt.image.BufferedImage;    
import java.io.File;    
import java.io.FileOutputStream;    

import javax.imageio.ImageIO;    

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
/**
* 给图片加水印
* @author iis
*/   
public final class ImageUtil {    
     public ImageUtil() {    
    
     }
     /**   
      * 把图片印刷到图片上   
      *    
      * @param pressImg
      *            水印文件   
      * @param targetImg   
      *            目标文件   
      * @param x   
      * @param y   
      */   
     public final static void pressImage(String pressImg, String targetImg,    
             int x, int y) {    
         try {    
             File _file = new File(targetImg);    
             Image src = ImageIO.read(_file);    
             int wideth = src.getWidth(null);    
             int height = src.getHeight(null);    
             BufferedImage image = new BufferedImage(wideth, height,    
                     BufferedImage.TYPE_INT_RGB);    
             Graphics g = image.createGraphics();    
             g.drawImage(src, 0, 0, wideth, height, null);    
    
             // 水印文件    
             File _filebiao = new File(pressImg);    
             Image src_biao = ImageIO.read(_filebiao);    
             int wideth_biao = src_biao.getWidth(null);    
             int height_biao = src_biao.getHeight(null);    
             g.drawImage(src_biao, wideth - wideth_biao - x, height    
                     - height_biao - y, wideth_biao, height_biao, null);    
             // /    
             g.dispose();    
             FileOutputStream out = new FileOutputStream(targetImg);    
             JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);    
             encoder.encode(image);    
             out.close();    
         } catch (Exception e) {    
             e.printStackTrace();    
         }    
     }    
    
     /**   
      * 打印文字水印图片   
      *    
      * @param pressText   
      *             --文字   
      * @param targetImg 
      * 			--目标图片   
      * @param fontName 
      * 			--字体名   
      * @param fontStyle 
      * 			--字体样式   
      * @param color    
      *            	--字体颜色   
      * @param fontSize    
      *            	--字体大小   
      * @param x    
      *        		--X偏移量   
      * @param y
      * 			--Y偏移量   
      */   
    
     public static void pressText(String pressText, String targetImg,    
             String fontName, int fontStyle, int color, int fontSize, int x,    
             int y) {    
         try {    
             File _file = new File(targetImg);    
             Image src = ImageIO.read(_file);    
             int wideth = src.getWidth(null);    
             int height = src.getHeight(null);    
             BufferedImage image = new BufferedImage(wideth, height,    
                     BufferedImage.TYPE_INT_RGB);    
             Graphics g = image.createGraphics();    
             g.drawImage(src, 0, 0, wideth, height, null);    
             // String s="www.qhd.com.cn";    
             g.setColor(Color.RED);    
             g.setFont(new Font(fontName, fontStyle, fontSize));    
    
             g.drawString(pressText,fontSize+x,fontSize    
                      +y);    
             g.dispose();    
             FileOutputStream out = new FileOutputStream(targetImg);    
             JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);    
             encoder.encode(image);    
             out.close();    
         } catch (Exception e) {    
             System.out.println(e);    
         }    
     }    
    
     public static void main(String[] args) {    
         pressImage("d:/1.jpg", "d:/2.jpg", 20, 20);    
         pressText("水印文字", "d:/1.jpg","宋体",1,1,25,10, 10);
     }    
}
