package Tests;

public class HelloService {
    private String name;
    private int year;
    public void Sayhello(){
        System.out.println("Hello!");
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
