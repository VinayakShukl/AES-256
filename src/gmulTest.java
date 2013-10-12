public class gmulTest {

    private static byte gmul2(byte a) {
        byte hi = (byte) (a & 0x80);
        a <<= 1;
        if ((hi == (byte) 0x80))
            a ^= 0x1b;
        return a;
    }

    private static byte gmul3(byte a) {

        byte b = a;
        a = gmul2(a);
        b ^= a;
        return b;
    }

    public static byte gmul4(byte a) {
        byte b = gmul2(a);
        return gmul2(b);
    }

    public static byte gmul8(byte a) {
        byte b = gmul2(a);
        byte c = gmul2(b);
        return gmul2(c);
    }

    public static byte gmul9(byte a) {
        byte b;
        b = (byte) (gmul8(a) ^ a);
        return b;
    }

    public static byte gmul11(byte a) {
        byte b;
        b = (byte) (gmul8(a) ^ gmul2(a) ^ a);
        return b;
    }

    public static byte gmul13(byte a) {
        byte b;
        b = (byte) (gmul8(a) ^ gmul4(a) ^ a);
        return b;
    }

    public static byte gmul14(byte a) {
        byte b;
        b = (byte) (gmul8(a) ^ gmul4(a) ^ gmul2(a));
        return b;
    }

    public static void main(String[] args) {

        byte[][] state = {
                            {(byte)0xd4, (byte)0xe0 , (byte)0xb8, 0x1e},
                            {(byte)0xbf, (byte)0xb4, 0x41, 0x27},
                            {0x5d, 0x52, 0x11, (byte)0x98},
                            {0x30, (byte)0xae, (byte)0xf1, (byte)0xe5}
                        };
        byte[] tempCol;

        System.out.println("\nInput: ");
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++)
                System.out.print(String.format("0x%02X", state[i][j]) + " ");
            System.out.println();
        }

        for(int j=0; j<4; j++){
                tempCol = new byte[]{state[0][j], state[1][j], state[2][j], state[3][j]};
                state[0][j] = (byte) (gmul2(tempCol[0]) ^ gmul3(tempCol[1]) ^ tempCol[2] ^ tempCol[3]);
                state[1][j] = (byte) (tempCol[0] ^ gmul2(tempCol[1]) ^ gmul3(tempCol[2]) ^ tempCol[3]);
                state[2][j] = (byte) (tempCol[0] ^ tempCol[1] ^ gmul2(tempCol[2]) ^ gmul3(tempCol[3]));
                state[3][j] = (byte) (gmul3(tempCol[0]) ^ tempCol[1] ^ tempCol[2] ^ gmul2(tempCol[3]));
        }

        System.out.println("\nMixColumns: ");
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++)
                System.out.print(String.format("0x%02X", state[i][j]) + " ");
            System.out.println();
        }

        for(int j=0; j<4; j++){
            tempCol = new byte[]{state[0][j], state[1][j], state[2][j], state[3][j]};
            state[0][j] = (byte) (gmul14(tempCol[0]) ^ gmul11(tempCol[1]) ^ gmul13(tempCol[2]) ^ gmul9(tempCol[3]));
            state[1][j] = (byte) (gmul9(tempCol[0]) ^ gmul14(tempCol[1]) ^ gmul11(tempCol[2]) ^ gmul13(tempCol[3]));
            state[2][j] = (byte) (gmul13(tempCol[0]) ^ gmul9(tempCol[1]) ^ gmul14(tempCol[2]) ^ gmul11(tempCol[3]));
            state[3][j] = (byte) (gmul11(tempCol[0]) ^ gmul13(tempCol[1]) ^ gmul9(tempCol[2]) ^ gmul14(tempCol[3]));
        }

        System.out.println("\nInvMixColumns: ");
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++)
                System.out.print(String.format("0x%02X", state[i][j]) + " ");
            System.out.println();
        }
    }
}
