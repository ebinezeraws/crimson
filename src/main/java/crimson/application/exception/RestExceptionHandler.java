package crimson.application.exception;

import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(UserExistenceException.class)
	public ResponseEntity<Map<String, String>> userValidationExceptionHandler(UserExistenceException ex) {
		return new ResponseEntity<Map<String, String>>(ex.getErrorMessages(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> userNotFoundException(UserNotFoundException ex) {
		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NO_CONTENT);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<String> invalidCredentails(InvalidCredentialsException ex) {
		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
	}

}
