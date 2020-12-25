package net.jueb.util4j.common.game.grid;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 格子网格服务
 * 实现基于左下角直角坐标系
 * 以宽度为X轴,高度为Y轴
 * 注意：格子的ID是由格子X轴和Y轴的数量索引拼接而成,且数量值从0开始
* @Description:
* @Author:         helin
* @CreateDate:     2019年6月25日
* @UpdateUser:     Administrator
* @Version:        1.0
 */
public interface IGridMeshService {

	/**
	 * 世界宽
	 *
	 * @return
	 */
	float getWorldWidthLength();

	/**
	 * 世界高
	 *
	 * @return
	 */
	float getWorldHeightLength();

	/**
	 * 单个格子的宽度
	 *
	 * @return
	 */
	float getGridWidthLength();

	/**
	 * 单个格子的高度
	 *
	 * @return
	 */
	float getGridHeightLength();

	/**
	 * 取横向(X轴)格子数量
	 *
	 * @return
	 */
	default int getGridNumWithWidth() {
		return (int) (getWorldWidthLength() / getGridWidthLength());
	}

	/**
	 * 取纵向(Y轴)格子数量
	 *
	 * @return
	 */
	default int getGridNumWithHeight() {
		return (int) (getWorldHeightLength() / getGridHeightLength());
	}

	/**
	 * 获取坐标所在格子id
	 * @param x
	 * @param y
	 * @return
	 */
	default int getGridId(float x, float y) {
		return getGrid(x,y).getId();
	}

	/**
	 * 获取坐标所在格子
	 * @param x
	 * @param y
	 * @return
	 */
	default Grid getGrid(float x, float y) {
		int gridX = GridUtil.posxToGridLocX(x,getGridWidthLength());
		int gridY = GridUtil.posyToGridLocY(y,getGridHeightLength());
		Grid grid = new Grid(gridX, gridY, this);
		return grid;
	}

	/**
	 * 根据格子id格子
	 * @param gridId
	 * @return
	 */
	default Grid getGridById(int gridId) {
		Grid grid=new Grid(gridId,this);
		return grid;
	}

	/**
	 * 根据左下角开始的求余索引ID拿格子
	 * @param indexId
	 * @return
	 */
	default Grid getGridByLeftDownIndexId(int indexId) {
		int gridId= GridUtil.leftDownIndexIdToGridId(indexId,getGridNumWithWidth());
		Grid grid=new Grid(gridId,this);
		return grid;
	}

	/**
	 * 根据左上角开始的求余索引ID拿格子
	 * @param indexId
	 * @return
	 */
	default Grid getGridByLeftUpIndexId(int indexId) {
		int gridId= GridUtil.leftUpIndexIdToGridId(indexId,getGridNumWithWidth(),getGridNumWithHeight());
		Grid grid=new Grid(gridId,this);
		return grid;
	}

	/**
	 * 根据坐标和像素范围获取格子
	 * @param x
	 * @param y
	 * @param range
	 * @return
	 */
	default Set<Grid> getGrids(float x, float y, float range) {
		return getGrids(x, y, range, range);
	}

	/**
	 * 根据坐标和格子数量范围获取格子
	 * @param x
	 * @param y
	 * @param gridNumRange 如果numRange=1,则取格子id相邻的9宫格
	 * @return
	 */
	default Set<Grid> getGrids(float x, float y, int gridNumRange) {
		return getGrids(getGridId(x,y),gridNumRange);
	}

	/**
	 *  根据坐标和格子数量范围获取格子
	 * @param x
	 * @param y
	 * @param xNumRange x轴格子数量范围
	 * @param yNumRange y轴格子数量范围
	 * @return
	 */
	default Set<Grid> getGrids(float x, float y, int xNumRange, int yNumRange) {
		return getGrids(getGridId(x,y),xNumRange,yNumRange);
	}

	/**
	 * 根据坐标和像素范围获取格子
	 * @param x
	 * @param y
	 * @param xRange
	 * @param yRange
	 * @return
	 */
	default Set<Grid> getGrids(float x, float y, float xRange, float yRange) {
		Set<Grid> grids = new LinkedHashSet<>();
		Set<Integer> ids=getGridIds(x,y,xRange,yRange);
		for(Integer id:ids){
			Grid grid=new Grid(id,this);
			grids.add(grid);
		}
		return grids;
	}

