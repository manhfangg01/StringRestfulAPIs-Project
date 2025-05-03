package vn.hoidanit.jobhunter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import vn.hoidanit.jobhunter.domain.response.RestResponse;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true; // xử lý cho mọi controller method
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        // Nếu trả về String → phải trả chuỗi JSON chứ không return object
        if (body instanceof String) {
            RestResponse<Object> wrapped = new RestResponse<>();
            wrapped.setStatusCode(status);
            wrapped.setData(body);
            wrapped.setMessage(getMessage(returnType, status));
            try {
                return mapper.writeValueAsString(wrapped);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error serializing response", e);
            }
        }

        // Trả về object JSON như bình thường
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(status);
        res.setData(body);
        res.setMessage(getMessage(returnType, status));

        return res;
    }

    private String getMessage(MethodParameter returnType, int status) {
        if (status >= 400) {
            return "CALL API FAILED";
        }

        ApiMessage annotation = returnType.getMethodAnnotation(ApiMessage.class);
        return (annotation != null) ? annotation.value() : "CALL API SUCCESS";
    }
}
