package ru.petrutik.samrtbuy.parseservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParseOzonService implements ParseMarketService{
    private final Logger logger;
    private final String marketBaseUrl = "https://www.ozon.ru";
    private final String searchUrl = marketBaseUrl + "/search/?from_global=true&sorting=price&text=";
    private final String jsonGetUrl = marketBaseUrl +"/api/composer-api.bx/page/json/v2?url=";
    private final ObjectMapper mapper = new ObjectMapper();

    public ParseOzonService() {
        this.logger = LoggerFactory.getLogger(ParseOzonService.class);
    }

    @Override
    public List<ProductDto> parseQuery(String searchQuery) {
        String encodedSearchText = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
        List<ProductDto> products = new ArrayList<>();
        try {
            Document result = Jsoup.connect(searchUrl + encodedSearchText).userAgent("Mozilla").get();

            Set<String> links = result.select("a[href]")
                    .stream()
                    .map(e -> e.attr("href"))
                    .filter(link -> link.startsWith("/product/"))
                    .collect(Collectors.toSet());

            for (String link : links) {
                products.add(parseUsingOzonJsonService(marketBaseUrl + link + "\n"));
            }
        } catch (IOException e) {
            logger.error("Unable to get results from Ozon, Jsoup connection error. ", e);
        }
        return products;
    }

    private ProductDto parseUsingOzonJsonService(String fullLink) throws IOException {
        //get json response
        Document jsonDoc = Jsoup.connect(jsonGetUrl + fullLink)
                .userAgent("Mozilla")
                .ignoreContentType(true)
                .get();
        JsonNode rootNode = mapper.readTree(jsonDoc.body().text());

        //get Ozon card price value
        JsonNode node = rootNode.findPath("widgetStates");
        String priseFieldName = "";
        Iterator<String> iterator = node.fieldNames();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.startsWith("webPrice")) {
                priseFieldName = key;
                break;
            }
        }
        node = node.findPath(priseFieldName);
        String[] webPrice = String.valueOf(node)
                .replace("\\\"", "")
                .split(",");
        String priceString = webPrice[1]  //second value - card price
                .split(":")[1]
                .replaceAll("[^\\d.]", "");
        long price = Long.parseLong(priceString);

        return new ProductDto(fullLink, BigDecimal.valueOf(price));
    }
}
