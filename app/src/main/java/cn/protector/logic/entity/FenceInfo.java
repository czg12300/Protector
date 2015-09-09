package cn.protector.logic.entity;

/**
 * 描述：围栏信息信息实体类
 *
 * @author Created by Administrator on 2015/9/4.
 */
public class FenceInfo implements JsonParse {
    public String name;
    public String address;
    public int range;

    @Override
    public void parse(String json) {

    }
}
