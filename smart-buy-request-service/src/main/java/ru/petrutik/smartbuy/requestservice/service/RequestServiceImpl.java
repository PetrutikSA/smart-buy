package ru.petrutik.smartbuy.requestservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.dto.ProductDto;
import ru.petrutik.smartbuy.event.dto.RequestDto;
import ru.petrutik.smartbuy.event.response.ExceptionResponseEvent;
import ru.petrutik.smartbuy.event.response.ListAllResponseEvent;
import ru.petrutik.smartbuy.event.response.RemoveResponseEvent;
import ru.petrutik.smartbuy.event.response.ShowResponseEvent;
import ru.petrutik.smartbuy.requestservice.dto.mapper.ProductMapper;
import ru.petrutik.smartbuy.requestservice.dto.mapper.RequestMapper;
import ru.petrutik.smartbuy.requestservice.model.Product;
import ru.petrutik.smartbuy.requestservice.model.Request;
import ru.petrutik.smartbuy.requestservice.model.User;
import ru.petrutik.smartbuy.requestservice.repository.ProductRepository;
import ru.petrutik.smartbuy.requestservice.repository.RequestRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final ProductRepository productRepository;
    private final RequestMapper requestMapper;
    private final ProductMapper productMapper;
    private final KafkaTemplate<Long, Object> kafkaTemplate;
    private final String responseTopicName;
    private final Logger logger;

    public RequestServiceImpl(UserService userService,
                              RequestRepository requestRepository, ProductRepository productRepository,
                              RequestMapper requestMapper, ProductMapper productMapper,
                              KafkaTemplate<Long, Object> kafkaTemplate,
                              @Value("#{@kafkaConfig.getResponseTopicName()}") String responseTopicName) {
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.productRepository = productRepository;
        this.requestMapper = requestMapper;
        this.productMapper = productMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.responseTopicName = responseTopicName;
        this.logger = LoggerFactory.getLogger(RequestServiceImpl.class);
    }

    @Override
    public void addRequest(Long chatId, String searchQuery, Integer maxPrice) {
        User user = userService.getUserByChatId(chatId);
        Request request = new Request();
        request.setSearchQuery(searchQuery);
        request.setMaxPrice(BigDecimal.valueOf(maxPrice));
        request.setUser(user);
        request.setUpdated(false);
        logger.info("New request created: {}", request);
        List<Request> userRequests = requestRepository.findAllByUserId(user.getId());
        request.setRequestNumber(userRequests.size() + 1);
        requestRepository.save(request);
        logger.info("Request saved to DB: {}", request);
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
        sendToKafkaTopic(chatId, listAllResponseEvent);
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
            sendToKafkaTopic(chatId, showResponseEvent);
        }
    }

    @Override
    public void removeRequest(Long chatId, Integer requestNumber) {
        Request request = getRequestByChatIdAndNumber(chatId, requestNumber);
        if (request != null) {
            logger.info("Removing request {}", request);
            requestRepository.delete(request);
            RemoveResponseEvent removeResponseEvent = new RemoveResponseEvent(chatId, requestNumber);
            sendToKafkaTopic(chatId, removeResponseEvent);
        }
    }

    @Override
    public void deleteAllRequest() {

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
            sendToKafkaTopic(chatId, exceptionResponseEvent);
            return null;
        }
    }

    private void sendToKafkaTopic(Long key, Object value) {
        CompletableFuture<SendResult<Long, Object>> future =
                kafkaTemplate.send(responseTopicName, key, value);
        future.whenComplete(((stringSendResult, throwable) -> {
            if (throwable != null) {
                logger.error("Failed to send message: {}", throwable.getLocalizedMessage(), throwable);
            } else {
                logger.info("Message sent successfully {}", stringSendResult);
            }
        }));
    }
}
