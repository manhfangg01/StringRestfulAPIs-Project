package vn.hoidanit.jobhunter.util.error;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.response.RestResponse;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleIdException(IdInvalidException idException) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(idException.getMessage());
        res.setMessage("IdInvalidException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = EmailExisted.class)
    public ResponseEntity<RestResponse<Object>> handleIdException(EmailExisted e) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(e.getMessage());
        res.setMessage("Email has already existed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<RestResponse<Object>> handleIdException(BusinessException e) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(e.getMessage());
        res.setMessage("Business-related error occurs");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = ObjectNotExisted.class)
    public ResponseEntity<RestResponse<Object>> handleIdException(ObjectNotExisted e) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(e.getMessage());
        res.setMessage("Not existed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());

        // Custom error message (optional, hoặc bạn có thể để null)
        List<String> errors = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        if (errors.size() > 1) {
            res.setMessage(errors);
        } else {
            res.setMessage(errors.get(0));
        }

        res.setError("Validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // handle exception not found (nếu url api không tồn tại)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNoHandlerFoundException(
            NoHandlerFoundException ex) {

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("Endpoint not found");
        res.setMessage("API endpoint does not exist");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNoHandlerFoundException(
            NoResourceFoundException ex) {

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("Endpoint not found");
        res.setMessage("API web URL not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    // handle exception filter (nếu truyền không đúng thông tin của filter)
    @ExceptionHandler({
            NullPointerException.class, // Khi filter null
            IllegalArgumentException.class, // Khi cú pháp filter sai
            IllegalStateException.class // Khi trạng thái filter không hợp lệ
    })
    public ResponseEntity<RestResponse<Object>> handleFilterException(
            Exception ex,
            WebRequest request) {

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Invalid filter parameter");

        // Message chi tiết hơn
        String errorMsg = ex.getMessage() != null
                ? "Filter error: " + ex.getMessage()
                : "Invalid filter syntax";
        res.setMessage(errorMsg);

        // Có thể thêm path nếu cần
        res.setData(Map.of(
                "path", request.getDescription(false).replace("uri=", "")));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

}
