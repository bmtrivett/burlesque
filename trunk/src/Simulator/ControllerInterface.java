package Simulator;

public interface ControllerInterface {

        /**
         * Executes the TRAP instruction. One of 7 different sub-instructions occurs
         * depending on the 2 hex digits: <br />
         * 21 = OUT: Write the character in R0[7:0] to the console. <br />
         * 22 = PUTS: Write the null-terminated string pointed to by R0 to the
         * console. <br />
         * 23 = IN: Print a prompt on the screen and read a single character from
         * the keyboard. The character is copied to the screen and its ASCII code is
         * copied to R0. The high 8 bits of R0 are cleared. <br />
         * 25 = HALT: Halt execution and print a message to the console. <br />
         * 31 = OUTN: Write the value of R0 to the console as a decimal integer. <br />
         * 33 = INN: Print a prompt on the screen and read a decimal number from the
         * keyboard. The number is echoed to the screen and stored in R0. Must be in
         * the range -32768 < x < 32767. <br />
         * 43 = RND: Store a random number in R0.
         * 
         * @param executeError
         *            A string with the first 4 characters being TRAP and the next 2
         *            being hex.
         * @return A string designating any errors that occurred or HALT if the halt
         *         instruction was read.
         */
        public abstract String executeTrap(String executeError);

        /**
         * Stores the character parameter in register 0. Must be in ascii table.
         * 
         * @param ch
         *            The character to be stored.
         * @return True if there was an error, false otherwise.
         */
        public abstract Boolean keyTyped(char ch);

        /**
         * Converts the string parameter to a number and stores it in register 0.
         * 
         * @param text
         *            The String to be converted.
         * @return True if there was an error, false otherwise.
         */
        public abstract Boolean readTrapInteger(String text);
}