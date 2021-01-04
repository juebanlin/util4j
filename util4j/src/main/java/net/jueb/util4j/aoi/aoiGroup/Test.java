package net.jueb.util4j.aoi.aoiGroup;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Test {

    @Getter
    @Setter
    class DemoAoiEntity implements AoiEntity{

        private long aoiId;

        private float aoiX;

        private float aoiY;

        private float aoiRange;
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

    public AoiResult<DemoAoiEntity> run(List<DemoAoiEntity> input, float worldX, float worldY,float aoiGridSize){
        // 初始化AOI
        Aoi aoi = new Aoi(input.size(),worldX, worldY, aoiGridSize);
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
    public void test(int num,float worldX,float worldY,float rangeMin,float rangeMax,float gridSize){
        List<DemoAoiEntity> input=buildList(num,worldX,worldY,rangeMin,rangeMax);
//        System.out.println("输入任意内容开始:");
//        new Scanner(System.in).nextLine();
        long time, use;
        String info = "";
        AoiResult<DemoAoiEntity> result=null;
        for (int i = 0; i < 10; i++) {
            time = System.currentTimeMillis();
            result = run(input, worldX, worldY,gridSize);
            use = System.currentTimeMillis() - time;
            // 显示结果
            info = String.format("%dms, group=%d", use, result.groups.size());
            System.out.println(info);

        }
        AoiRender renderer = new AoiRender(1920,1080,2.0f);
        renderer.init();
        renderer.setScale(2.0f);//缩放2倍展示
        BufferedImage image = renderer.update(worldX, worldY, result, info);
//        renderer.save(image);
    }


    public void test2(int num,float worldX,float worldY,float rangeMin,float rangeMax,float gridSize){
        System.out.println("输入任意内容开始:");
        new Scanner(System.in).nextLine();
        AoiRender renderer = new AoiRender(1024,1024);
        renderer.init();
        renderer.setScale(2.0f);//缩放2倍展示
        Executors.newSingleThreadExecutor().submit(()->{
            long time, use;
            String info = "";
            AoiResult<DemoAoiEntity> result=null;
            for (int i = 0; i < 100; i++) {
                List<DemoAoiEntity> input=buildList(num,worldX,worldY,rangeMin,rangeMax);
                time = System.currentTimeMillis();
                result = run(input, worldX, worldY,gridSize);
                use = System.currentTimeMillis() - time;
                // 显示结果
                info = String.format("%dms, group=%d", use, result.groups.size());
                System.out.println(info);
                renderer.update(worldX,worldY,result, info);
                try {
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }
        });
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        Test test = new Test();
        float gridSize=32f;
        gridSize=32f;
        test.test2(20000,5120,5120,3,15,gridSize);
    }
}
