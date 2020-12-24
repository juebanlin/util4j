package net.jueb.util4j.aoi.aoiGroup;

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
     * 窗口分辨率
     *
     * @param w
     * @param h
     */
    public AoiRender(int w, int h) {
        this.W = w;
        this.H = h;
    }

    /**
     * @param limitX 空间X尺寸
     * @param limitY 空间Y尺寸
     * @param result
     * @param info
     * @return
     */
    public <T extends AoiEntity> BufferedImage render(float limitX, float limitY, AoiResult<T> result, String info) {
        boolean scale = false;
        //对象缩放
        float xscale = 1f;
        float yscale = 1f;
        int width = (int) limitX;
        int height = (int) limitY;
        if (scale) {
            //对象缩放
            xscale = W / limitX;
            yscale = H / limitY;
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);

        drowList(result.noGroups, noGroupColor, xscale, yscale, g,false);
        for (int i = 0; i < result.groups.size(); i++) {
            List<T> group = result.groups.get(i);
            Random rand = new Random();
            int red = rand.nextInt(244);
            int green = rand.nextInt(244);
            int blue = rand.nextInt(244);
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

    public <T extends AoiEntity> void update(float limitX, float limitY, AoiResult<T> result, String info){
        BufferedImage image = render(limitX, limitY, result, info);
        jLabel.setIcon(new ImageIcon(image));
        jLabel.updateUI();
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