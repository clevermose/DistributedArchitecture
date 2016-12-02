package com.whs.da.searchengine;

/**
 * Lucene的Field对象
 * @author haiswang
 *
 */
public class LuceneField {
    
    //field的名称
    private String fieldName = null;
    
    //field的值
    private String fieldValue = null;
    
    //值的类型
    private ValueType valueType = null;
    
    //是否需要分词
    private boolean isAnalyzer;
    
    public LuceneField() {}
    
    public LuceneField(String fieldName, String fieldValue, ValueType valueType, boolean isAnalyzer) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.isAnalyzer = isAnalyzer;
        this.valueType = valueType;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public boolean isAnalyzer() {
        return isAnalyzer;
    }

    public void setAnalyzer(boolean isAnalyzer) {
        this.isAnalyzer = isAnalyzer;
    }
    
    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }
}
