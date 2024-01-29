package io.github.toquery.example.springcloud.gateway.resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
public class IndexRest {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headersMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            headersMap.put(name, request.getHeader(name));
        }
        return headersMap;
    }

    private Map<String, Object> getRequests() {
        Map<String, Object> requestsMap = new HashMap<>();
        requestsMap.put("headers", this.getRequestHeaders());
        requestsMap.put("uri", request.getRequestURI());
        requestsMap.put("url", request.getRequestURL().toString());
        requestsMap.put("method", request.getMethod());
        return requestsMap;
    }

    private Map<String, Object> getHttpServlets(Map<String, Object> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put("requests", this.getRequests());
        Map<String, String> responseMap = response.getHeaderNames().stream().collect(Collectors.toMap(item -> item, item -> response.getHeader(item)));
        map.put("response.headers", responseMap);
        return map;
    }

    @GetMapping
    public Map<String, Object> index() {
        Map<String, Object> map = this.getHttpServlets(null);
        return map;
    }

    @GetMapping("/test")
    public Map<String, Object> getIndex() {
        Map<String, Object> map = this.getHttpServlets(null);
        log.info("test");
        return map;
    }


}
