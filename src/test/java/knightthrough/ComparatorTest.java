package knightthrough;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class ComparatorTest {

	@Test
	public void test() {
		List<Integer> list = new LinkedList<Integer>();
		list.add(1);
		list.add(3);
		list.add(2);
		list.add(5);
		list.add(4);
		Collections.sort(list, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Integer.compare(o1, o2);
			}
		});

		System.out.println(Arrays.toString(list.toArray()));
	}

}
