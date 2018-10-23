//801021051 SURAJ M SHETTY - Task 2 (compute the logarithmic Term Frequency WF(t,d))
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;


public class TermFrequency extends Configured implements Tool {

    private static final Logger LOG = Logger.getLogger(TermFrequency.class);

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new TermFrequency(), args); //Initialize the main class
        System.exit(res);
    }


    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf(), " termfrequency ");  // First Job to be created in hadoop
        job.setJarByClass(this.getClass());
        FileInputFormat.addInputPaths(job, args[0]); //HDFS Input fake path
        FileOutputFormat.setOutputPath(job, new Path(args[1])); //HDFS Output fake path
        job.setMapperClass(Map.class); // Mapper
        job.setReducerClass(Reduce.class); // Reducer
        job.setOutputKeyClass(Text.class); //key value pair creation
        job.setOutputValueClass(FloatWritable.class);
        return job.waitForCompletion(true) ? 0 : 1; //wait for job to be completed with return statistics
    }

    public static class Map extends
            Mapper<LongWritable, Text, Text, FloatWritable> {

        private final static FloatWritable count = new FloatWritable(1);
        private Text word = new Text();
        private static final Pattern WORD_BOUNDARY = Pattern
                .compile("\\s*\\b\\s*");

        public void map(LongWritable offset, Text lineText, Context context)
                throws IOException, InterruptedException {


            String line = lineText.toString();
            Text currentWord = new Text();
            for (String word : WORD_BOUNDARY.split(line)) {
                if (word.isEmpty()) {
                    continue;
                }
                FileSplit fs = (FileSplit)context.getInputSplit(); //for file split with use of # delimiter
                String fname = fs.getPath().getName();
                String delim = new String("#####");
                String split = word.toString().trim().toLowerCase() + delim + fname + "\t";
                currentWord = new Text(split);
                context.write(currentWord, count);
            }
        }
    }


    public static class Reduce extends
            Reducer<Text, FloatWritable, Text, FloatWritable> { // Floatwritable because TF score comes in decimals

        @Override
        public void reduce(Text word, Iterable<FloatWritable> counts,
                           Context context) throws IOException, InterruptedException {
            int sum = 0; float tf = 0;
            for (FloatWritable count : counts)
            {
                sum += count.get();
            }

            if(sum == 0)
            {
                context.write(word, new FloatWritable(0));  //tf=0 ==> wf=0 So, assigning it
            }
            else
            {
                tf = (float) (1+(Math.log(sum)/Math.log(10))); //equation#1 of assignment manual usage
                context.write(word, new FloatWritable(tf));
            }
        }
    }
}