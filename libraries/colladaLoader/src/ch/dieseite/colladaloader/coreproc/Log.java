package ch.dieseite.colladaloader.coreproc;

/**
* <p>This source is free; you can redistribute it and/or modify it under
* the terms of the GNU General Public License and by nameing of the originally author</p>
*
* @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
* @version 3.0
*/
public class Log {
	
	
	/**
	 * 4 = extreme details 3 = details, 2 = medium, 1 = abstract , 0 = none
	 */
	public static int level =0; 
	
	/**
	 * Print Out messages from classes to console
	 * @param message
	 * @param abstractLevel 3 = max details, 2 = medium, 1 = abstract
	 */
	public static void msg(Class<?> from, String message, int abstractLevel)
	{
		if (abstractLevel <= level)
		{
			message = from.getSimpleName()+": "+message;
			for (int i = 0;i<abstractLevel;i++)
			{
				message = "\t"+message;
			}
			System.out.println(message);
		}
	}

}
