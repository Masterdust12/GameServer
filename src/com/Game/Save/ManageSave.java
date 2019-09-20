package com.Game.Save;

import com.Game.Init.PlayerConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.text.DecimalFormat;
import java.util.Scanner;

// Save Order:
// Login: name pass
// Pos: x y
// Skills: s1 s2 s3...

public class ManageSave {
    public static DecimalFormat df = new DecimalFormat("0.00");

    public static PlayerConnection loadPlayerData(String playername, DatagramPacket packet) {
        File getFile = new File("src/saves/" + playername.toLowerCase() + ".psave");
        PlayerConnection data = new PlayerConnection(packet.getAddress(), packet.getPort());
        Scanner scanner;
        try {
            scanner = new Scanner(getFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + playername.toLowerCase() + ".psave");
            return null;
        }
        String[] parts;
        while (scanner.hasNext()) {
            parts = scanner.nextLine().split(" ");
            switch (parts[0]) {
                case "Login:":
                    data.username = parts[1];
                    data.password = parts[2];
                    break;
                case "Pos:":
                    data.x = Integer.parseInt(parts[1]);
                    data.y = Integer.parseInt(parts[2]);
                    break;
                case "Skills:":
                    for (int i = 1; i < parts.length; i++) {
                        data.skillXP[i - 1] = Float.parseFloat(parts[i]);
                    }
                    break;
                case "Inventory:":
                    for (int i = 1; i < parts.length; i++) {
                        if (i % 2 == 1)
                            data.inventoryItems[(i - 1) / 2].id = i;
                        else
                            data.inventoryItems[(i - 1) / 2].amount = i;
                    }
                    break;
                case "Accessory:":
                    for (int i = 1; i < parts.length; i++) {
                        if (i % 2 == 1)
                            data.accessoryItems[(i - 1) / 2].id = Integer.parseInt(parts[i]);
                        else
                            data.accessoryItems[(i - 1) / 2].amount = Integer.parseInt(parts[i]);
                    }
                    break;
            }
        }
        return data;
    }

    public static PlayerConnection savePlayerData(PlayerConnection data) {
        File getFile = new File("src/saves/" + data.username.toLowerCase() + ".psave");;
        PrintWriter writer;

        try {
            writer = new PrintWriter(getFile);
        } catch (FileNotFoundException e) {
            return null;
        }

        writer.println("Login: " + data.username + " " + data.password);
        writer.println("Pos: " + data.x + " " + data.y);

        String skillsLine = "Skills:";

        for (float i : data.skillXP)
            skillsLine += " " + df.format(i);

        String invLine = "Inventory:";
        for (ItemMemory mem : data.inventoryItems)
            invLine += " " + mem.amount + " " + mem.id;

        writer.println(invLine);

        String accLine = "Accessory:";
        for (ItemMemory mem : data.accessoryItems)
            accLine += " " + mem.amount + " " + mem.id;

        writer.println(accLine);

        writer.println(skillsLine);
        writer.close();

        return data;
    }

    public static boolean loginCorrect(String username, String password) {
        File file = new File("src/saves/" + username.toLowerCase() + ".psave");

        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("FILE NOT FOUND: " + username.toLowerCase() + ".psave");
            return false;
        }
        String[] loginLine = scanner.nextLine().split(" ");
        return loginLine[2].trim().equalsIgnoreCase(password);
    }

    public static String getUsername(String username) {
        File file = new File("src/saves/" + username.toLowerCase() + ".psave");

        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("FILE NOT FOUND: " + username.toLowerCase() + ".psave");
            return null;
        }
        String[] loginLine = scanner.nextLine().split(" ");
        return loginLine[1].trim();
    }

    public static boolean usernameExists(String username) {
        File getFile = new File("src/saves/" + username.toLowerCase() + ".psave");
        return getFile.exists();
    }
}
