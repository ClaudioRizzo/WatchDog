package it.polimi.dima.watchdog.errors;

public class ErrorFactory {
	
	public static final String TITLE = "An error occurred: the application says";
	public static final String WIZARD_ERROR = "Fatal error in the inizialization: the application will be closed!";
	public static final String BAD_PHONE_NUMBER = "The number inserted is not a valid phone number: remember the national prefix starting with \"+\" character!";
	public static final String COMMAND_TO_NOT_ASSOCIATED = "You can't send a command to a not associated telephone!";
	public static final String MISSING_PREFERENCE = "Missing data in the application preferences: task aborted!";
	public static final String WRONG_PASSWORD = "Wrong password: task aborted!!!";
	public static final String BAD_PASSWORD = "The password must be 8-20 characters long and it must contain at least a number and a letter. Not all the special characters are allowed!";
	public static final String DIFFERENT_PASSWORDS = "The new and confirmed passwords are different!!!";
	public static final String WRONG_SECRET_ANSWER = "Wrong secret answer: association aborted!!!";
	public static final String BLANK_FIELD = "All fields must be filled: retry!";
	public static final String INTEGRITY_EXCEPTION = "The received message is not integer! Operation aborted!";
	public static final String SIGNATURE_EXCEPTION = "The received message has an invalid signature! Operation aborted!";
	public static final String INVALID_HEADER = "The received message has an invalid header! Operation aborted!";
	public static final String INTERNAL_CRYPTO_ERROR = "Fatal error while dealing with cryptography: task aborted!";
	public static final String LOCALIZATION_IMPOSSIBLE = "Gps and network localization are disabled: cannot complete the task!";
	public static final String SIREN_WAS_OFF = "The siren was already off, so nothing was performed!";
	public static final String BAD_RETURNED_DATA = "The target phone returned bad data. It's unknown whether the command worked or not!";
	public static final String GENERIC_ERROR = "An unknown error occurred: task aborted!";
	
	
	public static boolean isMessageInFactory(String message){
		if(message.equals(WIZARD_ERROR)){
			return true;
		}
		else if(message.equals(BAD_PHONE_NUMBER)){
			return true;
		}
		else if(message.equals(COMMAND_TO_NOT_ASSOCIATED)){
			return true;
		}
		else if(message.equals(MISSING_PREFERENCE)){
			return true;
		}
		else if(message.equals(WRONG_PASSWORD)){
			return true;
		}
		else if(message.equals(BAD_PASSWORD)){
			return true;
		}
		else if(message.equals(DIFFERENT_PASSWORDS)){
			return true;
		}
		else if(message.equals(WRONG_SECRET_ANSWER)){
			return true;
		}
		else if(message.equals(BLANK_FIELD)){
			return true;
		}
		else if(message.equals(INTEGRITY_EXCEPTION)){
			return true;
		}
		else if(message.equals(SIGNATURE_EXCEPTION)){
			return true;
		}
		else if(message.equals(INVALID_HEADER)){
			return true;
		}
		else if(message.equals(INTERNAL_CRYPTO_ERROR)){
			return true;
		}
		else if(message.equals(LOCALIZATION_IMPOSSIBLE)){
			return true;
		}
		else if(message.equals(SIREN_WAS_OFF)){
			return true;
		}
		else if(message.equals(BAD_RETURNED_DATA)){
			return true;
		}
		else if(message.equals(GENERIC_ERROR)){
			return true;
		}
		else{
			return false;
		}
	}
}
