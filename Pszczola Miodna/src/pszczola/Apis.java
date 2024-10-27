package pszczola;

import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Apis {

    private static List<Pszczola> ul;

    public Apis() {
        ul = new ArrayList<>();
        ul.add(new Robotnica("Ala"));
        ul.add(new Robotnica("Gertruda"));
        ul.add(new Truten("Bartek"));
        ul.add(new Truten("Kamil"));
        ul.add(new KrolowaMatka("Teodora"));
    }

    public static KrolowaMatka znajdzKrolowa() {
        for (Pszczola p : ul) {
            if (p instanceof KrolowaMatka) {
                return (KrolowaMatka) p;
            }
        }
        return null; // Nie znaleziono Królowej
    }

    public void zyciePszczol(){
        Random random = new Random();
        KrolowaMatka krolowa = null;
        int trutenCount = 0;
        List<Truten> trutnie = new ArrayList<Truten>();
        for(Pszczola p : ul){
            if(p instanceof Truten && trutenCount < 2){
                Truten truten = (Truten) p;
                trutnie.add(truten);
            }
            if(p instanceof Robotnica){
                Robotnica robotnica = (Robotnica) p;
                robotnica.zbierajNektar(random.nextInt(19) + 1);
            }
            if(p instanceof KrolowaMatka){
                krolowa = (KrolowaMatka) p;
            }
        }
        if(krolowa != null){
            for(Truten tr : trutnie){
                tr.zaplodnienie(krolowa);
            }
        }
    }

    public void sortujWgSilyIImienia() {
        ul.sort(new Comparator<Pszczola>() {
            @Override
            public int compare(Pszczola p1, Pszczola p2) {
                // Najpierw porównujemy siłę pszczół
                if (p1.silaAtaku != p2.silaAtaku) {
                    return Integer.compare(p2.silaAtaku, p1.silaAtaku); // Od największej do najmniejszej
                }
                // Jeśli siła jest taka sama, porównujemy alfabetycznie według imienia
                return p1.imie.compareTo(p2.imie);
            }
        });
    }

    public void dodajZolnierza(String imie, int silaAtaku) {
        Pszczola zolnierz = new Pszczola(imie) {
            {
                this.silaAtaku = silaAtaku; // Ustawienie siły ataku
            }

            @Override
            public void run() {
                System.out.println("Walka to moje życie!!!");
            }

            @Override
            public String toString() {
                return "Żołnierz " + imie + " (atak: " + silaAtaku + "), żyję " + getWiek() + " dni i potrafię użądlić!";
            }
        };
        ul.add(zolnierz);
    }

    public void dodajDowolnaPszczole(String imie){
        Random random = new Random();
        int r = random.nextInt(3)+1;
        if(r == 1){
            ul.add(new KrolowaMatka(imie));
        }
        if(r == 2){
            ul.add(new Truten(imie));
        }
        if(r == 3){
            ul.add(new Robotnica(imie));
        }
    }

    public void watkiPszczol(){
        System.out.println("W ulu jest "+ul.size()+" pszczół:");
        for(Pszczola p : ul){
            p.run();
        }
    }


    public static abstract class Pszczola implements Runnable{

        protected String imie;
        protected int silaAtaku;
        protected int wiek;
        private LocalDate dataUrodzenia;

        public Pszczola(String imie){
            this.imie = imie;
            this.silaAtaku = silaAtaku;
            this.dataUrodzenia = LocalDate.now();
            this.wiek = 0;
        }
        public int getWiek(){
            Period period = Period.between(LocalDate.now(),dataUrodzenia);
            wiek = period.getDays() + period.getMonths()*30 + period.getYears()*365;
            return wiek;
        }

        @Override
        public void run() {}

        @Override
        public String toString(){
            return "";
        }
    }

    public static class KrolowaMatka extends Pszczola{

        private int iloscJaj;

        public KrolowaMatka(String imie) {
            super(imie);
            this.iloscJaj = 0;
            this.silaAtaku = 100;
        }

        public void zaplodnienie(){
            this.iloscJaj += 1000;
        }

        @Override
        public void run() {
            System.out.println("Lot godowy...");
        }

        @Override
        public String toString() {
            return "Królowa "+imie+" (atak: "+silaAtaku+"), żyję "+getWiek()+" i będę matką dla "+iloscJaj+
                    " młodych pszczółek";
        }
    }

    public static class Truten extends Pszczola{

        protected boolean przydatny;

        public Truten(String imie) {
            super(imie);
            this.silaAtaku = 0;
            this.przydatny = true;
        }

        public String zaplodnienie(KrolowaMatka krolowa){
            przydatny = false;
            krolowa.zaplodnienie();
            return imie + "- byłem z Królową!!! Można umierać...";
        }
        @Override
        public void run(){
            Random random = new Random();
            if(random.nextDouble() < 0.5){
                System.out.println("Jak to przyjemnie nie robić nic...");
            }else {
                KrolowaMatka krolowa = Apis.znajdzKrolowa();
                if(krolowa != null){
                    System.out.println(zaplodnienie(krolowa));
                }
            }
        }

        @Override
        public String toString() {
            if(przydatny) {
                return "Truteń " + imie + " (atak: " + silaAtaku + "), żyję "+getWiek()+" dni";
            }
            return "Truteń " + imie + " (atak: " + silaAtaku + "), spełniłem swoje zadanie :(";
        }
    }

    public static class Robotnica extends Pszczola{

        private int iloscWyprodukowanegoMiodu;

        public Robotnica(String imie) {
            super(imie);
            this.iloscWyprodukowanegoMiodu = 0;
            Random random = new Random();
            this.silaAtaku = random.nextInt(100);
        }

        public void zbierajNektar(int miod){
            iloscWyprodukowanegoMiodu += miod;
        }

        @Override
        public void run(){
            Random random = new Random();
            int miod = random.nextInt(19)+1;
            zbierajNektar(miod);
            System.out.println(imie + " - kolejna porcja miodu do kubełka");

        }

        @Override
        public String toString() {
            return "Robotnica "+imie+" (atak: " +silaAtaku+"), żyję "+getWiek()+"dni i zrobiłam "+
                    iloscWyprodukowanegoMiodu+" baryłek miodu :)";
        }
    }

    private class PorownanieSily implements Comparator<Pszczola> {
        @Override
        public int compare(Pszczola p1, Pszczola p2) {
            // Porównanie siły ataku
            if (p1.silaAtaku != p2.silaAtaku) {
                return Integer.compare(p2.silaAtaku, p1.silaAtaku); // Sortowanie malejąco
            }
            // Jeśli siła ataku jest taka sama, porównaj alfabetycznie imiona
            return p1.imie.compareTo(p2.imie);
        }
    }

    public void sortujPszczoly() {
        ul.sort(new PorownanieSily());
    }

    public List<Pszczola> getPszczoly() {
        return ul;
    }

}
