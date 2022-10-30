package gt.mail.utils;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.function.Function;

public class EmailUtil {

    public static Function<String, InternetAddress> toInternetAddr() {
        return it -> {
            try {
                return new InternetAddress(it);
            } catch (AddressException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static InternetAddress[] toInetArray(Collection<String> tos) {
        if (tos == null) {
            return new InternetAddress[0];
        }
        return tos.stream().map(EmailUtil.toInternetAddr()).toArray(InternetAddress[]::new);
    }

}
