package pack;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;

public class Timers{
    protected JFrame frame;
    protected BufferStrategy strategy;
    protected int mode;
    protected ArrayList<Data> histories;
    protected Subject target;
    protected long time;
    protected Data today_task;
    protected int h_index;
    
    public static void main(String[] args){
        new Timers();
    }

    public Timers(){
        this.frame = new JFrame("Timers");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(800, 600);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setResizable(false);

        this.frame.setIgnoreRepaint(true);
        this.frame.createBufferStrategy(2);
        this.strategy = this.frame.getBufferStrategy();

        this.mode = 0;

        Calendar today = Calendar.getInstance();

        this.histories = new ArrayList<Data>();
        ArrayList<Subject> subjects = new ArrayList<Subject>();

        try{
            FileReader fr = new FileReader("data/title.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;

            while((line = br.readLine()) != null && line.length() > 0){
                Scanner sc = new Scanner(line);

                String t = sc.next();
                int a = sc.nextInt();

                subjects.add(new Subject(t, a, 0));
            }

            br.close();
            fr.close();
        }catch(IOException e){
            System.err.println("Error: title.txt");
            System.exit(-1);
        }

        try{
            FileReader fr = new FileReader("data/data.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            
            while((line = br.readLine()) != null && line.length() > 0){
                this.histories.add(new Data(line));
            }

            br.close();
            fr.close();
        }catch(IOException e){
            System.err.println("Error: data.txt");
            System.exit(-1);
        }

        if(this.histories.size() > 0){
            Data data = this.histories.get(this.histories.size() - 1);
            Calendar calendar = data.getCalendar();

            if(this.isSameDay(calendar, today)){
                data.arrangeAttribute(subjects);
                this.today_task = data;
                this.histories.remove(this.histories.size() - 1);
            }else{
                Calendar it_cal = Calendar.getInstance();
                it_cal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                it_cal.add(Calendar.DATE, 1);
                
                while(! this.isSameDay(it_cal, today)){
                    ArrayList<Subject> n_subs = new ArrayList<Subject>();
                    
                    for(int i = 0; i < subjects.size(); i++){
                        Subject subject = subjects.get(i);

                        n_subs.add(new Subject(subject.title, subject.assignment, 0));
                    }

                    this.histories.add(new Data(it_cal, n_subs));

                    Calendar n_cal = Calendar.getInstance();
                    n_cal.set(it_cal.get(Calendar.YEAR), it_cal.get(Calendar.MONTH), it_cal.get(Calendar.DATE));
                    n_cal.add(Calendar.DATE, 1);
                    
                    it_cal = n_cal;
                }
                this.today_task = new Data(today, subjects);
            }
        }else{
            this.today_task = new Data(today, subjects);
        }

        this.frame.addKeyListener(new Key());

        this.h_index = -1;

        new Timer().schedule(new RenderTask(), 0, 100);
    }

    synchronized public char iToChar(int i){
        char[] cs = {'a','b','c','d','e','f','g','h','i','j','k','l'};
        return cs[i];
    }

    synchronized public boolean isSameDay(Calendar a, Calendar b){
            boolean b_y = a.get(Calendar.YEAR) == b.get(Calendar.YEAR);
            boolean b_m = a.get(Calendar.MONTH) == b.get(Calendar.MONTH);
            boolean b_d = a.get(Calendar.DATE) == b.get(Calendar.DATE);

            return b_y && b_m && b_d;
    }

    synchronized public void render(){
        Graphics2D g = (Graphics2D)this.strategy.getDrawGraphics();
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, this.frame.getWidth(), this.frame.getHeight());
        g.setFont(new Font("Arial", 0, 24));
        
        if(this.mode == 0){
            this.render0(g);
        }else if(this.mode == 1){
            this.render1(g);
        }else{
            this.render2(g);
        }

        g.dispose();
        this.strategy.show();
    }

    synchronized public void render0(Graphics2D g){
        g.setColor(Color.WHITE);
        ArrayList<Subject> tasks = this.today_task.getSubjects();
        int size = tasks.size();

        Calendar ca = this.today_task.getCalendar();

        g.drawString(String.format("%d/%02d/%02d", ca.get(Calendar.YEAR), ca.get(Calendar.MONTH) + 1, ca.get(Calendar.DATE)), 100, 50);
        
        for(int i = 0; i < size; i++){
            g.drawString(String.format("%c", this.iToChar(i)), 50, i * 30 + 100);
        }
        for(int i = 0; i < size; i++){
            g.drawString(tasks.get(i).title, 100, i * 30 + 100);
        }
        for(int i = 0; i < size; i++){
            g.drawString(this.intToTime(tasks.get(i).assignment), 400, i * 30 + 100);
        }
        for(int i = 0; i < size; i++){
            g.drawString(this.intToTime(Math.min(tasks.get(i).result, tasks.get(i).assignment)), 550, i * 30 + 100);
        }
        g.setColor(Color.YELLOW);
        for(int i = 0; i < size; i++){
            Subject s = tasks.get(i);
            int remain = s.assignment - s.result;
            if(remain > 0){
                g.drawString(this.intToTime(remain), 700, i * 30 + 100);
            }else{
                g.drawString("clear", 700, i * 30 + 100);
            }
        }
    }

    synchronized public void render1(Graphics2D g){
        g.setColor(Color.WHITE);
        g.drawString(this.target.title, 300, 100);
        long s = System.currentTimeMillis() - this.time;
        int n_res = this.target.result + (int)(s / 1000L);
        if(this.target.assignment <= n_res){
            n_res = this.target.assignment;
            this.target.result = n_res;
            this.mode = 0;
        }
        int t = this.target.assignment - n_res;
        g.drawString(this.intToTime(t), 300, 300);
    }

    synchronized public void render2(Graphics2D g){
        g.setColor(Color.WHITE);
        Data d = this.histories.get(this.h_index);
        ArrayList<Subject> subjects = d.getSubjects();
        Calendar ca = d.getCalendar();

        g.drawString(String.format("%d/%d/%d", ca.get(Calendar.YEAR), ca.get(Calendar.MONTH) + 1, ca.get(Calendar.DATE)), 100, 50);
        
        for(int i = 0; i < subjects.size(); i++){
            Subject subject = subjects.get(i);

            g.drawString(subject.title, 100, i * 30 + 100);

            g.drawString(this.intToTime(subject.assignment), 400, i * 30 + 100);

            if(subject.assignment > subject.result){
                g.setColor(Color.RED);
                g.drawString("failure", 550, i * 30 + 100);
            }else{
                g.setColor(Color.YELLOW);
                g.drawString("clear", 550, i * 30 + 100);
            }
            
            g.setColor(Color.WHITE);
        }
    }

    synchronized public String intToTime(int t){
        return String.format("%03d:%02d", t / 60, t % 60);
    }

    synchronized public void exit(){
        try{
            FileWriter fw = new FileWriter("data/data.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            
            while(this.histories.size() >= 100){
                this.histories.remove(0);
            }
            
            for(int i = 0; i < this.histories.size(); i++){
                Data d = this.histories.get(i);
                bw.write(d.encode());
                bw.newLine();
                bw.flush();
            }

            bw.write(this.today_task.encode());
            bw.newLine();
            bw.flush();
            
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println(e);
            System.exit(-1);
        }
        
        System.exit(0);
    }

    class RenderTask extends TimerTask{
        @Override public void run(){
            Timers.this.render();
        }
    }

    class Key extends KeyAdapter{
        @Override public void keyPressed(KeyEvent event){
        }

        @Override public void keyReleased(KeyEvent event){
        }

        @Override public void keyTyped(KeyEvent event){
            char c = event.getKeyChar();

            synchronized(Timers.this){
                if(Timers.this.mode == 0){
                    if(c == 'Q'){
                        Timers.this.exit();
                    }else if(c == 'R'){
                        if(Timers.this.histories.size() > 0){
                            if(Timers.this.h_index == -1){
                                Timers.this.h_index = Timers.this.histories.size() - 1;
                            }
                            Timers.this.mode = 2;
                        }
                    }else{
                        ArrayList<Subject> subjects = Timers.this.today_task.getSubjects();
                        
                        for(int i = 0; i < subjects.size(); i++){
                            Subject now_sub = subjects.get(i);
                            
                            if(c == Timers.this.iToChar(i) && now_sub.assignment > now_sub.result){
                                Timers.this.mode = 1;
                                Timers.this.target = now_sub;
                                Timers.this.time = System.currentTimeMillis();
                                break;
                            }
                        }
                    }
                }else if(Timers.this.mode == 1){
                    if(c == '\n'){
                        Timers.this.mode = 0;
                        long t = System.currentTimeMillis();
                        int s = (int)((t - Timers.this.time) / 1000L);
                        int n_res = Timers.this.target.result + s;
                        if(n_res > Timers.this.target.assignment){
                            Timers.this.target.result = Timers.this.target.assignment;
                        }else{
                            Timers.this.target.result = n_res;
                        }
                    }
                }else{
                    if(c == 'n'){
                        Timers.this.h_index += 1;
                        if(Timers.this.h_index >= Timers.this.histories.size()){
                            Timers.this.h_index = Timers.this.histories.size() - 1;
                        }
                    }else if(c == 'p'){
                        Timers.this.h_index -= 1;
                        if(Timers.this.h_index < 0){
                            Timers.this.h_index = 0;
                        }
                    }else if(c == '\n'){
                        Timers.this.mode = 0;
                    }
                }
            }
        }
    }
}
