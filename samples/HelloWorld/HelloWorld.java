import com.greenlaw110.rythm.*;

public class HelloWorld {
  
  public static void main(String[] args) {
    String output = Rythm.render("hello.txt", "world");
    System.out.println(output);
  }

}