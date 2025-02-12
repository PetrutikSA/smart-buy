package ru.petrutik.smartbuy.requestservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.dto.ProductDto;
import ru.petrutik.smartbuy.event.dto.RequestDto;
import ru.petrutik.smartbuy.event.parse.request.AddRequestParseEvent;
import ru.petrutik.smartbuy.event.parse.request.UpdateRequestParseEvent;
import ru.petrutik.smartbuy.event.user.response.AddResponseEvent;
import ru.petrutik.smartbuy.event.user.response.ExceptionResponseEvent;
import ru.petrutik.smartbuy.event.user.response.ListAllResponseEvent;
import ru.petrutik.smartbuy.event.user.response.RemoveAllResponseEvent;
import ru.petrutik.smartbuy.event.user.response.RemoveResponseEvent;
import ru.petrutik.smartbuy.event.user.response.ShowResponseEvent;
import ru.petrutik.smartbuy.event.user.response.ShowResultsAfterAddResponseEvent;
import ru.petrutik.smartbuy.event.user.response.UserNotifyNewProductsEvent;
import ru.petrutik.smartbuy.requestservice.dto.mapper.ProductMapper;
import ru.petrutik.smartbuy.requestservice.dto.mapper.RequestMapper;
import ru.petrutik.smartbuy.requestservice.model.Product;
import ru.petrutik.smartbuy.requestservice.model.Request;
import ru.petrutik.smartbuy.requestservice.model.User;
import ru.petrutik.smartbuy.requestservice.repository.ProductRepository;
import ru.petrutik.smartbuy.requestservice.repository.RequestRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final ProductRepository productRepository;
    private final RequestMapper requestMapper;
    private final ProductMapper productMapper;
    private final KafkaTemplate<Long, Object> kafkaTemplate;
    private final String userResponseTopicName;
    private final String parseRequestTopicName;
    private final Logger logger;

    public RequestServiceImpl(UserService userService,
                              RequestRepository requestRepository, ProductRepository productRepository,
                              RequestMapper requestMapper, ProductMapper productMapper,
                              KafkaTemplate<Long, Object> kafkaTemplate,
                              @Value("#{@kafkaConfig.getUserResponseTopicName()}") String responseTopicName,
                              @Value("#{@kafkaConfig.getParseRequestTopicName()}") String parseRequestTopicName) {
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.productRepository = productRepository;
        this.requestMapper = requestMapper;
        this.productMapper = productMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.userResponseTopicName = responseTopicName;
        this.parseRequestTopicName = parseRequestTopicName;
        this.logger = LoggerFactory.getLogger(RequestServiceImpl.class);
    }

    @Override
    public void addRequest(Long chatId, String searchQuery, Integer maxPrice) {
        User user = userService.getUserByChatId(chatId);
        Request request = new Request();
        request.setSearchQuery(searchQuery);
        BigDecimal maxValue = (maxPrice == null) ? null : BigDecimal.valueOf(maxPrice);
        request.setMaxPrice(maxValue);
        request.setUser(user);
        request.setUpdated(false);
        logger.info("New request created: {}", request);
        List<Request> userRequests = requestRepository.findAllByUserId(user.getId());
        request.setRequestNumber(userRequests.size() + 1);
        requestRepository.save(request);
        logger.info("Request saved to DB: {}", request);
        AddResponseEvent addResponseEvent = new AddResponseEvent(chatId, userRequests.size() + 1);
        sendToUserKafkaTopic(chatId, addResponseEvent);
        AddRequestParseEvent addRequestParseEvent = new AddRequestParseEvent(request.getId(), request.getSearchQuery(),
                request.getMaxPrice());
        sendToParseKafkaTopic(request.getId(), addRequestParseEvent);
    }

    @Override
    public void getAllRequests(Long chatId) {
        User user = userService.getUserByChatId(chatId);
        logger.info("Getting user's request list, user: {}", user);
        List<Request> requests = requestRepository.findAllByUserId(user.getId());
        logger.info("Got user requests list: {}", requests);
        List<RequestDto> userRequestsDto = requests.stream()
                .map(requestMapper::requestToRequestDto)
                .toList();
        logger.info("Getting list of all requests of user: {}, requests: {}", user, userRequestsDto);
        ListAllResponseEvent listAllResponseEvent = new ListAllResponseEvent(chatId, userRequestsDto);
        sendToUserKafkaTopic(chatId, listAllResponseEvent);
    }

    @Override
    public void showRequest(Long chatId, Integer requestNumber) {
        Request request = getRequestByChatIdAndNumber(chatId, requestNumber);
        if (request != null) {
            logger.info("Getting request's products, request {}", request);
            List<Product> products = productRepository.findAllByRequestIdAndIsBanned(request.getId(), false);
            logger.info("Got request's product list: {}", products);
            List<ProductDto> productsDto = products.stream()
                    .map(productMapper::productToProductDto)
                    .toList();
            ShowResponseEvent showResponseEvent = new ShowResponseEvent(chatId, request.getSearchQuery(), productsDto);
            sendToUserKafkaTopic(chatId, showResponseEvent);
        }
    }

    @Override
    public void removeRequest(Long chatId, Integer requestNumber) {
        User user = userService.getUserByChatId(chatId);
        Request request = getRequestByChatIdAndNumber(chatId, requestNumber);
        if (request != null) {
            logger.info("Removing request {}", request);
            requestRepository.delete(request);
            logger.info("Getting all remain requests of user {}", user);
            List<Request> requests = requestRepository.findAllByUserId(user.getId());
            for (Request remainRequest : requests) {
                Integer remainRequestNumber = remainRequest.getRequestNumber();
                if (remainRequestNumber > requestNumber) remainRequest.setRequestNumber(remainRequestNumber - 1);
            }
            logger.info("Updating all remain requests with new request numbers of user {}", user);
            requestRepository.saveAll(requests);
            RemoveResponseEvent removeResponseEvent = new RemoveResponseEvent(chatId, requestNumber, requests.size());
            sendToUserKafkaTopic(chatId, removeResponseEvent);
        }
    }

    @Override
    public void removeAllRequest(Long chatId) {
        User user = userService.getUserByChatId(chatId);
        logger.info("Removing all requests of user {}", user);
        List<Request> requests = requestRepository.findAllByUserId(user.getId());
        requestRepository.deleteAll(requests);
        RemoveAllResponseEvent removeAllResponseEvent = new RemoveAllResponseEvent(chatId);
        sendToUserKafkaTopic(chatId, removeAllResponseEvent);
    }

    @Override
    public void resultParsingAfterAddRequest(Long requestId, List<ProductDto> productsDto) {
        if (productsDto != null && !productsDto.isEmpty()) {
            Optional<Request> requestOptional = requestRepository.findById(requestId);
            if (requestOptional.isPresent()) {
                Request request = requestOptional.get();
                logger.info("Got Product DTO list: {}", productsDto);
                List<Product> products = productsDto.stream()
                        .map(productDto -> productMapper.productDtoToProduct(productDto, request, false,
                                false))
                        .toList();
                logger.info("Map product DTOs to Entities list: {}", products);
                productRepository.saveAll(products);
                logger.info("Saved to DB products to request with id = {}", requestId);
                Long chatId = request.getUser().getChatId();
                ShowResultsAfterAddResponseEvent showResultsAfterAddResponseEvent =
                        new ShowResultsAfterAddResponseEvent(chatId, request.getSearchQuery(), productsDto);
                sendToUserKafkaTopic(chatId, showResultsAfterAddResponseEvent);
            } else {
                logger.error("Couldn't find request on which got parsing response, requestId = {}", requestId);
            }
        } else {
            logger.error("After add request parsing didn't find anything, request id = {}", requestId);
        }
    }

    @Override
    public void notifyUsers() {
        List<Request> updatedRequests = requestRepository.findAllByIsUpdated(true);
        logger.info("Got updated requests: {}", updatedRequests);
        if (!updatedRequests.isEmpty()) {
            List<Product> newProducts = productRepository.findAllByIsNew(true);
            logger.info("Got new products: {}", newProducts);
            if (!newProducts.isEmpty()) {
                Set<User> userToNotify = updatedRequests.stream()
                        .map(Request::getUser)
                        .collect(Collectors.toSet());
                logger.info("Got users to notify: {}", userToNotify);
                for (User user : userToNotify) {
                    Long chatId = user.getChatId();
                    List<Request> usersUpdatedRequests = updatedRequests.stream()
                            .filter(request -> Objects.equals(request.getUser().getId(), user.getId()))
                            .toList();
                    Map<String, List<ProductDto>> mapSearchQueryToListNewProducts = new HashMap<>();
                    for (Request request : usersUpdatedRequests) {
                        List<ProductDto> products = newProducts.stream()
                                .filter(product -> Objects.equals(product.getRequest().getId(), request.getId()))
                                .map(productMapper::productToProductDto)
                                .toList();
                        mapSearchQueryToListNewProducts.put(request.getSearchQuery(), products);
                    }
                    UserNotifyNewProductsEvent userNotifyNewProductsEvent =
                            new UserNotifyNewProductsEvent(chatId, mapSearchQueryToListNewProducts);
                    sendToUserKafkaTopic(chatId, userNotifyNewProductsEvent);
                }
            } else {
                logger.error("Have no new products, but have updated requests, process stopped.");
            }
        } else {
            logger.info("Have no updated requests to notify users, notify process stopped.");
        }
    }

    @Override
    public void updateRequestsInitialize() {
        logger.info("Getting all requests");
        List<Request> requests = requestRepository.findAll();
        for (Request request : requests) {
            long requestId = request.getId();
            logger.info("Sending parse update event for request with id = {}", requestId);
            UpdateRequestParseEvent updateRequestParseEvent =
                    new UpdateRequestParseEvent(requestId, request.getSearchQuery(), request.getMaxPrice());
            sendToParseKafkaTopic(requestId, updateRequestParseEvent);
        }
    }

    @Override
    public void updateRequest(Long requestId, List<ProductDto> productsDto) {
        if (productsDto != null && !productsDto.isEmpty()) {
            Optional<Request> requestOptional = requestRepository.findById(requestId);
            if (requestOptional.isPresent()) {
                Request request = requestOptional.get();
                List<Product> productsInDB = productRepository.findAllByRequestIdAndIsBanned(requestId, false);
                List<String> existedUrls = productsInDB.stream()
                        .map(Product::getUrl)
                        .toList();
                logger.info("Got existed url list: {}", existedUrls);
                logger.info("Got updates product DTO list: {}", productsDto);
                List<Product> products = productsDto.stream()
                        .map(productDto -> {
                            Product product = productMapper.productDtoToProduct(productDto, request, false,
                                    false);
                            if (!existedUrls.contains(product.getUrl())) {
                                product.setNew(true);
                                request.setUpdated(true);
                            }
                            return product;
                        })
                        .toList();
                logger.info("Map product DTOs to Entities list: {}, request updated = {}", products, request.isUpdated());

                productRepository.deleteAll(productsInDB);
                logger.info("Removed existed products in DB to request with id = {}", requestId);
                productRepository.saveAll(products);
                logger.info("Saved to DB updated products to request with id = {}", requestId);
                requestRepository.save(request);
                logger.info("Updated request with id = {}", requestId);

            } else {
                logger.error("Couldn't find request on which got parsing update response, requestId = {}", requestId);
            }
        } else {
            logger.error("After update request parsing didn't find anything, request id = {}", requestId);
        }
    }

    private Request getRequestByChatIdAndNumber(Long chatId, Integer requestNumber) {
        User user = userService.getUserByChatId(chatId);
        logger.info("Getting user's request with number {}, user: {}", requestNumber, user);
        Optional<Request> requestOptional = requestRepository.findByUserIdAndRequestNumber(user.getId(), requestNumber);
        if (requestOptional.isPresent()) {
            return requestOptional.get();
        } else {
            logger.error("Request with number {} not found, user {}", requestNumber, user);
            String message = "Не удалось получить информацию по поисковому запросу с номером " + requestNumber;
            ExceptionResponseEvent exceptionResponseEvent = new ExceptionResponseEvent(chatId, message);
            sendToUserKafkaTopic(chatId, exceptionResponseEvent);
            return null;
        }
    }

    private void sendToUserKafkaTopic(Long key, Object value) {
        sendToKafkaTopic(key, value, userResponseTopicName);
    }

    private void sendToParseKafkaTopic(Long key, Object value) {
        sendToKafkaTopic(key, value, parseRequestTopicName);
    }

    private void sendToKafkaTopic(Long key, Object value, String topicName) {
        CompletableFuture<SendResult<Long, Object>> future =
                kafkaTemplate.send(topicName, key, value);
        future.whenComplete(((stringSendResult, throwable) -> {
            if (throwable != null) {
                logger.error("Failed to send message: {}", throwable.getLocalizedMessage(), throwable);
            } else {
                logger.info("Message sent successfully {}", stringSendResult);
            }
        }));
    }
}
