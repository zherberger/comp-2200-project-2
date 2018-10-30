import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.text.*;

public class AddEditDialog extends JDialog implements ActionListener
{
	JTextField classNameField;
	JTextField assignmentNameField;
	JTextField assignedDateField;
	JTextField dueDateField;
	JTextField pointValueField;
	JSlider percentCompleteSlider;
	JComboBox submissionMethodBox;
	DataManager dm;
	Assignment assignmentToEdit;
	Assignment newAssignment;
	JTextField[] fields;
	String errorMessages = "";
	int originalIndex = -1;
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	final String[] submissionMethods = {"In Class", "Blackboard", "Demonstration", "E-Mail"};
	
	AddEditDialog(DataManager dm)
	{
		assignmentToEdit = null;
		this.dm = dm;
		setupFields();
		setupDialog(40, 35, "Add Assignment");
	}
	
	AddEditDialog(DataManager dm, int originalIndex, Assignment assignmentToEdit)
	{
		this.assignmentToEdit = assignmentToEdit;
		this.dm = dm;
		this.originalIndex = originalIndex;
		setupFields();
		populateFields(assignmentToEdit);
		setupDialog(40, 35, "Edit Assignment");
	}
	
	void setupFields()
	{
		JPanel buttonPanel;
		JPanel fieldPanel;
		GroupLayout fieldLayout;
		
		buttonPanel = new JPanel(new GridLayout(1, 4));
		JButton addButton = newButton("Add assignment", "ADD", this, true);
		JButton updateButton = newButton("Update assignment", "UPDATE", this, true);
		JButton randomButton = newButton("Random assignment", "RANDOM", this, true);
		JButton resetButton = newButton("Reset values", "RESET", this, true);
		JButton cancelButton = newButton("Cancel", "CANCEL", this, true);
		
		if(assignmentToEdit == null)
			buttonPanel.add(addButton);
		
		else
			buttonPanel.add(updateButton);
		
		buttonPanel.add(randomButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(cancelButton);
		
		JLabel classNameLabel = new JLabel("Class name: ");
		JLabel assignmentNameLabel = new JLabel("Assignment name: ");
		JLabel assignedDateLabel = new JLabel("Assigned date: ");
		JLabel dueDateLabel = new JLabel("Due date: ");
		JLabel pointValueLabel = new JLabel("Point value: ");
		JLabel percentCompleteLabel = new JLabel("Percent complete: ");
		JLabel submissionMethodLabel = new JLabel("Submission method: ");
		
		classNameField = new JTextField();
		assignmentNameField = new JTextField();
		assignedDateField = new JTextField();
		dueDateField = new JTextField();
		pointValueField = new JTextField();
		percentCompleteSlider = new JSlider(0, 100, 0);
		submissionMethodBox = new JComboBox<String>(submissionMethods);
		
		assignedDateField.setInputVerifier(new DateVerifier());
		dueDateField.setInputVerifier(new DateVerifier());
		pointValueField.setInputVerifier(new PointValueVerifier());
		
		fieldPanel = new JPanel();
		fieldLayout = new GroupLayout(fieldPanel);
		fieldPanel.setLayout(fieldLayout);
		fieldLayout.setAutoCreateGaps(true);
		fieldLayout.setAutoCreateContainerGaps(true);
		
		fieldLayout.setHorizontalGroup(
			fieldLayout.createSequentialGroup()
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(classNameLabel)
					.addComponent(assignmentNameLabel)
					.addComponent(assignedDateLabel)
					.addComponent(dueDateLabel)
					.addComponent(pointValueLabel)
					.addComponent(percentCompleteLabel)
					.addComponent(submissionMethodLabel))
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(classNameField)
					.addComponent(assignmentNameField)
					.addComponent(assignedDateField)
					.addComponent(dueDateField)
					.addComponent(pointValueField)
					.addComponent(percentCompleteSlider)
					.addComponent(submissionMethodBox))
		);
				
