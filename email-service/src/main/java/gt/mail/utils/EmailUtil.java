package gt.mail.utils;

import gt.mail.modules.email.EmailAddress;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EmailUtil {
    public static Function<EmailAddress, InternetAddress> fromEmailAddress() {
        return it -> {
            try {
                return new InternetAddress(it.getEmail(), it.getName());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Function<String, InternetAddress> toInternetAddr() {
        return it -> {
            try {
                return new InternetAddress(it);
            } catch (AddressException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Set<InternetAddress> toInternetAddr(Collection<String> to) {
        if (to == null) {
            return Set.of();
        }
        return to.stream().map(EmailUtil.toInternetAddr()).collect(Collectors.toSet());
    }

    public static Set<EmailAddress> toPlainStr(Collection<InternetAddress> to) {
        if (to == null) {
            return Set.of();
        }
        return to.stream().map(it -> EmailAddress.from(it.getAddress(), it.getPersonal())).collect(Collectors.toSet());
    }

    public static InternetAddress[] toInetArray(Collection<EmailAddress> tos) {
        if (tos == null) {
            return new InternetAddress[0];
        }
        return tos.stream().map(EmailUtil.fromEmailAddress()).toArray(InternetAddress[]::new);
    }

}
