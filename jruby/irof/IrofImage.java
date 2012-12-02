import java.util.Arrays;

public class IrofImage{
    private static final String IROF_ICON = "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";
    
    public static byte[] decodeBase64() {
        char[] cs = IROF_ICON.toCharArray();
        int q = cs.length / 4;
        byte[] bs = new byte[q * 3];
        int index = 0;
        for (int i = 0; i < q; i++) {
            int a =
                indexOf(cs[i * 4]) << 18
		| indexOf(cs[i * 4 + 1]) << 12
		| indexOf(cs[i * 4 + 2]) << 6
		| indexOf(cs[i * 4 + 3]);
            bs[index++] = (byte) (a >> 16);
            bs[index++] = (byte) (a >> 8);
            bs[index++] = (byte) a;
        }
        while (bs[--index] == 0) {
        }
        return Arrays.copyOf(bs, index + 1);
    }

    private static int indexOf(char c) {
        if (c == '+') {
            return 62;
        } else if (c == '/') {
            return 63;
        } else if ('A' <= c && c <= 'Z') {
            return c - 65;
        } else if ('a' <= c && c <= 'z') {
            return c - 71;
        } else if ('0' <= c && c <= '9') {
            return c + 4;
        }
        return 0;
    }

}
