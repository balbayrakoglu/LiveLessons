package utils;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A utility class containing helpful methods for manipulating various
 * BigFraction features.
 */
public class BigFractionUtils {
    /**
     * A utility class should always define a private constructor.
     */
    private BigFractionUtils() {
    }

    /**
     * Number of big fractions to process asynchronously in a Reactor
     * flux stream.
     */
    public static final int sMAX_FRACTIONS = 10;

    /**
     * These final strings are used to pass params to various lambdas in the
     * test methods below.
     */
    public static final String sF1 = "62675744/15668936";
    public static final String sF2 = "609136/913704";
    public static final String sBI1 = "846122553600669882";
    public static final String sBI2 = "188027234133482196";

    /**
     * A big reduced fraction constant.
     */
    public static final BigFraction sBigReducedFraction =
        BigFraction.valueOf(new BigInteger("846122553600669882"),
                            new BigInteger("188027234133482196"),
                            true);

    /**
     * Stores a completed mono with a value of sBigReducedFraction.
     */
    public static final Single<BigFraction> mBigReducedFractionM =
        Single.just(sBigReducedFraction);

    /**
     * Represents a test that's completed running when it returns.
     */
    public static final Completable sVoidM =
        Completable.complete();

    /**
     * A factory method that returns a large random BigFraction whose
     * creation is performed synchronously.
     *
     * @param random A random number generator
     * @param reduced A flag indicating whether to reduce the fraction or not
     * @return A large random BigFraction
     */
    public static BigFraction makeBigFraction(Random random,
                                              boolean reduced) {
        // Create a large random big integer.
        BigInteger numerator =
            new BigInteger(150000, random);

        // Create a denominator that's between 1 to 10 times smaller
        // than the numerator.
        BigInteger denominator =
            numerator.divide(BigInteger.valueOf(random.nextInt(10) + 1));

        // Return a big fraction.
        return BigFraction.valueOf(numerator,
                                   denominator,
                                   reduced);
    }

    /**
     * Sort the {@code list} in parallel using quicksort and mergesort
     * and then store the results in the {@code StringBuilder}
     * parameter.
     */
    public static Completable sortAndPrintList(List<BigFraction> list,
                                              StringBuilder sb) {
        // Quick sort the list asynchronously.
        Single<List<BigFraction>> quickSortM = Single
            // Use the just() factory method to obtain the
            // results of quick sorting the list.
            .just(quickSort(list))

            // Use subscribeOn() to run all the processing in the
            // parallel thread pool.
            .subscribeOn(Schedulers.computation());

        // Heap sort the list asynchronously.
        Single<List<BigFraction>> heapSortM =  Single
            // Use the just() factory method to obtain the
            // results of heap sorting the list.
            .just(heapSort(list))

            // Use subscribeOn() to run all the processing in the
            // parallel thread pool.
            .subscribeOn(Schedulers.computation());

        return Single
            // Use first() to select the result of whichever sort
            // finishes first and use it to print the sorted list.
            .ambArray(quickSortM,
                      heapSortM)

            // Use doOnSuccess() to process the first sorted list.
            .doOnSuccess(sortedList -> {
                    // Print the results as mixed fractions.
                    sortedList
                        .forEach(fraction ->
                                 sb.append("\n     "
                                           + fraction.toMixedString()));
                    sb.append("\n");
                    display(sb.toString());
                })
                
            // Return a completable to synchronize with
            // the AsyncTester framework.
            .ignoreElement();
    }

    /**
     * Perform a quick sort on the {@code list}.
     */
    public static List<BigFraction> quickSort(List<BigFraction> list) {
        // Convert the list to an array.
        BigFraction[] bigFractionArray =
            list.toArray(new BigFraction[0]);

        // Order the array with quick sort.
        Arrays.sort(bigFractionArray);

        // Convert the array back to a list.
        return List.of(bigFractionArray);
    }

    /*
     * Perform a heap sort on the {@code list}.
     */
    public static List<BigFraction> heapSort(List<BigFraction> list) {
        // Convert the list to an array.
        BigFraction[] bigFractionArray =
            list.toArray(new BigFraction[0]);

        // Order the array with heap sort.
        HeapSort.sort(bigFractionArray);

        // Convert the array back to a list.
        return List.of(bigFractionArray);
    }

    /**
     * Display the {@code string} after prepending the thread id.
     */
    public static void display(String string) {
        System.out.println("["
                           + Thread.currentThread().getId()
                           + "] "
                           + string);
    }
}
