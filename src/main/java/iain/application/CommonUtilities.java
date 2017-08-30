package iain.application;

/**
 * Created by Iain Woodburn on 03-Aug-17
 */

class CommonUtilities {

    private static int age;
    private static int projectionAge;
    private static int ageIndicator;
    private static int firstMenPeriodAge;
    private static int firstBirthAge;
    private static int firstDegreeRels;
    private static int everHadBiopsy;
    private static int numOfBiopsy;
    private static double rHyperPlasia;
    private static int race;

    //GETTER METHODS
    static int getAge() {
        return age;
    }

    static int getProjectionAge() {
        return projectionAge;
    }

    static int getAgeIndicator() {
        return ageIndicator;
    }

    static int getFirstMenPeriodAge() {
        return firstMenPeriodAge;
    }

    static int getFirstBirthAge() {
        return firstBirthAge;
    }

    static int getFirstDegreeRels() {
        return firstDegreeRels;
    }

    static int getEverHadBiopsy() {
        return everHadBiopsy;
    }

    static int getNumOfBiopsy() {
        return numOfBiopsy;
    }

    static double getRHyperPlasia() {
        return rHyperPlasia;
    }

    static int getRace() {
        return race;
    }

    //SETTER METHODS
    static void setAge(int age) {
        CommonUtilities.age = age;
    }

    static void setFirstBirthAge(int firstBirthAge) {
        CommonUtilities.firstBirthAge = firstBirthAge;
    }

    static void setFirstMenPeriodAge(int firstMenPeriodAge) {
        CommonUtilities.firstMenPeriodAge = firstMenPeriodAge;
    }

    static void setFirstDegreeRels(int firstDegreeRels) {
        CommonUtilities.firstDegreeRels = firstDegreeRels;
    }

    static void setProjectionAge(int projectionAge) {
        CommonUtilities.projectionAge = projectionAge;
    }

    static void setAgeIndicator(int ageIndicator) {
        CommonUtilities.ageIndicator = ageIndicator;
    }

    static void setRHyperPlasia(double rHyperPlasia) {
        CommonUtilities.rHyperPlasia = rHyperPlasia;
    }

    static void setRace(int race) {
        CommonUtilities.race = race;
    }

    static void setNumOfBiopsy(int numOfBiopsy) {
        CommonUtilities.numOfBiopsy = numOfBiopsy;
    }

    static void setEverHadBiopsy(int everHadBiopsy) {
        CommonUtilities.everHadBiopsy = everHadBiopsy;
    }
}