	/**
	 * 根据绝对坐标和以坐标为中心点的范围取覆盖格子id
	 * (比如一个圆或所占用的格子)
	 * @param x
	 * @param y
	 * @param range 以x,y为中心点的半径长度
	 * @return
	 */
	default Set<Integer> getGridIds(float x, float y, float range) {
		return getGridIds(x, y, range, range);
	}
	/**
	 * 根据绝对坐标和以坐标为中心点的范围取覆盖格子id
	 * @param x
	 * @param y
	 * @param gridNumRange 如果numRange=1,则取格子id相邻的9宫格
	 * @return
	 */
	default Set<Integer> getGridIds(float x, float y, int gridNumRange) {
		return getGridIds(getGridId(x,y), gridNumRange);
	}
	/**
	 * 根据绝对坐标和以坐标为中心点的范围取覆盖格子id
	 * @param x
	 * @param y
	 * @param xNumRange x轴格子数量范围
	 * @param yNumRange y轴格子数量范围
	 * @return
	 */
	default Set<Integer> getGridIds(float x, float y, int xNumRange, int yNumRange) {
		return getGridIds(getGridId(x,y), xNumRange,yNumRange);
	}

	/**
	 * 根据绝对坐标为中心点的范围取覆盖格子id
	 * (比如一个圆或者一个矩形所占用的格子)
	 * @param x
	 * @param y
	 * @param xRange 以x,y为中心点的朝向x轴的范围
	 * @param yRange 以x,y为中心点的朝向y轴的范围
	 * @return
	 */
	default Set<Integer> getGridIds(float x, float y, float xRange, float yRange) {
		return new HashSet<>(getGridIdList(x,y,xRange,yRange));
	}

	default List<Integer> getGridIdList(float x, float y, float xRange, float yRange) {
		float gridWidthLength=getGridWidthLength();
		float gridHeightLength=getGridHeightLength();

		int gridNumWithWidth=getGridNumWithWidth();
		int gridNumWithHeight=getGridNumWithHeight();

		int gridX = GridUtil.posxToGridLocX(x,gridWidthLength);
		int gridY = GridUtil.posyToGridLocY(y,gridHeightLength);

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

		List<Integer> indexes = new ArrayList<>();
		for (int gx = ox; gx <= ex; gx++) {
			for (int gy = oy; gy <= ey; gy++) {
				if (gx < 0 || gx >= gridNumWithWidth || gy < 0 || gy >= gridNumWithHeight) {//越界
					continue;
				}
				//格子数坐标转换为id索引
				int gid = GridUtil.locToNumber(gx, gy);
				indexes.add(gid);
			}
		}
		return indexes;
	}

	/**
	 * 根据格子id和格子数范围取周围格子id
	 * 如果numRange=1,则取格子id相邻的9宫格
	 * @param gridId
	 * @param numRange 格子数量范围
	 * @return
	 */
	default Set<Grid> getGrids(int gridId, int numRange) {
		return getGrids(gridId,numRange,numRange);
	}

	/**
	 * 根据格子id和格子数范围取周围格子
	 * 如果numRange=1,则取格子id相邻的9宫格
	 * @param gridId
	 * @param xNumRange x轴格子数量范围
	 * @param yNumRange y轴格子数量范围
	 * @return
	 */
	default Set<Grid> getGrids(int gridId, int xNumRange, int yNumRange) {
		Set<Grid> grids = new LinkedHashSet<>();
		Set<Integer> ids=getGridIds(gridId,xNumRange,yNumRange);
		for(Integer id:ids){
			Grid grid=new Grid(id,this);
			grids.add(grid);
		}
		return grids;
	}

	/**
	 * 根据格子id和格子数范围取周围格子id
	 * 如果numRange=1,则取格子id相邻的9宫格
	 * @param gridId
	 * @param numRange 格子数量范围
	 * @return
	 */
	default Set<Integer> getGridIds(int gridId, int numRange) {
		return getGridIds(gridId,numRange,numRange);
	}

	/**
	 * 根据格子id和格子数范围取周围格子
	 * 如果numRange=1,则取格子id相邻的9宫格
	 * @param gridId
	 * @param xNumRange x轴格子数量范围
	 * @param yNumRange y轴格子数量范围
	 * @return
	 */
	default Set<Integer> getGridIds(int gridId, int xNumRange, int yNumRange) {
		Set<Integer> indexes = new HashSet<>();
		int[] locXym = GridUtil.numberToLoc(gridId);
		int gridX = locXym[0];
		int gridY = locXym[1];
		int ox = gridX - xNumRange;
		int oy = gridY - yNumRange;
		int ex = gridX + xNumRange;
		int ey = gridY + yNumRange;
		for (int gx = ox; gx <= ex; gx++) {
			for (int gy = oy; gy <= ey; gy++) {
				if (gx < 0 || gx >= getGridNumWithWidth() || gy < 0 || gy >= getGridNumWithHeight()) {//越界
					continue;
				}
				//格子数坐标转换为id索引
				int id = GridUtil.locToNumber(gx, gy);
				indexes.add(id);
			}
		}
		return indexes;
	}

