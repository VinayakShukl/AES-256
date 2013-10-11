import java.io.*;

public class AES {

    private static String _mode;
    private static FileInputStream _keyFile;
    private static FileInputStream _inputFile;

    private final int Nb = 4;
    private final int wordSize = 4;
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
        //TODO: keyExpansion(_mode);
    }

    public byte[] encrypt(byte[] input) {
        this.state = new byte[4][Nb];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                this.state[j][i] = input[4 * i + j];
            }
        }
        printState("INITIAL STATE");
        this.SubByte();
        this.ShiftRows();
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


    private void MixColumns(){

        for(int j= 0; j<1; j++)
        {               state[0][j]=(byte) 0xd4 ;
            state[1][j] = (byte) 0xbf;
            state[2][j] = (byte) 0x5d;
            state[3][j] = (byte) 0x30;

            printState("BLAH");
            state[0][j] = (byte) (utils.gmul2(state[0][j]) ^ utils.gmul3(state[1][j]) ^ state[2][j] ^ state[3][j]);
            //state[1][j] = ;
            //state[2][j] = ;
            //state[3][j] = ;
            printState("STATE AFTER 1 MIXCOL");

        }
    }
}
