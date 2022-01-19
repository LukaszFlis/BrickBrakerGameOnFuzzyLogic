import javax.swing.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FuzzyController implements Runnable {

    private Gameplay gamePlay;

    FuzzyController(Gameplay gamePlay) {
        this.gamePlay = gamePlay;
    }

    // metoda jest nieuzywana?
    // a i b = support, x1 x2 = jądro z przedziałem, x= wartość ostra wejściowa do rozmycia
    private double createTermTrapeze(double a, double b, double x1, double x2, int x) {
        double value;
        if ((x >= a) & (x <= x1))
            value = (x - a) / (x1 - a);
        else {
            if ((x > x1) & (x < x2))
                value = 1;
            else {
                if ((x >= x2) & (x <= b))
                    value = (b - x) / (b - x2);
                else
                    value = 0;
            }
        }
        return value;
    }

    // a i b = support trójkąta, x0 = jądro, x= wartość ostra wejściowa do rozmycia
    private double createTermTriangle(double a, double b, double x0, int x) {
        double value;
        if ((x >= a) & (x <= x0))
            value = ((x - a) / (x0 - a));
        else {
            if ((x >= x0) & (x <= b))
                value = ((b - x) / (b - x0));
            else
                value = 0;
        }
        return value;
    }

    //sigm = rozkład, x0 = jądro, x = wartość wejściowa ostra do rozmycia
    private double createTermGauss(double sigm, int x0, int x) {

        double skladnik = 0.0;
        double value;
        skladnik = (-0.5 * Math.pow(((x - x0) / sigm), 2));
        value = Math.pow(Math.E, skladnik);
        return value;
    }

    // x0 = jądro,
    private double createTermSingleton(double x0, int x) {
        double value;
        if (x == x0)
            value = 1;
        else
            value = 0;
        return value;
    }


    public double runFuzzyController(int ballPositionX, int platePositionX) {

        // i = input (zmienna lingwistyczne wejściowe) o = output (zmienna lingwistyczna wyjściowa)
        double iBardzoLewo, iLewo, iTrocheLewo, iZero, iTrochePrawo, iPrawo, iBardzoPrawo;
        double oBardzoLewo, oLewo, oTrocheLewo, oZero, oTrochePrawo, oPrawo, oBardzoPrawo;

        //operator MAX
        double agregacja;

        //wyostrzanie metodą średniej ważonej
        double licznik = 0;
        double mianownik = 0;

        //wartość po wyostrzeniu
        double wynik;

        //róznica między środkiem piłki a środkiem paletki
        int error;

        //--------------------------------------------------------------------------------------------------
        // Obliczanie błędu pozycji paletki względem piłeczki - jest to wartość ostra poddana rozmyciu
        error = ballPositionX - platePositionX;

        //--------------------------------------------------------------------------------------------------
        // Tworzenie zbioru rozmytego (wejściowego) i fuzyfikacja
        //sakla jest od -592 do 592
        //użyto tylko termy trójkątne i przeskoczono skalę, aby nie używać termów trapezowych
        iBardzoLewo = createTermTriangle(-650, -240, -600, error);
        iLewo = createTermTriangle(-360, -120, -240, error);
        iTrocheLewo = createTermTriangle(-240, 0, -120, error);
        iZero = createTermTriangle(-50, 50, 0, error);
        iTrochePrawo = createTermTriangle(0, 240, 120, error);
        iPrawo = createTermTriangle(120, 360, 240, error);
        iBardzoPrawo = createTermTriangle(240, 650, 600, error);

        //--------------------------------------------------------------------------------------------------
        //Wnioskowanie, agregacja i jednoczesne tworzenie zbioru rozmytego (wyjściowego)

        //36px o tyle maksymalnie wychyla się paletka (wartość dobrana losowo dla płynnego ruchu paletki)
        for (int i = -36; i <= 36; i++) {
            //--------------------------------------------------------------------------------------------------
            //Tworzenie zbioru rozmytego (wyjściowego) oraz wnioskowanie
            //dlaczego został wykorzystany tylko singleton ?????/ bo tak było prościej,
            // gdyż przy termie trójkątnym zakres pętli musiałby być wiekszy niż 36px

            //Baza Reguł
            //R1 = IF iBardzoPrawo THEN oBardzoLewo
            oBardzoLewo = Math.min(iBardzoPrawo, createTermSingleton(-36, i));
            //R2 = IF iPrawo THEN oLewo
            oLewo = Math.min(iPrawo, createTermSingleton(-24, i));
            //R3 = IF iTrochePrawo THEN oTrocheLewo
            oTrocheLewo = Math.min(iTrochePrawo, createTermSingleton(-12, i));
            //R4 = IF iZero THEN oZero
            oZero = Math.min(iZero, createTermSingleton(0, i));
            //R5 = IF iTrocheLewo THEN oTrochePrawo
            oTrochePrawo = Math.min(iTrocheLewo, createTermSingleton(12, i));
            //R6 = IF iLewo THEN oPrawo
            oPrawo = Math.min(iLewo, createTermSingleton(24, i));
            //R7 = IF iBardzoLewo THEN oBardzoPrawo
            oBardzoPrawo = Math.min(iBardzoLewo, createTermSingleton(36, i));

            //--------------------------------------------------------------------------------------------------
            //Agregacja wartości zbioru rozmytego (wyjściowego)
            agregacja = oBardzoLewo;
            agregacja = Math.max(agregacja, oLewo);
            agregacja = Math.max(agregacja, oTrocheLewo);
            agregacja = Math.max(agregacja, oZero);
            agregacja = Math.max(agregacja, oTrochePrawo);
            agregacja = Math.max(agregacja, oPrawo);
            agregacja = Math.max(agregacja, oBardzoPrawo);
            licznik = licznik + agregacja * i;
            mianownik = mianownik + agregacja;
        }

        //--------------------------------------------------------------------------------------------------
        //Defuzyfikacja (wyostrzenie)
        if (mianownik != 0) {
            wynik = licznik / mianownik;
        } else {
            wynik = 0;
        }

        //--------------------------------------------------------------------------------------------------
        // Zwrot nowej wartosci ostrej po defuzyfikacji będącą wysterowaniem
        return wynik;
    }


    @Override
    public void run() {
        Random random = new Random();
        int centerPlatePosition;
        while (true) {
            // dlaczego do pozycji gracza dodajesz wartosc 50 ?
            centerPlatePosition = gamePlay.getPlayerX() + 50;
            //algorytm działa
            if (gamePlay.getBallposY() > 350 && gamePlay.getBallposY() < 570) {
                double value = this.runFuzzyController(gamePlay.getBallposX(), centerPlatePosition);
                if (gamePlay.getPlayerX() > 600) {
                    gamePlay.setPlayerX(600);
                }
                if (gamePlay.getPlayerX() < 0) {
                    gamePlay.setPlayerX(0);
                }
                //jeżeli paletka znajduje się w zakresie pola gry funkcja przesuwa ja o wyostrzoną wartość value
                if (gamePlay.getPlayerX() <= 600 && gamePlay.getPlayerX() >= 0) {
                    gamePlay.makeMove((int) value);
                }
                System.out.println("   value->" + value + "   getBallposX()->" + gamePlay.getBallposX() + "   centerPlatePosition->" + centerPlatePosition);
            }
            try {
                // symulacja opóźnienia ruchu paletki
                TimeUnit.MILLISECONDS.sleep(15 + random.nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}










