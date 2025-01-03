package nl.thedutchruben.mccore.bungee.commands;

import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.utils.classes.ClassFinder;

public class BungeeCommandRegistery {
    public BungeeCommandRegistery(Mccore mccore) {
        for (Class<?> allClass : new ClassFinder().getClasses(mccore.getJavaPlugin().getClass().getPackage().toString().split(" ")[1])) {
            if(allClass.isAnnotationPresent(BungeeCommand.class)) {

            }
        }
    }
}