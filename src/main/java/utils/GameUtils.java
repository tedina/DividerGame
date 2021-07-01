package utils;

/**
 * Created by Teodora.Toncheva on 01.07.2021
 */
public class GameUtils {
    public static int generateRandom() {
        int min = 1000;
        int max = Integer.MAX_VALUE - 4;
        return (int) (Math.random() * max) + min;
    }

    public static int[] setOfNumbers(int divider) {
        int[] numbers = new int[divider];
        int i = divider / 2;
        numbers[i] = 0;
        int j = 1;
        while (j <= i) {
            numbers[i + j] = j;
            numbers[i - j] = j * -1;
            j++;
        }

        return numbers;
    }
}
