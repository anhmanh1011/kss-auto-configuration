package com.kss.autoconfigure.ex;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.kss.autoconfigure.common.EnumCodeResponse;
import com.kss.autoconfigure.common.ErrorResponse;
import com.kss.autoconfigure.common.ResponseData;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
public class RestGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(RestGlobalExceptionHandler.class);

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        String[] denylist = new String[]{"class.*", "Class.*", "*.class.*", "*.Class.*"};
        dataBinder.setDisallowedFields(denylist);
    }

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<ResponseData<ApiException>> customHandleApiException(
            ApiException ex) {
        logger.error(ex.errorCode + ": " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(new ResponseData<ApiException>().traceId(TraceContext.traceId()).error(ex), HttpStatus.OK);
    }

    @Override
    protected ResponseEntity handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

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


        ApiException apiException = new ApiException(EnumCodeResponse.INVALID_PARAM.getCode(), msg);
        ex.printStackTrace();
        return new ResponseEntity<ResponseData>(new ResponseData<ApiException>().error(apiException), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestUnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity handlerUnauthorizedException(
            RestUnauthorizedException error) {
        return new ResponseEntity<>(new ResponseData<ApiException>().error(error), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseData> customHandleException(
            Exception ex) {
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return new ResponseEntity<ResponseData>(new ResponseData<ApiException>().error(new ApiException(EnumCodeResponse.INTERNAL_SERVER)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse error = null;
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error = new ErrorResponse(EnumCodeResponse.INVALID_PARAM.getCode(), fieldError.getField() + " " + fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(new ResponseData<>().error(error.getCode(), error.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
