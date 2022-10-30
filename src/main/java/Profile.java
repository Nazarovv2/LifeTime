public class Profile {
    private String name;
    private Long id;
    private UserStep userStep;
    private int year;
    private int month;
    private int day;

    public Profile(Long id, UserStep userStep) {
        this.id = id;
        this.userStep = userStep;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserStep getUserStep() {
        return userStep;
    }

    public void setUserStep(UserStep userStep) {
        this.userStep = userStep;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}


