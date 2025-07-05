package kg.sweezy.watchtime.utils;


import kg.sweezy.watchtime.utils.annotations.CheckEmptySpace;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class ReplaceEmptySpace {
    public boolean checkEmptySpace(Object object){
        if(object == null) return true;
        Field[] fields = object.getClass().getDeclaredFields();

        for(Field field : fields){
            if(field.isAnnotationPresent(CheckEmptySpace.class)){
                field.setAccessible(true);
                boolean isEmpty = field.toString().replace(" ", "").isEmpty();
                System.out.println(isEmpty);
                field.setAccessible(false);
                return isEmpty;
            }
        }
        return true;
    }
}
