package net.jueb.util4j.common.game.grid;

/**
 * 格子工具类
 */
public class GridUtil {
    /**
     * 格子坐标位数
     */
    public static final transient int GRID_LOC_DIG = 5;

    private static final  int OFFSET = (int) Math.pow(10, GRID_LOC_DIG);

    /**
     * 绝对坐标转格子X轴Y轴位置
     * @param x
     * @param y
     * @param gridWidthLength
     * @param gridHeightLength
     * @return
     */
    public static int[] posToGridLoc(float x,float y,float gridWidthLength,float gridHeightLength){
        int gridX = posxToGridLocX(x,gridWidthLength);
        int gridY = posyToGridLocY(y,gridHeightLength);
        return new int[]{gridX,gridY};
    }

    public static int posxToGridLocX(float x,float gridWidthLength){
        int gridX = Math.max(0, (int) Math.floor(x / gridWidthLength));//x轴格子坐标
        return gridX;
    }

    public static int posyToGridLocY(float y,float gridHeightLength){
        int gridY = Math.max(0, (int) Math.floor(y / gridHeightLength));//y轴格子坐标
        return gridY;
    }

    /**
     * 索引坐标位置转换为单个数字
     *
     * @param x
     * @param y
     * @return
     */
    public static int locToNumber(int x, int y) {
        int value=OFFSET;
        int id = (x * value + y);
        return id;
    }

    /**
     * 单个数字转换为格子索引坐标
     *
     * @param number
     * @return
     */
    public static int[] numberToLoc(int number) {
        int value=OFFSET;
        int x = number / value;
        int y = number % value;
        return new int[]{x, y};
    }

    /**
     * 左下角求余索引id转格子id
     *
     * @param indexId
     * @param xMaxGrid
     * @return
     */
    public static int leftDownIndexIdToGridId(int indexId, int xMaxGrid) {
        int yLoc = indexId / xMaxGrid;
        int xLoc = indexId % xMaxGrid;
        return locToNumber(xLoc, yLoc);
    }

    /**
     * 左上角求余索引id转格子id
     *
     * @param indexId
     * @param xMaxGrid x轴最大格子数量
     * @param yMaxGrid y轴最大格子数量
     * @return
     */
    public static int leftUpIndexIdToGridId(int indexId, int xMaxGrid, int yMaxGrid) {
        int num = indexId / xMaxGrid;
        int yLoc = yMaxGrid - num - 1;
        int xLoc = indexId % xMaxGrid;
        return locToNumber(xLoc, yLoc);
    }

    /**
     * 格子ID转换为左下角求余索引
     *
     * @param gridId
     * @param xMaxGrid x轴最大格子数量
     * @return
     */
    public static int gridIdToLeftDownIndexId(int gridId, int xMaxGrid) {
        int xLoc = numberToLoc(gridId)[0];
        int yLoc = numberToLoc(gridId)[1];
        int indexId = xLoc + yLoc * xMaxGrid;
        return indexId;
    }

    /**
     * 格子ID转换为左上角求余索引
     *
     * @param gridId
     * @param xMaxGrid x轴最大格子数量
     * @param yMaxGrid y轴最大格子数量
     * @return
     */
    public static int gridIdToLeftUpIndexId(int gridId, int xMaxGrid, int yMaxGrid) {
        int xLoc = numberToLoc(gridId)[0];
        int yLoc = numberToLoc(gridId)[1];
        int num = yMaxGrid - yLoc - 1;
        int indexId = xLoc + num * xMaxGrid;
        return indexId;
    }

    /**
     * 取格子的左下角顶点坐标
     *
     * @return
     */
    public static float[] getGridIdLeftLowerPos(int gridId, float gridWidth, float gridHeight) {
        float[] loc = new float[2];
        int[] locXy = numberToLoc(gridId);
        int gridX = locXy[0];
        int gridY = locXy[1];
        loc[0] = gridX * gridWidth;
        loc[1] = gridY * gridHeight;
        return loc;
    }

    /**
     * 根据格子ID取格子的左上角顶点坐标
     *
     * @param gridId
     * @return
     */
    public static float[] getGridIdLeftUpperPos(int gridId, float gridWidth, float gridHeight) {
        float[] loc = new float[2];
        int[] locXy = numberToLoc(gridId);
        int gridX = locXy[0];
        int gridY = locXy[1] + 1;
        loc[0] = gridX * gridWidth;
        loc[1] = gridY * gridHeight;
        return loc;
    }

    /**
     * 根据格子ID取格子的右上角顶点坐标
     *
     * @param gridId
     * @return
     */
    public static float[] getGridIdRightUpperPos(int gridId, float gridWidth, float gridHeight) {
        float[] loc = new float[2];
        int[] locXy = numberToLoc(gridId);
        int gridX = locXy[0] + 1;
        int gridY = locXy[1] + 1;
        loc[0] = gridX * gridWidth;
        loc[1] = gridY * gridHeight;
        return loc;
    }


    /**
     * 根据格子ID取格子的右下角顶点坐标
     *
     * @param gridId
     * @return
     */
    public static float[] getGridIdRightLowerPos(int gridId, float gridWidth, float gridHeight) {
        float[] loc = new float[2];
        int[] locXy = numberToLoc(gridId);
        int gridX = locXy[0] + 1;
        int gridY = locXy[1];
        loc[0] = gridX * gridWidth;
        loc[1] = gridY * gridHeight;
        return loc;
    }
}