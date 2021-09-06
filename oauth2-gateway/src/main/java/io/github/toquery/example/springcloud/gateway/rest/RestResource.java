package io.github.toquery.example.springcloud.gateway.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RestResource {

//    @Autowired
//    private HttpServletRequest request;

//    @Autowired
//    private HttpServletResponse response;


    @RequestMapping("/api/users/me")
    public ResponseEntity<Map<String, Object>> profile() {
        Map<String, Object> map = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //map.put("httpServlets", getHttpServlets());
        map.put("authentication", authentication);
        return ResponseEntity.ok(map);
    }


   /* private Map<String, String> getRequestHeaders() {
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

    private Map<String, Object> getHttpServlets() {
        return this.getHttpServlets(null);
    }

    */

}
