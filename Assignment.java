import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.io.*;

public class Assignment
{
	String className;
	String assignmentName;
	Date assignedDate;
	Date dueDate;
	int pointValue;
	int percentComplete;
	String submissionMethod;
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	static Random myRandom = new Random();
	static int randomCount = 0;
	static final String[] assignmentNames = {"Homework", "Project", "Quiz", "Test"};
	static final String[] submissionMethods = {"In Class", "Blackboard", "Demonstration", "E-Mail"};
	
	/*-------------------Constructors---------------------*/
	
	Assignment(String className, String assignmentName, Date assignedDate, Date dueDate, int pointValue, int percentComplete, String submissionMethod)
	{
		this.className = className;
		this.assignmentName = assignmentName;
		this.assignedDate = assignedDate;
		this.dueDate = dueDate;
		this.pointValue = pointValue;
		this.percentComplete = percentComplete;
		this.submissionMethod = submissionMethod;
	}
	
	Assignment(DataInputStream dis) throws IOException
	{
		className = dis.readUTF();
		assignmentName = dis.readUTF();
		assignedDate = new Date(dis.readLong());
		dueDate = new Date(dis.readLong());
		pointValue = dis.readInt();
		percentComplete = dis.readInt();
		submissionMethod = dis.readUTF();
	}
	
	void writeTo(DataOutputStream dos) throws IOException
	{
		dos.writeUTF(className);
		dos.writeUTF(assignmentName);
		dos.writeLong(assignedDate.getTime());
		dos.writeLong(dueDate.getTime());
		dos.writeInt(pointValue);
		dos.writeInt(percentComplete);
		dos.writeUTF(submissionMethod);
	}
	
	static Assignment getRandom()
	{
		int pv; //point value
		Date today;
		today = new Date();
		
		pv = 10 + 5 * myRandom.nextInt(15); //between 10 and 100
		
		return new Assignment
			("Class " + ++randomCount,
			assignmentNames[myRandom.nextInt(assignmentNames.length)] + " " + randomCount,
		    today,
		    new Date(today.getTime() + (3 * 24 * 3600 * 1000) + (24 * 3600 * 1000) * myRandom.nextInt(12)),
		    pv,
		    (int)(Math.round(100 * myRandom.nextFloat())),
		    submissionMethods[myRandom.nextInt(submissionMethods.length)]);
	}
	
	String[] getFields()
	{
		return new String[]{className, assignmentName, sdf.format(assignedDate), sdf.format(dueDate), Integer.toString(pointValue), Integer.toString(percentComplete)};
	}
	
	void setPercentComplete(int percent)
	{
		percentComplete = percent;
	}
	
	int getSubmissionMethodIndex()
	{
		int index;
		
		if(submissionMethod.equals("In Class"))
			index = 0;
		
		else if(submissionMethod.equals("Blackboard"))
			index = 1;
		
		else if(submissionMethod.equals("Demonstration"))
			index = 2;
		
		else index = 3;
		
		return index;
	}
	
	@Override
	public String toString()
	{
		return String.format("%-20s %-20s %-15s %-15s %5d %5d     %-20s", className, assignmentName, sdf.format(assignedDate), sdf.format(dueDate), pointValue, percentComplete, submissionMethod);
	}
}