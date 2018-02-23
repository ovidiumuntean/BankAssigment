package bankexercise;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class BankApplication extends JFrame {
	
	ArrayList<BankAccount> accountList = new ArrayList<BankAccount>();
	static HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();
	private final static int TABLE_SIZE = 29;
	static private final String newline = "\n";
	
	JMenuBar menuBar;
	JMenu navigateMenu, recordsMenu, transactionsMenu, fileMenu, exitMenu;
	JMenuItem nextItem, prevItem, firstItem, lastItem, findByAccount, findBySurname, listAll;
	JMenuItem createItem, modifyItem, deleteItem, setOverdraft, setInterest;
	JMenuItem deposit, withdraw, calcInterest;
	JMenuItem open, save, saveAs;
	JMenuItem closeApp;
	JButton firstItemButton, lastItemButton, nextItemButton, prevItemButton;
	JLabel accountIDLabel, accountNumberLabel, firstNameLabel, surnameLabel, accountTypeLabel, balanceLabel, overdraftLabel;
	JTextField accountIDTextField, accountNumberTextField, firstNameTextField, surnameTextField, accountTypeTextField, balanceTextField, overdraftTextField;
	static JFileChooser fc;
	JTable jTable;
	double interestRate;
	
	int currentItem = 0;
	
	
	boolean openValues;
	
	public BankApplication() {
		
		super("Bank Application");
		initComponents();
	}
	
	public void initComponents() {
		setLayout(new BorderLayout());

		//NEW COde for creating the display panel

		add(this.createDisplayPanel(), BorderLayout.CENTER);

		//New Code for creating and returning the button panel
		add(this.createButtonPanel(), BorderLayout.SOUTH);
		
		menuBar = new JMenuBar();
    	setJMenuBar(menuBar);

    	//NEW CODE
		this.createNavigationMenu();
    	
    	menuBar.add(navigateMenu);
    	// NEW code for creating the records menu
    	this.createRecordsMenu();
    	
    	menuBar.add(recordsMenu);
    	// NEW CODE for creating the transaction menu
    	this.createTransactionMenu();
    	
    	menuBar.add(transactionsMenu);
    	
    	this.createFileMenu();
    	
    	menuBar.add(fileMenu);
    	
    	exitMenu = new JMenu("Exit");
    	
    	closeApp = new JMenuItem("Close Application");
    	
    	exitMenu.add(closeApp);
    	
    	menuBar.add(exitMenu);
    	
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
	
		setOverdraft.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(table.get(currentItem).getAccountType().trim().equals("Current")){
					String newOverdraftStr = JOptionPane.showInputDialog(null, "Enter new Overdraft", JOptionPane.OK_CANCEL_OPTION);
					overdraftTextField.setText(newOverdraftStr);
					table.get(currentItem).setOverdraft(Double.parseDouble(newOverdraftStr));
				}
				else
					JOptionPane.showMessageDialog(null, "Overdraft only applies to Current Accounts");
			
			}
		});
	
		ActionListener first = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				saveOpenValues();
				
				currentItem=0;
				while(!table.containsKey(currentItem)){
					currentItem++;
				}
				displayDetails(currentItem);
			}
		};

		ActionListener next1 = new ActionListener(){
			public void actionPerformed(ActionEvent e){

				int maxKey = Collections.max(getTableKeyList());

                saveOpenValues();

                if(currentItem<maxKey){
                    currentItem++;
                    while(!table.containsKey(currentItem)){
                        currentItem++;
                    }
                }
					displayDetails(currentItem);			
			}
		};
		
		

		ActionListener prev = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			    //New code refactored for getting the table keyList
				int minKey = Collections.min(getTableKeyList());
				//System.out.println(minKey);
				
				if(currentItem>minKey){
					currentItem--;
					while(!table.containsKey(currentItem)){
						//System.out.println("Current: " + currentItem + ", min key: " + minKey);
						currentItem--;
					}
				}
				displayDetails(currentItem);				
			}
		};
	
		ActionListener last = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOpenValues();
				
				currentItem =29;
								
				while(!table.containsKey(currentItem)){
					currentItem--;
					
				}
				
				displayDetails(currentItem);
			}
		};
		
		nextItemButton.addActionListener(next1);
		nextItem.addActionListener(next1);
		
		prevItemButton.addActionListener(prev);
		prevItem.addActionListener(prev);

		firstItemButton.addActionListener(first);
		firstItem.addActionListener(first);

		lastItemButton.addActionListener(last);
		lastItem.addActionListener(last);
		
		deleteItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
						
							table.remove(currentItem);
							JOptionPane.showMessageDialog(null, "Account Deleted");
							

							currentItem=0;
							while(!table.containsKey(currentItem)){
								currentItem++;
							}
							displayDetails(currentItem);
							
			}
		});
		
		createItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new CreateBankDialog(table);		
			}
		});
		
		
		modifyItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				surnameTextField.setEditable(true);
				firstNameTextField.setEditable(true);
				
				openValues = true;
			}
		});
		
		setInterest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				 String interestRateStr = JOptionPane.showInputDialog("Enter Interest Rate: (do not type the % sign)");
				 if(interestRateStr!=null)
					 interestRate = Double.parseDouble(interestRateStr);
			
			}
		});
		
		listAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				//Refactored code for creating the display all window
		        new DisplayDetailsScrollPane("Table Demo", table);
			}
		});
		
		open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				readFile();
				currentItem=0;
				while(!table.containsKey(currentItem)){
					currentItem++;
				}
				displayDetails(currentItem);
			}
		});
		
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				writeFile();
			}
		});
		
		saveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveFileAs();
			}
		});
		
		closeApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int answer = JOptionPane.showConfirmDialog(BankApplication.this, "Do you want to save before quitting?");
				if (answer == JOptionPane.YES_OPTION) {
					saveFileAs();
					dispose();
				}
				else if(answer == JOptionPane.NO_OPTION)
					dispose();
				else if(answer==0)
					;
				
				
				
			}
		});	
		
		findBySurname.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				String sName = JOptionPane.showInputDialog("Search for surname: ");
				boolean found = false;
				
				 for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					   
					 if(sName.equalsIgnoreCase((entry.getValue().getSurname().trim()))){
						 found = true;
						 //Refactored Code After creating the method setDisplayAccountDetails()
						 setDisplayAccountDetails(entry.getValue());
					 }
				 }		
				 if(found)
					 JOptionPane.showMessageDialog(null, "Surname  " + sName + " found.");
				 else
					 JOptionPane.showMessageDialog(null, "Surname " + sName + " not found.");
			}
		});
		
		findByAccount.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				String accNum = JOptionPane.showInputDialog("Search for account number: ");
				boolean found = false;
			
				 for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					   
					 if(accNum.equals(entry.getValue().getAccountNumber().trim())){
						 found = true;
						 //Refactored Code After creating the method setDisplayAccountDetails()
						 setDisplayAccountDetails(entry.getValue());
					 }			 
				 }
				 if(found)
					 JOptionPane.showMessageDialog(null, "Account number " + accNum + " found.");
				 else
					 JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
				
			}
		});
		
		deposit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String accNum = JOptionPane.showInputDialog("Account number to deposit into: ");
				boolean found = false;
				
				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					if(accNum.equals(entry.getValue().getAccountNumber().trim())){
						found = true;
						String toDeposit = JOptionPane.showInputDialog("Account found, Enter Amount to Deposit: ");
						entry.getValue().setBalance(entry.getValue().getBalance() + Double.parseDouble(toDeposit));
						displayDetails(entry.getKey());
						//balanceTextField.setText(entry.getValue().getBalance()+"");
					}
				}
				if (!found)
					JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
			}
		});
		
		withdraw.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String accNum = JOptionPane.showInputDialog("Account number to withdraw from: ");
				String toWithdraw = JOptionPane.showInputDialog("Account found, Enter Amount to Withdraw: ");
				boolean found;
				
				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {

					if(accNum.equals(entry.getValue().getAccountNumber().trim())){

						//NEW CODE by creating a new method in BankAccount for withdraw money
						if(entry.getValue().withdrawMoney(Double.parseDouble(toWithdraw))){
						    displayDetails(entry.getKey());
                        }

					}					
				}
			}
		});
		
		calcInterest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					if(entry.getValue().getAccountType().equals("Deposit")){
						double equation = 1 + ((interestRate)/100);
						entry.getValue().setBalance(entry.getValue().getBalance()*equation);
						//System.out.println(equation);
						JOptionPane.showMessageDialog(null, "Balances Updated");
						displayDetails(entry.getKey());
					}
				}
			}
		});		
	}

	//********************************************************************
    //************************ METHODS ***********************************
    //********************************************************************


    ////******************************   GUI COMPONENTS
    //*************************************************
    private void createNavigationMenu(){
        navigateMenu = new JMenu("Navigate");

        nextItem = new JMenuItem("Next Item");
        prevItem = new JMenuItem("Previous Item");
        firstItem = new JMenuItem("First Item");
        lastItem = new JMenuItem("Last Item");
        findByAccount = new JMenuItem("Find by Account Number");
        findBySurname = new JMenuItem("Find by Surname");
        listAll = new JMenuItem("List All Records");

        navigateMenu.add(nextItem);
        navigateMenu.add(prevItem);
        navigateMenu.add(firstItem);
        navigateMenu.add(lastItem);
        navigateMenu.add(findByAccount);
        navigateMenu.add(findBySurname);
        navigateMenu.add(listAll);
    }

    private void createRecordsMenu(){
        recordsMenu = new JMenu("Records");

        createItem = new JMenuItem("Create Item");
        modifyItem = new JMenuItem("Modify Item");
        deleteItem = new JMenuItem("Delete Item");
        setOverdraft = new JMenuItem("Set Overdraft");
        setInterest = new JMenuItem("Set Interest");

        recordsMenu.add(createItem);
        recordsMenu.add(modifyItem);
        recordsMenu.add(deleteItem);
        recordsMenu.add(setOverdraft);
        recordsMenu.add(setInterest);
    }

    private void createTransactionMenu(){
        transactionsMenu = new JMenu("Transactions");

        deposit = new JMenuItem("Deposit");
        withdraw = new JMenuItem("Withdraw");
        calcInterest = new JMenuItem("Calculate Interest");

        transactionsMenu.add(deposit);
        transactionsMenu.add(withdraw);
        transactionsMenu.add(calcInterest);
    }

    private void createFileMenu(){
        fileMenu = new JMenu("File");

        open = new JMenuItem("Open File");
        save = new JMenuItem("Save File");
        saveAs = new JMenuItem("Save As");

        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
    }

    private JPanel createButtonPanel(){
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));

        nextItemButton = new JButton(new ImageIcon("src\\next.png"));
        prevItemButton = new JButton(new ImageIcon("src\\prev.png"));
        firstItemButton = new JButton(new ImageIcon("src\\first.png"));
        lastItemButton = new JButton(new ImageIcon("src\\last.png"));

        buttonPanel.add(firstItemButton);
        buttonPanel.add(prevItemButton);
        buttonPanel.add(nextItemButton);
        buttonPanel.add(lastItemButton);
        return buttonPanel;
    }

    private JPanel createDisplayPanel(){
        JPanel displayPanel = new JPanel(new MigLayout());

        accountIDLabel = new JLabel("Account ID: ");
        accountIDTextField = new JTextField(15);
        accountIDTextField.setEditable(false);

        displayPanel.add(accountIDLabel, "growx, pushx");
        displayPanel.add(accountIDTextField, "growx, pushx, wrap");

        accountNumberLabel = new JLabel("Account Number: ");
        accountNumberTextField = new JTextField(15);
        accountNumberTextField.setEditable(false);

        displayPanel.add(accountNumberLabel, "growx, pushx");
        displayPanel.add(accountNumberTextField, "growx, pushx, wrap");

        surnameLabel = new JLabel("Last Name: ");
        surnameTextField = new JTextField(15);
        surnameTextField.setEditable(false);

        displayPanel.add(surnameLabel, "growx, pushx");
        displayPanel.add(surnameTextField, "growx, pushx, wrap");

        firstNameLabel = new JLabel("First Name: ");
        firstNameTextField = new JTextField(15);
        firstNameTextField.setEditable(false);

        displayPanel.add(firstNameLabel, "growx, pushx");
        displayPanel.add(firstNameTextField, "growx, pushx, wrap");

        accountTypeLabel = new JLabel("Account Type: ");
        accountTypeTextField = new JTextField(5);
        accountTypeTextField.setEditable(false);

        displayPanel.add(accountTypeLabel, "growx, pushx");
        displayPanel.add(accountTypeTextField, "growx, pushx, wrap");

        balanceLabel = new JLabel("Balance: ");
        balanceTextField = new JTextField(10);
        balanceTextField.setEditable(false);

        displayPanel.add(balanceLabel, "growx, pushx");
        displayPanel.add(balanceTextField, "growx, pushx, wrap");

        overdraftLabel = new JLabel("Overdraft: ");
        overdraftTextField = new JTextField(10);
        overdraftTextField.setEditable(false);

        displayPanel.add(overdraftLabel, "growx, pushx");
        displayPanel.add(overdraftTextField, "growx, pushx, wrap");
        return displayPanel;
    }

    ////******************************   GUI COMPONENTS END ********

	//************** NEW METHOD FOR GETTING THE TABLE KEY LIST
	private ArrayList<Integer> getTableKeyList(){
        ArrayList<Integer> keyList = new ArrayList<Integer>();
        int i=0;

        while(i<TABLE_SIZE){
            i++;
            if(table.containsKey(i))
                keyList.add(i);
        }
        return keyList;
    }

    //*************** NEW METHOD for setting the account detail, used in found by surname, account number
    public void displayDetails(int currentItem) {
        accountIDTextField.setText(table.get(currentItem).getAccountID()+"");
        accountNumberTextField.setText(table.get(currentItem).getAccountNumber());
        surnameTextField.setText(table.get(currentItem).getSurname());
        firstNameTextField.setText(table.get(currentItem).getFirstName());
        accountTypeTextField.setText(table.get(currentItem).getAccountType());
        balanceTextField.setText(table.get(currentItem).getBalance()+"");
        if(accountTypeTextField.getText().trim().equals("Current"))
            overdraftTextField.setText(table.get(currentItem).getOverdraft()+"");
        else
            overdraftTextField.setText("Only applies to current accs");

    }
	
	public void saveOpenValues(){		
		if (openValues){
			surnameTextField.setEditable(false);
			firstNameTextField.setEditable(false);
				
			table.get(currentItem).setSurname(surnameTextField.getText());
			table.get(currentItem).setFirstName(firstNameTextField.getText());
		}
	}

	public void setDisplayAccountDetails(BankAccount account){
		accountIDTextField.setText(account.getAccountID()+"");
		accountNumberTextField.setText(account.getAccountNumber());
		surnameTextField.setText(account.getSurname());
		firstNameTextField.setText(account.getFirstName());
		accountTypeTextField.setText(account.getAccountType());
		balanceTextField.setText(account.getBalance()+"");
		overdraftTextField.setText(account.getOverdraft()+"");
	}
	
	private static FileHandling fileHandling = new FileHandling();
	
	public static void openFileRead()
	   {
		
		table.clear();
			
		fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		 
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

        } else {
                }

		   //NEW CODE
		   fileHandling.openFileForRead(fc.getSelectedFile());
			
	   } // end method openFile
	
	static String fileToSaveAs = "";
	
	public static void openFileWrite()

	   {
		if(fileToSaveAs!=""){
	      //NEW CODE
			fileHandling.openFileForWrite(fileToSaveAs);
		}
		else
			saveToFileAs();
	   }
	
	public static void saveToFileAs()
	   {
		
		fc = new JFileChooser();
		
		 int returnVal = fc.showSaveDialog(null);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
             File file = fc.getSelectedFile();
           
             fileToSaveAs = file.getName();
             JOptionPane.showMessageDialog(null, "Accounts saved to " + file.getName());
         } else {
             JOptionPane.showMessageDialog(null, "Save cancelled by user");
         }
        
     	    // NEW CODE using the file handling class
			 if(fc.getSelectedFile()==null){
				 JOptionPane.showMessageDialog(null, "Cancelled");
			 }
			 else
				 fileHandling.openFileForWrite(fc.getSelectedFile());
	   }

	
	public static void readRecords()
	   {
		   //NEW CODE
			for(BankAccount ba:fileHandling.readRecords()) {
				Integer key = Integer.valueOf(ba.getAccountNumber().trim());

				int hash = (key % TABLE_SIZE);


				while (table.containsKey(hash)) {

					hash = hash + 1;
				}

				table.put(hash, ba);
			}

	   }
	
	public static void saveToFile(){
		//NEW CODE
	  for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
		   fileHandling.saveToFile(entry.getValue());
	  }
	}

	public static void writeFile(){
		openFileWrite();
		saveToFile();
		//addRecords();
		fileHandling.closeFile();
	}
	
	public static void saveFileAs(){
		saveToFileAs();
		saveToFile();	
		fileHandling.closeFile();
	}
	
	public static void readFile(){
	    openFileRead();
	    readRecords();
	    fileHandling.closeFile();
	}
	
	public void put(int key, BankAccount value){
		int hash = (key%TABLE_SIZE);
	
		while(table.containsKey(key)){
			hash = hash+1;
		
		}
		table.put(hash, value);

	}
	
	public static void main(String[] args) {
		BankApplication ba = new BankApplication();
		ba.setSize(1200,400);
		ba.pack();
		ba.setVisible(true);
	}
	
	
}




