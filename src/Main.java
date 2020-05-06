import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class Main {

	public static void main(String[] args) {

		String operation = null;
		String filename = null;
		String outputFilename = null;

		try {
			operation = args[0];
			filename = args[1];
			outputFilename = args[2];
		}
		catch (Exception e) {
		}

		if (null == operation || null == filename || null == outputFilename || !("encode".equals(operation) || "decode".equals(operation))) {
			System.out.println("please enter parameters.");
			System.out.println("1st parameter is operation. Valid values: decode encode");
			System.out.println("2nd parameter is source file name. Should be in this folder");
			System.out.println("3rd parameter is target file name. Will overwrite existing files");
			System.out.println("example:");
			System.out.println("java -jar base64EncoderDecoder.jar encode soapui.zip soapui.txt");
			return;
		}

		if ("encode".equals(operation)) {
			encode(filename, outputFilename);
			System.out.println("encoded " + filename + " to " + outputFilename);
			return;
		}

		if ("decode".equals(operation)) {
			decode(filename, outputFilename);
			System.out.println("decoded " + filename + " to " + outputFilename);
			return;
		}
	}
	
	public static void encode(String filename, String outputFilename) {
		byte[] bytes = getBytesFromFile(filename);
		byte[] encodedBytes = Base64.getUrlEncoder().encode(bytes);
		writeBytesToFile(encodedBytes, outputFilename);
	}
	
	public static void decode(String filename, String outputFilename) {
		byte[] bytes = getBytesFromFile(filename);
		byte[] decodedBytes = Base64.getUrlDecoder().decode(bytes);
		writeBytesToFile(decodedBytes, outputFilename);
	}
	
	public static byte[] getBytesFromFile(String filename) {
		File file = new File(filename);
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException("problem creating input stream for file: " + filename, e);
		}

		int bufferLength = 1024;
		byte[] buffer = new byte[bufferLength];
		int readLength;
		
		IOException exception = null;
		
		try {
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				while ((readLength = is.read(buffer, 0, bufferLength)) != -1) {
					os.write(buffer, 0, readLength);
				}
				return os.toByteArray();
			}
		}
		catch (IOException e) {
			exception = e;
			throw new RuntimeException("error reading file to byte array ", e);
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {
				if (exception == null) {
					throw new RuntimeException("failed to close fileInputStream ", e);
				}
				else {
					exception.addSuppressed(e);
				}
			}
		}
	}
	
	public static void writeBytesToFile(byte[] bytes, String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			}
			catch (IOException e) {
				throw new RuntimeException("error creating output file: " + filename, e);
			}
		}
		
		try (FileOutputStream os = new FileOutputStream(file, false)) {
			os.write(bytes);
			os.close();
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException("output file not found: " + filename, e);
		}
		catch (IOException e) {
			throw new RuntimeException("failed to write to output file: " + filename, e);
		}
	}

}
