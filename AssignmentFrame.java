import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.Properties;
import java.awt.datatransfer.*;

public class AssignmentFrame extends JFrame implements ActionListener, ListSelectionListener, MouseListener, DropTargetListener, WindowListener
{
	JList<Assignment> assignmentListBox;
	AssignmentList assignments;
	JScrollPane myScrollPane;
	File assignmentFile;
	JButton saveButton;
	JButton editButton;
	JButton deleteButton;
	JMenuBar menuBar;
	JMenuItem saveItem;
	JMenuItem editItem;
	JMenuItem deleteItem;
	JMenuItem editPopupMenuItem;
	JMenuItem deletePopupMenuItem;
	Properties properties;
	DropTarget dropTarget;
	
//---------------------------------------------------------//
//----------------------Constructor------------------------//
//---------------------------------------------------------//
	
	AssignmentFrame()
	{
		JPanel buttonPanel;
		
		buttonPanel = new JPanel(new GridLayout(1, 6));

		saveButton = newButton("Save", "SAVE", this, false);
		editButton = newButton("Edit", "EDIT", this, false);
		deleteButton = newButton("Delete", "DELETE", this, false);
		buttonPanel.add(saveButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(newButton("Open", "OPEN", this, true));
		buttonPanel.add(newButton("New", "NEW", this, true));
		buttonPanel.add(newButton("Exit", "EXIT", this, true));
		add(buttonPanel, BorderLayout.SOUTH);
		
		assignments = new AssignmentList();
		assignmentListBox = new JList<Assignment>(assignments);
		assignmentListBox.setFont(new Font("monospaced", Font.PLAIN,10));
		assignmentListBox.addListSelectionListener(this);
		assignmentListBox.addMouseListener(this);
		myScrollPane = new JScrollPane(assignmentListBox);
		add(myScrollPane, BorderLayout.CENTER);
		
		addWindowListener(this); //want JFrame to receive WindowEvents from itself, i.e. closing
		dropTarget = new DropTarget(myScrollPane, this);
		
		menuBar = newMenuBar();
		setJMenuBar(menuBar);
		setupMainFrame(50, 50, "Assignments");
		loadProperties();
	}
	
//---------------------------------------------------------//
//--------------Constructor helper methods-----------------//
//---------------------------------------------------------//
	
	JButton newButton(String label, String actionCommand, ActionListener buttonListener, boolean startsEnabled)
	{
		JButton b;
		
		b = new JButton(label);
		b.setActionCommand(actionCommand);
		b.addActionListener(buttonListener);
		b.setEnabled(startsEnabled);
		
		return b;
	}
	
	JMenuBar newMenuBar()
	{
		JMenuBar menuBar;
		JMenu subMenu;
		
		menuBar = new JMenuBar();
		subMenu = new JMenu("File", true);
		subMenu.setMnemonic('F');
		subMenu.add(newItem("Open...", "OPEN", this, KeyEvent.VK_O, KeyEvent.VK_O, "Open an existing file.", true));
		saveItem = newItem("Save", "SAVE", this, KeyEvent.VK_S, KeyEvent.VK_S, "Save data to disk.", false);
		subMenu.add(saveItem);
		subMenu.add(newItem("Save As...", "SAVEAS", this, KeyEvent.VK_A, KeyEvent.VK_A, "Save data to disk under the specified name.", true));
		menuBar.add(subMenu);
		
		subMenu = new JMenu("Assignment", true);
		subMenu.setMnemonic('A');
		subMenu.add(newItem("New", "NEW", this, KeyEvent.VK_N, KeyEvent.VK_N, "Add new assignment to the list.", true));
		editItem = newItem("Edit Selected", "EDIT", this, KeyEvent.VK_E, KeyEvent.VK_E, "Edit selected assignment.", false);
		subMenu.add(editItem);
		deleteItem = newItem("Delete Selected", "DELETE", this, KeyEvent.VK_D, KeyEvent.VK_D, "Delete selected assignment.", false);
		subMenu.add(deleteItem);
		menuBar.add(subMenu);
		
		subMenu = new JMenu("Settings", true);
		subMenu.setMnemonic('S');
		subMenu.add(newItem("Set Foreground Color", "FGCOLOR", this, KeyEvent.VK_F, KeyEvent.VK_F, "Change application foreground color.", true));
		subMenu.add(newItem("Set Background Color", "BGCOLOR", this, KeyEvent.VK_B, KeyEvent.VK_B, "Change application background color.", true));
		menuBar.add(subMenu);
		
		return menuBar;
	}
	
	JMenuItem newItem(String label, String actionCommand, ActionListener menuListener, int mnemonic, int keyCode, String toolTipText, boolean startsEnabled)
	{
		JMenuItem m;
		
		m = new JMenuItem(label, mnemonic);
		m.setAccelerator(KeyStroke.getKeyStroke(keyCode, KeyEvent.ALT_DOWN_MASK));
		m.setToolTipText(toolTipText);
		m.setActionCommand(actionCommand);
		m.addActionListener(menuListener);
		m.setEnabled(startsEnabled);
		
		return m;
	}
	
	void setupMainFrame(int xScreenPercentage, int yScreenPercentage, String title)
	{
		Toolkit tk; //abstract, does hardware stuff
		Dimension d;
		
		tk = Toolkit.getDefaultToolkit(); //returns a concrete subclass of Toolkit
		d = tk.getScreenSize();
		
		setSize(xScreenPercentage * d.width/100, yScreenPercentage * d.height/100);
		setLocation((100 - xScreenPercentage) * d.width/200,
				   (100 - yScreenPercentage) * d.height/200);
		
		setTitle(title);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
	
	void loadProperties()
	{
		FileInputStream fis;
		File propertiesFile = new File("properties.txt");
		properties = new Properties();
		
		try
		{
			fis = new FileInputStream(propertiesFile); //might not exist. If this throws a FileNotFoundException, we'll create a file in the catch.
			properties.load(fis);
			
			assignmentListBox.setForeground(new Color(Integer.parseInt(properties.getProperty("foreground"))));
			assignmentListBox.setBackground(new Color(Integer.parseInt(properties.getProperty("background")))); //messy
		}
		
		catch(FileNotFoundException e)
		{
			FileOutputStream fos;
			
			try
			{
				propertiesFile.createNewFile();
				properties.setProperty("foreground", Integer.toString(assignmentListBox.getForeground().getRGB()));
				properties.setProperty("background", Integer.toString(assignmentListBox.getBackground().getRGB()));
				properties.setProperty("path", ""); //no path initially
				fos = new FileOutputStream(propertiesFile);
				properties.store(fos, "Properties for the AssignmentFrame.");
			}
			
			catch(IOException i)
			{
				System.out.println("Error creating properties file.");
				i.printStackTrace();
			}
		}
		
		catch(IOException e)
		{
			System.out.println("Error loading properties from file.");
			e.printStackTrace();
		}
	}
	
//---------------------------------------------------------//
//---------------------ActionListener----------------------//
//---------------------------------------------------------//

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if(command == "OPEN")
		{
			JFileChooser fc;
			int choice;
			
			if(confirmOperation("There are unsaved changes to one or more assignments. Do you want to open a new file?"))
			{
				fc = new JFileChooser();
				fc.setCurrentDirectory(new File(properties.getProperty("path")));
				choice = fc.showOpenDialog(this);
			
				if(choice == JFileChooser.APPROVE_OPTION)
					open(fc.getSelectedFile());
			}
		}
		
		else if(command == "SAVE")
		{
			DataOutputStream dos;
			
			if(properties.getProperty("path").equals(""))
				saveAs();
			
			else
				save();
		}
		
		else if(command == "SAVEAS")
		{
			saveAs();
		}
		
		else if(command == "NEW")
		{
			new AddEditDialog(assignments);
		}
		
		else if(command == "EDIT")
		{
			int selectedIndex = assignmentListBox.getSelectedIndex();
			new AddEditDialog(assignments, selectedIndex, assignments.get(selectedIndex));
		}
		
		else if(command == "DELETE")
		{
			int[] deleteIndices = assignmentListBox.getSelectedIndices();
			
			for(int i = deleteIndices.length - 1; i >= 0; i--) //gotta remove elements in reverse order. Otherwise, indices change!
				assignments.remove(deleteIndices[i]);
		}
		
		else if(command == "FGCOLOR")
		{
			Color fgColor;
			
			fgColor = getColor("Set Foreground Color", assignmentListBox.getForeground());
			assignmentListBox.setForeground(fgColor);
			properties.setProperty("foreground", Integer.toString(fgColor.getRGB()));
		}
		
		else if(command == "BGCOLOR")
		{
			Color bgColor;
			
			bgColor = getColor("Set Background Color", assignmentListBox.getBackground());
			assignmentListBox.setBackground(bgColor);
			properties.setProperty("background", Integer.toString(bgColor.getRGB()));
		}
		
		else if(command == "MARK100")
		{
			assignments.get(assignmentListBox.getSelectedIndex()).setPercentComplete(100);
		}
		
		else if(command == "EXIT")
		{
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		saveItem.setEnabled(assignments.needsSaved());
		saveButton.setEnabled(assignments.needsSaved());
	}

//---------------------------------------------------------//
//--------------ActionListener helper methods--------------//
//---------------------------------------------------------//

	void open(File inFile)
	{
		DataInputStream dis;
		
		try
		{
			dis = new DataInputStream(new FileInputStream(inFile));
			assignments = new AssignmentList(dis);
			assignmentListBox.setModel(assignments);
			properties.setProperty("path", inFile.getCanonicalPath());
		}
		
		catch(IOException e)
		{
			if(e.getMessage().equals("Unsupported file type."))
				System.out.println(e.getMessage());
			
			else
			{
				System.out.println("Error opening file.");
				e.printStackTrace();
			}
		}
	}
	
	void save()
	{
		DataOutputStream dos;
		
		try
		{
			dos = new DataOutputStream(new FileOutputStream(properties.getProperty("path")));
			assignments.writeTo(dos);
		}
		
		catch(IOException i)
		{
			System.out.println("Error writing to file.");
			i.printStackTrace();
		}
	}
	
	void saveAs()
	{
		JFileChooser fc;
		int choice;
		
		fc = new JFileChooser();
		choice = fc.showSaveDialog(this);
		
		if(choice == JFileChooser.APPROVE_OPTION)
		{
			properties.setProperty("path", fc.getSelectedFile().getName());
			save();
		}
	}
	
	Color getColor(String title, Color initialColor)
	{
		Color newColor;
		
		try
		{
			newColor = JColorChooser.showDialog(this, title, initialColor);
			
			if(newColor == null)
				throw new Exception();
		}
		
		catch(HeadlessException h)
		{
			System.out.println("Ah! It's the headless exception!");
			h.printStackTrace();
			
			newColor = initialColor;
		}
		
		catch(Exception e)
		{
			newColor = initialColor;
		}
		
		return newColor;
	}
	
	boolean confirmOperation(String message)
	{
		int response;
		boolean confirm = true;
		
		if(assignments.needsSaved())
		{
			response = JOptionPane.showConfirmDialog(this, message, "Unsaved changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			
			if(response != JOptionPane.YES_OPTION)
				confirm = false;
		}
		
		return confirm;
	}
	
	void exit()
	{
		FileOutputStream fos;
		
		properties.setProperty("path", ""); //go back to having no default path
		
		try
			{
				fos = new FileOutputStream(new File("properties.txt"));
				properties.store(fos, "Properties for the AssignmentFrame.");
			}
			
		catch(IOException i)
			{
				System.out.println("Error writing properties file.");
				i.printStackTrace();
			}
			
		System.exit(0);
	}
	
//---------------------------------------------------------//
//-----------------ListSelectionListener-------------------//
//---------------------------------------------------------//
	
	public void valueChanged(ListSelectionEvent e)
	{
		int numSelected = assignmentListBox.getSelectedIndices().length;
		
		editButton.setEnabled(numSelected == 1);
		editItem.setEnabled(numSelected == 1);
		deleteButton.setEnabled(numSelected > 0);
		deleteItem.setEnabled(numSelected > 0);
	}
	
//---------------------------------------------------------//
//---------------------MouseListener-----------------------//
//---------------------------------------------------------//
	
	public void mouseClicked(MouseEvent e)
	{
		JPopupMenu popupMenu;
		
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			assignmentListBox.setSelectedIndex(assignmentListBox.locationToIndex(e.getPoint()));
			popupMenu = new JPopupMenu();
			editPopupMenuItem = newItem("Edit Selected", "EDIT", this, KeyEvent.VK_E, KeyEvent.VK_E, "Edit selected assignment.", true);
			deletePopupMenuItem = newItem("Delete Selected", "DELETE", this, KeyEvent.VK_D, KeyEvent.VK_D, "Delete selected assignment.", true);
			popupMenu.add(editPopupMenuItem);
			popupMenu.add(deletePopupMenuItem);
			popupMenu.add(newItem("Mark 100% Complete", "MARK100", this, KeyEvent.VK_M, KeyEvent.VK_M, "", true));
			popupMenu.show(assignmentListBox, e.getX(), e.getY());
		}
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){} //unused MouseListener methods
	
//---------------------------------------------------------//
//-------------------DropTargetListener--------------------//
//---------------------------------------------------------//
	
	public void drop(DropTargetDropEvent dtde)
	{
		java.util.List<File> fileList;
		Transferable transferableData;
		DataInputStream dis;
		
		
		if(confirmOperation("There are unsaved changes to one or more assignments. Do you want to open a new file?"))
		{
			transferableData = dtde.getTransferable();
			
			try
			{
				if(transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));
					
					if(fileList.size() == 1)
						open(fileList.get(0));
				}
			}
		
			catch(UnsupportedFlavorException e)
			{
				System.out.println("Unsupported flavor found!");
				e.printStackTrace();
			}
			
			catch(IOException e)
			{
				System.out.println("Could not get transferable data.");
				e.printStackTrace();
			}
		}
	}
	
	public void dragEnter(DropTargetDragEvent dtde){}
	public void dragExit(DropTargetEvent dte){}
	public void dragOver(DropTargetDragEvent dtde){}
	public void dropActionChanged(DropTargetDragEvent dtde){} //unused DropTargetListener methods
	
//---------------------------------------------------------//
//---------------------WindowListener----------------------//
//---------------------------------------------------------//
	
	public void windowClosing(WindowEvent e)
	{
		int response;
		
		if(confirmOperation("There are unsaved changes to one or more assignments. Do you want to exit?"))
			exit();
	}
	
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){} //unused WindowListener methods
}