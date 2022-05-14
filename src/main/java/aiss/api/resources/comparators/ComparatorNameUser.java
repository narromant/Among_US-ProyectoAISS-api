package aiss.api.resources.comparators;

import java.util.Comparator;

import aiss.model.User;

public class ComparatorNameUser implements Comparator<User>{
   @Override
   public int compare(User o1, User o2) {
        return o1.getName().compareTo(o2.getName());
   }
}
