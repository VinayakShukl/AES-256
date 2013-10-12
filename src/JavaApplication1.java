/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

/**
 *
 * @author Jatin
 */
public class JavaApplication1 {

    /**
     * @param args the command line arguments
     */
    private static byte gmul2 ( byte a){
        System.out.println("GMUL 2");
	 byte hi;
         hi = (byte)(a & 0x80);
         //System.out.print(String.format("0x%02X", hi) + " ");
         a <<= 1;
         if((hi == (byte)0x80)){
             //System.out.print(String.format("0x%02X", hi) + " ");
             a^=0x1b;
         }
	return a;
}
    
        public static byte gmul4 (byte a)
    {
        System.out.println("GMUL 4");
        byte b = a;
        b = gmul2(a);
        byte c  = gmul2(b);
        b = (byte) (a ^ b);
        return c;

    }

    public static byte gmul8 (byte a)
    {
        System.out.println("GMUL 8");
        byte b = gmul2(a);
        byte c = gmul2(b);
        byte d = gmul2(c);
        //byte result = (byte) (b ^ c ^ d);
         //b = (byte) (gmul2(a) ^ gmul2(a) ^ gmul2(a));
        return d;
    }

    public static byte gmul11 (byte a){
        //Works and checked
        byte b = a;
        b = (byte) (gmul8(a) ^ gmul2(a) ^ a);
        return b;
    }

    public static byte gmul13 (byte a) {
        //Works and checked
        byte b = a;
        b = (byte) (gmul8(a) ^ gmul4(a) ^ a);
        return b;
    }

    public static byte gmul9 (byte a){
        //Works and checked
        byte b = a;
        b = (byte) (gmul8(a) ^ a);
        return b;
    }


    public static byte gmul14 (byte a){
        //Works and checked

        byte b = a;
       
        
        System.out.println(" IN GMUL 14");
        b = (byte) (gmul8(a) ^ gmul4(a) ^ gmul2(a));

        return b;

        /*


         mul14 = mul8(a) ^ mul4(a) ^ mul2

         mul13 = mul8(a) ^ mul4(a) ^ a

         mul9 =  mul8(a) ^ a

         mul11 = mul8(a) ^ mul2(a) ^ a

         mul8 = 3 times mul2;

         mul4 = 2 times mul2;

         */

    }
    public static byte gmul11ori (byte a){

        byte b = a;
        byte c = a;
              byte   hi = (byte)(a & 0x80);

        a <<= 1;
        if((hi == (byte)0x80)){
             System.out.print(String.format("0x%02X", hi) + " ");
             a^=0x1b;
         }
        hi = (byte)(a & 0x80);
        a <<= 1;
        if((hi == (byte)0x80)){
             System.out.print(String.format("0x%02X", hi) + " ");
             a^=0x1b;
         }
        hi = (byte)(a & 0x80);
        a <<= 1;
        if((hi == (byte)0x80)){
             System.out.print(String.format("0x%02X", hi) + " ");
             a^=0x1b;
         }
        b = (byte) (a ^ gmul3(b));
        System.out.println("MUL11 : " + b);
        System.out.print(String.format("0x%02X", b) + " ");
        System.out.print(String.format("0x%02X", (byte)(gmul8(c) ^ gmul3(c))) + " ");
        return b;
    }
    
    private static byte gmul3 (byte a){
        
        byte b = a;
        a = JavaApplication1.gmul2(a);
        b ^= a;
        //System.out.println("MUL3 : " + b);
        //System.out.print(String.format("0x%02X", b) + " ");
        return b;
    }
    public static void main(String[] args) {
        // TODO code application logic here
        //byte p  = JavaApplication1.gmul2((byte)0xd4);
         byte  b = gmul2((byte)0xbf);
         byte a = (byte) 0xd4;
         byte c = (byte) 0xe7;
         byte d = (byte) 0x30;
         
        byte [][] state = new byte[4][4];
        state[0][0] = (byte) 0xd4;
        state[1][0] = (byte) 0xbf;
        state[2][0] = (byte) 0x5d;
        state[3][0] = (byte) 0x30;
         
         state[1][0] = (byte) ((byte)state[0][0] ^ (byte)gmul2(state[1][0]) ^ (byte)gmul3(state[2][0]) ^ (byte)state[3][0]);
         state[0][0] = (byte) ((byte)gmul2(state[0][0]) ^ (byte)gmul3(state[1][0]) ^ (byte)state[2][0] ^ (byte)state[3][0]);
         state[2][0] = (byte) (state[0][0] ^ state[1][0] ^ gmul2(state[2][0]) ^ gmul3(state[3][0]));
         state[3][0] = (byte) (gmul3(state[0][0]) ^ state[1][0] ^ state[2][0] ^ gmul2(state[3][0]));
            

         
         b =  (byte) ((byte)a ^ (byte)b ^ (byte)c ^ (byte)d);
        System.out.println("ANSWER : ");
        //System.out.println(b);
        System.out.print(String.format("0x%02X", state[0][0]) + "\n ");
        System.out.print(String.format("0x%02X", state[1][0]) + "\n ");
        System.out.print(String.format("0x%02X", state[2][0]) + "\n ");
        System.out.print(String.format("0x%02X", state[3][0]) + "\n");

                        
        //System.out.println("OUTPUT : " + (byte)p);
    }
}
