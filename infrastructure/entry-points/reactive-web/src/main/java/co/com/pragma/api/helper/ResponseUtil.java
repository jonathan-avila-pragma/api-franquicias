package co.com.pragma.api.helper;

import co.com.pragma.api.dto.ResponseDto;
import lombok.experimental.UtilityClass;

import static co.com.pragma.api.helper.Constants.ERROR_TITLE;
import static co.com.pragma.api.helper.Constants.SUCCESSFULLY_TITLE;

@UtilityClass
public class ResponseUtil {
    public static <T> ResponseDto<T> responseSuccessful(T data, BusinessCode businessCode) {
        return ResponseDto.<T>builder()
                .code(businessCode.getCode())
                .title(SUCCESSFULLY_TITLE)
                .message(businessCode.getLog())
                .data(data)
                .build();
    }
    
    public static <T> ResponseDto<T> responseError(BusinessCode businessCode) {
        return ResponseDto.<T>builder()
                .code(businessCode.getCode())
                .title(ERROR_TITLE)
                .message(businessCode.getLog())
                .data(null)
                .build();
    }
    
    public static ResponseDto<Object> responseError(BusinessCode businessCode, String errorMessage) {
        return ResponseDto.builder()
                .code(businessCode.getCode())
                .title(ERROR_TITLE)
                .message(businessCode.getLog())
                .errors(java.util.List.of(errorMessage))
                .build();
    }
}
