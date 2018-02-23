package bankexercise;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class FileHandling extends BankAccount{
    private static final int NUMBER_RECORDS = 100;
    private static RandomAccessFile output;
    private static RandomAccessFile input;

    public FileHandling(){
        this(0, "", "", "", "", 0.0, 0.0);
    }

    public FileHandling(int accountID, String accountNumber, String surname, String firstName, String accountType, double balance, double overdraft) {
        super(accountID, accountNumber, surname, firstName, accountType, balance, overdraft);
    }

    public static void openFileForRead(File file){
        try // open file
        {
            if(file!=null)
                input = new RandomAccessFile( file, "r" );
        } // end try
        catch ( IOException ioException )
        {
            JOptionPane.showMessageDialog(null, "File Does Not Exist.");
        } // end catch
    }

    public static void openFileForWrite(String fileName){
        if(fileName!=""){
            try // open file
            {
                output = new RandomAccessFile( fileName, "rw" );
                JOptionPane.showMessageDialog(null, "Accounts saved to " + fileName);
            } // end try
            catch ( IOException ioException )
            {
                JOptionPane.showMessageDialog(null, "File does not exist.");
            } // end catch
        }
        else
            JOptionPane.showMessageDialog(null, "Invalid file name.");
    }

    public static void openFileForWrite(File file){
        try {
            if(file==null){
                JOptionPane.showMessageDialog(null, "File missing!");
            }
            else
                output = new RandomAccessFile(file, "rw" );
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void closeFile(){
        {
            try // close file and exit
            {
                if ( input != null )
                    input.close();
            } // end try
            catch ( IOException ioException )
            {

                JOptionPane.showMessageDialog(null, "Error closing file.");//System.exit( 1 );
            } // end catch
        } // end method closeFile
    }

    public static ArrayList<BankAccount> readRecords(){
        RandomAccessBankAccount record = new RandomAccessBankAccount();
        ArrayList<BankAccount> accounts = new ArrayList<>();
        try // read a record and display
        {
            while ( true )
            {
                do
                {
                    if(input!=null)
                        record.read( input );
                } while ( record.getAccountID() == 0 );



                BankAccount ba = new BankAccount(record.getAccountID(), record.getAccountNumber(), record.getFirstName(),
                        record.getSurname(), record.getAccountType(), record.getBalance(), record.getOverdraft());
                accounts.add(ba);

            } // end while
        } // end try
        catch ( EOFException eofException ) // close file
        {
            return accounts; // end of file was reached
        } // end catch
        catch ( IOException ioException )
        {
            JOptionPane.showMessageDialog(null, "Error reading file.");
            System.exit( 1 );
            return null;
        } // end catch
    }

    public static  void saveToFile(BankAccount account){
        RandomAccessBankAccount record = new RandomAccessBankAccount();
        record.setAccountID(account.getAccountID());
        record.setAccountNumber(account.getAccountNumber());
        record.setFirstName(account.getFirstName());
        record.setSurname(account.getSurname());
        record.setAccountType(account.getAccountType());
        record.setBalance(account.getBalance());
        record.setOverdraft(account.getOverdraft());

        if(output!=null){

            try {
                record.write( output );
            } catch (IOException u) {
                u.printStackTrace();
            }
        }
    }

}
