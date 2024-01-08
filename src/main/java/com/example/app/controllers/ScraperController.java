package com.example.app.controllers;

import com.example.app.dto.CardDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
public class ScraperController {

    private static final Logger logger = LogManager.getLogger(ScraperController.class);


    @GetMapping()
    public ResponseEntity get() throws Exception {
        logger.info((new Date()).toString());
        String listUrl1 = "https://www.ligamagic.com.br/?view=colecao%2Fcolecao&orderBy=8&modoExibicao=1&modoPrecos=7&pgA=5778&pgB=6822&pgC=41102.67&pgD=68084.68&pgE=117988.60&pgF=1901.89&pgG=3281.42&pgH=4487.63&id=56963&txtIdiomaValue=&txtEdicaoValue=&txt_qualid=&txt_raridade=&txt_extra=&txt_carta=&txt_preco_de=&txt_preco_ate=&txt_formato=&txt_tipo=";
//        String listUrl1 = "https://www.ligamagic.com.br/?view=colecao/colecao&id=168748"; // want
        String listUrl2 = "https://www.ligamagic.com.br/?view=colecao/colecao&id=185849"; //upgrades lathril

        ArrayList<CardDto> cards1 = getCards(listUrl1);
        ArrayList<CardDto> cards2 = getCards(listUrl2);

        List<CardDto> cardsMatches = getCardsMatches(cards1, cards2);

        logger.info((new Date()).toString());

        return ResponseEntity.ok(cardsMatches);
    }

    private List<CardDto> getCardsMatches(ArrayList<CardDto> cards1, ArrayList<CardDto> cards2) {
        ArrayList<CardDto> cardsMatches = new ArrayList<>();
        for (CardDto card2 : cards2) {
            for (CardDto card1 : cards1) {
                if (card1.originalName.equals(card2.originalName) && card1.name.equals(card2.name)) {
                    cardsMatches.add(card1);
                    break;
                }
            }
        }

        return cardsMatches;
    }

    private ArrayList<CardDto> getCards(String url) throws Exception {

        int numberPages = getNumberPages(url);
        if (numberPages == 0) {
            throw new Exception("number not found");
        }

        return extractData(url, numberPages);
    }

    private int getNumberPages(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Element rodapeEl = document.getElementsByClass("rodape-secao").first();
            if (rodapeEl != null) {
                Element spanEl = rodapeEl.getElementsByTag("span").first();
                if (spanEl != null) {
                    Element bEl = spanEl.getElementsByTag("b").first();
                    if (bEl != null) {
                        String quantityCards = bEl.text();
                        String quantityCardsReplaced = quantityCards.replace(".", "");
                        Integer quantityCardsInt = Integer.valueOf(quantityCardsReplaced);

                        double cardsPerPage = (double) quantityCardsInt / 80;

                        return (int) Math.ceil(cardsPerPage);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error to get number ", ex);
        }

        return 0;
    }


    private ArrayList<CardDto> extractData(String url, int numberPages) {
        try {
            ArrayList<CardDto> cardNames = new ArrayList<>();
            for (int i = 1; i <= numberPages; i++) {
                String urlReplaced = url.replace("&page=\\\\d+", "");
                String newUrl = urlReplaced.concat("&page=" + i);
                Document document = Jsoup.connect(newUrl)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                        .referrer("https://www.google.com")
                        .ignoreContentType(true).get();

                Element table = document.getElementById("listacolecao");
                Element bodyTable = table.getElementsByTag("tbody").first();
                Elements rows = bodyTable.getElementsByTag("tr");

                rows.forEach(row -> {
                    Elements linksEl = row.getElementsByTag("a");

                    CardDto cardDto = new CardDto();
                    for (int j = 0; j < linksEl.size(); j++) {
                        Element link = linksEl.get(j);
                        if (j == 0) {
                            cardDto.setName(link.text());
                        } else {
                            cardDto.setOriginalName(link.text());
                        }
                    }

                    if (cardDto.getName() != null && cardDto.getOriginalName() != null) {
                        cardNames.add(cardDto);
                    }
                });

                Thread.sleep(250);
            }

            return cardNames;
        } catch (Exception ex) {
            logger.error("Error to extract", ex);
        }
        return null;
    }
}
