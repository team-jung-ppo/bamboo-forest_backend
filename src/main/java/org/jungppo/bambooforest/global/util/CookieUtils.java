package org.jungppo.bambooforest.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtils {

    public static Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(name))
                        .findAny());
    }

    public static void addCookie(final HttpServletResponse response, final String name, final String value,
                                 final int maxAge) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(final HttpServletRequest request, final HttpServletResponse response,
                                    final String name) {
        getCookie(request, name).ifPresent(cookie -> {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        });
    }

    public static String serialize(final Object object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Base64.getUrlEncoder().encodeToString(baos.toByteArray());
        } catch (final IOException e) {
            throw new IllegalArgumentException("Failed to serialize object to cookie", e);
        }
    }

    public static <T> T deserialize(final Cookie cookie, final Class<T> cls) {
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(
                Base64.getUrlDecoder().decode(cookie.getValue()));
             final ObjectInputStream ois = new ObjectInputStream(bais)) {
            return cls.cast(ois.readObject());
        } catch (final IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to deserialize cookie to object", e);
        }
    }
}
