AES-256
=======

A java implementation of the AES cipher.

The implementation works for 128, 192 and 256 bit keys without any modification to the code.

The key schedule is stored in an int array

The states are stored in a 2-D byte array

For mixColumns and InvMixColumns() circular matrices specified in the AES proposal are used. The multiplication in Galois Field is done using seperate functions defined in the utils.java file. All these functions use only shift operators and XOR's making them very fast to implement in hardware while at the same time not requiring large amount of memory. The most trivial function is gmul2() which does the Galois field multiplication for 2. The other functions rely on this to obtain their outputs.
The multiplication for 2 is equivalent to shifting the number left by one and then XORing the value 0x1B if the high bit had been 1.
The multiplicaton by 3 is done by multiplying by 2 (using gmul2 explained earlier) and then XORing the output with the original value (as 3 = 2 xor 1)
mixColumns only requires Galois field multiplication for 1,2 and 3 so with these we are able to perform mixColumns()

For invMixColumns() we require Galois field multiplication for 9,11,13 and 14. These are also obtained in a similar way using the multiplication by 2 as the base. For example gmul9 is computed by using gmul8 and the original value.



utils.java has the methods to read + write to files, multiply matrices
