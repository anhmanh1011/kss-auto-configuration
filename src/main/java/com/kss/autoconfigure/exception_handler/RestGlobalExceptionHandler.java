package com.kss.autoconfigure.exception_handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.kss.autoconfigure.common.EnumCodeCommonResponse;
import com.kss.autoconfigure.common.IResponseData;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Log4j2
public class RestGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        String[] denylist = new String[]{"class.*", "Class.*", "*.class.*", "*.Class.*"};
        dataBinder.setDisallowedFields(denylist);
    }

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<IResponseData> customHandleApiException(
            ApiException ex) {
        log.error(ex);
        ex.printStackTrace();
        return new ResponseEntity<>(ex, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex);

        String msg = null;
        Throwable cause = ex.getCause();

        if (cause instanceof JsonParseException) {
            JsonParseException jpe = (JsonParseException) cause;
            msg = jpe.getOriginalMessage();
        }

        // special case of JsonMappingException below, too much class detail in error messages
        else if (cause instanceof MismatchedInputException) {
            MismatchedInputException mie = (MismatchedInputException) cause;
            if (mie.getPath() != null && mie.getPath().size() > 0) {
                msg = "Invalid request field: " + mie.getPath().get(0).getFieldName();
            }

            // just in case, haven't seen this condition
            else {
                msg = "Invalid request message";
            }
        } else if (cause instanceof JsonMappingException) {
            JsonMappingException jme = (JsonMappingException) cause;
            msg = jme.getOriginalMessage();
            if (jme.getPath() != null && jme.getPath().size() > 0) {
                msg = "Invalid request field: " + jme.getPath().get(0).getFieldName() +
                        ": " + msg;
            }
        }


        ApiException apiException = new ApiException(EnumCodeCommonResponse.INVALID_PARAM.getCode(), msg);
        ex.printStackTrace();
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestUnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity handlerUnauthorizedException(
            RestUnauthorizedException error) {
        log.error(error);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity customHandleException(
            Exception ex) {
        log.error(ex);
        return new ResponseEntity<>(new ApiException(EnumCodeCommonResponse.INTERNAL_SERVER), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public   ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex);
        ApiException apiException = new ApiException(EnumCodeCommonResponse.INVALID_PARAM);
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            apiException = new ApiException(EnumCodeCommonResponse.INVALID_PARAM.getCode(), fieldError.getField() + " " + fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex);
        ApiException apiException = new ApiException(EnumCodeCommonResponse.INVALID_PARAM);
        if(ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex1 = (MethodArgumentNotValidException) ex;
            for (FieldError fieldError : ex1.getBindingResult().getFieldErrors()) {
                apiException = new ApiException(EnumCodeCommonResponse.INVALID_PARAM.getCode(), fieldError.getField() + " " + fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}
