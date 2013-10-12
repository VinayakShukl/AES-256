public class gmulTest {

    private static byte gmul2(byte a) {
        byte hi = (byte) (a & 0x80);
        a <<= 1;
        if ((hi == (byte) 0x80))
            a ^= 0x1b;
        return a;
    }

    public static byte gmul4(byte a) {
        byte b = gmul2(a);
        byte c = gmul2(b);
        return c;
    }

    public static byte gmul8(byte a) {
        byte b = gmul2(a);
        byte c = gmul2(b);
        byte d = gmul2(c);
        return d;
    }

    public static byte gmul9(byte a) {
        byte b = a;
        b = (byte) (gmul8(a) ^ a);
        return b;
    }

    public static byte gmul11(byte a) {
        byte b = a;
        b = (byte) (gmul8(a) ^ gmul2(a) ^ a);
        return b;
    }

    public static byte gmul13(byte a) {
        byte b = a;
        b = (byte) (gmul8(a) ^ gmul4(a) ^ a);
        return b;
    }

    public static byte gmul14(byte a) {
        byte b = a;
        b = (byte) (gmul8(a) ^ gmul4(a) ^ gmul2(a));
        return b;
    }
     /*
         mul14 = mul8(a) ^ mul4(a) ^ mul2

         mul13 = mul8(a) ^ mul4(a) ^ a

         mul9 =  mul8(a) ^ a

         mul11 = mul8(a) ^ mul2(a) ^ a

         mul8 = 3 times mul2;

         mul4 = 2 times mul2;

     */
    public static byte gmul11ori(byte a) {

        byte b = a;
        byte c = a;
        byte hi = (byte) (a & 0x80);

        a <<= 1;
        if ((hi == (byte) 0x80)) {
            System.out.print(String.format("0x%02X", hi) + " ");
            a ^= 0x1b;
        }
        hi = (byte) (a & 0x80);
        a <<= 1;
        if ((hi == (byte) 0x80)) {
            System.out.print(String.format("0x%02X", hi) + " ");
            a ^= 0x1b;
        }
        hi = (byte) (a & 0x80);
        a <<= 1;
        if ((hi == (byte) 0x80)) {
            System.out.print(String.format("0x%02X", hi) + " ");
            a ^= 0x1b;
        }
        b = (byte) (a ^ gmul3(b));
        return b;
    }

    private static byte gmul3(byte a) {

        byte b = a;
        a = gmul2(a);
        b ^= a;
        return b;
    }

    public static void main(String[] args) {
        byte[][] state = new byte[4][4];
        byte[][] state2 = new byte[4][4];

        state[0][0] = (byte) 0xd4;
        state[1][0] = (byte) 0xbf;
        state[2][0] = (byte) 0x5d;
        state[3][0] = (byte) 0x30;

        System.out.println("INPUT : ");
        System.out.println(String.format("0x%02X", state[0][0]));
        System.out.println(String.format("0x%02X", state[1][0]));
        System.out.println(String.format("0x%02X", state[2][0]));
        System.out.println(String.format("0x%02X", state[3][0]));;

        state2[0][0] = (byte) (gmul2(state[0][0]) ^ gmul3(state[1][0]) ^ state[2][0] ^ state[3][0]);
        state2[1][0] = (byte) (state[0][0] ^ gmul2(state[1][0]) ^ gmul3(state[2][0]) ^ state[3][0]);
        state2[2][0] = (byte) (state[0][0] ^ state[1][0] ^ gmul2(state[2][0]) ^ gmul3(state[3][0]));
        state2[3][0] = (byte) (gmul3(state[0][0]) ^ state[1][0] ^ state[2][0] ^ gmul2(state[3][0]));

        System.out.println("ANSWER : ");
        System.out.println(String.format("0x%02X", state2[0][0]));
        System.out.println(String.format("0x%02X", state2[1][0]));
        System.out.println(String.format("0x%02X", state2[2][0]));
        System.out.println(String.format("0x%02X", state2[3][0]));
    }
}
