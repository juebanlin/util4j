package net.jueb.util4j.study.jdk8.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamTest{

	public List<String> list=new ArrayList<>();
	
	
	public Spliterator<String> spliterator() {
        return Spliterators.spliteratorUnknownSize(list.iterator(), 0);
    }
	
	public Stream<String> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

	public Stream<String> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
