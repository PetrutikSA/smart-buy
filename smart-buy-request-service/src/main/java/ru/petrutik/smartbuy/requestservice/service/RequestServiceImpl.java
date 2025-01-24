package ru.petrutik.smartbuy.requestservice.service;

import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.requestservice.model.Request;
import ru.petrutik.smartbuy.requestservice.model.User;
import ru.petrutik.smartbuy.requestservice.repository.RequestRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final RequestRepository requestRepository;

    public RequestServiceImpl(UserService userService, RequestRepository requestRepository) {
        this.userService = userService;
        this.requestRepository = requestRepository;
    }

    @Override
    public void addRequest(Long chatId, String searchQuery, Integer maxPrice) {
        User user = userService.getUserByChatId(chatId);
        Request request = new Request();
        request.setSearchQuery(searchQuery);
        request.setMaxPrice(BigDecimal.valueOf(maxPrice));
        request.setUpdated(false);
        List<Request> userRequests = user.getRequests();
        userRequests.add(request);
        request.setRequestNumber(userRequests.size());
        requestRepository.save(request);
        userService.updateUser(user);
    }

    @Override
    public void getAllRequests() {

    }

    @Override
    public void getRequest() {

    }

    @Override
    public void deleteRequest() {

    }

    @Override
    public void deleteAllRequest() {

    }
}
