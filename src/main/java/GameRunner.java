/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple4;

import com.google.common.annotations.VisibleForTesting;

public class GameRunner {
    private static final String INPUT_FILE = "Jeux.txt";
    private static final String OUTPUT_FILE = "Result.txt";
    static int limitX;
    static int limitY;
    private static Tuple4<String, Tuple2<Integer, Integer>, String, String> adventurer = null;
    private static final List<Tuple2<Integer, Integer>> treasurePosition = new ArrayList<>();
    private static final List<Tuple2<Integer, Integer>> mountainPosition = new ArrayList<>();
    private static int[][] gameCarte = new int[0][0];
    private static final String[][] directionMatrix = {
            { "N", "E", "O" },
            { "O", "N", "S" },
            { "E", "S", "N" },
            { "S", "O", "E" } };

    @VisibleForTesting
    protected static String getTheAxeOfTheMovement(String sens) {
        return switch (sens) {
            case "S", "N" -> "X";
            case "O", "E" -> "Y";
            default -> null;
        };
    }

    @VisibleForTesting
    protected static int getTheIndexOfTheDirection(String sens) {
        return switch (sens) {
            case "N" -> 0;
            case "O" -> 1;
            case "E" -> 2;
            case "S" -> 3;
            default -> -1;
        };
    }

    private static boolean readFile() {
        boolean tEntry = false;
        boolean mEntry = false;
        boolean aEntry = false;
        boolean cEntry = false;
        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entry = line.split("-");
                switch (entry[0]) {
                    case "T": {
                        checkIfMatrixSizeIsSet(cEntry);
                        gameCarte[Integer.parseInt(entry[2])][Integer.parseInt(entry[1])] = Integer.parseInt(entry[3]);
                        treasurePosition.add(new Tuple2<Integer, Integer>(Integer.parseInt(entry[2]),
                                Integer.parseInt(entry[1])));
                        tEntry = true;
                        break;
                    }
                    case "M": {
                        checkIfMatrixSizeIsSet(cEntry);
                        gameCarte[Integer.parseInt(entry[2])][Integer.parseInt(entry[1])] = -1;
                        mountainPosition.add(new Tuple2<Integer, Integer>(Integer.parseInt(entry[2]),
                                Integer.parseInt(entry[1])));
                        mEntry = true;
                        break;
                    }
                    case "C": {
                        limitX = Integer.parseInt(entry[2]);
                        limitY = Integer.parseInt(entry[1]);
                        gameCarte = new int[limitX][limitY];
                        cEntry = true;
                        break;
                    }
                    case "A": {
                        checkIfMatrixSizeIsSet(cEntry);
                        adventurer = new Tuple4<>(entry[1], new Tuple2<>(Integer.parseInt(entry[3]),
                                Integer.parseInt(entry[2])), entry[4], entry[5]);
                        aEntry = true;
                        break;
                    }
                    case "#": {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return cEntry & aEntry & mEntry & tEntry;
    }

    private static void checkIfMatrixSizeIsSet(boolean cEntry) {
        if (!cEntry) {
            throw new IllegalArgumentException("You need to set the matrix size first!!");
        }
    }

    public static void writeToFile(String adventurerName, Tuple2<Integer, Integer> adventurerPosition, int nbTresor,
            String adventurerDirection) {
        try {
            FileWriter fstream = new FileWriter(OUTPUT_FILE);
            BufferedWriter result = new BufferedWriter(fstream);
            result.write("C-" + limitY + "-" + limitX);
            result.newLine();
            for (Tuple2<Integer, Integer> tresor : treasurePosition) {
                if (gameCarte[tresor.v1][tresor.v2] > 0) {
                    result.write("T-" + tresor.v2 + "-" + tresor.v1 + "-" + gameCarte[tresor.v1][tresor.v2]);
                    result.newLine();
                }
            }
            for (Tuple2<Integer, Integer> montagne : mountainPosition) {
                result.write("M-" + montagne.v2 + "-" + montagne.v1);
                result.newLine();
            }
            result.write("A-" + adventurerName + "-" + adventurerPosition.v2 + "-" + adventurerPosition.v1 + "-" + adventurerDirection +
                    "-" + nbTresor);
            result.newLine();
            result.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {
        if (!readFile()) {
            throw new IllegalArgumentException("An entry or more are missing in the file, please check that all " +
                    "elements are provides (M,T,A)");
        }
        CharacterIterator it = new StringCharacterIterator(adventurer.v4);
        String adventurerDirection = adventurer.v3;
        int nbTresors = 0;
        String adventurerAxeOfMouvement;
        Tuple2<Integer, Integer> adventurerPosition = adventurer.v2;

        while (it.current() != CharacterIterator.DONE) {
            switch (it.current()) {
                case 'A': {
                    adventurerAxeOfMouvement = getTheAxeOfTheMovement(adventurerDirection);
                    AdventurerNextStepPosition adventurerNextStep = getAdventurerNextStep(adventurerAxeOfMouvement,
                            adventurerDirection,
                            adventurerPosition, limitX, limitY);
                    if (gameCarte[adventurerNextStep.xAxe()][adventurerNextStep.yAxe()] != -1) {
                        adventurerPosition = new Tuple2<>(adventurerNextStep.xAxe(), adventurerNextStep.yAxe());
                        if (gameCarte[adventurerPosition.v1][adventurerPosition.v2] > 0) {
                            nbTresors++;
                            gameCarte[adventurerPosition.v1][adventurerPosition.v2]--;
                        }
                    }
                    break;
                }
                case 'G': {
                    adventurerDirection = directionMatrix[getTheIndexOfTheDirection(adventurerDirection)][2];
                    break;

                }
                case 'D': {
                    adventurerDirection = directionMatrix[getTheIndexOfTheDirection(adventurerDirection)][1];
                    break;
                }
            }
            it.next();
        }
        writeToFile(adventurer.v1, adventurerPosition, nbTresors, adventurerDirection);
    }

    @VisibleForTesting
    protected static AdventurerNextStepPosition getAdventurerNextStep(String axeAdventurer,
            String adventurerDirection, Tuple2<Integer,
            Integer> adventurerPosition, int limitX, int limitY) {
        int xAxe = adventurerPosition.v1;
        int yAxe = adventurerPosition.v2;
        if (axeAdventurer.equals("X")) {
            if (adventurerDirection.equals("N")) {
                xAxe = adventurerPosition.v1 > 0 ? adventurerPosition.v1 - 1 :
                        adventurerPosition.v1;
            } else {
                xAxe = adventurerPosition.v1 < limitX ? adventurerPosition.v1 + 1 :
                        adventurerPosition.v1;
            }

        } else {
            if (axeAdventurer.equals("Y")) {
                if (adventurerDirection.equals("O")) {
                    yAxe = adventurerPosition.v2 > 0 ? adventurerPosition.v2 - 1 :
                            adventurerPosition.v2;
                } else {
                    yAxe = adventurerPosition.v2 < limitY ? adventurerPosition.v2 + 1 :
                            adventurerPosition.v2;
                }
            }
        }
        return new AdventurerNextStepPosition(xAxe, yAxe);
    }

    protected record AdventurerNextStepPosition(int xAxe, int yAxe) {}
}
