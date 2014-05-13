package pack;

import java.util.Calendar;

public class Week{
    public static void main(String[] args){
        Calendar cal = Calendar.getInstance();
        int i;

        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                i = 0;
                break;
            case Calendar.MONDAY:
                i = 1;
                break;
            case Calendar.TUESDAY:
                i = 2;
                break;
            case Calendar.WEDNESDAY:
                i = 3;
                break;
            case Calendar.THURSDAY:
                i = 4;
                break;
            case Calendar.FRIDAY:
                i = 5;
                break;
            default: 
                i = 6;
                break;
        }

        System.out.println(i);
    }
}
