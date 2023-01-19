package lowlang.parser;

import java.util.List;
import java.util.Arrays;

// just to help with string conversion
public class Join {
    public static String join(final String delimiter,
                              final int[] items) {
        final Integer[] converted = new Integer[items.length];
        for (int index = 0; index < items.length; index++) {
            converted[index] = Integer.valueOf(items[index]);
        }
        return join(delimiter, converted);
    }
    
    public static <A> String join(final String delimiter,
                                  final List<A> items) {
        final StringBuffer retval = new StringBuffer();
        boolean initial = true;
        for (final A a : items) {
            if (initial) {
                initial = false;
            } else {
                retval.append(delimiter);
            }
            retval.append(a.toString());
        }
        return retval.toString();
    }
        
    public static <A> String join(final String delimiter,
                                  final A[] items) {
        return join(delimiter, Arrays.asList(items));
    }
}
