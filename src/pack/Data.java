package pack;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Calendar;

public class Data{
    protected Calendar calendar;
    protected ArrayList<Subject> subjects;

    public Data(String line){
        this.calendar = Calendar.getInstance();
        this.subjects = new ArrayList<Subject>();
        
        Scanner sc = new Scanner(line);
        String d = sc.next();
        this.calendar.set(Calendar.YEAR, Integer.parseInt(d.substring(0, 4)));
        this.calendar.set(Calendar.MONTH, Integer.parseInt(d.substring(4, 6)) - 1);
        this.calendar.set(Calendar.DATE, Integer.parseInt(d.substring(6, 8)));

        while(sc.hasNext()){
            String t = sc.next();
            int a = sc.nextInt();
            int r = sc.nextInt();
            this.subjects.add(new Subject(t, a, r));
        }
    }

    public Data(Calendar c, ArrayList<Subject> s){
        this.calendar = c;
        this.subjects = s;
    }

    public Calendar getCalendar(){
        return this.calendar;
    }

    public void arrangeAttribute(ArrayList<Subject> subs){
        for(int i = 0; i < this.subjects.size(); i++){
            Subject subject = this.subjects.get(i);
            boolean flag = false;
            
            for(int j = 0; j < subs.size(); j++){
                Subject sub = subs.get(j);
                
                if(subject.title.equals(sub.title)){
                    flag = true;
                    subject.assignment = sub.assignment;
                }
            }
            if(! flag){
                this.subjects.remove(i);
                i--;
            }
        }

        for(int i = 0; i < subs.size(); i++){
            Subject sub = subs.get(i);
            boolean flag = false;
            
            for(int j = 0; j < this.subjects.size(); j++){
                Subject subject = this.subjects.get(j);

                if(sub.title.equals(subject.title)){
                    flag = true;
                }
            }

            if(! flag){
                this.subjects.add(sub);
            }
        }
    }

    public String encode(){
        int year = this.calendar.get(Calendar.YEAR);
        int month = this.calendar.get(Calendar.MONTH) + 1;
        int day = this.calendar.get(Calendar.DATE);

        String s = String.format("%d%02d%02d", year, month, day);

        for(int i = 0; i < this.subjects.size(); i++){
            Subject sub = this.subjects.get(i);
            s = s + " " + String.format("%s %d %d", sub.title, sub.assignment, sub.result);
        }

        return s;
    }

    public ArrayList<Subject> getSubjects(){
        return this.subjects;
    }
}
