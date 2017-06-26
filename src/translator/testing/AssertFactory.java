package translator.testing;

import java.util.ArrayList;
import java.util.Arrays;

import translator.main.CLAEngine;
import translator.main.FuncMaster;

public class AssertFactory { 
	
	public static void Assert( String []args )
	{
		CLAEngine eng = new CLAEngine( args );
		if( eng.checkArg("--assert-funcs") )
		{				
			try {
				assert callFunc( new String[]{"1"}, new String[]{"a","b","c"}, "C" ).equals("a"): "C() assert failed";
				assert callFunc( new String[]{"4"}, new String[]{"a","b","c"}, "C" ).equals("a"): "C() assert failed";
				
				assert callFunc( new String[]{"0", "3"}, new String[]{"aa","bb","c"}, "FC" ).equals("aab"): "FC() assert failed";
				assert callFunc( new String[]{"0", "30"}, new String[]{"aa","bb","c"}, "FC" ).equals("aabbc"): "FC() assert failed";
				
				assert callFunc( new String[]{"a", "Unanet"}, new String[]{}, "SEARCH" ).equals("2"): "SEARCH() assert failed";
				
				assert callFunc( new String[]{"a", "Unanet"}, new String[]{}, "CONCAT" ).equals("aUnanet"): "SEARCH() assert failed";
				
				assert callFunc( new String[]{"Unanet", "0", "4" }, new String[]{}, "SUBSTR" ).equals("Unan"): "SUBSTR() assert failed";
				assert callFunc( new String[]{"Unanet", "0", "40" }, new String[]{}, "SUBSTR" ).equals("Unanet"): "SUBSTR() assert failed";

				assert callFunc( new String[]{"Seven" }, new String[]{}, "LENGTH" ).equals("5"): "LENGTH() assert failed";
				assert callFunc( new String[]{"" }, new String[]{}, "LENGTH" ).equals("0"): "LENGTH() assert failed";
				
				assert callFunc( new String[]{"4*2+(15- 4^2)", "2" }, new String[]{}, "MATH" ).equals("7.0"): "MATH() assert failed";

				assert callFunc( new String[]{"2", "Unanet"}, new String[]{}, "RIGHT" ).equals("et"): "RIGHT() assert failed";
				assert callFunc( new String[]{"2", "Unanet"}, new String[]{}, "LEFT" ).equals("Un"): "LEFT() assert failed";
				
				assert callFunc( new String[]{"22"}, new String[]{}, "SEQ" ).equals("22"): "SEQ() assert failed";
				
				assert callFunc( new String[]{"LOWER"}, new String[]{}, "LOWER" ).equals("lower"): "LOWER() assert failed";
				assert callFunc( new String[]{"upper"}, new String[]{}, "UPPER" ).equals("UPPER"): "UPPER() assert failed";

				assert callFunc( new String[]{"123"}, new String[]{}, "ISNUMERIC" ).equals("true"): "ISNUMERIC() assert failed";
				assert callFunc( new String[]{"abc"}, new String[]{}, "ISNUMERIC" ).equals("false"): "ISNUMERIC() assert failed";
				
				assert callFunc( new String[]{"    123"}, new String[]{}, "LTRIM" ).equals("123"): "LTRIM() assert failed";
				assert callFunc( new String[]{"123    "}, new String[]{}, "RTRIM" ).equals("123"): "RTRIM() assert failed";
				assert callFunc( new String[]{"  123  "}, new String[]{},  "TRIM" ).equals("123"): "LTRIM() assert failed";

			}
			catch( AssertionError as )
			{
				System.err.println( "Function assertion failed:");
				System.err.println( as.getMessage() );
				System.err.println( Arrays.toString(as.getStackTrace()) );
			}
		}
		
		System.out.println("\nAsserts completed.");
		System.exit(0);
	}
	
	public static String callFunc( String []params, String []data, String funcName )
	{
		ArrayList<String> tempParams = new ArrayList<>(Arrays.asList(params));
		ArrayList<String> tempData = new ArrayList<>(Arrays.asList(data));
		
		//DJ JazzyJeff THE FUNC MASTAH
		return new FuncMaster().CallFunc(tempData, tempParams, funcName);
	}
	
}
