package com.example.app.controllers;

import com.example.app.api.ScryfallApiService;
import com.example.app.dto.CardCsvDto;
import com.example.app.dto.ScryfallResponseDto;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExportMatchesController {

    @Autowired
    private ScryfallApiService scryfallApiService;

    @GetMapping("export/matches")
    public ScryfallResponseDto get() throws IOException, CsvException {
        /*String file1 = "src/main/resources/test/lathril.csv";
        String file2 = "src/main/resources/test/want.csv";
        List<CardCsvDto> list1 = readCsvFile(file1);
        List<CardCsvDto> list2 = readCsvFile(file2);

        List<CardCsvDto> cardsMatches = getCardsMatches(list1, list2);

        return cardsMatches;*/
        if (scryfallApiService != null) {
            ScryfallResponseDto scryfallResponseDto = scryfallApiService.fetchDataFromApi("cards/search?q=negate");
            return scryfallResponseDto;
        }

        return null;

    }

    private List<CardCsvDto> getCardsMatches(List<CardCsvDto> cards1, List<CardCsvDto> cards2) {
        ArrayList<CardCsvDto> cardsMatches = new ArrayList<>();
        for (CardCsvDto card2 : cards2) {
            for (CardCsvDto card1 : cards1) {
                if (card1.originalName.equals(card2.originalName) || card1.name.equals(card2.name)) {
                    cardsMatches.add(card1);
                    break;
                }
            }
        }

//        cardsMatches.sort((a, b) -> (int) (a.getPriceDouble() - b.getPriceDouble()));

        return cardsMatches;
    }

    private static List<CardCsvDto> readCsvFile(String file) throws IOException, CsvException {
        FileInputStream fileInputStream = new FileInputStream(file);

        Reader reader = new InputStreamReader(fileInputStream);

        CSVReader csvReader = new CSVReaderBuilder(reader).build();
        List<String[]> rows = csvReader.readAll();

        List<CardCsvDto> cardList = new ArrayList<>();

        int count = 0;
        for (String[] row : rows) {
            if (count > 0) {
                CardCsvDto cardCsvDto = new CardCsvDto();
                cardCsvDto.setSet(row[1]);
                cardCsvDto.setSetAcronym(row[2]);
                cardCsvDto.setName(row[3]);
                cardCsvDto.setOriginalName(row[4]);

                /*JsonNode ScryfallResponseDto = scryfallApiService.fetchDataFromApi("cards/search?q=" + cardCsvDto.getOriginalName());
                int totalCards = scryfallModel.get("total_cards").intValue();
                if (totalCards == 1) {
                    JsonNode data = scryfallModel.get("data");
                    JsonNode prices = data.get("prices");
                }*/

                cardList.add(cardCsvDto);
            }
            count++;
        }

        csvReader.close();

        return cardList;
    }
}