	/**
	 * 比较两个网格是否相邻
	 * @param gridId1 网格1
	 * @param range1  网格1的范围
	 * @param gridId2 网格2
	 * @param range2  网格2的范围
	 * @return
	 */
	default boolean isNearBy(int gridId1, int range1, int gridId2, int range2) {
		//将grid 1 范围扩大一倍计算后，是否包含grid 2的格子
		Set<Integer> gridId1List = getGridIds(gridId1, range1 + 1);
		Set<Integer> gridId2List = getGridIds(gridId2, range2);
		//求交集
		Set<Integer> intersection = gridId1List.stream().filter(gridId2List::contains).collect(Collectors.toSet());
		return intersection.size() > 0;
	}

	/**
	 * 根据左下角索引id
	 * 比较两个网格是否相邻
	 * @param indexId1
	 * @param range1
	 * @param indexId2
	 * @param range2
	 * @return
	 */
	default boolean isNearByWithLeftDownIndexId(int indexId1, int range1, int indexId2, int range2) {
		int gridId1= GridUtil.leftDownIndexIdToGridId(indexId1,getGridNumWithWidth());
		int gridId2= GridUtil.leftDownIndexIdToGridId(indexId2,getGridNumWithWidth());
		return isNearBy(gridId1,range1,gridId2,range2);
	}

	/**
	 * 根据当前网格的格子ID取此格子范围在另外一层网格所交叉的格子id集合
	 * (取格子范围在下层网格所在格子的集合)
	 * @param grid          当前网格的格子
	 * @param otherGridMesh 其它网格
	 * @return 返回other中交叉的格子
	 */
	default Set<Grid> getCrossGrids(int grid, IGridMeshService otherGridMesh) {
		Set<Grid> grids = new LinkedHashSet<>();
		Set<Integer> ids=getCrossGridIds(grid,otherGridMesh);
		for(Integer id:ids){
			Grid g=new Grid(id,this);
			grids.add(g);
		}
		return grids;
	}

	/**
	 * 根据当前网格的格子ID取此格子范围在另外一层网格所交叉的格子id集合
	 * (取格子范围在下层网格所在格子的集合)
	 * @param indexId       当前网格的格子左下角求余索引
	 * @param otherGridMesh 其它网格
	 * @return 返回other中交叉的格子
	 */
	default Set<Grid> getCrossGridsByLeftDownIndexId(int indexId, IGridMeshService otherGridMesh) {
		int gridId=getGridByLeftDownIndexId(indexId).getId();
		Set<Grid> grids = new LinkedHashSet<>();
		Set<Integer> ids=getCrossGridIds(gridId,otherGridMesh);
		for(Integer id:ids){
			Grid g=new Grid(id,this);
			grids.add(g);
		}
		return grids;
	}

	/**
	 * 根据当前网格的格子ID取此格子范围在另外一层网格所交叉的格子id集合
	 * (取格子范围在下层网格所在格子的集合)
	 *
	 * @param grid          当前网格的格子
	 * @param otherGridMesh 其它网格
	 * @return 返回other中交叉的格子
	 */
	default Set<Integer> getCrossGridIds(int grid, IGridMeshService otherGridMesh) {
		float[] xy = GridUtil.getGridIdLeftLowerPos(grid,getGridWidthLength(),getGridHeightLength());//左下角顶点坐标
		float x = xy[0];
		float y = xy[1];
		float xRange = getGridWidthLength() / 2;
		float yRange = getGridHeightLength() / 2;
		float centerX = x + xRange;
		float centerY = y + yRange;
		return otherGridMesh.getGridIds(centerX, centerY, xRange, yRange);
	}

	/**
	 * 计算线通过了哪些当前网格的格子
	 * @param ox 线起始点
	 * @param oy
	 * @param tx 线结束点
	 * @param ty
	 * @return
	 */
	default Set<Grid> getCrossGridsByLine(float ox, float oy, float tx, float ty){
		Set<Grid> grids = new LinkedHashSet<>();
		Set<Integer> ids=getCrossGridIdsByLine(ox,oy,tx,ty);
		for(Integer id:ids){
			Grid grid=new Grid(id,this);
			grids.add(grid);
		}
		return grids;
	}

