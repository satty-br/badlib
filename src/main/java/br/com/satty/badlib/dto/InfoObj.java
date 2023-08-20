package br.com.satty.badlib.dto;

public class InfoObj {
    private String type;

    public String getType() {
        return type;
    }

    public InfoObj(String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String key;
    private String value;
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"").append(type).append("\",");
        json.append("\"key\":\"").append(key).append("\",");
        json.append("\"value\":\"").append(value).append("\"");
        json.append("}");
        return json.toString();
    }
}
