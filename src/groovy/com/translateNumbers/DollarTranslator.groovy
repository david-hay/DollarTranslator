package com.translateNumbers

import org.apache.commons.lang.Validate

/**
 * Translator, able to translate a numeric dollar amount into the
 * appropriate words
 *
 * @author: David J Hay
 * Date: 2/16/13
 * Time: 12:22 PM
 */
class DollarTranslator {

    static DIGITS = ['zero', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine']

    static TEN_TO_NINETEEN = ['ten', 'eleven', 'twelve', 'thirteen', 'fourteen', 'fifteen', 'sixteen', 'seventeen', 'eighteen', 'nineteen']

    static TENS = ['twenty', 'thirty', 'forty', 'fifty', 'sixty', 'seventy', 'eighty', 'ninety']

    static UNITS = ['', 'thousand', 'million', 'billion', 'trillion', 'quadrillion', 'quintillion']


    /**
     * This class is not supposed to be instantiated - it has no state.
     *
     * Groovy doesn't quite prevent this, but at least it communicates our intent.
     */
    private DollarTranslator() {
        throw new UnsupportedOperationException()
    }


    /**
     * Translate a numeric String representing a dollar amount, into words.
     *
     *    eg 2,523.04 will return "Two thousand five hundred twenty-three and 04/100 dollars"
     *
     * All commas in the input are removed (so commas in the wrong place are just ignored).
     * If cents are specified, there must be the full 2 decimal places specified (easily changed).
     *
     * @param   input     a numeric string representing a dollar amount
     * @return            a String holding the number translated into words.
     */
    static String translate(String input) {

        Validate.notEmpty(input, "String to translate cannot be null or blank.")

        //strip commas - could later throw error if commas found in unexpected places
        input = input.replaceAll(',', '')

        //validate what we received
        def errors = validate(input)

        if (errors) {
            def errorMsg = "The amount you entered is not valid:"
            errors.each {
                errorMsg += "\n-$it"
            }
            throw new TranslatorException(errorMsg)
        }

        //split into dollar and cents and translate
        def parts = input.split("\\.")

        def translation = translateDollarPortion(parts[0])

        if (parts.size() > 1 && parts[1].toInteger() != 0) {
            translation += ' and ' + translateCents(parts[1])
        }

        def dollarString = translation == 'one' ? 'dollar' : 'dollars'

        return (translation + ' ' + dollarString).capitalize()
    }

    /**
     * Validate that a String is a valid currency String.
     *
     * All commas should be removed before calling this method.  All errors encountered are return
     * in a List.
     *
     * @param   input     String to validate
     * @return            a List holding zero or more errors
     */
    private static List validate(String input) {

        List errors = []

        if (!input.isNumber()) {
            errors << 'Input must be a valid number'
        }
        else if (input.toBigDecimal() < 0) {
            errors << "Only positive amounts are supported."
        }

        if (input.contains('.') && input.indexOf('.') != input.length() - 3) {
            errors << 'If cents are specified, two decimal places must be used'
        }

        if (input.split("\\.")[0].length() > 21) {
            errors << 'Only whole dollar amounts of up to 21 characters (quintillion) are supported.'
        }

        return errors
    }

    /**
     * Translate a String holding a whole dollar amount into words.
     *
     *    eg 2,523.04 will return "Two thousand five hundred twenty-three"
     *
     * All commas should have been remove before calling this method,
     * and the String should be a not-null whole number with max length of
     * 21.
     *
     * @param dollarPart
     * @return
     */
    private static String translateDollarPortion(String dollarPart) {

        def translation = ''

        //special case 0 - makes things easier later
        if (dollarPart == '0') {
            return 'zero'
        }

        //pad with leading zeros to make length a multiple of 3
        def padded = pad(dollarPart)

        def len = padded.length()

        //index into units eg millions/thousands etc
        int unitsIndex = (int) len / 3 - 1

        //loop through triplets, translating each with appropriate units
        for (i in (0..len-1).step(3)) {
            String s = translateTriplet(padded[i..i+2], UNITS[unitsIndex--])
            if (s) {
                translation += (i > 0 ? ' ' + s : s)        //if not the first one, add with leading space
            }
        }

        translation
    }

    /**
     * Utility method to translate a string of length 3 into the
     * equivalent wording, including the unit passed to it.
     *
     * eg (135, Thousand) => one hundred thirty-five Thousand
     *
     * An empty string is returned for zero, as this is what you want
     * in all cases except true zero, which should be handled elsewhere.
     *
     * @param s      a String on length 3 holding an integer value to translate
     * @param unit   a String holding the units to append
     * @return       a String holding s translated into words
     */
    private static String translateTriplet(String s, String unit) {

        Validate.isTrue(s.length() == 3, 'input must be of length 3')
        Validate.isTrue(s.isInteger(), 'input must be an integer')

        //if '000' return empty string
        if (s == '000') {
            return ''
        }

        //divide into hundreds and 0-99
        int hundreds = s[0].toInteger()
        int n = s[1..2].toInteger()

        String translation = ''

        //translate 1-99 portion
        switch (n) {
            case 0:     //we must be associated with a multiple of 100, do nothing
                break

            case 1..9:
                translation += DIGITS[n]
                break

            case 10..19:
                translation += TEN_TO_NINETEEN[n-10]
                break

            default: //20..99
                int xFactor = n / 10

                translation += TENS[xFactor-2]

                if (n % 10 > 0) {
                    translation += "-${DIGITS[n%10]}"
                }
        }

        if (hundreds > 0) {
            translation = DIGITS[hundreds] + ' hundred' + (translation ? ' ' + translation : '')
        }

        translation + (unit ? ' ' + unit : '')
    }

    /**
     * Pads a string on the left with zeros, to make its length
     * a multiple of 3
     *
     * @param s String to pad
     * @return  String whose length is a multiple of 3
     */
    private static String pad(String s) {
        int len = s.length()

        if (len % 3 > 0) {
            int intDiv = len / 3
            return s.padLeft(3 * ++intDiv, '0')
        }

        s
    }

    /**
     * Translate a String representing the amount in cents, into a fraction.
     *
     * Translation will be in the form of 24/100
     *
     * @param centPart      String holding numerical amount of cents to translate
     * @return              String holding equivalent as a fraction.
     */
    private static String translateCents(String centPart) {

        return "$centPart/100"
    }

}
