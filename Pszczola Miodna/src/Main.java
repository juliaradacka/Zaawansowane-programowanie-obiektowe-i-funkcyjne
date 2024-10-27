import pszczola.Apis;

import java.util.Collections;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Apis ul = new Apis();


        ul.dodajDowolnaPszczole("Ela");
        ul.dodajDowolnaPszczole("Agnieszka");
        ul.dodajDowolnaPszczole("Seba");


        ul.sortujPszczoly();

        ul.watkiPszczol();

        ul.zyciePszczol();





    }
}