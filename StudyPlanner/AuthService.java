/**
 * Account logic: sign in / sign up / reset password (NO Swing/AWT).
 * Accounts are stored by UserStorage in users.txt, so an account created once
 * is remembered every time the program is opened again.
 */
public class AuthService {

    public enum SignUpResult { OK, EMPTY, MISMATCH, INVALID_CHARS, TAKEN, ERROR }
    public enum ResetResult  { OK, EMPTY, MISMATCH, INVALID_CHARS, NOT_FOUND, ERROR }

    /** Returns the User if username/password are correct, otherwise null. */
    public User signIn(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) return null;
        return UserStorage.validateLogin(username, password) ? new User(username) : null;
    }

    public SignUpResult signUp(String username, String password, String confirm) {
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) return SignUpResult.EMPTY;
        if (!password.equals(confirm))                                     return SignUpResult.MISMATCH;
        if (hasBadChars(username) || hasBadChars(password))                return SignUpResult.INVALID_CHARS;
        if (UserStorage.usernameExists(username))                          return SignUpResult.TAKEN;
        return UserStorage.registerUser(username, password) ? SignUpResult.OK : SignUpResult.ERROR;
    }

    public ResetResult resetPassword(String username, String newPassword, String confirm) {
        if (username.isEmpty() || newPassword.isEmpty() || confirm.isEmpty()) return ResetResult.EMPTY;
        if (!newPassword.equals(confirm))                                     return ResetResult.MISMATCH;
        if (hasBadChars(newPassword))                                         return ResetResult.INVALID_CHARS;
        if (!UserStorage.usernameExists(username))                            return ResetResult.NOT_FOUND;
        return UserStorage.resetPassword(username, newPassword) ? ResetResult.OK : ResetResult.ERROR;
    }

    // '|' and line breaks would corrupt the users.txt format
    private boolean hasBadChars(String s) {
        return s.contains("|") || s.contains("\n") || s.contains("\r");
    }
}
