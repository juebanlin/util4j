package net.jueb.util4j.aoi.aoiGroup;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

public class Aoi<T extends AoiEntity> {

    private float gridWidth;

    private float gridHeight;

    /**
     * 横向格子数量
     */
    private int wNum;

    /**
     * 纵向格子数量
     */
    private int hNum;

    /**
     * 总格子数量
     */
    private int num;

    /**
     * 区域格子
     */
    private AoiArea[] areas;

    /**
     * 在一起的
     * 分组的实体
     */
    private List<AoiGroup> groups;

    /**
     * 所有实体
     */
    private Map<Long, T> entityMap;

    /**
     * 实体和组的关系
     */
    private Map<T,AoiGroup> entity_group;


    private Set<Long> idTmps;

    @Getter
    @Setter
    private class AoiGroup {
        boolean drop;
        final Set<T> entitys = new HashSet<>();
        public void add(T e) {
            entitys.add(e);
            setAoiGroup(e,this);
        }
    }


    public Aoi(int num,float worldWidth, float worldHeight, float gridSize) {
        this(num,worldWidth,worldHeight,gridSize,gridSize);
    }

    /**
     *
     * @param entityNum
     * @param worldWidth
     * @param worldHeight
     * @param gridWidth AOI网格宽度，理论上应该跟实体直径差不多大。
     * @param gridHeight AOI网格高度，理论上应该跟实体直径差不多大。
     */
    public Aoi(int entityNum,float worldWidth, float worldHeight, float gridWidth,float gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.wNum = (int) Math.ceil(worldWidth / this.gridWidth);//向上取整
        this.hNum = (int) Math.ceil(worldHeight / this.gridHeight);
        this.num = wNum * hNum;
        idTmps = new HashSet<>();
        areas = new AoiArea[this.num];
        groups = new ArrayList<>(entityNum / 2 + 1);// 最差情况两两分组
        entityMap = new HashMap<>();
        entity_group=new HashMap<>();
    }

    public AoiResult<T> input(List<T> list){
        for (int i = 0; i < list.size(); i++) {
            T aoiEntity = list.get(i);
            enter(aoiEntity,aoiEntity.getAoiId());
        }
        return buildResult(list);
    }

    private AoiResult<T> buildResult(List<T> list){
        AoiResult<T> aoiResult=new AoiResult();
        for (int i = 0; i < list.size(); i++) {
            T aoiEntity = list.get(i);
            AoiGroup aoiGroup = getAoiGroup(aoiEntity);
            if(aoiGroup==null){
                aoiResult.noGroups.add(aoiEntity);
                continue;
            }
        }
        for (int i = 0; i < groups.size(); i++) {
            AoiGroup group = groups.get(i);
            if(!group.isDrop()){
                aoiResult.groups.add(new ArrayList<>(group.entitys));
            }
        }
        return aoiResult;
    }

    private AoiGroup getAoiGroup(T aoiEntity){
        return entity_group.get(aoiEntity);
    }

    private void setAoiGroup(T aoiEntity,AoiGroup aoiGroup){
        entity_group.put(aoiEntity,aoiGroup);
    }

    public void enter(T e,Long id) {
        entityMap.put(id, e);
        // 放入area
        put(e,id);
        // 加入分组
        addGroup(e,id);
    }

    private void put(T e, Long id) {
        forInGrid(e,idx->{
            fillAreaId(idx,id);
        });
    }

