package com.example.app.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class ScryfallResponseDto {
    String object;
    Integer total_cards;
    Boolean has_more;
    JsonNode data;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getTotal_cards() {
        return total_cards;
    }

    public void setTotal_cards(Integer total_cards) {
        this.total_cards = total_cards;
    }

    public Boolean getHas_more() {
        return has_more;
    }

    public void setHas_more(Boolean has_more) {
        this.has_more = has_more;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
