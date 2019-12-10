package com.jd.lc.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class SourceDataLimit {
    public static void main(String[] args) throws Exception {

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        List<String> list = new ArrayList<>();

        list.add("world");
        list.add("hello");
        list.add("hello");
        list.add("world");
        list.add("world");



        DataSource<String> text = env.fromCollection(list);

        AggregateOperator<Tuple2<String, Integer>> counts = text.flatMap(new WordCountByList.Tokenizer()).groupBy(0).sum(1);

        counts.filter((Tuple2 <String,Integer> x) -> x.f1 >= 2).print();

    }

    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}
