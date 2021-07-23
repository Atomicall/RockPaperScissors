
package com.company;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        if (args.length < 3 || args.length % 2 == 0 || Arrays.stream(args).distinct().toArray().length != args.length) {
            System.out.println("Invalid or missing args: enter >=3 unique args (num of args must be odd)\n" +
                    "example: <rock paper scissors> or <1, 2, 3, 4, 5>");
            return;
        }
        boolean exitFlag = false;
        Scanner scanner = new Scanner(System.in);
        SecureRandom randomGenerator = new SecureRandom();
        int playerMove = 0;
        int computerMove = randomGenerator.nextInt(args.length);
        byte generatedKey[] = new byte[16];
        randomGenerator.nextBytes(generatedKey);
        System.out.println("DD: " + byteArrayToHex(generatedKey));
        SecretKeySpec signingKey = new SecretKeySpec(generatedKey, "SHA-256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] hmacResult = mac.doFinal(ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(computerMove).array());
        System.out.println("HMAC: " + byteArrayToHex(hmacResult));
        while (!exitFlag) {
            System.out.println("Available moves:");
            for (int i = 0; i < args.length; i++) {
                System.out.println(i + 1 + " " + " - " + args[i]);
            }
            System.out.println("0 -  Exit\n" + "Enter your move:");
            try {
                playerMove = scanner.nextInt();
            } catch (Exception e) {
                scanner.nextLine();
                continue;
            }
            if (0 == playerMove) return;
            else if (playerMove > 0 && playerMove <= args.length) {
                exitFlag = true;
            }
        }
        System.out.println("Your move: " + args[--playerMove]);
        System.out.println("Computer move: " + args[computerMove]);
        if (playerMove == computerMove) {
            System.out.println("It is a draw");
        } else {
            if (checkPlayerWin(computerMove, playerMove, args.length)) {
                System.out.println("Your win");
            } else {
                System.out.println("You lose");
            }
        }
        System.out.println("HMAC key: " + byteArrayToHex(generatedKey));
    }

    private static String byteArrayToHex(byte[] whatTo) {
        StringBuilder sb = new StringBuilder(whatTo.length * 2);
        for (byte b : whatTo)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static boolean checkPlayerWin(int computerMove, int playerMove, int argsLength) {
        int currArrPos = playerMove;
        for (int i = 0; i < (argsLength - 1) / 2; i++) {
            currArrPos++;
            if (currArrPos >= argsLength) currArrPos = 0;
            if (currArrPos == computerMove) return false;
        }
        return true;
    }
}
