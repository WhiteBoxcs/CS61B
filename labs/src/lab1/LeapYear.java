package lab1;

public class LeapYear {

	/**
	 * Determiens whether not a given year is al eap year.
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year){
		return year % 400 == 0 
				|| year % 4 == 0 && year % 100 != 0;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int year = 2000;
		
		if(isLeapYear(year))
			System.out.println(year + " is a leap year." );
		else
			System.out.println(year + " is not a leap year." );
		
	}

}
