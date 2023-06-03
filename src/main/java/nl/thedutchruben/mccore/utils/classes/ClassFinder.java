package nl.thedutchruben.mccore.utils.classes;


import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClassFinder {

    public Set<Class<?>> getClasses(String path) {
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader cl = getClass().getClassLoader();
        Set<ClassPath.ClassInfo> classesInPackage = null;
        try {
            classesInPackage = ClassPath.from(cl).getTopLevelClassesRecursive(path);
            for (ClassPath.ClassInfo classInfo : classesInPackage) {
                if(!classInfo.getName().contains("extentions")){
                    classes.add(classInfo.load());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return classes;
    }

}
