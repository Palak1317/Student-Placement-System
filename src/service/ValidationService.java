package service;

public class ValidationService {

    public static boolean isEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}