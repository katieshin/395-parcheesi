package bugzapper;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME) @Target(METHOD)
public @interface Postcondition {
	String value();
}
