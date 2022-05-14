package aiss.api.resources.comparators;

import java.util.Comparator;

import aiss.model.User;

public class ComparatorNameUserReversed implements Comparator<User> {
	   @Override
	   public int compare(User o1, User o2) {
	        return o2.getName().compareTo(o1.getName());
	   }
}
