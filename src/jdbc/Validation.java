package jdbc;

public class Validation {

	    // Check whether input is empty
	    public static boolean isNotEmpty(String value) {
	        return value != null && !value.trim().isEmpty();
	    }

	    // Check alphabets and spaces
	    public static boolean containsOnlyAlphabets(String value) {
	        return value != null && value.matches("[A-Za-z ]+");
	    }

	    // Check alphabets, numbers and spaces
	    public static boolean isValidAlphaNumeric(String value) {
	        return value != null && value.matches("[A-Za-z0-9 ]+");
	    }

	    // Check only numbers
	    public static boolean containsOnlyNumbers(String value) {
	        return value != null && value.matches("\\d+");
	    }

	    // Validate email
	    public static boolean isValidEmail(String email) {
	        return email != null &&
	               email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	    }

	    // Validate phone
	    public static boolean isValidPhone(String phone) {
	        return phone != null &&
	               phone.matches("[6-9][0-9]{9}");
	    }

	    // Validate positive integer
	    public static boolean isValidPositiveNumber(String value) {
	        try {
	            return Integer.parseInt(value) > 0;
	        } catch (NumberFormatException e) {
	            return false;
	        }
	    }

	    // Validate positive fare
	    public static boolean isValidFare(String value) {
	        try {
	            return Double.parseDouble(value) > 0;
	        } catch (NumberFormatException e) {
	            return false;
	        }
	    }
	}

