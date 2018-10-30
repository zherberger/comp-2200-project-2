import javax.swing.DefaultListModel;
import java.io.*;

public class AssignmentList extends DefaultListModel<Assignment> implements DataManager
{
	boolean needsSaved;
	final long signature = 1198425093013089107L;
	
	AssignmentList()
	{
		super();
		needsSaved = false;
	}
	
	AssignmentList(DataInputStream dis) throws IOException
	{
		if(dis.readLong() != signature)
			throw new IOException("Unsupported file type.");
		
		int numRecords = dis.readInt();
		
		for(int i = 0; i < numRecords; i++)
			addElement(new Assignment(dis));
		
		needsSaved = false;
	}
	
	void writeTo(DataOutputStream dos) throws IOException
	{
		int size = size();
		
		dos.writeLong(signature);
		dos.writeInt(size);
		
		for(int i = 0; i < size; i++)
			get(i).writeTo(dos);
		
		needsSaved = false;
	}
	
	boolean needsSaved()
	{
		return needsSaved;
	}
	
	@Override
	public void addElement(Assignment a)
	{
		super.addElement(a);
		needsSaved = true;
	}
	
	@Override
	public Assignment remove(int index)
	{
		needsSaved = true;
		return super.remove(index);
	}
	
/*----------------- Interface methods ----------------------*/

	public void addAssignment(Assignment assignment)
	{
		addElement(assignment);
		needsSaved = true;
	}
	
	public void replaceAssignment(Assignment assignment, int index)
	{
		set(index, assignment);
		needsSaved = true;
	}
}