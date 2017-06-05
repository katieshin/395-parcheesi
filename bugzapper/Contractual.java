package bugzapper;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import javax.interceptor.InterceptorBinding;
import javax.enterprise.util.Nonbinding;

@InterceptorBinding
@Retention(RUNTIME) @Target({TYPE, METHOD})
public @interface Contractual { }
