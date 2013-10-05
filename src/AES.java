import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AES {

    private final int Nb = 4;
    private final int word = 4;
    private int Nk;
    private int Nr;
    private byte[] key;
    private byte[][] w;
    private byte[][] dw;
    private byte[][] state;
    private byte[][] rcon;

    public static void readInput(String[] args) {

        if (args.length < 3) {
            System.err.println("\tUsage: AES option keyFile inputFile");
            System.err.println("\t\toption     [e]ncryption or [d]ecryption");
            System.err.println("\t\tkeyFile    file containing 256 bit key (64 hex characters, one line)");
            System.err.println("\t\tinputFile  plain/cipher text (32 hex characters per line)\n");
            System.exit(1);
        }

        String _mode = args[0];
        try {
            FileInputStream _keyFile = new FileInputStream(args[1]);
            FileInputStream _inputFile = new FileInputStream(args[2]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setupVar() {

    }

    public static void main(String[] args) {
        byte b[] = utils.hexToByte("00112233445566778899AABBCCDDEEFF");

        for(int i=0; i<b.length; i++){
            System.out.print(String.format("0x%02X", b[i]) + ",");
        }
    }
}
