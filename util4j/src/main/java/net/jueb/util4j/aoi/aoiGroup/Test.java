package net.jueb.util4j.aoi.aoiGroup;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Test {

    @Getter
    @Setter
    class DemoAoiEntity implements AoiEntity{

        private long aoiId;

        private float aoiX;

        private float aoiY;

        private float aoiRange;

        private transient Object aoiGroup;

        public int hashCode() {
            return Long.hashCode(aoiId);
        }

        public boolean equals(DemoAoiEntity e) {
            return e.aoiId == aoiId;
        }
    }

    private List<DemoAoiEntity> buildList(int num,float worldX,float worldY,float rangeMin,float rangeMax){
        Random rand = new Random();
        List<DemoAoiEntity> entityList=new ArrayList<>();
        for(int i=1;i<=num;i++){
            DemoAoiEntity e = new DemoAoiEntity();
            e.setAoiId(i);
            e.setAoiRange(rand.nextFloat() * (rangeMax-rangeMin) + rangeMin);
            e.setAoiX(rand.nextFloat() * worldX);
            e.setAoiY(rand.nextFloat() * worldY);
            entityList.add(e);
        }
        return entityList;
    }

    public AoiResult<DemoAoiEntity> run(List<DemoAoiEntity> input, float worldX, float worldY){
        // 初始化AOI
        float aoiSIZE = 32f;//AOI网格宽度，理论上应该跟实体直径差不多大。
        Aoi aoi = new Aoi(input.size(),worldX, worldY, aoiSIZE);
        // 生成实体，并加入aoi
        return aoi.input(input);
    }

    /**
     *
     * @param num 实体数量
     * @param worldX 世界尺寸
     * @param worldY
     * @param rangeMin 半径最小
     * @param rangeMax 半径最大
     */
    public void test(int num,float worldX,float worldY,float rangeMin,float rangeMax){
        List<DemoAoiEntity> input=buildList(num,worldX,worldY,rangeMin,rangeMax);
        System.out.println("输入任意内容开始:");
        new Scanner(System.in).nextLine();
        long time, use;
        String info = "";
        AoiResult<DemoAoiEntity> result=null;
        for (int i = 0; i < 10; i++) {
            time = System.currentTimeMillis();
            result = run(input, worldX, worldY);
            use = System.currentTimeMillis() - time;
            // 显示结果
            info = String.format("%dms, group=%d", use, result.groups.size());
            System.out.println(info);
        }
        AoiRender renderer = new AoiRender(1024,1024);
        BufferedImage image = renderer.render(worldX,worldY,result, info);
        renderer.showImg(image);
        renderer.save(image);
    }



    /**
     * @param args
     */
    public static void main(String[] args) {
        Test test = new Test();
        test.test(20000,5120,5120,5,15);
    }
}
