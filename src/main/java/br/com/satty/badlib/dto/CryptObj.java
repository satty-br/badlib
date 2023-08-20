package br.com.satty.badlib.dto;

public class CryptObj {
    public CryptObj(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String id;
    private String data;

    public String toJsonString() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"").append(id).append("\",");
        json.append("\"data\":").append(data);
        json.append("}");
        return json.toString();
    }
}
