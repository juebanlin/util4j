package net.jueb.util4j.aoi.aoiGroup;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AoiResult<T extends AoiEntity> {

    final List<T> noGroups=new ArrayList<>();
    final List<List<T>> groups=new ArrayList<>();
}
