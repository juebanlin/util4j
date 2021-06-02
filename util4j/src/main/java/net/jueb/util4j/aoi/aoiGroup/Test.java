package net.jueb.util4j.aoi.aoiGroup;

import lombok.Getter;
import lombok.Setter;
import net.jueb.util4j.common.game.grid.GridUtil;

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


    public void testGrid(){
        float worldW=100;
        float worldH=100;
        float gridWidth=25;
        float gridHeight=25;
        int wNum=(int)(worldW/gridWidth);
        int hNum=(int)(worldH/gridHeight);

        float x=25;
        float y=20;
        float xRange=6f;
        float yRange=6f;

        int gridX = Math.max(0, (int) Math.floor(x / gridWidth));//x轴格子坐标
        int gridY = Math.max(0, (int) Math.floor(y / gridHeight));//y轴格子坐标

        float gridWidthLength=gridWidth;
        float gridHeightLength=gridHeight;
        int gridNumWithWidth=wNum;
        int gridNumWithHeight=hNum;
        //以格子的4个边界为起点的range(需要去掉range在格子中所占用的长度)
        float fixRange_Left = xRange - (x % gridWidthLength);//朝左的长度
        float fixRange_Right = xRange - (gridWidthLength - x % gridWidthLength);//朝右的长度
        float fixRange_Up = yRange - (gridHeightLength - y % gridHeightLength);//朝上的长度
        float fixRange_Down = yRange - (y % gridHeightLength);//朝下的长度
        //4个方向的边界延伸所占用的格子数
        int RangeXLNum = (int) Math.ceil(fixRange_Left / gridWidthLength);//x左边
        int RangeXRNum = (int) Math.ceil(fixRange_Right / gridWidthLength);//x右边
        int RangeYUNum = (int) Math.ceil(fixRange_Up / gridHeightLength);//y上边
        int RangeYDNum = (int) Math.ceil(fixRange_Down / gridHeightLength);//y下边
        //占用的所有格子的范围
        int ox = gridX - RangeXLNum;
        int oy = gridY - RangeYDNum;
        int ex = gridX + RangeXRNum;
        int ey = gridY + RangeYUNum;

        for (int gx = ox; gx <= ex; gx++) {
            for (int gy = oy; gy <= ey; gy++) {
                if (gx < 0 || gx >= gridNumWithWidth || gy < 0 || gy >= gridNumWithHeight) {//越界
                    continue;
                }
                //格子数坐标转换为id索引
                int gid = GridUtil.locToNumber(gx, gy);
                System.out.println("gx:"+gx+",gy:"+gy);
                int idx = gx + gy * wNum;
                System.out.println("idx:"+idx+","+gx+":"+gy+",gid:"+gid);
            }
        }
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
                System.out.println(Aoi.at1+"-"+Aoi.at2+"-"+Aoi.at3);
                Aoi.at1.set(0);
                Aoi.at2.set(0);
                Aoi.at3.set(0);
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
        test.test2(40000,5120,5120,3,15,gridSize);
    }
}
