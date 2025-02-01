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
        logger.info("Start parsing request in Ozone parse service");
        try {
            logger.info("Try to get search url answer thought jsoup, search query: {}", searchQuery);
            Document result = Jsoup.connect(searchUrl + encodedSearchText)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:134.0) Gecko/20100101 Firefox/134.0")
                    .cookie("abt_data", "7.mNtYuRTS0xtELWXw0QrTRV1_vE_5_iml3o4Gf5Ls19tASvfKrBd1IcN6mbf0XMe0kJ65CHTM-zN2Wa1barlJ_m8P_O5YuwapF4kUtFm1WPjPKCWf3oXhYBvgjbXpBhTUBaahmA8R_UZ6XlDhRJuzNRsLebrdVBzySN9hTc_CEXlCUfNZx34ddGu1TK1x98bk6nuGEXYV3gERNmfU_GFMO9Afy1f028KUpsMKHhdy6CWmtU5E75WiaSitsPOQaBd7Lz7dqT_FZsIN2gpLS13hew3S8OHio6QTHsj0n98w_th0MLY87ymU7nF35ZX4tokzZ7dTZ1ImKbCY-F91k7pqy04crf-y2zg2HFiaCg")
                    .cookie("__Secure-access-token", "7.0.2bavL4_sS2a1T4goxOtYNQ.39.ASg8Ril1r9vvpVgOQ_WJs0bQ-t64u64KexWiyeuu6U_OOvlqXzGr_dy1hKdFcWS9Eg..20250130141156.MMZk0uAA0X18DbvBZmiJzj29iL6CfLhV6C0Apt4ihCA.16b02f471eec0d053")
                    .get();

            logger.info("Jsoup connection ok, getting links from result document.");
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
        logger.info("Start parsing product JSON in Ozone parse service, link: {}", fullLink);
        Document jsonDoc = Jsoup.connect(jsonGetUrl + fullLink)
                .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:134.0) Gecko/20100101 Firefox/134.0")
                .ignoreContentType(true)
                .cookie("abt_data", "7.mNtYuRTS0xtELWXw0QrTRV1_vE_5_iml3o4Gf5Ls19tASvfKrBd1IcN6mbf0XMe0kJ65CHTM-zN2Wa1barlJ_m8P_O5YuwapF4kUtFm1WPjPKCWf3oXhYBvgjbXpBhTUBaahmA8R_UZ6XlDhRJuzNRsLebrdVBzySN9hTc_CEXlCUfNZx34ddGu1TK1x98bk6nuGEXYV3gERNmfU_GFMO9Afy1f028KUpsMKHhdy6CWmtU5E75WiaSitsPOQaBd7Lz7dqT_FZsIN2gpLS13hew3S8OHio6QTHsj0n98w_th0MLY87ymU7nF35ZX4tokzZ7dTZ1ImKbCY-F91k7pqy04crf-y2zg2HFiaCg")
                .cookie("__Secure-access-token", "7.0.2bavL4_sS2a1T4goxOtYNQ.39.ASg8Ril1r9vvpVgOQ_WJs0bQ-t64u64KexWiyeuu6U_OOvlqXzGr_dy1hKdFcWS9Eg..20250130141156.MMZk0uAA0X18DbvBZmiJzj29iL6CfLhV6C0Apt4ihCA.16b02f471eec0d053")
                .get();
        JsonNode rootNode = mapper.readTree(jsonDoc.body().text());

        logger.info("Jsoup connection ok, getting values of product from result document.");
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

        //Cut additional data from link
        String shortLink = fullLink.split("/\\?")[0];
        String link = (shortLink == null || shortLink.isEmpty()) ? fullLink : shortLink;

        return new ProductDto(link, BigDecimal.valueOf(price));
    }
}
