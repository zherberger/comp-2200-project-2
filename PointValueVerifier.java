import javax.swing.*;

public class PointValueVerifier extends InputVerifier
{
	public boolean verify(JComponent input)
	{
		String inputString;
		int pointValue;
		boolean isValid = true;
		
		inputString = ((JTextField) input).getText().trim();
		
		if(!inputString.equals("")) //if input string is not blank (or spaces)
		{
			try
			{
				pointValue = Integer.parseInt(inputString);
				
				if(pointValue < 0)
					throw new Exception();
			}
		
			catch(Exception e)
			{
				isValid = false;
			}
		}
		
		return isValid;
	}
}