	/**
	 * 计算线通过了哪些当前网格的格子id
	 * @param ox 线起始点
	 * @param oy
	 * @param tx 线结束点
	 * @param ty
	 * @return
	 */
	default Set<Integer> getCrossGridIdsByLine(float ox, float oy, float tx, float ty){
		Set<Integer> indexes = new LinkedHashSet<>();
		if(ox==tx&&oy==ty)
		{
			indexes.add(getGridId(ox,oy));
			return indexes;
		}
		int oid=getGridId(ox,oy);//起点格子id
		int tid=getGridId(tx,ty);//结束点格子id
		int[] oidLoc= GridUtil.numberToLoc(oid);//起点格子索引坐标
		int[] tidLoc= GridUtil.numberToLoc(tid);//目标点格子索引坐标
		int oidIndexX=oidLoc[0];
		int oidIndexY=oidLoc[1];
		int tidIndexX=tidLoc[0];
		int tidIndexY=tidLoc[1];
		int xRange=Math.abs(oidIndexX-tidIndexX);
		int yRange=Math.abs(oidIndexY-tidIndexY);
		Set<Integer> gridIds=getGridIds(oid,xRange,yRange);
		for(Integer gridId:gridIds)
		{
			float[] leftUp= GridUtil.getGridIdLeftUpperPos(gridId,getGridWidthLength(),getGridHeightLength());
			float[] rightDown= GridUtil.getGridIdRightLowerPos(gridId,getGridWidthLength(),getGridHeightLength());
			if(isLineIntersectRectangle(ox,oy,tx,ty,leftUp[0],leftUp[1],rightDown[0],rightDown[1])){
				indexes.add(gridId);
			}
		}
		return indexes;
	}

	/**
	 * 坐标是否在世界
	 * @param x
	 * @param y
	 * @return
	 */
	default boolean inWorld(float x, float y){
		return x<0||x>getWorldWidthLength()||y<0||y>getWorldHeightLength();
	}

	/**
	 * <p>
	 * 判断线段是否在矩形内
	 * </p>
	 * 先看线段所在直线是否与矩形相交， 如果不相交则返回false， 如果相交，
	 * 则看线段的两个点是否在矩形的同一边（即两点的x(y)坐标都比矩形的小x(y)坐标小，或者大）, 若在同一边则返回false， 否则就是相交的情况。
	 * @param linePointX1           线段起始点x坐标
	 * @param linePointY1           线段起始点y坐标
	 * @param linePointX2           线段结束点x坐标
	 * @param linePointY2           线段结束点y坐标
	 * @param rectangleLeftTopX     矩形左上点x坐标
	 * @param rectangleLeftTopY     矩形左上点y坐标
	 * @param rectangleRightBottomX 矩形右下点x坐标
	 * @param rectangleRightBottomY 矩形右下点y坐标
	 * @return 是否相交
	 */
	static boolean isLineIntersectRectangle(float linePointX1, float linePointY1, float linePointX2, float linePointY2, float rectangleLeftTopX, float rectangleLeftTopY,
                                            float rectangleRightBottomX, float rectangleRightBottomY) {
		float lineHeight = linePointY1 - linePointY2;
		float lineWidth = linePointX2 - linePointX1; // 计算叉乘
		float c = linePointX1 * linePointY2 - linePointX2 * linePointY1;
		if ((lineHeight * rectangleLeftTopX + lineWidth * rectangleLeftTopY + c >= 0 && lineHeight * rectangleRightBottomX + lineWidth * rectangleRightBottomY + c <= 0)
				|| (lineHeight * rectangleLeftTopX + lineWidth * rectangleLeftTopY + c <= 0 && lineHeight * rectangleRightBottomX + lineWidth * rectangleRightBottomY + c >= 0)
				|| (lineHeight * rectangleLeftTopX + lineWidth * rectangleRightBottomY + c >= 0 && lineHeight * rectangleRightBottomX + lineWidth * rectangleLeftTopY + c <= 0)
				|| (lineHeight * rectangleLeftTopX + lineWidth * rectangleRightBottomY + c <= 0 && lineHeight * rectangleRightBottomX + lineWidth * rectangleLeftTopY + c >= 0)) {

			if (rectangleLeftTopX > rectangleRightBottomX) {
				float temp = rectangleLeftTopX;
				rectangleLeftTopX = rectangleRightBottomX;
				rectangleRightBottomX = temp;
			}
			if (rectangleLeftTopY < rectangleRightBottomY) {
				float temp1 = rectangleLeftTopY;
				rectangleLeftTopY = rectangleRightBottomY;
				rectangleRightBottomY = temp1;
			}
			if ((linePointX1 < rectangleLeftTopX && linePointX2 < rectangleLeftTopX) || (linePointX1 > rectangleRightBottomX && linePointX2 > rectangleRightBottomX)
					|| (linePointY1 > rectangleLeftTopY && linePointY2 > rectangleLeftTopY) || (linePointY1 < rectangleRightBottomY && linePointY2 < rectangleRightBottomY)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 以格子尺寸
	 * 构建一个默认规则的网格服务
	 * @param worldW
	 * @param worldH
	 * @param gridW
	 * @param gridH
	 * @return
	 */
	static IGridMeshService build(float worldW, float worldH, float gridW, float gridH) {
		return new IGridMeshService() {
			@Override
			public float getWorldHeightLength() {
				return worldH;
			}

			@Override
			public float getWorldWidthLength() {
				return worldW;
			}

			@Override
			public float getGridWidthLength() {
				return gridW;
			}

			@Override
			public float getGridHeightLength() {
				return gridH;
			}
		};
	}
}
