package idv.const_x.tools.otp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


public class Token {


    public static enum TokenType {
        HOTP, TOTP
    }

    private static char[] STEAMCHARS = new char[]{
            '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C',
            'D', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
            'R', 'T', 'V', 'W', 'X', 'Y'};


    private String issuerExt;
    private TokenType type;
    private String algo;
    private byte[] secret;
    private int digits;
    private long counter;
    private int period;

    /**
     * TOTP算法(Time-based One-time Password algorithm)
     * @param secret
     * @param algorithm 算法 默认sha1
     * @param digits 数字位数 默认6
     * @param period 刷新时长（秒） 默认30
     * @return
     */
    public static Token getTOTPToken(String secret, String algorithm, Integer digits, Integer period) {
        return new Token(secret, TokenType.TOTP, algorithm, digits, period, null);
    }

    /**
     * HOTPToken (HMAC-based One-Time Password)
     * @param secret
     * @param algorithm
     * @param digits
     * @param period
     * @param counter
     * @return
     */
    public static Token getHOTPToken(String secret, String algorithm, Integer digits, Integer period, Integer counter) {
        return new Token(secret, TokenType.HOTP, algorithm, digits, period, counter);
    }


    private Token(String secret, TokenType type, String algorithm, Integer digits, Integer period, Integer counter) {
        issuerExt = "otpauth";
        this.type = type;
        algo = algorithm;
        if (algo == null)
            algo = "sha1";
        algo = algo.toUpperCase(Locale.US);
        try {
            Mac.getInstance("Hmac" + algo);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        if (digits == null) {
            digits = 6;
        }
        this.digits = digits;

        if (!issuerExt.equals("Steam") && digits != 6 && digits != 8) {
            digits = 6;
        }
        if (period == null) {
            period = 30;
        }
        this.period = period;

        if (type == TokenType.HOTP) {
            if (counter == null) {
                counter = 0;
            }
            this.counter = counter;
        }

        try {
            this.secret = Base32String.decode(secret);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getCode(long counter) {
        // Encode counter in network byte order
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(counter);

        // Create digits divisor
        int div = 1;
        for (int i = digits; i > 0; i--)
            div *= 10;

        // Create the HMAC
        try {
            Mac mac = Mac.getInstance("Hmac" + algo);
            mac.init(new SecretKeySpec(secret, "Hmac" + algo));

            // Do the hashing
            byte[] digest = mac.doFinal(bb.array());

            // Truncate
            int binary;
            int off = digest[digest.length - 1] & 0xf;
            binary = (digest[off] & 0x7f) << 0x18;
            binary |= (digest[off + 1] & 0xff) << 0x10;
            binary |= (digest[off + 2] & 0xff) << 0x08;
            binary |= (digest[off + 3] & 0xff);

            String hotp = "";
            if (issuerExt.equals("Steam")) {
                for (int i = 0; i < digits; i++) {
                    hotp += STEAMCHARS[binary % STEAMCHARS.length];
                    binary /= STEAMCHARS.length;
                }
            } else {
                binary = binary % div;

                // Zero pad
                hotp = Integer.toString(binary);
                while (hotp.length() != digits)
                    hotp = "0" + hotp;
            }

            return hotp;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    public int getDigits() {
        return digits;
    }

    // NOTE: This may change internal data. You MUST save the token immediately.
    public TokenCode generateCodes() {
        long cur = System.currentTimeMillis();

        switch (type) {
            case HOTP:
                return new TokenCode(getCode(counter++), cur, cur + (period * 1000));

            case TOTP:
                long counter = cur / 1000 / period;
                return new TokenCode(getCode(counter + 0),
                        (counter + 0) * period * 1000,
                        (counter + 1) * period * 1000,
                        new TokenCode(getCode(counter + 1),
                                (counter + 1) * period * 1000,
                                (counter + 2) * period * 1000));
        }

        return null;
    }

    public TokenType getType() {
        return type;
    }


    public static void main(String[] args) throws InterruptedException {
        Token token = Token.getTOTPToken("MO3S3LXYSRI3T57L", null, 6, 30);
        TokenCode tokenCode = token.generateCodes();
        String cur = null;
        while (true) {
            String code = tokenCode.getCurrentCode();
            if (code == null) {
                break;
            }
            if (code != cur) {
                cur = code;
                System.out.println(cur);
            }
            Thread.currentThread();
            Thread.sleep(1000);
        }

    }


}
