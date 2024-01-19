package com.example.app.api;

import com.example.app.dto.ScryfallResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ScryfallApiService {

    private final String apiUrl = "https://api.scryfall.com/";

    private final RestTemplate restTemplate;

    public ScryfallApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ScryfallResponseDto fetchDataFromApi(String parameters) {
        String apiUrl = "https://api.scryfall.com/";
        String scryfallModel =  restTemplate.getForObject(apiUrl + parameters, String.class);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(scryfallModel, ScryfallResponseDto.class);
    }
}
