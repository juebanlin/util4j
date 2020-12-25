package net.jueb.util4j.aoi.aoiGroup;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class AoiRender {

    // 分辨率
    private final int W;

    private final int H;

    private Color bgColor = Color.WHITE;

    // 未分组的普通实体，使用黑色
    private Color noGroupColor = Color.BLACK;

    /**
     * 缩放,对象和图片尺寸一起缩放
     */
    @Setter
    @Getter
    private float scale=1.0f;

    /**
     * 等比缩放 图片尺寸不变,对象发生变化
     */
    @Getter
    @Setter
    private boolean scaleWithWorld;

    /**
     *
     * @param windowWidth
     * @param windowHeight
     */
    public AoiRender(int windowWidth, int windowHeight) {
       this(windowWidth,windowHeight,1.0f);
    }

    /**
     *
     * @param windowWidth
     * @param windowHeight
     * @param scale
     */
    public AoiRender(int windowWidth, int windowHeight,float scale) {
        this.W = windowWidth;
        this.H = windowHeight;
        this.scale=scale;
    }

    /**
     * @param limitX 空间X尺寸
     * @param limitY 空间Y尺寸
     * @param result
     * @param info
     * @return
     */
    public <T extends AoiEntity> BufferedImage buildImg(float limitX, float limitY, AoiResult<T> result, String info) {
        //对象缩放
        float xscale = scale;
        float yscale = scale;
        int imageWidth = (int)Math.ceil((limitX*xscale));
        int imageHeight = (int)Math.ceil((limitY*yscale));
        if (scaleWithWorld) {
            //等比缩放 图片尺寸不变,对象发生变化
            xscale = W / limitX;
            yscale = H / limitY;
            imageWidth = (int)Math.ceil((limitX));
            imageHeight = (int)Math.ceil((limitY));
        }
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, imageWidth, imageHeight);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drowList(result.noGroups, noGroupColor, xscale, yscale, g,false);
        for (int i = 0; i < result.groups.size(); i++) {
            List<T> group = result.groups.get(i);
            Random rand = new Random();
            int red = rand.nextInt(244)+1;
            int green = rand.nextInt(244)+1;
            int blue = rand.nextInt(244)+1;
            Color color = new Color(red, green, blue);
            drowList(group, color, xscale, yscale, g,true);
        }

        Font font = new Font("宋体", Font.PLAIN, 16);
        g.setFont(font);
        g.drawString(info,4,20);
        return image;
    }

    private <T extends AoiEntity> void drowList(List<T> list, Color color, float xscale, float yscale, Graphics2D g,boolean fill) {
        for (T e : list) {
            int x = (int) (xscale * (e.getAoiX() - e.getAoiRange()));
            int y = (int) (yscale * (e.getAoiY() - e.getAoiRange()));
            int w = (int) (xscale * (e.getAoiRange() + e.getAoiRange()));
            int h = (int) (yscale * (e.getAoiRange() + e.getAoiRange()));
            g.setColor(color);
            if(fill){
                g.fillOval(x, y, w, h);
            }else {
                g.drawOval(x, y, w, h);
            }
        }
    }

    public <T extends AoiEntity> BufferedImage update(float limitX, float limitY, AoiResult<T> result, String info){
        BufferedImage image = buildImg(limitX, limitY, result, info);
        jLabel.setIcon(new ImageIcon(image));
        jLabel.updateUI();
        return image;
    }

    JFrame jf = new JFrame();
    JScrollPane scrollPanel = new JScrollPane();
    JLabel jLabel = new JLabel();

    public void init(){
        jf.setTitle("图示");
        jf.setSize(W, H);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(3);
        jf.add(scrollPanel, BorderLayout.CENTER);
        scrollPanel.setViewportView(jLabel);
        scrollPanel.setVisible(true);
        jf.setVisible(true);
    }

    public void save(BufferedImage image) {
        try {
            ImageIO.write(image, "png", new FileOutputStream(System.currentTimeMillis() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}