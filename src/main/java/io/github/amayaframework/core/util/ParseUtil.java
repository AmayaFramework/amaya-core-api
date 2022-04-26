package io.github.amayaframework.core.util;

import io.github.amayaframework.core.filters.*;
import io.github.amayaframework.core.routes.HttpRoute;
import io.github.amayaframework.core.scanners.FilterScanner;
import io.github.amayaframework.core.wrapping.Content;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParseUtil {
    public static final String CONTENT_HEADER = "Content-Type";
    public static final String COOKIE_HEADER = "Cookie";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";
    public static final Map<String, StringFilter> STRING_FILTERS = getStringFilters();
    public static final Map<String, ContentFilter> CONTENT_FILTERS = getContentFilters();
    private static final Pattern ROUTE = Pattern.compile("(?:/[^\\s/]+)+");
    private static final String PARAM_DELIMITER = ":";
    private static final Pattern QUERY_VALIDATOR = Pattern.compile("^(?:[^&]+=[^&]+(?:&|$))+$");
    private static final Pattern QUERY = Pattern.compile("([^&]+)=([^&]+)");
    private static final String NEW_LINE = "<br>";
    private static final String SPACE = "&nbsp;";

    private static Map<String, StringFilter> getStringFilters() {
        FilterScanner<StringFilter> scanner = new FilterScanner<>(StringFilter.class);
        Map<String, StringFilter> ret = scanner.safetyFind();
        ret.put("bigint", new BigIntegerFilter());
        ret.put("bool", new BooleanFilter());
        ret.put("double", new DoubleFilter());
        ret.put("int", new IntegerFilter());
        return Collections.unmodifiableMap(ret);
    }

    private static Map<String, ContentFilter> getContentFilters() {
        FilterScanner<ContentFilter> scanner = new FilterScanner<>(ContentFilter.class);
        Map<String, ContentFilter> ret = scanner.safetyFind();
        ret.put(Content.PATH, new PathFilter());
        ret.put(Content.QUERY, new MapListFilter());
        ret.put(Content.COOKIE, new CookieFilter());
        return Collections.unmodifiableMap(ret);
    }

    public static void validateRoute(String route) {
        if (!route.isEmpty() && !ParseUtil.ROUTE.matcher(route).matches()) {
            throw new InvalidRouteFormatException(route);
        }
    }

    public static String normalizeRoute(String route) {
        if (route.equals("/")) {
            route = "";
        }
        if (route.endsWith("/")) {
            route = route.substring(0, route.length() - 1);
        }
        return route;
    }

    public static Variable<String, StringFilter> parseRouteParameter(String source) {
        String[] split = source.split(PARAM_DELIMITER);
        if (split.length < 1 || split.length > 2) {
            throw new InvalidFormatException("Invalid parameter \"" + source + "\"");
        }
        if (split.length == 1) {
            return new Variable<>(split[0], null);
        }
        return new Variable<>(split[0], STRING_FILTERS.get(split[1]));
    }

    public static Map<String, Object> extractRouteParameters(HttpRoute route, String source) {
        Map<String, Object> ret = new HashMap<>();
        if (!route.isRegexp()) {
            return ret;
        }
        Matcher finder = route.getPattern().matcher(source);
        Iterator<Variable<String, StringFilter>> parameters = route.getParameters().iterator();
        if (!finder.find()) {
            return null;
        }
        Variable<String, StringFilter> next;
        for (int i = 1; i <= finder.groupCount(); ++i) {
            next = parameters.next();
            if (next.getValue() != null) {
                ret.put(next.getKey(), next.getValue().transform(finder.group(i)));
            } else {
                ret.put(next.getKey(), finder.group(i));
            }
        }
        return ret;
    }

    public static Map<String, List<String>> parseQueryString(String source, Charset charset)
            throws UnsupportedEncodingException {
        Map<String, List<String>> ret = new HashMap<>();
        if (source == null || source.isEmpty()) {
            return ret;
        }
        if (!QUERY_VALIDATOR.matcher(source).matches()) {
            return ret;
        }
        Matcher matcher = QUERY.matcher(source);
        String charsetName = charset.name();
        while (matcher.find()) {
            String value = URLDecoder.decode(matcher.group(2), charsetName);
            ret.computeIfAbsent(matcher.group(1), key -> new ArrayList<>()).add(value);
        }
        return ret;
    }

    public static Map<String, Cookie> parseCookieHeader(String header) {
        String[] split = header.split("; ");
        Map<String, Cookie> ret = new HashMap<>();
        for (String rawCookie : split) {
            int delimIndex = rawCookie.indexOf('=');
            if (delimIndex < 0) {
                return ret;
            }
            String name = rawCookie.substring(0, delimIndex);
            String value = rawCookie.substring(delimIndex + 1);
            ret.put(name, new Cookie(name, value));
        }
        return ret;
    }

    public static Charset parseCharsetHeader(String header, Charset defaultCharset) {
        if (header == null) {
            return defaultCharset;
        }
        header = header.trim();
        if (!header.startsWith("charset")) {
            return defaultCharset;
        }
        int position = header.indexOf('=');
        if (position < 0) {
            return defaultCharset;
        }
        try {
            return Charset.forName(header.substring(position + 1));
        } catch (Exception e) {
            return defaultCharset;
        }
    }

    public static String escapeHtml(String string) {
        String ret = StringEscapeUtils.escapeHtml4(string);
        return ret.replace("\\n", NEW_LINE).replaceAll("\\s", SPACE);
    }
}