    private void forInGrid(T current, IntConsumer grid){
        float x=current.getAoiX();
        float y=current.getAoiY();
        int gridX = Math.max(0, (int) Math.floor(x / gridWidth));//x轴格子坐标
        int gridY = Math.max(0, (int) Math.floor(y / gridHeight));//y轴格子坐标
        float xRange=current.getAoiRange();
        float yRange=current.getAoiRange();
        //以格子的4个边界为起点的range(需要去掉range在格子中所占用的长度)
        float fixRange_Left = xRange - (x % gridWidth);//朝左的长度
        float fixRange_Right = xRange - (gridWidth - x % gridWidth);//朝右的长度
        float fixRange_Up = yRange - (gridHeight - y % gridHeight);//朝上的长度
        float fixRange_Down = yRange - (y % gridHeight);//朝下的长度
        //4个方向的边界延伸所占用的格子数
        int RangeXLNum = (int) Math.ceil(fixRange_Left / gridWidth);//x左边
        int RangeXRNum = (int) Math.ceil(fixRange_Right / gridWidth);//x右边
        int RangeYUNum = (int) Math.ceil(fixRange_Up / gridHeight);//y上边
        int RangeYDNum = (int) Math.ceil(fixRange_Down / gridHeight);//y下边
        //占用的所有格子的范围
        int ox = gridX - RangeXLNum;
        int oy = gridY - RangeYDNum;
        int ex = gridX + RangeXRNum;
        int ey = gridY + RangeYUNum;
        for (int gx = ox; gx <= ex; gx++) {
            for (int gy = oy; gy <= ey; gy++) {
                if (gx < 0 || gx >= wNum || gy < 0 || gy >= hNum) {//越界
                    continue;
                }
                //格子数坐标转换为id索引
                int idx = gx + gy * wNum;
                grid.accept(idx);
            }
        }
    }

    /**
     * 对实体进行相交检测，将重合的实体分为一组。
     * 
     * @param current
     */
    private void addGroup(T current,Long currentId) {
        forInGrid(current,idx->{
            AoiArea area = areas[idx];
            if (area != null) {
                idTmps.addAll(area);
            }
        });
        if (idTmps.isEmpty()) {// 没有area，跳出
            return;
        }
        if (idTmps.size()>at3.get()){
            at3.set(idTmps.size());
        }
        for (Long id : idTmps) {
            // 相同实体
            if (id.equals(currentId)) {
                continue;
            }
            T entity = entityMap.get(id);
            // 分组相同，不需要检测了
            AoiGroup aoiGroup = getAoiGroup(entity);
            at1.incrementAndGet();
            if (aoiGroup != null && aoiGroup == getAoiGroup(current)) {
                continue;
            }
            // 碰撞检测
            if (isCollision(current,entity)) {
                mergeGroup(current, entity);
                at2.incrementAndGet();
            }
        }
        idTmps.clear();
    }

    public static AtomicInteger at1=new AtomicInteger(0);
    public static AtomicInteger at2=new AtomicInteger(0);
    public static AtomicInteger at3=new AtomicInteger(0);

    /**
     * 碰撞检测
     * @param a
     * @param b
     * @return
     */
    private boolean isCollision(T a, T b){
        float dy = a.getAoiY() - b.getAoiY();
        float dx = a.getAoiX() - b.getAoiX();
        float dr = a.getAoiRange() + b.getAoiRange();
        return Math.abs(Math.pow(dx,2)) + Math.abs(Math.pow(dy,2)) <= Math.pow(dr,2);
    }


    /**
     * 找到了相交的实体，将其放到一组中。
     * 
     * @param self
     * @param other
     */
    private void mergeGroup(T self, T other) {
        AoiGroup selfGroup = getAoiGroup(self);
        AoiGroup otherGroup = getAoiGroup(other);
        if (selfGroup == null) {
            if (otherGroup == null) {
                //双方都没有组,创建组
                AoiGroup group = new AoiGroup();
                groups.add(group);
                group.add(self);
                group.add(other);
            } else {
                // 加入对方组
                otherGroup.add(self);
            }
            return;
        }
        if (otherGroup == null) {
            // 对方没有组,对应加入我组
            selfGroup.add(other);
        } else {
            //对方有组
            if (selfGroup == otherGroup) {
                // 同组，不做处理
                return;
            } else {
                // 这个实体碰到了2个不同的组，意味着这两组实体通过它连接成了一组。
                for (T o : otherGroup.entitys) {
                    //这个组包括other本身和其它在这个组的都加入到当前对象组
                    selfGroup.add(o);
                }
                // 干掉原来的分组
                otherGroup.setDrop(true);
            }
        }
    }

    /**
     * 填充区域ID
     * @param idx
     * @param id
     */
    private void fillAreaId(int idx, Long id) {
        if (areas[idx] == null) {
            areas[idx] = new AoiArea();
        }
        areas[idx].add(id);
    }
}