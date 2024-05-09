package tm;

public class ToDoTM {

    private String id;
    private String description;
    private String user_id;
    private String color;
    private String date;

    public ToDoTM() {
    }

    public ToDoTM(String id, String description, String user_id, String color, String date) {
        this.id = id;
        this.description = description;
        this.user_id = user_id;
        this.color = color;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setColor(String color) {
        this.color=color;
    }

    public String getColor() {
        return color;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString(){

        return description+"        " +date;
    }
}
