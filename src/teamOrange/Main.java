package teamOrange;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // write your code here
        int x =3;
        ArrayList arr = new ArrayList();
        System.out.println(arr.size());
        arr.add(x);
        if(arr.get(0) instanceof Integer)
            System.out.println(x + " is an int");
        //teamOrange.Terminal.startTerminal(args);
    }
}