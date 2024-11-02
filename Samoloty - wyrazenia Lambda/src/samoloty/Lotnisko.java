package samoloty;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.List;
import java.util.function.Consumer;

public class Lotnisko {

    private List<Samolot> samoloty;
    private NazwaGenerator nazwaGenerator;
    Random random = new Random();

    public Lotnisko(int iloscSamolotow) {
        samoloty = new ArrayList<>();
        nazwaGenerator = () -> {
            int dlugosc = 1 + random.nextInt(20);
            StringBuilder nazwa = new StringBuilder(dlugosc);

            for(int i=0; i<dlugosc; i++){
                char znak = (char) ('a'+random.nextInt(26));
                nazwa.append(znak);
            }

            return nazwa.toString();
        };

        for (int i = 0; i < iloscSamolotow; i++) {
            int typSamolotu = random.nextInt(3);
            String nazwa = nazwaGenerator.generuj();

            switch (typSamolotu) {
                case 0:
                    int predkoscPasazerski = 500 + random.nextInt(501);
                    int maxPasazerow = 100 + random.nextInt(201);
                    samoloty.add(new SamolotPasazerski(nazwa, predkoscPasazerski, maxPasazerow));
                    break;

                case 1:
                    int predkoscTowarowy = 300 + random.nextInt(401);
                    int maxTowar = 10 + random.nextInt(91);
                    samoloty.add(new SamolotTowarowy(nazwa, predkoscTowarowy, maxTowar));
                    break;

                case 2:
                    int predkoscMysliwiec = 900 + random.nextInt(2101);
                    samoloty.add(new Mysliwiec(nazwa, predkoscMysliwiec));
                    break;
            }
        }
    }
    @FunctionalInterface
    public interface NazwaGenerator{
        String generuj();
    }
    @FunctionalInterface
    public interface KomparatorGenerator{
        Comparator<Samolot> generuj();
    }

    public void odprawaSamolotow(){
        for(Samolot samolot : samoloty){
            if(samolot instanceof SamolotPasazerski){
                int pasazerowie = 1 + random.nextInt(400);
                SamolotPasazerski samolotPasazerski = (SamolotPasazerski) samolot;
                try {
                    samolotPasazerski.odprawa(pasazerowie);
                } catch(WyjatekLotniczy | WyjatekEkonomiczny | WyjatekPrzeladowanie e) {
                    System.out.println(e.getMessage());
                }
            }
            else if(samolot instanceof SamolotTowarowy){
                int zaladunek = 1 + random.nextInt(200);
                SamolotTowarowy samolotTowarowy = (SamolotTowarowy) samolot;
                try {
                    samolotTowarowy.odprawa(zaladunek);
                } catch(WyjatekLotniczy | WyjatekEkonomiczny | WyjatekPrzeladowanie e) {
                    System.out.println(e.getMessage());
                }
            }else{
                int rakiety = 1 + random.nextInt(10);
                Mysliwiec mysliwiec = (Mysliwiec) samolot;
                try {
                    mysliwiec.odprawa(rakiety);
                } catch(WyjatekLotniczy | WyjatekEkonomiczny | WyjatekPrzeladowanie e) {
                    System.out.println(e.getMessage());
                }
            }

        }

    }

    public void dzialaniaLotniskowe(){
        Consumer<Samolot> consumerToString = samolot -> System.out.println(samolot.toString());
        Consumer<Samolot> consumerLaduj = samolot -> samolot.laduj();

        Consumer<Samolot> consumerOdprawa = samolot -> {
            int zaladunek = 1 + random.nextInt(400);
            try{
                samolot.odprawa(zaladunek);
            }catch(WyjatekLotniczy | WyjatekEkonomiczny | WyjatekPrzeladowanie e){
                System.out.println(e.getMessage());
            }
        };

        Consumer<Samolot> consumerLec = samolot -> samolot.lec(10);

        Consumer<Samolot> consumerAtak = samolot -> {
            if(samolot instanceof Mysliwiec){
                ((Mysliwiec) samolot).atak();
            }
        };

        samoloty.forEach(consumerToString);
        samoloty.forEach(consumerLaduj);
        samoloty.forEach(consumerOdprawa);
        samoloty.forEach(consumerLec);
        samoloty.forEach(consumerAtak);
    }

