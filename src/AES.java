import java.io.*;

import static java.lang.System.*;

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
            err.println("\tUsage: AES option keyFile inputFile");
            err.println("\t\toption     [e]ncryption or [d]ecryption");
            err.println("\t\tkeyFile    file containing 256 bit key (64 hex characters, one line)");
            err.println("\t\tinputFile  plain/cipher text (32 hex characters per line)\n");
            exit(1);
        } else if (args[0].length() != 1) {
            err.println("\tUsage: AES option keyFile inputFile");
            err.println("\t\toption     [e]ncryption or [d]ecryption");
            exit(1);
        } else if (args[0].charAt(0) != 'e' && args[0].charAt(0) != 'd') {
            err.println("\tUsage: AES option keyFile inputFile");
            err.println("\t\toption     [e]ncryption or [d]ecryption");
            exit(1);
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
                out.println("Invalid Key Length");
                break;
        }
        out.println("\nKEY LENGTH: " + Nk);
        out.println("ROUNDS    : " + Nr);
        AES.key = key;
        schedule = new byte[Nb * (Nr + 1)][4];
        keyExpansion(_mode);
    }

    private void keyExpansion(String _mode){
        int i = 0;
        while( i < Nk){
            schedule[i][0] = key[4*i];
            schedule[i][1] = key[4*i+1];
            schedule[i][2] = key[4*i+2];
            schedule[i][3] = key[4*i+3];
            i++;
        }

       /* for(i=0; i<schedule.length; i++){
            out.println();
            for(int j=0; j<4; j++)
                System.out.print(String.format("0x%02X", schedule[i][j]) + " ");
        }*/
    }

    private byte[] encrypt(byte[] input) {
        this.state = new byte[4][Nb];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                this.state[j][i] = input[4 * i + j];
            }
        }
        printState("Initial State");
        this.SubBytes();
        this.ShiftRows();
        this.MixColumns();
        //this.InvSubBytes();
        //this.InvShiftRows();
        this.InvMixColumns();
        return input;
    }

    public static void readInput() throws IOException {
        int k;
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
        out.println("\n" + id + ": ");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                out.print(String.format("0x%02X", state[i][j]) + " ");
            }
            out.printf("\n");
        }
    }

    public static void printInputs(String id, byte[] b, final int numberOfColumns) {
        long streamPtr = 0;
        out.println("\n" + id + ": ");

        if (id.equals("INPUT")) {
            for (byte aB : b) {
                final long col = streamPtr++ % numberOfColumns;
                out.print(String.format("0x%02X", aB) + " ");
                if (col == (numberOfColumns - 1)) {
                    out.printf("\n");
                }
            }
        } else if (id.equals("KEY")) {
            for (int k = 0; k < 4; k++) {
                for (int i = 0; i < b.length / 4; i++)
                    out.print(String.format("0x%02x ", b[i * 4 + k]));
                out.println();
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
            err.println("Decryption not supported yet.");
            // TODO: test.decrypt(input);
        }

    }


    private void SubBytes()
    {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                state[j][i] = utils.SBox.sub(state[j][i]);
        }
        printState("Output of SubBytes");
    }


    private void InvSubBytes(){
        for(int i =0; i<4; i++)
            for (int j = 0; j < 4; j++)
                state[j][i] = utils.SBox.invSub(state[j][i]);
        printState("Ouput of InvSubBytes");
    }

    private void ShiftRows(){
        byte temp;
        for(int i=0; i<4;i++)
            for (int k = 0; k < i; k++) {
                temp = state[i][0];
                for (int j = 0; j < 3; j++)
                    state[i][j] = state[i][j + 1];
                state[i][3] = temp;
            }
        printState("Ouput of ShiftRow");
    }

    private void InvShiftRows(){
        byte temp;
        for(int i=0; i<4; i++){
            for(int k = 0; k<i; k++)
            {
                temp = state[i][3];
                System.arraycopy(state[i], 0, state[i], 1, 3);
                state[i][0] = temp;
            }
        }
        printState("Output of InvShiftRows");
    }

    private void MixColumns(){

        //printState("Input to MixCol");
        byte[] tempCol;
        for(int j=0; j<4; j++){
            tempCol = new byte[]{state[0][j], state[1][j], state[2][j], state[3][j]};
            state[0][j] = (byte) (utils.gmul2(tempCol[0]) ^ utils.gmul3(tempCol[1]) ^ tempCol[2] ^ tempCol[3]);
            state[1][j] = (byte) (tempCol[0] ^ utils.gmul2(tempCol[1]) ^ utils.gmul3(tempCol[2]) ^ tempCol[3]);
            state[2][j] = (byte) (tempCol[0] ^ tempCol[1] ^ utils.gmul2(tempCol[2]) ^ utils.gmul3(tempCol[3]));
            state[3][j] = (byte) (utils.gmul3(tempCol[0]) ^ tempCol[1] ^ tempCol[2] ^ utils.gmul2(tempCol[3]));
        }
        printState("Ouput of MixCol");
    }

    private void InvMixColumns(){

        //printState("Input to InvMixColumns");
        byte[] tempCol;
        for(int j=0; j<4; j++){
            tempCol = new byte[]{state[0][j], state[1][j], state[2][j], state[3][j]};
            state[0][j] = (byte) (utils.gmul14(tempCol[0]) ^ utils.gmul11(tempCol[1]) ^ utils.gmul13(tempCol[2]) ^ utils.gmul9(tempCol[3]));
            state[1][j] = (byte) (utils.gmul9(tempCol[0]) ^ utils.gmul14(tempCol[1]) ^ utils.gmul11(tempCol[2]) ^ utils.gmul13(tempCol[3]));
            state[2][j] = (byte) (utils.gmul13(tempCol[0]) ^ utils.gmul9(tempCol[1]) ^ utils.gmul14(tempCol[2]) ^ utils.gmul11(tempCol[3]));
            state[3][j] = (byte) (utils.gmul11(tempCol[0]) ^ utils.gmul13(tempCol[1]) ^ utils.gmul9(tempCol[2]) ^ utils.gmul14(tempCol[3]));
        }
        printState("Output of InvMixColumns");
    }
}
