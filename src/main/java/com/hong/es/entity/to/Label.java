package com.hong.es.entity.to;

/**
 * 标签
 **/
public class Label {

    //标签名
    private String label;

    //完整标签名
    private String fullLabel;

    //风险分层一级标签代码
    private String level1;

    //风险分层二级标签代码
    private String level2;

    //风险分层三级标签代码
    private String level3;

    //风险分层一级标签
    private String level1Name;

    //风险分层二级标签
    private String level2Name;

    //风险分层三级标签
    private String level3Name;

    public void setLabelInfo(LabelVO labelVO){
        this.label = labelVO.getLabel();
        this.fullLabel = labelVO.getFullLabel();
        this.level1 = labelVO.getLevel1();
        this.level2 =labelVO.getLevel2();
        this.level3= labelVO.getLevel3();
        this.level1Name =labelVO.getLevel1Name();
        this.level2Name = labelVO.getLevel2Name();
        this.level3Name =labelVO.getLevel3Name();

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFullLabel() {
        return fullLabel;
    }

    public void setFullLabel(String fullLabel) {
        this.fullLabel = fullLabel;
    }

    public String getLevel1() {
        return level1;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public String getLevel3() {
        return level3;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    public String getLevel1Name() {
        return level1Name;
    }

    public void setLevel1Name(String level1Name) {
        this.level1Name = level1Name;
    }

    public String getLevel2Name() {
        return level2Name;
    }

    public void setLevel2Name(String level2Name) {
        this.level2Name = level2Name;
    }

    public String getLevel3Name() {
        return level3Name;
    }

    public void setLevel3Name(String level3Name) {
        this.level3Name = level3Name;
    }
}
