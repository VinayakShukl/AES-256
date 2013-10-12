import java.io.*;

public class AES {

    private static String _mode;
    private static FileInputStream _keyFile;
    private static FileInputStream _inputFile;

    private final int Nb = 4;
    private final int wordSize = 4; /* 1 word := 8 bytes */
    private int Nk;
    private int Nr;
    private byte[][] schedule;
    private byte[][] state;
    private byte[][] rcon;
    private static byte[] input;
    private static byte[] key;

    public static void readArgs(String[] args) {

        if (args.length < 3) {
            System.err.println("\tUsage: AES option keyFile inputFile");
            System.err.println("\t\toption     [e]ncryption or [d]ecryption");
            System.err.println("\t\tkeyFile    file containing 256 bit key (64 hex characters, one line)");
            System.err.println("\t\tinputFile  plain/cipher text (32 hex characters per line)\n");
            System.exit(1);
        } else if (args[0].length() != 1) {
            System.err.println("\tUsage: AES option keyFile inputFile");
            System.err.println("\t\toption     [e]ncryption or [d]ecryption");
            System.exit(1);
        } else if (args[0].charAt(0) != 'e' && args[0].charAt(0) != 'd') {
            System.err.println("\tUsage: AES option keyFile inputFile");
            System.err.println("\t\toption     [e]ncryption or [d]ecryption");
            System.exit(1);
        }

        _mode = args[0];
        try {
            _keyFile = new FileInputStream(args[1]);
            _inputFile = new FileInputStream(args[2]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public AES(byte[] key) {
        //TODO: rcon();
        switch (key.length) {
            case 16:
                Nk = 4;
                Nr = 10;
                break;
            case 24:
                Nk = 6;
                Nr = 12;
                break;
            case 32:
                Nk = 8;
                Nr = 14;
                break;
            default:
                System.out.println("Invalid Key Length");
                break;
        }
        System.out.println("\nKEY LENGTH: " + Nk);
        System.out.println("ROUNDS    : " + Nr);
        this.key = key;
        schedule = new byte[Nb * (Nr + 1)][4];
        keyExpansion(_mode);
    }

    private void keyExpansion(String _mode){
        int i = 0, temp;
        byte b;
        while( i < Nk){
            schedule[i][0] = key[4*i];
            schedule[i][1] = key[4*i+1];
            schedule[i][2] = key[4*i+2];
            schedule[i][3] = key[4*i+3];
            i++;
        }

        for(i=0; i<schedule.length; i++){
            System.out.println();
            for(int j=0; j<4; j++)
                System.out.print(String.format("0x%02X", schedule[i][j]) + " ");
        }
    }

    private byte[] encrypt(byte[] input) {
        this.state = new byte[4][Nb];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                this.state[j][i] = input[4 * i + j];
            }
        }
        printState("INITIAL STATE");
        //this.SubByte();
        //this.ShiftRows();
        //this.InvShiftRows();
        this.InvMixColumns();
        this.MixColumns();
        //this.InvSubBytes();
        return input;
    }

    public static void readInput() throws IOException {
        int k = 0;
        StringBuilder s = new StringBuilder();
        StringBuilder s2 = new StringBuilder();

        while ((k = _keyFile.read()) != -1) {
            s.append((char) k);
        }
        key = utils.hexToByte(s.toString());

        while ((k = _inputFile.read()) != -1) {
            if ((char) k == '\n' || (char) k == '\r') {
                continue;
            }
            s2.append((char) k);
        }
        input = utils.hexToByte(s2.toString());
    }

    public void printState(String id) {
        long streamPtr = 0;
        System.out.println("\n" + id + ": ");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                System.out.print(String.format("0x%02X", state[i][j]) + " ");
            }
            System.out.printf("\n");
        }
    }

    public static void printInputs(String id, byte[] b, final int numberOfColumns) {
        long streamPtr = 0;
        System.out.println("\n" + id + ": ");

        if (id.equals("INPUT")) {
            for (int k = 0; k < b.length; k++) {
                final long col = streamPtr++ % numberOfColumns;
                System.out.print(String.format("0x%02X", b[k]) + " ");
                if (col == (numberOfColumns - 1)) {
                    System.out.printf("\n");
                }
            }
        } else if (id.equals("KEY")) {
            for (int k = 0; k < 4; k++) {
                for (int i = 0; i < b.length / 4; i++)
                    System.out.print(String.format("0x%02x ", b[i * 4 + k]));
                System.out.println();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        readArgs(args);
        readInput();
        printInputs("KEY", key, 8);
        printInputs("INPUT", input, 16);
        AES test = new AES(key);
        if (_mode.charAt(0) == 'e')
            test.encrypt(input);

        else {
            System.err.println("Decryption not supported yet.");
            // TODO: test.decrypt(input);
        }

    }


    private void SubByte()         //The current state is received and the state after the operation is returned.
    {
        byte temp;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                state[j][i] = utils.SBox.sub(state[j][i]);
        }
        printState("SBOX 1 STATE");
    }


    private void InvSubBytes(){
        for(int i =0; i<4; i++){
            for(int j =0; j< 4; j++){
                state[j][i] = utils.SBox.invSub(state[j][i]);
            }
        }
        printState("SBOX 1 STATE AFTER INVERSE");
    }

    private void ShiftRows(){
        byte temp;
        for(int i=0; i<4;i++){
            for(int k =0; k<i; k++) {
                temp = state[i][0];
                for(int j =0; j<3 ; j++){

                    state[i][j] = state[i][j+1];

                }
                state[i][3] = temp;
            }
        }
        printState("SBOX 1 STATE AFTER SHIFTROW()");
    }

    private void InvShiftRows(){
        byte temp;
        for(int i=0; i<4; i++){
            for(int k = 0; k<i; k++)
            {
                temp = state[i][3];
                for(int j = 3; j>0; j--)
                {
                    state[i][j] = state[i][j-1];
                }
                state[i][0] = temp;
            }
        }
        printState("STATE AFTER InvShiftRows");

    }


    private void MixColumns(){

        state[0][0] = (byte) 0xd4;
        state[1][0] = (byte) 0xbf;
        state[2][0] = (byte) 0x5d;
        state[3][0] = (byte) 0x30;

        state[0][1] = (byte) 0xe0;
        state[1][1] = (byte) 0xb4;
        state[2][1] = (byte) 0x52;
        state[3][1] = (byte) 0xae;

        state[0][2] = (byte) 0xb8;
        state[1][2] = (byte) 0x41;
        state[2][2] = (byte) 0x11;
        state[3][2] = (byte) 0xf1;

        state[0][3] = (byte) 0x1e;
        state[1][3] = (byte) 0x27;
        state[2][3] = (byte) 0x98;
        state[3][3] = (byte) 0xe5;

        printState("INPUT TO MIXCOL");
        for(int j= 0; j<4; j++)
        {   /*
            System.out.print(String.format("0x%02X", state[0][j]) + " \n");
            System.out.print(String.format("0x%02X", utils.gmul2(state[1][j])) + " \n");
            System.out.print(String.format("0x%02X", utils.gmul3(state[2][j])) + " \n");
            System.out.print(String.format("0x%02X", state[3][j]) + " \n");
            System.out.println(utils.gmul2(state[1][j]));
            System.out.println(utils.gmul3(state[2][j]));
            System.out.println(state[0][j]);
            System.out.println(state[3][j]);  */
            //printState("BLAH");
            // The variables p,q,r and s act as the buffer and store the values of each column
            byte p = state[0][j];
            byte q = state[1][j];
            byte r = state[2][j];
            byte s = state[3][j];


            state[0][j] = (byte) (utils.gmul2(p) ^ utils.gmul3(q) ^ r ^ s);
            state[1][j] = (byte) (p ^ (byte)utils.gmul2(q) ^ (byte)utils.gmul3(r) ^ s);
            state[2][j] = (byte) (p ^ q ^ utils.gmul2(r) ^ utils.gmul3(s));
            state[3][j] = (byte) (utils.gmul3(p) ^ q ^ r ^ utils.gmul2(s));

        }
        printState("AFTER MIXCOLUMNS :");
    }

    private void InvMixColumns(){

        state[0][0] = (byte) 0x04;
        state[1][0] = (byte) 0x66;
        state[2][0] = (byte) 0x81;
        state[3][0] = (byte) 0xe5;

        state[0][1] = (byte) 0xe0;
        state[1][1] = (byte) 0xcb;
        state[2][1] = (byte) 0x19;
        state[3][1] = (byte) 0x9a;

        state[0][2] = (byte) 0x48;
        state[1][2] = (byte) 0xf8;
        state[2][2] = (byte) 0xd3;
        state[3][2] = (byte) 0x7a;

        state[0][3] = (byte) 0x28;
        state[1][3] = (byte) 0x06;
        state[2][3] = (byte) 0x26;
        state[3][3] = (byte) 0x4c;

        printState("Input to InvMixColumns");


        for(int j = 0; j<4 ; j++)
        {
            byte p = state[0][j];
            byte q = state[1][j];
            byte r = state[2][j];
            byte s = state[3][j];

            state[0][j] = (byte) (utils.gmul14(p) ^ utils.gmul11(q) ^ utils.gmul3(r) ^ utils.gmul9(s));
            state[1][j] = (byte) (utils.gmul9(p) ^  utils.gmul14(q) ^ utils.gmul11(r) ^ utils.gmul13(s));
            state[0][j] = (byte) (utils.gmul13(p) ^ utils.gmul9(q) ^ utils.gmul14(r) ^ utils.gmul11(s));
            state[0][j] = (byte) (utils.gmul11(p) ^ utils.gmul13(q) ^ utils.gmul9(r) ^ utils.gmul14(s));

        }

        printState("Output of InvMixColumns");
    }
}
