package pack;

public class Subject{
    public String title;
    public int assignment;
    public int result;

    public Subject(String t, int a, int r){
        this.title = t;
        this.assignment = a;
        this.result = r;
    }

    public Subject(String t, int a){
        this.title = t;
        this.assignment = a;
        this.result = a;
    }

    public boolean isCleared(){
        return this.assignment <= this.result;
    }
}