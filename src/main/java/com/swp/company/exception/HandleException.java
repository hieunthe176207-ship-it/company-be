package com.swp.company.exception;


import com.swp.company.dto.response.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class HandleException {
    @ExceptionHandler(value = {BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ResponseError> handleException(Exception e) {
        ResponseError responseError = new ResponseError();
        responseError.setMessage("Bạn nhập sai email hoặc mật khẩu");
        responseError.setError("UNAUTHORIZED");
        responseError.setCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ResponseError> handleException(NoResourceFoundException e) {
        ResponseError responseError = new ResponseError();
        responseError.setMessage(e.getMessage());
        responseError.setError("NOT_FOUND");
        responseError.setCode(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ResponseError> handleException(ApiException e) {
        ResponseError responseError = new ResponseError();
        responseError.setMessage(e.getMessage());
        responseError.setCode(e.getStatus());
        return ResponseEntity.status(e.getStatus()).body(responseError);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseError> handleExceptionCommon(Exception e) {
        ResponseError responseError = new ResponseError();
        responseError.setMessage(e.getMessage());
        responseError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(responseError);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<ResponseError> handleExceptionForbidden(Exception e) {
        ResponseError responseError = new ResponseError();
        responseError.setMessage("Không có quyền truy cập");
        responseError.setCode(HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(responseError);
    }
}
