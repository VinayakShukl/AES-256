import java.io.*;

import static java.lang.System.*;

public class AES {

    private static String _mode;
    private static FileInputStream _keyFile;
    private static FileInputStream _inputFile;

    private final int Nb = 4;
    private int Nk;
    private int Nr;
    private int[] schedule;
    private byte[][] state;
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
            err.println("\tFile does not exist. Please check the extension.\n\t\t" + e.getMessage());
            exit(1);
        }
    }

    public AES(byte[] key) {
        int keyBits = key.length * 8;
        if (keyBits != 128 && keyBits != 192 && keyBits != 256) {
            throw new RuntimeException("Invalid AES key size (" + keyBits + " bits)");
        }
        Nk = keyBits >>> 5;
        Nr = Nk + 6;
        out.println("\n\tNOTE: Keys of any length (128, 192, 256) are allowed! :D");
        out.println("\nKey Length: " + Nk + " words");
        out.println("Rounds    : " + Nr);
        AES.key = key;
        schedule = new int[Nb * (Nr + 1)];
        keyExpansion();
    }

    private int RotWord(int i) {
        return Integer.rotateLeft(i, 8);
    }

    private int SubWord(int i) {
        return ((utils.SBox.sub((i >>> 24))) << 24) |
                ((utils.SBox.sub((i >>> 16) & 0xff) & 0xff) << 16) |
                ((utils.SBox.sub((i >>> 8) & 0xff) & 0xff) << 8) |
                ((utils.SBox.sub((i) & 0xff) & 0xff));
    }

    private void keyExpansion() {

        int temp;
        int[] rcon = new int[11];
        for (int i = 0, k = 0; i < Nk; i++, k += 4) {
            schedule[i] =
                    ((key[k]) << 24) |
                            ((key[k + 1] & 0xff) << 16) |
                            ((key[k + 2] & 0xff) << 8) |
                            ((key[k + 3] & 0xff));
        }

        rcon[1] = (byte) 0x01 << 24;
        rcon[2] = (byte) 0x02 << 24;
        rcon[3] = (byte) 0x04 << 24;
        rcon[4] = (byte) 0x08 << 24;
        rcon[5] = (byte) 0x10 << 24;
        rcon[6] = (byte) 0x20 << 24;
        rcon[7] = (byte) 0x40 << 24;
        rcon[8] = (byte) 0x80 << 24;
        rcon[9] = (byte) 0x1b << 24;
        rcon[10] = (byte) 0x36 << 24;

        for (int i = Nk; i < Nb * (Nr + 1); i++) {
            temp = schedule[i - 1];
            if (i % Nk == 0) {
                temp = SubWord(RotWord(temp));
                temp ^= rcon[i / Nk];
            } else if (Nk > 6 && i % Nk == 4) {
                temp = SubWord(temp);
            }
            schedule[i] = schedule[i - Nk] ^ temp;
        }
    }

    private void AddRoundKey(int round) {
        for (int c = 0; c < Nb; c++) {
            int w = schedule[4 * round + c];
            state[0][c] ^= (w >>> 24) & 0xff;
            state[1][c] ^= (w >>> 16) & 0xff;
            state[2][c] ^= (w >>> 8) & 0xff;
            state[3][c] ^= (w) & 0xff;
        }
    }

    private void SubBytes() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                state[j][i] = utils.SBox.sub(state[j][i]);
        }
    }


    private void InvSubBytes() {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                state[j][i] = utils.SBox.invSub(state[j][i]);
    }

    private void ShiftRows() {
        byte temp;
        for (int i = 0; i < 4; i++)
            for (int k = 0; k < i; k++) {
                temp = state[i][0];
                System.arraycopy(state[i], 1, state[i], 0, 3);
                state[i][3] = temp;
            }
    }

    private void InvShiftRows() {
        byte temp;
        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < i; k++) {
                temp = state[i][3];
                System.arraycopy(state[i], 0, state[i], 1, 3);
                state[i][0] = temp;
            }
        }
    }

    private void MixColumns() {
        byte[] tempCol;
        for (int j = 0; j < 4; j++) {
            tempCol = new byte[]{state[0][j], state[1][j], state[2][j], state[3][j]};
            state[0][j] = (byte) (utils.gmul2(tempCol[0]) ^ utils.gmul3(tempCol[1]) ^ tempCol[2] ^ tempCol[3]);
            state[1][j] = (byte) (tempCol[0] ^ utils.gmul2(tempCol[1]) ^ utils.gmul3(tempCol[2]) ^ tempCol[3]);
            state[2][j] = (byte) (tempCol[0] ^ tempCol[1] ^ utils.gmul2(tempCol[2]) ^ utils.gmul3(tempCol[3]));
            state[3][j] = (byte) (utils.gmul3(tempCol[0]) ^ tempCol[1] ^ tempCol[2] ^ utils.gmul2(tempCol[3]));
        }
    }

    private void InvMixColumns() {
        byte[] tempCol;
        for (int j = 0; j < 4; j++) {
            tempCol = new byte[]{state[0][j], state[1][j], state[2][j], state[3][j]};
            state[0][j] = (byte) (utils.gmul14(tempCol[0]) ^ utils.gmul11(tempCol[1]) ^ utils.gmul13(tempCol[2]) ^ utils.gmul9(tempCol[3]));
            state[1][j] = (byte) (utils.gmul9(tempCol[0]) ^ utils.gmul14(tempCol[1]) ^ utils.gmul11(tempCol[2]) ^ utils.gmul13(tempCol[3]));
            state[2][j] = (byte) (utils.gmul13(tempCol[0]) ^ utils.gmul9(tempCol[1]) ^ utils.gmul14(tempCol[2]) ^ utils.gmul11(tempCol[3]));
            state[3][j] = (byte) (utils.gmul11(tempCol[0]) ^ utils.gmul13(tempCol[1]) ^ utils.gmul9(tempCol[2]) ^ utils.gmul14(tempCol[3]));
        }
    }

    private void encrypt(byte[] input) {
        this.state = new byte[4][Nb];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                this.state[j][i] = input[4 * i + j];
            }
        }
        out.print("\nPlaintext : ");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out.print(String.format("%02x", state[j][i]));
            }
        }
        //printState("Initial State");
        AddRoundKey(0);
        for (int round = 1; round < Nr; round++) {
            SubBytes();
            ShiftRows();
            MixColumns();
            AddRoundKey(round);
        }
        SubBytes();
        ShiftRows();
        AddRoundKey(Nr);
    }

    private void decrypt(byte[] input) {
        this.state = new byte[4][Nb];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                this.state[j][i] = input[4 * i + j];
            }
        }
        out.print("\nCiphertext: ");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out.print(String.format("%02x", state[j][i]));
            }
        }
        AddRoundKey(Nr);
        for (int round = 1; round < Nr; round++) {
            InvShiftRows();
            InvSubBytes();
            AddRoundKey(Nr - round);
            InvMixColumns();
        }
        InvShiftRows();
        InvSubBytes();
        AddRoundKey(0);
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

    public static void main(String[] args) throws IOException {
        out.print("BEGIN");
        readArgs(args);
        readInput();
        AES test = new AES(key);


        File old = new File(args[2]);       //Opening the input file

        out.print("\nKey       : ");
        for (int i = 0; i < test.Nk * 4; i++)
            out.print(String.format("%02x", key[i]));

        if (_mode.charAt(0) == 'e') {

            File newf = new File(args[2] + ".enc");
            old.renameTo(newf);
            FileWriter fw = new FileWriter(newf);

            long startTime = System.nanoTime();
            test.encrypt(input);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            out.print("\nCiphertext: ");
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    out.print(String.format("%02x", test.state[j][i]));
                    fw.write(String.format("%02x", test.state[j][i]));
                }
            }
            System.out.println("\nTime taken: " + duration + " nanoseconds");
            out.println();
            out.println("\nCiphertext written to: " + newf);
            fw.close();

        } else {

            File newf = new File(args[2] + ".dec");
            old.renameTo(newf);
            FileWriter fw = new FileWriter(newf);
            long startTime = System.nanoTime();
            test.decrypt(input);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            out.print("\nPlaintext : ");
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    out.print(String.format("%02x", test.state[j][i]));
                    fw.write(String.format("%02x", test.state[j][i]));
                }
            }
            System.out.println("\nTime taken: " + duration + " nanoseconds");
            out.println();
            out.println("\nPlaintext written to: " + newf);
            fw.close();
        }
    }
}