/*
The task for your second assignment is to construct a system that will allow users to define a data structure representing a collection of records that can be displayed both by means of a dialog that can be scrolled through and by means of a table to give an overall view of the collection contents. 
The user should be able to carry out tasks such as adding records to the collection, modifying the contents of records, and deleting records from the collection, as well as being able to save and retrieve the contents of the collection to and from external random access files.
The records in the collection will represent bank account records with the following fields:

AccountID (this will be an integer unique to a particular account and 
will be automatically generated when a new account record is created)

AccountNumber (this will be a string of eight digits and should also 
be unique - you will need to check for this when creating a new record)

Surname (this will be a string of length 20)

FirstName (this will be a string of length 20)

AccountType (this will have two possible options - "Current " and 
"Deposit" - and again will be selected from a drop down list when 
entering a record)

Balance (this will a real number which will be initialised to 0.0 
and can be increased or decreased by means of transactions)

Overdraft (this will be a real number which will be initialised 
to 0.0 but can be updated by means of a dialog - it only applies 
to current accounts)

You may consider whether you might need more than one class to deal with bank accounts.
The system should be menu-driven, with the following menu options:

Navigate: First, Last, Next, Previous, Find By Account Number 
(allows you to find a record by account number entered via a 
dialog box), Find By Surname (allows you to find a record by 
surname entered via a dialog box),List All (displays the 
contents of the collection as a dialog containing a JTable)

Records: Create, Modify, Delete, Set Overdraft (this should 
use a dialog to allow you to set or update the overdraft for 
a current account), Set Interest Rate (this should allow you 
to set the interest rate for deposit accounts by means of a 
dialog)

Transactions: Deposit, Withdraw (these should use dialogs which
allow you to specify an account number and the amount to withdraw
or deposit, and should check that a withdrawal would not cause
the overdraft limit for a current account to be exceeded, or be 
greater than the balance in a deposit account, before the balance 
is updated), Calculate Interest (this calculates the interest rate 
for all deposit accounts and updates the balances)

File: Open, Save, Save As (these should use JFileChooser dialogs. 
The random access file should be able to hold 25 records. The position 
in which a record is stored and retrieved will be determined by its account 
number by means of a hashing procedure, with a standard method being used when 
dealing with possible hash collisions)

Exit Application (this should make sure that the collection is saved - or that 
the user is given the opportunity to save the collection - before the application closes)

When presenting the results in a JTable for the List All option, the records should be sortable on all fields, but not editable (changing the records or adding and deleting records can only be done through the main dialog).
For all menu options in your program, you should perform whatever validation, error-checking and exception-handling you consider to be necessary.
The programs Person.java and PersonApplication.java (from OOSD2) and TableDemo.java may be of use to you in constructing your interfaces. The set of Java programs used to create, edit and modify random access files will also provide you with a basis for your submission.

*/