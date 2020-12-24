package net.jueb.util4j.aoi.aoiGroup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Aoi<T extends AoiEntity> {

    private float size;

    private float invSize;

    private int w;

    private int h;

    private int len;

    private Set<Long> tmp;

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

    @Getter
    @Setter(AccessLevel.PACKAGE)
    class AoiGroup {
        boolean drop;
        final Set<T> entitys = new HashSet<>();
        public void add(T e) {
            entitys.add(e);
            setAoiGroup(e,this);
        }
    }

    public Aoi(int num,float width, float height, float size) {
        this.size = size;
        this.invSize = 1f / size;
        this.w = (int) Math.ceil(width * invSize) + 2;
        this.h = (int) Math.ceil(height * invSize) + 2;
        this.len = w * h;
        tmp = new HashSet<>();
        areas = new AoiArea[len];
        groups = new ArrayList<>(num / 2 + 1);// 最差情况两两分组
        entityMap = new HashMap<>();
        entity_group=new HashMap<>();
    }

    public AoiResult<T> input(List<T> list){
        for (int i = 0; i < list.size(); i++) {
            T aoiEntity = list.get(i);
            enter(aoiEntity);
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

    public void enter(T e) {
        entityMap.put(e.getAoiId(), e);
        // 放入area
        put(e);
        // 加入分组
        addGroup(e);
    }

    /**
     * 对实体进行相交检测，将重合的实体分为一组。
     * 
     * @param current
     */
    private void addGroup(T current) {
        boolean hasArea = false;

        int x = (int) Math.ceil(current.getAoiX() * invSize) + 1;
        int y = (int) Math.ceil(current.getAoiY() * invSize) + 1;

        for (int dx = x - 2; dx <= x + 2; dx++) {
            for (int dy = y - 2; dy <= y + 2; dy++) {
                if (dx < 0 || dy < 0 || dx >= w || dy >= h) {
                    continue;
                }

                int idx = dx + dy * w;
                AoiArea area = areas[idx];
                if (area == null) {
                    continue;
                }

                tmp.addAll(area);
                hasArea = true;// 检测到一个area，flag标识为true
            }
        }
        if (!hasArea) {// 没有area，跳出
            return;
        }
        for (Long id : tmp) {
            T it = entityMap.get(id);
            // 相同实体
            if (it.getAoiId() == current.getAoiId()) {
                continue;
            }
            // 分组相同，不需要检测了
            AoiGroup aoiGroup = getAoiGroup(it);
            if (aoiGroup != null && aoiGroup == getAoiGroup(current)) {
                continue;
            }
            // 碰撞检测
            if (isCollision(current,it)) {
                mergeGroup(current, it);
            }
        }
        tmp.clear();
    }

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
        return dx * dx + dy * dy <= dr * dr;
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

    private void put(T e) {
        int xmin = (int) Math.ceil((e.getAoiX() - e.getAoiRange()) * invSize);
        int ymin = (int) Math.ceil((e.getAoiY() - e.getAoiRange()) * invSize);

        int xmax = (int) Math.ceil((e.getAoiX() + e.getAoiRange()) * invSize);
        int ymax = (int) Math.ceil((e.getAoiY() + e.getAoiRange()) * invSize);

        if (xmin != xmax || ymin != ymax) {
            for (int dx = xmin; dx <= xmax; dx++) {
                for (int dy = ymin; dy <= ymax; dy++) {
                    fillAreaId(dx, dy, e.getAoiId());
                }
            }
        } else {
            fillAreaId(xmin, ymin, e.getAoiId());
        }
    }

    /**
     * 填充区域ID
     * @param x
     * @param y
     * @param id
     */
    private void fillAreaId(int x, int y, long id) {
        int idx = x + y * w;
        if (areas[idx] == null) {
            areas[idx] = new AoiArea();
        }
        areas[idx].add(id);
    }
}