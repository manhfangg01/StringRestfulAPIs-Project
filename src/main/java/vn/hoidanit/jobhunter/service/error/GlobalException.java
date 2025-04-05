package vn.hoidanit.jobhunter.service.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<String> handleBlogAlreadyExistsException(IdInvalidException idException) {
        // return ResponseEntity.status(HttpStatus.CONFLICT).body("id khong hop le");
        return ResponseEntity.badRequest().body(idException.getMessage());
    } // Nếu định nghĩa ở đây thì chỉ có phạm vi local thôi
}
