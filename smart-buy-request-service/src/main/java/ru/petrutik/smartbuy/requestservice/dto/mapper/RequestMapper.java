package ru.petrutik.smartbuy.requestservice.dto.mapper;

import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.dto.RequestDto;
import ru.petrutik.smartbuy.requestservice.model.Request;

@Component
public class RequestMapper {
    public RequestDto requestToRequestDto(Request request) {
        return new RequestDto(request.getRequestNumber(), request.getSearchQuery(), request.getMaxPrice());
    }
}
