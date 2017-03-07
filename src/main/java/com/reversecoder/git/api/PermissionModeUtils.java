package com.reversecoder.git.api;

public final class PermissionModeUtils {
    private PermissionModeUtils() {
    }

    /**
     * See the System Interfaces volume of IEEE Std 1003.1-2001, umask(1)
     *
     * @param modeStr
     *            permission mode (numeric or symbolic)
     * @return the mode that can be used with umask to accomplish modeStr.
     */
    public static String getUserMaskFor(String modeStr) {
        String ret = null;

        try {
            int mode = Integer.valueOf(modeStr, 8).intValue();

            mode = mode % 8 + ((mode / 8) % 8) * 8 + ((mode / 64) % 8) * 64;

            // CHECKSTYLE_OFF: MagicNumber
            ret = Integer.toOctalString(0777 - mode);
            // CHECKSTYLE_ON: MagicNumber
        } catch (final NumberFormatException e) {
            try {
                Integer.parseInt(modeStr);
            } catch (final NumberFormatException e1) {
                ret = modeStr;
            }
        }

        if (ret == null) {
            throw new IllegalArgumentException("The mode is a number but is not octal");
        }

        return ret;
    }
}
