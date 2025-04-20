package vn.hoidanit.jobhunter.util;

import org.hibernate.boot.model.source.spi.SizeSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.RestResponse;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true; // return true để xử lý Response(Điều kiện sử dụng)
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        RestResponse<Object> res = new RestResponse<Object>();
        if (status >= 200 && status <= 399) {
            // Case Success
            res.setMessage("Call Api Successfully");
            res.setData(body);
        } else {
            return body; // Vứt body(Object) ra cho GobalException xử lý
        }

        return res;
    }
}
// Object body: body of Response
// MethodParameter returnType: custom return Type of Controller
// MediaType selectedContentType: Content Types: JSON / XML
// Class<? extends HttpMessageConverter<?>> selectedConverterType: Choose a
// converter for JSON, which typically is Jackson
// ServerHttpRequest request: give access to the incoming HTTP request
// ServerHttpResponse response: Give access to HTTP response, add custom Header
// and change status code