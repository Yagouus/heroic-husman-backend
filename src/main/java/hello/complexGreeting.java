package hello;

public class complexGreeting {

    private long id;
    private  String content;
    private  String age;

    public complexGreeting(){
        id = 0;
        content ="";
        age="";
    }

    public complexGreeting(long id, String content, String age) {
        this.id = id;
        this.content = content;
        this.age = age;
    }

    public long getId() {
        return id;
    }
    public String getContent() {
        return content;
    }
    public String getAge(){return age;}

    public void setAge(String age) {
        this.age = age;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id) {
        this.id = id;
    }
}

