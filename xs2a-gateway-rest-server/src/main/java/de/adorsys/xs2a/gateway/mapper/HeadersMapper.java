package de.adorsys.xs2a.gateway.mapper;

import de.adorsys.xs2a.gateway.service.ResponseHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HeadersMapper {

    public HttpHeaders toHttpHeaders(ResponseHeaders responseHeaders) {
        HttpHeaders httpHeaders = new HttpHeaders();
        responseHeaders.getHeadersMap().forEach(httpHeaders::add);
        return httpHeaders;
    }
}
