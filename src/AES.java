import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AES {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("\tUsage: AES option keyFile inputFile");
            System.err.println("\t\toption     [e]ncryption or [d]ecryption");
            System.err.println("\t\tkeyFile    file containing 256 bit key (64 hex characters, one line)");
            System.err.println("\t\tinputFile  plain/cipher text (32 hex characters per line)\n");
            System.exit(1);
        }
        /*
            read CMD arguments
         */
        String _mode = args[0];
        try {
            FileInputStream _keyFile = new FileInputStream(args[1]);
            FileInputStream _inputFile = new FileInputStream(args[2]);
        } catch (FileNotFoundException e) {
            System.err.println("\tThe specified file was not found!");
            e.printStackTrace();
        }
    }


}