		fieldLayout.setVerticalGroup(
			fieldLayout.createSequentialGroup()
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(classNameLabel)
					.addComponent(classNameField))
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(assignmentNameLabel)
					.addComponent(assignmentNameField))
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(assignedDateLabel)
					.addComponent(assignedDateField))
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(dueDateLabel)
					.addComponent(dueDateField))
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(pointValueLabel)
					.addComponent(pointValueField))
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(percentCompleteLabel)
					.addComponent(percentCompleteSlider))
				.addGroup(fieldLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(submissionMethodLabel)
					.addComponent(submissionMethodBox))
		);
		
		add(fieldPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		fields = new JTextField[]{classNameField, assignmentNameField, assignedDateField, dueDateField, pointValueField}; //convenience factor
	}
	
	void populateFields(Assignment a)
	{
		String[] assignmentFields;
		int boxIndex;
		
		if(a == null)
		{
			for(JTextField field : fields)
				field.setText("");
		}
		
		else
		{
			assignmentFields = a.getFields();
		
			for(int i = 0; i < 5; i++)
				fields[i].setText(assignmentFields[i]);
			
			try
			{
				percentCompleteSlider.setValue(Integer.parseInt(assignmentFields[5]));
			}
			
			catch(NumberFormatException e)
			{
				e.printStackTrace(); //just to be safe
			}
			
			submissionMethodBox.setSelectedIndex(a.getSubmissionMethodIndex());
		}
	}
	
	void setupDialog(int xScreenPercentage, int yScreenPercentage, String title)
	{
		Toolkit tk;
		Dimension d;
		
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		
		setSize(xScreenPercentage * d.width/100, yScreenPercentage * d.height/100);
		setLocation((100 - xScreenPercentage) * d.width/200,
				   (100 - yScreenPercentage) * d.height/200);
		
		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}
	
	JButton newButton(String label, String actionCommand, ActionListener buttonListener, boolean startsEnabled)
	{
		JButton b;
		
		b = new JButton(label);
		b.setActionCommand(actionCommand);
		b.addActionListener(buttonListener);
		b.setEnabled(startsEnabled);
		
		return b;
	}
	
//---------------------------------------------------------//
//---------------------ActionListener----------------------//
//---------------------------------------------------------//

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if(command.equals("ADD"))
		{
			createAssignment();
			
			if(newAssignment != null)
			{
				dm.addAssignment(newAssignment);
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			}
		}
		
		else if(command.equals("UPDATE"))
		{
			createAssignment();
			
			if(newAssignment != null)
			{
				dm.replaceAssignment(newAssignment, originalIndex);
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			}
		}
		
		else if(command.equals("RANDOM"))
		{
			populateFields(Assignment.getRandom());
		}
		
		else if(command.equals("RESET"))
		{
			populateFields(assignmentToEdit);
		}
		
		else if(command.equals("CANCEL"))
		{
			exit();
		}
	}
	
	void createAssignment()
	{
		Assignment a;
		int pointValue;
		int percentComplete;
		Date assignedDate;
		Date dueDate;
		boolean isValid = true;
		
		for(JTextField field : fields)
		{
			if(field.getText().trim().equals(""))
			{
				isValid = false;
			}
		}
		
		if(!isValid)
		{
			errorMessages += "One or more fields were left blank.\n";
		}
		
		if(!datesInOrder())
		{
			isValid = false;
			errorMessages += "Due date must be after assigned date.\n";
		}
		
		if(isValid)
		{
			pointValue = Integer.parseInt(pointValueField.getText().trim());
			assignedDate = parseDate(assignedDateField.getText().trim());
			dueDate = parseDate(dueDateField.getText().trim());
		
			newAssignment = new Assignment(classNameField.getText().trim(),
										assignmentNameField.getText().trim(),
										assignedDate,
										dueDate,
										pointValue,
										percentCompleteSlider.getValue(),
										(String) (submissionMethodBox.getSelectedItem()));
		}
		
		else
		{
			JOptionPane.showMessageDialog(this, errorMessages, "Could not add/edit assignment", JOptionPane.ERROR_MESSAGE);
			errorMessages = "";
			newAssignment = null;
		}
	}
	
	boolean datesInOrder()
	{
		Date assignedDate;
		Date dueDate;
		boolean inOrder;
		
		assignedDate = parseDate(assignedDateField.getText().trim());
		dueDate = parseDate(dueDateField.getText().trim());
		
		if(dueDate.getTime() < assignedDate.getTime())
			inOrder = false;
		
		else inOrder = true;
		
		return inOrder;
	}
	
	Date parseDate(String dateString)
	{
		Date date;
		
		try
		{
			date = sdf.parse(dateString);
		}
		
		catch(ParseException p)
		{
			//This should have been handled in DateVerifier.
			date = new Date(0);
		}
		
		return date;
	}

	void exit()
	{
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}