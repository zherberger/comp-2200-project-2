import javax.swing.*;
import java.util.Date;
import java.text.*;

public class DateVerifier extends InputVerifier
{
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
	public boolean verify(JComponent input)
	{
		String inputString;
		Date date;
		boolean isValid = true;
		
		inputString = ((JTextField) input).getText().trim();
		
		if(!inputString.equals("")) //if input string is not blank (or spaces)
		{
			try
			{
				date = sdf.parse(inputString);
			}
			
			catch(ParseException pe)
			{
				isValid = false;
			}
		}
		
		return isValid;
	}
}