    public void sortowanieSamolotow(){
        samoloty.sort(Comparator.comparingInt(Samolot::getPredkoscMax));

        samoloty.sort(new Comparator<Samolot>() {
            @Override
            public int compare(Samolot s1, Samolot s2) {
                boolean s1Long = s1.getNazwa().length() > 5;
                boolean s2Long = s2.getNazwa().length() > 5;

                if (s1Long && s2Long) {
                    return s1.getNazwa().compareTo(s2.getNazwa());
                }

                if (s1Long) {
                    return -1;
                }
                if (s2Long) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public void sortowanieLosowe(){

        KomparatorGenerator generator = () -> {
            if (random.nextBoolean()) {
                return Comparator.comparingInt(Samolot::getPredkoscMax);
            } else {
                return new Comparator<Samolot>() {
                    @Override
                    public int compare(Samolot s1, Samolot s2) {
                        boolean s1Long = s1.getNazwa().length() > 5;
                        boolean s2Long = s2.getNazwa().length() > 5;
                        if (s1Long && s2Long) {
                            return s1.getNazwa().compareTo(s2.getNazwa());
                        }
                        if (s1Long) {
                            return -1;
                        }
                        if (s2Long) {
                            return 1;
                        }
                        return 0;
                    }
                };
            }
        };

        samoloty.sort(generator.generuj());

    }


    public class WyjatekLotniczy extends Exception{
        public WyjatekLotniczy(String message){
            super(message);
        }
    }
    public static class WyjatekEkonomiczny extends Exception{
        public WyjatekEkonomiczny(String message){
            super(message);
        }
    }
    public static class WyjatekPrzeladowanie extends Exception{
        public WyjatekPrzeladowanie(String message){
            super(message);
        }
    }



    public static abstract class Samolot {

        protected String nazwa;
        protected int predkoscMax;
        protected int iloscGodzinWPowietrzu;
        protected boolean czyPoOdprawie = false;
        protected boolean czyWPowietrzu = false;

        public String getNazwa() {
            return nazwa;
        }

        public int getPredkoscMax() {
            return predkoscMax;
        }

        public Samolot(String nazwa, int predkoscMax){
            this.nazwa = nazwa;
            this.predkoscMax = predkoscMax;
            this.iloscGodzinWPowietrzu = 0;
        }

        public void lec(int godzinyLotu) {
            if(!czyPoOdprawie){
                System.out.println("Nie możemy wystartować");
            }else if(czyWPowietrzu){
                System.out.println("Lecimy");
                iloscGodzinWPowietrzu += godzinyLotu;
            }else{
                System.out.println("Startujemy");
                czyWPowietrzu = true;
                iloscGodzinWPowietrzu += godzinyLotu;
            }
        }

        public abstract void odprawa(int iloscZaladunku) throws WyjatekLotniczy, WyjatekPrzeladowanie, WyjatekEkonomiczny;

        public void laduj(){
            if(czyWPowietrzu){
                System.out.println("Lądujemy");
                czyWPowietrzu = false;
                czyPoOdprawie = false;
            }else{
                System.out.println("I tak jestesmy na ziemi");
            }
        }
        public abstract String toString();

    }

    public static class SamolotPasazerski extends Samolot{

        private int maxPasazerowie;
        private int aktualniPasazerowie;

        public SamolotPasazerski(String nazwa, int predkoscMax, int maxPasazerowie) {
            super(nazwa, predkoscMax);
            this.maxPasazerowie = maxPasazerowie;
            this.aktualniPasazerowie = 0;
        }

        @Override
        public void odprawa(int iloscZaladunku) throws WyjatekLotniczy, WyjatekPrzeladowanie, WyjatekEkonomiczny {
            if(iloscZaladunku < 0.5*maxPasazerowie){
                throw new WyjatekEkonomiczny("Za mało pasażerów, nie opłaca się lecieć");
            }
            if(iloscZaladunku + aktualniPasazerowie > maxPasazerowie) {
                czyPoOdprawie = true;
                aktualniPasazerowie += iloscZaladunku;
                int nadmiar = aktualniPasazerowie - maxPasazerowie;
                throw new WyjatekPrzeladowanie("Za dużo o " + nadmiar + " pasazerów");
            }
            czyPoOdprawie = true;
            aktualniPasazerowie += iloscZaladunku;
        }

        @Override
        public String toString() {
            if(!czyWPowietrzu) {
                return "Samolot pasażerski o nazwie " + nazwa + ". Predkość maksymalna " + predkoscMax + ", w " +
                        "powietrzu spędził łącznie " + iloscGodzinWPowietrzu + " godzin, moze zabrac na pokład " + maxPasazerowie +
                        " pasażerów. Aktualnie uziemiony";
            }
            return "Samolot pasażerski o nazwie "+nazwa+". Predkość maksymalna "+predkoscMax+", w " +
                    "powietrzu spędził łącznie "+iloscGodzinWPowietrzu+" godzin, moze zabrac na pokład "+maxPasazerowie+
                    " pasażerów. Obecnie leci z "+aktualniPasazerowie+" pasażerami na pokładzie.";
        }

    }

    public static class SamolotTowarowy extends Samolot {

        private int maxLadunek;
        private int aktualnyLadunek;

        public SamolotTowarowy(String nazwa, int predkoscMax, int maxLadunek) {
            super(nazwa, predkoscMax);
            this.aktualnyLadunek = 0;
            this.maxLadunek = maxLadunek;
        }

        @Override
        public void odprawa(int iloscZaladunku) throws WyjatekLotniczy, WyjatekPrzeladowanie, WyjatekEkonomiczny {
            if(iloscZaladunku < 0.5*maxLadunek){
                throw new WyjatekEkonomiczny("Zbyt mały ładunek, nie opłaca się lecieć");
            }
            if(iloscZaladunku + aktualnyLadunek > maxLadunek) {
                czyPoOdprawie = true;
                aktualnyLadunek += iloscZaladunku;
                int nadmiar = aktualnyLadunek - maxLadunek;
                throw new WyjatekPrzeladowanie("Za dużo o " + nadmiar + " ton ładunku");
            }
            czyPoOdprawie = true;
            aktualnyLadunek += iloscZaladunku;
        }

        @Override
        public String toString() {
            if(!czyWPowietrzu) {
                return "Samolot towarowy o nazwie " + nazwa + ". Predkość maksymalna " + predkoscMax + ", w " +
                        "powietrzu spędził łącznie " + iloscGodzinWPowietrzu + " godzin, moze zabrac na pokład " + maxLadunek +
                        " ton ładunku. Aktualnie uziemiony";
            }
            return "Samolot pasażerski o nazwie "+nazwa+". Predkość maksymalna "+predkoscMax+", w " +
                    "powietrzu spędził łącznie "+iloscGodzinWPowietrzu+" godzin, moze zabrac na pokład "+maxLadunek+
                    " ton ładunku. Obecnie leci z "+aktualnyLadunek+ " ton ładunku na pokładzie.";
        }
    }

    public static class Mysliwiec extends Samolot {

        private int iloscRakiet;

        public Mysliwiec(String nazwa, int predkoscMax) {
            super(nazwa, predkoscMax);
            this.iloscRakiet = 0;
        }

        @Override
        public void odprawa(int iloscZaladunku) throws WyjatekLotniczy, WyjatekPrzeladowanie, WyjatekEkonomiczny {
            iloscRakiet += iloscZaladunku;
            czyPoOdprawie = true;
        }

        @Override
        public String toString() {
            if(!czyWPowietrzu) {
                return "Mysliwiec o nazwie " + nazwa + ". Predkość maksymalna " + predkoscMax + ", w " +
                        "powietrzu spędził łącznie " + iloscGodzinWPowietrzu + ". Aktualnie uziemiony";
            }
            return "Mysliwiec o nazwie "+nazwa+". Predkość maksymalna "+predkoscMax+", w " +
                    "powietrzu spędził łącznie "+iloscGodzinWPowietrzu+". Obecnie leci, rakiet "+iloscRakiet;
        }

        public void atak(){
            if(czyWPowietrzu){
                iloscRakiet -= 1;
                System.out.println("Ataaaaaaak");
                if(iloscRakiet == 0){
                    laduj();
                }
            }
        }
    }


}
