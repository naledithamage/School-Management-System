//Student attributes here

public class Student{

    public String id;
    public String phone;
    public String name;

    public Student(String name, String id, String phone){
        this.name = name;
        this.id = id;
        this.phone = phone;
    }

    public String getId(){
        return id;
    }

    public String getPhone(){
        return phone;
    }

    public String getName(){
        return name;
    }
}