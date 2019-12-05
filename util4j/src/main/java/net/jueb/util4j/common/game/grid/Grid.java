package net.jueb.util4j.common.game.grid;

import lombok.Data;

import java.util.Objects;

/**
 * 网格上的格子
 */
@Data
public class Grid {

    /**
     * 唯一id(格子索引坐标方式合成的number)
     */
    final int id;
    /**
     * 格子在X轴方向的索引
     */
    final int gridIndexX;
    /**
     * 格子在Y轴方向的索引
     */
    final int gridIndexY;

    /**
     * 格子宽
     */
    final float width;

    /**
     * 格子高
     */
    final float height;

    /**
     * 所在网格X轴最大格子数量
     */
    final int limitXNum;
    /**
     * 所在网格Y轴最大格子数量
     */
    final int limitYNum;

    /**
     * 左下角开始求余的索引id
     */
    final int leftDownIndexId;

    /**
     * 左上角开始求余的索引id
     */
    final int leftUpIndexId;

    /**
     * @param gridIndexX 格子在X轴方向的索引
     * @param gridIndexY 格子在Y轴方向的索引
     * @param limitXNum 所在网格X轴最大格子数量
     * @param limitYNum 所在网格Y轴最大格子数量
     */
    public Grid(int gridIndexX, int gridIndexY, int limitXNum, int limitYNum, float width, float height) {
        this.id = GridUtil.locToNumber(gridIndexX, gridIndexY);
        this.gridIndexX = gridIndexX;
        this.gridIndexY = gridIndexY;
        this.limitXNum = limitXNum;
        this.limitYNum = limitYNum;
        this.width = width;
        this.height = height;
        this.leftUpIndexId = GridUtil.gridIdToLeftUpIndexId(id, limitXNum, limitYNum);
        this.leftDownIndexId = GridUtil.gridIdToLeftDownIndexId(id, limitXNum);
    }

    /**
     * @param gridIndexX 格子在X轴方向的索引
     * @param gridIndexY 格子在Y轴方向的索引
     * @param ownerMesh 格子所属网格
     */
    public Grid(int gridIndexX, int gridIndexY, IGridMeshService ownerMesh) {
        this(gridIndexX, gridIndexY, ownerMesh.getGridNumWithWidth(), ownerMesh.getGridNumWithHeight(), ownerMesh.getGridWidthLength(), ownerMesh.getGridHeightLength());
    }

    /**
     * @param id id(格子索引坐标方式合成的number)
     * @param ownerMesh 格子所在网格
     */
    public Grid(int id, IGridMeshService ownerMesh) {
        this(GridUtil.numberToLoc(id)[0], GridUtil.numberToLoc(id)[1], ownerMesh);
    }

    /**
     * 取格子的左下角顶点坐标
     *
     * @return
     */
    public float[] getGridIdLeftLowerPos() {
        return GridUtil.getGridIdLeftLowerPos(id, getWidth(), getHeight());
    }

    /**
     * 根据格子ID取格子的左上角顶点坐标
     *
     * @return
     */
    public float[] getGridIdLeftUpperPos() {
        return GridUtil.getGridIdLeftUpperPos(id, getWidth(), getHeight());
    }

    /**
     * 根据格子ID取格子的右上角顶点坐标
     *
     * @return
     */
    public float[] getGridIdRightUpperPos() {
        return GridUtil.getGridIdRightUpperPos(id, getWidth(), getHeight());
    }


    /**
     * 根据格子ID取格子的右下角顶点坐标
     *
     * @return
     */
    public float[] getGridIdRightLowerPos() {
        return GridUtil.getGridIdRightLowerPos(id, getWidth(), getHeight());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grid grid = (Grid) o;
        return id == grid.id &&
                Float.compare(grid.width, width) == 0 &&
                Float.compare(grid.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, width, height);
    }
}