public class Estado {
    public String id;
    public String name;
    public float x;
    public float y;
    public boolean isInitial;
    public boolean isFinal;

    public Estado() {
    }

    public Estado(String id, String name, float x, float y, boolean isInitial, boolean isFinal) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
    }
}