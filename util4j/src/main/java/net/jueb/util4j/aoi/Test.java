package net.jueb.util4j.aoi;


import net.jueb.util4j.aoi.aoiGroup.Aoi;
import net.jueb.util4j.common.game.grid.GridUtil;

public class Test {
    public static void main(String[] args) {
        test();
    }


    public static void test(){
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
                System.out.println(gx+":"+gy+",gid:"+gid);
            }
        }
    }
}
