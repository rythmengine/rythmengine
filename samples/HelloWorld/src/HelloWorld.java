import org.rythmengine.Rythm;

/**
 * A Hello world sample
 */
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println(Rythm.render("@args String @1\nhello @1!", "rythm"));
    }
}
